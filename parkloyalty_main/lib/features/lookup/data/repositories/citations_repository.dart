import 'dart:convert';

import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/constants/consts.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/features/lookup/data/models/request_model.dart';
import 'package:park_enfoecement/features/lookup/data/models/timing_record.dart';

import '../../../../app/core/constants/api_endpoints.dart';
import '../../../../app/core/models/drop_down_model.dart';
import '../../../../app/core/services/auth_service.dart';
import '../../../home/data/repository/home_storage_repository.dart';
import '../models/citation.dart';

class CitationsRepository extends GetxService {
  CitationsRepository(this.apiClient, this.storageService, this.authService);

  final ApiClient apiClient;
  final HomeStorageRepository storageService;
  final AuthService authService;

  Future<DropDownModel> fetchDropDownData(
    String type, {
    String shift = '',
  }) async {
    if (storageService.isDropDownDataAvailable(type)) {
      return DropDownModel.fromJson(
        jsonDecode(storageService.getDropDownData(type)),
      );
    }
    dynamic req = {"type": type, "shard": Consts.shard};
    if (shift.isNotEmpty) {
      req['site_id'] = shift;
    }
    final response = await apiClient.postRequest(ApiEndpoints.dataSet, req);
    storageService.saveDropDownData(type, jsonEncode(response.body));
    return DropDownModel.fromJson(response.body);
  }

  Future<Citation> fetchCitations(int page, RequestModel reqModel) async {
    var todayDate = DateTime.now();
    var req = {
      "issue_ts_from": DateUtil.getReqDate(todayDate),
      "issue_ts_to": DateTime(
        todayDate.year,
        todayDate.month,
        todayDate.day + 2,
      ).toUtc().toIso8601String(),
      "site_officer_id": authService.user?.siteOfficerId,
      "limit": '25',
      "page": page.toString(),
      "shift": authService.user?.officerShift,
    };
    if ((reqModel.street?.label1 ?? '').isNotEmpty) {
      req['street'] = reqModel.street?.label1;
    }
    if ((reqModel.ticketNo ?? '').isNotEmpty) {
      req['ticket_no'] = reqModel.ticketNo;
    }
    if ((reqModel.blocName ?? '').isNotEmpty) {
      req['block'] = reqModel.blocName;
    }
    if ((reqModel.licenseNo ?? '').isNotEmpty) {
      req['lp_number'] = reqModel.licenseNo;
    }
    if ((reqModel.side ?? '').isNotEmpty) {
      req['side'] = reqModel.side;
    }
    final response = await apiClient.getRequest(
      ApiEndpoints.getCitations,
      query: req,
    );
    return Citation.fromJson(response.body);
  }

  Future<TimingRecord> fetchTimingRecords(
    int page,
    RequestModel reqModel,
  ) async {
    var todayDate = DateTime.now();
    var req = {
      "issue_ts_from": DateUtil.getReqDate(todayDate),
      // "issue_ts_from": DateTime(2025,12).toUtc().toIso8601String(),
      "issue_ts_to": DateTime(
        todayDate.year,
        todayDate.month,
        todayDate.day + 2,
      ).toUtc().toIso8601String(),
      "site_officer_id": authService.user?.siteOfficerId,
      "limit": '25',
      "page": page.toString(),
      "arrival_status": 'Open',
    };
    if ((reqModel.street?.label1 ?? '').isNotEmpty) {
      req['street'] = reqModel.street?.label1;
    }
    if ((reqModel.ticketNo ?? '').isNotEmpty) {
      req['ticket_no'] = reqModel.ticketNo;
    }
    if ((reqModel.blocName ?? '').isNotEmpty) {
      req['block'] = reqModel.blocName;
    }
    if ((reqModel.licenseNo ?? '').isNotEmpty) {
      req['lp_number'] = reqModel.licenseNo;
    }
    if ((reqModel.selectedSide?.label1 ?? '').isNotEmpty) {
      req['side'] = reqModel.selectedSide!.label1;
    }
    if (reqModel.timing != null) {
      req['regulation_time'] = reqModel.timing!.label2.toString();
    }
    if ((reqModel.enforced ?? '').isNotEmpty) {
      req['enforced'] = reqModel.enforced;
    }
    final response = await apiClient.getRequest(
      ApiEndpoints.timingRecords,
      query: req,
    );
    return TimingRecord.fromJson(response.body);
  }

  Future<dynamic> mark(List<String> idList) async {
    final response = await apiClient.patchRequest(ApiEndpoints.mark, {
      "mark_ids": idList,
      "arrival_status": "GOA",
    });
    return response.body;
  }
}

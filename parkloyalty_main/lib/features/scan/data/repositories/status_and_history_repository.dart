import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/features/scan/data/models/lpr_model.dart';

import '../../../../app/core/constants/api_endpoints.dart';
import '../../../../app/shared/utils/date_utils.dart';
import '../../../lookup/data/models/timing_record.dart';

class StatusAndHistoryRepository extends GetxService {
  StatusAndHistoryRepository(this.apiClient);
  final ApiClient apiClient;

  Future<LprModel> fetchHistory(String lprNumber, int page) async {
    final response = await apiClient.postRequest(ApiEndpoints.getDataFromLrp, {
      "lp_number": lprNumber,
      "page": page,
      "type": "CitationData",
      "limit": "25",
    });
    return LprModel.fromJson(response.body);
  }

  Future<TimingRecord> fetchStatusList(String lpNumber) async {
    var todayDate = DateTime.now();
    var req = {
      "issue_ts_from": DateUtil.getReqDate(todayDate),
      // "issue_ts_from": DateTime(2025,12).toUtc().toIso8601String(),
      "issue_ts_to": DateTime(
        todayDate.year,
        todayDate.month,
        todayDate.day + 2,
      ).toUtc().toIso8601String(),
      "lp_number": lpNumber,
      "arrival_status": 'Open',
    };
    final response = await apiClient.getRequest(
      ApiEndpoints.timingRecords,
      query: req,
    );
    return TimingRecord.fromJson(response.body);
  }
}

import 'dart:convert';

import 'package:park_enfoecement/app/core/constants/consts.dart';
import 'package:park_enfoecement/app/core/constants/template_types.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart' hide Datum;
import 'package:park_enfoecement/app/core/models/template_model.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import 'package:park_enfoecement/features/home/data/models/citation_book.dart';
import 'package:park_enfoecement/features/home/data/models/welcome_model.dart' hide Datum, Response;

import '../../../../app/core/api/api_client.dart';
import '../../../../app/core/constants/api_endpoints.dart';
import '../models/update_officer_quest_model.dart';
import 'home_storage_repository.dart';

class HomeRepository {
  final ApiClient apiClient;

  HomeRepository(this.apiClient);

  Future<TemplateModel> fetchWelcomeForm(String type, HomeStorageRepository storageService) async {
    final response = await apiClient.getRequest('${ApiEndpoints.template}?template_type=$type');

    TemplateModel model;
    if (type == TemplateTypes.citation || type == TemplateTypes.municipalCitation) {
      model = TemplateModel(
        data: [TemplateData(response: response.body['data'].map<TemplateRes>((e) => TemplateRes.fromJson(e)).toList())],
      );
    } else {
      model = TemplateModel.fromJson(response.body);
    }
    storageService.saveTemplate(jsonEncode(model.toJson()), type);
    return model;
  }

  Future<DropDownModel> fetchDropDownData(String type, HomeStorageRepository storageService) async {
    try {
      if (storageService.isDropDownDataAvailable(type)) {
        return DropDownModel.fromJson(jsonDecode(storageService.getDropDownData(type)));
      }
      final response = await apiClient.postRequest(ApiEndpoints.dataSet, {"type": type, "shard": Consts.shard});
      if (response.statusCode == 200) {
        storageService.saveDropDownData(type, jsonEncode(response.body));
      }

      return DropDownModel.fromJson(response.body);
    } catch (e, s) {
      logging("Error: $e\n$s");
      return DropDownModel(data: [], status: false, message: 'Internal Server Error');
    }
  }

  Future<WelcomeModel> welcome() async {
    final response = await apiClient.getRequest(ApiEndpoints.welcome);

    return WelcomeModel.fromJson(response.body);
  }

  Future<dynamic> updateOfficer(UpdateOfficerRequestModel req) async {
    final response = await apiClient.postRequest(ApiEndpoints.updateOfficer, req.toJson());

    return response.body;
  }

  Future<CitationBook> issueCitationBook(String deviceId, HomeStorageRepository storageService) async {
    final response = await apiClient.postRequest(ApiEndpoints.issueCitationBook, {'device_id': deviceId});
    var model=CitationBook.fromJson(response.body);
    var idList = model.data[0].res.citationBooklet;
    idList.forEach((element) => storageService.saveCitationIds(element));
    return model;
  }
}

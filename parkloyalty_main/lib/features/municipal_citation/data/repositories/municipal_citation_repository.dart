import 'dart:convert';

import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/models/template_model.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';

import '../../../../app/core/constants/api_endpoints.dart';

class MunicipalCitationRepository extends GetxService {
  final ApiClient apiClient;
  final HomeStorageRepository storageRepository;

  MunicipalCitationRepository({required this.apiClient, required this.storageRepository});

  Future<TemplateModel> getMunicipalCitationsTemplate(String type) async {
    if (storageRepository.getSavedTemplate(type).data.isNotEmpty) {
      return storageRepository.getSavedTemplate(type);
    }
    final response = await apiClient.getRequest('${ApiEndpoints.template}?template_type=$type');
    if (response.statusCode == 200) {
      storageRepository.saveTemplate(jsonEncode(response.body), type);

      return TemplateModel(
        data: [TemplateData(response: response.body['data'].map<TemplateRes>((e) => TemplateRes.fromJson(e)).toList())],
      );
    } else {
      return TemplateModel(data: []);
    }
  }

  TemplateModel filterComponents(TemplateModel model) {
    return storageRepository.filterComponents(model);
  }

  String getCitationId() {
    return storageRepository.getCitationId();
  }

  String getDropdownFieldDataset(String type) {
    final rawJson = storageRepository.getDropDownData(type);
    logging("getDropdownFieldDataset :: ${type} ===> $rawJson");
    return storageRepository.getDropDownData(type);
  }

  Future<Response> getIssuedCitation({required Map<String,String> query}) async {
    final response = await apiClient.getRequest(ApiEndpoints.getCitations,query: query);
    return response;
  }
}

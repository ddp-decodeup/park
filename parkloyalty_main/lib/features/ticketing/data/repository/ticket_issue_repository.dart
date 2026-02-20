import 'dart:convert';
import 'dart:developer';
import 'dart:io';

import 'package:get/get_connect/http/src/multipart/multipart_file.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/data/models/bulk_upload_response.dart';
import 'package:park_enfoecement/features/ticketing/data/models/citation_similarity_request.dart';

import '../../../../app/core/api/api_client.dart';
import '../../../../app/core/constants/api_endpoints.dart';
import '../../../../app/core/models/template_model.dart';
import '../models/ticket_creation_request.dart';

class TicketIssueRepository {
  final ApiClient apiClient;
  HomeStorageRepository storageService;

  TicketIssueRepository(this.apiClient, this.storageService);

  Future<TemplateModel> fetchForm(String type) async {
    if (storageService
        .getSavedTemplate(type)
        .data
        .isNotEmpty) {
      return storageService.getSavedTemplate(type);
    }
    final response = await apiClient.getRequest('${ApiEndpoints.template}?template_type=$type');
    storageService.saveTemplate(jsonEncode(response.body), type);

    return TemplateModel(
      data: [TemplateData(response: response.body['data'].map<TemplateRes>((e) => TemplateRes.fromJson(e)).toList())],
    );
  }

  Future<dynamic> checkSimilarity(CitationSimilarityRequest req) async {
    final response = await apiClient.postRequest('${ApiEndpoints.citationSimilarityCheck}', req.toJson());

    return response.body;
  }

  Future<BulkUploadResponse> uploadFiles(List<File> files) async {
    final List<MultipartFile> multipartFiles = files.map((file) {
      return MultipartFile(file.readAsBytesSync(), filename: file.path
          .split('/')
          .last);
    }).toList();
    final Map<String, dynamic> formMap = {};
    formMap['upload_type'] = 'CitationImages';
    formMap['type'] = 'citation';
    formMap['data'] = files.map((e) =>
    e.path
        .split('/')
        .last).toList().toString();
    formMap['files'] = multipartFiles;

    final response = await apiClient.uploadImages('${ApiEndpoints.bulkUpload}', formMap);

    return BulkUploadResponse.fromJson(response.body);
  }

  Future<dynamic> createTicket(TicketCreationRequest req) async {
    final response = await apiClient.postRequest(
        ApiEndpoints.getCitations,
        req.toJson()
    );
    return response.body;
  }

  String getDropdownFieldDataset(String type) {
    final rawJson = storageService.getDropDownData(type);
    log("getDropdownFieldDataset :: ${type} ===> $rawJson")
    ;
    return storageService.getDropDownData(type);
  }
}
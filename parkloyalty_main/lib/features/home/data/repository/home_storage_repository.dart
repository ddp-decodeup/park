import 'dart:convert';

import 'package:get/get.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';

import '../../../../app/core/constants/template_types.dart';
import '../../../../app/core/models/offline_request_model.dart';
import '../../../../app/core/models/template_model.dart';
import '../../../../app/core/services/local_storage_service.dart';

class HomeStorageRepository extends GetxService {
  HomeStorageRepository(this.storageService);

  LocalStorageService storageService;

  Future<void> saveDropDownData(String key, dynamic value) async {
    await LocalStorageService.dropDownBox.put(key, value);
  }

  dynamic getDropDownData(String key) {
    return LocalStorageService.dropDownBox.get(key);
  }

  bool isDropDownDataAvailable(String key) {
    return LocalStorageService.dropDownBox.get(key) != null;
  }

  Future<void> saveOfflineRequest(OfflineRequest request, String key) async {
    await LocalStorageService.offlineRequestsBox.put(key, request);

  }

  Future<void> saveTemplate(String request, String key) async {
    await LocalStorageService.templateBox.put(key, request);
  }

  TemplateModel getSavedTemplate(String key) {
    try {
      dynamic res = jsonDecode(LocalStorageService.templateBox.get(key)!);

      if (key == TemplateTypes.citation) {
        if (res == null) {
          return TemplateModel(data: []);
        }
        // final model = TemplateModel(
        //   data: List<TemplateData>.from(
        //     res["data"].map((x) => TemplateData.fromJson(x)),
        //   ).where((element) => element.response.isNotEmpty).toList(),
        // );

        return TemplateModel(
          data: List<TemplateData>.from(
            res["data"].map((x) => TemplateData.fromJson(x)),
          ).where((element) => element.response.isNotEmpty).toList(),
        );
      }
      return TemplateModel.fromJson(res);
    } on Error catch (e) {
      logging('$e ${e.stackTrace}');
    }
    return TemplateModel(data: []);
  }

  List<String> getAllCollections() {
    var templateList = [
      getSavedTemplate(TemplateTypes.activity),
      getSavedTemplate(TemplateTypes.citation),
      getSavedTemplate(TemplateTypes.timing),
    ];
    List<String> collections = [];
    templateList.forEach((element) {
      element.data[0].response.forEach((element) {
        element.fields.forEach((element) {
          if (element.collectionName!.isNotEmpty && !collections.contains(element.collectionName)) {
            collections.add(element.collectionName!);
          }
        });
      });
    });
    return collections;
  }

  List<OfflineRequest> getOfflineRequests() {
    return LocalStorageService.offlineRequestsBox.values.toList();
  }

  Future<void> saveCitationIds(String id) async {
    await LocalStorageService.citationBookBox.put(id, id);

  }

  String getCitationId() {
    return LocalStorageService.citationBookBox.values.toList()[0];
  }

  bool isCitationIdAvailable() {
    return LocalStorageService.citationBookBox.keys.isNotEmpty;
  }

  Future<void> deleteCitationId(String id) async {
    await LocalStorageService.citationBookBox.delete(id);

  }

  Future<void> deleteOfflineRequest(String id) async {
    await LocalStorageService.offlineRequestsBox.delete(id);
  }

  TemplateModel filterComponents(TemplateModel components) {
    components.data.removeWhere((element) {
      element.response.removeWhere((responseElement) {
        responseElement.fields.removeWhere((field) => field.tag.isEmpty);
        return responseElement.fields.isEmpty;
      });
      return false;
    });
    return components;
  }
}

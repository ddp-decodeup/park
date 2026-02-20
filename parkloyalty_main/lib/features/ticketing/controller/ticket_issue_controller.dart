import 'dart:convert';
import 'dart:io';

import 'package:collection/collection.dart';
import 'package:flutter/cupertino.dart';
import 'package:image_picker/image_picker.dart';
import 'package:park_enfoecement/app/core/constants/field_types.dart';
import 'package:park_enfoecement/app/core/constants/template_types.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/models/template_model.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/app/shared/utils/extensions/drop_down_list.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/ticket_issue_repository.dart';

import '../../../app/core/controllers/app_controller.dart';
import '../../../app/core/models/citation_similarity_request_model.dart';
import '../../../app/core/models/dataset_model.dart';
import '../../../app/core/models/drop_down_model.dart';
import '../../../app/core/services/location_service.dart';

class TicketIssueController extends BaseController {
  TicketIssueController(
    this.repository,
    this.storageService,
    this.appController,
    this.locationService,
    this.authService,
    this.loaderController,
  );

  final TicketIssueRepository repository;
  final HomeStorageRepository storageService;
  final AppController appController;
  late CitationSimilarityRequestModel requestModel;
  final LocationService locationService;
  final AuthService authService;
  final LoaderController loaderController;

  List<TemplateRes> template = [];
  TemplateModel? model;
  String citationNumber = "";
  DateTime ticketDate = .now();
  String agency = "";
  String officerId = "";
  final ImagePicker _picker = ImagePicker();
  List<File> imageFileList = [];

  @override
  void onReady() {
    fetchForm();
    super.onReady();
  }

  String getCitationId() {
    return storageService.getCitationId();
  }

  Future<void> fetchForm() async {
    loaderController.showLoader();

    model = await repository.fetchForm(TemplateTypes.citation);
    cleanCitationTemplate(model!);
    template = model?.data.isNotEmpty == true ? model?.data[0].response ?? [] : [];
    ticketDate = DateTime.now();
    citationNumber = getCitationId();
    officerId = authService.user?.officerBadgeId.toString() ?? "";
    agency = authService.user?.officerAgency ?? "";
    template.removeWhere((component) => component.component == "Officer" || component.component == "Header");
    for (final component in template) {
      component.fields.sort(
        (a, b) => _parseFormLayoutOrder(a.formLayoutOrder).compareTo(_parseFormLayoutOrder(b.formLayoutOrder)),
      );
    }

    final priorityMap = {"Citation Type": 0, "Vehicle": 1, "Location": 2, "Violation": 3, "Comments": 4};

    template.sort((a, b) {
      int getPriority(String? component) {
        if (component == null) return priorityMap.length;
        for (var entry in priorityMap.entries) {
          if (component.contains(entry.key)) return entry.value;
        }
        return priorityMap.length;
      }

      return getPriority(a.component).compareTo(getPriority(b.component));
      // if (priorityCompare != 0) return priorityCompare;
      // return _componentOrder(a).compareTo(_componentOrder(b));
    });
    update();
    await Future.delayed(Duration(seconds: 1));
    initializeTemplateFields();

    loaderController.hideLoader();
  }

  void initializeTemplateFields() {
    template.forEach((component) {
      component.fields.forEach((field) {
        if (field.tag != FieldTypes.dropdown) {
          field.enteredData = TextEditingController();
        } else if (field.tag == FieldTypes.dropdown && field.collectionName!.isNotEmpty) {
          final dataset = dataSetModelFromJson(repository.getDropdownFieldDataset(field.collectionName ?? ""));
          final dropdownList = dataset.data?[0].response?.map((e) {
            var fieldName = field.fieldName;
            if (fieldName == 'make') {
              fieldName = 'make_full';
            }
            if (field.name == 'violation_abbr') {
              return DataSet(
                id: e.id.toString(),
                label1: e.toJson()[fieldName],
                label2: e.toJson()['violation_description'],
                label3: e.toJson()['amount'],
              );
            }
            if (field.name == 'lot') {
              return DataSet(id: e.id.toString(), label1: e.toJson()[fieldName], label2: e.toJson()['street']);
            }
            return DataSet(id: e.id.toString(), label1: e.toJson()[fieldName]);
          }).toList();
          field.dropDownOptionList = dropdownList;
        }
        modifyFieldTag(field);
        if (field.name == 'make') {
          modifyDropDownList(field);
        }
        // await Future.delayed(Duration(milliseconds: 100));
      });
      // await Future.delayed(Duration(milliseconds: 100));
    });
    update();
  }

  List<DataSet> getDropDownOptions(Field field) {
    var parentList = appController.allDropdownList[field.collectionName!]?.data.first;
    if (field.name == 'make') {
      return uniqueByMakeFull(parentList?.response ?? []);
    }
    return parentList?.response ?? [];
  }

  List<DataSet> uniqueByMakeFull(List<DataSet> list) {
    final seen = <String>{};
    return list.where((item) {
      final key = item.label1 ?? '';
      if (seen.contains(key)) {
        return false; // duplicate -> remove
      } else {
        seen.add(key);
        return true; // first time -> keep
      }
    }).toList();
  }

  void cleanCitationTemplate(TemplateModel model) {
    for (final datum in model.data) {
      for (final response in datum.response) {
        response.fields.removeWhere((field) => field.tag.trim().isEmpty);
      }
      datum.response.removeWhere((response) => response.fields.isEmpty);
    }
  }

  void autoFillField(
    DataSet dataset,
    List<Field> fieldList,
    String? component,
    List<DataSet> dropDownList,
    String name,
  ) {
    if (component == 'Violation' && name == 'violation_abbr') {
      var field = fieldList.singleWhere((element) => element.repr == 'Code');
      var descriptionField = fieldList.singleWhere((element) => element.name == 'description');
      var fineField = fieldList.singleWhere((element) => element.name == 'fine');
      field.selectedDropDownOption = field.dropDownOptionList!.singleWhereOrNull((element) => element.id == dataset.id);
      descriptionField.enteredData = TextEditingController(text: dataset.label2);
      fineField.enteredData = TextEditingController(text: dataset.label3);
      update();
    } else if (component == 'Location' && name == 'lot') {
      var field = fieldList.singleWhere((element) => element.repr == 'Street');
      field.selectedDropDownOption = field.dropDownOptionList!.singleWhereOrNull((element) {
        return element.label1 == dataset.label2;
      });
      update();
    } else if (component == 'Vehicle' && name == 'make') {
      var makeField = fieldList.singleWhere((element) => element.repr == 'Make');
      var modelField = fieldList.singleWhere((element) => element.repr == 'Model');

      if (makeField.selectedDropDownOption != null) {
        final data = dataSetModelFromJson(repository.getDropdownFieldDataset(modelField.collectionName ?? ""));
        final dropdownList = data.data?[0].response?.map((e) {
          return DataSet(
            id: e.id.toString(),
            label1: e.toJson()[modelField.fieldName],
            label2: e.toJson()['make_full'],
          );
        }).toList();
        modelField.dropDownOptionList = dropdownList!.where((element) {
          return element.label2 == dataset.label1;
        }).toList();
        modelField.isEditable = true;
      }
      update();
    }
  }

  void modifyFieldTag(Field field) {
    if (field.name == 'model') {
      field.isEditable = false;
    } else if (field.tag == FieldTypes.dropdown && field.dropDownOptionList == null ||
        field.tag == FieldTypes.dropdown && field.dropDownOptionList!.isEmpty) {
      field.tag = FieldTypes.editView;
      field.enteredData = TextEditingController();
    }
  }

  Future<void> openCamera() async {
    final XFile? photo = await _picker.pickImage(source: ImageSource.camera);

    if (photo != null) {
      imageFileList.add(File(photo.path));
      update();
    }
  }

  void removeImageFile(File file) {
    imageFileList.remove(file);
    update();
  }

  // int _componentOrder(TemplateRes component) {
  //   if (component.fields.isEmpty) return 9999;
  //   final values = component.fields
  //       .map((field) => _parseFormLayoutOrder(field.formLayoutOrder))
  //       .where((value) => value >= 0)
  //       .toList();
  //   if (values.isEmpty) return 9999;
  //   values.sort();
  //   return values.first;
  // }

  int _parseFormLayoutOrder(String? value) {
    if (value == null) return 9999;
    final cleaned = value.trim();
    if (cleaned.isEmpty) return 9999;
    final numeric = RegExp(r'\d+').firstMatch(cleaned)?.group(0);
    return int.tryParse(numeric ?? '') ?? 9999;
  }

  void modifyDropDownList(Field field) {
    field.dropDownOptionList = field.dropDownOptionList.uniqueByLabel1();
  }
}

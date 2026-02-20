import 'dart:io';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:park_enfoecement/app/core/constants/template_types.dart';
import 'package:park_enfoecement/app/core/controllers/app_controller.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/models/dataset_model.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/app/core/models/template_model.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/core/services/location_service.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_dialog.dart';
import 'package:park_enfoecement/features/municipal_citation/data/models/municipal_citation_creation_model.dart';
import 'package:park_enfoecement/features/municipal_citation/data/repositories/municipal_citation_repository.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';

import '../../../app/core/constants/field_types.dart';

class MunicipalCitationController extends BaseController {
  final AuthService authService;
  final LoaderController loaderController;
  final AppController appController;
  final MunicipalCitationRepository repository;

  MunicipalCitationController({
    required this.authService,
    required this.loaderController,
    required this.appController,
    required this.repository,
  });

  List<TemplateRes> template = [];
  String citationNumber = "";
  DateTime ticketDate = .now();
  String agency = "";
  String officerId = "";
  String officerName = "";
  String beat = "--";
  GlobalKey<FormState> formKey = GlobalKey<FormState>();
  final String ticketAndOfficerDetailId = "TicketAndOfficerDetailId";
  final String formId = "formId";
  final String imageId = "imageId";
  List<Field> personalInformationField = [];
  List<Field> locationDetailsField = [];
  List<Field> violationDetailsField = [];
  List<Field> commentsField = [];
  List<Field> citationTypeField = [];
  final ImagePicker _picker = ImagePicker();
  List<File> imageFileList = [];
  bool buttonDisabled = true;
  TextEditingController firstNameController = TextEditingController();
  TextEditingController lastNameController = TextEditingController();
  TextEditingController dobController = TextEditingController();
  bool officerHasCheckedTicket = false;
  MunicipalCitationCreationModel model = MunicipalCitationCreationModel();

  @override
  void onInit() {
    getTemplate();
    super.onInit();
  }

  @override
  void dispose() {
    removeListeners();
    super.dispose();
  }

  Future<void> getTemplate() async {
    loaderController.showLoader();
    try {
      final response = await repository.getMunicipalCitationsTemplate(TemplateTypes.municipalCitation);
      removeEmptyFieldsTemplate(response);
      ticketDate = DateTime.now();
      citationNumber = repository.getCitationId();
      officerId = authService.user?.officerBadgeId.toString() ?? "";
      agency = authService.user?.officerAgency ?? "";
      beat = authService.user?.officerBeat ?? "";
      officerName = (authService.user?.officerFirstName ?? "") + " " + (authService.user?.officerLastName ?? "");
      template = response.data.isNotEmpty ? response.data[0].response : [];
      template.removeWhere((component) => component.component == "Officer" || component.component == "Header");
      await Future.delayed(Duration(seconds: 1), () => initializeForms());

      update([ticketAndOfficerDetailId, formId]);
      addListeners();
    } catch (e, s) {
      logging("error: $e\n$s");
    } finally {
      loaderController.hideLoader();
    }
  }

  void initializeForms() {
    for (final component in template) {
      if (component.component?.toLowerCase().contains("motorist") == true) {
        personalInformationField.addAll(component.fields);
      } else if (component.component?.toLowerCase().contains("location") == true) {
        locationDetailsField.addAll(component.fields);
      } else if (component.component?.toLowerCase().contains("violation") == true) {
        violationDetailsField.addAll(component.fields);
      } else if (component.component?.toLowerCase().contains("comments") == true) {
        commentsField.addAll(component.fields);
      } else if (component.component?.toLowerCase().contains("citation") == true) {
        citationTypeField.addAll(component.fields);
      }
    }
    personalInformationField = initializeTemplateFields(personalInformationField);
    locationDetailsField = initializeTemplateFields(locationDetailsField);
    violationDetailsField = initializeTemplateFields(violationDetailsField);
    commentsField = initializeTemplateFields(commentsField);
    citationTypeField = initializeTemplateFields(citationTypeField);
  }

  void removeEmptyFieldsTemplate(TemplateModel model) {
    for (final datum in model.data) {
      for (final response in datum.response) {
        response.fields.removeWhere((field) => field.tag.trim().isEmpty);
      }
      datum.response.removeWhere((response) => response.fields.isEmpty);
    }
  }

  void addListeners() {
    firstNameController = personalInformationField
        .firstWhere((element) => element.name.toLowerCase() == "first_name")
        .enteredData!;
    lastNameController = personalInformationField
        .firstWhere((element) => element.name.toLowerCase() == "last_name")
        .enteredData!;
    dobController = personalInformationField
        .firstWhere((element) => element.name.toLowerCase() == "date_of_birth")
        .enteredData!;
    firstNameController.addListener(() {
      officerHasCheckedTicket = false;
      if (firstNameController.text.isNotEmpty &&
          lastNameController.text.isNotEmpty == true &&
          dobController.text.isNotEmpty == true) {
        buttonDisabled = false;
        update([formId]);
      } else {
        buttonDisabled = true;
        update([formId]);
      }
    });
    lastNameController.addListener(() {
      officerHasCheckedTicket = false;
      if (firstNameController.text.isNotEmpty == true &&
          lastNameController.text.isNotEmpty &&
          dobController.text.isNotEmpty == true) {
        buttonDisabled = false;
        update([formId]);
      } else {
        buttonDisabled = true;
        update([formId]);
      }
    });
    dobController.addListener(() {
      officerHasCheckedTicket = false;
      if (firstNameController.text.isNotEmpty == true &&
          lastNameController.text.isNotEmpty == true &&
          dobController.text.isNotEmpty == true) {
        buttonDisabled = false;
        update([formId]);
      } else {
        buttonDisabled = true;
        update([formId]);
      }
    });
  }

  void removeListeners() {
    firstNameController = personalInformationField
        .firstWhere((element) => element.name.toLowerCase() == "first_name")
        .enteredData!;
    lastNameController = personalInformationField
        .firstWhere((element) => element.name.toLowerCase() == "last_name")
        .enteredData!;
    dobController = personalInformationField
        .firstWhere((element) => element.name.toLowerCase() == "date_of_birth")
        .enteredData!;
    firstNameController.removeListener(() {
      officerHasCheckedTicket = false;
      if (firstNameController.text.isNotEmpty &&
          lastNameController.text.isNotEmpty == true &&
          dobController.text.isNotEmpty == true) {
        buttonDisabled = false;
        update([formId]);
      } else {
        buttonDisabled = true;
        update([formId]);
      }
    });
    lastNameController.removeListener(() {
      officerHasCheckedTicket = false;
      if (firstNameController.text.isNotEmpty == true &&
          lastNameController.text.isNotEmpty &&
          dobController.text.isNotEmpty == true) {
        buttonDisabled = false;
        update([formId]);
      } else {
        buttonDisabled = true;
        update([formId]);
      }
    });
    dobController.removeListener(() {
      officerHasCheckedTicket = false;
      if (firstNameController.text.isNotEmpty == true &&
          lastNameController.text.isNotEmpty == true &&
          dobController.text.isNotEmpty == true) {
        buttonDisabled = false;
        update([formId]);
      } else {
        buttonDisabled = true;
        update([formId]);
      }
    });
  }

  List<Field> initializeTemplateFields(List<Field> fields) {
    fields.forEach((field) {
      if (field.tag != FieldTypes.dropdown) {
        field.enteredData = TextEditingController();
      } else if (field.tag == FieldTypes.dropdown && field.collectionName!.isNotEmpty) {
        final dataset = dataSetModelFromJson(repository.getDropdownFieldDataset(field.collectionName ?? ""));
        final dropdownList = dataset.data?[0].response?.map((e) {
          return DataSet(id: e.id.toString(), label1: e.toJson()[field.fieldName]);
        }).toList();
        field.dropDownOptionList = dropdownList;
      }
      modifyFieldTag(field);
    });
    return fields;
  }

  void modifyFieldTag(Field field) {
    if (field.tag == FieldTypes.dropdown && field.dropDownOptionList == null ||
        field.tag == FieldTypes.dropdown && field.dropDownOptionList!.isEmpty ||
        field.tag == FieldTypes.datePicker) {
      field.tag = FieldTypes.editView;
      field.enteredData = TextEditingController();
    }
    if (field.name == 'date_of_birth') {
      field.tag = FieldTypes.datePicker;
      field.enteredData = TextEditingController();
    }
    if (field.name == "code") {
      field.tag = FieldTypes.textview;
      field.enteredData = TextEditingController();
    }
  }

  Future<void> openCamera() async {
    try {
      final XFile? photo = await _picker.pickImage(source: ImageSource.camera);

      if (photo == null) return;

      final Directory appDir = await getApplicationDocumentsDirectory();

      final String fileName = 'img_${DateTime.now().millisecondsSinceEpoch}${p.extension(photo.path)}';

      final String newPath = p.join(appDir.path, fileName);

      final File savedFile = await File(photo.path).copy(newPath);

      imageFileList.add(savedFile);

      update([imageId]);
    } catch (e) {
      debugPrint('openCamera error: $e');
    }
  }

  Future<void> removeImageFile(File file) async {
    try {
      if (await file.exists()) {
        await file.delete();
      }

      imageFileList.remove(file);

      update([imageId]);
    } catch (e) {
      debugPrint('removeImageFile error: $e');
    }
  }

  void onViolationDropdownChange() {
    final selectedDropdown = violationDetailsField.firstWhere((element) => element.selectedDropDownOption != null);

    if (!selectedDropdown.name.contains("violation")) {
      return;
    }
    final selectedViolationDetails = dataSetModelFromJson(
      repository.getDropdownFieldDataset(selectedDropdown.collectionName ?? ""),
    ).data?.first.response?.firstWhere((dataset) => dataset.id == selectedDropdown.selectedDropDownOption?.id).toJson();

    violationDetailsField.forEach((field) {
      if (field.enteredData != null && field.tag != FieldTypes.dropdown) {
        field.enteredData!.text = selectedViolationDetails?[field.fieldName];
      }
    });

    update([formId]);
  }

  Future<void> onPreview() async {
    if (!formKey.currentState!.validate()) {
      return;
    }
    if (!officerHasCheckedTicket) {
      Get.dialog(
        CustomDialog(
          title: LocalKeys.ticketCheckTitle.tr,
          titleTextStyle: textStyles.titleLarge,
          showCloseButton: false,
          mainAxisAlignment: .center,

          content: Text(
            LocalKeys.ticketCheckMessage.tr,
            style: textStyles.bodyLarge?.copyWith(fontWeight: .w500),
            textAlign: .justify,
          ),
          elevatedButtonText: LocalKeys.ok.tr,
          onTapElevatedButton: () {
            Get.back();
          },
        ),
      );
      return;
    }
    try {
      loaderController.showLoader();
      await createCitationData();
      Get.toNamed(Routes.municipalCitationPreview);
    } finally {
      loaderController.hideLoader();
    }
  }

  Future<void> checkIssuedCitation() async {
    loaderController.showLoader();
    try {
      await run(() async {
        final query = {
          "motorist_first_name": firstNameController.text,
          "motorist_last_name": lastNameController.text,
          "motorist_date_of_birth": dobController.text,
          "limit": "25",
          "page": "1",
        };
        final res = await repository.getIssuedCitation(query: query);

        if (res.statusCode == 200) {
          final data = res.body['data'] as List?;
          if (data == null || data.isEmpty) {
            Get.dialog(
              CustomDialog(
                title: LocalKeys.noOffenceFound.tr,
                titleTextStyle: textStyles.titleLarge,
                showCloseButton: false,
                mainAxisAlignment: .center,
                showDivider: true,
                onTapOutlineButton: () {
                  Get.back();
                },
                outlineButtonText: LocalKeys.cancel.tr,
                content: Text(
                  LocalKeys.noOffenceFoundMessage.tr,
                  style: textStyles.bodyLarge?.copyWith(fontWeight: .w500),
                  textAlign: .center,
                ),
                elevatedButtonText: LocalKeys.ok.tr,
                onTapElevatedButton: () {
                  Get.back();
                },
              ),
            );
          } else {
            Get.dialog(
              CustomDialog(
                title: LocalKeys.offenceFound.tr,
                titleTextStyle: textStyles.titleLarge,
                showCloseButton: false,
                mainAxisAlignment: .center,

                content: Text(
                  LocalKeys.offenceFoundMessage.tr.replaceAll("@offenceCount", data.length.toString()),
                  style: textStyles.bodyLarge?.copyWith(fontWeight: .w500),
                  textAlign: .center,
                ),
                elevatedButtonText: LocalKeys.ok.tr,
                onTapElevatedButton: () {
                  Get.back();
                },
              ),
            );
          }
        }
      });
    } finally {
      officerHasCheckedTicket = true;
      loaderController.hideLoader();
    }
  }

  ViolationDetails getViolationDetails() {
    final Map<String, Field> fieldMap = {for (final field in violationDetailsField) field.name.toLowerCase(): field};
    Field? findField(String key) {
      return fieldMap.entries.firstWhere((entry) => entry.key.contains(key)).value;
    }

    String readValue(Field? field) {
      if (field == null) return '';
      if (field.tag == FieldTypes.dropdown) {
        return field.selectedDropDownOption?.label1?.trim() ?? '';
      }
      return field.enteredData?.text.trim() ?? '';
    }

    final violationField = findField('violation');
    final codeField = findField('code');
    final descriptionField = findField('description');
    final fineField = findField('fine');
    final lateFineField = findField('late_fine');
    final due15DaysField = findField('due_15_days');
    final due30DaysField = findField('due_30_days');

    return ViolationDetails(
      violation: readValue(violationField),
      code: readValue(codeField),
      description: readValue(descriptionField),
      fine: readValue(fineField),
      lateFine: readValue(lateFineField),
      due15Days: readValue(due15DaysField),
      due30Days: readValue(due30DaysField),
    );
  }

  MotoristDetails getMotoristDetails() {
    final Map<String, Field> fieldMap = {for (final field in personalInformationField) field.name.toLowerCase(): field};
    Field? findField(String key) {
      return fieldMap.entries.firstWhere((entry) => entry.key.contains(key)).value;
    }

    String readValue(Field? field) {
      if (field == null) return '';
      if (field.tag == FieldTypes.dropdown) {
        return field.selectedDropDownOption?.label1?.trim() ?? '';
      }
      return field.enteredData?.text.trim() ?? '';
    }

    final firstNameField = findField('first_name');
    final middleNameField = findField('middle_name');
    final lastNameField = findField('last_name');
    final dobField = findField('date_of_birth');
    final dlNumberField = findField('dl_number');
    final blockField = findField("block");
    final streetField = findField("street");
    final cityField = findField("city");
    final stateField = findField("state");
    final zipField = findField("zip");

    return MotoristDetails(
      motoristFirstName: readValue(firstNameField),
      motoristMiddleName: readValue(middleNameField),
      motoristLastName: readValue(lastNameField),
      motoristDateOfBirth: readValue(dobField),
      motoristDlNumber: readValue(dlNumberField),
      motoristAddressBlock: readValue(blockField),
      motoristAddressStreet: readValue(streetField),
      motoristAddressCity: readValue(cityField),
      motoristAddressState: readValue(stateField),
      motoristAddressZip: readValue(zipField),
    );
  }

  LocationDetails getLocationDetails() {
    final Map<String, Field> fieldMap = {for (final field in locationDetailsField) field.name.toLowerCase(): field};
    Field? findField(String key) {
      return fieldMap.entries.firstWhere((entry) => entry.key.contains(key)).value;
    }

    String readValue(Field? field) {
      if (field == null) return '';
      if (field.tag == FieldTypes.dropdown) {
        return field.selectedDropDownOption?.label1?.trim() ?? '';
      }
      return field.enteredData?.text.trim() ?? '';
    }

    final streetField = findField("street");
    final blockField = findField("block");
    final zoneField = findField("zone");
    final lotField = findField("lot");

    return LocationDetails(
      block: readValue(blockField),
      street: readValue(streetField),
      lot: readValue(lotField),
      spaceId: readValue(zoneField),
    );
  }

  CommentDetails getCommentDetails() {
    final Map<String, Field> fieldMap = {for (final field in commentsField) field.name.toLowerCase(): field};
    Field? findField(String key) {
      return fieldMap.entries.firstWhere((entry) => entry.key.contains(key)).value;
    }

    String readValue(Field? field) {
      if (field == null) return '';
      if (field.tag == FieldTypes.dropdown) {
        return field.selectedDropDownOption?.label1?.trim() ?? '';
      }
      return field.enteredData?.text.trim() ?? '';
    }

    final note1Field = findField("note_1");
    final remark1Field = findField("remark_1");

    return CommentDetails(note1: readValue(note1Field), remark1: readValue(remark1Field));
  }

  Future<void> createCitationData() async {
    final DateTime now = .now();
    final violationDetails = getViolationDetails();
    final motoristDetails = getMotoristDetails();
    final locationDetails = getLocationDetails();
    final commentDetails = getCommentDetails();
    final finalLocationDetails = locationDetails.copyWith(spaceId: "");
    final currentLocation = await LocationService().getCurrentLocation();

    model = MunicipalCitationCreationModel(
      code: violationDetails.code,
      locationDetails: finalLocationDetails,
      motoristDetails: motoristDetails,
      commentDetails: commentDetails,
      violationDetails: violationDetails,
      officerDetails: OfficerDetails(
        agency: agency,
        badgeId: officerId,
        officerName: officerName,
        beat: beat,
        zone: locationDetails.spaceId,
        deviceFriendlyName: authService.user?.officerDeviceId.deviceFriendlyName,
        deviceId: authService.user?.officerDeviceId.deviceId,
        shift: authService.user?.officerShift,
        officerLookupCode: "",
        signature: "",
        squad: "",
        peoFname: "",
        peoLname: "",
        peoName: "",
      ),
      hearingDate: "",
      lpNumber: "",
      notes: "",
      invoiceFeeStructure: InvoiceFeeStructure(citationFee: "0.00", parkingFee: "0.00", saleTax: "0.00"),
      ticketNo: citationNumber,
      type: "",
      status: "Valid",
      timeLimitEnforcementObservedTime: "",
      headerDetails: HeaderDetails(
        citationNumber: citationNumber,
        timestamp: DateUtil.simplifiedDate2(ticketDate).replaceAll(" - ", " "),
      ),
      citationStartTimestamp: ticketDate,
      citationIssueTimestamp: now,
      reissue: false,
      timeLimitEnforcement: true,
      timeLimitEnforcementId: "",
      latitude: currentLocation?.latitude.toString(),
      longitude: currentLocation?.longitude.toString(),
      printQuery: "",
      category: "municipal_ticket",
      vehicleDetails: VehicleDetails(),
      imageUrls: imageFileList.map((e) => e.path).toList(),
    );
  }
}

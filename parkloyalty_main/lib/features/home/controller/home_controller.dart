import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/api_endpoints.dart';
import 'package:park_enfoecement/app/core/constants/consts.dart';
import 'package:park_enfoecement/app/core/constants/enums.dart';
import 'package:park_enfoecement/app/core/constants/template_types.dart';
import 'package:park_enfoecement/app/core/controllers/app_controller.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/app/core/repositories/event_logging_repository.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import 'package:park_enfoecement/features/home/data/models/welcome_model.dart' as welcome;
import 'package:park_enfoecement/features/home/data/repository/home_repository.dart';

import '../../../app/core/models/offline_request_model.dart';
import '../../../app/core/models/template_model.dart';
import '../../../app/core/services/auth_service.dart';
import '../../../app/core/services/location_service.dart';
import '../data/models/update_officer_quest_model.dart';
import '../data/repository/home_storage_repository.dart';

class HomeController extends BaseController {
  final HomeRepository repository;
  final HomeStorageRepository storageService;
  final AuthService authService;
  final AppController appController;
  final LocationService locationService;
  final LoaderController loaderController;
  final EventLoggingRepository loggingRepository;

  HomeController(
    this.repository,
    this.storageService,
    this.authService,
    this.appController,
    this.locationService,
    this.loaderController,
    this.loggingRepository,
  );

  @override
  void onInit() {
    currentLogin = DateTime.parse("${authService.currentLogin}").toLocal();
    previousLogin = DateTime.parse("${authService.lastLogin}").toLocal();
    Future.wait([_askLocation(), fetchForm(), fetchAllDataSet()]);

    super.onInit();
  }

  welcome.User? user;
  List<TemplateRes> res = [];
  welcome.WelcomeModel? welcomeModel;
  TemplateModel? _templateModel;
  DateTime currentLogin = DateTime.now();
  GlobalKey<FormState> formKey = GlobalKey<FormState>();
  DateTime previousLogin = DateTime.now();

  Future<void> fetchForm() async {
    loaderController.showLoader();

    try {
      await run(
        () async {
          await Future.wait([
            ...TemplateTypes.templateTypeList.map((e) async {
              await repository.fetchWelcomeForm(e, storageService);
            }),
            getCitationBook(),
          ]);

          _templateModel = storageService.getSavedTemplate(TemplateTypes.activity);

          res = _templateModel!.data[0].response;
          welcomeModel = await repository.welcome();
          user = welcomeModel!.data[0].response.user;
          authService.storeUserDetails(user!.toJson());

          for (final component in _templateModel!.data[0].response) {
            component.fields.sort(
              (a, b) => _parseFormLayoutOrder(b.formLayoutOrder).compareTo(_parseFormLayoutOrder(a.formLayoutOrder)),
            );
            for (final field in component.fields) {
              if (field.collectionName!.isNotEmpty) {
                var model = storageService.getDropDownData(field.collectionName!);
                var dropDownModel = DropDownModel.fromJson(jsonDecode(model));
                field.dropDownOptionList = dropDownModel.data[0].response.map((e) {
                  var datasetMap = jsonDecode(model)['data'][0]['response'].singleWhere((x) => x['_id'] == e.id);
                  return DataSet.fromJson(datasetMap, key: field.fieldName ?? '');
                }).toList();

                field.selectedDropDownOption = field.dropDownOptionList?.firstWhereOrNull((element) {
                  return element.label1 == getInitialFieldData(fieldName: field.name.toLowerCase()) ||
                      element.label2 == getInitialFieldData(fieldName: field.name);
                });
              }
              if (field.collectionName!.isEmpty) {
                field.enteredData = TextEditingController(text: user!.getReleventKey(field.name, authService));
              }
            }
          }
          res.removeWhere(
            (element) =>
                element.component == "OfficerActivity" ||
                element.component == "Login" ||
                element.component == "Officer",
          );
          res.sort((a, b) => a.component!.compareTo(b.component!));

          update();
        },
        whenOffline: () {
          _templateModel = storageService.getSavedTemplate(TemplateTypes.activity);
          res = _templateModel!.data[0].response;
          user = authService.user;
          update();
        },
      );
    } finally {
      loaderController.hideLoader();
    }
  }

  String getInitialFieldData({required String fieldName}) {
    switch (fieldName) {
      case 'supervisor':
        return user?.officerSupervisor ?? "";
      case 'deviceid':
        return user?.officerDeviceId.deviceFriendlyName ?? "";
      case 'agency':
        return user?.officerAgency ?? "";
      default:
        return '';
    }
  }

  Future<void> fetchAllDataSet() async {
    await Future.wait(
      DataSetTypes.typeList.map((collectionName) async {
        final response = await repository.fetchDropDownData(collectionName, storageService);
        appController.setDropdowns(response, collectionName);
      }),
    );
  }

  Future<void> updateOfficer({bool showLoader = true}) async {
    if (!formKey.currentState!.validate()) return;
    if (showLoader) loaderController.showLoader();
    var map = gatherData();
    var user = welcomeModel!.data[0].response.user;
    final updatedDevice = OfficerDeviceId(
      deviceFriendlyName: map['deviceName'],
      devideId: map['deviceid'],
      androidId: Consts.androidId,
    );
    var req = UpdateOfficerRequestModel(
      siteId: user.siteId,
      siteOfficerId: user.siteOfficerId,
      updatePackage: UpdatePackage(
        officerShift: user.officerShift,
        officerSupervisor: map['supervisor'],
        officerSupervisorBadgeId: "0",
        officerAgency: map['agency'],
        officerDeviceId: OfficerDeviceId(
          deviceFriendlyName: map['deviceName'],
          devideId: map['deviceid'],
          androidId: Consts.androidId,
        ),
      ),
    );
    this.user = user.copyWith(
      officerSupervisor: map['supervisor'],
      officerSupervisorBadgeId: "0",
      officerAgency: map['agency'],
      officerDeviceId: welcome.OfficerDeviceId.fromJson(updatedDevice.toJson()),
    );
    authService.storeUserDetails(this.user!.toJson());
    await run(
      () async {
        var res = await repository.updateOfficer(req);
        if (res['data'][0]) {
          logging('success');
        }
      },
      whenOffline: () {
        int key = DateTime.now().millisecond;
        storageService.saveOfflineRequest(
          OfflineRequest(
            body: req.toJson(),
            method: 'post',
            url: ApiEndpoints.updateOfficer,
            createdAt: key.toString(),
          ),
          key.toString(),
        );
      },
    );
    if (showLoader) loaderController.hideLoader();
  }

  Map gatherData() {
    var map = {};
    for (final component in _templateModel!.data[0].response) {
      for (final field in component.fields) {
        if (field.name == 'supervisor') {
          map['supervisor'] = field.selectedDropDownOption!.label1;
        } else if (field.name == 'agency') {
          map['agency'] = field.selectedDropDownOption!.label1;
        } else if (field.name == 'deviceid') {
          map['deviceid'] = field.selectedDropDownOption!.label1;
          map['deviceName'] = field.selectedDropDownOption!.label2;
        }
      }
    }
    return map;
  }

  Future getCitationBook() async {
    run(() async {
      if (storageService.isCitationIdAvailable()) return;
      await repository.issueCitationBook('${Consts.androidId}-Device',storageService);
    });
  }

  Future<void> _askLocation() async {
    await locationService.requestPermission();
  }

  Future<void> onScanPressed() async {
    if (!formKey.currentState!.validate()) return;
    loaderController.showLoader();
    try {
      await updateOfficer(showLoader: false);
      await loggingRepository.updateActivity(activityName: "Welcome Scan");
    } catch (e, s) {
      logging("Error: ${e}\n${s}");
    } finally {
      loaderController.hideLoader();
      Get.toNamed(Routes.manualEntry);
    }
  }

  int _parseFormLayoutOrder(String? value) {
    if (value == null) return 9999;
    final cleaned = value.trim();
    if (cleaned.isEmpty) return 9999;
    final numeric = RegExp(r'\d+').firstMatch(cleaned)?.group(0);
    return int.tryParse(numeric ?? '') ?? 9999;
  }
}

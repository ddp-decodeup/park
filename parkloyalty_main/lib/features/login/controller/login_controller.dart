import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/consts.dart';
import 'package:park_enfoecement/app/core/constants/enums.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/app/core/repositories/event_logging_repository.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';

import '../../../app/core/controllers/base_controller.dart';
import '../../../app/core/routes/app_routes.dart';
import '../../../app/core/services/auth_service.dart';
import '../data/login_repository.dart';

class LoginController extends BaseController {
  final LoginRepository repository;
  final AuthService authService;
  final LoaderController loaderController;
  final EventLoggingRepository loggingRepository;

  LoginController(this.repository, this.authService, this.loaderController, this.loggingRepository);

  final errorMessage = ''.obs;

  final username = ''.obs;
  final password = ''.obs;
  final obscureText = true.obs;
  List<DataSet> shiftList = [];
  List<DataSet> hearingTimeList = [];
  TextEditingController shiftController = TextEditingController();

  GlobalKey<FormState> formKey = GlobalKey<FormState>();

  @override
  void onInit() {
    getInitialDataset();
    super.onInit();
  }

  void togglePasswordVisibility() {
    obscureText.toggle();
  }

  void login() async {
    if (!formKey.currentState!.validate()) {
      return;
    }
    loaderController.showLoader();
    errorMessage.value = '';

    try {
      await run(() async {
        final request = {
          'site_id': Consts.sideId,
          'site_officer_user_name': username.value,
          'site_officer_password': password.value,
        };

        final response = await repository.login(request);

        if (!response.status) {
          Get.snackbar("Login Failed", response.response);
          return;
        }

        if (response.response.isNotEmpty) {
          authService.saveData(
            response.response,
            response.metadata!.currentLogin.toIso8601String(),
            response.metadata!.lastLogin.toIso8601String(),
          );
          await loggingRepository.updateLocationEvent(locationUpdateType: "login");
          Get.offAllNamed(Routes.home);
        }
      });
    } finally {
      loaderController.hideLoader();
    }
  }

  Future<void> getInitialDataset() async {
    loaderController.showLoader();
    try {
      await run(() async {
        await Future.wait(
          DataSetTypes.initialDataSetTypes.map((type) async {
            final response = await repository.getDataSetWithoutToken(type: type, siteId: Consts.sideId);
            if (type == DataSetTypes.shiftList) {
              shiftList = response.data[0].response;
            } else if (type == DataSetTypes.hearingTimeList) {
              hearingTimeList = response.data[0].response;
            } else {
              shiftList = [];
              hearingTimeList = [];
            }
          }),
        );
      });
    } finally {
      loaderController.hideLoader();
      update();
    }
  }

  void onShiftChange(DataSet? value) {
    shiftController.text = value?.label1;
    update();
    Get.back();
  }
}

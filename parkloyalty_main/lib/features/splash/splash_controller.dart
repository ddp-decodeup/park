import 'package:get/get.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';

import '../../app/core/routes/app_routes.dart';
import '../../app/core/services/auth_service.dart';

class SplashController extends GetxController {
  final AuthService authService;

  SplashController(this.authService);

  @override
  void onInit() {
    logging('🔥 SPLASH CONTROLLER RUNNING');
    _checkLogin();
    super.onInit();
    // LocalStorageService.dropDownBox.clear();
  }

  void _checkLogin() async {
    Future.delayed(Duration(seconds: 2), () {
      if (authService.isLoggedIn && authService.token != null) {
        Get.offAllNamed(Routes.home);
      } else {
        Get.offAllNamed(Routes.login);
      }
    });
  }
}

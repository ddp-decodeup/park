import 'package:get/get.dart';
import 'package:park_enfoecement/features/splash/splash_controller.dart';

import '../../../app/core/services/auth_service.dart';

class SplashBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<SplashController>(
      () => SplashController(Get.find<AuthService>()),
    );
    Get.find<SplashController>();
  }
}

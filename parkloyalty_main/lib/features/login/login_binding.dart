import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/repositories/event_logging_repository.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';

import '../../../app/core/services/auth_service.dart';
import 'controller/login_controller.dart';
import 'data/login_repository.dart';

class LoginBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<AuthService>(() => AuthService());

    Get.lazyPut<LoginController>(
      () => LoginController(
        Get.find<LoginRepository>(),
        Get.find<AuthService>(),
        Get.find<LoaderController>(),
        Get.find<EventLoggingRepository>(),
      ),
      fenix: true
    );
  }
}

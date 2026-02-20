import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';

import '../../app/core/controllers/app_controller.dart';
import '../../app/core/repositories/event_logging_repository.dart';
import '../../app/core/services/location_service.dart';
import '../../app/shared/controller/loader_controller.dart';
import 'controller/home_controller.dart';
import 'data/repository/home_repository.dart';
import 'data/repository/home_storage_repository.dart';

class HomeBinding extends Bindings {
  @override
  void dependencies() {
    Get.put<HomeController>(
      HomeController(
        Get.find<HomeRepository>(),
        Get.find<HomeStorageRepository>(),
        Get.find<AuthService>(),
        Get.find<AppController>(),
        Get.find<LocationService>(),
        Get.find<LoaderController>(),
        Get.find<EventLoggingRepository>(),
      ),
    );
  }
}

import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/controllers/app_controller.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/core/services/local_storage_service.dart';
import 'package:park_enfoecement/app/core/services/location_service.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/controller/ticket_issue_controller.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/ticket_issue_repository.dart';

class TicketIssueBindings extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<TicketIssueRepository>(
      () => TicketIssueRepository(Get.find<ApiClient>(),Get.find<HomeStorageRepository>()),
    );
    Get.lazyPut<HomeStorageRepository>(
      () => HomeStorageRepository(Get.find<LocalStorageService>()),
    );
    Get.lazyPut<TicketIssueController>(
      () => TicketIssueController(
        Get.find<TicketIssueRepository>(),
        Get.find<HomeStorageRepository>(),
        Get.find<AppController>(),
        Get.find<LocationService>(),
        Get.find<AuthService>(),
        Get.find<LoaderController>(),
      ),
    );
  }
}

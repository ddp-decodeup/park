import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/controllers/app_controller.dart';
import 'package:park_enfoecement/app/core/repositories/event_logging_repository.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/core/services/brand_config_service.dart';
import 'package:park_enfoecement/app/core/services/local_storage_service.dart';
import 'package:park_enfoecement/app/core/services/location_service.dart';
import 'package:park_enfoecement/app/shared/controller/bottom_nav_bar_controller.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/home/data/repository/home_repository.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/login/data/login_repository.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/ticket_issue_repository.dart';

import '../services/offline_sync_service.dart';

class Di extends Bindings {
  @override
  void dependencies() {
    Get.put<AppController>(AppController(), permanent: true);
    Get.lazyPut<LocationService>(() => LocationService(), fenix: true);
    Get.lazyPut<AuthService>(() => AuthService(), fenix: true);
    Get.lazyPut<BrandConfigService>(() => BrandConfigService(), fenix: true);
    Get.lazyPut<LoaderController>(() => LoaderController(), fenix: true);
    Get.put<BottomNavBarController>(BottomNavBarController());
    Get.lazyPut<ApiClient>(() => ApiClient(), fenix: true);
    Get.lazyPut<LocalStorageService>(() => LocalStorageService(), fenix: true);
    Get.lazyPut<HomeStorageRepository>(() => HomeStorageRepository(Get.find<LocalStorageService>()), fenix: true);
    Get.lazyPut<LoginRepository>(
      () => LoginRepository(Get.find<ApiClient>(), Get.find<HomeStorageRepository>()),
      fenix: true,
    );
    Get.lazyPut<HomeRepository>(() => HomeRepository(Get.find<ApiClient>()), fenix: true);
    Get.lazyPut<TicketIssueRepository>(() => TicketIssueRepository(Get.find<ApiClient>(),Get.find<HomeStorageRepository>()), fenix: true);
    Get.lazyPut<EventLoggingRepository>(() => EventLoggingRepository(Get.find<ApiClient>()), fenix: true);

    Get.find<BrandConfigService>().applyCurrentConfiguration();

    final offlineSyncService = OfflineSyncService(
      apiClient: Get.find<ApiClient>(),
      localStorage: Get.find<HomeStorageRepository>(),
    );
    offlineSyncService.startListening();
  }
}

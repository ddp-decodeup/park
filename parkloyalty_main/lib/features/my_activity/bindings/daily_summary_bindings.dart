import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/my_activity/controller/daily_summary_controller.dart';

import '../data/repositories/daily_summary_repository.dart';

class DailySummaryBindings extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<DailySummaryRepository>(
      () => DailySummaryRepository(Get.find<ApiClient>()),
    );
    Get.lazyPut<DailySummaryController>(
      () => DailySummaryController(
        dailySummaryRepository: Get.find<DailySummaryRepository>(),
        authService: Get.find<AuthService>(),
        loaderController: Get.find<LoaderController>(),
      ),
    );
  }
}

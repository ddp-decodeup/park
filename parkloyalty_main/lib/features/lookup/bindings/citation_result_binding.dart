import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/lookup/controllers/citations_controller.dart';

import '../controllers/timing_records_controller.dart';
import '../data/repositories/citations_repository.dart';

class CitationResultBinding extends Bindings {
  @override
  void dependencies() {
    Get.put<CitationsRepository>(
      CitationsRepository(
        Get.find<ApiClient>(),
        Get.find<HomeStorageRepository>(),
        Get.find<AuthService>(),
      ),
    );
    Get.put<CitationsController>(
      CitationsController(citationsRepository: Get.find<CitationsRepository>()),
    );
    Get.put<TimingRecordsController>(
      TimingRecordsController(
        citationsRepository: Get.find<CitationsRepository>(),
        loaderController: Get.find<LoaderController>(),
      ),
    );
  }
}

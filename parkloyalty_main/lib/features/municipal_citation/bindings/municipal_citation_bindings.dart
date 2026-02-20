import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/controllers/app_controller.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/municipal_citation/controller/municipal_citation_controller.dart';
import 'package:park_enfoecement/features/municipal_citation/data/repositories/municipal_citation_repository.dart';

class MunicipalCitationBindings extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<MunicipalCitationRepository>(
      () => MunicipalCitationRepository(
        apiClient: Get.find<ApiClient>(),
        storageRepository: Get.find<HomeStorageRepository>(),
      ),
    );
    Get.lazyPut<MunicipalCitationController>(
      () => MunicipalCitationController(
        authService: Get.find<AuthService>(),
        loaderController: Get.find<LoaderController>(),
        repository: Get.find<MunicipalCitationRepository>(),
        appController: Get.find<AppController>(),
      ),
    );
  }
}

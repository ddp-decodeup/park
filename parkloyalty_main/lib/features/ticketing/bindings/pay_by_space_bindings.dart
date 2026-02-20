import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/controller/pay_by_space_controller.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/pay_by_space_repository.dart';

class PayBySpaceBindings extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<PayBySpaceRepository>(
      () => PayBySpaceRepository(Get.find<ApiClient>()),
    );
    Get.lazyPut<PayBySpaceController>(
      () => PayBySpaceController(
        payBySpaceRepository: Get.find<PayBySpaceRepository>(),
        loaderController: Get.find<LoaderController>(),
        homeStorageRepository: Get.find<HomeStorageRepository>(),
      ),
    );
  }
}

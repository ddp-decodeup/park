import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/controller/pay_by_plate_controller.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/pay_by_plate_repository.dart';

class PayByPlateBindings extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<PayByPlateRepository>(
      () => PayByPlateRepository(Get.find<ApiClient>()),
    );
    Get.lazyPut<PayByPlateController>(
      () => PayByPlateController(
        payByPlateRepository: Get.find<PayByPlateRepository>(),
        loaderController: Get.find<LoaderController>(),
        homeStorageRepository: Get.find<HomeStorageRepository>(),
      ),
    );
  }
}

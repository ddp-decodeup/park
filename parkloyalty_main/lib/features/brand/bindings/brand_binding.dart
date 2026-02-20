import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/services/brand_config_service.dart';
import 'package:park_enfoecement/features/brand/controller/brand_controller.dart';

class BrandBinding extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<BrandController>(
      () => BrandController(Get.find<BrandConfigService>()),
    );

    Get.find<BrandController>();

  }
}

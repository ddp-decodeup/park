import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';

import '../controllers/cite_history_controller.dart';
import '../controllers/manual_data_entry_controller.dart';
import '../controllers/status_controller.dart';
import '../data/repositories/status_and_history_repository.dart';

class ScanBindings extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<StatusAndHistoryRepository>(() => StatusAndHistoryRepository(Get.find<ApiClient>()));
    Get.lazyPut<ManualDataEntryController>(() => ManualDataEntryController(Get.find<LoaderController>()));
    Get.put<CiteHistoryController>(CiteHistoryController(Get.find<StatusAndHistoryRepository>()));
    Get.put<StatusController>(StatusController(Get.find<StatusAndHistoryRepository>()));
  }
}

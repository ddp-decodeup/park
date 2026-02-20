import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/scan/controllers/status_controller.dart';

import 'cite_history_controller.dart';

class ManualDataEntryController extends BaseController {
  ManualDataEntryController(this.loaderController);

  final LoaderController loaderController;

  String lprNumber='';

  void clearLprNumber() {
    lprNumber = '';
    update();
  }

  void onChangeLpr(String s){
    lprNumber=s;
  }

  Future<void> check() async {
    Get.find<CiteHistoryController>().clearHistoryList();
    Get.find<StatusController>().clearStatusList();
    loaderController.showLoader();
    try {
      await run(() async {
        await Get.find<CiteHistoryController>().fetchHistory(lprNumber);
        await Get.find<StatusController>().fetchStatusList(lprNumber);
      });
    } finally {
      loaderController.hideLoader();
    }

  }
}
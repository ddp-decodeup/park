import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import 'package:park_enfoecement/app/shared/utils/snackbar_utils.dart';

import '../exceptions/exception_handler.dart';
import '../exceptions/network_exception.dart';
import '../localization/local_keys.dart';

abstract class BaseController extends GetxController {
  Future<T?> run<T>(Future<T> Function() action, {void Function()? whenOffline}) async {
    try {
      return await action();
    } on NetworkException catch (e) {
      logging("Error: $e");
      if (whenOffline == null) {
        SnackBarUtils.showSnackBar(message: LocalKeys.offlineMsg.tr, color: AppColors.errorRed);
      } else {
        whenOffline();
      }
    } on Error catch (e) {
      logging('error:: $e ${e.stackTrace}');
      if (whenOffline == null) {
        handleError(e);
      }
      if (e is NetworkException) {
        whenOffline?.call();
        if (whenOffline != null) {
          Get.snackbar(LocalKeys.offline.tr, LocalKeys.offlineMsg.tr);
        }
      }
      return null;
    }
    return null;
  }

  void handleError(dynamic e) {
    final message = ExceptionHandler.getMessage(e);
    Get.snackbar("Error", message);
  }
}

import 'package:flutter/animation.dart';
import 'package:get/get.dart';

class SnackBarUtils {
  SnackBarUtils._();

  static void showSnackBar({required String message, required Color color}) {
    Get.closeAllSnackbars();
    Get.showSnackbar(
      GetSnackBar(
        message: message,
        backgroundColor: color,
        duration: Duration(seconds: 2),
      ),
    );
  }
}

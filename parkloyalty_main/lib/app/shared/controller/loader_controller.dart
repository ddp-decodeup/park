import 'package:get/get.dart';

class LoaderController extends GetxController {
  bool loader = false;

  void showLoader() {
    Future.microtask(() {
      loader = true;
      update();
    });
  }

  void hideLoader() {
    Future.microtask(() {
      loader = false;
      update();
    });
  }
}

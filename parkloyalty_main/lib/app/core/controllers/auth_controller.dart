import 'package:get/get.dart';

import '../routes/app_routes.dart';
import '../services/auth_service.dart';

class AuthController extends GetxController {
  final AuthService authService;

  AuthController(this.authService);

  Future<void> logout() async {
    authService.clearSession();

    Get.deleteAll(force: true);

    Get.offAllNamed(Routes.login);
  }
}

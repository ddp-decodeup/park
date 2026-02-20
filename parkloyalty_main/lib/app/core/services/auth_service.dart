import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:park_enfoecement/app/core/constants/storage_keys.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/features/home/data/models/welcome_model.dart';

class AuthService extends GetxService {
  final _box = GetStorage();

  // Save token
  void saveData(String token, String currentLogin, String lastLogin) {
    _box.write(StorageKeys.authToken, token);
    _box.write(StorageKeys.isLoggedIn, true);
    _box.write(StorageKeys.currentLogin, currentLogin);
    _box.write(StorageKeys.lastLogin, lastLogin);
  }

  void storeUserDetails(Map<String, dynamic> userDetails) {
    _box.write(StorageKeys.userDetails, userDetails);
  }

  User? get user {
    final raw = _box.read(StorageKeys.userDetails);
    if (raw is Map) {
      return User.fromJson(Map<String, dynamic>.from(raw));
    }
    return null;
  }

  String? get token => _box.read(StorageKeys.authToken);

  String? get lastLogin => _box.read(StorageKeys.lastLogin);

  String? get currentLogin => _box.read(StorageKeys.currentLogin);

  bool get isLoggedIn => _box.read(StorageKeys.isLoggedIn) ?? false;

  void clearSession() {
    _box.remove(StorageKeys.authToken);
    _box.remove(StorageKeys.isLoggedIn);
    _box.remove(StorageKeys.currentLogin);
    _box.remove(StorageKeys.lastLogin);
    _box.remove(StorageKeys.userDetails);
    if (Get.routing.current != Routes.login) {
      Get.offAllNamed(Routes.login);
    }
  }
}

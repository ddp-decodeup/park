import 'dart:convert';

import 'package:park_enfoecement/app/core/constants/consts.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';

import '../../../app/core/api/api_client.dart';
import '../../../app/core/constants/api_endpoints.dart';
import 'login_response.dart';

class LoginRepository {
  final ApiClient apiClient;
  final HomeStorageRepository storageService;

  LoginRepository(this.apiClient, this.storageService);

  Future<DropDownModel> getDataSetWithoutToken({
    required String type,
    required String siteId,
  }) async {
    if (storageService.isDropDownDataAvailable(type)) {
      return DropDownModel.fromJson(
        jsonDecode(storageService.getDropDownData(type)),
      );
    }
    final response = await apiClient.postRequest(
      ApiEndpoints.getDataSetWithoutToken,
      {"type": type, "shard": Consts.shard, "site_id": siteId},
    );
    storageService.saveDropDownData(type, jsonEncode(response.body));
    return DropDownModel.fromJson(response.body);
  }

  Future<LoginResponse> login(Map<String, String> request) async {
    /*RecaptchaClient client = await Recaptcha.fetchClient('6LehlLAcAAAAAOWuW3qLLbUKE53Wqe2oCmvtFHRk');
    String token = await client.execute(RecaptchaAction.LOGIN());*/
    final response = await apiClient.postRequest(ApiEndpoints.login, request);

    return LoginResponse.fromJson(response.body);
  }
}

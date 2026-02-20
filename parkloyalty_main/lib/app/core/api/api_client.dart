import 'dart:convert';
import 'dart:io';

import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/api_endpoints.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import 'package:park_enfoecement/app/shared/utils/snackbar_utils.dart';

import '../exceptions/api_exception.dart';
import '../exceptions/network_exception.dart';
import '../services/auth_service.dart';

class ApiClient extends GetConnect {
  @override
  void onInit() {
    httpClient.timeout = const Duration(seconds: 30);
    httpClient.baseUrl = ApiEndpoints.baseUrl;

    httpClient.addRequestModifier<dynamic>((request) async {
      final authService = Get.find<AuthService>();
      final token = authService.token;

      if (token != null && token.isNotEmpty) {
        request.headers['token'] = token;
      }

      // request.headers['Content-Type'] = 'application/json';
      request.headers['Accept'] = 'application/json';

      return request;
    });

    httpClient.addResponseModifier((request, response) {
      if (response.statusCode == 401) {
        final authService = Get.find<AuthService>();
        authService.clearSession();
        SnackBarUtils.showSnackBar(message: LocalKeys.tokenExpiredMessage.tr, color: AppColors.errorRed);
      }
      return response;
    });
    super.onInit();
  }

  Future<Response> getRequest(String url, {Map<String, dynamic>? query}) async {
    try {
      final response = await get(url, query: query);

      if (response.status.connectionError) {
        throw NetworkException("No internet connection");
      } else if (response.status.hasError) {
        throw _handleError(response);
      }

      return response;
    } on SocketException {
      throw NetworkException("No internet connection");
    }
  }

  Future<Response> postRequest(String url, Map<String, dynamic> body) async {
    try {
      final response = await post(url, body);

      logging('req:: $url\n${jsonEncode(body)}');

      if (response.status.connectionError) {
        throw NetworkException("No internet connection");
      } else if (response.status.hasError) {
        throw _handleError(response);
      }

      return response;
    } on SocketException {
      throw NetworkException("No internet connection");
    }
  }

  Future<Response> patchRequest(String url, Map<String, dynamic> body) async {
    try {
      final response = await patch(url, body);

      logging('req:: $url\n${jsonEncode(body)}');

      if (response.status.connectionError) {
        throw NetworkException("No internet connection");
      } else if (response.status.hasError) {
        throw _handleError(response);
      }

      return response;
    } on SocketException {
      throw NetworkException("No internet connection");
    }
  }

  Future<Response> uploadImages(String url, Map<String, dynamic> formMap) async {
    try {
      final form = FormData(formMap);

      return post(url, form);
    } on SocketException {
      throw NetworkException("No internet connection");
    }
  }

  AppException _handleError(Response response) {
    final statusCode = response.statusCode ?? 0;
    String message = "";
    logging("\n\n\nResponse.body:\n${response.request?.url}\n${response.body}\n\n\n");
    try {
      message = response.body?['message'] ?? "Unknown error";
    } catch (e) {
      message = "Unknown error";
    }
    switch (statusCode) {
      case 400:
        return BadRequestException(message, statusCode: statusCode);
      case 401:
        return UnauthorizedException(message, statusCode: statusCode);
      case 500:
        return ServerException("Server error", statusCode: statusCode);
      default:
        return ServerException(message, statusCode: statusCode);
    }
  }
}

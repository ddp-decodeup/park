import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/constants/api_endpoints.dart';

class DailySummaryRepository extends GetxService {
  final ApiClient apiClient;

  DailySummaryRepository(this.apiClient);

  Future<Response> getDailySummary({required String shift}) {
    return apiClient.getRequest(ApiEndpoints.getDailySummary + "?shift=$shift");
  }
}

import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/constants/api_endpoints.dart';

class PayBySpaceRepository extends GetxService {
  final ApiClient apiClient;

  PayBySpaceRepository(this.apiClient);

  Future<Response> getPayBySpaceAnalytics({
    String? zone,
    String? lpNumber,
  }) async {
    Map<String, dynamic> query = {};

    if (zone != null && zone.isNotEmpty) {
      query['zone'] = zone;
    }
    if (lpNumber != null && lpNumber.isNotEmpty && lpNumber.length >= 4) {
      query['lp_number'] = lpNumber;
    }
    final response = await apiClient.getRequest(
      ApiEndpoints.getPayByPlateAnalytics,
      query: query,
    );
    return response;
  }
}

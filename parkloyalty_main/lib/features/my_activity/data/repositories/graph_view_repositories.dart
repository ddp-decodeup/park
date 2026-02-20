import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/constants/api_endpoints.dart';

class GraphViewRepository extends GetxService {
  final ApiClient apiClient;

  GraphViewRepository(this.apiClient);

  Future<Response> getBarChartData({required String shift}) async {
    final response = await apiClient.getRequest(ApiEndpoints.getAnalyticsCount + "?shift=$shift");

    return response;
  }

  Future<Response> getLineChartData({required String shift, required String timeline}) async {
    final response = await apiClient.getRequest(
      ApiEndpoints.getAnalyticsArrayCount + "?shift=$shift&timeline=$timeline",
    );

    return response;
  }

  Future<Response> getViolationCountData({required String shift}) async {
    final response = await apiClient.getRequest(ApiEndpoints.getOfficerViolationCountData + "?shift=$shift");

    return response;
  }

  Future<Response> getLocationsData({required String shift}) async {
    final response = await apiClient.getRequest(ApiEndpoints.getLocations + "?shift=$shift");
    return response;
  }

  Future<Response> getActivityLogData({required String shift}) async {
    try {
      final response = await apiClient.getRequest(ApiEndpoints.getActivityLog + "?shift=$shift");
      return response;
    } catch (e) {
      return Response(statusCode: 500, statusText: "Internal Server Error");
    }
  }
}

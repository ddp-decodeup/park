import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/constants/api_endpoints.dart';
import 'package:park_enfoecement/app/core/constants/consts.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/core/services/location_service.dart';
import 'package:park_enfoecement/app/shared/widgets/activity_update_request.dart';

class EventLoggingRepository extends GetxService {
  final ApiClient apiClient;

  EventLoggingRepository(this.apiClient);

  Future<void> updateActivity({required String activityName}) async {
    try {
      final position = await LocationService().getCurrentLocation();
      final authService = Get.isRegistered<AuthService>()
          ? Get.find<AuthService>()
          : null;
      final user = authService?.user;
      ActivityUpdateRequest req = ActivityUpdateRequest(
        activityType: 'ActivityUpdate',
        activityId: null,
        clientTimestamp: DateTime.now().toUtc().toIso8601String(),
        initiatorId: user?.siteOfficerId ?? '',
        initiatorRole: user?.role ?? '',
        latitude: position?.latitude,
        logType: 'NodePort',
        longitude: position?.longitude,
        siteId: Consts.sideId,
        activityName: activityName,
        shift: user?.officerShift ?? Consts.shift,
        isDisplay: true,
        androidId: Consts.androidId,
      );
      await apiClient.postRequest(ApiEndpoints.updateActivity, req.toJson());
    } catch (_) {}
  }

  Future<void> updateLocationEvent({
    String locationUpdateType = "regular",
    String? deviceId,
  }) async {
    try {
      final position = await LocationService().getCurrentLocation();
      final authService = Get.isRegistered<AuthService>()
          ? Get.find<AuthService>()
          : null;
      final user = authService?.user;
      final body = {
        "activity_type": "LocationUpdate",
        "latitude": position?.latitude,
        "location_update_type": locationUpdateType,
        "log_type": "NodePort",
        "longitude": position?.longitude,
        "site_id": Consts.sideId,
        "client_timestamp": DateTime.now().toUtc().toIso8601String(),
        "shift": user?.officerShift ?? Consts.shift,
        "device_id": deviceId,
      };
      await apiClient.postRequest(ApiEndpoints.updateLocation, body);
    } catch (_) {}
  }
}

import 'dart:convert';

LocationModel locationModelFromJson(String str) =>
    LocationModel.fromJson(json.decode(str));

String locationModelToJson(LocationModel data) => json.encode(data.toJson());

class LocationModel {
  final List<LocationData>? data;
  final bool? success;

  LocationModel({this.data, this.success});

  factory LocationModel.fromJson(Map<String, dynamic> json) => LocationModel(
    data: json["data"] == null
        ? []
        : List<LocationData>.from(
            json["data"].map((x) => LocationData.fromJson(x)),
          ),
    success: json["success"],
  );

  Map<String, dynamic> toJson() => {
    "data": data?.map((x) => x.toJson()).toList() ?? [],
    "success": success,
  };
}

class LocationData {
  final String? id;
  final String? initiatorId;
  final String? initiatorRole;
  final String? activityType;
  final String? locationUpdateType;
  final String? siteId;
  final String? logType;
  final String? agency;
  final int? serverTimestamp;
  final double? latitude;
  final String? shift;
  final double? longitude;
  final DateTime? clientTimestamp;
  final String? deviceId;

  LocationData({
    this.id,
    this.initiatorId,
    this.initiatorRole,
    this.activityType,
    this.locationUpdateType,
    this.siteId,
    this.logType,
    this.agency,
    this.serverTimestamp,
    this.latitude,
    this.shift,
    this.longitude,
    this.clientTimestamp,
    this.deviceId,
  });

  factory LocationData.fromJson(Map<String, dynamic> json) => LocationData(
    id: json["id"],
    initiatorId: json["initiator_id"],
    initiatorRole: json["initiator_role"],
    activityType: json["activity_type"],
    locationUpdateType: json["location_update_type"],
    siteId: json["site_id"],
    logType: json["log_type"],
    agency: json["agency"],
    serverTimestamp: json["server_timestamp"],
    latitude: json["latitude"]?.toDouble(),
    shift: json["shift"],
    longitude: json["longitude"]?.toDouble(),
    clientTimestamp: json["client_timestamp"] == null
        ? null
        : DateTime.parse(json["client_timestamp"]),
    deviceId: json["device_id"],
  );

  Map<String, dynamic> toJson() => {
    "id": id,
    "initiator_id": initiatorId,
    "initiator_role": initiatorRole,
    "activity_type": activityType,
    "location_update_type": locationUpdateType,
    "site_id": siteId,
    "log_type": logType,
    "agency": agency,
    "server_timestamp": serverTimestamp,
    "latitude": latitude,
    "shift": shift,
    "longitude": longitude,
    "client_timestamp": clientTimestamp?.toIso8601String(),
    "device_id": deviceId,
  };
}

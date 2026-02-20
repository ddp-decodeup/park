// To parse this JSON data, do
//
//     final activityUpdateRequest = activityUpdateRequestFromJson(jsonString);

import 'dart:convert';

ActivityUpdateRequest activityUpdateRequestFromJson(String str) =>
    ActivityUpdateRequest.fromJson(json.decode(str));

String activityUpdateRequestToJson(ActivityUpdateRequest data) =>
    json.encode(data.toJson());

class ActivityUpdateRequest {
  String activityType;
  dynamic activityId;
  String clientTimestamp;
  String initiatorId;
  String initiatorRole;
  double? latitude;
  String logType;
  double? longitude;
  String siteId;
  String activityName;
  String shift;
  dynamic block;
  dynamic street;
  dynamic side;
  dynamic squad;
  dynamic deviceId;
  dynamic image1;
  dynamic image2;
  dynamic image3;
  bool isDisplay;
  String androidId;

  ActivityUpdateRequest({
    required this.activityType,
    required this.activityId,
    required this.clientTimestamp,
    required this.initiatorId,
    required this.initiatorRole,
    required this.latitude,
    required this.logType,
    required this.longitude,
    required this.siteId,
    required this.activityName,
    required this.shift,
    this.block,
    this.street,
    this.side,
    this.squad,
    this.deviceId,
    this.image1,
    this.image2,
    this.image3,
    required this.isDisplay,
    required this.androidId,
  });

  factory ActivityUpdateRequest.fromJson(Map<String, dynamic> json) =>
      ActivityUpdateRequest(
        activityType: json["activity_type"],
        activityId: json["activity_id"],
        clientTimestamp: json["client_timestamp"],
        initiatorId: json["initiator_id"],
        initiatorRole: json["initiator_role"],
        latitude: json["latitude"]?.toDouble(),
        logType: json["log_type"],
        longitude: json["longitude"]?.toDouble(),
        siteId: json["site_id"],
        activityName: json["activity_name"],
        shift: json["shift"],
        block: json["block"],
        street: json["street"],
        side: json["side"],
        squad: json["squad"],
        deviceId: json["device_id"],
        image1: json["image_1"],
        image2: json["image_2"],
        image3: json["image_3"],
        isDisplay: json["is_display"],
        androidId: json["android_id"],
      );

  Map<String, dynamic> toJson() => {
    "activity_type": activityType,
    "activity_id": activityId,
    "client_timestamp": clientTimestamp,
    "initiator_id": initiatorId,
    "initiator_role": initiatorRole,
    "latitude": latitude,
    "log_type": logType,
    "longitude": longitude,
    "site_id": siteId,
    "activity_name": activityName,
    "shift": shift,
    "block": block,
    "street": street,
    "side": side,
    "squad": squad,
    "device_id": deviceId,
    "image_1": image1,
    "image_2": image2,
    "image_3": image3,
    "is_display": isDisplay,
    "android_id": androidId,
  };
}

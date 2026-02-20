// To parse this JSON data, do
//
//     final activityLogModel = activityLogModelFromJson(jsonString);

import 'dart:convert';

ActivityLogModel activityLogModelFromJson(String str) =>
    ActivityLogModel.fromJson(json.decode(str));

String activityLogModelToJson(ActivityLogModel data) =>
    json.encode(data.toJson());

class ActivityLogModel {
  final List<ActivityData>? data;
  final bool? status;
  final String? message;

  ActivityLogModel({this.data, this.status, this.message});

  factory ActivityLogModel.fromJson(Map<String, dynamic> json) =>
      ActivityLogModel(
        data: json["data"] == null
            ? []
            : List<ActivityData>.from(
                json["data"]!.map((x) => ActivityData.fromJson(x)),
              ),
        status: json["status"],
        message: json["message"],
      );

  Map<String, dynamic> toJson() => {
    "data": data == null
        ? []
        : List<dynamic>.from(data!.map((x) => x.toJson())),
    "status": status,
    "message": message,
  };
}

class ActivityData {
  final List<ActivityUpdate>? activityUpdates;

  ActivityData({this.activityUpdates});

  factory ActivityData.fromJson(Map<String, dynamic> json) => ActivityData(
    activityUpdates: json["activity_updates"] == null
        ? []
        : List<ActivityUpdate>.from(
            json["activity_updates"]!.map((x) => ActivityUpdate.fromJson(x)),
          ),
  );

  Map<String, dynamic> toJson() => {
    "activity_updates": activityUpdates == null
        ? []
        : List<dynamic>.from(activityUpdates!.map((x) => x.toJson())),
  };
}

class ActivityUpdate {
  final String? id;
  final String? initiatorId;
  final String? initiatorRole;
  final String? activityType;
  final String? siteId;
  final String? logType;
  final int? serverTimestamp;
  final String? activityName;
  final DateTime? clientTimestamp;

  ActivityUpdate({
    this.id,
    this.initiatorId,
    this.initiatorRole,
    this.activityType,
    this.siteId,
    this.logType,
    this.serverTimestamp,
    this.activityName,
    this.clientTimestamp,
  });

  factory ActivityUpdate.fromJson(Map<String, dynamic> json) => ActivityUpdate(
    id: json["_id"],
    initiatorId: json["initiator_id"],
    initiatorRole: json["initiator_role"],
    activityType: json["activity_type"],
    siteId: json["site_id"],
    logType: json["log_type"],
    serverTimestamp: json["server_timestamp"],
    activityName: json["activity_name"],
    clientTimestamp: json["client_timestamp"] == null
        ? null
        : DateTime.parse(json["client_timestamp"] + "Z").toLocal(),
  );

  Map<String, dynamic> toJson() => {
    "_id": id,
    "initiator_id": initiatorId,
    "initiator_role": initiatorRole,
    "activity_type": activityType,
    "site_id": siteId,
    "log_type": logType,
    "server_timestamp": serverTimestamp,
    "activity_name": activityName,
    "client_timestamp": clientTimestamp?.toIso8601String(),
  };
}

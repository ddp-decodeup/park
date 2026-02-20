// To parse this JSON data, do
//
//     final welcomeModel = welcomeModelFromJson(jsonString);

import 'dart:convert';

import 'package:park_enfoecement/app/core/constants/enums.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';

WelcomeModel welcomeModelFromJson(String str) =>
    WelcomeModel.fromJson(json.decode(str));

String welcomeModelToJson(WelcomeModel data) => json.encode(data.toJson());

class WelcomeModel {
  final List<Datum> data;
  final bool status;
  final String message;

  WelcomeModel({
    required this.data,
    required this.status,
    required this.message,
  });

  factory WelcomeModel.fromJson(Map<String, dynamic> json) => WelcomeModel(
    data: List<Datum>.from(json["data"].map((x) => Datum.fromJson(x))),
    status: json["status"],
    message: json["message"],
  );

  Map<String, dynamic> toJson() => {
    "data": List<dynamic>.from(data.map((x) => x.toJson())),
    "status": status,
    "message": message,
  };

  WelcomeModel copyWith({List<Datum>? data, bool? status, String? message}) {
    return WelcomeModel(
      data: data ?? this.data,
      status: status ?? this.status,
      message: message ?? this.message,
    );
  }
}

class Datum {
  final bool status;
  final Response response;
  final Metadata metadata;

  Datum({required this.status, required this.response, required this.metadata});

  factory Datum.fromJson(Map<String, dynamic> json) => Datum(
    status: json["status"],
    response: Response.fromJson(json["response"]),
    metadata: Metadata.fromJson(json["metadata"]),
  );

  Map<String, dynamic> toJson() => {
    "status": status,
    "response": response.toJson(),
    "metadata": metadata.toJson(),
  };

  Datum copyWith({bool? status, Response? response, Metadata? metadata}) {
    return Datum(
      status: status ?? this.status,
      response: response ?? this.response,
      metadata: metadata ?? this.metadata,
    );
  }
}

class Metadata {
  final DateTime serverTimestamp;

  Metadata({required this.serverTimestamp});

  factory Metadata.fromJson(Map<String, dynamic> json) =>
      Metadata(serverTimestamp: DateTime.parse(json["server_timestamp"]));

  Map<String, dynamic> toJson() => {
    "server_timestamp": serverTimestamp.toIso8601String(),
  };

  Metadata copyWith({DateTime? serverTimestamp}) {
    return Metadata(serverTimestamp: serverTimestamp ?? this.serverTimestamp);
  }
}

class Response {
  final User user;

  Response({required this.user});

  factory Response.fromJson(Map<String, dynamic> json) =>
      Response(user: User.fromJson(json["user"]));

  Map<String, dynamic> toJson() => {"user": user.toJson()};

  Response copyWith({User? user}) {
    return Response(user: user ?? this.user);
  }
}

class User {
  final String id;
  final String officerFirstName;
  final String officerMiddleName;
  final String officerLastName;
  final String officerUserName;
  final int officerBadgeId;
  final String officerSquad;
  final String officerBeat;
  final String officerRadio;
  final String officerShift;
  final String officerZone;
  final String officerAgency;
  final OfficerDeviceId officerDeviceId;
  final String siteOfficerId;
  final String role;
  final String siteId;
  final bool enable;
  final String cityZone;
  final String lot;
  final String officerEquipment;
  final String officerSupervisor;
  final String officerSupervisorBadgeId;
  final String signature;
  final DateTime timestamp;

  User({
    required this.id,
    required this.officerFirstName,
    required this.officerMiddleName,
    required this.officerLastName,
    required this.officerUserName,
    required this.officerBadgeId,
    required this.officerSquad,
    required this.officerBeat,
    required this.officerRadio,
    required this.officerShift,
    required this.officerZone,
    required this.officerAgency,
    required this.officerDeviceId,
    required this.siteOfficerId,
    required this.role,
    required this.siteId,
    required this.enable,
    required this.cityZone,
    required this.lot,
    required this.officerEquipment,
    required this.officerSupervisor,
    required this.officerSupervisorBadgeId,
    required this.signature,
    required this.timestamp,
  });

  factory User.fromJson(Map<String, dynamic> json) => User(
    id: json["_id"],
    officerFirstName: json["officer_first_name"],
    officerMiddleName: json["officer_middle_name"],
    officerLastName: json["officer_last_name"],
    officerUserName: json["officer_user_name"],
    officerBadgeId: json["officer_badge_id"],
    officerSquad: json["officer_squad"],
    officerBeat: json["officer_beat"],
    officerRadio: json["officer_radio"],
    officerShift: json["officer_shift"],
    officerZone: json["officer_zone"],
    officerAgency: json["officer_agency"],
    officerDeviceId: OfficerDeviceId.fromJson(json["officer_device_id"]),
    siteOfficerId: json["site_officer_id"],
    role: json["role"],
    siteId: json["site_id"],
    enable: json["enable"],
    cityZone: json["city_zone"],
    lot: json["lot"],
    officerEquipment: json["officer_equipment"],
    officerSupervisor: json["officer_supervisor"],
    officerSupervisorBadgeId: json["officer_supervisor_badge_id"].toString(),
    signature: json["signature"],
    timestamp: DateTime.parse(json["timestamp"]),
  );

  Map<String, dynamic> toJson() => {
    "_id": id,
    "officer_first_name": officerFirstName,
    "officer_middle_name": officerMiddleName,
    "officer_last_name": officerLastName,
    "officer_user_name": officerUserName,
    "officer_badge_id": officerBadgeId,
    "officer_squad": officerSquad,
    "officer_beat": officerBeat,
    "officer_radio": officerRadio,
    "officer_shift": officerShift,
    "officer_zone": officerZone,
    "officer_agency": officerAgency,
    "officer_device_id": officerDeviceId.toJson(),
    "site_officer_id": siteOfficerId,
    "role": role,
    "site_id": siteId,
    "enable": enable,
    "city_zone": cityZone,
    "lot": lot,
    "officer_equipment": officerEquipment,
    "officer_supervisor": officerSupervisor,
    "officer_supervisor_badge_id": officerSupervisorBadgeId,
    "signature": signature,
    "timestamp": timestamp.toIso8601String(),
  };

  User copyWith({
    String? id,
    String? officerFirstName,
    String? officerMiddleName,
    String? officerLastName,
    String? officerUserName,
    int? officerBadgeId,
    String? officerSquad,
    String? officerBeat,
    String? officerRadio,
    String? officerShift,
    String? officerZone,
    String? officerAgency,
    OfficerDeviceId? officerDeviceId,
    String? siteOfficerId,
    String? role,
    String? siteId,
    bool? enable,
    String? cityZone,
    String? lot,
    String? officerEquipment,
    String? officerSupervisor,
    String? officerSupervisorBadgeId,
    String? signature,
    DateTime? timestamp,
  }) {
    return User(
      id: id ?? this.id,
      officerFirstName: officerFirstName ?? this.officerFirstName,
      officerMiddleName: officerMiddleName ?? this.officerMiddleName,
      officerLastName: officerLastName ?? this.officerLastName,
      officerUserName: officerUserName ?? this.officerUserName,
      officerBadgeId: officerBadgeId ?? this.officerBadgeId,
      officerSquad: officerSquad ?? this.officerSquad,
      officerBeat: officerBeat ?? this.officerBeat,
      officerRadio: officerRadio ?? this.officerRadio,
      officerShift: officerShift ?? this.officerShift,
      officerZone: officerZone ?? this.officerZone,
      officerAgency: officerAgency ?? this.officerAgency,
      officerDeviceId: officerDeviceId ?? this.officerDeviceId,
      siteOfficerId: siteOfficerId ?? this.siteOfficerId,
      role: role ?? this.role,
      siteId: siteId ?? this.siteId,
      enable: enable ?? this.enable,
      cityZone: cityZone ?? this.cityZone,
      lot: lot ?? this.lot,
      officerEquipment: officerEquipment ?? this.officerEquipment,
      officerSupervisor: officerSupervisor ?? this.officerSupervisor,
      officerSupervisorBadgeId:
          officerSupervisorBadgeId ?? this.officerSupervisorBadgeId,
      signature: signature ?? this.signature,
      timestamp: timestamp ?? this.timestamp,
    );
  }

  String getReleventKey(String element, AuthService authService) {
    if (element == DataSetTypes.agencyList) {
      return officerAgency;
    } else if (element == DataSetTypes.supervisorList) {
      return officerSupervisor;
    } else if (element == DataSetTypes.deviceList) {
      return officerDeviceId.deviceId ?? '';
    } else if (element == 'badge_id') {
      return officerBadgeId.toString();
    } else if (element == 'officer_id') {
      return siteOfficerId.split('-')[0];
    } else if (element == 'previous_login') {
      return authService.lastLogin!;
    } else if (element == 'current_login') {
      return authService.currentLogin!;
    }
    return '';
  }
}

class OfficerDeviceId {
  final String deviceFriendlyName;
  final String? deviceId;
  final String androidId;

  OfficerDeviceId({
    required this.deviceFriendlyName,
    this.deviceId,
    required this.androidId,
  });

  factory OfficerDeviceId.fromJson(Map<String, dynamic> json) =>
      OfficerDeviceId(
        deviceFriendlyName: json["device_friendly_name"],
        deviceId: json["devide_id"],
        androidId: json["android_id"],
      );

  Map<String, dynamic> toJson() => {
    "device_friendly_name": deviceFriendlyName,
    "device_id": deviceId,
    "android_id": androidId,
  };

  OfficerDeviceId copyWith({
    String? deviceFriendlyName,
    String? deviceId,
    String? androidId,
  }) {
    return OfficerDeviceId(
      deviceFriendlyName: deviceFriendlyName ?? this.deviceFriendlyName,
      deviceId: deviceId ?? this.deviceId,
      androidId: androidId ?? this.androidId,
    );
  }
}

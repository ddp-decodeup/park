// To parse this JSON data, do
//
//     final updateOfficerRequestModel = updateOfficerRequestModelFromJson(jsonString);

import 'dart:convert';

UpdateOfficerRequestModel updateOfficerRequestModelFromJson(String str) =>
    UpdateOfficerRequestModel.fromJson(json.decode(str));

String updateOfficerRequestModelToJson(UpdateOfficerRequestModel data) =>
    json.encode(data.toJson());

class UpdateOfficerRequestModel {
  String siteId;
  String siteOfficerId;
  UpdatePackage updatePackage;

  UpdateOfficerRequestModel({
    required this.siteId,
    required this.siteOfficerId,
    required this.updatePackage,
  });

  factory UpdateOfficerRequestModel.fromJson(Map<String, dynamic> json) =>
      UpdateOfficerRequestModel(
        siteId: json["site_id"],
        siteOfficerId: json["site_officer_id"],
        updatePackage: UpdatePackage.fromJson(json["update_package"]),
      );

  Map<String, dynamic> toJson() => {
    "site_id": siteId,
    "site_officer_id": siteOfficerId,
    "update_package": updatePackage.toJson(),
  };
}

class UpdatePackage {
  String officerShift;
  String officerSupervisor;
  String officerSupervisorBadgeId;
  String officerAgency;
  OfficerDeviceId officerDeviceId;

  UpdatePackage({
    required this.officerShift,
    required this.officerSupervisor,
    required this.officerSupervisorBadgeId,
    required this.officerAgency,
    required this.officerDeviceId,
  });

  factory UpdatePackage.fromJson(Map<String, dynamic> json) => UpdatePackage(
    officerShift: json["officer_shift"],
    officerSupervisor: json["officer_supervisor"],
    officerSupervisorBadgeId: json["officer_supervisor_badge_id"],
    officerAgency: json["officer_agency"],
    officerDeviceId: OfficerDeviceId.fromJson(json["officer_device_id"]),
  );

  Map<String, dynamic> toJson() => {
    "officer_shift": officerShift,
    "officer_supervisor": officerSupervisor,
    "officer_supervisor_badge_id": officerSupervisorBadgeId,
    "officer_agency": officerAgency,
    "officer_device_id": officerDeviceId.toJson(),
  };
}

class OfficerDeviceId {
  String deviceFriendlyName;
  String devideId;
  String androidId;

  OfficerDeviceId({
    required this.deviceFriendlyName,
    required this.devideId,
    required this.androidId,
  });

  factory OfficerDeviceId.fromJson(Map<String, dynamic> json) =>
      OfficerDeviceId(
        deviceFriendlyName: json["device_friendly_name"],
        devideId: json["devide_id"],
        androidId: json["android_id"],
      );

  Map<String, dynamic> toJson() => {
    "device_friendly_name": deviceFriendlyName,
    "devide_id": devideId,
    "android_id": androidId,
  };
}

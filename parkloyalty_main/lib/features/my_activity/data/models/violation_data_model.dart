// To parse this JSON data, do
//
//     final violationDataModel = violationDataModelFromJson(jsonString);

import 'dart:convert';

ViolationDataModel violationDataModelFromJson(String str) =>
    ViolationDataModel.fromJson(json.decode(str));

String violationDataModelToJson(ViolationDataModel data) =>
    json.encode(data.toJson());

class ViolationDataModel {
  final List<ViolationData>? data;
  final bool? status;
  final String? message;

  ViolationDataModel({this.data, this.status, this.message});

  factory ViolationDataModel.fromJson(Map<String, dynamic> json) =>
      ViolationDataModel(
        data: json["data"] == null
            ? []
            : List<ViolationData>.from(
                json["data"]!.map((x) => ViolationData.fromJson(x)),
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

class ViolationData {
  final List<Resonse>? resonse;

  ViolationData({this.resonse});

  factory ViolationData.fromJson(Map<String, dynamic> json) => ViolationData(
    resonse: json["resonse"] == null
        ? []
        : List<Resonse>.from(json["resonse"]!.map((x) => Resonse.fromJson(x))),
  );

  Map<String, dynamic> toJson() => {
    "resonse": resonse == null
        ? []
        : List<dynamic>.from(resonse!.map((x) => x.toJson())),
  };
}

class Resonse {
  final String? violationName;
  final int? violationCounts;

  Resonse({this.violationName, this.violationCounts});

  factory Resonse.fromJson(Map<String, dynamic> json) => Resonse(
    violationName: json["violation_name"],
    violationCounts: json["violation_counts"],
  );

  Map<String, dynamic> toJson() => {
    "violation_name": violationName,
    "violation_counts": violationCounts,
  };
}

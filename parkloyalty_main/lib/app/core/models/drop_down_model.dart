// To parse this JSON data, do
//
//     final dropDownModel = dropDownModelFromJson(jsonString);

import 'dart:convert';

DropDownModel dropDownModelFromJson(String str) =>
    DropDownModel.fromJson(json.decode(str));

// String dropDownModelToJson(DropDownModel data) => json.encode(data.toJson());

class DropDownModel {
  List<Datum> data;
  bool status;
  String message;
  static String? fieldName;

  DropDownModel({
    required this.data,
    required this.status,
    required this.message,
  });

  factory DropDownModel.fromJson(Map<String, dynamic> json) => DropDownModel(
    data: List<Datum>.from(json["data"].map((x) => Datum.fromJson(x,fieldName))),
    status: json["status"],
    message: json["message"],
  );

  /*Map<String, dynamic> toJson() => {
    "data": List<dynamic>.from(data.map((x) => x.toJson())),
    "status": status,
    "message": message,
  };*/
}

class Datum {
  bool status;
  List<DataSet> response;
  Metadata metadata;

  Datum({required this.status, required this.response, required this.metadata});

  factory Datum.fromJson(Map<String, dynamic> json, String? fieldName) => Datum(
    status: json["status"],
    response: List<DataSet>.from(
      json["response"].map((x) => DataSet.fromJson(x)),
    ),
    metadata: Metadata.fromJson(json["metadata"]),
  );

  /*Map<String, dynamic> toJson() => {
    "status": status,
    "response": List<dynamic>.from(response.map((x) => x.toJson())),
    "metadata": metadata.toJson(),
  };*/
}

class Metadata {
  String type;
  int totalShards;
  int length;

  Metadata({
    required this.type,
    required this.totalShards,
    required this.length,
  });

  factory Metadata.fromJson(Map<String, dynamic> json) => Metadata(
    type: json["type"],
    totalShards: json["total_shards"],
    length: json["length"],
  );

  Map<String, dynamic> toJson() => {
    "type": type,
    "total_shards": totalShards,
    "length": length,
  };
}

class DataSet {
  String id;
  dynamic label1;
  dynamic label2;
  dynamic label3;

  DataSet({
    required this.id,
    required this.label1,
     this.label2,
    this.label3,
  });

  factory DataSet.fromJson(Map<String, dynamic> json, {String key = 'a'}){
    if(key=='make'){
      key='make_full';
    }
    return DataSet(
          id: json["_id"],
          label1:
          json[key] ??
              json["side_name"] ??
              json["shift_name"] ??
              json["officer_supervisor"] ??
              json["activity"] ??
              json["agency_name"] ??
              json["repr"] ??
              json["friendly_name"] ??
              json["beat_name"] ??
              json["zone_name"] ??
              json["cancel_reason"] ??
              json["description"] ??
              json["state_name"] ??
              json["street"] ??
              json["details"] ??
              json["violation_description"] ??
              json["tier_stem_name"] ??
              json["note"] ??
              json["remark"] ??
              json["regulation"] ??
              json["lot"] ??
              json["void_and_reissue_reason"] ??
              json["block_name"] ??
              json["street_name"] ??
              json["make_full"] ??
              json["model"] ??
              '',
          label2:
          json["side_short"] ??
              json["supervisor_badge_id"] ??
              json["activity_key"] ??
              json["type"] ??
              json["device_id"] ??
              json["zone_metadata"] ??
              json["color_code"] ??
              json["state_abbreviated"] ??
              json["name"] ??
              json["body_style"] ??
              json["violation"] ??
              json["time"] ??
              json["street"] ??
              json["block_value"] ??
              json["make_full"] ??
              '',
          label3: json['amount'].toString(),
        );
  }

  Map<String, dynamic> toJson() => {
    "_id": id,
    "side_short": label1,
    "side_name": label2,
  };
}

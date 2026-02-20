// To parse this JSON data, do
//
//     final timingRecord = timingRecordFromJson(jsonString);

import 'dart:convert';

TimingRecord timingRecordFromJson(String str) =>
    TimingRecord.fromJson(json.decode(str));

String timingRecordToJson(TimingRecord data) => json.encode(data.toJson());

class TimingRecord {
  List<TimingData> data;
  int recordCount;
  bool success;

  TimingRecord({
    required this.data,
    required this.recordCount,
    required this.success,
  });

  TimingRecord copyWith({
    List<TimingData>? data,
    int? recordCount,
    bool? success,
  }) => TimingRecord(
    data: data ?? this.data,
    recordCount: recordCount ?? this.recordCount,
    success: success ?? this.success,
  );

  factory TimingRecord.fromJson(Map<String, dynamic> json) => TimingRecord(
    data: List<TimingData>.from(
      json["data"].map((x) => TimingData.fromJson(x)),
    ),
    recordCount: json["record_count"],
    success: json["success"],
  );

  Map<String, dynamic> toJson() => {
    "data": List<dynamic>.from(data.map((x) => x.toJson())),
    "record_count": recordCount,
    "success": success,
  };
}

class TimingData {
  String citationTicketNumber;
  String address;
  String alertType;
  String arrivalStatus;
  String badgeId;
  String block;
  String color;
  String dataSource;
  int dcDeltaSeconds;
  String dcHit;
  String id;
  List<String> images;
  bool isAbandonVehicle;
  bool isViolation;
  DateTime lastMarkTimestamp;
  Location location;
  String lot;
  String lpNumber;
  String lpState;
  String make;
  DateTime markIssueTimestamp;
  DateTime markStartTimestamp;
  int markTimingType;
  String meterNumber;
  String model;
  String officerName;
  String parentMarkId;
  int regulationTime;
  String regulationTimeValue;
  String remark;
  String remark2;
  String shift;
  String side;
  String siteId;
  String siteOfficerId;
  String street;
  String supervisor;
  int tireStemBack;
  int tireStemFront;
  String user;
  String vendorName;
  String vinNumber;
  String zone;
  double? difference;
  DateTime? observedTime2;

  TimingData({
    required this.citationTicketNumber,
    required this.address,
    required this.alertType,
    required this.arrivalStatus,
    required this.badgeId,
    required this.block,
    required this.color,
    required this.dataSource,
    required this.dcDeltaSeconds,
    required this.dcHit,
    required this.id,
    required this.images,
    required this.isAbandonVehicle,
    required this.isViolation,
    required this.lastMarkTimestamp,
    required this.location,
    required this.lot,
    required this.lpNumber,
    required this.lpState,
    required this.make,
    required this.markIssueTimestamp,
    required this.markStartTimestamp,
    required this.markTimingType,
    required this.meterNumber,
    required this.model,
    required this.officerName,
    required this.parentMarkId,
    required this.regulationTime,
    required this.regulationTimeValue,
    required this.remark,
    required this.remark2,
    required this.shift,
    required this.side,
    required this.siteId,
    required this.siteOfficerId,
    required this.street,
    required this.supervisor,
    required this.tireStemBack,
    required this.tireStemFront,
    required this.user,
    required this.vendorName,
    required this.vinNumber,
    required this.zone,
    this.difference,
    this.observedTime2,
  });

  TimingData copyWith({
    String? citationTicketNumber,
    String? address,
    String? alertType,
    String? arrivalStatus,
    String? badgeId,
    String? block,
    String? color,
    String? dataSource,
    int? dcDeltaSeconds,
    String? dcHit,
    String? id,
    List<String>? images,
    bool? isAbandonVehicle,
    bool? isViolation,
    DateTime? lastMarkTimestamp,
    Location? location,
    String? lot,
    String? lpNumber,
    String? lpState,
    String? make,
    DateTime? markIssueTimestamp,
    DateTime? markStartTimestamp,
    int? markTimingType,
    String? meterNumber,
    String? model,
    String? officerName,
    String? parentMarkId,
    int? regulationTime,
    String? regulationTimeValue,
    String? remark,
    String? remark2,
    String? shift,
    String? side,
    String? siteId,
    String? siteOfficerId,
    String? street,
    String? supervisor,
    int? tireStemBack,
    int? tireStemFront,
    String? user,
    String? vendorName,
    String? vinNumber,
    String? zone,
    double? difference,
    DateTime? observedTime2,
  }) => TimingData(
    citationTicketNumber: citationTicketNumber ?? this.citationTicketNumber,
    address: address ?? this.address,
    alertType: alertType ?? this.alertType,
    arrivalStatus: arrivalStatus ?? this.arrivalStatus,
    badgeId: badgeId ?? this.badgeId,
    block: block ?? this.block,
    color: color ?? this.color,
    dataSource: dataSource ?? this.dataSource,
    dcDeltaSeconds: dcDeltaSeconds ?? this.dcDeltaSeconds,
    dcHit: dcHit ?? this.dcHit,
    id: id ?? this.id,
    images: images ?? this.images,
    isAbandonVehicle: isAbandonVehicle ?? this.isAbandonVehicle,
    isViolation: isViolation ?? this.isViolation,
    lastMarkTimestamp: lastMarkTimestamp ?? this.lastMarkTimestamp,
    location: location ?? this.location,
    lot: lot ?? this.lot,
    lpNumber: lpNumber ?? this.lpNumber,
    lpState: lpState ?? this.lpState,
    make: make ?? this.make,
    markIssueTimestamp: markIssueTimestamp ?? this.markIssueTimestamp,
    markStartTimestamp: markStartTimestamp ?? this.markStartTimestamp,
    markTimingType: markTimingType ?? this.markTimingType,
    meterNumber: meterNumber ?? this.meterNumber,
    model: model ?? this.model,
    officerName: officerName ?? this.officerName,
    parentMarkId: parentMarkId ?? this.parentMarkId,
    regulationTime: regulationTime ?? this.regulationTime,
    regulationTimeValue: regulationTimeValue ?? this.regulationTimeValue,
    remark: remark ?? this.remark,
    remark2: remark2 ?? this.remark2,
    shift: shift ?? this.shift,
    side: side ?? this.side,
    siteId: siteId ?? this.siteId,
    siteOfficerId: siteOfficerId ?? this.siteOfficerId,
    street: street ?? this.street,
    supervisor: supervisor ?? this.supervisor,
    tireStemBack: tireStemBack ?? this.tireStemBack,
    tireStemFront: tireStemFront ?? this.tireStemFront,
    user: user ?? this.user,
    vendorName: vendorName ?? this.vendorName,
    vinNumber: vinNumber ?? this.vinNumber,
    zone: zone ?? this.zone,
    difference: difference ?? this.difference,
    observedTime2: observedTime2 ?? this.observedTime2,
  );

  factory TimingData.fromJson(Map<String, dynamic> json) => TimingData(
    citationTicketNumber: json["CitationTicketNumber"],
    address: json["address"],
    alertType: json["alert_type"],
    arrivalStatus: json["arrival_status"],
    badgeId: json["badge_id"],
    block: json["block"],
    color: json["color"],
    dataSource: json["data_source"],
    dcDeltaSeconds: json["dc_delta_seconds"],
    dcHit: json["dc_hit"],
    id: json["id"],
    images: List<String>.from(json["images"].map((x) => x)),
    isAbandonVehicle: json["is_abandon_vehicle"],
    isViolation: json["is_violation"],
    lastMarkTimestamp: DateTime.parse(json["last_mark_timestamp"]),
    location: Location.fromJson(json["location"]),
    lot: json["lot"],
    lpNumber: json["lp_number"],
    lpState: json["lp_state"],
    make: json["make"],
    markIssueTimestamp: DateTime.parse(json["mark_issue_timestamp"]),
    markStartTimestamp: DateTime.parse(
      json["mark_start_timestamp"].toString().replaceFirst("Z", ""),
    ),
    markTimingType: json["mark_timing_type"],
    meterNumber: json["meter_number"],
    model: json["model"],
    officerName: json["officer_name"],
    parentMarkId: json["parent_mark_id"],
    regulationTime: json["regulation_time"],
    regulationTimeValue: json["regulation_time_value"],
    remark: json["remark"],
    remark2: json["remark_2"],
    shift: json["shift"],
    side: json["side"],
    siteId: json["site_id"],
    siteOfficerId: json["site_officer_id"],
    street: json["street"],
    supervisor: json["supervisor"],
    tireStemBack: json["tire_stem_back"],
    tireStemFront: json["tire_stem_front"],
    user: json["user"],
    vendorName: json["vendor_name"],
    vinNumber: json["vin_number"],
    zone: json["zone"],
    difference: json["difference"]?.toDouble(),
    observedTime2: json["observed_time_2"] == null
        ? null
        : DateTime.parse(json["observed_time_2"]),
  );

  Map<String, dynamic> toJson() => {
    "CitationTicketNumber": citationTicketNumber,
    "address": address,
    "alert_type": alertType,
    "arrival_status": arrivalStatus,
    "badge_id": badgeId,
    "block": block,
    "color": color,
    "data_source": dataSource,
    "dc_delta_seconds": dcDeltaSeconds,
    "dc_hit": dcHit,
    "id": id,
    "images": List<dynamic>.from(images.map((x) => x)),
    "is_abandon_vehicle": isAbandonVehicle,
    "is_violation": isViolation,
    "last_mark_timestamp": lastMarkTimestamp.toIso8601String(),
    "location": location.toJson(),
    "lot": lot,
    "lp_number": lpNumber,
    "lp_state": lpState,
    "make": make,
    "mark_issue_timestamp": markIssueTimestamp.toIso8601String(),
    "mark_start_timestamp": markStartTimestamp.toIso8601String(),
    "mark_timing_type": markTimingType,
    "meter_number": meterNumber,
    "model": model,
    "officer_name": officerName,
    "parent_mark_id": parentMarkId,
    "regulation_time": regulationTime,
    "regulation_time_value": regulationTimeValue,
    "remark": remark,
    "remark_2": remark2,
    "shift": shift,
    "side": side,
    "site_id": siteId,
    "site_officer_id": siteOfficerId,
    "street": street,
    "supervisor": supervisor,
    "tire_stem_back": tireStemBack,
    "tire_stem_front": tireStemFront,
    "user": user,
    "vendor_name": vendorName,
    "vin_number": vinNumber,
    "zone": zone,
    "difference": difference,
    "observed_time_2": observedTime2?.toIso8601String(),
  };
}

class Location {
  List<double> coordinates;
  String type;

  Location({required this.coordinates, required this.type});

  Location copyWith({List<double>? coordinates, String? type}) => Location(
    coordinates: coordinates ?? this.coordinates,
    type: type ?? this.type,
  );

  factory Location.fromJson(Map<String, dynamic> json) => Location(
    coordinates: List<double>.from(
      json["Coordinates"].map((x) => x?.toDouble()),
    ),
    type: json["Type"],
  );

  Map<String, dynamic> toJson() => {
    "Coordinates": List<dynamic>.from(coordinates.map((x) => x)),
    "Type": type,
  };
}

// To parse this JSON data, do
//
//     final dataSetModel = dataSetModelFromJson(jsonString);

import 'dart:convert';

import '../../shared/utils/extensions/json_utils.dart';

DataSetModel dataSetModelFromJson(String str) => DataSetModel.fromJson(json.decode(str));

String dataSetModelToJson(DataSetModel data) => json.encode(data.toJson());

class DataSetModel {
  final List<Datum>? data;
  final bool? status;
  final String? message;

  DataSetModel({this.data, this.status, this.message});

  factory DataSetModel.fromJson(Map<String, dynamic> json) => DataSetModel(
    data: json["data"] == null ? [] : List<Datum>.from(json["data"]!.map((x) => Datum.fromJson(x))),
    status: json["status"],
    message: json["message"],
  );

  Map<String, dynamic> toJson() => {
    "data": data == null ? [] : List<dynamic>.from(data!.map((x) => x.toJson())),
    "status": status,
    "message": message,
  };
}

class Datum {
  final bool? status;
  final List<Response>? response;
  final Metadata? metadata;

  Datum({this.status, this.response, this.metadata});

  factory Datum.fromJson(Map<String, dynamic> json) => Datum(
    status: json["status"],
    response: json["response"] == null ? [] : List<Response>.from(json["response"]!.map((x) => Response.fromJson(x))),
    metadata: json["metadata"] == null ? null : Metadata.fromJson(json["metadata"]),
  );

  Map<String, dynamic> toJson() => {
    "status": status,
    "response": response == null ? [] : List<dynamic>.from(response!.map((x) => x.toJson())),
    "metadata": metadata?.toJson(),
  };
}

class Metadata {
  final String? type;
  final int? totalShards;
  final int? length;

  Metadata({this.type, this.totalShards, this.length});

  factory Metadata.fromJson(Map<String, dynamic> json) =>
      Metadata(type: json["type"], totalShards: json["total_shards"], length: json["length"]);

  Map<String, dynamic> toJson() => {"type": type, "total_shards": totalShards, "length": length};
}

class BeatMetadata {
  final double? beatHq;
  final int? name;

  BeatMetadata({this.beatHq, this.name});

  factory BeatMetadata.fromJson(Map<String, dynamic> json) => BeatMetadata(beatHq: json["beat_hq"], name: json["name"]);

  Map<String, dynamic> toJson() => {"beat_hq": beatHq, "name": name};
}

class SettingMetadata {
  final String? timezoneDelta;
  final String? timezoneDifference;
  final String? timezoneName;

  SettingMetadata({this.timezoneDelta, this.timezoneDifference, this.timezoneName});

  factory SettingMetadata.fromJson(Map<String, dynamic> json) => SettingMetadata(
    timezoneDelta: (json["timezone_delta"]as Object?).asString(),
    timezoneDifference: (json["timezone_difference"]as Object?).asString(),
    timezoneName: (json["timezone_name"]as Object?).asString(),
  );

  Map<String, dynamic> toJson() => {
    "timezone_delta": timezoneDelta,
    "timezone_difference": timezoneDifference,
    "timezone_name": timezoneName,
  };
}

class SupervisorMetadata {
  final String? zoneId;

  SupervisorMetadata({this.zoneId});

  factory SupervisorMetadata.fromJson(Map<String, dynamic> json) => SupervisorMetadata(zoneId: json["zone_id"]);

  Map<String, dynamic> toJson() => {"zone_id": zoneId};
}

class Location {
  final String? type;
  final List<double>? coordinates;
  final String? name;

  const Location({this.type, this.coordinates, this.name});

  factory Location.fromJson(Map<String, dynamic>? json) {
    if (json == null) return const Location();

    return Location(
      type: json['type'],
      coordinates: (json['coordinates'] as List?)?.map((e) => (e as num).toDouble()).toList(),
      name: json['name'],
    );
  }

  Map<String, dynamic> toJson() {
    return {'type': type, 'coordinates': coordinates, 'name': name};
  }
}

class Response {
  final String? id;
  final String? name;
  final String? year;
  final String? sideShort;
  final String? sideName;
  final bool? enable;
  final String? siteId;
  final String? deviceId;
  final String? friendlyName;
  final String? beatName;
  final String? shiftMetadata;
  final String? shiftName;
  final String? shiftStart;
  final String? shiftEnd;
  final String? colorCode;
  final String? description;
  final String? amount;
  final String? code;
  final String? due15Days;
  final String? due30Days;
  final String? due45Days;
  final String? cost;
  final String? parkingFee;
  final String? citationFee;
  final String? totalDueNow;
  final String? justInTimeCheck;
  final String? lateFine;
  final String? meterlistReq;
  final String? permitVio;
  final String? scofflawVio;
  final String? timelimitVio;
  final String? violation;
  final String? violationDescription;
  final String? zoneId;
  final String? remark;
  final String? streetName;
  final String? streetLookupCode;
  final String? violationReferenceCode;
  final String? violationQueryType;
  final String? regulation;
  final String? time;
  final String? regex;
  final String? repr;
  final String? type;
  final String? value;
  final String? note;
  final String? activity;
  final String? activityKey;
  final String? zoneMetadata;
  final String? zoneName;
  final String? supervisorBadgeId;
  final String? supervisorName;
  final String? officerSupervisor;
  final SupervisorMetadata? supervisorMetadata;
  final BeatMetadata? beatMetadata;
  final SettingMetadata? settingMetadata;
  final String? androidId;
  final String? license;
  final String? deviceFriendlyName;
  final String? equipmentName;
  final String? equipmentType;
  final String? equipmentId;
  final String? squadName;
  final String? abbrev;
  final String? radioName;
  final String? tierStem;
  final String? make;
  final String? model;
  final String? modelLookupCode;
  final String? stateAbbreviated;
  final String? stateName;
  final String? street;
  final String? bodyStyle;
  final String? bodyStyleLookupCode;
  final String? lastName;
  final String? firstName;
  final String? agencyName;
  final String? cancelReason;
  final String? block;
  final String? blockName;
  final String? direction;
  final String? lot;
  final String? lotBranchId;
  final String? lotLookupCode;
  final String? lotZone;
  final String? voidReasonLookupCode;
  final String? details;
  final String? spaceName;
  final String? pbcReq;
  final String? violationLateFee;
  final String? escalated2;
  final String? escalated3;
  final String? escalated4;
  final String? escalated5;
  final String? pbcZone;
  final String? exportCode;
  final String? amountDays;
  final String? lateFineDays;
  final String? due15DateDays;
  final String? due30DateDays;
  final bool? lock;
  final String? markTime;
  final String? zoneMandatory;
  final String? warningViolation;
  final String? cityName;
  final String? sanctionsSticker;
  final String? isVisible;
  final String? flatFineByLot;
  final String? vioTypeCode;
  final String? vioTypeDescription;
  final String? vioType;
  final String? holiday;
  final String? date;
  final String? day;
  final String? makeFull;
  final String? unnamed3;
  final String? unnamed4;
  final String? unnamed5;
  final String? lpState;
  final String? color;
  final String? address;
  final String? lpNumber;
  final String? vinNumber;
  final String? meterNumber;
  final String? regulationTime;
  final String? regulationTimeValue;
  final List<String>? images;
  final String? side;
  final String? zone;
  final String? arrivalStatus;
  final Location? location;
  final String? source;
  final String? remark2;
  final String? markTimingType;
  final String? officerName;
  final String? badgeId;
  final String? shift;
  final String? space;
  final String? supervisor;
  final bool? isViolation;
  final String? tireStemFront;
  final String? tireStemBack;
  final String? siteOfficerId;
  final String? markStartTimestamp;
  final String? markIssueTimestamp;
  final String? createdAt;
  final String? citationTicketNumber;
  final String? lastMarkTimestamp;
  final bool? isAbandonVehicle;
  final String? parentMarkId;
  final String? escalation1Days;
  final String? escalation1Fee;
  final String? escalation2Days;
  final String? escalation2Fee;
  final String? escalation3Days;
  final String? escalation3Fee;
  final String? adminFee;

  Response({
    this.id,
    this.name,
    this.year,
    this.sideShort,
    this.sideName,
    this.enable,
    this.siteId,
    this.deviceId,
    this.friendlyName,
    this.beatName,
    this.shiftMetadata,
    this.shiftName,
    this.shiftStart,
    this.shiftEnd,
    this.colorCode,
    this.description,
    this.amount,
    this.code,
    this.due15Days,
    this.due30Days,
    this.due45Days,
    this.cost,
    this.parkingFee,
    this.citationFee,
    this.totalDueNow,
    this.justInTimeCheck,
    this.lateFine,
    this.meterlistReq,
    this.permitVio,
    this.scofflawVio,
    this.timelimitVio,
    this.violation,
    this.violationDescription,
    this.zoneId,
    this.remark,
    this.streetName,
    this.streetLookupCode,
    this.violationReferenceCode,
    this.violationQueryType,
    this.regulation,
    this.time,
    this.regex,
    this.repr,
    this.type,
    this.value,
    this.note,
    this.activity,
    this.activityKey,
    this.zoneMetadata,
    this.zoneName,
    this.supervisorBadgeId,
    this.supervisorName,
    this.officerSupervisor,
    this.supervisorMetadata,
    this.beatMetadata,
    this.settingMetadata,
    this.androidId,
    this.license,
    this.deviceFriendlyName,
    this.equipmentName,
    this.equipmentType,
    this.equipmentId,
    this.squadName,
    this.abbrev,
    this.radioName,
    this.tierStem,
    this.make,
    this.model,
    this.modelLookupCode,
    this.stateAbbreviated,
    this.stateName,
    this.street,
    this.bodyStyle,
    this.bodyStyleLookupCode,
    this.lastName,
    this.firstName,
    this.agencyName,
    this.cancelReason,
    this.block,
    this.blockName,
    this.direction,
    this.location,
    this.lot,
    this.lotBranchId,
    this.lotLookupCode,
    this.lotZone,
    this.voidReasonLookupCode,
    this.details,
    this.spaceName,
    this.pbcReq,
    this.violationLateFee,
    this.escalated2,
    this.escalated3,
    this.escalated4,
    this.escalated5,
    this.pbcZone,
    this.exportCode,
    this.amountDays,
    this.lateFineDays,
    this.due15DateDays,
    this.due30DateDays,
    this.lock,
    this.markTime,
    this.zoneMandatory,
    this.warningViolation,
    this.cityName,
    this.sanctionsSticker,
    this.isVisible,
    this.flatFineByLot,
    this.vioTypeCode,
    this.vioTypeDescription,
    this.vioType,
    this.holiday,
    this.date,
    this.day,
    this.makeFull,
    this.unnamed3,
    this.unnamed4,
    this.unnamed5,
    this.lpState,
    this.color,
    this.address,
    this.lpNumber,
    this.vinNumber,
    this.meterNumber,
    this.regulationTime,
    this.regulationTimeValue,
    this.images,
    this.side,
    this.zone,
    this.arrivalStatus,
    this.source,
    this.remark2,
    this.markTimingType,
    this.officerName,
    this.badgeId,
    this.shift,
    this.space,
    this.supervisor,
    this.isViolation,
    this.tireStemFront,
    this.tireStemBack,
    this.siteOfficerId,
    this.markStartTimestamp,
    this.markIssueTimestamp,
    this.createdAt,
    this.citationTicketNumber,
    this.lastMarkTimestamp,
    this.isAbandonVehicle,
    this.parentMarkId,
    this.escalation1Days,
    this.escalation1Fee,
    this.escalation2Days,
    this.escalation2Fee,
    this.escalation3Days,
    this.escalation3Fee, this.adminFee,
  });

  factory Response.fromJson(Map<String, dynamic> json) => Response(
    id: (json["_id"] as Object? ).asString(),
    name: (json["name"] as Object?).asString(),
    year: (json["year"] as Object?).asString(),
    sideShort: (json["side_short"]as Object?).asString(),
    sideName: (json["side_name"]as Object?).asString(),
    enable: (json["enable"]as Object?).asBool(),
    siteId: (json["site_id"]as Object?).asString(),
    deviceId: (json["device_id"]as Object?).asString(),
    friendlyName: (json["friendly_name"]as Object?).asString(),
    beatName: (json["beat_name"]as Object?).asString(),
    shiftMetadata: (json["shift_metadata"]as Object?).asString(),
    shiftName: (json["shift_name"]as Object?).asString(),
    shiftStart: (json["shift_start"]as Object?).asString(),
    shiftEnd: (json["shift_end"]as Object?).asString(),
    colorCode: (json["color_code"]as Object?).asString(),
    description: (json["description"]as Object?).asString(),
    amount: (json["amount"] as Object?).asString(),
    code: (json["code"]as Object?).asString(),
    due15Days: (json["due_15_days"] as Object?).asString(),
    due30Days: (json["due_30_days"] as Object?).asString(),
    due45Days: (json["due_45_days"] as Object?).asString(),
    cost: (json["cost"]as Object?).asString(),
    parkingFee: (json["parking_fee"]as Object?).asString(),
    citationFee: (json["citation_fee"]as Object?).asString(),
    totalDueNow: (json["total_due_now"]as Object?).asString(),
    justInTimeCheck:( json["just_in_time_check"]as Object?).asString(),
    lateFine: (json["late_fine"] as Object?).asString(),
    meterlistReq: (json["meterlist_req"]as Object?).asString(),
    permitVio: (json["permit_vio"]as Object?).asString(),
    scofflawVio: (json["scofflaw_vio"]as Object?).asString(),
    timelimitVio: (json["timelimit_vio"]as Object?).asString(),
    violation: (json["violation"]as Object?).asString(),
    violationDescription:( json["violation_description"]as Object?).asString(),
    zoneId: (json["zone_id"]as Object?).asString(),
    remark: (json["remark"]as Object?).asString(),
    streetName: ((json["street_name"] ?? json["street"]) as Object?).asString(),
    streetLookupCode: (json["street_lookup_code"]as Object?).asString(),
    violationReferenceCode: (json["violation_reference_code"]as Object?).asString(),
    violationQueryType: (json["violation_query_type"]as Object?).asString(),
    regulation: (json["regulation"]as Object?).asString(),
    time: (json["time"] as Object?).asString(),
    regex: (json['regex']as Object?).asString(),
    repr: (json['repr']as Object?).asString(),
    type: (json['type']as Object?).asString(),
    value: (json['value']as Object?).asString(),
    note: (json['note']as Object?).asString(),
    activity: (json['activity']as Object?).asString(),
    activityKey: (json['activity_key']as Object?).asString(),
    zoneMetadata: (json['zone_metadata'] as Object?).asString(),
    zoneName: (json['zone_name']as Object?).asString(),
    supervisorBadgeId: (json['supervisor_badge_id']as Object?).asString(),
    supervisorName: (json['supervisor_name']as Object?).asString(),
    officerSupervisor: (json['officer_supervisor']as Object?).asString(),
    supervisorMetadata: json['supervisor_metadata'] == null
        ? null
        : SupervisorMetadata.fromJson(json['supervisor_metadata']),
    beatMetadata: json['beat_metadata'] == null ? null : BeatMetadata.fromJson(json['beat_metadata']),
    settingMetadata: json['metadata'] == null ? null : SettingMetadata.fromJson(json['metadata']),
    androidId:( json['android_id']as Object?).asString(),
    license: (json['license']as Object?).asString(),
    deviceFriendlyName: (json['device_friendly_name']as Object?).asString(),
    equipmentName: (json['equipment_name']as Object?).asString(),
    equipmentType: (json['equipment_type']as Object?).asString(),
    equipmentId: (json['equipment_id']as Object?).asString(),
    squadName: (json['squad_name']as Object?).asString(),
    abbrev: (json['abbrev']as Object?).asString(),
    radioName: (json['radio_name']as Object?).asString(),
    tierStem: (json['tier_stem_name']as Object?).asString(),
    make: (json['make']as Object?).asString(),
    model: (json['model']as Object?).asString(),
    modelLookupCode: (json['model_lookup_code']as Object?).asString(),
    stateAbbreviated: (json['state_abbreviated']as Object?).asString(),
    stateName: (json['state_name']as Object?).asString(),
    street: (json['street']as Object?).asString(),
    bodyStyle: (json['body_style']as Object?).asString(),
    bodyStyleLookupCode: (json['body_style_lookup_code']as Object?).asString(),
    lastName: (json['last_name']as Object?).asString(),
    firstName: (json['first_name']as Object?).asString(),
    agencyName: (json['agency_name']as Object?).asString(),
    cancelReason: (json['cancel_reason']as Object?).asString(),
    block: (json['block'] as Object?).asString(),
    blockName: (json['block_name'] as Object?).asString(),
    direction: (json['direction']as Object?).asString(),
    location: json['location'] is Map<String, dynamic> || json["location"] is String
        ? Location.fromJson(json['location'] is String ? {"name": json["location"]} : json['location'])
        : null,
    lot: (json['lot']as Object?).asString(),
    lotBranchId: (json['branch_lotid']as Object?).asString(),
    lotLookupCode: (json['lot_lookup_code']as Object?).asString(),
    lotZone: ((json['zone'] ?? json['zone_name'])as Object?).asString(),
    voidReasonLookupCode: (json['void_reason_lookup_code']as Object?).asString(),
    details: (json['details']as Object?).asString(),
    spaceName: (json['space_name']as Object?).asString(),
    pbcReq: (json['PBC_req']as Object?).asString(),
    violationLateFee: (json['violationLateFee'] as Object?).asString(),
    escalated2: (json['escalated2']as Object?).asString(),
    escalated3: (json['escalated3']as Object?).asString(),
    escalated4: (json['escalated4']as Object?).asString(),
    escalated5: (json['escalated5']as Object?).asString(),
    pbcZone: (json['pbc_zone']as Object?).asString(),
    exportCode: (json['export_code']as Object?).asString(),
    amountDays: (json['amount_days']as Object?).asString(),
    lateFineDays: (json['late_fine_days']as Object?).asString(),
    due15DateDays: (json['due_15_date_days']as Object?).asString(),
    due30DateDays: (json['due_30_date_days']as Object?).asString(),
    lock:( json['lock']as Object?).asBool(),
    markTime:( json['mark_time']as Object?).asString(),
    zoneMandatory:( json['zone_mandatory']as Object?).asString(),
    warningViolation:( json['warning_violation']as Object?).asString(),
    cityName:( json['city_name']as Object?).asString(),
    sanctionsSticker:( json['sanctions_sticker']as Object?).asString(),
    isVisible:( json['Is_visible']as Object?).asString(),
    flatFineByLot:( json['flat_fine_by_lot']as Object?).asString(),
    vioTypeCode:( json['vio_type_code']as Object?).asString(),
    vioTypeDescription:( json['vio_type_description']as Object?).asString(),
    vioType:( json['vio_type']as Object?).asString(),
    holiday:( json['holiday']as Object?).asString(),
    date:( json['date']as Object?).asString(),
    day:( json['day']as Object?).asString(),
    makeFull:( json['make_full']as Object?).asString(),
    unnamed3:( json['Unnamed: 3']as Object?).asString(),
    unnamed4:( json['Unnamed: 4']as Object?).asString(),
    unnamed5:( json['Unnamed: 5']as Object?).asString(),
    lpState:( json['lp_state']as Object?).asString(),
    color:( json['color']as Object?).asString(),
    address:( json['address']as Object?).asString(),
    lpNumber:( json['lp_number']as Object?).asString(),
    vinNumber:( json['vin_number']as Object?).asString(),
    meterNumber:( json['meter_number']as Object?).asString(),
    regulationTime:( json['regulation_time']as Object?).asString(),
    regulationTimeValue:( json['regulation_time_value']as Object?).asString(),
    images: (json['images'] as List?)?.map((e) => e.toString()).toList(),
    side:( json['side']as Object?).asString(),
    zone:( json['zone']as Object?).asString(),
    arrivalStatus:( json['arrival_status']as Object?).asString(),
    source:( json['source']as Object?).asString(),
    remark2:( json['remark_2']as Object?).asString(),
    markTimingType: (json['mark_timing_type'] as Object?).asString(),
    officerName:( json['officer_name']as Object?).asString(),
    badgeId:( json['badge_id']as Object?).asString(),
    shift:( json['shift']as Object?).asString(),
    space:( json['space']as Object?).asString(),
    supervisor:( json['supervisor']as Object?).asString(),
    isViolation:( json['is_violation']as Object?).asBool(),
    tireStemFront: (json['tire_stem_front'] as Object?).asString(),
    tireStemBack: (json['tire_stem_back'] as Object?).asString(),
    siteOfficerId: (json['site_officer_id'] as Object?).asString(),
    markStartTimestamp: (json['mark_start_timestamp'] as Object?).asString(),
    markIssueTimestamp: (json['mark_issue_timestamp'] as Object?).asString(),
    createdAt: (json['created_at'] as Object?).asString(),
    citationTicketNumber: (json['citation_ticket_number'] as Object?).asString(),
    lastMarkTimestamp: (json['last_mark_timestamp'] as Object?).asString(),
    isAbandonVehicle:( json['is_abandon_vehicle']as Object?).asBool(),
    parentMarkId: (json['parent_mark_id'] as Object?).asString(),
    escalation1Days:( json["escalation_1_days"]as Object?).asString(),
    escalation1Fee:( json["escalation_1_fee"]as Object?).asString(),
    escalation2Days:( json["escalation_2_days"]as Object?).asString(),
    escalation2Fee:( json["escalation_2_fee"]as Object?).asString(),
    escalation3Days:( json["escalation_3_days"]as Object?).asString(),
    escalation3Fee:( json["escalation_3_fee"]as Object?).asString(),
    adminFee:( json["admin_fee"]as Object?).asString(),
  );

  Map<String, dynamic> toJson() => {
    "_id": id,
    "name": name,
    "year": year,
    "side_short": sideShort,
    "side_name": sideName,
    "enable": enable,
    "site_id": siteId,
    "device_id": deviceId,
    "friendly_name": friendlyName,
    "beat_name": beatName,
    "shift_metadata": shiftMetadata,
    "shift_name": shiftName,
    "shift_start": shiftStart,
    "shift_end": shiftEnd,
    "color_code": colorCode,
    "description": description,
    "amount": amount,
    "code": code,
    "due_15_days": due15Days,
    "due_30_days": due30Days,
    "due_45_days": due45Days,
    "cost": cost,
    "parking_fee": parkingFee,
    "citation_fee": citationFee,
    "total_due_now": totalDueNow,
    "just_in_time_check": justInTimeCheck,
    "late_fine": lateFine,
    "meterlist_req": meterlistReq,
    "permit_vio": permitVio,
    "scofflaw_vio": scofflawVio,
    "timelimit_vio": timelimitVio,
    "violation": violation,
    "violation_description": violationDescription,
    "zone_id": zoneId,
    "remark": remark,
    "street_name": streetName,
    "street_lookup_code": streetLookupCode,
    "violation_reference_code": violationReferenceCode,
    "violation_query_type": violationQueryType,
    "regulation": regulation,
    "time": time,
    "regex": regex,
    "repr": repr,
    "type": type,
    "value": value,
    "note": note,
    "activity": activity,
    "activity_key": activityKey,
    "zone_metadata": zoneMetadata,
    "zone_name": zoneName,
    "supervisor_badge_id": supervisorBadgeId,
    "supervisor_name": supervisorName,
    "officer_supervisor": officerSupervisor,
    "supervisor_metadata": supervisorMetadata?.toJson(),
    "beat_metadata": beatMetadata?.toJson(),
    "metadata": settingMetadata?.toJson(),
    "android_id": androidId,
    "license": license,
    "device_friendly_name": deviceFriendlyName,
    "equipment_name": equipmentName,
    "equipment_type": equipmentType,
    "equipment_id": equipmentId,
    "squad_name": squadName,
    "abbrev": abbrev,
    "radio_name": radioName,
    "tier_stem_name": tierStem,
    "make": make,
    "model": model,
    "model_lookup_code": modelLookupCode,
    "state_abbreviated": stateAbbreviated,
    "state_name": stateName,
    "street": street,
    "body_style": bodyStyle,
    "body_style_lookup_code": bodyStyleLookupCode,
    "last_name": lastName,
    "first_name": firstName,
    "agency_name": agencyName,
    "cancel_reason": cancelReason,
    "block": block,
    "block_name": blockName,
    "direction": direction,
    "location": location?.toJson(),
    "lot": lot,
    "branch_lotid": lotBranchId,
    "lot_lookup_code": lotLookupCode,
    "zone": lotZone,
    "void_reason_lookup_code": voidReasonLookupCode,
    "details": details,
    "space_name": spaceName,
    "PBC_req": pbcReq,
    "violationLateFee": violationLateFee,
    "escalated2": escalated2,
    "escalated3": escalated3,
    "escalated4": escalated4,
    "escalated5": escalated5,
    "pbc_zone": pbcZone,
    "export_code": exportCode,
    "amount_days": amountDays,
    "late_fine_days": lateFineDays,
    "due_15_date_days": due15DateDays,
    "due_30_date_days": due30DateDays,
    "lock": lock,
    "mark_time": markTime,
    "zone_mandatory": zoneMandatory,
    "warning_violation": warningViolation,
    "city_name": cityName,
    "sanctions_sticker": sanctionsSticker,
    "Is_visible": isVisible,
    "flat_fine_by_lot": flatFineByLot,
    "vio_type_code": vioTypeCode,
    "vio_type_description": vioTypeDescription,
    "vio_type": vioType,
    "holiday": holiday,
    "date": date,
    "day": day,
    "make_full": makeFull,
    "Unnamed: 3": unnamed3,
    "Unnamed: 4": unnamed4,
    "Unnamed: 5": unnamed5,
    "lp_state": lpState,
    "color": color,
    "address": address,
    "lp_number": lpNumber,
    "vin_number": vinNumber,
    "meter_number": meterNumber,
    "regulation_time": regulationTime,
    "regulation_time_value": regulationTimeValue,
    "images": images,
    "side": side,
    "arrival_status": arrivalStatus,
    "source": source,
    "remark_2": remark2,
    "mark_timing_type": markTimingType,
    "officer_name": officerName,
    "badge_id": badgeId,
    "shift": shift,
    "space": space,
    "supervisor": supervisor,
    "is_violation": isViolation,
    "tire_stem_front": tireStemFront,
    "tire_stem_back": tireStemBack,
    "site_officer_id": siteOfficerId,
    "mark_start_timestamp": markStartTimestamp,
    "mark_issue_timestamp": markIssueTimestamp,
    "created_at": createdAt,
    "citation_ticket_number": citationTicketNumber,
    "last_mark_timestamp": lastMarkTimestamp,
    "is_abandon_vehicle": isAbandonVehicle,
    "parent_mark_id": parentMarkId,
    "escalation_1_days": escalation1Days,
    "escalation_1_fee": escalation1Fee,
    "escalation_2_days": escalation2Days,
    "escalation_2_fee": escalation2Fee,
    "escalation_3_days": escalation3Days,
    "escalation_3_fee": escalation3Fee,
    "admin_fee":adminFee,
    "due_31_days": due30Days,
  };
}

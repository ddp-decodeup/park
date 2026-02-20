// To parse this JSON data, do
//
//     final dailySummaryModel = dailySummaryModelFromJson(jsonString);

import 'dart:convert';

DailySummaryModel dailySummaryModelFromJson(String str) =>
    DailySummaryModel.fromJson(json.decode(str));

String dailySummaryModelToJson(DailySummaryModel data) =>
    json.encode(data.toJson());

class DailySummaryModel {
  final Data? data;
  final bool? status;
  final String? message;

  DailySummaryModel({this.data, this.status, this.message});

  factory DailySummaryModel.fromJson(Map<String, dynamic> json) =>
      DailySummaryModel(
        data: json["data"] == null ? null : Data.fromJson(json["data"]),
        status: json["status"],
        message: json["message"],
      );

  Map<String, dynamic> toJson() => {
    "data": data?.toJson(),
    "status": status,
    "message": message,
  };
}

class Data {
  final OfficerDailySummary? officerDailySummary;

  Data({this.officerDailySummary});

  factory Data.fromJson(Map<String, dynamic> json) => Data(
    officerDailySummary: json["officer_daily_summary"] == null
        ? null
        : OfficerDailySummary.fromJson(json["officer_daily_summary"]),
  );

  Map<String, dynamic> toJson() => {
    "officer_daily_summary": officerDailySummary?.toJson(),
  };
}

class OfficerDailySummary {
  final TimeseriesInfo? timeseriesInfo;
  final OfficerDetails? officerDetails;
  final List<Shift>? shifts;

  OfficerDailySummary({this.timeseriesInfo, this.officerDetails, this.shifts});

  factory OfficerDailySummary.fromJson(Map<String, dynamic> json) =>
      OfficerDailySummary(
        timeseriesInfo: json["timeseries_info"] == null
            ? null
            : TimeseriesInfo.fromJson(json["timeseries_info"]),
        officerDetails: json["officer_details"] == null
            ? null
            : OfficerDetails.fromJson(json["officer_details"]),
        shifts: json["shifts"] == null
            ? []
            : List<Shift>.from(json["shifts"]!.map((x) => Shift.fromJson(x))),
      );

  Map<String, dynamic> toJson() => {
    "timeseries_info": timeseriesInfo?.toJson(),
    "officer_details": officerDetails?.toJson(),
    "shifts": shifts == null
        ? []
        : List<dynamic>.from(shifts!.map((x) => x.toJson())),
  };
}

class OfficerDetails {
  final String? username;
  final int? badgeId;
  final String? beat;
  final String? squad;
  final String? supervisor;
  final String? deviceName;
  final String? printer;
  final String? radio;

  OfficerDetails({
    this.username,
    this.badgeId,
    this.beat,
    this.squad,
    this.supervisor,
    this.deviceName,
    this.printer,
    this.radio,
  });

  factory OfficerDetails.fromJson(Map<String, dynamic> json) => OfficerDetails(
    username: json["username"],
    badgeId: json["badge_id"],
    beat: json["beat"],
    squad: json["squad"],
    supervisor: json["supervisor"],
    deviceName: json["device_name"],
    printer: json["printer"],
    radio: json["radio"],
  );

  Map<String, dynamic> toJson() => {
    "username": username,
    "badge_id": badgeId,
    "beat": beat,
    "squad": squad,
    "supervisor": supervisor,
    "device_name": deviceName,
    "printer": printer,
    "radio": radio,
  };
}

class Shift {
  final String? shift;
  final ShiftDetails? shiftDetails;
  final ScanMetrics? scanMetrics;
  final IssuanceMetrics? issuanceMetrics;

  Shift({
    this.shift,
    this.shiftDetails,
    this.scanMetrics,
    this.issuanceMetrics,
  });

  factory Shift.fromJson(Map<String, dynamic> json) => Shift(
    shift: json["shift"],
    shiftDetails: json["shift_details"] == null
        ? null
        : ShiftDetails.fromJson(json["shift_details"]),
    scanMetrics: json["scan_metrics"] == null
        ? null
        : ScanMetrics.fromJson(json["scan_metrics"]),
    issuanceMetrics: json["issuance_metrics"] == null
        ? null
        : IssuanceMetrics.fromJson(json["issuance_metrics"]),
  );

  Map<String, dynamic> toJson() => {
    "shift": shift,
    "shift_details": shiftDetails?.toJson(),
    "scan_metrics": scanMetrics?.toJson(),
    "issuance_metrics": issuanceMetrics?.toJson(),
  };
}

class IssuanceMetrics {
  final DateTime? firstIssuanceTimestamp;
  final DateTime? lastIssuanceTimestamp;
  final int? issuanceTotal;
  final int? issuanceValid;
  final int? cancel;
  final int? totalCancel;
  final int? issuanceRescind;
  final int? issuanceReissue;
  final int? pbcCancelCount;
  final int? driveOffCount;
  final int? tvrCount;
  final int? reissueCount;

  IssuanceMetrics({
    this.firstIssuanceTimestamp,
    this.lastIssuanceTimestamp,
    this.issuanceTotal,
    this.issuanceValid,
    this.cancel,
    this.totalCancel,
    this.issuanceRescind,
    this.issuanceReissue,
    this.pbcCancelCount,
    this.driveOffCount,
    this.tvrCount,
    this.reissueCount,
  });

  factory IssuanceMetrics.fromJson(Map<String, dynamic> json) =>
      IssuanceMetrics(
        firstIssuanceTimestamp:
            DateTime.tryParse(json["first_issuance_timestamp"]) ?? null,
        lastIssuanceTimestamp:
            DateTime.tryParse(json["last_issuance_timestamp"]) ?? null,
        issuanceTotal: json["issuance_total"],
        issuanceValid: json["issuance_valid"],
        cancel: json["cancel"],
        totalCancel: json["total_cancel"],
        issuanceRescind: json["issuance_rescind"],
        issuanceReissue: json["issuance_reissue"],
        pbcCancelCount: json["pbc_cancel_count"],
        driveOffCount: json["drive_off_count"],
        tvrCount: json["tvr_count"],
        reissueCount: json["reissue_count"],
      );

  Map<String, dynamic> toJson() => {
    "first_issuance_timestamp": firstIssuanceTimestamp,
    "last_issuance_timestamp": lastIssuanceTimestamp,
    "issuance_total": issuanceTotal,
    "issuance_valid": issuanceValid,
    "cancel": cancel,
    "total_cancel": totalCancel,
    "issuance_rescind": issuanceRescind,
    "issuance_reissue": issuanceReissue,
    "pbc_cancel_count": pbcCancelCount,
    "drive_off_count": driveOffCount,
    "tvr_count": tvrCount,
    "reissue_count": reissueCount,
  };
}

class ScanMetrics {
  final DateTime? firstScanTimestamp;
  final DateTime? lastScanTimestamp;
  final int? scanPaymentHit;
  final int? scanScofflawHit;
  final int? scanPermitHit;
  final int? scanTimingHit;
  final int? scanTotalHits;

  ScanMetrics({
    this.firstScanTimestamp,
    this.lastScanTimestamp,
    this.scanPaymentHit,
    this.scanScofflawHit,
    this.scanPermitHit,
    this.scanTimingHit,
    this.scanTotalHits,
  });

  factory ScanMetrics.fromJson(Map<String, dynamic> json) => ScanMetrics(
    firstScanTimestamp:  DateTime.tryParse(json["first_scan_timestamp"])??null,
    lastScanTimestamp: DateTime.tryParse(json["last_scan_timestamp"])??null,
    scanPaymentHit: json["scan_payment_hit"],
    scanScofflawHit: json["scan_scofflaw_hit"],
    scanPermitHit: json["scan_permit_hit"],
    scanTimingHit: json["scan_timing_hit"],
    scanTotalHits: json["scan_total_hits"],
  );

  Map<String, dynamic> toJson() => {
    "first_scan_timestamp": firstScanTimestamp?.toIso8601String(),
    "last_scan_timestamp": lastScanTimestamp?.toIso8601String(),
    "scan_payment_hit": scanPaymentHit,
    "scan_scofflaw_hit": scanScofflawHit,
    "scan_permit_hit": scanPermitHit,
    "scan_timing_hit": scanTimingHit,
    "scan_total_hits": scanTotalHits,
  };
}

class ShiftDetails {
  final DateTime? loginTimestamp;
  final DateTime? logoutTimestamp;
  final DateTime? lunchTimestamp;
  final DateTime? break1Timestamp;
  final DateTime? break2Timestamp;

  ShiftDetails({
    this.loginTimestamp,
    this.logoutTimestamp,
    this.lunchTimestamp,
    this.break1Timestamp,
    this.break2Timestamp,
  });

  factory ShiftDetails.fromJson(Map<String, dynamic> json) => ShiftDetails(
    loginTimestamp: DateTime.tryParse(json["login_timestamp"]) ?? null,
    logoutTimestamp: DateTime.tryParse(json["logout_timestamp"]) ?? null,
    lunchTimestamp: DateTime.tryParse(json["lunch_timestamp"]) ?? null,
    break1Timestamp: DateTime.tryParse(json["break1_timestamp"]),
    break2Timestamp: DateTime.tryParse(json["break2_timestamp"]),
  );

  Map<String, dynamic> toJson() => {
    "login_timestamp": loginTimestamp?.toIso8601String(),
    "logout_timestamp": logoutTimestamp?.toIso8601String(),
    "lunch_timestamp": lunchTimestamp,
    "break1_timestamp": break1Timestamp,
    "break2_timestamp": break2Timestamp,
  };
}

class TimeseriesInfo {
  final DateTime? startTimestamp;
  final DateTime? endTimestamp;

  TimeseriesInfo({this.startTimestamp, this.endTimestamp});

  factory TimeseriesInfo.fromJson(Map<String, dynamic> json) => TimeseriesInfo(
    startTimestamp: json["start_timestamp"] == null
        ? null
        : DateTime.parse(json["start_timestamp"]),
    endTimestamp: json["end_timestamp"] == null
        ? null
        : DateTime.parse(json["end_timestamp"]),
  );

  Map<String, dynamic> toJson() => {
    "start_timestamp": startTimestamp?.toIso8601String(),
    "end_timestamp": endTimestamp?.toIso8601String(),
  };
}

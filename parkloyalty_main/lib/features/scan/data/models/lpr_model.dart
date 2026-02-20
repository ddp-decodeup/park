// To parse this JSON data, do
//
//     final lprModel = lprModelFromJson(jsonString);

import 'dart:convert';

LprModel lprModelFromJson(String str) => LprModel.fromJson(json.decode(str));

String lprModelToJson(LprModel data) => json.encode(data.toJson());

class LprModel {
  List<LPRData> data;
  bool status;
  String message;

  LprModel({required this.data, required this.status, required this.message});

  LprModel copyWith({List<LPRData>? data, bool? status, String? message}) =>
      LprModel(
        data: data ?? this.data,
        status: status ?? this.status,
        message: message ?? this.message,
      );

  factory LprModel.fromJson(Map<String, dynamic> json) => LprModel(
    data: List<LPRData>.from(json["data"].map((x) => LPRData.fromJson(x))),
    status: json["status"],
    message: json["message"],
  );

  Map<String, dynamic> toJson() => {
    "data": List<dynamic>.from(data.map((x) => x.toJson())),
    "status": status,
    "message": message,
  };
}

class LPRData {
  bool status;
  Response response;
  String metadata;

  LPRData({
    required this.status,
    required this.response,
    required this.metadata,
  });

  LPRData copyWith({bool? status, Response? response, String? metadata}) =>
      LPRData(
        status: status ?? this.status,
        response: response ?? this.response,
        metadata: metadata ?? this.metadata,
      );

  factory LPRData.fromJson(Map<String, dynamic> json) => LPRData(
    status: json["status"],
    response: Response.fromJson(json["response"]),
    metadata: json["metadata"],
  );

  Map<String, dynamic> toJson() => {
    "status": status,
    "response": response.toJson(),
    "metadata": metadata,
  };
}

class Response {
  int length;
  String type;
  List<Result> results;
  List<String> plates;
  double fineAmountSum;
  double driveOff;
  double tvr;

  Response({
    required this.length,
    required this.type,
    required this.results,
    required this.plates,
    required this.fineAmountSum,
    required this.driveOff,
    required this.tvr,
  });

  Response copyWith({
    int? length,
    String? type,
    List<Result>? results,
    List<String>? plates,
    double? fineAmountSum,
    double? driveOff,
    double? tvr,
  }) => Response(
    length: length ?? this.length,
    type: type ?? this.type,
    results: results ?? this.results,
    plates: plates ?? this.plates,
    fineAmountSum: fineAmountSum ?? this.fineAmountSum,
    driveOff: driveOff ?? this.driveOff,
    tvr: tvr ?? this.tvr,
  );

  factory Response.fromJson(Map<String, dynamic> json) => Response(
    length: json["length"],
    type: json["type"],
    results: List<Result>.from(json["results"].map((x) => Result.fromJson(x))),
    plates: List<String>.from(json["plates"].map((x) => x)),
    fineAmountSum: double.tryParse(json["fine_amount_sum"].toString()) ?? 0,
    driveOff: double.tryParse(json["DriveOff"].toString()) ?? 0,
    tvr: double.tryParse(json["TVR"].toString()) ?? 0,
  );

  Map<String, dynamic> toJson() => {
    "length": length,
    "type": type,
    "results": List<dynamic>.from(results.map((x) => x.toJson())),
    "plates": List<dynamic>.from(plates.map((x) => x)),
    "fine_amount_sum": fineAmountSum,
    "DriveOff": driveOff,
    "TVR": tvr,
  };
}

class Result {
  String code;
  String status;
  int createdAt;
  String ticketNo;
  double fineAmount;
  String siteOfficerId;
  String type;
  List<String> images;
  bool resultVoid;
  Location location;
  OfficerDetails officerDetails;
  VehicleDetails vehicleDetails;
  ViolationDetails violationDetails;
  CommentDetails commentDetails;
  HeaderDetails headerDetails;
  dynamic motoristDetails;
  String lpNumber;
  bool reissue;
  String timeLimitEnforcementId;
  bool timeLimitEnforcement;
  String timeLimitEnforcementObservedTime;
  DateTime citationStartTimestamp;
  DateTime citationIssueTimestamp;
  double latitude;
  dynamic paymentDetails;
  bool paymentDone;
  double longitude;
  bool driveOff;
  bool tvr;
  bool pbcCancel;
  String citationId;
  DateTime updatedAt;
  dynamic escalationDetail;
  dynamic lateFeeDetail;
  bool registeredRoFlag;
  String roRequestStatus;
  RegisteredRoDetail registeredRoDetail;
  dynamic roInfoRequestedAt;
  dynamic roInfoReceivedAt;
  dynamic currentDueDate;
  double balanceDue;
  bool hasAppeal;
  bool sharedTicket;
  HearingDetails hearingDetails;
  String category;
  InvoiceFeeStructure invoiceFeeStructure;
  String printQuery;
  DateTime hearingDate;
  List<dynamic> paymentData;
  double chargeBackFee;
  double appealRejectedFee;
  double hearingRejectedFee;
  double bootTowFee;
  dynamic bootUpdateAt;
  dynamic fineUpdateAt;
  double collectionFee;
  dynamic collectionDate;
  double nsfFee;
  TransactionFeeData transactionFeeData;
  List<AuditTrail> auditTrail;
  bool sentForRegHolds;
  double regHoldFee;
  bool parkingBaseEmailReceived;

  Result({
    required this.code,
    required this.status,
    required this.createdAt,
    required this.ticketNo,
    required this.fineAmount,
    required this.siteOfficerId,
    required this.type,
    required this.images,
    required this.resultVoid,
    required this.location,
    required this.officerDetails,
    required this.vehicleDetails,
    required this.violationDetails,
    required this.commentDetails,
    required this.headerDetails,
    required this.motoristDetails,
    required this.lpNumber,
    required this.reissue,
    required this.timeLimitEnforcementId,
    required this.timeLimitEnforcement,
    required this.timeLimitEnforcementObservedTime,
    required this.citationStartTimestamp,
    required this.citationIssueTimestamp,
    required this.latitude,
    required this.paymentDetails,
    required this.paymentDone,
    required this.longitude,
    required this.driveOff,
    required this.tvr,
    required this.pbcCancel,
    required this.citationId,
    required this.updatedAt,
    required this.escalationDetail,
    required this.lateFeeDetail,
    required this.registeredRoFlag,
    required this.roRequestStatus,
    required this.registeredRoDetail,
    required this.roInfoRequestedAt,
    required this.roInfoReceivedAt,
    required this.currentDueDate,
    required this.balanceDue,
    required this.hasAppeal,
    required this.sharedTicket,
    required this.hearingDetails,
    required this.category,
    required this.invoiceFeeStructure,
    required this.printQuery,
    required this.hearingDate,
    required this.paymentData,
    required this.chargeBackFee,
    required this.appealRejectedFee,
    required this.hearingRejectedFee,
    required this.bootTowFee,
    required this.bootUpdateAt,
    required this.fineUpdateAt,
    required this.collectionFee,
    required this.collectionDate,
    required this.nsfFee,
    required this.transactionFeeData,
    required this.auditTrail,
    required this.sentForRegHolds,
    required this.regHoldFee,
    required this.parkingBaseEmailReceived,
  });

  Result copyWith({
    String? code,
    String? status,
    int? createdAt,
    String? ticketNo,
    double? fineAmount,
    String? siteOfficerId,
    String? type,
    List<String>? images,
    bool? resultVoid,
    Location? location,
    OfficerDetails? officerDetails,
    VehicleDetails? vehicleDetails,
    ViolationDetails? violationDetails,
    CommentDetails? commentDetails,
    HeaderDetails? headerDetails,
    dynamic motoristDetails,
    String? lpNumber,
    bool? reissue,
    String? timeLimitEnforcementId,
    bool? timeLimitEnforcement,
    String? timeLimitEnforcementObservedTime,
    DateTime? citationStartTimestamp,
    DateTime? citationIssueTimestamp,
    double? latitude,
    dynamic paymentDetails,
    bool? paymentDone,
    double? longitude,
    bool? driveOff,
    bool? tvr,
    bool? pbcCancel,
    String? citationId,
    DateTime? updatedAt,
    dynamic escalationDetail,
    dynamic lateFeeDetail,
    bool? registeredRoFlag,
    String? roRequestStatus,
    RegisteredRoDetail? registeredRoDetail,
    dynamic roInfoRequestedAt,
    dynamic roInfoReceivedAt,
    dynamic currentDueDate,
    double? balanceDue,
    bool? hasAppeal,
    bool? sharedTicket,
    HearingDetails? hearingDetails,
    String? category,
    InvoiceFeeStructure? invoiceFeeStructure,
    String? printQuery,
    DateTime? hearingDate,
    List<dynamic>? paymentData,
    double? chargeBackFee,
    double? appealRejectedFee,
    double? hearingRejectedFee,
    double? bootTowFee,
    dynamic bootUpdateAt,
    dynamic fineUpdateAt,
    double? collectionFee,
    dynamic collectionDate,
    double? nsfFee,
    TransactionFeeData? transactionFeeData,
    List<AuditTrail>? auditTrail,
    bool? sentForRegHolds,
    double? regHoldFee,
    bool? parkingBaseEmailReceived,
  }) => Result(
    code: code ?? this.code,
    status: status ?? this.status,
    createdAt: createdAt ?? this.createdAt,
    ticketNo: ticketNo ?? this.ticketNo,
    fineAmount: fineAmount ?? this.fineAmount,
    siteOfficerId: siteOfficerId ?? this.siteOfficerId,
    type: type ?? this.type,
    images: images ?? this.images,
    resultVoid: resultVoid ?? this.resultVoid,
    location: location ?? this.location,
    officerDetails: officerDetails ?? this.officerDetails,
    vehicleDetails: vehicleDetails ?? this.vehicleDetails,
    violationDetails: violationDetails ?? this.violationDetails,
    commentDetails: commentDetails ?? this.commentDetails,
    headerDetails: headerDetails ?? this.headerDetails,
    motoristDetails: motoristDetails ?? this.motoristDetails,
    lpNumber: lpNumber ?? this.lpNumber,
    reissue: reissue ?? this.reissue,
    timeLimitEnforcementId:
        timeLimitEnforcementId ?? this.timeLimitEnforcementId,
    timeLimitEnforcement: timeLimitEnforcement ?? this.timeLimitEnforcement,
    timeLimitEnforcementObservedTime:
        timeLimitEnforcementObservedTime ??
        this.timeLimitEnforcementObservedTime,
    citationStartTimestamp:
        citationStartTimestamp ?? this.citationStartTimestamp,
    citationIssueTimestamp:
        citationIssueTimestamp ?? this.citationIssueTimestamp,
    latitude: latitude ?? this.latitude,
    paymentDetails: paymentDetails ?? this.paymentDetails,
    paymentDone: paymentDone ?? this.paymentDone,
    longitude: longitude ?? this.longitude,
    driveOff: driveOff ?? this.driveOff,
    tvr: tvr ?? this.tvr,
    pbcCancel: pbcCancel ?? this.pbcCancel,
    citationId: citationId ?? this.citationId,
    updatedAt: updatedAt ?? this.updatedAt,
    escalationDetail: escalationDetail ?? this.escalationDetail,
    lateFeeDetail: lateFeeDetail ?? this.lateFeeDetail,
    registeredRoFlag: registeredRoFlag ?? this.registeredRoFlag,
    roRequestStatus: roRequestStatus ?? this.roRequestStatus,
    registeredRoDetail: registeredRoDetail ?? this.registeredRoDetail,
    roInfoRequestedAt: roInfoRequestedAt ?? this.roInfoRequestedAt,
    roInfoReceivedAt: roInfoReceivedAt ?? this.roInfoReceivedAt,
    currentDueDate: currentDueDate ?? this.currentDueDate,
    balanceDue: balanceDue ?? this.balanceDue,
    hasAppeal: hasAppeal ?? this.hasAppeal,
    sharedTicket: sharedTicket ?? this.sharedTicket,
    hearingDetails: hearingDetails ?? this.hearingDetails,
    category: category ?? this.category,
    invoiceFeeStructure: invoiceFeeStructure ?? this.invoiceFeeStructure,
    printQuery: printQuery ?? this.printQuery,
    hearingDate: hearingDate ?? this.hearingDate,
    paymentData: paymentData ?? this.paymentData,
    chargeBackFee: chargeBackFee ?? this.chargeBackFee,
    appealRejectedFee: appealRejectedFee ?? this.appealRejectedFee,
    hearingRejectedFee: hearingRejectedFee ?? this.hearingRejectedFee,
    bootTowFee: bootTowFee ?? this.bootTowFee,
    bootUpdateAt: bootUpdateAt ?? this.bootUpdateAt,
    fineUpdateAt: fineUpdateAt ?? this.fineUpdateAt,
    collectionFee: collectionFee ?? this.collectionFee,
    collectionDate: collectionDate ?? this.collectionDate,
    nsfFee: nsfFee ?? this.nsfFee,
    transactionFeeData: transactionFeeData ?? this.transactionFeeData,
    auditTrail: auditTrail ?? this.auditTrail,
    sentForRegHolds: sentForRegHolds ?? this.sentForRegHolds,
    regHoldFee: regHoldFee ?? this.regHoldFee,
    parkingBaseEmailReceived:
        parkingBaseEmailReceived ?? this.parkingBaseEmailReceived,
  );

  factory Result.fromJson(Map<String, dynamic> json) => Result(
    code: json["code"],
    status: json["status"],
    createdAt: json["created_at"],
    ticketNo: json["ticket_no"],
    fineAmount: double.tryParse(json["fine_amount"].toString()) ?? 0,
    siteOfficerId: json["site_officer_id"],
    type: json["type"],
    images: List<String>.from(json["images"].map((x) => x)),
    resultVoid: json["void"],
    location: Location.fromJson(json["location"]),
    officerDetails: OfficerDetails.fromJson(json["officer_details"]),
    vehicleDetails: VehicleDetails.fromJson(json["vehicle_details"]),
    violationDetails: ViolationDetails.fromJson(json["violation_details"]),
    commentDetails: CommentDetails.fromJson(json["comment_details"]),
    headerDetails: HeaderDetails.fromJson(json["header_details"]),
    motoristDetails: json["motorist_details"],
    lpNumber: json["lp_number"],
    reissue: json["reissue"],
    timeLimitEnforcementId: json["time_limit_enforcement_id"],
    timeLimitEnforcement: json["time_limit_enforcement"],
    timeLimitEnforcementObservedTime:
        json["time_limit_enforcement_observed_time"],
    citationStartTimestamp: DateTime.parse(json["citation_start_timestamp"]),
    citationIssueTimestamp: DateTime.parse(json["citation_issue_timestamp"]),
    latitude: json["latitude"]?.toDouble(),
    paymentDetails: json["payment_details"],
    paymentDone: json["payment_done"],
    longitude: json["longitude"]?.toDouble(),
    driveOff: json["drive_off"],
    tvr: json["tvr"],
    pbcCancel: json["pbc_cancel"],
    citationId: json["citation_id"],
    updatedAt: DateTime.parse(json["updated_at"]),
    escalationDetail: json["escalation_detail"],
    lateFeeDetail: json["late_fee_detail"],
    registeredRoFlag: json["registered_ro_flag"],
    roRequestStatus: json["ro_request_status"],
    registeredRoDetail: RegisteredRoDetail.fromJson(
      json["registered_ro_detail"],
    ),
    roInfoRequestedAt: json["ro_info_requested_at"],
    roInfoReceivedAt: json["ro_info_received_at"],
    currentDueDate: json["current_due_date"],
    balanceDue: double.tryParse(json["balance_due"].toString()) ?? 0,
    hasAppeal: json["has_appeal"],
    sharedTicket: json["shared_ticket"],
    hearingDetails: HearingDetails.fromJson(json["hearing_details"]),
    category: json["category"],
    invoiceFeeStructure: InvoiceFeeStructure.fromJson(
      json["invoice_fee_structure"],
    ),
    printQuery: json["print_query"],
    hearingDate: DateTime.parse(json["hearing_date"]),
    paymentData: List<dynamic>.from(json["payment_data"].map((x) => x)),
    chargeBackFee: double.tryParse(json["charge_back_fee"].toString()) ?? 0,
    appealRejectedFee:
        double.tryParse(json["appeal_rejected_fee"].toString()) ?? 0,
    hearingRejectedFee:
        double.tryParse(json["hearing_rejected_fee"].toString()) ?? 0,
    bootTowFee: double.tryParse(json["boot_tow_fee"].toString()) ?? 0,
    bootUpdateAt: json["boot_update_at"],
    fineUpdateAt: json["fine_update_at"],
    collectionFee: double.tryParse(json["collection_fee"].toString()) ?? 0,
    collectionDate: json["collection_date"],
    nsfFee: double.tryParse(json["nsf_fee"].toString()) ?? 0,
    transactionFeeData: TransactionFeeData.fromJson(
      json["transaction_fee_data"],
    ),
    auditTrail: List<AuditTrail>.from(
      json["audit_trail"].map((x) => AuditTrail.fromJson(x)),
    ),
    sentForRegHolds: json["sent_for_reg_holds"],
    regHoldFee: double.tryParse(json["reg_hold_fee"].toString()) ?? 0,
    parkingBaseEmailReceived: json["parking_base_email_received"],
  );

  Map<String, dynamic> toJson() => {
    "code": code,
    "status": status,
    "created_at": createdAt,
    "ticket_no": ticketNo,
    "fine_amount": fineAmount,
    "site_officer_id": siteOfficerId,
    "type": type,
    "images": List<dynamic>.from(images.map((x) => x)),
    "void": resultVoid,
    "location": location.toJson(),
    "officer_details": officerDetails.toJson(),
    "vehicle_details": vehicleDetails.toJson(),
    "violation_details": violationDetails.toJson(),
    "comment_details": commentDetails.toJson(),
    "header_details": headerDetails.toJson(),
    "motorist_details": motoristDetails,
    "lp_number": lpNumber,
    "reissue": reissue,
    "time_limit_enforcement_id": timeLimitEnforcementId,
    "time_limit_enforcement": timeLimitEnforcement,
    "time_limit_enforcement_observed_time": timeLimitEnforcementObservedTime,
    "citation_start_timestamp": citationStartTimestamp.toIso8601String(),
    "citation_issue_timestamp": citationIssueTimestamp.toIso8601String(),
    "latitude": latitude,
    "payment_details": paymentDetails,
    "payment_done": paymentDone,
    "longitude": longitude,
    "drive_off": driveOff,
    "tvr": tvr,
    "pbc_cancel": pbcCancel,
    "citation_id": citationId,
    "updated_at": updatedAt.toIso8601String(),
    "escalation_detail": escalationDetail,
    "late_fee_detail": lateFeeDetail,
    "registered_ro_flag": registeredRoFlag,
    "ro_request_status": roRequestStatus,
    "registered_ro_detail": registeredRoDetail.toJson(),
    "ro_info_requested_at": roInfoRequestedAt,
    "ro_info_received_at": roInfoReceivedAt,
    "current_due_date": currentDueDate,
    "balance_due": balanceDue,
    "has_appeal": hasAppeal,
    "shared_ticket": sharedTicket,
    "hearing_details": hearingDetails.toJson(),
    "category": category,
    "invoice_fee_structure": invoiceFeeStructure.toJson(),
    "print_query": printQuery,
    "hearing_date": hearingDate.toIso8601String(),
    "payment_data": List<dynamic>.from(paymentData.map((x) => x)),
    "charge_back_fee": chargeBackFee,
    "appeal_rejected_fee": appealRejectedFee,
    "hearing_rejected_fee": hearingRejectedFee,
    "boot_tow_fee": bootTowFee,
    "boot_update_at": bootUpdateAt,
    "fine_update_at": fineUpdateAt,
    "collection_fee": collectionFee,
    "collection_date": collectionDate,
    "nsf_fee": nsfFee,
    "transaction_fee_data": transactionFeeData.toJson(),
    "audit_trail": List<dynamic>.from(auditTrail.map((x) => x.toJson())),
    "sent_for_reg_holds": sentForRegHolds,
    "reg_hold_fee": regHoldFee,
    "parking_base_email_received": parkingBaseEmailReceived,
  };
}

class AuditTrail {
  String oldValue;
  String newValue;
  String updateType;
  DateTime timestampUtc;
  DateTime timestamp;
  String initiatorId;
  String initiatorRole;
  String initiatorName;
  String reason;
  String comment;

  AuditTrail({
    required this.oldValue,
    required this.newValue,
    required this.updateType,
    required this.timestampUtc,
    required this.timestamp,
    required this.initiatorId,
    required this.initiatorRole,
    required this.initiatorName,
    required this.reason,
    required this.comment,
  });

  AuditTrail copyWith({
    String? oldValue,
    String? newValue,
    String? updateType,
    DateTime? timestampUtc,
    DateTime? timestamp,
    String? initiatorId,
    String? initiatorRole,
    String? initiatorName,
    String? reason,
    String? comment,
  }) => AuditTrail(
    oldValue: oldValue ?? this.oldValue,
    newValue: newValue ?? this.newValue,
    updateType: updateType ?? this.updateType,
    timestampUtc: timestampUtc ?? this.timestampUtc,
    timestamp: timestamp ?? this.timestamp,
    initiatorId: initiatorId ?? this.initiatorId,
    initiatorRole: initiatorRole ?? this.initiatorRole,
    initiatorName: initiatorName ?? this.initiatorName,
    reason: reason ?? this.reason,
    comment: comment ?? this.comment,
  );

  factory AuditTrail.fromJson(Map<String, dynamic> json) => AuditTrail(
    oldValue: json["old_value"],
    newValue: json["new_value"],
    updateType: json["update_type"],
    timestampUtc: DateTime.parse(json["timestamp_utc"]),
    timestamp: DateTime.parse(json["timestamp"]),
    initiatorId: json["initiator_id"],
    initiatorRole: json["initiator_role"],
    initiatorName: json["initiator_name"],
    reason: json["reason"],
    comment: json["comment"],
  );

  Map<String, dynamic> toJson() => {
    "old_value": oldValue,
    "new_value": newValue,
    "update_type": updateType,
    "timestamp_utc": timestampUtc.toIso8601String(),
    "timestamp": timestamp.toIso8601String(),
    "initiator_id": initiatorId,
    "initiator_role": initiatorRole,
    "initiator_name": initiatorName,
    "reason": reason,
    "comment": comment,
  };
}

class CommentDetails {
  String note2;
  String note3;
  String remark1;
  String remark2;
  String note1;

  CommentDetails({
    required this.note2,
    required this.note3,
    required this.remark1,
    required this.remark2,
    required this.note1,
  });

  CommentDetails copyWith({
    String? note2,
    String? note3,
    String? remark1,
    String? remark2,
    String? note1,
  }) => CommentDetails(
    note2: note2 ?? this.note2,
    note3: note3 ?? this.note3,
    remark1: remark1 ?? this.remark1,
    remark2: remark2 ?? this.remark2,
    note1: note1 ?? this.note1,
  );

  factory CommentDetails.fromJson(Map<String, dynamic> json) => CommentDetails(
    note2: json["note_2"],
    note3: json["note_3"],
    remark1: json["remark_1"],
    remark2: json["remark_2"],
    note1: json["note_1"],
  );

  Map<String, dynamic> toJson() => {
    "note_2": note2,
    "note_3": note3,
    "remark_1": remark1,
    "remark_2": remark2,
    "note_1": note1,
  };
}

class HeaderDetails {
  String timestamp;
  String citationNumber;

  HeaderDetails({required this.timestamp, required this.citationNumber});

  HeaderDetails copyWith({String? timestamp, String? citationNumber}) =>
      HeaderDetails(
        timestamp: timestamp ?? this.timestamp,
        citationNumber: citationNumber ?? this.citationNumber,
      );

  factory HeaderDetails.fromJson(Map<String, dynamic> json) => HeaderDetails(
    timestamp: json["timestamp"],
    citationNumber: json["citation_number"],
  );

  Map<String, dynamic> toJson() => {
    "timestamp": timestamp,
    "citation_number": citationNumber,
  };
}

class HearingDetails {
  dynamic date;
  String comment;
  String status;
  String name;
  String hearingDateString;
  dynamic userInfo;

  HearingDetails({
    required this.date,
    required this.comment,
    required this.status,
    required this.name,
    required this.hearingDateString,
    required this.userInfo,
  });

  HearingDetails copyWith({
    dynamic date,
    String? comment,
    String? status,
    String? name,
    String? hearingDateString,
    dynamic userInfo,
  }) => HearingDetails(
    date: date ?? this.date,
    comment: comment ?? this.comment,
    status: status ?? this.status,
    name: name ?? this.name,
    hearingDateString: hearingDateString ?? this.hearingDateString,
    userInfo: userInfo ?? this.userInfo,
  );

  factory HearingDetails.fromJson(Map<String, dynamic> json) => HearingDetails(
    date: json["date"],
    comment: json["comment"],
    status: json["status"],
    name: json["name"],
    hearingDateString: json["hearing_date_string"],
    userInfo: json["user_info"],
  );

  Map<String, dynamic> toJson() => {
    "date": date,
    "comment": comment,
    "status": status,
    "name": name,
    "hearing_date_string": hearingDateString,
    "user_info": userInfo,
  };
}

class InvoiceFeeStructure {
  double citationFee;
  double saleTax;
  double parkingFee;

  InvoiceFeeStructure({
    required this.citationFee,
    required this.saleTax,
    required this.parkingFee,
  });

  InvoiceFeeStructure copyWith({
    double? citationFee,
    double? saleTax,
    double? parkingFee,
  }) => InvoiceFeeStructure(
    citationFee: citationFee ?? this.citationFee,
    saleTax: saleTax ?? this.saleTax,
    parkingFee: parkingFee ?? this.parkingFee,
  );

  factory InvoiceFeeStructure.fromJson(Map<String, dynamic> json) =>
      InvoiceFeeStructure(
        citationFee: double.tryParse(json["citation_fee"].toString()) ?? 0,
        saleTax: double.tryParse(json["sale_tax"].toString()) ?? 0,
        parkingFee: double.tryParse(json["parking_fee"].toString()) ?? 0,
      );

  Map<String, dynamic> toJson() => {
    "citation_fee": citationFee,
    "sale_tax": saleTax,
    "parking_fee": parkingFee,
  };
}

class Location {
  String meter;
  String side;
  String direction;
  String lotLookupCode;
  String street;
  String streetLookupCode;
  String lot;
  String branchLotid;
  String spaceId;
  dynamic impoundCode;
  String block;

  Location({
    required this.meter,
    required this.side,
    required this.direction,
    required this.lotLookupCode,
    required this.street,
    required this.streetLookupCode,
    required this.lot,
    required this.branchLotid,
    required this.spaceId,
    required this.impoundCode,
    required this.block,
  });

  Location copyWith({
    String? meter,
    String? side,
    String? direction,
    String? lotLookupCode,
    String? street,
    String? streetLookupCode,
    String? lot,
    String? branchLotid,
    String? spaceId,
    dynamic impoundCode,
    String? block,
  }) => Location(
    meter: meter ?? this.meter,
    side: side ?? this.side,
    direction: direction ?? this.direction,
    lotLookupCode: lotLookupCode ?? this.lotLookupCode,
    street: street ?? this.street,
    streetLookupCode: streetLookupCode ?? this.streetLookupCode,
    lot: lot ?? this.lot,
    branchLotid: branchLotid ?? this.branchLotid,
    spaceId: spaceId ?? this.spaceId,
    impoundCode: impoundCode ?? this.impoundCode,
    block: block ?? this.block,
  );

  factory Location.fromJson(Map<String, dynamic> json) => Location(
    meter: json["meter"],
    side: json["side"],
    direction: json["direction"],
    lotLookupCode: json["lot_lookup_code"],
    street: json["street"],
    streetLookupCode: json["street_lookup_code"],
    lot: json["lot"],
    branchLotid: json["branch_lotid"],
    spaceId: json["space_id"],
    impoundCode: json["impound_code"],
    block: json["block"],
  );

  Map<String, dynamic> toJson() => {
    "meter": meter,
    "side": side,
    "direction": direction,
    "lot_lookup_code": lotLookupCode,
    "street": street,
    "street_lookup_code": streetLookupCode,
    "lot": lot,
    "branch_lotid": branchLotid,
    "space_id": spaceId,
    "impound_code": impoundCode,
    "block": block,
  };
}

class OfficerDetails {
  dynamic peoName;
  String squad;
  String zone;
  String deviceId;
  String badgeId;
  String beat;
  String officerName;
  String signature;
  String shift;
  String deviceFriendlyName;
  String agency;
  String officerLookupCode;
  dynamic peoFname;
  dynamic peoLname;

  OfficerDetails({
    required this.peoName,
    required this.squad,
    required this.zone,
    required this.deviceId,
    required this.badgeId,
    required this.beat,
    required this.officerName,
    required this.signature,
    required this.shift,
    required this.deviceFriendlyName,
    required this.agency,
    required this.officerLookupCode,
    required this.peoFname,
    required this.peoLname,
  });

  OfficerDetails copyWith({
    dynamic peoName,
    String? squad,
    String? zone,
    String? deviceId,
    String? badgeId,
    String? beat,
    String? officerName,
    String? signature,
    String? shift,
    String? deviceFriendlyName,
    String? agency,
    String? officerLookupCode,
    dynamic peoFname,
    dynamic peoLname,
  }) => OfficerDetails(
    peoName: peoName ?? this.peoName,
    squad: squad ?? this.squad,
    zone: zone ?? this.zone,
    deviceId: deviceId ?? this.deviceId,
    badgeId: badgeId ?? this.badgeId,
    beat: beat ?? this.beat,
    officerName: officerName ?? this.officerName,
    signature: signature ?? this.signature,
    shift: shift ?? this.shift,
    deviceFriendlyName: deviceFriendlyName ?? this.deviceFriendlyName,
    agency: agency ?? this.agency,
    officerLookupCode: officerLookupCode ?? this.officerLookupCode,
    peoFname: peoFname ?? this.peoFname,
    peoLname: peoLname ?? this.peoLname,
  );

  factory OfficerDetails.fromJson(Map<String, dynamic> json) => OfficerDetails(
    peoName: json["peo_name"],
    squad: json["squad"],
    zone: json["zone"],
    deviceId: json["device_id"],
    badgeId: json["badge_id"],
    beat: json["beat"],
    officerName: json["officer_name"],
    signature: json["signature"],
    shift: json["shift"],
    deviceFriendlyName: json["device_friendly_name"],
    agency: json["agency"],
    officerLookupCode: json["officer_lookup_code"],
    peoFname: json["peo_fname"],
    peoLname: json["peo_lname"],
  );

  Map<String, dynamic> toJson() => {
    "peo_name": peoName,
    "squad": squad,
    "zone": zone,
    "device_id": deviceId,
    "badge_id": badgeId,
    "beat": beat,
    "officer_name": officerName,
    "signature": signature,
    "shift": shift,
    "device_friendly_name": deviceFriendlyName,
    "agency": agency,
    "officer_lookup_code": officerLookupCode,
    "peo_fname": peoFname,
    "peo_lname": peoLname,
  };
}

class RegisteredRoDetail {
  String account;
  Address currentAddress;
  Address oldAddress;
  String mailReturnedRemark;
  bool mailReturned;
  String mailReturnedDate;

  RegisteredRoDetail({
    required this.account,
    required this.currentAddress,
    required this.oldAddress,
    required this.mailReturnedRemark,
    required this.mailReturned,
    required this.mailReturnedDate,
  });

  RegisteredRoDetail copyWith({
    String? account,
    Address? currentAddress,
    Address? oldAddress,
    String? mailReturnedRemark,
    bool? mailReturned,
    String? mailReturnedDate,
  }) => RegisteredRoDetail(
    account: account ?? this.account,
    currentAddress: currentAddress ?? this.currentAddress,
    oldAddress: oldAddress ?? this.oldAddress,
    mailReturnedRemark: mailReturnedRemark ?? this.mailReturnedRemark,
    mailReturned: mailReturned ?? this.mailReturned,
    mailReturnedDate: mailReturnedDate ?? this.mailReturnedDate,
  );

  factory RegisteredRoDetail.fromJson(Map<String, dynamic> json) =>
      RegisteredRoDetail(
        account: json["account"],
        currentAddress: Address.fromJson(json["current_address"]),
        oldAddress: Address.fromJson(json["old_address"]),
        mailReturnedRemark: json["mail_returned_remark"],
        mailReturned: json["mail_returned"],
        mailReturnedDate: json["mail_returned_date"],
      );

  Map<String, dynamic> toJson() => {
    "account": account,
    "current_address": currentAddress.toJson(),
    "old_address": oldAddress.toJson(),
    "mail_returned_remark": mailReturnedRemark,
    "mail_returned": mailReturned,
    "mail_returned_date": mailReturnedDate,
  };
}

class Address {
  String name;
  String address;
  String city;
  String state;
  String zip;

  Address({
    required this.name,
    required this.address,
    required this.city,
    required this.state,
    required this.zip,
  });

  Address copyWith({
    String? name,
    String? address,
    String? city,
    String? state,
    String? zip,
  }) => Address(
    name: name ?? this.name,
    address: address ?? this.address,
    city: city ?? this.city,
    state: state ?? this.state,
    zip: zip ?? this.zip,
  );

  factory Address.fromJson(Map<String, dynamic> json) => Address(
    name: json["name"],
    address: json["address"],
    city: json["city"],
    state: json["state"],
    zip: json["zip"],
  );

  Map<String, dynamic> toJson() => {
    "name": name,
    "address": address,
    "city": city,
    "state": state,
    "zip": zip,
  };
}

class TransactionFeeData {
  double paidRegHoldFee;

  TransactionFeeData({required this.paidRegHoldFee});

  TransactionFeeData copyWith({double? paidRegHoldFee}) =>
      TransactionFeeData(paidRegHoldFee: paidRegHoldFee ?? this.paidRegHoldFee);

  factory TransactionFeeData.fromJson(Map<String, dynamic> json) =>
      TransactionFeeData(
        paidRegHoldFee:
            double.tryParse(json["paid_reg_hold_fee"].toString()) ?? 0,
      );

  Map<String, dynamic> toJson() => {"paid_reg_hold_fee": paidRegHoldFee};
}

class VehicleDetails {
  String state;
  String color;
  String bodyStyleLookupCode;
  String model;
  String modelLookupCode;
  String licenseExpiry;
  String lpNumber;
  String make;
  String bodyStyle;
  String decalYear;
  String decalNumber;
  String vinNumber;

  VehicleDetails({
    required this.state,
    required this.color,
    required this.bodyStyleLookupCode,
    required this.model,
    required this.modelLookupCode,
    required this.licenseExpiry,
    required this.lpNumber,
    required this.make,
    required this.bodyStyle,
    required this.decalYear,
    required this.decalNumber,
    required this.vinNumber,
  });

  VehicleDetails copyWith({
    String? state,
    String? color,
    String? bodyStyleLookupCode,
    String? model,
    String? modelLookupCode,
    String? licenseExpiry,
    String? lpNumber,
    String? make,
    String? bodyStyle,
    String? decalYear,
    String? decalNumber,
    String? vinNumber,
  }) => VehicleDetails(
    state: state ?? this.state,
    color: color ?? this.color,
    bodyStyleLookupCode: bodyStyleLookupCode ?? this.bodyStyleLookupCode,
    model: model ?? this.model,
    modelLookupCode: modelLookupCode ?? this.modelLookupCode,
    licenseExpiry: licenseExpiry ?? this.licenseExpiry,
    lpNumber: lpNumber ?? this.lpNumber,
    make: make ?? this.make,
    bodyStyle: bodyStyle ?? this.bodyStyle,
    decalYear: decalYear ?? this.decalYear,
    decalNumber: decalNumber ?? this.decalNumber,
    vinNumber: vinNumber ?? this.vinNumber,
  );

  factory VehicleDetails.fromJson(Map<String, dynamic> json) => VehicleDetails(
    state: json["state"],
    color: json["color"],
    bodyStyleLookupCode: json["body_style_lookup_code"],
    model: json["model"],
    modelLookupCode: json["model_lookup_code"],
    licenseExpiry: json["license_expiry"],
    lpNumber: json["lp_number"],
    make: json["make"],
    bodyStyle: json["body_style"],
    decalYear: json["decal_year"],
    decalNumber: json["decal_number"],
    vinNumber: json["vin_number"],
  );

  Map<String, dynamic> toJson() => {
    "state": state,
    "color": color,
    "body_style_lookup_code": bodyStyleLookupCode,
    "model": model,
    "model_lookup_code": modelLookupCode,
    "license_expiry": licenseExpiry,
    "lp_number": lpNumber,
    "make": make,
    "body_style": bodyStyle,
    "decal_year": decalYear,
    "decal_number": decalNumber,
    "vin_number": vinNumber,
  };
}

class ViolationDetails {
  String violation;
  String description;
  double fine;
  String vioType;
  String vioTypeDescription;
  String code;
  double lateFine;
  double due15Days;
  double due30Days;
  double due45Days;
  String exportCode;
  String vioTypeCode;
  double cost;
  dynamic invoiceFeeStructure;
  String sanctionsType;

  ViolationDetails({
    required this.violation,
    required this.description,
    required this.fine,
    required this.vioType,
    required this.vioTypeDescription,
    required this.code,
    required this.lateFine,
    required this.due15Days,
    required this.due30Days,
    required this.due45Days,
    required this.exportCode,
    required this.vioTypeCode,
    required this.cost,
    required this.invoiceFeeStructure,
    required this.sanctionsType,
  });

  ViolationDetails copyWith({
    String? violation,
    String? description,
    double? fine,
    String? vioType,
    String? vioTypeDescription,
    String? code,
    double? lateFine,
    double? due15Days,
    double? due30Days,
    double? due45Days,
    String? exportCode,
    String? vioTypeCode,
    double? cost,
    dynamic invoiceFeeStructure,
    String? sanctionsType,
  }) => ViolationDetails(
    violation: violation ?? this.violation,
    description: description ?? this.description,
    fine: fine ?? this.fine,
    vioType: vioType ?? this.vioType,
    vioTypeDescription: vioTypeDescription ?? this.vioTypeDescription,
    code: code ?? this.code,
    lateFine: lateFine ?? this.lateFine,
    due15Days: due15Days ?? this.due15Days,
    due30Days: due30Days ?? this.due30Days,
    due45Days: due45Days ?? this.due45Days,
    exportCode: exportCode ?? this.exportCode,
    vioTypeCode: vioTypeCode ?? this.vioTypeCode,
    cost: cost ?? this.cost,
    invoiceFeeStructure: invoiceFeeStructure ?? this.invoiceFeeStructure,
    sanctionsType: sanctionsType ?? this.sanctionsType,
  );

  factory ViolationDetails.fromJson(Map<String, dynamic> json) =>
      ViolationDetails(
        violation: json["violation"],
        description: json["description"],
        fine: double.tryParse(json["fine"].toString()) ?? 0,
        vioType: json["vio_type"],
        vioTypeDescription: json["vio_type_description"],
        code: json["code"],
        lateFine: double.tryParse(json["late_fine"].toString()) ?? 0,
        due15Days: double.tryParse(json["due_15_days"].toString()) ?? 0,
        due30Days: double.tryParse(json["due_30_days"].toString()) ?? 0,
        due45Days: double.tryParse(json["due_45_days"].toString()) ?? 0,
        exportCode: json["export_code"],
        vioTypeCode: json["vio_type_code"],
        cost: double.tryParse(json["cost"].toString()) ?? 0,
        invoiceFeeStructure: json["invoice_fee_structure"],
        sanctionsType: json["sanctions_type"],
      );

  Map<String, dynamic> toJson() => {
    "violation": violation,
    "description": description,
    "fine": fine,
    "vio_type": vioType,
    "vio_type_description": vioTypeDescription,
    "code": code,
    "late_fine": lateFine,
    "due_15_days": due15Days,
    "due_30_days": due30Days,
    "due_45_days": due45Days,
    "export_code": exportCode,
    "vio_type_code": vioTypeCode,
    "cost": cost,
    "invoice_fee_structure": invoiceFeeStructure,
    "sanctions_type": sanctionsType,
  };
}

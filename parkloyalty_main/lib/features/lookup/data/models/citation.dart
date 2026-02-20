// To parse this JSON data, do
//
//     final citation = citationFromJson(jsonString);

import 'dart:convert';

Citation citationFromJson(String str) => Citation.fromJson(json.decode(str));

String citationToJson(Citation data) => json.encode(data.toJson());

class Citation {
  List<CitationData> data;
  int length;
  bool success;

  Citation({required this.data, required this.length, required this.success});

  Citation copyWith({List<CitationData>? data, int? length, bool? success}) =>
      Citation(
        data: data ?? this.data,
        length: length ?? this.length,
        success: success ?? this.success,
      );

  factory Citation.fromJson(Map<String, dynamic> json) => Citation(
    data: json["data"] == null
        ? []
        : List<CitationData>.from(
            json["data"].map((x) => CitationData.fromJson(x)),
          ),
    length: json["length"],
    success: json["success"],
  );

  Map<String, dynamic> toJson() => {
    "data": List<dynamic>.from(data.map((x) => x.toJson())),
    "length": length,
    "success": success,
  };
}

class CitationData {
  String id;
  int originalAmount;
  int amountDue;
  String code;
  String status;
  int createdAt;
  String ticketNo;
  int fineAmount;
  String siteOfficerId;
  DateTime vehicleInTimestamp;
  DateTime vehicleOutTimestamp;
  String type;
  bool datumVoid;
  dynamic voidImages;
  List<String> images;
  dynamic plImages;
  Location location;
  OfficerDetails officerDetails;
  VehicleDetails vehicleDetails;
  ViolationDetails violationDetails;
  CommentDetails commentDetails;
  HeaderDetails headerDetails;
  String lpNumber;
  bool reissue;
  bool timeLimitEnforcement;
  String timeLimitEnforcementId;
  String timeLimitEnforcementObservedTime;
  DateTime citationStartTimestamp;
  DateTime citationIssueTimestamp;
  double latitude;
  PaymentDetails paymentDetails;
  bool paymentDone;
  double longitude;
  bool driveOff;
  bool tvr;
  String citationId;
  bool pbcCancel;
  String scofflawId;
  List<AuditTrail> auditTrail;
  String roRequestStatus;
  bool registeredRoFlag;
  RegisteredRoDetail registeredRoDetail;
  bool hasAppeal;
  bool sharedTicket;
  dynamic noticeDetail;
  String category;
  List<dynamic> paymentData;
  Map<String, int> transactionFeeData;
  DateTime sentForCollectionAt;
  bool sentForCollection;
  bool hasCollectionFee;
  InvoiceFeeStructure invoiceFeeStructure;
  int nsfFee;
  int collectionFee;
  int bootTowFee;
  int chargeBackFee;
  int appealRejectedFee;
  int hearingRejectedFee;
  int regHoldFee;
  dynamic refundData;
  int serviceFee;
  int salesTax;
  int salesTaxNonCardPayments;
  int netAmount;
  DateTime updatedAt;
  dynamic escalatedAmount;
  dynamic escalationDetail;
  dynamic lateFeeDetail;
  int balanceDue;
  HearingDetails hearingDetails;
  dynamic notificationAuditTrail;
  String printQuery;
  bool invoiceGenerated;
  bool ticketFirstNoticeGenerated;
  bool lateInvoiceGenerated;
  DateTime appealEligibleDate;
  bool courtNoticeSent;
  bool sentForRegHolds;
  bool sendToFloridaSftp;
  int administrativeFee;
  int parkingFee;
  DateTime hearingDate;
  int discountFee;
  String bootInstanceId;
  String cancelledBy;
  String gatewayTransactionId;
  String username;
  String email;

  CitationData({
    required this.id,
    required this.originalAmount,
    required this.amountDue,
    required this.code,
    required this.status,
    required this.createdAt,
    required this.ticketNo,
    required this.fineAmount,
    required this.siteOfficerId,
    required this.vehicleInTimestamp,
    required this.vehicleOutTimestamp,
    required this.type,
    required this.datumVoid,
    required this.voidImages,
    required this.images,
    required this.plImages,
    required this.location,
    required this.officerDetails,
    required this.vehicleDetails,
    required this.violationDetails,
    required this.commentDetails,
    required this.headerDetails,
    required this.lpNumber,
    required this.reissue,
    required this.timeLimitEnforcement,
    required this.timeLimitEnforcementId,
    required this.timeLimitEnforcementObservedTime,
    required this.citationStartTimestamp,
    required this.citationIssueTimestamp,
    required this.latitude,
    required this.paymentDetails,
    required this.paymentDone,
    required this.longitude,
    required this.driveOff,
    required this.tvr,
    required this.citationId,
    required this.pbcCancel,
    required this.scofflawId,
    required this.auditTrail,
    required this.roRequestStatus,
    required this.registeredRoFlag,
    required this.registeredRoDetail,
    required this.hasAppeal,
    required this.sharedTicket,
    required this.noticeDetail,
    required this.category,
    required this.paymentData,
    required this.transactionFeeData,
    required this.sentForCollectionAt,
    required this.sentForCollection,
    required this.hasCollectionFee,
    required this.invoiceFeeStructure,
    required this.nsfFee,
    required this.collectionFee,
    required this.bootTowFee,
    required this.chargeBackFee,
    required this.appealRejectedFee,
    required this.hearingRejectedFee,
    required this.regHoldFee,
    required this.refundData,
    required this.serviceFee,
    required this.salesTax,
    required this.salesTaxNonCardPayments,
    required this.netAmount,
    required this.updatedAt,
    required this.escalatedAmount,
    required this.escalationDetail,
    required this.lateFeeDetail,
    required this.balanceDue,
    required this.hearingDetails,
    required this.notificationAuditTrail,
    required this.printQuery,
    required this.invoiceGenerated,
    required this.ticketFirstNoticeGenerated,
    required this.lateInvoiceGenerated,
    required this.appealEligibleDate,
    required this.courtNoticeSent,
    required this.sentForRegHolds,
    required this.sendToFloridaSftp,
    required this.administrativeFee,
    required this.parkingFee,
    required this.hearingDate,
    required this.discountFee,
    required this.bootInstanceId,
    required this.cancelledBy,
    required this.gatewayTransactionId,
    required this.username,
    required this.email,
  });

  CitationData copyWith({
    String? id,
    int? originalAmount,
    int? amountDue,
    String? code,
    String? status,
    int? createdAt,
    String? ticketNo,
    int? fineAmount,
    String? siteOfficerId,
    DateTime? vehicleInTimestamp,
    DateTime? vehicleOutTimestamp,
    String? type,
    bool? datumVoid,
    dynamic voidImages,
    List<String>? images,
    dynamic plImages,
    Location? location,
    OfficerDetails? officerDetails,
    VehicleDetails? vehicleDetails,
    ViolationDetails? violationDetails,
    CommentDetails? commentDetails,
    HeaderDetails? headerDetails,
    String? lpNumber,
    bool? reissue,
    bool? timeLimitEnforcement,
    String? timeLimitEnforcementId,
    String? timeLimitEnforcementObservedTime,
    DateTime? citationStartTimestamp,
    DateTime? citationIssueTimestamp,
    double? latitude,
    PaymentDetails? paymentDetails,
    bool? paymentDone,
    double? longitude,
    bool? driveOff,
    bool? tvr,
    String? citationId,
    bool? pbcCancel,
    String? scofflawId,
    List<AuditTrail>? auditTrail,
    String? roRequestStatus,
    bool? registeredRoFlag,
    RegisteredRoDetail? registeredRoDetail,
    bool? hasAppeal,
    bool? sharedTicket,
    dynamic noticeDetail,
    String? category,
    List<dynamic>? paymentData,
    Map<String, int>? transactionFeeData,
    DateTime? sentForCollectionAt,
    bool? sentForCollection,
    bool? hasCollectionFee,
    InvoiceFeeStructure? invoiceFeeStructure,
    int? nsfFee,
    int? collectionFee,
    int? bootTowFee,
    int? chargeBackFee,
    int? appealRejectedFee,
    int? hearingRejectedFee,
    int? regHoldFee,
    dynamic refundData,
    int? serviceFee,
    int? salesTax,
    int? salesTaxNonCardPayments,
    int? netAmount,
    DateTime? updatedAt,
    dynamic escalatedAmount,
    dynamic escalationDetail,
    dynamic lateFeeDetail,
    int? balanceDue,
    HearingDetails? hearingDetails,
    dynamic notificationAuditTrail,
    String? printQuery,
    bool? invoiceGenerated,
    bool? ticketFirstNoticeGenerated,
    bool? lateInvoiceGenerated,
    DateTime? appealEligibleDate,
    bool? courtNoticeSent,
    bool? sentForRegHolds,
    bool? sendToFloridaSftp,
    int? administrativeFee,
    int? parkingFee,
    DateTime? hearingDate,
    int? discountFee,
    String? bootInstanceId,
    String? cancelledBy,
    String? gatewayTransactionId,
    String? username,
    String? email,
  }) => CitationData(
    id: id ?? this.id,
    originalAmount: originalAmount ?? this.originalAmount,
    amountDue: amountDue ?? this.amountDue,
    code: code ?? this.code,
    status: status ?? this.status,
    createdAt: createdAt ?? this.createdAt,
    ticketNo: ticketNo ?? this.ticketNo,
    fineAmount: fineAmount ?? this.fineAmount,
    siteOfficerId: siteOfficerId ?? this.siteOfficerId,
    vehicleInTimestamp: vehicleInTimestamp ?? this.vehicleInTimestamp,
    vehicleOutTimestamp: vehicleOutTimestamp ?? this.vehicleOutTimestamp,
    type: type ?? this.type,
    datumVoid: datumVoid ?? this.datumVoid,
    voidImages: voidImages ?? this.voidImages,
    images: images ?? this.images,
    plImages: plImages ?? this.plImages,
    location: location ?? this.location,
    officerDetails: officerDetails ?? this.officerDetails,
    vehicleDetails: vehicleDetails ?? this.vehicleDetails,
    violationDetails: violationDetails ?? this.violationDetails,
    commentDetails: commentDetails ?? this.commentDetails,
    headerDetails: headerDetails ?? this.headerDetails,
    lpNumber: lpNumber ?? this.lpNumber,
    reissue: reissue ?? this.reissue,
    timeLimitEnforcement: timeLimitEnforcement ?? this.timeLimitEnforcement,
    timeLimitEnforcementId:
        timeLimitEnforcementId ?? this.timeLimitEnforcementId,
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
    citationId: citationId ?? this.citationId,
    pbcCancel: pbcCancel ?? this.pbcCancel,
    scofflawId: scofflawId ?? this.scofflawId,
    auditTrail: auditTrail ?? this.auditTrail,
    roRequestStatus: roRequestStatus ?? this.roRequestStatus,
    registeredRoFlag: registeredRoFlag ?? this.registeredRoFlag,
    registeredRoDetail: registeredRoDetail ?? this.registeredRoDetail,
    hasAppeal: hasAppeal ?? this.hasAppeal,
    sharedTicket: sharedTicket ?? this.sharedTicket,
    noticeDetail: noticeDetail ?? this.noticeDetail,
    category: category ?? this.category,
    paymentData: paymentData ?? this.paymentData,
    transactionFeeData: transactionFeeData ?? this.transactionFeeData,
    sentForCollectionAt: sentForCollectionAt ?? this.sentForCollectionAt,
    sentForCollection: sentForCollection ?? this.sentForCollection,
    hasCollectionFee: hasCollectionFee ?? this.hasCollectionFee,
    invoiceFeeStructure: invoiceFeeStructure ?? this.invoiceFeeStructure,
    nsfFee: nsfFee ?? this.nsfFee,
    collectionFee: collectionFee ?? this.collectionFee,
    bootTowFee: bootTowFee ?? this.bootTowFee,
    chargeBackFee: chargeBackFee ?? this.chargeBackFee,
    appealRejectedFee: appealRejectedFee ?? this.appealRejectedFee,
    hearingRejectedFee: hearingRejectedFee ?? this.hearingRejectedFee,
    regHoldFee: regHoldFee ?? this.regHoldFee,
    refundData: refundData ?? this.refundData,
    serviceFee: serviceFee ?? this.serviceFee,
    salesTax: salesTax ?? this.salesTax,
    salesTaxNonCardPayments:
        salesTaxNonCardPayments ?? this.salesTaxNonCardPayments,
    netAmount: netAmount ?? this.netAmount,
    updatedAt: updatedAt ?? this.updatedAt,
    escalatedAmount: escalatedAmount ?? this.escalatedAmount,
    escalationDetail: escalationDetail ?? this.escalationDetail,
    lateFeeDetail: lateFeeDetail ?? this.lateFeeDetail,
    balanceDue: balanceDue ?? this.balanceDue,
    hearingDetails: hearingDetails ?? this.hearingDetails,
    notificationAuditTrail:
        notificationAuditTrail ?? this.notificationAuditTrail,
    printQuery: printQuery ?? this.printQuery,
    invoiceGenerated: invoiceGenerated ?? this.invoiceGenerated,
    ticketFirstNoticeGenerated:
        ticketFirstNoticeGenerated ?? this.ticketFirstNoticeGenerated,
    lateInvoiceGenerated: lateInvoiceGenerated ?? this.lateInvoiceGenerated,
    appealEligibleDate: appealEligibleDate ?? this.appealEligibleDate,
    courtNoticeSent: courtNoticeSent ?? this.courtNoticeSent,
    sentForRegHolds: sentForRegHolds ?? this.sentForRegHolds,
    sendToFloridaSftp: sendToFloridaSftp ?? this.sendToFloridaSftp,
    administrativeFee: administrativeFee ?? this.administrativeFee,
    parkingFee: parkingFee ?? this.parkingFee,
    hearingDate: hearingDate ?? this.hearingDate,
    discountFee: discountFee ?? this.discountFee,
    bootInstanceId: bootInstanceId ?? this.bootInstanceId,
    cancelledBy: cancelledBy ?? this.cancelledBy,
    gatewayTransactionId: gatewayTransactionId ?? this.gatewayTransactionId,
    username: username ?? this.username,
    email: email ?? this.email,
  );

  factory CitationData.fromJson(Map<String, dynamic> json) => CitationData(
    id: json["id"],
    originalAmount: json["original_amount"],
    amountDue: json["amount_due"],
    code: json["code"],
    status: json["status"],
    createdAt: json["created_at"],
    ticketNo: json["ticket_no"],
    fineAmount: json["fine_amount"],
    siteOfficerId: json["site_officer_id"],
    vehicleInTimestamp: DateTime.parse(json["vehicle_in_timestamp"]),
    vehicleOutTimestamp: DateTime.parse(json["vehicle_out_timestamp"]),
    type: json["type"],
    datumVoid: json["void"],
    voidImages: json["void_images"],
    images: List<String>.from(json["images"].map((x) => x)),
    plImages: json["pl_images"],
    location: Location.fromJson(json["location"]),
    officerDetails: OfficerDetails.fromJson(json["officer_details"]),
    vehicleDetails: VehicleDetails.fromJson(json["vehicle_details"]),
    violationDetails: ViolationDetails.fromJson(json["violation_details"]),
    commentDetails: CommentDetails.fromJson(json["comment_details"]),
    headerDetails: HeaderDetails.fromJson(json["header_details"]),
    lpNumber: json["lp_number"],
    reissue: json["reissue"],
    timeLimitEnforcement: json["time_limit_enforcement"],
    timeLimitEnforcementId: json["time_limit_enforcement_id"],
    timeLimitEnforcementObservedTime:
        json["time_limit_enforcement_observed_time"],
    citationStartTimestamp: DateTime.parse(json["citation_start_timestamp"]),
    citationIssueTimestamp: DateTime.parse(json["citation_issue_timestamp"]),
    latitude: json["latitude"],
    paymentDetails: PaymentDetails.fromJson(json["payment_details"]),
    paymentDone: json["payment_done"],
    longitude: json["longitude"],
    driveOff: json["drive_off"],
    tvr: json["tvr"],
    citationId: json["citation_id"],
    pbcCancel: json["pbc_cancel"],
    scofflawId: json["scofflaw_id"],
    auditTrail: List<AuditTrail>.from(
      json["audit_trail"].map((x) => AuditTrail.fromJson(x)),
    ),
    roRequestStatus: json["ro_request_status"],
    registeredRoFlag: json["registered_ro_flag"],
    registeredRoDetail: RegisteredRoDetail.fromJson(
      json["registered_ro_detail"],
    ),
    hasAppeal: json["has_appeal"],
    sharedTicket: json["shared_ticket"],
    noticeDetail: json["notice_detail"],
    category: json["category"],
    paymentData: List<dynamic>.from(json["payment_data"].map((x) => x)),
    transactionFeeData: Map.from(
      json["transaction_fee_data"],
    ).map((k, v) => MapEntry<String, int>(k, v)),
    sentForCollectionAt: DateTime.parse(json["sent_for_collection_at"]),
    sentForCollection: json["sent_for_collection"],
    hasCollectionFee: json["has_collection_fee"],
    invoiceFeeStructure: InvoiceFeeStructure.fromJson(
      json["invoice_fee_structure"],
    ),
    nsfFee: json["nsf_fee"],
    collectionFee: json["collection_fee"],
    bootTowFee: json["boot_tow_fee"],
    chargeBackFee: json["charge_back_fee"],
    appealRejectedFee: json["appeal_rejected_fee"],
    hearingRejectedFee: json["hearing_rejected_fee"],
    regHoldFee: json["reg_hold_fee"],
    refundData: json["refund_data"],
    serviceFee: json["service_fee"],
    salesTax: json["sales_tax"],
    salesTaxNonCardPayments: json["sales_tax_non_card_payments"],
    netAmount: json["net_amount"],
    updatedAt: DateTime.parse(json["updated_at"]),
    escalatedAmount: json["escalated_amount"],
    escalationDetail: json["escalation_detail"],
    lateFeeDetail: json["late_fee_detail"],
    balanceDue: json["balance_due"],
    hearingDetails: HearingDetails.fromJson(json["hearing_details"]),
    notificationAuditTrail: json["notification_audit_trail"],
    printQuery: json["print_query"],
    invoiceGenerated: json["invoice_generated"],
    ticketFirstNoticeGenerated: json["ticket_first_notice_generated"],
    lateInvoiceGenerated: json["late_invoice_generated"],
    appealEligibleDate: DateTime.parse(json["appeal_eligible_date"]),
    courtNoticeSent: json["court_notice_sent"],
    sentForRegHolds: json["sent_for_reg_holds"],
    sendToFloridaSftp: json["send_to_florida_sftp"],
    administrativeFee: json["administrative_fee"],
    parkingFee: json["parking_fee"],
    hearingDate: DateTime.parse(json["hearing_date"]),
    discountFee: json["discount_fee"],
    bootInstanceId: json["boot_instance_id"],
    cancelledBy: json["cancelled_by"],
    gatewayTransactionId: json["gateway_transaction_id"],
    username: json["username"],
    email: json["email"],
  );

  Map<String, dynamic> toJson() => {
    "id": id,
    "original_amount": originalAmount,
    "amount_due": amountDue,
    "code": code,
    "status": status,
    "created_at": createdAt,
    "ticket_no": ticketNo,
    "fine_amount": fineAmount,
    "site_officer_id": siteOfficerId,
    "vehicle_in_timestamp": vehicleInTimestamp.toIso8601String(),
    "vehicle_out_timestamp": vehicleOutTimestamp.toIso8601String(),
    "type": type,
    "void": datumVoid,
    "void_images": voidImages,
    "images": List<dynamic>.from(images.map((x) => x)),
    "pl_images": plImages,
    "location": location.toJson(),
    "officer_details": officerDetails.toJson(),
    "vehicle_details": vehicleDetails.toJson(),
    "violation_details": violationDetails.toJson(),
    "comment_details": commentDetails.toJson(),
    "header_details": headerDetails.toJson(),
    "lp_number": lpNumber,
    "reissue": reissue,
    "time_limit_enforcement": timeLimitEnforcement,
    "time_limit_enforcement_id": timeLimitEnforcementId,
    "time_limit_enforcement_observed_time": timeLimitEnforcementObservedTime,
    "citation_start_timestamp": citationStartTimestamp.toIso8601String(),
    "citation_issue_timestamp": citationIssueTimestamp.toIso8601String(),
    "latitude": latitude,
    "payment_details": paymentDetails.toJson(),
    "payment_done": paymentDone,
    "longitude": longitude,
    "drive_off": driveOff,
    "tvr": tvr,
    "citation_id": citationId,
    "pbc_cancel": pbcCancel,
    "scofflaw_id": scofflawId,
    "audit_trail": List<dynamic>.from(auditTrail.map((x) => x.toJson())),
    "ro_request_status": roRequestStatus,
    "registered_ro_flag": registeredRoFlag,
    "registered_ro_detail": registeredRoDetail.toJson(),
    "has_appeal": hasAppeal,
    "shared_ticket": sharedTicket,
    "notice_detail": noticeDetail,
    "category": category,
    "payment_data": List<dynamic>.from(paymentData.map((x) => x)),
    "transaction_fee_data": Map.from(
      transactionFeeData,
    ).map((k, v) => MapEntry<String, dynamic>(k, v)),
    "sent_for_collection_at": sentForCollectionAt.toIso8601String(),
    "sent_for_collection": sentForCollection,
    "has_collection_fee": hasCollectionFee,
    "invoice_fee_structure": invoiceFeeStructure.toJson(),
    "nsf_fee": nsfFee,
    "collection_fee": collectionFee,
    "boot_tow_fee": bootTowFee,
    "charge_back_fee": chargeBackFee,
    "appeal_rejected_fee": appealRejectedFee,
    "hearing_rejected_fee": hearingRejectedFee,
    "reg_hold_fee": regHoldFee,
    "refund_data": refundData,
    "service_fee": serviceFee,
    "sales_tax": salesTax,
    "sales_tax_non_card_payments": salesTaxNonCardPayments,
    "net_amount": netAmount,
    "updated_at": updatedAt.toIso8601String(),
    "escalated_amount": escalatedAmount,
    "escalation_detail": escalationDetail,
    "late_fee_detail": lateFeeDetail,
    "balance_due": balanceDue,
    "hearing_details": hearingDetails.toJson(),
    "notification_audit_trail": notificationAuditTrail,
    "print_query": printQuery,
    "invoice_generated": invoiceGenerated,
    "ticket_first_notice_generated": ticketFirstNoticeGenerated,
    "late_invoice_generated": lateInvoiceGenerated,
    "appeal_eligible_date": appealEligibleDate.toIso8601String(),
    "court_notice_sent": courtNoticeSent,
    "sent_for_reg_holds": sentForRegHolds,
    "send_to_florida_sftp": sendToFloridaSftp,
    "administrative_fee": administrativeFee,
    "parking_fee": parkingFee,
    "hearing_date": hearingDate.toIso8601String(),
    "discount_fee": discountFee,
    "boot_instance_id": bootInstanceId,
    "cancelled_by": cancelledBy,
    "gateway_transaction_id": gatewayTransactionId,
    "username": username,
    "email": email,
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
    oldValue: json["OldValue"],
    newValue: json["NewValue"],
    updateType: json["UpdateType"],
    timestampUtc: DateTime.parse(json["TimestampUTC"]),
    timestamp: DateTime.parse(json["Timestamp"]),
    initiatorId: json["InitiatorID"],
    initiatorRole: json["InitiatorRole"],
    initiatorName: json["InitiatorName"],
    reason: json["Reason"],
    comment: json["Comment"],
  );

  Map<String, dynamic> toJson() => {
    "OldValue": oldValue,
    "NewValue": newValue,
    "UpdateType": updateType,
    "TimestampUTC": timestampUtc.toIso8601String(),
    "Timestamp": timestamp.toIso8601String(),
    "InitiatorID": initiatorId,
    "InitiatorRole": initiatorRole,
    "InitiatorName": initiatorName,
    "Reason": reason,
    "Comment": comment,
  };
}

class CommentDetails {
  String note1;
  dynamic note2;
  dynamic note3;
  String remark1;
  dynamic remark2;

  CommentDetails({
    required this.note1,
    required this.note2,
    required this.note3,
    required this.remark1,
    required this.remark2,
  });

  CommentDetails copyWith({
    String? note1,
    dynamic note2,
    dynamic note3,
    String? remark1,
    dynamic remark2,
  }) => CommentDetails(
    note1: note1 ?? this.note1,
    note2: note2 ?? this.note2,
    note3: note3 ?? this.note3,
    remark1: remark1 ?? this.remark1,
    remark2: remark2 ?? this.remark2,
  );

  factory CommentDetails.fromJson(Map<String, dynamic> json) => CommentDetails(
    note1: json["note_1"],
    note2: json["note_2"],
    note3: json["note_3"],
    remark1: json["remark_1"],
    remark2: json["remark_2"],
  );

  Map<String, dynamic> toJson() => {
    "note_1": note1,
    "note_2": note2,
    "note_3": note3,
    "remark_1": remark1,
    "remark_2": remark2,
  };
}

class HeaderDetails {
  String citationNumber;
  String timestamp;

  HeaderDetails({required this.citationNumber, required this.timestamp});

  HeaderDetails copyWith({String? citationNumber, String? timestamp}) =>
      HeaderDetails(
        citationNumber: citationNumber ?? this.citationNumber,
        timestamp: timestamp ?? this.timestamp,
      );

  factory HeaderDetails.fromJson(Map<String, dynamic> json) => HeaderDetails(
    citationNumber: json["citation_number"],
    timestamp: json["timestamp"],
  );

  Map<String, dynamic> toJson() => {
    "citation_number": citationNumber,
    "timestamp": timestamp,
  };
}

class HearingDetails {
  String comment;
  String status;
  String name;
  String hearingDateString;
  dynamic userInfo;

  HearingDetails({
    required this.comment,
    required this.status,
    required this.name,
    required this.hearingDateString,
    required this.userInfo,
  });

  HearingDetails copyWith({
    String? comment,
    String? status,
    String? name,
    String? hearingDateString,
    dynamic userInfo,
  }) => HearingDetails(
    comment: comment ?? this.comment,
    status: status ?? this.status,
    name: name ?? this.name,
    hearingDateString: hearingDateString ?? this.hearingDateString,
    userInfo: userInfo ?? this.userInfo,
  );

  factory HearingDetails.fromJson(Map<String, dynamic> json) => HearingDetails(
    comment: json["comment"],
    status: json["status"],
    name: json["name"],
    hearingDateString: json["hearing_date_string"],
    userInfo: json["user_info"],
  );

  Map<String, dynamic> toJson() => {
    "comment": comment,
    "status": status,
    "name": name,
    "hearing_date_string": hearingDateString,
    "user_info": userInfo,
  };
}

class InvoiceFeeStructure {
  int adminFee;
  int parkingFee;
  int totalFee;
  dynamic adminFeeUpdateAt;
  dynamic parkingFeeUpdateAt;
  CollectionFees collectionFees;
  bool hasCollectionFee;

  InvoiceFeeStructure({
    required this.adminFee,
    required this.parkingFee,
    required this.totalFee,
    required this.adminFeeUpdateAt,
    required this.parkingFeeUpdateAt,
    required this.collectionFees,
    required this.hasCollectionFee,
  });

  InvoiceFeeStructure copyWith({
    int? adminFee,
    int? parkingFee,
    int? totalFee,
    dynamic adminFeeUpdateAt,
    dynamic parkingFeeUpdateAt,
    CollectionFees? collectionFees,
    bool? hasCollectionFee,
  }) => InvoiceFeeStructure(
    adminFee: adminFee ?? this.adminFee,
    parkingFee: parkingFee ?? this.parkingFee,
    totalFee: totalFee ?? this.totalFee,
    adminFeeUpdateAt: adminFeeUpdateAt ?? this.adminFeeUpdateAt,
    parkingFeeUpdateAt: parkingFeeUpdateAt ?? this.parkingFeeUpdateAt,
    collectionFees: collectionFees ?? this.collectionFees,
    hasCollectionFee: hasCollectionFee ?? this.hasCollectionFee,
  );

  factory InvoiceFeeStructure.fromJson(Map<String, dynamic> json) =>
      InvoiceFeeStructure(
        adminFee: json["AdminFee"],
        parkingFee: json["ParkingFee"],
        totalFee: json["TotalFee"],
        adminFeeUpdateAt: json["AdminFeeUpdateAt"],
        parkingFeeUpdateAt: json["ParkingFeeUpdateAt"],
        collectionFees: CollectionFees.fromJson(json["collection_fees"]),
        hasCollectionFee: json["has_collection_fee"],
      );

  Map<String, dynamic> toJson() => {
    "AdminFee": adminFee,
    "ParkingFee": parkingFee,
    "TotalFee": totalFee,
    "AdminFeeUpdateAt": adminFeeUpdateAt,
    "ParkingFeeUpdateAt": parkingFeeUpdateAt,
    "collection_fees": collectionFees.toJson(),
    "has_collection_fee": hasCollectionFee,
  };
}

class CollectionFees {
  int originalFine;
  int collectionFee;

  CollectionFees({required this.originalFine, required this.collectionFee});

  CollectionFees copyWith({int? originalFine, int? collectionFee}) =>
      CollectionFees(
        originalFine: originalFine ?? this.originalFine,
        collectionFee: collectionFee ?? this.collectionFee,
      );

  factory CollectionFees.fromJson(Map<String, dynamic> json) => CollectionFees(
    originalFine: json["OriginalFine"],
    collectionFee: json["CollectionFee"],
  );

  Map<String, dynamic> toJson() => {
    "OriginalFine": originalFine,
    "CollectionFee": collectionFee,
  };
}

class Location {
  dynamic block;
  dynamic branchLotid;
  dynamic direction;
  dynamic impoundCode;
  String lot;
  dynamic lotLookupCode;
  dynamic meter;
  dynamic side;
  dynamic spaceId;
  String street;
  String streetLookupCode;

  Location({
    required this.block,
    required this.branchLotid,
    required this.direction,
    required this.impoundCode,
    required this.lot,
    required this.lotLookupCode,
    required this.meter,
    required this.side,
    required this.spaceId,
    required this.street,
    required this.streetLookupCode,
  });

  Location copyWith({
    dynamic block,
    dynamic branchLotid,
    dynamic direction,
    dynamic impoundCode,
    String? lot,
    dynamic lotLookupCode,
    dynamic meter,
    dynamic side,
    dynamic spaceId,
    String? street,
    String? streetLookupCode,
  }) => Location(
    block: block ?? this.block,
    branchLotid: branchLotid ?? this.branchLotid,
    direction: direction ?? this.direction,
    impoundCode: impoundCode ?? this.impoundCode,
    lot: lot ?? this.lot,
    lotLookupCode: lotLookupCode ?? this.lotLookupCode,
    meter: meter ?? this.meter,
    side: side ?? this.side,
    spaceId: spaceId ?? this.spaceId,
    street: street ?? this.street,
    streetLookupCode: streetLookupCode ?? this.streetLookupCode,
  );

  factory Location.fromJson(Map<String, dynamic> json) => Location(
    block: json["block"],
    branchLotid: json["branch_lotid"],
    direction: json["direction"],
    impoundCode: json["impound_code"],
    lot: json["lot"],
    lotLookupCode: json["lot_lookup_code"],
    meter: json["meter"],
    side: json["side"],
    spaceId: json["space_id"],
    street: json["street"],
    streetLookupCode: json["street_lookup_code"],
  );

  Map<String, dynamic> toJson() => {
    "block": block,
    "branch_lotid": branchLotid,
    "direction": direction,
    "impound_code": impoundCode,
    "lot": lot,
    "lot_lookup_code": lotLookupCode,
    "meter": meter,
    "side": side,
    "space_id": spaceId,
    "street": street,
    "street_lookup_code": streetLookupCode,
  };
}

class OfficerDetails {
  String agency;
  String badgeId;
  String beat;
  String deviceFriendlyName;
  String deviceId;
  String officerLookupCode;
  String officerName;
  dynamic peoFname;
  dynamic peoLname;
  dynamic peoName;
  String shift;
  String signature;
  String squad;
  String zone;

  OfficerDetails({
    required this.agency,
    required this.badgeId,
    required this.beat,
    required this.deviceFriendlyName,
    required this.deviceId,
    required this.officerLookupCode,
    required this.officerName,
    required this.peoFname,
    required this.peoLname,
    required this.peoName,
    required this.shift,
    required this.signature,
    required this.squad,
    required this.zone,
  });

  OfficerDetails copyWith({
    String? agency,
    String? badgeId,
    String? beat,
    String? deviceFriendlyName,
    String? deviceId,
    String? officerLookupCode,
    String? officerName,
    dynamic peoFname,
    dynamic peoLname,
    dynamic peoName,
    String? shift,
    String? signature,
    String? squad,
    String? zone,
  }) => OfficerDetails(
    agency: agency ?? this.agency,
    badgeId: badgeId ?? this.badgeId,
    beat: beat ?? this.beat,
    deviceFriendlyName: deviceFriendlyName ?? this.deviceFriendlyName,
    deviceId: deviceId ?? this.deviceId,
    officerLookupCode: officerLookupCode ?? this.officerLookupCode,
    officerName: officerName ?? this.officerName,
    peoFname: peoFname ?? this.peoFname,
    peoLname: peoLname ?? this.peoLname,
    peoName: peoName ?? this.peoName,
    shift: shift ?? this.shift,
    signature: signature ?? this.signature,
    squad: squad ?? this.squad,
    zone: zone ?? this.zone,
  );

  factory OfficerDetails.fromJson(Map<String, dynamic> json) => OfficerDetails(
    agency: json["agency"],
    badgeId: json["badge_id"],
    beat: json["beat"],
    deviceFriendlyName: json["device_friendly_name"],
    deviceId: json["device_id"],
    officerLookupCode: json["officer_lookup_code"],
    officerName: json["officer_name"],
    peoFname: json["peo_fname"],
    peoLname: json["peo_lname"],
    peoName: json["peo_name"],
    shift: json["shift"],
    signature: json["signature"],
    squad: json["squad"],
    zone: json["zone"],
  );

  Map<String, dynamic> toJson() => {
    "agency": agency,
    "badge_id": badgeId,
    "beat": beat,
    "device_friendly_name": deviceFriendlyName,
    "device_id": deviceId,
    "officer_lookup_code": officerLookupCode,
    "officer_name": officerName,
    "peo_fname": peoFname,
    "peo_lname": peoLname,
    "peo_name": peoName,
    "shift": shift,
    "signature": signature,
    "squad": squad,
    "zone": zone,
  };
}

class PaymentDetails {
  String paymentMode;
  DateTime paymentDatetime;
  int amount;
  String receiptId;
  String citationId;
  String paymentStatus;

  PaymentDetails({
    required this.paymentMode,
    required this.paymentDatetime,
    required this.amount,
    required this.receiptId,
    required this.citationId,
    required this.paymentStatus,
  });

  PaymentDetails copyWith({
    String? paymentMode,
    DateTime? paymentDatetime,
    int? amount,
    String? receiptId,
    String? citationId,
    String? paymentStatus,
  }) => PaymentDetails(
    paymentMode: paymentMode ?? this.paymentMode,
    paymentDatetime: paymentDatetime ?? this.paymentDatetime,
    amount: amount ?? this.amount,
    receiptId: receiptId ?? this.receiptId,
    citationId: citationId ?? this.citationId,
    paymentStatus: paymentStatus ?? this.paymentStatus,
  );

  factory PaymentDetails.fromJson(Map<String, dynamic> json) => PaymentDetails(
    paymentMode: json["payment_mode"],
    paymentDatetime: DateTime.parse(json["payment_datetime"]),
    amount: json["amount"],
    receiptId: json["receipt_id"],
    citationId: json["citation_id"],
    paymentStatus: json["payment_status"],
  );

  Map<String, dynamic> toJson() => {
    "payment_mode": paymentMode,
    "payment_datetime": paymentDatetime.toIso8601String(),
    "amount": amount,
    "receipt_id": receiptId,
    "citation_id": citationId,
    "payment_status": paymentStatus,
  };
}

class RegisteredRoDetail {
  String account;
  Address currentAddress;
  Address oldAddress;
  String mailReturnedRemark;
  bool mailReturned;
  String mailReturnedDate;
  dynamic requestDate;
  dynamic responseDate;

  RegisteredRoDetail({
    required this.account,
    required this.currentAddress,
    required this.oldAddress,
    required this.mailReturnedRemark,
    required this.mailReturned,
    required this.mailReturnedDate,
    required this.requestDate,
    required this.responseDate,
  });

  RegisteredRoDetail copyWith({
    String? account,
    Address? currentAddress,
    Address? oldAddress,
    String? mailReturnedRemark,
    bool? mailReturned,
    String? mailReturnedDate,
    dynamic requestDate,
    dynamic responseDate,
  }) => RegisteredRoDetail(
    account: account ?? this.account,
    currentAddress: currentAddress ?? this.currentAddress,
    oldAddress: oldAddress ?? this.oldAddress,
    mailReturnedRemark: mailReturnedRemark ?? this.mailReturnedRemark,
    mailReturned: mailReturned ?? this.mailReturned,
    mailReturnedDate: mailReturnedDate ?? this.mailReturnedDate,
    requestDate: requestDate ?? this.requestDate,
    responseDate: responseDate ?? this.responseDate,
  );

  factory RegisteredRoDetail.fromJson(Map<String, dynamic> json) =>
      RegisteredRoDetail(
        account: json["account"],
        currentAddress: Address.fromJson(json["current_address"]),
        oldAddress: Address.fromJson(json["old_address"]),
        mailReturnedRemark: json["mail_returned_remark"],
        mailReturned: json["mail_returned"],
        mailReturnedDate: json["mail_returned_date"],
        requestDate: json["request_date"],
        responseDate: json["response_date"],
      );

  Map<String, dynamic> toJson() => {
    "account": account,
    "current_address": currentAddress.toJson(),
    "old_address": oldAddress.toJson(),
    "mail_returned_remark": mailReturnedRemark,
    "mail_returned": mailReturned,
    "mail_returned_date": mailReturnedDate,
    "request_date": requestDate,
    "response_date": responseDate,
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

class VehicleDetails {
  String bodyStyle;
  String bodyStyleLookupCode;
  String color;
  String colorFull;
  dynamic decalNumber;
  dynamic decalYear;
  String licenseExpiry;
  String lpNumber;
  String make;
  String model;
  String modelLookupCode;
  String state;
  String vinNumber;

  VehicleDetails({
    required this.bodyStyle,
    required this.bodyStyleLookupCode,
    required this.color,
    required this.colorFull,
    required this.decalNumber,
    required this.decalYear,
    required this.licenseExpiry,
    required this.lpNumber,
    required this.make,
    required this.model,
    required this.modelLookupCode,
    required this.state,
    required this.vinNumber,
  });

  VehicleDetails copyWith({
    String? bodyStyle,
    String? bodyStyleLookupCode,
    String? color,
    String? colorFull,
    dynamic decalNumber,
    dynamic decalYear,
    String? licenseExpiry,
    String? lpNumber,
    String? make,
    String? model,
    String? modelLookupCode,
    String? state,
    String? vinNumber,
  }) => VehicleDetails(
    bodyStyle: bodyStyle ?? this.bodyStyle,
    bodyStyleLookupCode: bodyStyleLookupCode ?? this.bodyStyleLookupCode,
    color: color ?? this.color,
    colorFull: colorFull ?? this.colorFull,
    decalNumber: decalNumber ?? this.decalNumber,
    decalYear: decalYear ?? this.decalYear,
    licenseExpiry: licenseExpiry ?? this.licenseExpiry,
    lpNumber: lpNumber ?? this.lpNumber,
    make: make ?? this.make,
    model: model ?? this.model,
    modelLookupCode: modelLookupCode ?? this.modelLookupCode,
    state: state ?? this.state,
    vinNumber: vinNumber ?? this.vinNumber,
  );

  factory VehicleDetails.fromJson(Map<String, dynamic> json) => VehicleDetails(
    bodyStyle: json["body_style"],
    bodyStyleLookupCode: json["body_style_lookup_code"],
    color: json["color"],
    colorFull: json["color_full"],
    decalNumber: json["decal_number"],
    decalYear: json["decal_year"],
    licenseExpiry: json["license_expiry"],
    lpNumber: json["lp_number"],
    make: json["make"],
    model: json["model"],
    modelLookupCode: json["model_lookup_code"],
    state: json["state"],
    vinNumber: json["vin_number"],
  );

  Map<String, dynamic> toJson() => {
    "body_style": bodyStyle,
    "body_style_lookup_code": bodyStyleLookupCode,
    "color": color,
    "color_full": colorFull,
    "decal_number": decalNumber,
    "decal_year": decalYear,
    "license_expiry": licenseExpiry,
    "lp_number": lpNumber,
    "make": make,
    "model": model,
    "model_lookup_code": modelLookupCode,
    "state": state,
    "vin_number": vinNumber,
  };
}

class ViolationDetails {
  String code;
  dynamic cost;
  String description;
  int due15Days;
  int due30Days;
  int due45Days;
  dynamic exportCode;
  int fine;
  dynamic invoiceFeeStructure;
  int lateFine;
  dynamic sanctionsType;
  String vioType;
  dynamic vioTypeCode;
  dynamic vioTypeDescription;
  String violation;

  ViolationDetails({
    required this.code,
    required this.cost,
    required this.description,
    required this.due15Days,
    required this.due30Days,
    required this.due45Days,
    required this.exportCode,
    required this.fine,
    required this.invoiceFeeStructure,
    required this.lateFine,
    required this.sanctionsType,
    required this.vioType,
    required this.vioTypeCode,
    required this.vioTypeDescription,
    required this.violation,
  });

  ViolationDetails copyWith({
    String? code,
    dynamic cost,
    String? description,
    int? due15Days,
    int? due30Days,
    int? due45Days,
    dynamic exportCode,
    int? fine,
    dynamic invoiceFeeStructure,
    int? lateFine,
    dynamic sanctionsType,
    String? vioType,
    dynamic vioTypeCode,
    dynamic vioTypeDescription,
    String? violation,
  }) => ViolationDetails(
    code: code ?? this.code,
    cost: cost ?? this.cost,
    description: description ?? this.description,
    due15Days: due15Days ?? this.due15Days,
    due30Days: due30Days ?? this.due30Days,
    due45Days: due45Days ?? this.due45Days,
    exportCode: exportCode ?? this.exportCode,
    fine: fine ?? this.fine,
    invoiceFeeStructure: invoiceFeeStructure ?? this.invoiceFeeStructure,
    lateFine: lateFine ?? this.lateFine,
    sanctionsType: sanctionsType ?? this.sanctionsType,
    vioType: vioType ?? this.vioType,
    vioTypeCode: vioTypeCode ?? this.vioTypeCode,
    vioTypeDescription: vioTypeDescription ?? this.vioTypeDescription,
    violation: violation ?? this.violation,
  );

  factory ViolationDetails.fromJson(Map<String, dynamic> json) =>
      ViolationDetails(
        code: json["code"],
        cost: json["cost"],
        description: json["description"],
        due15Days: json["due_15_days"],
        due30Days: json["due_30_days"],
        due45Days: json["due_45_days"],
        exportCode: json["export_code"],
        fine: json["fine"],
        invoiceFeeStructure: json["invoice_fee_structure"],
        lateFine: json["late_fine"],
        sanctionsType: json["sanctions_type"],
        vioType: json["vio_type"],
        vioTypeCode: json["vio_type_code"],
        vioTypeDescription: json["vio_type_description"],
        violation: json["violation"],
      );

  Map<String, dynamic> toJson() => {
    "code": code,
    "cost": cost,
    "description": description,
    "due_15_days": due15Days,
    "due_30_days": due30Days,
    "due_45_days": due45Days,
    "export_code": exportCode,
    "fine": fine,
    "invoice_fee_structure": invoiceFeeStructure,
    "late_fine": lateFine,
    "sanctions_type": sanctionsType,
    "vio_type": vioType,
    "vio_type_code": vioTypeCode,
    "vio_type_description": vioTypeDescription,
    "violation": violation,
  };
}

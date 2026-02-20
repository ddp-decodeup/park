import 'dart:convert';

TicketCreationResponse ticketCreationResponseFromJson(String str) =>
    TicketCreationResponse.fromJson(json.decode(str));

String ticketCreationResponseToJson(TicketCreationResponse data) =>
    json.encode(data.toJson());

class TicketCreationResponse {
  final Data? data;
  final String? message;
  final Metadata? metadata;
  final bool? success;

  TicketCreationResponse({this.data, this.message, this.metadata, this.success});

  factory TicketCreationResponse.fromJson(Map<String, dynamic> json) {
    return TicketCreationResponse(
      data: json["data"] != null ? Data.fromJson(json["data"]) : null,
      message: json["message"] as String?,
      metadata:
      json["metadata"] != null ? Metadata.fromJson(json["metadata"]) : null,
      success: json["success"] as bool?,
    );
  }

  Map<String, dynamic> toJson() => {
    "data": data?.toJson(),
    "message": message,
    "metadata": metadata?.toJson(),
    "success": success,
  };
}

class Data {
  final String? id;
  final String? code;
  final String? status;
  final int? createdAt;
  final String? ticketNo;
  final int? fineAmount;
  final String? siteOfficerId;
  final String? type;
  final List<dynamic> images;
  final bool? dataVoid;

  final Location? location;
  final OfficerDetails? officerDetails;
  final VehicleDetails? vehicleDetails;
  final ViolationDetails? violationDetails;

  final dynamic motoristDetails;
  final CommentDetails? commentDetails;
  final HeaderDetails? headerDetails;

  final String? lpNumber;
  final bool? reissue;
  final String? timeLimitEnforcementId;
  final bool? timeLimitEnforcement;
  final String? timeLimitEnforcementObservedTime;

  final DateTime? citationStartTimestamp;
  final DateTime? citationIssueTimestamp;

  final double? latitude;
  final double? longitude;

  final dynamic paymentDetails;
  final bool? paymentDone;

  final bool? driveOff;
  final bool? tvr;
  final bool? pbcCancel;

  final String? citationId;
  final String? scofflawId;

  final DateTime? updatedAt;

  final dynamic escalationDetail;
  final dynamic lateFeeDetail;

  final bool? registeredRoFlag;
  final String? roRequestStatus;
  final RegisteredRoDetail? registeredRoDetail;

  final int? balanceDue;
  final bool? hasAppeal;
  final bool? sharedTicket;

  final HearingDetails? hearingDetails;
  final String? category;

  final InvoiceFeeStructure? invoiceFeeStructure;
  final String? printQuery;

  final DateTime? hearingDate;

  final List<dynamic> paymentData;

  final int? chargeBackFee;
  final int? appealRejectedFee;
  final int? hearingRejectedFee;
  final int? bootTowFee;
  final int? collectionFee;
  final int? nsfFee;

  final Map<String, int> transactionFeeData;
  final List<AuditTrail> auditTrail;

  final bool? sentForRegHolds;
  final int? regHoldFee;
  final bool? parkingBaseEmailReceived;

  Data({
    this.id,
    this.code,
    this.status,
    this.createdAt,
    this.ticketNo,
    this.fineAmount,
    this.siteOfficerId,
    this.type,
    required this.images,
    this.dataVoid,
    this.location,
    this.officerDetails,
    this.vehicleDetails,
    this.violationDetails,
    this.motoristDetails,
    this.commentDetails,
    this.headerDetails,
    this.lpNumber,
    this.reissue,
    this.timeLimitEnforcementId,
    this.timeLimitEnforcement,
    this.timeLimitEnforcementObservedTime,
    this.citationStartTimestamp,
    this.citationIssueTimestamp,
    this.latitude,
    this.longitude,
    this.paymentDetails,
    this.paymentDone,
    this.driveOff,
    this.tvr,
    this.pbcCancel,
    this.citationId,
    this.scofflawId,
    this.updatedAt,
    this.escalationDetail,
    this.lateFeeDetail,
    this.registeredRoFlag,
    this.roRequestStatus,
    this.registeredRoDetail,
    this.balanceDue,
    this.hasAppeal,
    this.sharedTicket,
    this.hearingDetails,
    this.category,
    this.invoiceFeeStructure,
    this.printQuery,
    this.hearingDate,
    required this.paymentData,
    this.chargeBackFee,
    this.appealRejectedFee,
    this.hearingRejectedFee,
    this.bootTowFee,
    this.collectionFee,
    this.nsfFee,
    required this.transactionFeeData,
    required this.auditTrail,
    this.sentForRegHolds,
    this.regHoldFee,
    this.parkingBaseEmailReceived,
  });

  factory Data.fromJson(Map<String, dynamic> json) {
    return Data(
      id: json["id"] as String?,
      code: json["code"] as String?,
      status: json["status"] as String?,
      createdAt: json["created_at"] as int?,
      ticketNo: json["ticket_no"] as String?,
      fineAmount: json["fine_amount"] as int?,
      siteOfficerId: json["site_officer_id"] as String?,
      type: json["type"] as String?,
      images: (json["images"] as List?) ?? [],
      dataVoid: json["void"] as bool?,
      location:
      json["location"] != null ? Location.fromJson(json["location"]) : null,
      officerDetails: json["officer_details"] != null
          ? OfficerDetails.fromJson(json["officer_details"])
          : null,
      vehicleDetails: json["vehicle_details"] != null
          ? VehicleDetails.fromJson(json["vehicle_details"])
          : null,
      violationDetails: json["violation_details"] != null
          ? ViolationDetails.fromJson(json["violation_details"])
          : null,
      motoristDetails: json["motorist_details"],
      commentDetails: json["comment_details"] != null
          ? CommentDetails.fromJson(json["comment_details"])
          : null,
      headerDetails: json["header_details"] != null
          ? HeaderDetails.fromJson(json["header_details"])
          : null,
      lpNumber: json["lp_number"] as String?,
      reissue: json["reissue"] as bool?,
      timeLimitEnforcementId:
      json["time_limit_enforcement_id"] as String?,
      timeLimitEnforcement: json["time_limit_enforcement"] as bool?,
      timeLimitEnforcementObservedTime:
      json["time_limit_enforcement_observed_time"] as String?,
      citationStartTimestamp: json["citation_start_timestamp"] != null
          ? DateTime.tryParse(json["citation_start_timestamp"])
          : null,
      citationIssueTimestamp: json["citation_issue_timestamp"] != null
          ? DateTime.tryParse(json["citation_issue_timestamp"])
          : null,
      latitude: (json["latitude"] as num?)?.toDouble(),
      longitude: (json["longitude"] as num?)?.toDouble(),
      paymentDetails: json["payment_details"],
      paymentDone: json["payment_done"] as bool?,
      driveOff: json["drive_off"] as bool?,
      tvr: json["tvr"] as bool?,
      pbcCancel: json["pbc_cancel"] as bool?,
      citationId: json["citation_id"] as String?,
      scofflawId: json["scofflaw_id"] as String?,
      updatedAt: json["updated_at"] != null
          ? DateTime.tryParse(json["updated_at"])
          : null,
      escalationDetail: json["escalation_detail"],
      lateFeeDetail: json["late_fee_detail"],
      registeredRoFlag: json["registered_ro_flag"] as bool?,
      roRequestStatus: json["ro_request_status"] as String?,
      registeredRoDetail: json["registered_ro_detail"] != null
          ? RegisteredRoDetail.fromJson(json["registered_ro_detail"])
          : null,
      balanceDue: json["balance_due"] as int?,
      hasAppeal: json["has_appeal"] as bool?,
      sharedTicket: json["shared_ticket"] as bool?,
      hearingDetails: json["hearing_details"] != null
          ? HearingDetails.fromJson(json["hearing_details"])
          : null,
      category: json["category"] as String?,
      invoiceFeeStructure: json["invoice_fee_structure"] != null
          ? InvoiceFeeStructure.fromJson(json["invoice_fee_structure"])
          : null,
      printQuery: json["print_query"] as String?,
      hearingDate: json["hearing_date"] != null
          ? DateTime.tryParse(json["hearing_date"])
          : null,
      paymentData: (json["PaymentData"] as List?) ?? [],
      chargeBackFee: json["charge_back_fee"] as int?,
      appealRejectedFee: json["appeal_rejected_fee"] as int?,
      hearingRejectedFee: json["hearing_rejected_fee"] as int?,
      bootTowFee: json["boot_tow_fee"] as int?,
      collectionFee: json["collection_fee"] as int?,
      nsfFee: json["nsf_fee"] as int?,
      transactionFeeData:
      (json["TransactionFeeData"] as Map?)?.map<String, int>(
            (k, v) => MapEntry(k.toString(), (v as num).toInt()),
      ) ??
          {},
      auditTrail: (json["audit_trail"] as List?)
          ?.map((e) => AuditTrail.fromJson(e))
          .toList() ??
          [],
      sentForRegHolds: json["sent_for_reg_holds"] as bool?,
      regHoldFee: json["reg_hold_fee"] as int?,
      parkingBaseEmailReceived:
      json["parking_base_email_received"] as bool?,
    );
  }

  Map<String, dynamic> toJson() => {
    "id": id,
    "code": code,
    "status": status,
    "created_at": createdAt,
    "ticket_no": ticketNo,
    "fine_amount": fineAmount,
    "site_officer_id": siteOfficerId,
    "type": type,
    "images": images,
    "void": dataVoid,
    "location": location?.toJson(),
    "officer_details": officerDetails?.toJson(),
    "vehicle_details": vehicleDetails?.toJson(),
    "violation_details": violationDetails?.toJson(),
    "motorist_details": motoristDetails,
    "comment_details": commentDetails?.toJson(),
    "header_details": headerDetails?.toJson(),
    "lp_number": lpNumber,
    "reissue": reissue,
    "time_limit_enforcement_id": timeLimitEnforcementId,
    "time_limit_enforcement": timeLimitEnforcement,
    "time_limit_enforcement_observed_time":
    timeLimitEnforcementObservedTime,
    "citation_start_timestamp":
    citationStartTimestamp?.toIso8601String(),
    "citation_issue_timestamp":
    citationIssueTimestamp?.toIso8601String(),
    "latitude": latitude,
    "longitude": longitude,
    "payment_details": paymentDetails,
    "payment_done": paymentDone,
    "drive_off": driveOff,
    "tvr": tvr,
    "pbc_cancel": pbcCancel,
    "citation_id": citationId,
    "scofflaw_id": scofflawId,
    "updated_at": updatedAt?.toIso8601String(),
    "escalation_detail": escalationDetail,
    "late_fee_detail": lateFeeDetail,
    "registered_ro_flag": registeredRoFlag,
    "ro_request_status": roRequestStatus,
    "registered_ro_detail": registeredRoDetail?.toJson(),
    "balance_due": balanceDue,
    "has_appeal": hasAppeal,
    "shared_ticket": sharedTicket,
    "hearing_details": hearingDetails?.toJson(),
    "category": category,
    "invoice_fee_structure": invoiceFeeStructure?.toJson(),
    "print_query": printQuery,
    "hearing_date": hearingDate?.toIso8601String(),
    "PaymentData": paymentData,
    "charge_back_fee": chargeBackFee,
    "appeal_rejected_fee": appealRejectedFee,
    "hearing_rejected_fee": hearingRejectedFee,
    "boot_tow_fee": bootTowFee,
    "collection_fee": collectionFee,
    "nsf_fee": nsfFee,
    "TransactionFeeData": transactionFeeData,
    "audit_trail": auditTrail.map((e) => e.toJson()).toList(),
    "sent_for_reg_holds": sentForRegHolds,
    "reg_hold_fee": regHoldFee,
    "parking_base_email_received": parkingBaseEmailReceived,
  };
}

// ===== Nested classes (all null-safe) =====

class AuditTrail {
  final String? oldValue;
  final String? newValue;
  final String? updateType;
  final DateTime? timestampUtc;
  final DateTime? timestamp;
  final String? initiatorId;
  final String? initiatorRole;
  final String? initiatorName;
  final String? reason;
  final String? comment;

  AuditTrail({
    this.oldValue,
    this.newValue,
    this.updateType,
    this.timestampUtc,
    this.timestamp,
    this.initiatorId,
    this.initiatorRole,
    this.initiatorName,
    this.reason,
    this.comment,
  });

  factory AuditTrail.fromJson(Map<String, dynamic> json) => AuditTrail(
    oldValue: json["OldValue"] as String?,
    newValue: json["NewValue"] as String?,
    updateType: json["UpdateType"] as String?,
    timestampUtc: json["TimestampUTC"] != null
        ? DateTime.tryParse(json["TimestampUTC"])
        : null,
    timestamp: json["Timestamp"] != null
        ? DateTime.tryParse(json["Timestamp"])
        : null,
    initiatorId: json["InitiatorID"] as String?,
    initiatorRole: json["InitiatorRole"] as String?,
    initiatorName: json["InitiatorName"] as String?,
    reason: json["Reason"] as String?,
    comment: json["Comment"] as String?,
  );

  Map<String, dynamic> toJson() => {
    "OldValue": oldValue,
    "NewValue": newValue,
    "UpdateType": updateType,
    "TimestampUTC": timestampUtc?.toIso8601String(),
    "Timestamp": timestamp?.toIso8601String(),
    "InitiatorID": initiatorId,
    "InitiatorRole": initiatorRole,
    "InitiatorName": initiatorName,
    "Reason": reason,
    "Comment": comment,
  };
}

class CommentDetails {
  final String? note1;
  final String? note2;
  final String? note3;
  final String? remark1;
  final String? remark2;

  CommentDetails({this.note1, this.note2, this.note3, this.remark1, this.remark2});

  factory CommentDetails.fromJson(Map<String, dynamic> json) => CommentDetails(
    note1: json["note_1"] as String?,
    note2: json["note_2"] as String?,
    note3: json["note_3"] as String?,
    remark1: json["remark_1"] as String?,
    remark2: json["remark_2"] as String?,
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
  final String? citationNumber;
  final String? timestamp;

  HeaderDetails({this.citationNumber, this.timestamp});

  factory HeaderDetails.fromJson(Map<String, dynamic> json) => HeaderDetails(
    citationNumber: json["citation_number"] as String?,
    timestamp: json["timestamp"] as String?,
  );

  Map<String, dynamic> toJson() => {
    "citation_number": citationNumber,
    "timestamp": timestamp,
  };
}

class HearingDetails {
  final String? comment;
  final String? status;
  final String? name;
  final String? hearingDateString;
  final dynamic userInfo;

  HearingDetails(
      {this.comment,
        this.status,
        this.name,
        this.hearingDateString,
        this.userInfo});

  factory HearingDetails.fromJson(Map<String, dynamic> json) => HearingDetails(
    comment: json["comment"] as String?,
    status: json["status"] as String?,
    name: json["name"] as String?,
    hearingDateString: json["hearing_date_string"] as String?,
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
  final int? citationFee;
  final int? parkingFee;
  final int? saleTax;

  InvoiceFeeStructure({this.citationFee, this.parkingFee, this.saleTax});

  factory InvoiceFeeStructure.fromJson(Map<String, dynamic> json) =>
      InvoiceFeeStructure(
        citationFee: json["citation_fee"] as int?,
        parkingFee: json["parking_fee"] as int?,
        saleTax: json["sale_tax"] as int?,
      );

  Map<String, dynamic> toJson() => {
    "citation_fee": citationFee,
    "parking_fee": parkingFee,
    "sale_tax": saleTax,
  };
}

class Location {
  final String? block;
  final String? branchLotid;
  final String? direction;
  final dynamic impoundCode;
  final String? lot;
  final String? lotLookupCode;
  final String? meter;
  final String? side;
  final String? spaceId;
  final String? street;
  final String? streetLookupCode;

  Location(
      {this.block,
        this.branchLotid,
        this.direction,
        this.impoundCode,
        this.lot,
        this.lotLookupCode,
        this.meter,
        this.side,
        this.spaceId,
        this.street,
        this.streetLookupCode});

  factory Location.fromJson(Map<String, dynamic> json) => Location(
    block: json["block"] as String?,
    branchLotid: json["branch_lotid"] as String?,
    direction: json["direction"] as String?,
    impoundCode: json["impound_code"],
    lot: json["lot"] as String?,
    lotLookupCode: json["lot_lookup_code"] as String?,
    meter: json["meter"] as String?,
    side: json["side"] as String?,
    spaceId: json["space_id"] as String?,
    street: json["street"] as String?,
    streetLookupCode: json["street_lookup_code"] as String?,
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
  final String? agency;
  final String? badgeId;
  final String? beat;
  final String? deviceFriendlyName;
  final String? deviceId;
  final String? officerLookupCode;
  final String? officerName;
  final dynamic peoFname;
  final dynamic peoLname;
  final dynamic peoName;
  final String? shift;
  final String? signature;
  final String? squad;
  final String? zone;

  OfficerDetails(
      {this.agency,
        this.badgeId,
        this.beat,
        this.deviceFriendlyName,
        this.deviceId,
        this.officerLookupCode,
        this.officerName,
        this.peoFname,
        this.peoLname,
        this.peoName,
        this.shift,
        this.signature,
        this.squad,
        this.zone});

  factory OfficerDetails.fromJson(Map<String, dynamic> json) => OfficerDetails(
    agency: json["agency"] as String?,
    badgeId: json["badge_id"] as String?,
    beat: json["beat"] as String?,
    deviceFriendlyName: json["device_friendly_name"] as String?,
    deviceId: json["device_id"] as String?,
    officerLookupCode: json["officer_lookup_code"] as String?,
    officerName: json["officer_name"] as String?,
    peoFname: json["peo_fname"],
    peoLname: json["peo_lname"],
    peoName: json["peo_name"],
    shift: json["shift"] as String?,
    signature: json["signature"] as String?,
    squad: json["squad"] as String?,
    zone: json["zone"] as String?,
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

class RegisteredRoDetail {
  final String? account;
  final Address? currentAddress;
  final Address? oldAddress;
  final String? mailReturnedRemark;
  final bool? mailReturned;
  final String? mailReturnedDate;
  final dynamic requestDate;
  final dynamic responseDate;

  RegisteredRoDetail(
      {this.account,
        this.currentAddress,
        this.oldAddress,
        this.mailReturnedRemark,
        this.mailReturned,
        this.mailReturnedDate,
        this.requestDate,
        this.responseDate});

  factory RegisteredRoDetail.fromJson(Map<String, dynamic> json) =>
      RegisteredRoDetail(
        account: json["account"] as String?,
        currentAddress: json["current_address"] != null
            ? Address.fromJson(json["current_address"])
            : null,
        oldAddress: json["old_address"] != null
            ? Address.fromJson(json["old_address"])
            : null,
        mailReturnedRemark: json["mail_returned_remark"] as String?,
        mailReturned: json["mail_returned"] as bool?,
        mailReturnedDate: json["mail_returned_date"] as String?,
        requestDate: json["request_date"],
        responseDate: json["response_date"],
      );

  Map<String, dynamic> toJson() => {
    "account": account,
    "current_address": currentAddress?.toJson(),
    "old_address": oldAddress?.toJson(),
    "mail_returned_remark": mailReturnedRemark,
    "mail_returned": mailReturned,
    "mail_returned_date": mailReturnedDate,
    "request_date": requestDate,
    "response_date": responseDate,
  };
}

class Address {
  final String? name;
  final String? address;
  final String? city;
  final String? state;
  final String? zip;

  Address({this.name, this.address, this.city, this.state, this.zip});

  factory Address.fromJson(Map<String, dynamic> json) => Address(
    name: json["name"] as String?,
    address: json["address"] as String?,
    city: json["city"] as String?,
    state: json["state"] as String?,
    zip: json["zip"] as String?,
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
  final String? bodyStyle;
  final String? bodyStyleLookupCode;
  final String? color;
  final String? decalNumber;
  final String? decalYear;
  final String? licenseExpiry;
  final String? lpNumber;
  final String? make;
  final String? model;
  final String? modelLookupCode;
  final String? state;
  final String? vinNumber;

  VehicleDetails(
      {this.bodyStyle,
        this.bodyStyleLookupCode,
        this.color,
        this.decalNumber,
        this.decalYear,
        this.licenseExpiry,
        this.lpNumber,
        this.make,
        this.model,
        this.modelLookupCode,
        this.state,
        this.vinNumber});

  factory VehicleDetails.fromJson(Map<String, dynamic> json) => VehicleDetails(
    bodyStyle: json["body_style"] as String?,
    bodyStyleLookupCode: json["body_style_lookup_code"] as String?,
    color: json["color"] as String?,
    decalNumber: json["decal_number"] as String?,
    decalYear: json["decal_year"] as String?,
    licenseExpiry: json["license_expiry"] as String?,
    lpNumber: json["lp_number"] as String?,
    make: json["make"] as String?,
    model: json["model"] as String?,
    modelLookupCode: json["model_lookup_code"] as String?,
    state: json["state"] as String?,
    vinNumber: json["vin_number"] as String?,
  );

  Map<String, dynamic> toJson() => {
    "body_style": bodyStyle,
    "body_style_lookup_code": bodyStyleLookupCode,
    "color": color,
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
  final String? code;
  final int? cost;
  final String? description;
  final int? due15Days;
  final int? due30Days;
  final int? due45Days;
  final String? exportCode;
  final int? fine;
  final dynamic invoiceFeeStructure;
  final int? lateFine;
  final String? sanctionsType;
  final String? vioType;
  final String? vioTypeCode;
  final String? vioTypeDescription;
  final String? violation;

  ViolationDetails(
      {this.code,
        this.cost,
        this.description,
        this.due15Days,
        this.due30Days,
        this.due45Days,
        this.exportCode,
        this.fine,
        this.invoiceFeeStructure,
        this.lateFine,
        this.sanctionsType,
        this.vioType,
        this.vioTypeCode,
        this.vioTypeDescription,
        this.violation});

  factory ViolationDetails.fromJson(Map<String, dynamic> json) =>
      ViolationDetails(
        code: json["code"] as String?,
        cost: json["cost"] as int?,
        description: json["description"] as String?,
        due15Days: json["due_15_days"] as int?,
        due30Days: json["due_30_days"] as int?,
        due45Days: json["due_45_days"] as int?,
        exportCode: json["export_code"] as String?,
        fine: json["fine"] as int?,
        invoiceFeeStructure: json["invoice_fee_structure"],
        lateFine: json["late_fine"] as int?,
        sanctionsType: json["sanctions_type"] as String?,
        vioType: json["vio_type"] as String?,
        vioTypeCode: json["vio_type_code"] as String?,
        vioTypeDescription: json["vio_type_description"] as String?,
        violation: json["violation"] as String?,
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

class Metadata {
  final CommentDetails? commentDetails;
  final OfficerDetails? officerDetails;
  final VehicleDetails? vehicleDetails;
  final ViolationDetails? violationDetails;

  Metadata(
      {this.commentDetails,
        this.officerDetails,
        this.vehicleDetails,
        this.violationDetails});

  factory Metadata.fromJson(Map<String, dynamic> json) => Metadata(
    commentDetails: json["comment_details"] != null
        ? CommentDetails.fromJson(json["comment_details"])
        : null,
    officerDetails: json["officer_details"] != null
        ? OfficerDetails.fromJson(json["officer_details"])
        : null,
    vehicleDetails: json["vehicle_details"] != null
        ? VehicleDetails.fromJson(json["vehicle_details"])
        : null,
    violationDetails: json["violation_details"] != null
        ? ViolationDetails.fromJson(json["violation_details"])
        : null,
  );

  Map<String, dynamic> toJson() => {
    "comment_details": commentDetails?.toJson(),
    "officer_details": officerDetails?.toJson(),
    "vehicle_details": vehicleDetails?.toJson(),
    "violation_details": violationDetails?.toJson(),
  };
}

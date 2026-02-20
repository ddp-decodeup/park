// To parse this JSON data, do
//
//     final municipalCitationCreationModel = municipalCitationCreationModelFromJson(jsonString);

import 'dart:convert';

MunicipalCitationCreationModel municipalCitationCreationModelFromJson(String str) => MunicipalCitationCreationModel.fromJson(json.decode(str));

String municipalCitationCreationModelToJson(MunicipalCitationCreationModel data) => json.encode(data.toJson());

class MunicipalCitationCreationModel {
  final String? code;
  final String? hearingDate;
  final List<String>? imageUrls;
  final LocationDetails? locationDetails;
  final String? lpNumber;
  final String? notes;
  final OfficerDetails? officerDetails;
  final CommentDetails? commentDetails;
  final InvoiceFeeStructure? invoiceFeeStructure;
  final String? ticketNo;
  final String? type;
  final String? status;
  final String? timeLimitEnforcementObservedTime;
  final VehicleDetails? vehicleDetails;
  final ViolationDetails? violationDetails;
  final HeaderDetails? headerDetails;
  final MotoristDetails? motoristDetails;
  final DateTime? citationIssueTimestamp;
  final DateTime? citationStartTimestamp;
  final bool? reissue;
  final bool? timeLimitEnforcement;
  final String? timeLimitEnforcementId;
  final String? latitude;
  final String? longitude;
  final String? printQuery;
  final String? category;

  MunicipalCitationCreationModel({
    this.code,
    this.hearingDate,
    this.imageUrls,
    this.locationDetails,
    this.lpNumber,
    this.notes,
    this.officerDetails,
    this.commentDetails,
    this.invoiceFeeStructure,
    this.ticketNo,
    this.type,
    this.status,
    this.timeLimitEnforcementObservedTime,
    this.vehicleDetails,
    this.violationDetails,
    this.headerDetails,
    this.motoristDetails,
    this.citationIssueTimestamp,
    this.citationStartTimestamp,
    this.reissue,
    this.timeLimitEnforcement,
    this.timeLimitEnforcementId,
    this.latitude,
    this.longitude,
    this.printQuery,
    this.category,
  });

  MunicipalCitationCreationModel copyWith({
    String? code,
    String? hearingDate,
    List<String>? imageUrls,
    LocationDetails? locationDetails,
    String? lpNumber,
    String? notes,
    OfficerDetails? officerDetails,
    CommentDetails? commentDetails,
    InvoiceFeeStructure? invoiceFeeStructure,
    String? ticketNo,
    String? type,
    String? status,
    String? timeLimitEnforcementObservedTime,
    VehicleDetails? vehicleDetails,
    ViolationDetails? violationDetails,
    HeaderDetails? headerDetails,
    MotoristDetails? motoristDetails,
    DateTime? citationIssueTimestamp,
    DateTime? citationStartTimestamp,
    bool? reissue,
    bool? timeLimitEnforcement,
    String? timeLimitEnforcementId,
    String? latitude,
    String? longitude,
    String? printQuery,
    String? category,
  }) =>
      MunicipalCitationCreationModel(
        code: code ?? this.code,
        hearingDate: hearingDate ?? this.hearingDate,
        imageUrls: imageUrls ?? this.imageUrls,
        locationDetails: locationDetails ?? this.locationDetails,
        lpNumber: lpNumber ?? this.lpNumber,
        notes: notes ?? this.notes,
        officerDetails: officerDetails ?? this.officerDetails,
        commentDetails: commentDetails ?? this.commentDetails,
        invoiceFeeStructure: invoiceFeeStructure ?? this.invoiceFeeStructure,
        ticketNo: ticketNo ?? this.ticketNo,
        type: type ?? this.type,
        status: status ?? this.status,
        timeLimitEnforcementObservedTime: timeLimitEnforcementObservedTime ?? this.timeLimitEnforcementObservedTime,
        vehicleDetails: vehicleDetails ?? this.vehicleDetails,
        violationDetails: violationDetails ?? this.violationDetails,
        headerDetails: headerDetails ?? this.headerDetails,
        motoristDetails: motoristDetails ?? this.motoristDetails,
        citationIssueTimestamp: citationIssueTimestamp ?? this.citationIssueTimestamp,
        citationStartTimestamp: citationStartTimestamp ?? this.citationStartTimestamp,
        reissue: reissue ?? this.reissue,
        timeLimitEnforcement: timeLimitEnforcement ?? this.timeLimitEnforcement,
        timeLimitEnforcementId: timeLimitEnforcementId ?? this.timeLimitEnforcementId,
        latitude: latitude ?? this.latitude,
        longitude: longitude ?? this.longitude,
        printQuery: printQuery ?? this.printQuery,
        category: category ?? this.category,
      );

  factory MunicipalCitationCreationModel.fromJson(Map<String, dynamic> json) => MunicipalCitationCreationModel(
    code: json["code"],
    hearingDate: json["hearing_date"],
    imageUrls: json["image_urls"] == null ? [] : List<String>.from(json["image_urls"]!.map((x) => x)),
    locationDetails: json["location_details"] == null ? null : LocationDetails.fromJson(json["location_details"]),
    lpNumber: json["lp_number"],
    notes: json["notes"],
    officerDetails: json["officer_details"] == null ? null : OfficerDetails.fromJson(json["officer_details"]),
    commentDetails: json["comment_details"] == null ? null : CommentDetails.fromJson(json["comment_details"]),
    invoiceFeeStructure: json["invoice_fee_structure"] == null ? null : InvoiceFeeStructure.fromJson(json["invoice_fee_structure"]),
    ticketNo: json["ticket_no"],
    type: json["type"],
    status: json["status"],
    timeLimitEnforcementObservedTime: json["time_limit_enforcement_observed_time"],
    vehicleDetails: json["vehicle_details"] == null ? null : VehicleDetails.fromJson(json["vehicle_details"]),
    violationDetails: json["violation_details"] == null ? null : ViolationDetails.fromJson(json["violation_details"]),
    headerDetails: json["header_details"] == null ? null : HeaderDetails.fromJson(json["header_details"]),
    motoristDetails: json["motorist_details"] == null ? null : MotoristDetails.fromJson(json["motorist_details"]),
    citationIssueTimestamp: json["citation_issue_timestamp"] == null ? null : DateTime.parse(json["citation_issue_timestamp"]),
    citationStartTimestamp: json["citation_start_timestamp"] == null ? null : DateTime.parse(json["citation_start_timestamp"]),
    reissue: json["reissue"],
    timeLimitEnforcement: json["time_limit_enforcement"],
    timeLimitEnforcementId: json["time_limit_enforcement_id"],
    latitude: json["latitude"]?.toDouble(),
    longitude: json["longitude"]?.toDouble(),
    printQuery: json["print_query"],
    category: json["category"],
  );

  Map<String, dynamic> toJson() => {
    "code": code,
    "hearing_date": hearingDate,
    "image_urls": imageUrls == null ? [] : List<dynamic>.from(imageUrls!.map((x) => x)),
    "location_details": locationDetails?.toJson(),
    "lp_number": lpNumber,
    "notes": notes,
    "officer_details": officerDetails?.toJson(),
    "comment_details": commentDetails?.toJson(),
    "invoice_fee_structure": invoiceFeeStructure?.toJson(),
    "ticket_no": ticketNo,
    "type": type,
    "status": status,
    "time_limit_enforcement_observed_time": timeLimitEnforcementObservedTime,
    "vehicle_details": vehicleDetails?.toJson(),
    "violation_details": violationDetails?.toJson(),
    "header_details": headerDetails?.toJson(),
    "motorist_details": motoristDetails?.toJson(),
    "citation_issue_timestamp": citationIssueTimestamp?.toIso8601String(),
    "citation_start_timestamp": citationStartTimestamp?.toIso8601String(),
    "reissue": reissue,
    "time_limit_enforcement": timeLimitEnforcement,
    "time_limit_enforcement_id": timeLimitEnforcementId,
    "latitude": latitude,
    "longitude": longitude,
    "print_query": printQuery,
    "category": category,
  };
}

class CommentDetails {
  final String? note1;
  final String? note2;
  final String? note3;
  final String? remark1;
  final String? remark2;

  CommentDetails({
    this.note1,
    this.note2,
    this.note3,
    this.remark1,
    this.remark2,
  });

  CommentDetails copyWith({
    String? note1,
    String? note2,
    String? note3,
    String? remark1,
    String? remark2,
  }) =>
      CommentDetails(
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
  final String? citationNumber;
  final String? timestamp;

  HeaderDetails({
    this.citationNumber,
    this.timestamp,
  });

  HeaderDetails copyWith({
    String? citationNumber,
    String? timestamp,
  }) =>
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

class InvoiceFeeStructure {
  final String? parkingFee;
  final String? citationFee;
  final String? saleTax;

  InvoiceFeeStructure({
    this.parkingFee,
    this.citationFee,
    this.saleTax,
  });

  InvoiceFeeStructure copyWith({
    String? parkingFee,
    String? citationFee,
    String? saleTax,
  }) =>
      InvoiceFeeStructure(
        parkingFee: parkingFee ?? this.parkingFee,
        citationFee: citationFee ?? this.citationFee,
        saleTax: saleTax ?? this.saleTax,
      );

  factory InvoiceFeeStructure.fromJson(Map<String, dynamic> json) => InvoiceFeeStructure(
    parkingFee: json["parking_fee"],
    citationFee: json["citation_fee"],
    saleTax: json["sale_tax"],
  );

  Map<String, dynamic> toJson() => {
    "parking_fee": parkingFee,
    "citation_fee": citationFee,
    "sale_tax": saleTax,
  };
}

class LocationDetails {
  final String? block;
  final String? meter;
  final String? side;
  final String? direction;
  final String? lot;
  final String? street;
  final String? streetLookupCode;
  final String? spaceId;
  final String? impoundCode;

  LocationDetails({
    this.block,
    this.meter,
    this.side,
    this.direction,
    this.lot,
    this.street,
    this.streetLookupCode,
    this.spaceId,
    this.impoundCode,
  });

  LocationDetails copyWith({
    String? block,
    String? meter,
    String? side,
    String? direction,
    String? lot,
    String? street,
    String? streetLookupCode,
    String? spaceId,
    String? impoundCode,
  }) =>
      LocationDetails(
        block: block ?? this.block,
        meter: meter ?? this.meter,
        side: side ?? this.side,
        direction: direction ?? this.direction,
        lot: lot ?? this.lot,
        street: street ?? this.street,
        streetLookupCode: streetLookupCode ?? this.streetLookupCode,
        spaceId: spaceId ?? this.spaceId,
        impoundCode: impoundCode ?? this.impoundCode,
      );

  factory LocationDetails.fromJson(Map<String, dynamic> json) => LocationDetails(
    block: json["block"],
    meter: json["meter"],
    side: json["side"],
    direction: json["direction"],
    lot: json["lot"],
    street: json["street"],
    streetLookupCode: json["street_lookup_code"],
    spaceId: json["space_id"],
    impoundCode: json["impound_code"],
  );

  Map<String, dynamic> toJson() => {
    "block": block,
    "meter": meter,
    "side": side,
    "direction": direction,
    "lot": lot,
    "street": street,
    "street_lookup_code": streetLookupCode,
    "space_id": spaceId,
    "impound_code": impoundCode,
  };
}

class MotoristDetails {
  final String? motoristFirstName;
  final String? motoristMiddleName;
  final String? motoristLastName;
  final String? motoristDateOfBirth;
  final String? motoristDlNumber;
  final String? motoristAddressBlock;
  final String? motoristAddressStreet;
  final String? motoristAddressCity;
  final String? motoristAddressState;
  final String? motoristAddressZip;

  MotoristDetails({
    this.motoristFirstName,
    this.motoristMiddleName,
    this.motoristLastName,
    this.motoristDateOfBirth,
    this.motoristDlNumber,
    this.motoristAddressBlock,
    this.motoristAddressStreet,
    this.motoristAddressCity,
    this.motoristAddressState,
    this.motoristAddressZip,
  });

  MotoristDetails copyWith({
    String? motoristFirstName,
    String? motoristMiddleName,
    String? motoristLastName,
    String? motoristDateOfBirth,
    String? motoristDlNumber,
    String? motoristAddressBlock,
    String? motoristAddressStreet,
    String? motoristAddressCity,
    String? motoristAddressState,
    String? motoristAddressZip,
  }) =>
      MotoristDetails(
        motoristFirstName: motoristFirstName ?? this.motoristFirstName,
        motoristMiddleName: motoristMiddleName ?? this.motoristMiddleName,
        motoristLastName: motoristLastName ?? this.motoristLastName,
        motoristDateOfBirth: motoristDateOfBirth ?? this.motoristDateOfBirth,
        motoristDlNumber: motoristDlNumber ?? this.motoristDlNumber,
        motoristAddressBlock: motoristAddressBlock ?? this.motoristAddressBlock,
        motoristAddressStreet: motoristAddressStreet ?? this.motoristAddressStreet,
        motoristAddressCity: motoristAddressCity ?? this.motoristAddressCity,
        motoristAddressState: motoristAddressState ?? this.motoristAddressState,
        motoristAddressZip: motoristAddressZip ?? this.motoristAddressZip,
      );

  factory MotoristDetails.fromJson(Map<String, dynamic> json) => MotoristDetails(
    motoristFirstName: json["motorist_first_name"],
    motoristMiddleName: json["motorist_middle_name"],
    motoristLastName: json["motorist_last_name"],
    motoristDateOfBirth: json["motorist_date_of_birth"],
    motoristDlNumber: json["motorist_dl_number"],
    motoristAddressBlock: json["motorist_address_block"],
    motoristAddressStreet: json["motorist_address_street"],
    motoristAddressCity: json["motorist_address_city"],
    motoristAddressState: json["motorist_address_state"],
    motoristAddressZip: json["motorist_address_zip"],
  );

  Map<String, dynamic> toJson() => {
    "motorist_first_name": motoristFirstName,
    "motorist_middle_name": motoristMiddleName,
    "motorist_last_name": motoristLastName,
    "motorist_date_of_birth": motoristDateOfBirth,
    "motorist_dl_number": motoristDlNumber,
    "motorist_address_block": motoristAddressBlock,
    "motorist_address_street": motoristAddressStreet,
    "motorist_address_city": motoristAddressCity,
    "motorist_address_state": motoristAddressState,
    "motorist_address_zip": motoristAddressZip,
  };
}

class OfficerDetails {
  final String? agency;
  final String? badgeId;
  final String? officerLookupCode;
  final String? beat;
  final String? officerName;
  final String? peoFname;
  final String? peoLname;
  final String? peoName;
  final String? squad;
  final String? zone;
  final String? signature;
  final String? shift;
  final String? deviceId;
  final String? deviceFriendlyName;

  OfficerDetails({
    this.agency,
    this.badgeId,
    this.officerLookupCode,
    this.beat,
    this.officerName,
    this.peoFname,
    this.peoLname,
    this.peoName,
    this.squad,
    this.zone,
    this.signature,
    this.shift,
    this.deviceId,
    this.deviceFriendlyName,
  });

  OfficerDetails copyWith({
    String? agency,
    String? badgeId,
    String? officerLookupCode,
    String? beat,
    String? officerName,
    String? peoFname,
    String? peoLname,
    String? peoName,
    String? squad,
    String? zone,
    String? signature,
    String? shift,
    String? deviceId,
    String? deviceFriendlyName,
  }) =>
      OfficerDetails(
        agency: agency ?? this.agency,
        badgeId: badgeId ?? this.badgeId,
        officerLookupCode: officerLookupCode ?? this.officerLookupCode,
        beat: beat ?? this.beat,
        officerName: officerName ?? this.officerName,
        peoFname: peoFname ?? this.peoFname,
        peoLname: peoLname ?? this.peoLname,
        peoName: peoName ?? this.peoName,
        squad: squad ?? this.squad,
        zone: zone ?? this.zone,
        signature: signature ?? this.signature,
        shift: shift ?? this.shift,
        deviceId: deviceId ?? this.deviceId,
        deviceFriendlyName: deviceFriendlyName ?? this.deviceFriendlyName,
      );

  factory OfficerDetails.fromJson(Map<String, dynamic> json) => OfficerDetails(
    agency: json["agency"],
    badgeId: json["badge_id"],
    officerLookupCode: json["officer_lookup_code"],
    beat: json["beat"],
    officerName: json["officer_name"],
    peoFname: json["peo_fname"],
    peoLname: json["peo_lname"],
    peoName: json["peo_name"],
    squad: json["squad"],
    zone: json["zone"],
    signature: json["signature"],
    shift: json["shift"],
    deviceId: json["device_id"],
    deviceFriendlyName: json["device_friendly_name"],
  );

  Map<String, dynamic> toJson() => {
    "agency": agency,
    "badge_id": badgeId,
    "officer_lookup_code": officerLookupCode,
    "beat": beat,
    "officer_name": officerName,
    "peo_fname": peoFname,
    "peo_lname": peoLname,
    "peo_name": peoName,
    "squad": squad,
    "zone": zone,
    "signature": signature,
    "shift": shift,
    "device_id": deviceId,
    "device_friendly_name": deviceFriendlyName,
  };
}

class VehicleDetails {
  final String? color;
  final String? lpNumber;
  final String? make;
  final String? bodyStyle;
  final String? bodyStyleLookupCode;
  final String? decalYear;
  final String? decalNumber;
  final String? vinNumber;
  final String? model;
  final String? modelLookupCode;
  final String? state;
  final String? licenseExpiry;

  VehicleDetails({
    this.color,
    this.lpNumber,
    this.make,
    this.bodyStyle,
    this.bodyStyleLookupCode,
    this.decalYear,
    this.decalNumber,
    this.vinNumber,
    this.model,
    this.modelLookupCode,
    this.state,
    this.licenseExpiry,
  });

  VehicleDetails copyWith({
    String? color,
    String? lpNumber,
    String? make,
    String? bodyStyle,
    String? bodyStyleLookupCode,
    String? decalYear,
    String? decalNumber,
    String? vinNumber,
    String? model,
    String? modelLookupCode,
    String? state,
    String? licenseExpiry,
  }) =>
      VehicleDetails(
        color: color ?? this.color,
        lpNumber: lpNumber ?? this.lpNumber,
        make: make ?? this.make,
        bodyStyle: bodyStyle ?? this.bodyStyle,
        bodyStyleLookupCode: bodyStyleLookupCode ?? this.bodyStyleLookupCode,
        decalYear: decalYear ?? this.decalYear,
        decalNumber: decalNumber ?? this.decalNumber,
        vinNumber: vinNumber ?? this.vinNumber,
        model: model ?? this.model,
        modelLookupCode: modelLookupCode ?? this.modelLookupCode,
        state: state ?? this.state,
        licenseExpiry: licenseExpiry ?? this.licenseExpiry,
      );

  factory VehicleDetails.fromJson(Map<String, dynamic> json) => VehicleDetails(
    color: json["color"],
    lpNumber: json["lp_number"],
    make: json["make"],
    bodyStyle: json["body_style"],
    bodyStyleLookupCode: json["body_style_lookup_code"],
    decalYear: json["decal_year"],
    decalNumber: json["decal_number"],
    vinNumber: json["vin_number"],
    model: json["model"],
    modelLookupCode: json["model_lookup_code"],
    state: json["state"],
    licenseExpiry: json["license_expiry"],
  );

  Map<String, dynamic> toJson() => {
    "color": color,
    "lp_number": lpNumber,
    "make": make,
    "body_style": bodyStyle,
    "body_style_lookup_code": bodyStyleLookupCode,
    "decal_year": decalYear,
    "decal_number": decalNumber,
    "vin_number": vinNumber,
    "model": model,
    "model_lookup_code": modelLookupCode,
    "state": state,
    "license_expiry": licenseExpiry,
  };
}

class ViolationDetails {
  final String? violation;
  final String? code;
  final String? description;
  final String? fine;
  final String? lateFine;
  final String? due15Days;
  final String? due30Days;
  final String? due45Days;
  final String? exportCode;
  final String? cost;
  final String? invoiceFeeStructure;

  ViolationDetails({
    this.violation,
    this.code,
    this.description,
    this.fine,
    this.lateFine,
    this.due15Days,
    this.due30Days,
    this.due45Days,
    this.exportCode,
    this.cost,
    this.invoiceFeeStructure,
  });

  ViolationDetails copyWith({
    String? violation,
    String? code,
    String? description,
    String? fine,
    String? lateFine,
    String? due15Days,
    String? due30Days,
    String? due45Days,
    String? exportCode,
    String? cost,
    String? invoiceFeeStructure,
  }) =>
      ViolationDetails(
        violation: violation ?? this.violation,
        code: code ?? this.code,
        description: description ?? this.description,
        fine: fine ?? this.fine,
        lateFine: lateFine ?? this.lateFine,
        due15Days: due15Days ?? this.due15Days,
        due30Days: due30Days ?? this.due30Days,
        due45Days: due45Days ?? this.due45Days,
        exportCode: exportCode ?? this.exportCode,
        cost: cost ?? this.cost,
        invoiceFeeStructure: invoiceFeeStructure ?? this.invoiceFeeStructure,
      );

  factory ViolationDetails.fromJson(Map<String, dynamic> json) => ViolationDetails(
    violation: json["violation"],
    code: json["code"],
    description: json["description"],
    fine: json["fine"],
    lateFine: json["late_fine"],
    due15Days: json["due_15_days"],
    due30Days: json["due_30_days"],
    due45Days: json["due_45_days"],
    exportCode: json["export_code"],
    cost: json["cost"],
    invoiceFeeStructure: json["invoice_fee_structure"],
  );

  Map<String, dynamic> toJson() => {
    "violation": violation,
    "code": code,
    "description": description,
    "fine": fine,
    "late_fine": lateFine,
    "due_15_days": due15Days,
    "due_30_days": due30Days,
    "due_45_days": due45Days,
    "export_code": exportCode,
    "cost": cost,
    "invoice_fee_structure": invoiceFeeStructure,
  };
}

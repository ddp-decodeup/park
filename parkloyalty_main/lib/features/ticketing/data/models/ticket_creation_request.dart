import 'dart:convert';

import 'package:collection/collection.dart';
import 'package:park_enfoecement/features/home/data/models/welcome_model.dart';

import '../../../../app/core/models/template_model.dart';

TicketCreationRequest ticketCreationRequestFromJson(String str) => TicketCreationRequest.fromJson(json.decode(str));

String ticketCreationRequestToJson(TicketCreationRequest data) => json.encode(data.toJson());

class TicketCreationRequest {
  String? code;
  String? hearingDate;
  List<String>? imageUrls;
  LocationDetails? locationDetails;
  String? lpNumber;
  String? notes;
  OfficerDetails? officerDetails;
  CommentDetails? commentDetails;
  InvoiceFeeStructure? invoiceFeeStructure;
  String? ticketNo;
  String? type;
  String? status;
  String? timeLimitEnforcementObservedTime;
  VehicleDetails? vehicleDetails;
  ViolationDetails? violationDetails;
  HeaderDetails? headerDetails;
  DateTime? citationIssueTimestamp;
  DateTime? citationStartTimestamp;
  bool? reissue;
  bool? timeLimitEnforcement;
  String? timeLimitEnforcementId;
  double? latitude;
  double? longitude;
  String? printQuery;

  TicketCreationRequest({
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
    this.citationIssueTimestamp,
    this.citationStartTimestamp,
    this.reissue,
    this.timeLimitEnforcement,
    this.timeLimitEnforcementId,
    this.latitude,
    this.longitude,
    this.printQuery,
  });

  factory TicketCreationRequest.fromJson(Map<String, dynamic>? json) {
    if (json == null) return TicketCreationRequest();

    return TicketCreationRequest(
      code: json["code"]?.toString(),
      hearingDate: json["hearing_date"]?.toString(),
      imageUrls: json["image_urls"] == null ? null : List<String>.from(json["image_urls"].map((x) => x.toString())),
      locationDetails: LocationDetails.fromJson(json["location_details"]),
      lpNumber: json["lp_number"]?.toString(),
      notes: json["notes"]?.toString()??'',
      officerDetails: OfficerDetails.fromJson(json["officer_details"]),
      commentDetails: CommentDetails.fromJson(json["comment_details"]),
      invoiceFeeStructure: InvoiceFeeStructure.fromJson(json["invoice_fee_structure"]),
      ticketNo: json["ticket_no"]?.toString(),
      type: json["type"]?.toString()??'',
      status: json["status"]?.toString()??'Valid',
      timeLimitEnforcementObservedTime: json["time_limit_enforcement_observed_time"]?.toString()??'',
      vehicleDetails: VehicleDetails.fromJson(json["vehicle_details"]),
      violationDetails: ViolationDetails.fromJson(json["violation_details"]),
      headerDetails: HeaderDetails.fromJson(json["header_details"]),
      citationIssueTimestamp: DateTime.tryParse(json["citation_issue_timestamp"]??DateTime.now().toString()),
      citationStartTimestamp: DateTime.tryParse(json["citation_start_timestamp"]??DateTime.now().toString()),
      reissue: json["reissue"] as bool?,
      timeLimitEnforcement: json["time_limit_enforcement"] as bool?,
      timeLimitEnforcementId: json["time_limit_enforcement_id"]?.toString()??'',
      latitude: (json["latitude"] as num?)?.toDouble(),
      longitude: (json["longitude"] as num?)?.toDouble(),
      printQuery: json["print_query"]?.toString(),
    );
  }

  Map<String, dynamic> toJson() => {
    "code": code,
    "hearing_date": hearingDate,
    "image_urls": imageUrls,
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
    "citation_issue_timestamp": citationIssueTimestamp?.toIso8601String(),
    "citation_start_timestamp": citationStartTimestamp?.toIso8601String(),
    "reissue": reissue,
    "time_limit_enforcement": timeLimitEnforcement,
    "time_limit_enforcement_id": timeLimitEnforcementId,
    "latitude": latitude,
    "longitude": longitude,
    "print_query": printQuery,
  };
}

class CommentDetails {
  String? note1;
  String? note2;
  String? note3;
  String? remark1;
  String? remark2;

  CommentDetails({this.note1, this.note2, this.note3, this.remark1, this.remark2});

  factory CommentDetails.fromJson(List<Field> fields) {
    // if (json == null) return CommentDetails();
    return CommentDetails(
      note1: fields.singleWhereOrNull((element) => element.name=='note_1',)?.enteredData?.text??'',
      note2: fields.singleWhereOrNull((element) => element.name=='note_2',)?.enteredData?.text??'',
      note3: fields.singleWhereOrNull((element) => element.name=='note_3',)?.enteredData?.text??'',
      remark1: fields.singleWhereOrNull((element) => element.name=='remark_1',)?.enteredData?.text??'',
      remark2: fields.singleWhereOrNull((element) => element.name=='remark_2',)?.enteredData?.text??'',
    );
  }

  Map<String, dynamic> toJson() => {
    "note_1": note1,
    "note_2": note2,
    "note_3": note3,
    "remark_1": remark1,
    "remark_2": remark2,
  };
}

class HeaderDetails {
  String? citationNumber;
  String? timestamp;

  HeaderDetails({this.citationNumber, this.timestamp});

  factory HeaderDetails.fromJson(Map<String, dynamic>? json) {
    if (json == null) return HeaderDetails();
    return HeaderDetails(citationNumber: json["citation_number"]?.toString(), timestamp: json["timestamp"]?.toString());
  }

  Map<String, dynamic> toJson() => {"citation_number": citationNumber, "timestamp": timestamp};
}

class InvoiceFeeStructure {
  double? parkingFee;
  double? citationFee;
  double? saleTax;

  InvoiceFeeStructure({this.parkingFee, this.citationFee, this.saleTax});

  factory InvoiceFeeStructure.fromJson(Map<String, dynamic>? json) {
    if (json == null) return InvoiceFeeStructure();
    return InvoiceFeeStructure(
      parkingFee: (json["parking_fee"] as num?)?.toDouble()??0.0,
      citationFee: (json["citation_fee"] as num?)?.toDouble()??0.0,
      saleTax: (json["sale_tax"] as num?)?.toDouble()??0.0,
    );
  }

  Map<String, dynamic> toJson() => {"parking_fee": parkingFee, "citation_fee": citationFee, "sale_tax": saleTax};
}

class LocationDetails {
  String? block;
  String? meter;
  String? side;
  String? direction;
  String? lot;
  String? branchLotid;
  String? lotLookupCode;
  String? street;
  String? streetLookupCode;
  String? spaceId;
  dynamic impoundCode;

  LocationDetails({
    this.block,
    this.meter,
    this.side,
    this.direction,
    this.lot,
    this.branchLotid,
    this.lotLookupCode,
    this.street,
    this.streetLookupCode,
    this.spaceId,
    this.impoundCode,
  });

  factory LocationDetails.fromJson(List<Field> fields) {
    // if (json == null) return LocationDetails();
    return LocationDetails(
      block: '',
      meter: '',
      side: '',
      direction: '',
      lot: fields.singleWhere((element) => element.name=='lot',).selectedDropDownOption?.label1,
      branchLotid: '',
      lotLookupCode: '',
      street: fields.singleWhere((element) => element.name=='street',).selectedDropDownOption?.label1,
      streetLookupCode: '',
      spaceId: '',
      impoundCode: null,
    );
  }

  Map<String, dynamic> toJson() => {
    "block": block,
    "meter": meter,
    "side": side,
    "direction": direction,
    "lot": lot,
    "branch_lotid": branchLotid,
    "lot_lookup_code": lotLookupCode,
    "street": street,
    "street_lookup_code": streetLookupCode,
    "space_id": spaceId,
    "impound_code": impoundCode,
  };
}

class OfficerDetails {
  String? agency;
  String? badgeId;
  String? officerLookupCode;
  String? beat;
  String? officerName;
  dynamic peoFname;
  dynamic peoLname;
  dynamic peoName;
  String? squad;
  String? zone;
  String? signature;
  String? shift;
  String? deviceId;
  String? deviceFriendlyName;

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

  factory OfficerDetails.fromJson(User? user) {
    // if (json == null) return OfficerDetails();
    return OfficerDetails(
        agency: user?.officerAgency,
        badgeId: user?.officerBadgeId.toString(),
        officerName: '${user?.officerFirstName} ${user?.officerLastName}',
        shift: user?.officerShift,
        deviceId: user?.officerDeviceId.deviceId,
        deviceFriendlyName: user?.officerDeviceId.deviceFriendlyName,
      
      officerLookupCode: '',
      beat: '',
      peoFname: null,
      peoLname: null,
      peoName: null,
      squad: '',
      zone: '',
      signature: '',
    );
  }

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
  String? color;
  String? lpNumber;
  String? make;
  String? bodyStyle;
  String? bodyStyleLookupCode;
  String? decalYear;
  String? decalNumber;
  String? vinNumber;
  String? model;
  String? modelLookupCode;
  String? state;
  String? licenseExpiry;

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

  factory VehicleDetails.fromJson(List<Field> fields) {
    // if (json == null) return VehicleDetails();
    return VehicleDetails(
      color: fields.singleWhere((element) => element.name=='color',).selectedDropDownOption?.label1,
      lpNumber: fields.singleWhere((element) => element.name=='lp_number',).enteredData?.text,
      make: fields.singleWhere((element) => element.name=='make',).selectedDropDownOption?.label1,
      bodyStyle: '',
      bodyStyleLookupCode: '',
      decalYear: '',
      decalNumber: '',
      vinNumber: fields.singleWhere((element) => element.name=='vin_number',).enteredData?.text,
      model: fields.singleWhere((element) => element.name=='model',).selectedDropDownOption?.label1,
      modelLookupCode: fields.singleWhere((element) => element.name=='model',).selectedDropDownOption?.label1,
      state: fields.singleWhere((element) => element.name=='state',).selectedDropDownOption?.label1,
      licenseExpiry: '',
    );
  }

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
  String? violation;
  String? code;
  String? description;
  double? fine;
  double? lateFine;
  double? due15Days;
  double? due30Days;
  double? due45Days;
  String? exportCode;
  double? cost;
  dynamic invoiceFeeStructure;
  String? sanctionsType;
  String? vioType;
  String? vioTypeCode;
  String? vioTypeDescription;

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
    this.sanctionsType,
    this.vioType,
    this.vioTypeCode,
    this.vioTypeDescription,
  });

  factory ViolationDetails.fromJson(List<Field> fields) {
    return ViolationDetails(
      violation: fields.singleWhere((element) => element.name=='violation_abbr',).selectedDropDownOption?.label1,
      code: fields.singleWhere((element) => element.name=='code',).selectedDropDownOption?.label1,
      description: fields.singleWhere((element) => element.name=='description',).enteredData?.text,
      fine: double.tryParse(fields.singleWhere((element) => element.name=='fine',).enteredData!.text.toString())??0,
      lateFine: 0.0,
      due15Days: 0.0,
      due30Days: 0.0,
      due45Days: 0.0,
      exportCode: '',
      cost: 0.0,
      invoiceFeeStructure: null,
      sanctionsType: '',
      vioType: '',
      vioTypeCode: '',
      vioTypeDescription: '',
    );
  }

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
    "sanctions_type": sanctionsType,
    "vio_type": vioType,
    "vio_type_code": vioTypeCode,
    "vio_type_description": vioTypeDescription,
  };
}

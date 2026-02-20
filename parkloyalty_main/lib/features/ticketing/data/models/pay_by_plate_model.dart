// To parse this JSON data, do
//
//     final payByPlateModel = payByPlateModelFromJson(jsonString);

import 'dart:convert';

PayByPlateModel payByPlateModelFromJson(String str) =>
    PayByPlateModel.fromJson(json.decode(str));

String payByPlateModelToJson(PayByPlateModel data) =>
    json.encode(data.toJson());

class PayByPlateModel {
  final List<List<PayByPlateData>>? data;
  final bool? status;
  final String? message;

  PayByPlateModel({this.data, this.status, this.message});

  factory PayByPlateModel.fromJson(Map<String, dynamic> json) =>
      PayByPlateModel(
        data: json["data"] == null
            ? []
            : List<List<PayByPlateData>>.from(
                json["data"]!.map(
                  (x) => List<PayByPlateData>.from(
                    x.map((x) => PayByPlateData.fromJson(x)),
                  ),
                ),
              ),
        status: json["status"],
        message: json["message"],
      );

  Map<String, dynamic> toJson() => {
    "data": data == null
        ? []
        : List<dynamic>.from(
            data!.map((x) => List<dynamic>.from(x.map((x) => x.toJson()))),
          ),
    "status": status,
    "message": message,
  };
}

class PayByPlateData {
  final String? receiptId;
  final DateTime? transactionTimestamp;
  final bool? addTransaction;
  final double? amountInDollars;
  final DateTime? expiryTimestamp;
  final int? latency;
  final String? lpNumber;
  final String? lpState;
  final String? meterId;
  final DateTime? recievedTimestamp;
  final String? siteId;
  final String? source;
  final String? spaceId;
  final DateTime? startTimestamp;
  final String? user;
  final String? vendorId;
  final String? vendorName;
  final String? zone;

  PayByPlateData({
    this.receiptId,
    this.transactionTimestamp,
    this.addTransaction,
    this.amountInDollars,
    this.expiryTimestamp,
    this.latency,
    this.lpNumber,
    this.lpState,
    this.meterId,
    this.recievedTimestamp,
    this.siteId,
    this.source,
    this.spaceId,
    this.startTimestamp,
    this.user,
    this.vendorId,
    this.vendorName,
    this.zone,
  });

  factory PayByPlateData.fromJson(Map<String, dynamic> json) => PayByPlateData(
    receiptId: json["receipt_id"],
    transactionTimestamp: json["transaction_timestamp"] == null
        ? null
        : DateTime.parse(json["transaction_timestamp"]),
    addTransaction: json["add_transaction"],
    amountInDollars: json["amount_in_dollars"]?.toDouble(),
    expiryTimestamp: json["expiry_timestamp"] == null
        ? null
        : DateTime.parse(json["expiry_timestamp"]),
    latency: json["latency"],
    lpNumber: json["lp_number"],
    lpState: json["lp_state"],
    meterId: json["meter_id"],
    recievedTimestamp: json["recieved_timestamp"] == null
        ? null
        : DateTime.parse(json["recieved_timestamp"]),
    siteId: json["site_id"],
    source: json["source"],
    spaceId: json["space_id"],
    startTimestamp: json["start_timestamp"] == null
        ? null
        : DateTime.parse(json["start_timestamp"]),
    user: json["user"],
    vendorId: json["vendor_id"],
    vendorName: json["vendor_name"],
    zone: json["zone"],
  );

  Map<String, dynamic> toJson() => {
    "receipt_id": receiptId,
    "transaction_timestamp": transactionTimestamp?.toIso8601String(),
    "add_transaction": addTransaction,
    "amount_in_dollars": amountInDollars,
    "expiry_timestamp": expiryTimestamp?.toIso8601String(),
    "latency": latency,
    "lp_number": lpNumber,
    "lp_state": lpState,
    "meter_id": meterId,
    "recieved_timestamp": recievedTimestamp?.toIso8601String(),
    "site_id": siteId,
    "source": source,
    "space_id": spaceId,
    "start_timestamp": startTimestamp?.toIso8601String(),
    "user": user,
    "vendor_id": vendorId,
    "vendor_name": vendorName,
    "zone": zone,
  };
}

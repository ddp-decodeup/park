// To parse this JSON data, do
//
//     final citationSimilarityRequestModel = citationSimilarityRequestModelFromJson(jsonString);

import 'dart:convert';

CitationSimilarityRequestModel citationSimilarityRequestModelFromJson(
  String str,
) => CitationSimilarityRequestModel.fromJson(json.decode(str));

String citationSimilarityRequestModelToJson(
  CitationSimilarityRequestModel data,
) => json.encode(data.toJson());

class CitationSimilarityRequestModel {
  String lpNumber;
  String zone;
  String code;
  String description;
  dynamic block;
  String street;
  dynamic side;
  String state;
  String ticketNo;
  CitationSimilarityRequestModel({
    required this.lpNumber,
    required this.zone,
    required this.code,
    required this.description,
    required this.block,
    required this.street,
    required this.side,
    required this.state,
    required this.ticketNo,
  });

  factory CitationSimilarityRequestModel.fromJson(Map<String, dynamic> json) =>
      CitationSimilarityRequestModel(
        lpNumber: json["lp_number"],
        zone: json["zone"],
        code: json["code"],
        description: json["description"],
        block: json["block"],
        street: json["street"],
        side: json["side"],
        state: json["state"],
        ticketNo: json["ticket_no"],
      );

  Map<String, dynamic> toJson() => {
    "lp_number": lpNumber,
    "zone": zone,
    "code": code,
    "description": description,
    "block": block,
    "street": street,
    "side": side,
    "state": state,
    "ticket_no": ticketNo,
  };
}

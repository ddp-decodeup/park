// To parse this JSON data, do
//
//     final citationSimilarityRequest = citationSimilarityRequestFromJson(jsonString);

import 'dart:convert';

CitationSimilarityRequest citationSimilarityRequestFromJson(String str) =>
    CitationSimilarityRequest.fromJson(json.decode(str));

String citationSimilarityRequestToJson(CitationSimilarityRequest data) =>
    json.encode(data.toJson());

class CitationSimilarityRequest {
  String lpNumber;
  String zone;
  String code;
  String description;
  dynamic block;
  String street;
  dynamic side;
  String state;
  String ticketNo;

  CitationSimilarityRequest({
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

  factory CitationSimilarityRequest.fromJson(Map<String, dynamic> json) =>
      CitationSimilarityRequest(
        lpNumber: json["lp_number"],
        zone: json["zone"] ?? '',
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

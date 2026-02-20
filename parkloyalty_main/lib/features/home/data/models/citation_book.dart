// To parse this JSON data, do
//
//     final citationBook = citationBookFromJson(jsonString);

import 'dart:convert';

CitationBook citationBookFromJson(String str) =>
    CitationBook.fromJson(json.decode(str));

String citationBookToJson(CitationBook data) => json.encode(data.toJson());

class CitationBook {
  List<Data> data;
  bool status;
  String message;

  CitationBook({
    required this.data,
    required this.status,
    required this.message,
  });

  factory CitationBook.fromJson(Map<String, dynamic> json) => CitationBook(
    data: List<Data>.from(json["data"].map((x) => Data.fromJson(x))),
    status: json["status"],
    message: json["message"],
  );

  Map<String, dynamic> toJson() => {
    "data": List<dynamic>.from(data.map((x) => x.toJson())),
    "status": status,
    "message": message,
  };
}

class Data {
  bool status;
  Res res;
  dynamic metadata;

  Data({required this.status, required this.res, required this.metadata});

  factory Data.fromJson(Map<String, dynamic> json) => Data(
    status: json["status"],
    res: Res.fromJson(json["response"]),
    metadata: json["metadata"],
  );

  Map<String, dynamic> toJson() => {
    "status": status,
    "response": res.toJson(),
    "metadata": metadata,
  };
}

class Res {
  List<String> citationBooklet;
  int latestCitationNumber;

  Res({required this.citationBooklet, required this.latestCitationNumber});

  factory Res.fromJson(Map<String, dynamic> json) => Res(
    citationBooklet: List<String>.from(json["citation_booklet"].map((x) => x)),
    latestCitationNumber: json["latest_citation_number"],
  );

  Map<String, dynamic> toJson() => {
    "citation_booklet": List<dynamic>.from(citationBooklet.map((x) => x)),
    "latest_citation_number": latestCitationNumber,
  };
}

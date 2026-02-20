// To parse this JSON data, do
//
//     final loginResponse = loginResponseFromJson(jsonString);

import 'dart:convert';

LoginResponse loginResponseFromJson(String str) =>
    LoginResponse.fromJson(json.decode(str));

String loginResponseToJson(LoginResponse data) => json.encode(data.toJson());

class LoginResponse {
  bool status;
  String response;
  Metadata? metadata;

  LoginResponse({
    required this.status,
    required this.response,
    required this.metadata,
  });

  factory LoginResponse.fromJson(Map<String, dynamic> json) => LoginResponse(
    status: json["status"],
    response: json["response"] ?? '',
    metadata: json["metadata"] == null
        ? null
        : Metadata.fromJson(json["metadata"]),
  );

  Map<String, dynamic> toJson() => {
    "status": status,
    "response": response,
    "metadata": metadata?.toJson(),
  };
}

class Metadata {
  DateTime lastLogin;
  DateTime currentLogin;

  Metadata({required this.lastLogin, required this.currentLogin});

  factory Metadata.fromJson(Map<String, dynamic> json) => Metadata(
    lastLogin: DateTime.parse(json["last_login"]),
    currentLogin: DateTime.parse(json["current_login"]),
  );

  Map<String, dynamic> toJson() => {
    "last_login": lastLogin.toIso8601String(),
    "current_login": currentLogin.toIso8601String(),
  };
}

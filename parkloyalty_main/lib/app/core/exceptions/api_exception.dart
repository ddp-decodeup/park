abstract class AppException implements Exception {
  final String message;
  final int? statusCode;

  AppException(this.message, {this.statusCode});

  @override
  String toString() => message;
}

class BadRequestException extends AppException {
  BadRequestException(String message, {int? statusCode})
    : super(message, statusCode: statusCode);
}

class UnauthorizedException extends AppException {
  UnauthorizedException(String message, {int? statusCode})
    : super(message, statusCode: statusCode);
}

class ServerException extends AppException {
  ServerException(String message, {int? statusCode})
    : super(message, statusCode: statusCode);
}

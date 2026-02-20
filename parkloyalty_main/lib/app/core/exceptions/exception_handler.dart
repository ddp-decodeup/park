import 'api_exception.dart';
import 'network_exception.dart';

class ExceptionHandler {
  static String getMessage(dynamic error) {
    if (error is AppException) {
      return error.message;
    }

    if (error is NetworkException) {
      return "Please check your internet connection";
    }

    return "Something went wrong. Please try again.";
  }
}

import 'package:park_enfoecement/app/core/models/drop_down_model.dart';

class RequestModel {
  String? ticketNo, blocName, side, licenseNo, enforced, arrivalStatus;
  DataSet? street, timing, selectedSide;

  RequestModel({
    this.ticketNo,
    this.blocName,
    this.street,
    this.side,
    this.licenseNo,
    this.enforced,
    this.arrivalStatus,
    this.timing,
    this.selectedSide,
  });

  /*RequestModel copyWith({String? ticketNo, String? blocName, DataSet? street, String? side, String? licenseNo, String? regulationTime}) {
    return RequestModel(
      ticketNo: ticketNo ?? this.ticketNo,
      blocName: blocName ?? this.blocName,
      street: street ?? this.street,
      side: side ?? this.side,
      licenseNo: licenseNo ?? this.licenseNo,
      regulationTime: regulationTime ?? this.regulationTime,
      regulationTime: regulationTime ?? this.regulationTime,
    );
  }*/
}

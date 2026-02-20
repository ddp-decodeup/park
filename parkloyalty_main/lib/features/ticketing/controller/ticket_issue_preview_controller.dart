import 'dart:convert';
import 'dart:io';

import 'package:collection/collection.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/api_endpoints.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/models/template_model.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import 'package:park_enfoecement/features/home/data/models/welcome_model.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/data/models/bulk_upload_response.dart';
import 'package:park_enfoecement/features/ticketing/data/models/ticket_creation_request.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/ticket_issue_repository.dart';

import '../../../app/core/constants/field_types.dart';
import '../../../app/core/controllers/app_controller.dart';
import '../../../app/core/services/location_service.dart';
import '../data/models/citation_similarity_request.dart';
import '../data/models/ticket_creation_response.dart' as ticketResponse;
import '../ui/screens/ticket_preview_screen.dart';

class TicketIssuePreviewController extends BaseController {
  TicketIssuePreviewController(this.repository, this.storageService, this.appController, this.authService);

  final TicketIssueRepository repository;
  final HomeStorageRepository storageService;
  final AuthService authService;
  final AppController appController;
  late TemplateModel model;
  List<File> imageList = [];
  var reqKeys = ["lp_number", "zone", "code", "description", "street", "state", "ticket_no"];
  BulkUploadResponse? bulkUploadResponse;

  @override
  void onInit() {
    var map = Get.arguments;
    model = map['model'] as TemplateModel;
    imageList = map['images'] as List<File>;
    super.onInit();
  }

  @override
  void onReady() {
    citationSimilarityCheck();
    super.onReady();
  }

  Future citationSimilarityCheck() async {
    var res = await repository.checkSimilarity(getReq());
    logging('result:: $res');
  }

  Future uploadFiles() async {
    Get.toNamed(Routes.ticketPreview,arguments: {'model': getTicketCreationRequest(), 'images': imageList});
    /*run(() async {
      if (imageList.isNotEmpty) {
        bulkUploadResponse = await repository.uploadFiles(imageList);
      }
      var body = await repository.createTicket(await getTicketCreationRequest());
      var res = ticketResponse.TicketCreationResponse.fromJson(body);
      if ((res.data?.lpNumber ?? '').isNotEmpty) {
        storageService.deleteCitationId(res.data?.ticketNo ?? '');
      }
      logging('upload image res :: ${res}');
});*/
  }

  getTicketCreationRequest() {
    // final position = await LocationService().getCurrentLocation();
    var req = TicketCreationRequest();
    model.data[0].response.forEach((e) {
      if (e.component == 'Violation') {
        req.violationDetails = ViolationDetails.fromJson(e.fields);
        req.code = e.fields.singleWhere((element) => element.name=='code',).selectedDropDownOption?.label1;
      } else if (e.component == 'Location') {
        req.locationDetails = LocationDetails.fromJson(e.fields);
      } else if (e.component == 'Vehicle') {
        req.vehicleDetails = VehicleDetails.fromJson(e.fields);
        req.lpNumber = e.fields.singleWhere((element) => element.name == 'lp_number').enteredData?.text;
      } else if (e.component == 'Comments') {
        req.commentDetails = CommentDetails.fromJson(e.fields);
      }
      if ((req.hearingDate ?? '').isEmpty)
        req.hearingDate = e.fields
            .singleWhereOrNull((element) => element.name == 'hearing_date')
            ?.selectedDropDownOption
            ?.label1??DateUtil.getHearingDate();
    });
    req.ticketNo = storageService.getCitationId();
    req.officerDetails = OfficerDetails.fromJson(authService.user);
    if (imageList.isNotEmpty) req.imageUrls = bulkUploadResponse?.data[0].response.links;
    req.reissue = false;
    // req.latitude = position?.latitude ?? 0;
    // req.longitude = position?.longitude ?? 0;
    req.timeLimitEnforcement = false;
    req.headerDetails = HeaderDetails(
      citationNumber: storageService.getCitationId(),
      timestamp: DateTime.now().toString(),
    );
    req.printQuery = generatePrintQuery(req, authService.user);
    logging('ticket req:: ${jsonEncode(req.toJson())}');
    return req;
  }

  getReq() {
    final Map<String, dynamic> requestMap = {};

    model.data[0].response.forEach((res) {
      for (final field in res.fields) {
        if (reqKeys.contains(field.name)) {
          if (field.tag == FieldTypes.textview ||
              field.tag == FieldTypes.editView ||
              field.tag == FieldTypes.textarea) {
            requestMap[field.name] = field.enteredData?.text ?? '';
          } else if (field.tag == FieldTypes.dropdown) {
            requestMap[field.name] = field.selectedDropDownOption?.label1 ?? '';
          }
        }
      }
      requestMap['ticket_no'] = storageService.getCitationId();
    });

    return CitationSimilarityRequest.fromJson(requestMap);
  }

  String generatePrintQuery(TicketCreationRequest req, User? user) {
    final buffer = StringBuffer();

    final location = "${req.locationDetails?.lot}  ${req.locationDetails?.street}";
    final paymentUrl =
        "${ApiEndpoints.baseUrl}search-result?query=%26ticket_number%3D${req.ticketNo}%26state%3D${req.vehicleDetails?.state}";

    buffer.write("VTEXT 2 2 551 300  \r\n");
    buffer.write("TEXT 7 1 10 160.0 PARKING NOTICE \r\n");

    buffer.write("BOX 0 200.0 565 480.0 1 \r\n");
    buffer.write("TEXT 7 0 5.0 210.0 Citation Number \r\n");
    buffer.write("TEXT 7 1 5.0 236.0 ${req.ticketNo} \r\n");

    buffer.write("TEXT 7 0 250.0 210.0 Date \r\n");
    buffer.write("TEXT 7 1 250.0 236.0 ${DateUtil.mmDDYYYY(DateTime.now())} \r\n");

    buffer.write("TEXT 7 0 398.0 210.0 Time \r\n");
    buffer.write("TEXT 7 1 398.0 236.0 ${DateUtil.hhmm(DateTime.now())} \r\n");

    buffer.write("TEXT 7 0 5.0 310.0 Officer ID \r\n");
    buffer.write("TEXT 7 1 5.0 336.0 ${user?.siteOfficerId} \r\n");

    buffer.write("TEXT 7 0 250.0 310.0 Agency \r\n");
    buffer.write("TEXT 7 1 250.0 336.0 ${user?.officerAgency} \r\n");

    buffer.write("TEXT 7 0 5.0 410.0 Location \r\n");
    buffer.write("TEXT 7 1 5.0 436.0 $location \r\n");

    buffer.write("TEXT 7 1 10 500.0 VIOLATION \r\n");
    buffer.write("BOX 0 540.0 565 710.0 1 \r\n");

    // buffer.write("TEXT 7 0 5.0 550.0 Code \r\n");
    // buffer.write("TEXT 7 1 5.0 576.0 $violationCode \r\n");

    buffer.write("TEXT 7 0 340.0 550.0 Fine \r\n");
    buffer.write("TEXT 7 1 340.0 576.0 \$ ${req.violationDetails?.fine} \r\n");

    buffer.write("TEXT 7 1 5.0 640.0 ${req.violationDetails?.description} \r\n");

    buffer.write("TEXT 7 1 10 710.0 VEHICLE INFORMATION \r\n");
    buffer.write("BOX 0 750.0 565 1030.0 1 \r\n");

    buffer.write("TEXT 7 0 5.0 760.0 License Number \r\n");
    buffer.write("TEXT 7 1 5.0 786.0 ${req.vehicleDetails?.lpNumber} \r\n");

    buffer.write("TEXT 7 0 340.0 760.0 State \r\n");
    buffer.write("TEXT 7 1 340.0 786.0 ${req.vehicleDetails?.state} \r\n");

    buffer.write("TEXT 7 0 5.0 860.0 Make \r\n");
    buffer.write("TEXT 7 1 5.0 886.0 ${req.vehicleDetails?.make} \r\n");

    buffer.write("TEXT 7 0 340.0 860.0 Color \r\n");
    buffer.write("TEXT 7 1 340.0 886.0 ${req.vehicleDetails?.color} \r\n");

    buffer.write("TEXT 7 0 5.0 960.0 Vin Number \r\n");
    buffer.write("TEXT 7 1 5.0 986.0 ${req.vehicleDetails?.vinNumber} \r\n");

    buffer.write("TEXT 7 0 340.0 960.0 Model \r\n");
    buffer.write("TEXT 7 1 340.0 986.0 $model \r\n");

    buffer.write("TEXT 5 2 120.0 40.0 LAZ PARKING \r\n");
    buffer.write("TEXT 5 1 80.0 100.0 KANSAS CITY PRIVATE LOTS \r\n");

    // QR Code
    buffer.write("B QR 190 1240 M 2 U 3 \r\n");
    buffer.write("M0A,QR code $paymentUrl \r\n");
    buffer.write("ENDQR\r\n");

    buffer.write("TEXT 7 1 175 1190 SCAN TO PAY \r\n");
    buffer.write("TEXT 7 1 150 1360 https://www.exmaple.com/ \r\n");

    return buffer.toString();
  }
}

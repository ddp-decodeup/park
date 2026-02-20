import 'dart:convert';
import 'dart:io';

import 'package:collection/collection.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/api_endpoints.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/models/template_model.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/features/home/data/models/welcome_model.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/data/models/bulk_upload_response.dart';
import 'package:park_enfoecement/features/ticketing/data/models/ticket_creation_request.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/ticket_issue_repository.dart';
import 'package:park_enfoecement/features/ticketing/ui/screens/ticket_post_page.dart';

import '../../../app/core/constants/field_types.dart';
import '../../../app/core/controllers/app_controller.dart';
import '../../../app/core/services/location_service.dart';
import '../data/models/citation_similarity_request.dart';
import '../data/models/ticket_creation_response.dart' as ticketResponse;
import '../ui/screens/ticket_preview_screen.dart';
import 'dart:io';
import 'dart:typed_data';
import 'dart:ui' as ui;
import 'package:flutter/rendering.dart';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';


class TicketPreviewController extends BaseController {
  TicketPreviewController(this.repository, this.storageService, this.appController, this.authService);

  final TicketIssueRepository repository;
  final HomeStorageRepository storageService;
  final AuthService authService;
  final AppController appController;
  late TicketCreationRequest model;
  List<File> imageList = [];
  var reqKeys = ["lp_number", "zone", "code", "description", "street", "state", "ticket_no"];
  BulkUploadResponse? bulkUploadResponse;
  final GlobalKey previewKey = GlobalKey();

  @override
  void onInit() {
    var map = Get.arguments;
    model = map['model'] as TicketCreationRequest;
    imageList = map['images'] as List<File>;
    super.onInit();
  }

  @override
  void onReady() {
    super.onReady();
    initialize();

  }


  Future uploadFiles() async {
    run(() async {
      if (imageList.isNotEmpty) {
        bulkUploadResponse = await repository.uploadFiles(imageList);
      }
      var body = await repository.createTicket(await getTicketCreationRequest());
      var res = ticketResponse.TicketCreationResponse.fromJson(body);
      if ((res.data?.lpNumber ?? '').isNotEmpty) {
        storageService.deleteCitationId(res.data?.ticketNo ?? '');
        Get.to(TicketPostPage(ticket: res.data));
      }
      print('upload image res :: ${body}');
    });
  }

  Future<TicketCreationRequest> getTicketCreationRequest() async {
    final position = await LocationService().getCurrentLocation();
    model.latitude = position?.latitude ?? 0;
    model.longitude = position?.longitude ?? 0;
    model.imageUrls=bulkUploadResponse?.data.first.response.links??[];
    return model;
  }

  Future<File?> captureWidgetToJpg(GlobalKey key) async {
    try {
      await Future.delayed(const Duration(milliseconds: 100));

      final context = previewKey.currentContext;
      if (context == null) {
        debugPrint("❌ previewKey.currentContext is null. Widget not built yet.");
        return null;
      }
      RenderRepaintBoundary boundary =
      key.currentContext!.findRenderObject() as RenderRepaintBoundary;

      // Higher pixelRatio = better quality
      ui.Image image = await boundary.toImage(pixelRatio: 3.0);

      ByteData? byteData =
      await image.toByteData(format: ui.ImageByteFormat.rawRgba);

      if (byteData == null) return null;

      final Uint8List pngBytes = byteData.buffer.asUint8List();

      // Convert RGBA to JPG using Flutter Image library
      final codec = await ui.instantiateImageCodec(
        pngBytes,
        targetWidth: image.width,
        targetHeight: image.height,
      );
      final frame = await codec.getNextFrame();
      final ui.Image finalImage = frame.image;

      final ByteData? jpgBytes =
      await finalImage.toByteData(format: ui.ImageByteFormat.png); // PNG bytes

      if (jpgBytes == null) return null;

      final directory = await getTemporaryDirectory();
      final file = File('${directory.path}/widget_image.jpg');

      await file.writeAsBytes(jpgBytes.buffer.asUint8List());
      return file;
    } on Error catch (e) {
      debugPrint("Error capturing widget: $e ${e.stackTrace}");
      return null;
    }
  }

  Future<void> initialize() async {
    var file=await captureWidgetToJpg(previewKey);
    if(file!=null) {
      imageList.add(file);
      update(['key']);
    }
  }

}

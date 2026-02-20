import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/controllers/app_controller.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/controller/ticket_preview_controller.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/ticket_issue_repository.dart';

import '../controller/ticket_issue_preview_controller.dart';

class TicketPreviewBindings extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<TicketPreviewController>(
      () => TicketPreviewController(
        Get.find<TicketIssueRepository>(),
        Get.find<HomeStorageRepository>(),
        Get.find<AppController>(),
        Get.find<AuthService>(),
      ),
    );
  }
}

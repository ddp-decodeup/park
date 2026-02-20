import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/shared/widgets/app_divider.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/features/ticketing/controller/ticket_issue_preview_controller.dart';
import 'package:park_enfoecement/features/ticketing/ui/widgets/image_section.dart';

import '../../../../app/core/constants/app_sizes.dart';
import '../widgets/ticket_detail_section.dart';

class TicketIssuePreviewPage extends GetView<TicketIssuePreviewController> {
  const TicketIssuePreviewPage({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<TicketIssuePreviewController>(
      builder: (controller) {
        return AppScaffold(
          onBack: Get.back,
          body: Column(
            children: [
              Flexible(
                child: ListView(
                  padding: AppSizes.defaultHorizontal,
                  shrinkWrap: true,
                  children: [
                    for (final component in controller.model.data[0].response) ...{
                      TicketDetailSection(model: component),
                      AppDivider(marginBottom: 15.h),
                    },
                    ImageSection(
                      fileList: controller.imageList,
                      isEditable: false,
                      title: LocalKeys.images.tr,
                      padding: EdgeInsets.zero,
                    ),

                    SizedBox(height: 15.h),
                    ColorButton(
                      onPressed: () {
                        controller.uploadFiles();
                      },
                      label: LocalKeys.print.tr,
                    ),
                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}

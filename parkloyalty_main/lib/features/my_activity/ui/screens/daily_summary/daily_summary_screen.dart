import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/app/shared/widgets/loader.dart';
import 'package:park_enfoecement/features/my_activity/controller/daily_summary_controller.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/comment_section.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/issuance_info_section.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/officer_details_sections.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/scan_hit_info_section.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/shift_details_section.dart';

class DailySummaryScreen extends GetView<DailySummaryController> {
  const DailySummaryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    controller;

    return Loader(
      child: AppScaffold(
        onBack: () {
          Get.back();
        },
        showDivider: true,
        body: SingleChildScrollView(
          padding: EdgeInsets.only(
            left: 16.w,
            right: 16.w,
            top: 16.h,
            bottom: 30.h,
          ),
          child: GetBuilder<DailySummaryController>(
            builder: (controller) {
              return Column(
                spacing: 16.h,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  OfficerDetailsSection(
                    officerTableData: controller.officerDetails,
                  ),
                  ShiftDetailsSection(
                    shiftDetailsTableData: controller.shiftDetails,
                  ),
                  IssuanceInfoSection(
                    officerTableData: controller.issuanceDetails,
                    totalCount: controller.totalCount.toString(),
                  ),
                  ScanHitInfoSection(
                    scanHitTableData: controller.scanHitDetails,
                    totalCount: controller.totalScans.toString(),
                  ),
                  CommentSection(controller: controller.commentController),
                  SizedBox.shrink(),
                  ColorButton(
                    onPressed: () {
                      controller.openBluetooth();
                    },
                    label: LocalKeys.submit.tr,
                    width: double.infinity,
                  ),
                ],
              );
            },
          ),
        ),
      ),
    );
  }
}

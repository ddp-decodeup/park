import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/app/shared/widgets/app_divider.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_check_box.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

import '../../../../app/core/theme/app_colors.dart';
import '../../../../app/shared/widgets/outline_button.dart';
import '../../data/models/ticket_creation_response.dart';

class TicketPostPage extends StatelessWidget {
  final Data? ticket;

  const TicketPostPage({super.key, required this.ticket});

  @override
  Widget build(BuildContext context) {
    var vehicleDetails = ticket?.vehicleDetails;
    var violationDetails = ticket?.violationDetails;
    return AppScaffold(
      body: ListView(
        shrinkWrap: true,
        padding: AppSizes.defaultHorizontal,
        children: [
          RenderSvgImage(assetName: AppIcons.successGraphic),
          Text(LocalKeys.ticketPosted.tr, style: textStyles.titleLarge),
          AppDivider(marginBottom: 15.h, marginTop: 15.h),
          Row(
            children: [
              CustomCheckBox(label: LocalKeys.driverOff.tr, onChanged: (b) {}, isSelected: true),
              SizedBox(width: 20.w),
              CustomCheckBox(label: LocalKeys.driverOff.tr, onChanged: (b) {}, isSelected: true),
            ],
          ),
          SizedBox(height: 15.h),
          Text(LocalKeys.ticketDetails.tr, style: textStyles.titleMedium),
          SizedBox(height: 15.h),
          Row(
            children: [
              Expanded(child: dataCell(LocalKeys.issueNo.tr, ticket?.ticketNo ?? '')),
              Expanded(child: dataCell(LocalKeys.status.tr, ticket?.status ?? 'Valid')),
            ],
          ),
          SizedBox(height: 15.h),
          dataCell(LocalKeys.ticketDate.tr, DateUtil.simplifiedDate2(ticket?.updatedAt ?? DateTime.now())),
          SizedBox(height: 15.h),
          dataCell(
            LocalKeys.violationDetails.tr,
            "${violationDetails?.violation ?? ''} ${violationDetails?.description ?? ''} ${LocalKeys.fine.tr} ${violationDetails?.fine ?? ''}",
          ),
          SizedBox(height: 15.h),
          dataCell(LocalKeys.address.tr, ticket?.location?.street ?? ''),
          SizedBox(height: 15.h),
          dataCell(
            LocalKeys.vehicle.tr,
            "${vehicleDetails?.make ?? ''} ${vehicleDetails?.lpNumber ?? ''} ${vehicleDetails?.model ?? ''}",
          ),
          SizedBox(height: 15.h),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              imageButton(AppIcons.scanIcon, LocalKeys.scan.tr),
              imageButton(AppIcons.ticketIcon, LocalKeys.issueMore.tr),
              imageButton(AppIcons.printerIcon, LocalKeys.print.tr),
              imageButton(AppIcons.notesIcon, LocalKeys.addNote.tr),
            ],
          ),
          SizedBox(height: 20.h),
          Row(
            children: [
              Expanded(child: buildOutlineButton(LocalKeys.cancel.tr, onPressed: () {})),
              SizedBox(width: 15.w),
              Expanded(child: buildOutlineButton(LocalKeys.voidReIssue.tr, onPressed: () {})),
            ],
          ),
          SizedBox(height: 15.h),
          buildOutlineButton(LocalKeys.regenerate.tr, onPressed: () {}),
          SizedBox(height: 100.h),
        ],
      ),
    );
  }

  OutlineButton buildOutlineButton(String label, {required void Function() onPressed}) =>
      OutlineButton(onPressed: onPressed, label: label, borderOnlyColor: AppColors.borderPrimary);

  Widget dataCell(String label, String value) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: textStyles.labelMedium),
        Text(value, style: textStyles.bodyMedium),
      ],
    );
  }

  Widget imageButton(String assetName, String label) {
    return Column(
      children: [
        Container(
          margin: EdgeInsets.only(bottom: 5.h),
          padding: EdgeInsets.all(10),
          decoration: BoxDecoration(
            color: AppColors.imageBg,
            borderRadius: BorderRadius.circular(12.r),
            border: Border.all(width: 1, color: AppColors.borderPrimary),
          ),
          child: RenderSvgImage(assetName: assetName, color: AppColors.textHeader),
        ),
        Text(label, style: textStyles.bodyMedium),
      ],
    );
  }
}

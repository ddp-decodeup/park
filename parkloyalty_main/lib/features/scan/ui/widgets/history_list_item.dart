import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';

import '../../../../app/core/localization/local_keys.dart';
import '../../../../app/core/theme/app_colors.dart';
import '../../../../app/core/theme/app_text_styles.dart';
import '../../../lookup/ui/widgets/look_up_list_sub_item.dart';
import '../../data/models/lpr_model.dart';

class HistoryListItem extends StatelessWidget {
  const HistoryListItem({super.key, required this.model});

  final Result model;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 14.w, vertical: 10.h),
      margin: EdgeInsets.only(top: 10.h),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(
          color: model.violationDetails.violation.isEmpty
              ? AppColors.errorRed
              : AppColors.successGreen,
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '#${DateUtil.citationHistoryDateFormat(model.citationIssueTimestamp)}',
            textAlign: TextAlign.right,
            style: textStyles.labelLarge?.copyWith(color: AppColors.textHeader),
          ),
          SizedBox(height: 10.h),
          LookUpListSubItem(LocalKeys.issueNo.tr, model.ticketNo),
          LookUpListSubItem(LocalKeys.issueType.tr, model.type),
          LookUpListSubItem(LocalKeys.driveOffTVR.tr, getDriver(model)),
          LookUpListSubItem(
            LocalKeys.licenseNo.tr,
            model.vehicleDetails.lpNumber,
          ),
          LookUpListSubItem(LocalKeys.status.tr, model.status),
          LookUpListSubItem(LocalKeys.address.tr, model.location.street),
          LookUpListSubItem(
            LocalKeys.violationDetails.tr,
            '${model.violationDetails.code} ${model.violationDetails.description}',
          ),
          LookUpListSubItem(LocalKeys.lPRNumber.tr, model.lpNumber),
        ],
      ),
    );
  }

  getDriver(Result data) {
    return "${data.driveOff ? 'Yes/' : 'No/'}${data.tvr ? 'Yes' : 'No'}";
  }
}

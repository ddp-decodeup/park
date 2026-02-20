import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/features/lookup/data/models/citation.dart';

import 'look_up_list_sub_item.dart';

class CitationListItem extends StatelessWidget {
  const CitationListItem({super.key, required this.data});

  final CitationData data;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 14.w, vertical: 10.h),
      margin: EdgeInsets.only(left: 14.w, top: 10.h, right: 14.w),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(color: AppColors.borderPrimary, width: 1),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            data.headerDetails.timestamp,
            style: textStyles.titleSmall?.copyWith(fontWeight: .w600),
          ),
          SizedBox(height: 10.h),
          LookUpListSubItem(LocalKeys.issueNo.tr, data.ticketNo),
          LookUpListSubItem(LocalKeys.issueType.tr, data.type),
          LookUpListSubItem(LocalKeys.driveOffTVR.tr, getDriver(data)),
          LookUpListSubItem(LocalKeys.status.tr, data.status),
          // LookUpListSubItem(LocalKeys.licenseNo.tr, data.lpNumber),
          LookUpListSubItem(LocalKeys.address.tr, data.location.street),
          LookUpListSubItem(
            LocalKeys.violationDetails.tr,
            '${data.violationDetails.code} ${data.violationDetails.description}',
          ),
          LookUpListSubItem(LocalKeys.lPRNumber.tr, data.lpNumber),
          LookUpListSubItem(LocalKeys.remark1.tr, data.commentDetails.remark1),
          LookUpListSubItem(LocalKeys.remark2.tr, data.commentDetails.remark2),
          LookUpListSubItem(LocalKeys.note1.tr, data.commentDetails.note1),
          LookUpListSubItem(LocalKeys.note2.tr, data.commentDetails.note2),
        ],
      ),
    );
  }

  getDriver(CitationData data) {
    return "${data.driveOff ? 'Yes/' : 'No/'}${data.tvr ? 'Yes' : 'No'}";
  }
}

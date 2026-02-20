import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/features/lookup/data/models/citation.dart';
import 'package:park_enfoecement/features/lookup/data/models/timing_record.dart';
import 'package:park_enfoecement/features/lookup/ui/widgets/tyre_direction_indicator_image.dart';

import '../../../../app/shared/utils/date_utils.dart';
import '../../../../app/shared/widgets/custom_check_box.dart';
import 'look_up_list_sub_item.dart';

class TimingRecordListItem extends StatelessWidget {
  const TimingRecordListItem({super.key, required this.data, this.isSelected = false, this.onSelectionChanged});

  final TimingData data;
  final bool isSelected;
  final void Function(bool)? onSelectionChanged;

  @override
  Widget build(BuildContext context) {
    var remainingTime = DateUtil.timeDiffOrZero(data.markStartTimestamp, data.regulationTime);
    bool isViolate = data.arrivalStatus == 'GOA' || remainingTime == '00:00:00';
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 14.w, vertical: 10.h),
      margin: EdgeInsets.only(left: 14.w, top: 10.h, right: 14.w),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.r),
        border: Border.all(color: isViolate ? AppColors.errorRed : AppColors.successGreen, width: 1),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (onSelectionChanged != null)
            CustomCheckBox(isSelected: isSelected, onChanged: onSelectionChanged!, label: ''),
          SizedBox(height: 10.h),
          LookUpListSubItem(LocalKeys.date.tr, DateUtil.getMMddhhmm(data.markStartTimestamp)),
          LookUpListSubItem(LocalKeys.regulation.tr, DateUtil.minutesToHoursMinutes(data.regulationTime)),
          LookUpListSubItem(LocalKeys.elapsed.tr, DateUtil.timeDifferenceHHMMSS(data.markStartTimestamp)),
          LookUpListSubItem(LocalKeys.licenseNo.tr, data.lpNumber),
          LookUpListSubItem(LocalKeys.zone.tr, data.zone),
          LookUpListSubItem(LocalKeys.status.tr, data.arrivalStatus),
          LookUpListSubItem(LocalKeys.remaining.tr, remainingTime),
          LookUpListSubItem(LocalKeys.location.tr, data.address),
          Container(
            height: 1,
            width: double.infinity,
            color: AppColors.borderDotted,
            margin: EdgeInsets.only(top: 10.h, bottom: 15.h),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Image.asset(AppIcons.carIcon),
              SizedBox(width: 15.w),
              TyreDirectionIndicatorImage([data.tireStemFront, data.tireStemBack]),
            ],
          ),
        ],
      ),
    );
  }

  getDriver(CitationData data) {
    return "${data.driveOff ? 'Yes/' : 'No/'}${data.tvr ? 'Yes' : 'No'}";
  }
}

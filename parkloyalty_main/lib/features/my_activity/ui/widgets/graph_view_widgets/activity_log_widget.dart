import 'package:flutter/material.dart' hide DateUtils;
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/features/my_activity/data/models/activity_log_model.dart';

class ActivityLogWidget extends StatelessWidget {
  final List<ActivityUpdate> activityLogs;

  const ActivityLogWidget({super.key, required this.activityLogs});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: .start,
      spacing: 5.h,
      children: [
        Text(LocalKeys.activityLog.tr, style: textStyles.titleMedium),
        Container(
          width: double.infinity,

          child: Column(
            spacing: 10.h,
            crossAxisAlignment: .start,
            children: [
              Row(
                spacing: 8.w,
                crossAxisAlignment: .center,
                children: [
                  Text(
                    "●",
                    style: textStyles.bodySmall?.copyWith(
                      color: AppColors.bulletPoint,
                    ),
                  ),
                  Text(
                    DateUtil.ddMMMMyyyy(DateTime.now().toLocal()),
                    style: textStyles.bodyMedium?.copyWith(
                      color: AppColors.textPrimary,
                      fontWeight: .w400,
                    ),
                  ),
                ],
              ),
              Padding(
                padding: EdgeInsets.symmetric(
                  horizontal: AppSizes.horizontalSpacing,
                ),
                child: Visibility(
                  visible: activityLogs.isNotEmpty,
                  replacement: Center(
                    child: Text(
                      LocalKeys.noActivityData.tr,
                      style: textStyles.bodyMedium,
                    ),
                  ),
                  child: Column(
                    spacing: 5.h,
                    children: List.generate(activityLogs.length, (index) {
                      final item = activityLogs[index];

                      return _buildActivityLogRow(
                        DateUtil.hhmm(item.clientTimestamp ?? DateTime.now()),
                        item.activityName.toString(),
                      );
                    }),
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildActivityLogRow(
    String label,
    String value, [
    TextStyle? textStyle,
  ]) {
    return Row(
      mainAxisAlignment: .spaceBetween,
      children: [
        Text(label, style: textStyle ?? textStyles.bodyMedium),
        // RenderSvgImage(AppIcons.longRightArrow),
        Text(
          value,
          style: (textStyle ?? textStyles.bodyMedium)?.copyWith(
            color: AppColors.pureBlack,
          ),
        ),
      ],
    );
  }
}

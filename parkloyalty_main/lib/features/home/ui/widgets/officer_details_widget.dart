import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

class OfficerDetailsWidget extends StatelessWidget {
  final DateTime previousLoginDate;
  final DateTime currentLoginDate;
  final String officerName;
  final String officerId;

  const OfficerDetailsWidget({
    super.key,
    required this.previousLoginDate,
    required this.currentLoginDate,
    required this.officerName,
    required this.officerId,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: .infinity,
      padding: EdgeInsetsGeometry.all(5),
      decoration: BoxDecoration(
        color: AppColors.backgroundCard,
        border: Border.all(color: AppColors.borderCard, width: 1.5),
        borderRadius: BorderRadius.circular(12.r),
      ),
      child: Column(
        crossAxisAlignment: .start,
        children: [
          Container(
            width: .infinity,
            padding: EdgeInsetsGeometry.symmetric(
              horizontal: 8.w,
              vertical: 8.h,
            ),
            decoration: BoxDecoration(
              color: AppColors.pureWhite,
              borderRadius: BorderRadius.circular(8.r),
              border: Border.all(color: AppColors.borderCard, width: 1.5),
            ),
            child: Column(
              crossAxisAlignment: .start,
              spacing: 12.h,
              children: [
                Text(
                  LocalKeys.loginActivityAndOfficerDetails.tr,
                  style: textStyles.titleMedium,
                ),
                Row(
                  spacing: 5.w,
                  mainAxisAlignment: .spaceBetween,
                  children: [
                    _buildLoginDetailsColumn(
                      title: LocalKeys.previousLogin.tr + ":",
                      value: DateUtil.simplifiedDate2(previousLoginDate),
                      color: AppColors.errorRed,
                    ),
                    _buildLoginDetailsColumn(
                      title: LocalKeys.currentLogin.tr + ":",
                      value: DateUtil.simplifiedDate2(currentLoginDate),
                      color: AppColors.successGreen,
                    ),
                  ],
                ),
              ],
            ),
          ),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 16.w, vertical: 8.h),
            child: Column(
              spacing: 5.h,
              children: [
                _buildOfficerDetailsRow(
                  title: LocalKeys.officerName.tr,
                  value: officerName,
                ),
                _buildOfficerDetailsRow(
                  title: LocalKeys.officerId.tr,
                  value: officerId,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLoginDetailsColumn({
    required String title,
    required String value,
    required Color color,
  }) {
    return Column(
      crossAxisAlignment: .start,
      mainAxisSize: .min,
      children: [
        Row(
          spacing: 5.w,
          children: [
            RenderSvgImage(
              assetName: AppIcons.lockCheckIcon,
              height: 16.h,
              width: 16.h,
              color: color,
            ),
            Text(title, style: textStyles.labelMedium?.copyWith(color: color)),
          ],
        ),
        Text(value, style: textStyles.labelSmall?.copyWith(fontWeight: .w500)),
      ],
    );
  }

  Widget _buildOfficerDetailsRow({
    required String title,
    required String value,
  }) {
    return Row(
      mainAxisAlignment: .spaceBetween,
      children: [
        Text(
          title,
          style: textStyles.labelLarge?.copyWith(color: AppColors.textSubtitle),
        ),
        Text(value, style: textStyles.bodyMedium),
      ],
    );
  }
}

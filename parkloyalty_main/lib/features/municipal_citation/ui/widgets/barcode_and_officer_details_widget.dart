import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';

class BarcodeAndOfficerDetailsWidget extends StatelessWidget {
  final String officerName;
  final String officerId;
  final String zone;
  final String agency;

  const BarcodeAndOfficerDetailsWidget({
    super.key,
    required this.officerName,
    required this.officerId,
    required this.zone,
    required this.agency,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: .infinity,
      margin: AppSizes.defaultHorizontal,
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
            padding: EdgeInsetsGeometry.symmetric(horizontal: 8.w, vertical: 8.h),
            decoration: BoxDecoration(
              color: AppColors.pureWhite,
              borderRadius: BorderRadius.circular(8.r),
              border: Border.all(color: AppColors.borderCard, width: 1.5),
            ),
            child: Column(crossAxisAlignment: .start, spacing: 12.h, children: []),
          ),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 16.w, vertical: 8.h),
            child: Column(
              spacing: 5.h,
              children: [
                _buildOfficerDetailsRow(title: LocalKeys.officerName.tr, value: officerName),
                _buildOfficerDetailsRow(title: LocalKeys.officerId.tr, value: officerId),
                _buildOfficerDetailsRow(title: LocalKeys.zone.tr, value: zone),
                _buildOfficerDetailsRow(title: LocalKeys.agency.tr, value: agency),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildOfficerDetailsRow({required String title, required String value}) {
    return Row(
      mainAxisAlignment: .spaceBetween,
      children: [
        Text(title, style: textStyles.labelLarge?.copyWith(color: AppColors.textSubtitle)),
        Text(value, style: textStyles.bodyMedium),
      ],
    );
  }
}

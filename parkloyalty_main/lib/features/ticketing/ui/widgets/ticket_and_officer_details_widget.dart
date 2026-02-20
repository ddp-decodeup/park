import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';

class TicketAndOfficerDetailsWidget extends StatelessWidget {
  final String citationNumber;
  final DateTime ticketDate;
  final String agency;
  final String officerId;
  final String? officerName;
  final String? beat;
  final String? officerIdHeadingName;
  final String? citationHeadingName;

  const TicketAndOfficerDetailsWidget({
    super.key,
    required this.citationNumber,
    required this.ticketDate,
    required this.agency,
    required this.officerId,
    this.officerName,
    this.beat,
    this.officerIdHeadingName,
    this.citationHeadingName,
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
            padding: EdgeInsetsGeometry.symmetric(horizontal: 8.w, vertical: 8.h),
            decoration: BoxDecoration(
              color: AppColors.pureWhite,
              borderRadius: BorderRadius.circular(8.r),
              border: Border.all(color: AppColors.borderCard, width: 1.5),
            ),
            child: Column(
              crossAxisAlignment: .start,
              spacing: 12.h,
              children: [
                Text(LocalKeys.ticketAndOfficerDetails.tr, style: textStyles.titleMedium),
                Row(
                  spacing: 5.w,
                  mainAxisAlignment: .spaceBetween,
                  children: [
                    _buildTicketDetailsColumn(
                      title: citationHeadingName ?? LocalKeys.citationNumber.tr,
                      value: citationNumber,
                    ),
                    _buildTicketDetailsColumn(
                      title: LocalKeys.ticketDate.tr,
                      value: DateUtil.simplifiedDate2(ticketDate).replaceAll(" - "," "),
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
                if (officerName != null) ...{
                  _buildOfficerDetailsRow(title: LocalKeys.officerName.tr, value: officerName!),
                },
                _buildOfficerDetailsRow(title: officerIdHeadingName ?? LocalKeys.officerId.tr, value: officerId),
                if (beat != null) ...{_buildOfficerDetailsRow(title: LocalKeys.beat.tr, value: beat!)},

                _buildOfficerDetailsRow(title: LocalKeys.agency.tr, value: agency),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTicketDetailsColumn({required String title, required String value}) {
    return Column(
      crossAxisAlignment: .start,
      mainAxisSize: .min,
      children: [
        Text(title, style: textStyles.labelMedium?.copyWith(color: AppColors.textPrimary)),
        Text(value, style: textStyles.labelSmall?.copyWith(fontWeight: .w500)),
      ],
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

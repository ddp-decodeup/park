import 'package:flutter/cupertino.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/features/my_activity/data/models/violation_data_model.dart';

class ViolationDetailsWidget extends StatelessWidget {
  final List<Resonse> violationData;

  const ViolationDetailsWidget({super.key, required this.violationData});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: AppSizes.defaultHorizontal,
      child: Column(
        crossAxisAlignment: .start,
        spacing: 10.h,
        children: [
          _buildViolationDetailsRow(
            LocalKeys.violationDetails.tr,
            LocalKeys.count.tr,
            textStyles.titleMedium,
          ),

          Visibility(
            visible: violationData.isNotEmpty,
            replacement: Center(
              child: Text(
                LocalKeys.noViolationData.tr,
                style: textStyles.bodyMedium,
              ),
            ),
            child: Column(
              spacing: 5.h,
              children: List.generate(violationData.length, (index) {
                final item = violationData[index];
                return _buildViolationDetailsRow(
                  item.violationName.toString(),
                  item.violationCounts.toString(),
                );
              }),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildViolationDetailsRow(
    String label,
    String value, [
    TextStyle? textStyle,
  ]) {
    return Row(
      mainAxisAlignment: .spaceBetween,
      children: [
        Text(label, style: textStyle ?? textStyles.labelLarge),
        Text(
          value,
          style: (textStyle ?? textStyles.labelLarge)?.copyWith(
            color: AppColors.pureBlack,
          ),
        ),
      ],
    );
  }
}

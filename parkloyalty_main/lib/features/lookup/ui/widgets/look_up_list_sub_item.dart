import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import '../../../../app/core/theme/app_colors.dart';
import '../../../../app/core/theme/app_text_styles.dart';

class LookUpListSubItem extends StatelessWidget {
  const LookUpListSubItem(this.label, this.value, {super.key});
  final String label, value;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsetsGeometry.only(bottom: 10.h),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(label, style: textStyles.labelLarge),
          Expanded(
            child: Text(
              value,
              textAlign: TextAlign.right,
              style: textStyles.labelLarge?.copyWith(
                color: AppColors.textHeader,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

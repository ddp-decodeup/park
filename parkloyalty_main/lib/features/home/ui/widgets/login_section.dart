import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/context_extensions.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';

import '../../../../app/core/models/template_model.dart';
import '../../../../app/core/theme/app_colors.dart';

class LoginSection extends StatelessWidget {
  const LoginSection({super.key, required this.field});

  final Field field;

  @override
  Widget build(BuildContext context) {
    final textStyle = context.textTheme;
    return Expanded(
      child: Container(
        decoration: BoxDecoration(
          border: Border.all(width: 1, color: AppColors.borderPrimary),
          borderRadius: AppSizes.defaultBorderRadius,
        ),
        padding: EdgeInsets.symmetric(horizontal: 8.w, vertical: 15.h),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(field.repr, style: textStyle.bodyMedium),
            SizedBox(height: 10.h),
            Text(field.enteredData?.text ?? '', style: textStyle.labelMedium),
          ],
        ),
      ),
    );
  }
}

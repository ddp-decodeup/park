import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';

import '../../core/theme/app_colors.dart';

class OutlineButton extends StatelessWidget {
  const OutlineButton({
    required this.onPressed,
    required this.label,
    this.color,
    this.borderOnlyColor,
    super.key,
  });

  final void Function()? onPressed;
  final String label;
  final Color? color; // It'll change both border and text color
  final Color? borderOnlyColor; // It'll change only border color

  @override
  Widget build(BuildContext context) {
    return OutlinedButton(
      onPressed: onPressed,
      style: OutlinedButton.styleFrom(
        padding: EdgeInsets.symmetric(vertical: 12.h),
        foregroundColor: Colors.transparent,
        side: BorderSide(color: color ?? borderOnlyColor?? AppColors.pureBlack),
        shape: RoundedRectangleBorder(
          borderRadius: AppSizes.defaultBorderRadius,
        ),
      ),
      child: Text(
        label,
        style: textStyles.labelLarge?.copyWith(
          color: color ?? AppColors.textPrimary,
        ),
      ),
    );
  }
}

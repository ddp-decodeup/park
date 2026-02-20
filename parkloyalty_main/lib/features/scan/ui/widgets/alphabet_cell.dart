import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';

class AlphabetCell extends StatelessWidget {
  const AlphabetCell({super.key, required this.alphabet});
  final String alphabet;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.only(right: 15.w, top: 15.h, bottom: 15.h),
      decoration: BoxDecoration(
        border: Border.all(color: AppColors.errorRed, width: 1),
        borderRadius: BorderRadius.circular(8.r),
      ),
      alignment: Alignment.center,
      height: 42.h,
      width: 42.w,
      child: Text(
        alphabet,
        style: textStyles.labelLarge?.copyWith(
          color: AppColors.textHeader,
          fontWeight: .w500,
        ),
      ),
    );
  }
}

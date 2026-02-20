import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import 'app_colors.dart';

TextTheme get textStyles => AppTextStyles.textTheme;

class AppTextStyles {
  static const String fontFamily = 'sf_pro';
  static TextStyle fontSize24px = _style(
    fontSize: 24.sp,
    color: AppColors.textHeader,
    fontWeight: .w600,
  );
  static TextStyle fontSize20px = _style(
    fontSize: 20.sp,
    fontWeight: FontWeight.w600,
    color: AppColors.textHeader,
  );
  static TextStyle fontSize18px = _style(
    fontSize: 18.sp,
    fontWeight: FontWeight.w600,
    color: AppColors.textHeader,
  );
  static TextStyle fontSize16px = _style(
    fontSize: 16.sp,
    fontWeight: FontWeight.w600,
  );
  static TextStyle fontSize14px = _style(fontSize: 14.sp);
  static TextStyle fontSize13px = _style(
    fontSize: 13.sp,
    color: AppColors.textSubtitle,
  );
  static TextStyle fontSize12px = _style(
    fontSize: 12.sp,
    fontWeight: FontWeight.w400,
    color: AppColors.textSubtitle,
  );

  static TextStyle _style({
    required double fontSize,
    FontWeight fontWeight = FontWeight.w500,
    Color color = AppColors.textPrimary,
  }) {
    return TextStyle(
      fontFamily: fontFamily,
      fontSize: fontSize,
      color: color,
      fontWeight: fontWeight,
      letterSpacing: -0.20
    );
  }

  static TextTheme get textTheme => TextTheme(
    labelSmall: fontSize12px,
    labelMedium: fontSize13px,
    labelLarge: fontSize14px.copyWith(
      color: AppColors.textHint,
      fontWeight: FontWeight.w400,
    ),
    bodySmall: fontSize14px.copyWith(
      color: AppColors.textHint,
      fontWeight: FontWeight.w600,
    ),
    bodyMedium: fontSize14px,
    bodyLarge: fontSize16px,
    titleSmall: fontSize16px.copyWith(
      color: AppColors.textSubtitle,
      fontWeight: .w400,
    ),
    titleMedium: fontSize18px,
    titleLarge: fontSize20px,
    headlineSmall: fontSize24px,
    headlineMedium: null,
    headlineLarge: null,
    displaySmall: null,
    displayMedium: null,
    displayLarge: null,
  );
}

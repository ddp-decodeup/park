import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import '../constants/app_sizes.dart';
import 'app_colors.dart';
import 'app_text_styles.dart';

class AppTheme {
  static ThemeData lightTheme = ThemeData(
    colorScheme: ColorScheme(
      brightness: Brightness.light,
      primary: AppColors.primaryBlue,
      onPrimary: AppColors.pureWhite,
      secondary: AppColors.accentPurple,
      onSecondary: AppColors.pureWhite,
      error: AppColors.errorRed,
      onError: AppColors.pureWhite,
      surface: AppColors.backgroundCard,
      onSurface: AppColors.textPrimary,
    ),
    fontFamily: 'sf_pro',
    primaryColor: AppColors.primaryBlue,
    scaffoldBackgroundColor: AppColors.pureWhite,
    textTheme: AppTextStyles.textTheme,
    appBarTheme: AppBarTheme(
      backgroundColor: AppColors.pureWhite,
      surfaceTintColor: AppColors.pureWhite,
      titleTextStyle: AppTextStyles.fontSize18px,
      iconTheme: IconThemeData(color: AppColors.pureWhite),
    ),

    iconTheme: IconThemeData(color: AppColors.iconGray),

    dividerTheme: DividerThemeData(color: AppColors.borderCard, thickness: 1.5),

    cardTheme: CardThemeData(
      color: AppColors.backgroundCard,
      elevation: 0,
      shape: RoundedRectangleBorder(borderRadius: AppSizes.defaultBorderRadius),
    ),

    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        backgroundColor: AppColors.primaryBlue,
        foregroundColor: AppColors.pureWhite,
        disabledBackgroundColor: AppColors.buttonDisabled,
        disabledForegroundColor: AppColors.textHint,
        textStyle: textStyles.bodySmall,
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: AppSizes.defaultBorderRadius,
        ),
      ),
    ),

    outlinedButtonTheme: OutlinedButtonThemeData(
      style: OutlinedButton.styleFrom(
        foregroundColor: AppColors.primaryBlue,
        side: BorderSide(color: AppColors.borderPrimary),
        shape: RoundedRectangleBorder(
          borderRadius: AppSizes.defaultBorderRadius,
        ),
        textStyle: textStyles.bodyMedium,
      ),
    ),

    textButtonTheme: TextButtonThemeData(
      style: TextButton.styleFrom(
        foregroundColor: AppColors.primaryBlue,
        textStyle: textStyles.bodyMedium?.copyWith(
          color: AppColors.primaryBlue,
        ),
      ),
    ),

    floatingActionButtonTheme: FloatingActionButtonThemeData(
      backgroundColor: AppColors.primaryBlue,
      foregroundColor: AppColors.pureWhite,
    ),

    bottomNavigationBarTheme: BottomNavigationBarThemeData(
      backgroundColor: AppColors.pureWhite,
      selectedItemColor: AppColors.primaryBlue,
      unselectedItemColor: AppColors.iconGray,
      showUnselectedLabels: true,
    ),

    snackBarTheme: SnackBarThemeData(
      backgroundColor: AppColors.pureBlack,
      contentTextStyle: AppTextStyles.fontSize14px.copyWith(
        color: AppColors.pureWhite,
      ),
    ),

    inputDecorationTheme: InputDecorationTheme(
      hintStyle: textStyles.labelLarge,
      filled: true,
      fillColor: AppColors.pureWhite,
      errorStyle: textStyles.labelSmall?.copyWith(color: AppColors.errorRed),
      constraints: BoxConstraints(minHeight: 48.h),
      suffixIconConstraints: BoxConstraints(maxHeight: 48.h),

      contentPadding: EdgeInsets.symmetric(
        horizontal: AppSizes.defaultHorizontal.left,
        vertical: AppSizes.defaultVertical.top,
      ),
      border: OutlineInputBorder(
        borderSide: BorderSide(color: AppColors.shadowDark, width: 1.5),
        borderRadius: AppSizes.defaultBorderRadius,
      ),
      enabledBorder: OutlineInputBorder(
        borderSide: BorderSide(color: AppColors.shadowDark, width: 1.5),
        borderRadius: AppSizes.defaultBorderRadius,
      ),
      focusedBorder: OutlineInputBorder(
        borderSide: BorderSide(color: AppColors.primaryBlue, width: 1.5),
        borderRadius: AppSizes.defaultBorderRadius,
      ),
      errorBorder: OutlineInputBorder(
        borderSide: BorderSide(color: AppColors.errorRed, width: 1.5),
        borderRadius: AppSizes.defaultBorderRadius,
      ),
      disabledBorder: OutlineInputBorder(
        borderSide: BorderSide(color: AppColors.shadowDark, width: 1.5),
        borderRadius: AppSizes.defaultBorderRadius,
      ),
    ),
    bottomSheetTheme: BottomSheetThemeData(
      backgroundColor: AppColors.pureWhite,
    ),
  );
}

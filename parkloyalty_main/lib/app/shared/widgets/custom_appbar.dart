import 'package:flutter/material.dart' hide BackButton;
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/shared/widgets/app_logo.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_back_button.dart';
import 'package:park_enfoecement/app/shared/widgets/hamburger_button.dart';

import '../../core/theme/app_colors.dart';
import '../../core/theme/app_text_styles.dart';

class CustomAppBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;

  final VoidCallback? onTapHamburger;

  final VoidCallback? onBack;

  final bool centerTitle;

  final bool showDivider;

  final bool showShadowWhileScroll;

  const CustomAppBar({
    super.key,
    this.title = "",
    this.onTapHamburger,
    this.onBack,
    this.centerTitle = false,
    this.showDivider = false,
    this.showShadowWhileScroll = false,
  });

  @override
  Size get preferredSize => Size.fromHeight(kToolbarHeight + 10);

  @override
  Widget build(BuildContext context) {
    return AppBar(
      elevation: 0,
      scrolledUnderElevation: showShadowWhileScroll?2.5:0,
      shape: showDivider ? Border(bottom: BorderSide(color: AppColors.borderCard, width: 1.5)) : null,
      centerTitle: centerTitle,
      backgroundColor: AppColors.pureWhite,
      surfaceTintColor: AppColors.pureWhite,
      shadowColor: AppColors.textPrimary,
      automaticallyImplyLeading: false,
      leading: onBack != null
          ? Padding(
              padding: EdgeInsets.symmetric(horizontal: 8.0.w),
              child: CustomBackButton(onBack: onBack),
            )
          : null,
      title: Row(
        mainAxisAlignment: centerTitle || onBack != null ? .center : .start,
        spacing: 5.w,
        children: [
          AppLogo(height: 40.h),
          Text(LocalKeys.parkLoyalty.tr, style: textStyles.titleMedium),
        ],
      ),
      actions: [
        Padding(
          padding: EdgeInsets.symmetric(horizontal: 8.0.w),
          child: _buildAppBarContent(),
        ),
      ],
    );
  }

  Widget _buildAppBarContent() {
    return HamburgerButton(onTapHamburger: onTapHamburger);
  }
}

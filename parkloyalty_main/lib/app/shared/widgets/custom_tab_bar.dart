import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

import '../../core/theme/app_colors.dart';
import '../utils/extensions/context_extensions.dart';

class CustomTabBar extends StatelessWidget {
  const CustomTabBar({super.key, required this.tabList});
  final List<Widget> tabList;

  @override
  Widget build(BuildContext context) {
    return TabBar(
      tabs: tabList,
      isScrollable: false,
      labelStyle: context.theme.textTheme.bodyMedium?.copyWith(
        fontWeight: FontWeight.w500,
        fontSize: 14.sp,
        color: AppColors.primaryBlue,
      ),
      indicator: UnderlineTabIndicator(
        borderSide: BorderSide(color: AppColors.primaryBlue, width: 2),
      ),
      indicatorColor: AppColors.primaryBlue,
      indicatorWeight: 2,
      dividerColor: AppColors.borderPrimary,
      indicatorSize: TabBarIndicatorSize.tab,
      physics: NeverScrollableScrollPhysics(),
      unselectedLabelStyle: context.theme.textTheme.bodyMedium?.copyWith(
        fontWeight: FontWeight.w500,
        color: AppColors.textHint,
        fontSize: 14.sp,
      ),
    );
  }
}

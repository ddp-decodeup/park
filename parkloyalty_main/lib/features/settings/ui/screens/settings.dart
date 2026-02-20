import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

import '../../../../app/shared/widgets/custom_bottom_nav_bar.dart';

final List<Map<String, String>> settingScreenItems = [
  {"title": "BARCODE_URL", "subtitle": "No"},
  {"title": "DEFAULT_STATE", "subtitle": "CALIFORNIA"},
  {"title": "HAS_EXEMPT", "subtitle": "No"},
  {"title": "HAS_MAKE", "subtitle": "No"},
  {"title": "TIMEZONE", "subtitle": "PST8PDT"},
  {"title": "MAX_IMAGES", "subtitle": "10"},
  {"title": "HAS_TIMING", "subtitle": "Yes"},
  {"title": "QRCODE_URL", "subtitle": "https://www.metro.net/payviolation"},
  {"title": "PRINTER", "subtitle": "Zebra 510"},
  {"title": "HAS_PERMIT", "subtitle": "Yes"},
  {"title": "HAS_PAYMENTS", "subtitle": "Yes"},
];

class SettingsPage extends StatelessWidget {
  const SettingsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return AppScaffold(
      body: ListView.builder(
        itemBuilder: (context, index) {
          final item = settingScreenItems[index];
          return _buildItemsRow(item: item, showDivider: false);
        },

        itemCount: settingScreenItems.length,
      ),

      bottomNavigationBar: CustomBottomNavBar(),
    );
  }

  Widget _buildItemsRow({
    required Map<String, String> item,
    bool showDivider = true,
  }) {
    return ListTile(
      shape: showDivider
          ? Border(
              bottom: BorderSide(width: 1.5.w, color: AppColors.borderCard),
            )
          : Border(),
      onTap: () {},
      leading: RenderSvgImage(
        assetName: AppIcons.settingsIcon,
        height: 20.h,
        width: 20.h,
        fit: .fill,
        color: AppColors.textSubtitle,
      ),
      title: Text(
        item["title"]!,
        style: textStyles.bodyLarge?.copyWith(fontWeight: .w500),
      ),
      subtitle: Text(
        item["subtitle"]!,
        style: textStyles.labelLarge?.copyWith(fontWeight: .w500),
      ),
    );
  }
}

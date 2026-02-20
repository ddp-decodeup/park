import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_bottom_nav_bar.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

final List<Map<String, String>> reportScreenItems = [
  {"label": "Broken Asset Report"},
  {"label": "Full Time EOW Officer Report"},
  {"label": "Part Time EOW Officer Report"},
  {"label": "EOW Supervisor Shift Report"},
  {"label": "Hand Held Malfunction"},
  {"label": "Sign Report"},
  {"label": "Vehicle Inspection Report"},
  {"label": "Bike Inspection"},
  {"label": "72hrs Notice To Tow"},
  {"label": "Tow Report"},
  {"label": "Sign Off Report"},
];

class ReportsPage extends StatelessWidget {
  const ReportsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return AppScaffold(
      showDivider: true,
      body: ListView.builder(
        itemBuilder: (context, index) {
          final item = reportScreenItems[index];
          return _buildItemsRow(
            item: item,
            showDivider: index != reportScreenItems.length - 1,
          );
        },

        itemCount: reportScreenItems.length,
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
        assetName: AppIcons.reportIcon,
        height: 20.h,
        width: 20.h,
        fit: .fill,
        color: AppColors.textSubtitle,
      ),
      title: Text(
        item["label"]!,
        style: textStyles.bodyLarge?.copyWith(fontWeight: .w500),
      ),
      trailing: RenderSvgImage(assetName: AppIcons.arrowRightIcon),
    );
  }
}

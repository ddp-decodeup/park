import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

import '../../../../app/shared/widgets/app_scaffold.dart';
import '../../../../app/shared/widgets/custom_bottom_nav_bar.dart';

final List<Map<String, String>> activityScreenItems = [
  {
    "icon": AppIcons.graphIcon,
    "label": LocalKeys.graphView.tr,
    "route": Routes.graphView,
  },
  {
    "icon": AppIcons.summaryIcon,
    "label": LocalKeys.dailySummary.tr,
    "route": Routes.dailySummary,
  },
  {
    "icon": AppIcons.mapIcon,
    "label": LocalKeys.lprHits.tr,
    "route": Routes.lprHits,
  },
];

class MyActivityPage extends StatelessWidget {
  const MyActivityPage({super.key});

  @override
  Widget build(BuildContext context) {
    return AppScaffold(
      showDivider: true,
      body: ListView.builder(
        itemBuilder: (context, index) {
          final item = activityScreenItems[index];
          return _buildItemsRow(
            item: item,
            showDivider: index != activityScreenItems.length - 1,
          );
        },

        itemCount: activityScreenItems.length,
      ),

      bottomNavigationBar: const CustomBottomNavBar(),
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
      onTap: () {
        Get.toNamed(item["route"]!);
      },
      leading: RenderSvgImage(
        assetName: item["icon"]!,
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

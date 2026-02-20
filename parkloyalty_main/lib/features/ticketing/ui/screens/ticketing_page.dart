import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/repositories/event_logging_repository.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_bottom_nav_bar.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

final List<Map<String, String>> ticketScreenItems = [
  {
    "icon": AppIcons.notesIcon,
    "label": LocalKeys.issue.tr,
    "route": Routes.ticketIssue,
  },
  {
    "icon": AppIcons.scanIcon,
    "label": LocalKeys.scan.tr,
    "route": Routes.manualEntry,
  },
  {
    "icon": AppIcons.scanIcon,
    "label": LocalKeys.municipalCitation.tr,
    "route": Routes.municipalCitation,
  },
  {
    "icon": AppIcons.citationResultIcon,
    "label": LocalKeys.citationResult.tr,
    "route": Routes.citationResult,
  },
  {
    "icon": AppIcons.payByPlateIcon,
    "label": LocalKeys.payByPlate.tr,
    "route": Routes.payByPlate,
  },
  {
    "icon": AppIcons.payBySpaceIcon,
    "label": LocalKeys.payBySpace.tr,
    "route": Routes.payBySpace,
  },
];

class TicketingPage extends StatelessWidget {
  const TicketingPage({super.key});

  @override
  Widget build(BuildContext context) {
    return AppScaffold(
      showDivider: true,

      body: ListView.builder(
        itemBuilder: (context, index) {
          final item = ticketScreenItems[index];
          return _buildItemsRow(
            item: item,
            showDivider: index != ticketScreenItems.length - 1,
          );
        },

        itemCount: ticketScreenItems.length,
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
      onTap: () {
        _logActivityForRoute(item['route']);
        Get.toNamed(item['route'].toString());
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

  void _logActivityForRoute(String? route) {
    final loggingRepository = Get.find<EventLoggingRepository>();
    if (route == Routes.ticketIssue) {
      loggingRepository.updateActivity(activityName: "Menu Issue");
    } else if (route == Routes.manualEntry) {
      loggingRepository.updateActivity(activityName: "Menu Scan");
    } else if (route == Routes.citationResult) {
      loggingRepository.updateActivity(activityName: "Lookup");
    }
  }
}

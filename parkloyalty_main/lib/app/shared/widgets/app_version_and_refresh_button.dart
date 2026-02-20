import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';
import 'package:park_enfoecement/app/shared/widgets/restart_widget.dart';

class AppVersionAndRefreshButtonWidget extends StatelessWidget {
  const AppVersionAndRefreshButtonWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: .center,
      children: [
        Text(
          "v 37.07.009\n8788171de12fb651",
          style: textStyles.labelSmall,
          textAlign: .center,
        ),
        TextButton.icon(
          style: TextButton.styleFrom(
            disabledForegroundColor: AppColors.primaryBlue,

            padding: EdgeInsetsGeometry.symmetric(
              vertical: AppSizes.defaultVertical.top,
              horizontal: AppSizes.defaultHorizontal.left * 2,
            ),
          ),
          icon: RenderSvgImage(assetName: AppIcons.refreshIcon),
          onPressed: () {
            RestartWidget.restartApp(context);
          },
          label: Text(
            LocalKeys.refreshApp.tr,
            style: textStyles.bodySmall?.copyWith(color: AppColors.primaryBlue),
          ),
        ),
      ],
    );
  }
}

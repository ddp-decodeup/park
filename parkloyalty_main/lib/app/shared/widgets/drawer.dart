import 'package:flutter/material.dart' hide DrawerController;
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart' show AppIcons;
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/controller/drawer_controller.dart';
import 'package:park_enfoecement/app/shared/widgets/app_logo.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_dialog.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

import 'custom_close_button.dart';

class CustomDrawer extends StatefulWidget {
  const CustomDrawer({super.key});

  @override
  State<CustomDrawer> createState() => _CustomDrawerState();
}

class _CustomDrawerState extends State<CustomDrawer> {
  final DrawerController _controller = Get.put(DrawerController());
  final authService = Get.find<AuthService>();

  @override
  void dispose() {
    Get.delete<DrawerController>();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Drawer(
      shape: RoundedRectangleBorder(),
      child: Column(
        crossAxisAlignment: .start,
        children: [
          SafeArea(
            top: true,
            bottom: false,
            child: SizedBox(
              height: 65.h,
              child: DrawerHeader(
                child: Row(
                  spacing: 10.w,
                  children: [
                    AppLogo(height: 40.h),
                    Text(LocalKeys.parkLoyalty.tr, style: textStyles.titleMedium),
                    Spacer(),
                    CustomCloseButton(
                      height: 30.h,
                      onTap: () {
                        _controller.closeDrawer(context);
                      },
                    ),
                  ],
                ),
              ),
            ),
          ),

          ...List.generate(_controller.bottomNavBarController.items.length, (index) {
            final item = _controller.bottomNavBarController.items[index];

            return _buildCustomDrawerRow(
              label: item['label'] as String,
              onTap: () {
                _controller.onTapDrawerRow(index);
                _controller.closeDrawer(context);
              },
              icon: item['icon'] as String,
            );
          }),

          Spacer(),
          SafeArea(
            bottom: true,
            top: false,
            child: InkWell(
              onTap: () {
                _controller.closeDrawer(context);
                showDialog(
                  context: context,
                  builder: (context) {
                    return CustomDialog(
                      title: LocalKeys.logout.tr + "?",
                      content: Column(
                        mainAxisSize: .min,
                        children: [Text(LocalKeys.logoutMessage.tr, style: textStyles.bodyMedium)],
                      ),
                      outlineButtonText: LocalKeys.cancel.tr,
                      elevatedButtonText: LocalKeys.logout.tr,
                      onTapOutlineButton: () {
                        Get.back();
                      },
                      onTapElevatedButton: () {
                        authService.clearSession();
                      },
                    );
                  },
                );
              },
              child: Container(
                padding: EdgeInsets.all(10.h),
                decoration: BoxDecoration(border: Border(top: Divider.createBorderSide(context))),
                child: Row(
                  crossAxisAlignment: .center,
                  spacing: 5.w,
                  mainAxisAlignment: .center,
                  children: [
                    RenderSvgImage(assetName: AppIcons.logoutIcon, color: AppColors.errorRed),
                    Text(LocalKeys.logout.tr, style: textStyles.bodyLarge?.copyWith(fontWeight: .w500)),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCustomDrawerRow({String? label, VoidCallback? onTap, String? icon}) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: EdgeInsets.symmetric(vertical: 10.h, horizontal: 14.w),
        child: Row(
          crossAxisAlignment: .center,
          spacing: 10.w,
          mainAxisAlignment: .start,
          children: [
            Visibility(
              visible: icon != null,
              child: RenderSvgImage(
                assetName: icon ?? "",
                height: 20.h,
                width: 20.h,
                fit: BoxFit.fill,
                color: AppColors.textHint,
              ),
            ),
            Visibility(
              visible: label != null,
              child: Text(
                label ?? "",
                style: textStyles.bodyLarge?.copyWith(fontWeight: .w500),
                maxLines: 2,
                textAlign: TextAlign.end,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

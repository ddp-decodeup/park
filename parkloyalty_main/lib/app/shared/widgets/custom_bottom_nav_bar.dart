import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/controller/bottom_nav_bar_controller.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

class CustomBottomNavBar extends StatefulWidget {
  const CustomBottomNavBar({super.key});

  @override
  State<CustomBottomNavBar> createState() => _CustomBottomNavBarState();
}

class _CustomBottomNavBarState extends State<CustomBottomNavBar> {
  final BottomNavBarController controller = Get.find<BottomNavBarController>();

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: controller.currentIndex == 0,
      onPopInvokedWithResult: (didPop, result) {
        if (controller.currentIndex != 0) {
          controller.changeIndex(0);
        } else {
          exit(0);
        }
      },
      child: SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Divider(height: 1, thickness: 1, color: AppColors.borderCard),
            Padding(
              padding: const EdgeInsets.only(top: 10.0, bottom: 8.0),
              child: Row(
                crossAxisAlignment: .center,
                mainAxisAlignment: .spaceEvenly,
                children: List.generate(controller.items.length, (index) {
                  final item = controller.items[index];
                  return Obx(() {
                    return Expanded(
                      child: InkWell(
                        splashColor: Colors.transparent,
                        highlightColor: Colors.transparent,
                        onTap: () {
                          controller.changeIndex(index);
                        },
                        child: _buildBottomBarItem(item: item, isSelected: controller.currentIndex == index),
                      ),
                    );
                  });
                }),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBottomBarItem({required Map<String, String> item, bool isSelected = false}) {
    return Column(
      mainAxisAlignment: .center,
      mainAxisSize: .min,
      spacing: .5.h,
      children: [
        RenderSvgImage(
          assetName: item['icon'] as String,
          color: isSelected ? AppColors.primaryBlue : AppColors.textHint,
        ),
        Text(
          item['label'] as String,
          style: textStyles.labelSmall?.copyWith(color: isSelected ? AppColors.primaryBlue : AppColors.textHint),
        ),
        Visibility(
          visible: isSelected,
          child: Container(
            width: 8.w,
            height: 4.w,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.vertical(top: Radius.circular(100.r)),
              color: AppColors.primaryBlue,
            ),
          ),
        ),
      ],
    );
  }
}

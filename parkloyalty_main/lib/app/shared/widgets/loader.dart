import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_images.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';

class Loader extends StatelessWidget {
  final Widget child;

  const Loader({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        child,
        GetBuilder<LoaderController>(
          builder: (LoaderController controller) {
            return Visibility(
              visible: controller.loader,
              child: Container(
                color: AppColors.borderPrimary,
                child: Stack(
                  children: [
                    Center(
                      child: Image.asset(
                        AppImages.logo,
                        height: 38.h,
                        width: 38.h,
                      ),
                    ),
                    Center(
                      child: CircularProgressIndicator(
                        strokeAlign: 8.r,
                        color: AppColors.pureWhite,
                        strokeWidth: 3,
                      ),
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ],
    );
  }
}

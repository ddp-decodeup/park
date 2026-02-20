import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/constants/app_images.dart';

class AppLogo extends StatelessWidget {
  final double? height;

  const AppLogo({super.key, this.height});

  @override
  Widget build(BuildContext context) {
    return Image.asset(AppImages.logo, height: height ?? 80.h, width: height ?? 80.h, fit: .contain);
  }
}

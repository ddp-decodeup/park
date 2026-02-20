import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/constants/app_images.dart';

class CustomBackground extends StatelessWidget {
  final Widget child;

  const CustomBackground({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: double.infinity,
      width: double.infinity,
      decoration: BoxDecoration(
        image: DecorationImage(
          image: AssetImage(AppImages.customBackGround),
          fit: .fill,
        ),
      ),
      child: child,
    );
  }
}

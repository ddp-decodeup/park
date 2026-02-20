import 'package:flutter/material.dart';
import '../../core/theme/app_colors.dart';

class AppDivider extends StatelessWidget {
  const AppDivider({super.key, this.marginBottom = 0, this.marginTop = 0});
  final double marginTop, marginBottom;

  @override
  Widget build(BuildContext context) {
    return  Divider(height: 1, thickness: 1, color: AppColors.borderDotted);
  }
}

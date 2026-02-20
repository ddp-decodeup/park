import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

import '../../core/constants/app_icons.dart';
import '../../core/theme/app_colors.dart';
import '../../core/theme/app_text_styles.dart';

class NoDataLayout extends StatelessWidget {
  const NoDataLayout({super.key, required this.message});
  final String message;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          RenderSvgImage(assetName: AppIcons.notFoundGraphic),
          SizedBox(height: 10.h),
          Text(
            message,
            style: textStyles.bodyLarge?.copyWith(
              color: AppColors.textHeader,
              fontWeight: .w500,
            ),
          ),
        ],
      ),
    );
  }
}

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/outline_button.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

import 'color_button.dart';

class CustomDialog extends StatefulWidget {
  final String? outlineButtonText;
  final String elevatedButtonText;
  final VoidCallback? onTapOutlineButton;
  final VoidCallback onTapElevatedButton;
  final String title;
  final Widget? content;
  final bool showDivider;
  final TextStyle? titleTextStyle;
  final bool showCloseButton;
  final MainAxisAlignment mainAxisAlignment;

  const CustomDialog({
    super.key,
    required this.title,
    required this.content,
    this.outlineButtonText,
    required this.elevatedButtonText,
    this.onTapOutlineButton,
    required this.onTapElevatedButton,
    this.showDivider = false,
    this.titleTextStyle,
    this.showCloseButton = true,
    this.mainAxisAlignment = .spaceBetween,
  });

  @override
  State<CustomDialog> createState() => _CustomDialogState();
}

class _CustomDialogState extends State<CustomDialog> {
  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Row(
        mainAxisAlignment: widget.mainAxisAlignment,
        children: [
          Text(
            widget.title,
            style: widget.titleTextStyle ?? textStyles.displaySmall?.copyWith(color: AppColors.textPrimary),
          ),
          if (widget.showCloseButton)
            InkWell(
              customBorder: CircleBorder(),
              onTap: () {
                Get.back();
              },
              child: RenderSvgImage(assetName: AppIcons.clearIcon),
            ),
        ],
      ),
      contentPadding: .zero,
      content: Column(
        spacing: 5.h,
        mainAxisSize: .min,
        children: [
          Padding(
            padding: EdgeInsets.only(
              left: AppSizes.horizontalSpacing,
              right: AppSizes.horizontalSpacing,
              top: AppSizes.verticalSpacing,
              bottom: 5.h,
            ),
            child: widget.content ?? SizedBox.shrink(),
          ),
          Padding(
            padding: EdgeInsets.only(bottom: 10.h),
            child: Visibility(
              visible: widget.showDivider,
              child: Divider(color: AppColors.shadowDark),
            ),
          ),
        ],
      ),
      actions: [
        Row(
          spacing: widget.outlineButtonText != null ? 10.w : 0.w,
          children: [
            Visibility(
              visible: widget.outlineButtonText != null,
              child: Expanded(
                child: OutlineButton(onPressed: widget.onTapOutlineButton, label: widget.outlineButtonText ?? ""),
              ),
            ),
            Expanded(
              child: ColorButton(onPressed: widget.onTapElevatedButton, label: widget.elevatedButtonText),
            ),
          ],
        ),
      ],
    );
  }
}

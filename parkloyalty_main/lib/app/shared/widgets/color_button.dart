import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

class ColorButton extends StatelessWidget {
  const ColorButton({
    required this.onPressed,
    required this.label,
    this.width,
    this.margin,
    this.bgColor,
    this.icon,
    this.disableBgColor,
    this.disableFgColor,
    super.key,
  });

  final void Function()? onPressed;
  final String label;
  final double? width;
  final EdgeInsets? margin;
  final Color? bgColor;
  final Color? disableBgColor;
  final Color? disableFgColor;
  final String? icon;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width,
      margin: margin,
      child: ElevatedButton(
        onPressed: onPressed,
        style: ElevatedButton.styleFrom(
          padding: EdgeInsets.symmetric(vertical: 12.h),
          backgroundColor: bgColor,
          disabledBackgroundColor: disableBgColor,disabledForegroundColor: disableFgColor
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (icon != null) ...{
              RenderSvgImage(assetName: icon!),
              SizedBox(width: 8.w),
            },
            Text(label),
          ],
        ),
      ),
    );
  }
}

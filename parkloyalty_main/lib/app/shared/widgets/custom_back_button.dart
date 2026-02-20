import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

class CustomBackButton extends StatelessWidget {
  final VoidCallback? onBack;

  const CustomBackButton({super.key, this.onBack});

  @override
  Widget build(BuildContext context) {
    return InkWell(
      splashColor: Colors.transparent,
      highlightColor: Colors.transparent,
      onTap: onBack,
      child: RenderSvgImage(
        assetName: AppIcons.backIcon,
        height: 20,
        width: 20,
      ),
    );
  }
}

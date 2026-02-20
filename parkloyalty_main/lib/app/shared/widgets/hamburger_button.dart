import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

class HamburgerButton extends StatelessWidget {
  final VoidCallback? onTapHamburger;

  const HamburgerButton({super.key, this.onTapHamburger});

  @override
  Widget build(BuildContext context) {
    return InkWell(
      splashColor: Colors.transparent,
      highlightColor: Colors.transparent,
      onTap: onTapHamburger,
      child: RenderSvgImage(assetName: AppIcons.hamburgerIcon),
    );
  }
}

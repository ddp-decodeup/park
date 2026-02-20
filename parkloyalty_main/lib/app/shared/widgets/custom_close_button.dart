import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

class CustomCloseButton extends StatelessWidget {
  final VoidCallback? onTap;
  final double? height;


  const CustomCloseButton({super.key, this.onTap, this.height});

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: RenderSvgImage(assetName: AppIcons.closeIcon,height: height,width: height,),
      customBorder: CircleBorder(),
    );
  }
}

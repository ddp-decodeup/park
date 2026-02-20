import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';

import '../../../../app/core/constants/app_icons.dart';
import '../../../../app/shared/widgets/render_svg_image.dart';

class TyreDirectionIndicatorImage extends StatelessWidget {
  const TyreDirectionIndicatorImage(this.list, {super.key});
  final List<int> list;
  @override
  Widget build(BuildContext context) {
    const int total = 12;
    final double size = 84.w; // circle image size
    final double numberSize = 18.w;
    final double radius = size / 2 - numberSize / 4;

    return SizedBox(
      width: size,
      height: size,
      child: Stack(
        alignment: Alignment.center,
        clipBehavior: Clip.none,
        children: [
          RenderSvgImage(assetName: AppIcons.tyreIcon),
          ...List.generate(total, (index) {
            final angle = (2 * pi / total) * index - pi / 2;

            final frontIndex = clockToIndex(list[0]);
            final backIndex = clockToIndex(list[1]);

            final bool isFront = index == frontIndex;
            final bool isBack = index == backIndex;

            final dx = radius * cos(angle);
            final dy = radius * sin(angle);

            return Positioned(
              left: size / 2 + dx - numberSize / 2,
              top: size / 2 + dy - numberSize / 2,
              child: Visibility(
                visible: isFront || isBack,
                child: Container(
                  width: numberSize,
                  height: numberSize,
                  decoration: BoxDecoration(
                    color: getBgColor(isFront, isBack),
                    shape: BoxShape.circle,
                    border: Border.all(
                      color: getBorderColor(isFront, isBack),
                      width: 2,
                    ),
                  ),
                  alignment: Alignment.center,
                  child: Text(
                    '${index == 0 ? 12 : index}',
                    style: TextStyle(
                      fontSize: 10.sp,
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ),
            );
          }),
        ],
      ),
    );
    // return ;
  }

  int clockToIndex(int clock) => clock % 12;

  Color getBgColor(bool isFront, bool isBack) {
    if (isFront) {
      return AppColors.warningYellow;
    } else if (isBack) {
      return AppColors.errorRed;
    }
    return Colors.transparent;
  }

  Color getBorderColor(bool isFront, bool isBack) {
    if (isFront) {
      return AppColors.warningYellowDark;
    } else if (isBack) {
      return AppColors.errorRedDark;
    }
    return Colors.transparent;
  }
}

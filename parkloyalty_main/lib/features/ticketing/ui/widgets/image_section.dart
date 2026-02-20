import 'dart:io';

import 'package:dotted_border/dotted_border.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

import '../../../../app/core/localization/local_keys.dart';
import '../../../../app/core/theme/app_text_styles.dart';

class ImageSection extends StatelessWidget {
  const ImageSection({super.key, this.onTap, required this.fileList, this.onDelete,this.isEditable=true,this.title,this.padding});

  final void Function()? onTap;
  final void Function(File)? onDelete;
  final List<File> fileList;
  final bool isEditable;
  final String? title;
  final EdgeInsets? padding;

  @override
  Widget build(BuildContext context) {
    return Column(
      spacing: 12.h,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: padding??AppSizes.defaultHorizontal,
          child: Text(title??LocalKeys.citationImages.tr, style: textStyles.titleMedium),
        ),
        SizedBox(
          height: 96.h,
          child: ListView.builder(
            itemCount: fileList.length + 1,
            shrinkWrap: true,
            scrollDirection: Axis.horizontal,
            padding: padding??AppSizes.defaultHorizontal,
            itemBuilder: (context, index) {
              if (index < fileList.length)
                return Stack(
                  children: [
                    Container(
                      height: 96.h,
                      width: 96.w,
                      margin: EdgeInsets.only(right: 10.w),
                      clipBehavior: Clip.hardEdge,
                      decoration: BoxDecoration(borderRadius: BorderRadius.circular(10.r)),
                      child: Image.file(fileList[index],fit: BoxFit.fitWidth),
                    ),
                    Positioned(
                        right: 15.w,
                        top: 5.h,
                        child: Visibility(
                          visible: isEditable,
                          child: GestureDetector(
                              onTap: () => onDelete?.call(fileList[index]),
                              child: RenderSvgImage(assetName: AppIcons.closeIcon,height: 20.h,)),
                        ))
                  ],
                );
              else
                return Visibility(
                  visible: isEditable,
                  child: Wrap(
                    children: [
                      GestureDetector(
                        onTap: onTap,
                        child: Container(
                          color: AppColors.backgroundCard,
                          child: DottedBorder(
                            options: RoundedRectDottedBorderOptions(
                              color: AppColors.borderDotted,
                              dashPattern: [10, 5],
                              strokeWidth: 2,
                              padding: EdgeInsets.symmetric(horizontal: 30.w, vertical: 30.h),
                              radius: Radius.circular(10.r),
                            ),
                            child: Container(
                              padding: EdgeInsets.symmetric(horizontal: 12.w, vertical: 12.h),
                              // inner alignment / padding
                              decoration: BoxDecoration(
                                color: Colors.white, // #FFFFFF
                                borderRadius: BorderRadius.circular(12.r),
                                boxShadow: [
                                  BoxShadow(
                                    color: Colors.black.withValues(alpha: 0.10), // #000000 10%
                                    offset: const Offset(0, 4), // X: 0, Y: 4
                                    blurRadius: 20, // Blur: 20
                                    spreadRadius: 0, // Spread: 0
                                  ),
                                ],
                              ),
                              child: RenderSvgImage(assetName: AppIcons.uploadIcon,),
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                );
            },
          ),
        ),
      ],
    );
  }
}

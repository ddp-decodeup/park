import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

class BuildPreviewSection extends StatelessWidget {
  final bool showEditIcon;
  final List<Map<String, dynamic>> previewData;
  final String sectionTitle;

  const BuildPreviewSection({
    super.key,
    this.showEditIcon = true,
    required this.previewData,
    required this.sectionTitle,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: AppSizes.defaultHorizontal,
      child: Column(
        spacing: 5.h,
        children: [
          _buildHeaderRow(),
          SizedBox(),
          ...List.generate(previewData.length, (index) {
            final data = previewData[index];
            final label = data.keys.first;
            final value = data.values.first;
            final flex = (data["flex"] as int?) ?? 0;
            return _buildBodyRow(label: label, value: value,flex: flex);
          }),
        ],
      ),
    );
  }

  Widget _buildHeaderRow() {
    return Row(
      mainAxisAlignment: .spaceBetween,
      children: [
        Text(sectionTitle, style: textStyles.titleMedium),
        if (showEditIcon)
          Material(
            child: InkWell(
              onTap: () {
                Get.back();
              },
              child: Row(
                spacing: 5.w,
                crossAxisAlignment: .center,
                children: [
                  RenderSvgImage(assetName: AppIcons.editIcon, height: 14.h, width: 14.h),
                  Text(
                    LocalKeys.edit.tr,
                    style: textStyles.bodyMedium?.copyWith(
                      color: AppColors.primaryBlue,
                      decoration: TextDecoration.underline,

                      decorationColor: AppColors.primaryBlue,
                    ),
                  ),
                ],
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildBodyRow({required String label, required String value, required int flex}) {
    return Row(
      mainAxisAlignment: .spaceBetween,
      crossAxisAlignment: .start,
      spacing: 10.w,
      children: [
        Text(
          label,
          style: textStyles.bodySmall?.copyWith(color: AppColors.textSubtitle, fontWeight: .normal),
        ),
        Expanded(
          flex: flex,
          child: Text(
            value,
            style: textStyles.bodySmall?.copyWith(color: AppColors.textPrimary, fontWeight: FontWeight.w500),
            maxLines: 2,
          ),
        ),
      ],
    );
  }
}

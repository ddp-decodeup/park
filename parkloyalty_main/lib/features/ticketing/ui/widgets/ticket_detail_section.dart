import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

import '../../../../app/core/constants/field_types.dart';
import '../../../../app/core/models/template_model.dart';
import '../../../../app/core/theme/app_text_styles.dart';

class TicketDetailSection extends StatelessWidget {
  const TicketDetailSection({required this.model, super.key});

  final TemplateRes model;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(model.component!, style: textStyles.titleLarge),
            Row(
              children: [
                RenderSvgImage(assetName: AppIcons.editIcon),
                SizedBox(width: 2.w),
                Text(
                  LocalKeys.edit.tr,
                  style: textStyles.bodyMedium?.copyWith(
                    color: AppColors.primaryBlue,
                    fontWeight: .w500,
                    decoration: TextDecoration.underline,
                    decorationColor: AppColors.primaryBlue,
                  ),
                ),
              ],
            ),
          ],
        ),

        SizedBox(height: 15.h),

        for (final field in model.fields) ...{
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(field.repr, style: textStyles.bodyMedium),
              SizedBox(width: 20.w),

              if (field.tag == FieldTypes.textview ||
                  field.tag == FieldTypes.editView ||
                  field.tag == FieldTypes.textarea) ...{
                Expanded(
                  child: Text(textAlign: .right, field.enteredData?.text ?? '', style: textStyles.labelMedium),
                ),
              } else if (field.tag == FieldTypes.dropdown) ...{
                Expanded(
                  child: Text(
                    textAlign: .right,
                    field.selectedDropDownOption?.label1 ?? '',
                    style: textStyles.labelMedium,
                  ),
                ),
              },
            ],
          ),

          SizedBox(height: 10.h),
        },
      ],
    );
  }
}

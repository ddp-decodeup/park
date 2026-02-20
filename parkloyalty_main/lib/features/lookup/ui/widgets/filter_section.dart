import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_drop_down.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_text_field.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';
import 'package:park_enfoecement/features/lookup/data/models/request_model.dart';

import '../../../../app/core/theme/app_text_styles.dart';

class FilterSection extends StatelessWidget {
  const FilterSection({
    super.key,
    required this.streetDropDownItems,
    required this.searchHint,
    required this.req,
    required this.onApply,
    required this.onSearchComplete,
    this.timingTypeDropDownItems,
    this.sideDropDownItems,
    this.isFromCitation = true,
  });

  final List<DataSet> streetDropDownItems;
  final List<DataSet>? timingTypeDropDownItems;
  final List<DataSet>? sideDropDownItems;
  final String searchHint;
  final RequestModel req;
  final void Function() onApply;
  final void Function(String) onSearchComplete;
  final bool isFromCitation;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: AppSizes.defaultHorizontal,
      child: Column(
        children: [
          SizedBox(height: 15.h),
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: CustomTextField(
                  hintText: searchHint,
                  onChanged: onSearchComplete,
                  prefix: Padding(
                    padding: EdgeInsets.only(left: 15.w, right: 5.w),
                    child: RenderSvgImage(assetName: AppIcons.searchIcon),
                  ),
                ),
              ),
              GestureDetector(
                onTap: () => filterBottomSheet(
                  context,
                  streetDropDownItems,
                  timingTypeDropDownItems,
                  sideDropDownItems,
                  req,
                  onApply,
                ),
                child: Container(
                  margin: EdgeInsets.only(left: 10.w),
                  padding: EdgeInsets.symmetric(
                    horizontal: 10.w,
                    vertical: 10.h,
                  ),
                  decoration: BoxDecoration(
                    color: AppColors.primaryBlue,
                    borderRadius: BorderRadius.circular(10.r),
                  ),
                  child: RenderSvgImage(assetName: AppIcons.filterIcon),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Future<dynamic> filterBottomSheet(
    BuildContext context,
    List<DataSet> streetDropDownItems,
    List<DataSet>? timingTypeDropDownItems,
    List<DataSet>? sideDropDownItems,
    RequestModel model,
    void Function()? onApply,
  ) async {
    return showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Padding(
            padding: EdgeInsets.symmetric(
              horizontal: AppSizes.horizontalSpacing,
              vertical: AppSizes.verticalSpacing,
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: [
                Center(
                  child: Container(
                    width: 40,
                    height: 4,
                    decoration: BoxDecoration(
                      color: Colors.grey,
                      borderRadius: BorderRadius.circular(2),
                    ),
                  ),
                ),
                Text(LocalKeys.bloc.tr, style: textStyles.bodyMedium),
                SizedBox(height: 10.h),
                CustomTextField(
                  controller: TextEditingController(text: model.blocName),
                  onChanged: (s) => model.blocName = s,
                  hintText: LocalKeys.blocName.tr,
                ),
                SizedBox(height: 15.h),
                Text(LocalKeys.street.tr, style: textStyles.bodyMedium),
                SizedBox(height: 10.h),
                CustomDropDown(
                  items: streetDropDownItems,
                  onChanged: (value) {
                    model.street = value;
                  },
                  hint: LocalKeys.select.tr,
                  selectedItem: model.street,
                ),
                SizedBox(height: 15.h),
                if (!isFromCitation) ...{
                  Text(LocalKeys.timingType.tr, style: textStyles.bodyMedium),
                  SizedBox(height: 10.h),
                  CustomDropDown(
                    items: timingTypeDropDownItems ?? [],
                    onChanged: (value) {
                      model.timing = value;
                    },
                    hint: LocalKeys.timingType.tr,
                    selectedItem: model.timing,
                  ),
                  SizedBox(height: 15.h),
                  Text(LocalKeys.sideOfStreet.tr, style: textStyles.bodyMedium),
                  SizedBox(height: 10.h),
                  CustomDropDown(
                    items: sideDropDownItems ?? [],
                    onChanged: (value) {
                      model.selectedSide = value;
                    },
                    hint: LocalKeys.sideOfStreet.tr,
                    selectedItem: model.selectedSide,
                  ),
                  SizedBox(height: 15.h),
                } else ...{
                  Text(LocalKeys.sideOfStreet.tr, style: textStyles.bodyMedium),
                  SizedBox(height: 10.h),
                  CustomTextField(
                    hintText: LocalKeys.sideOfStreet.tr,
                    onChanged: (s) => model.side = s,
                    controller: TextEditingController(text: model.side),
                  ),
                  SizedBox(height: 15.h),
                  Text(LocalKeys.licenseNo.tr, style: textStyles.bodyMedium),
                  SizedBox(height: 10.h),
                  CustomTextField(
                    hintText: LocalKeys.licenseNo.tr,
                    onChanged: (s) => model.licenseNo = s,
                    controller: TextEditingController(text: model.licenseNo),
                  ),
                  SizedBox(height: 10.h),
                },
              ],
            ),
          ),

          Container(
            height: 1,
            width: double.infinity,
            color: AppColors.borderPrimary,
          ),
          ColorButton(
            width: double.infinity,
            margin: EdgeInsets.all(14),
            onPressed: onApply,
            label: LocalKeys.apply.tr,
          ),
        ],
      ),
    );
  }
}

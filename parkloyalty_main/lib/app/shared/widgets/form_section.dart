import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_check_box.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_date_picker_field.dart';

import '../../core/constants/field_types.dart';
import '../../core/models/template_model.dart';
import '../../core/theme/app_text_styles.dart';
import 'custom_drop_down.dart';
import 'custom_text_field.dart';

class FormSection extends StatelessWidget {
  final TemplateRes section;
  final bool showHorizontalPadding;
  final Color backgroundColor;
  final bool showVerticalPadding;
  final Function(DataSet, List<Field>, List<DataSet>, String)? onChanged;
  final Function()? onDropdownChange;
  final bool showButton;
  final Function()? onButtonPressed;
  final String? buttonText;
  final bool buildFormHorizontal;
  final Color? disableButtonBgColor;
  final Color? disableButtonFgColor;

  const FormSection({
    required this.section,
    super.key,
    this.onChanged,
    this.onDropdownChange,
    this.showHorizontalPadding = true,
    this.showVerticalPadding = false,
    this.buildFormHorizontal = false,
    this.backgroundColor = Colors.transparent,
    this.showButton = false,
    this.onButtonPressed,
    this.buttonText,
    this.disableButtonBgColor,
    this.disableButtonFgColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: .infinity,
      color: backgroundColor,
      child: Padding(
        padding: EdgeInsets.symmetric(
          horizontal: showHorizontalPadding ? AppSizes.horizontalSpacing : 0.w,
          vertical: showVerticalPadding ? AppSizes.verticalSpacing : 0.h,
        ),
        child: Column(
          spacing: 12.h,
          crossAxisAlignment: .start,
          children: [
            Text(section.component ?? "", style: textStyles.titleMedium),
            if (buildFormHorizontal) ...{
              SingleChildScrollView(
                scrollDirection: .horizontal,
                child: Row(
                  spacing: AppSizes.horizontalSpacing,
                  children: section.fields.map((field) {
                    return Column(
                      spacing: 5.h,
                      crossAxisAlignment: .start,
                      mainAxisSize: .min,
                      children: [
                        // if(field.tag != FieldTypes.checkbox)
                        // Text(field.repr, style: textStyles.bodyMedium),
                        getFieldBasedOnTag(tag: field.tag, field: field),
                      ],
                    );
                  }).toList(),
                ),
              ),
            } else ...{
              ...List.generate(section.fields.length, (index) {
                final field = section.fields[index];
                return Column(
                  spacing: 5.h,
                  crossAxisAlignment: .start,
                  children: [
                    if (field.tag != FieldTypes.checkbox) Text(field.repr, style: textStyles.bodyMedium),
                    getFieldBasedOnTag(tag: field.tag, field: field),
                  ],
                );
              }),
            },

            if (showButton)
              ColorButton(
                onPressed: onButtonPressed,
                label: buttonText ?? "",
                width: .infinity,
                disableBgColor: disableButtonBgColor,
                disableFgColor: disableButtonFgColor,
              ),
          ],
        ),
      ),
    );
  }

  Widget getFieldBasedOnTag({required String tag, required Field field}) {
    switch (tag) {
      case FieldTypes.textview || FieldTypes.editView:
        return CustomTextField(
          controller: field.enteredData,
          hintText: field.repr,
          readOnly: !field.isEditable,
          isRequired: field.isRequired,
          // onChanged: onTextFieldChanges,
        );
      case FieldTypes.textarea:
        return CustomTextField(
          controller: field.enteredData,
          hintText: field.repr,
          readOnly: !field.isEditable,
          isRequired: field.isRequired,
          maxLines: 5,
          minLines: 5,
          // onChanged: onTextFieldChanges,
        );
      case FieldTypes.dropdown:
        if (field.tag == FieldTypes.dropdown && field.dropDownOptionList == null ||
            field.tag == FieldTypes.dropdown && field.dropDownOptionList!.isEmpty)
          return CustomTextField(
            controller: field.enteredData,
            hintText: field.repr,
            readOnly: !field.isEditable,
            isRequired: field.isRequired,
          );
        return CustomDropDown(
          key: ValueKey(field.selectedDropDownOption?.label1),
          isEnabled: field.isEditable,
          items: field.dropDownOptionList ?? [],
          isRequired: field.isRequired,
          // validator: (value) {
          //
          // },
          onChanged: (data) {
            if (field.selectedDropDownOption != data) {
              field.selectedDropDownOption = data;
              logging("${field.selectedDropDownOption?.label1}");
              onChanged?.call(data!, section.fields, field.dropDownOptionList ?? [], field.name);
              onDropdownChange?.call();
            }
          },
          onClear: () {
            field.selectedDropDownOption = null;
            logging(jsonEncode(field.selectedDropDownOption?.toJson()));
          },
          hint: field.repr,
          selectedItem: field.selectedDropDownOption,
        );
      case FieldTypes.checkbox:
        return CustomCheckBox(
          label: field.repr,
          isSelected: field.isChecked,
          onChanged: (bool p1) {
            field.isChecked = p1;
          },
        );
      case FieldTypes.datePicker:
        return CustomDatePickerField(hintText: field.repr, controller: field.enteredData, isRequired: field.isRequired);
      default:
        return SizedBox.shrink();
    }
  }
}

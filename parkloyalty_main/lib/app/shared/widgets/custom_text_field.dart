import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';

class CustomTextField extends StatelessWidget {
  final String hintText;
  final String? labelText;
  final void Function(String)? onChanged;
  final bool readOnly;
  final int minLines;
  final int maxLines;
  final TextEditingController? controller;
  final bool isRequired;
  final String? Function(String?)? validator;
  final VoidCallback? onTap;
  final Widget? suffixIcon;
  final Widget? prefix;
  final bool obscureText;

  const CustomTextField({
    required this.hintText,
    this.labelText,
    this.onChanged,
    this.readOnly = false,
    this.minLines = 1,
    this.maxLines = 1,
    this.controller,
    super.key,
    this.isRequired = false,
    this.onTap,
    this.suffixIcon,
    this.validator,
    this.prefix,
    this.obscureText = false,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      spacing: labelText != null ? 5.h : 0.h,
      children: [
        Visibility(
          visible: labelText != null,
          child: Text(labelText ?? "", style: textStyles.bodyMedium),
        ),
        TextFormField(
          obscureText: obscureText,
          controller: controller,
          style: textStyles.bodyMedium,
          validator: isRequired && validator == null
              ? (s) => s == null || (s.isEmpty) ? LocalKeys.thisFieldIsRequired.tr : null
              : validator,
          onChanged: onChanged,
          onTap: onTap,
          readOnly: readOnly,
          minLines: minLines,
          maxLines: maxLines,
          onTapOutside: (event) {
            FocusManager.instance.primaryFocus?.unfocus();
          },
          autovalidateMode: AutovalidateMode.onUserInteraction,
          decoration: InputDecoration(
            hintText: hintText,
            suffixIcon: suffixIcon,
            prefixIcon: prefix,
            prefixIconConstraints: BoxConstraints(minHeight: 20.h, minWidth: 20.w),
            suffixIconConstraints: BoxConstraints(minHeight: 20.h, minWidth: 20.w),
          ),
        ),
      ],
    );
  }

  OutlineInputBorder buildOutlineInputBorder() => OutlineInputBorder(
    borderRadius: AppSizes.defaultBorderRadius,
    borderSide: BorderSide(color: AppColors.borderPrimary, width: 1),
  );
}

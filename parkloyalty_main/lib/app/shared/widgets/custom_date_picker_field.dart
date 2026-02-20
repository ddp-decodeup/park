import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_text_field.dart';

class CustomDatePickerField extends StatelessWidget {
  final String hintText;
  final String? labelText;
  final TextEditingController? controller;
  final bool isRequired;

  const CustomDatePickerField({
    super.key,
    required this.hintText,
    this.labelText,
    this.controller,
    this.isRequired = false,
  });

  @override
  Widget build(BuildContext context) {
    return CustomTextField(
      hintText: hintText,
      controller: controller,
      labelText: labelText,
      readOnly: true,
      isRequired: isRequired,
      onTap: () {
        _openDatePicker(context);
      },
    );
  }

  Future<void> _openDatePicker(BuildContext context) async {
    final dateTime = await showDatePicker(
      context: context,
      firstDate: DateTime(1900),
      lastDate: .now(),
      initialDate: .now(),
    );
    if (dateTime != null) {
      controller?.text = DateUtil.ddMMyyyy(dateTime);
    }
  }
}

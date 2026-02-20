import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/context_extensions.dart';

import '../../../../app/core/models/template_model.dart';
import '../../../../app/shared/widgets/custom_text_field.dart';

class SmallField extends StatelessWidget {
  const SmallField({super.key, required this.field});

  final Field field;

  @override
  Widget build(BuildContext context) {
    final textStyle = context.textTheme;
    return Expanded(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(field.repr, style: textStyle.bodyMedium),
          SizedBox(height: 10.h),
          CustomTextField(
            controller: field.enteredData,
            // onChanged: (s) => field.enteredData = s,
            hintText: '',
            readOnly: !field.isEditable,
          ),
        ],
      ),
    );
  }
}

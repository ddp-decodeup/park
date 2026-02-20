import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';

class CustomCheckBox extends StatefulWidget {
  const CustomCheckBox({
    required this.label,
    required this.isSelected,
    required this.onChanged,
    this.textStyle,
    super.key,
  });

  final String label;
  final bool isSelected;
  final void Function(bool) onChanged;
  final TextStyle? textStyle;

  @override
  State<CustomCheckBox> createState() => _CustomCheckBoxState();
}

class _CustomCheckBoxState extends State<CustomCheckBox> {
  bool isSelected = false;

  @override
  void initState() {
    isSelected = widget.isSelected;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        widget.onChanged.call(!isSelected);
        setState(() {
          isSelected = !isSelected;
        });
      },
      child: Row(
        children: [
          Container(
            height: 20.h,
            width: 20.w,
            margin: EdgeInsets.only(right: 10.w),
            child: Checkbox(
              value: isSelected,
              visualDensity: VisualDensity.compact,
              onChanged: (value) {
                setState(() {
                  isSelected = value!;
                });
                widget.onChanged.call(isSelected);
              },
              side: BorderSide(color: AppColors.textSubtitle, width: 0.5),
            ),
          ),
          Text(widget.label, style: (widget.textStyle ?? textStyles.labelMedium)?.copyWith(fontWeight: isSelected? FontWeight.w600:.normal,color: isSelected? AppColors.textPrimary: AppColors.textSubtitle)),
        ],
      ),
    );
  }
}

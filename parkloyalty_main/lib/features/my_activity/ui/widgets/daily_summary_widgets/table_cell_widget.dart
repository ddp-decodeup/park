import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';

class TableCellWidget extends StatelessWidget {
  final String text;

  const TableCellWidget({required this.text, super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 6),
      child: Text(
        text,
        textAlign: TextAlign.center,
        style: textStyles.labelSmall?.copyWith(
          color: AppColors.textPrimary,
          fontWeight: .w600,
        ),
      ),
    );
  }
}

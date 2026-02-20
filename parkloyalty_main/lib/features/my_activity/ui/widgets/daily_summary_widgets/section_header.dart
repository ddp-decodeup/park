import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';

class SectionHeader extends StatelessWidget {
  final String title;
  final String? trailing;

  const SectionHeader({required this.title, this.trailing, super.key});

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Text(title, style: textStyles.bodyLarge),
        const Spacer(),
        if (trailing != null) Text(trailing!, style: textStyles.bodyLarge),
      ],
    );
  }
}

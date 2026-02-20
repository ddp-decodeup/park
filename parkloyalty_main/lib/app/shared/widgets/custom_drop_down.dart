import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';

import '../../core/models/drop_down_model.dart';
import 'dropdown.dart';

class CustomDropDown extends StatelessWidget {
  final List<DataSet> items;
  final DataSet? selectedItem;
  final ValueChanged<DataSet?>? onChanged;
  final String hint;
  final bool isRequired;
  final bool isEnabled;
  final String? Function(DataSet?)? validator;
  final String? labelText;
  final void Function()? onClear;

  const CustomDropDown({
    super.key,
    required this.items,
    required this.onChanged,
    this.selectedItem,
    this.hint = '',
    this.isRequired = false,
    this.isEnabled = true,
    this.validator,
    this.labelText,
    this.onClear,
  });

  @override
  Widget build(BuildContext context) {
    return Dropdown<DataSet>(
      dropdownMenuEntries: items.map((item) {
        return DropdownMenuEntry(
          value: item,
          label: "${item.label1 ?? ""}",
          style: ButtonStyle(textStyle: WidgetStatePropertyAll(textStyles.bodyMedium)),
        );
      }).toList(),

      hintText: hint,
      searchController: TextEditingController(text: selectedItem?.label1),
      onClear: onClear,
      isRequired: isRequired,
      initialSelection: selectedItem,
      isEnabled: isEnabled,
      validator: validator,
      autoValidateMode: AutovalidateMode.onUserInteraction,
      onSelected: onChanged,
      labelText: labelText,
    );
  }
}

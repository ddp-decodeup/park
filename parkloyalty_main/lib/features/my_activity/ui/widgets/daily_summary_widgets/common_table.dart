import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/features/my_activity/data/models/daily_summary_table_model.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/table_cell_widget.dart';

class CommonTable extends StatelessWidget {
  final List<DailySummaryTableModel> data;

  const CommonTable({super.key, required this.data});

  @override
  Widget build(BuildContext context) {
    return Table(
      border: TableBorder.all(color: AppColors.textPrimary),
      children: [
        TableRow(
          children: List.generate(data.length, (index) {
            final dataItem = data[index];
            return TableCellWidget(text: dataItem.heading);
          }),
        ),
        TableRow(
          children: List.generate(data.length, (index) {
            final dataItem = data[index];
            return TableCellWidget(text: dataItem.value);
          }),
        ),
      ],
    );
  }
}

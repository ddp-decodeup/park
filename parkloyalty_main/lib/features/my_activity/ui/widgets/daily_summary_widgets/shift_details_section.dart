import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/features/my_activity/data/models/daily_summary_table_model.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/common_table.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/section_header.dart';

class ShiftDetailsSection extends StatelessWidget {
  final List<List<DailySummaryTableModel>> shiftDetailsTableData;

  const ShiftDetailsSection({super.key, required this.shiftDetailsTableData});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SectionHeader(title: 'Shift Details'),
        SizedBox(height: 12.h),
        ...List.generate(shiftDetailsTableData.length, (index) {
          final data = shiftDetailsTableData[index];
          return CommonTable(data: data);
        }),
      ],
    );
  }
}

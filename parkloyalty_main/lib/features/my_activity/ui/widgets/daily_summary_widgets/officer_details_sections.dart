import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/features/my_activity/data/models/daily_summary_table_model.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/common_table.dart';

import 'section_header.dart';

class OfficerDetailsSection extends StatelessWidget {
  final List<List<DailySummaryTableModel>> officerTableData;

  const OfficerDetailsSection({super.key, required this.officerTableData});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(
          title: 'Officer Details',
          trailing: DateUtil.simplifiedDate2(.now()),
        ),
        SizedBox(height: 12.h),
        ...List.generate(officerTableData.length, (index) {
          final data = officerTableData[index];
          return CommonTable(data: data);
        }),
      ],
    );
  }
}

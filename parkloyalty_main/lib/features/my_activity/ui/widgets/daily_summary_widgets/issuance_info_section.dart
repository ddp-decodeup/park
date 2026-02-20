import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/features/my_activity/data/models/daily_summary_table_model.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/common_table.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/section_header.dart';

class IssuanceInfoSection extends StatelessWidget {
  final List<List<DailySummaryTableModel>> officerTableData;
  final String totalCount;

  const IssuanceInfoSection({
    super.key,
    required this.officerTableData,
    required this.totalCount,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(
          title: 'Issuance Info',
          trailing: LocalKeys.totalCount.tr + " : $totalCount",
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

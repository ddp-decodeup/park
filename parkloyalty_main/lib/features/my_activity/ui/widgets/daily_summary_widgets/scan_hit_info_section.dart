import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/features/my_activity/data/models/daily_summary_table_model.dart';

import 'common_table.dart';
import 'section_header.dart';

class ScanHitInfoSection extends StatelessWidget {
  final List<List<DailySummaryTableModel>> scanHitTableData;
  final String totalCount;

  const ScanHitInfoSection({
    super.key,
    required this.scanHitTableData,
    required this.totalCount,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SectionHeader(
          title: 'Scan Hit Info',
          trailing: LocalKeys.totalScans.tr + " : $totalCount",
        ),
        SizedBox(height: 12.h),
        ...List.generate(scanHitTableData.length, (index) {
          final data = scanHitTableData[index];
          return CommonTable(data: data);
        }),
      ],
    );
  }
}

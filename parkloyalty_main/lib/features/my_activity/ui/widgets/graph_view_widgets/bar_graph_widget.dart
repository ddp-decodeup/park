import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

import '../../../data/models/chart_data_model.dart';

class BarGraphWidget extends StatelessWidget {
  final List<ChartDataModel> chartData;

  const BarGraphWidget({super.key, required this.chartData});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Container(
          height: 0.3.sh,
          alignment: .center,
          padding: EdgeInsets.fromLTRB(10.w, 0.h, 10.w, 0),
          child: SfCartesianChart(
            plotAreaBorderWidth: 0,
            primaryXAxis: CategoryAxis(
              arrangeByIndex: true,
              labelRotation: -270,
              axisLine: const AxisLine(width: 1, color: AppColors.iconGray),
              majorTickLines: const MajorTickLines(size: 0),
              labelStyle: textStyles.labelSmall?.copyWith(
                color: AppColors.textPrimary,
                fontWeight: .w500,
              ),
            ),
            primaryYAxis: NumericAxis(
              majorGridLines: MajorGridLines(
                width: 1.5,
                color: AppColors.borderDotted,
              ),
              minorGridLines: MinorGridLines(
                width: 1.5,
                color: AppColors.borderDotted,
              ),
              axisLine: const AxisLine(width: 1, color: AppColors.iconGray),
              majorTickLines: const MajorTickLines(size: 0),
              labelStyle: textStyles.labelSmall?.copyWith(
                color: AppColors.textPrimary,
                fontWeight: .w500,
              ),
            ),
            series: <ColumnSeries<ChartDataModel, String>>[
              ColumnSeries<ChartDataModel, String>(
                dataSource: chartData,
                xValueMapper: (data, _) => data.key.capitalize,
                yValueMapper: (data, _) => data.value,
                color: AppColors.warningYellow,
                width: 1,
                spacing: 0.5,
              ),
            ],
          ),
        ),
        SizedBox(height: AppSizes.verticalSpacing),
        Padding(
          padding: EdgeInsets.symmetric(horizontal: 24.w),
          child: Row(
            spacing: 5.w,
            children: [
              Container(
                height: 10.h,
                width: 10.h,
                color: AppColors.warningYellow,
              ),
              Text(
                LocalKeys.numberOfCitation.tr,
                style: textStyles.labelSmall?.copyWith(
                  color: AppColors.textPrimary,
                  fontWeight: .w500,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

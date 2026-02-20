import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart' show AppColors;
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/features/my_activity/data/models/chart_data_model.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

import '../../../../../app/core/constants/app_sizes.dart';

class LineChartWidget extends StatelessWidget {
  final List<LineChartModel> lineChartData;

  const LineChartWidget({super.key, required this.lineChartData});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 0.4.sh,
      alignment: .center,
      padding: EdgeInsets.symmetric(horizontal: AppSizes.horizontalSpacing),

      child: Column(
        spacing: 10.h,
        children: [
          Expanded(
            child: SfCartesianChart(
              plotAreaBorderWidth: 0,
              primaryXAxis: NumericAxis(
                axisLine: const AxisLine(width: 1, color: AppColors.borderDotted),
                majorTickLines: const MajorTickLines(size: 0),
                majorGridLines: MajorGridLines(color: AppColors.borderDotted, width: 1.5),
                minorGridLines: MinorGridLines(color: AppColors.borderDotted, width: 1.5),
                labelStyle: textStyles.labelSmall?.copyWith(color: AppColors.textPrimary),
                interval: 2.0,
                opposedPosition: true,
              ),
              primaryYAxis: NumericAxis(
                majorGridLines: MajorGridLines(width: 1.5, color: AppColors.borderDotted),
                minorGridLines: MinorGridLines(width: 1.5, color: AppColors.borderDotted),
                axisLine: AxisLine(width: 1, color: AppColors.borderDotted),
                majorTickLines: const MajorTickLines(size: 0),
                labelStyle: textStyles.labelSmall?.copyWith(color: AppColors.textPrimary),
              ),
              series: List<LineSeries<ChartDataModel, int>>.generate(lineChartData.length, (index) {
                final lineData = lineChartData[index];
                return LineSeries<ChartDataModel, int>(
                  dataSource: lineData.data,
                  xValueMapper: (data, _) => int.parse(data.key),
                  yValueMapper: (data, _) => data.value,
                  color: lineData.color,
                  width: 2,
                );
              }),
            ),
          ),
          Padding(
            padding: EdgeInsets.only(left: AppSizes.horizontalSpacing),
            child: Row(
              mainAxisAlignment: .start,
              spacing: AppSizes.horizontalSpacing,
              children: List.generate(lineChartData.length, (index) {
                final item = lineChartData[index];
                return _buildLegend(item.color, item.label);
              }),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLegend(Color color, String text) {
    return Container(
      padding: EdgeInsetsGeometry.symmetric(vertical: AppSizes.defaultVertical.top / 2),

      child: Row(
        children: [
          Container(
            width: 10.h,
            height: 10.h,
            decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(2.r)),
          ),
          SizedBox(width: 5.w),
          Text(text, style: textStyles.labelSmall?.copyWith(color: AppColors.textPrimary)),
        ],
      ),
    );
  }
}

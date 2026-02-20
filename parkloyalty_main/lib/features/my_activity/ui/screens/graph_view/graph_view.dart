import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/loader.dart';
import 'package:park_enfoecement/features/my_activity/controller/graph_view_controller.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/graph_view_widgets/activity_log_widget.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/graph_view_widgets/bar_graph_widget.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/graph_view_widgets/line_chart_widget.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/graph_view_widgets/route_details_widget.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/graph_view_widgets/violation_details_widget.dart';

import '../../../../../app/core/routes/app_routes.dart';

class GraphView extends GetView<GraphViewController> {
  const GraphView({super.key});

  @override
  Widget build(BuildContext context) {
    final divider = Divider(color: AppColors.borderDotted);
    final defaultPadding = EdgeInsets.only(
      left: AppSizes.defaultHorizontal.left,
      right: AppSizes.defaultHorizontal.right,
      top: AppSizes.defaultVertical.top / 2,
      bottom: AppSizes.defaultVertical.bottom,
    );
    return Loader(
      child: AppScaffold(
        showDivider: true,
        onBack: () {
          Get.back();
        },
        body: GetBuilder<GraphViewController>(
          builder: (controller) {

            return Padding(
              padding: EdgeInsets.only(top: AppSizes.verticalSpacing, bottom: 30.h),
              child: SingleChildScrollView(
                child: Column(
                  spacing: AppSizes.horizontalSpacing,
                  crossAxisAlignment: .start,
                  children: [
                    Padding(
                      padding: defaultPadding,
                      child: Text(LocalKeys.summary.tr, style: textStyles.titleMedium),
                    ),

                    BarGraphWidget(chartData: controller.barChartData),

                    divider,

                    LineChartWidget(lineChartData: controller.lineChartData),

                    divider,
                    ViolationDetailsWidget(violationData: controller.violationData.value.data?.first.resonse ?? []),

                    Padding(
                      padding: defaultPadding,
                      child: GetBuilder<GraphViewController>(
                        builder: (controller) => controller.locationData.isNotEmpty
                            ? RouteDetailsWidget(
                                locations: controller.locationData,
                                onTap: (_, __) {
                                  Get.toNamed(Routes.lprHits);
                                },
                              )
                            : Center(child: CircularProgressIndicator()),
                      ),
                    ),

                    Padding(
                      padding: defaultPadding,
                      child: ActivityLogWidget(activityLogs: controller.activityLogData),
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

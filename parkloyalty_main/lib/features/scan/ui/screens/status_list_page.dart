import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';

import '../../../../app/core/constants/app_sizes.dart';
import '../../../../app/core/localization/local_keys.dart';
import '../../../../app/shared/widgets/color_button.dart';
import '../../../../app/shared/widgets/no_data_layout.dart';
import '../../../lookup/ui/widgets/timing_record_list_item.dart';
import '../../controllers/status_controller.dart';

class StatusListPage extends StatelessWidget {
  const StatusListPage({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<StatusController>(
      builder: (c) {
        return SingleChildScrollView(
          child: Column(
            children: [
              ListView.builder(
                shrinkWrap: true,
                padding: EdgeInsetsGeometry.symmetric(vertical: 15.h),
                itemCount: c.statusList.length,
                itemBuilder: (context, index) {
                  final model = c.statusList[index];
                  return TimingRecordListItem(data: model);
                },
              ),
              if (c.statusList.isEmpty)
                Padding(
                  padding: EdgeInsetsGeometry.symmetric(vertical: 20.h),
                  child: NoDataLayout(message: LocalKeys.noStatus.tr),
                ),
              ColorButton(
                onPressed: () => Get.toNamed(Routes.ticketIssue),
                label: LocalKeys.issueTicket.tr,
                margin: AppSizes.defaultHorizontal,
              ),
            ],
          ),
        );
      },
    );
  }
}

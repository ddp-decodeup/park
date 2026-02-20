import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/app/shared/widgets/no_data_layout.dart';
import 'package:park_enfoecement/features/scan/ui/widgets/history_list_item.dart';

import '../../../../app/core/constants/app_sizes.dart';
import '../../../../app/core/routes/app_routes.dart';
import '../../controllers/cite_history_controller.dart';

class CiteHistoryListPage extends StatelessWidget {
  const CiteHistoryListPage({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CiteHistoryController>(
      builder: (c) {
        return SingleChildScrollView(
          child: Column(
            children: [
              ListView.builder(
                shrinkWrap: true,
                padding: AppSizes.defaultHorizontal,
                itemCount: c.historyList.length,
                itemBuilder: (_, i) => HistoryListItem(model: c.historyList[i]),
              ),
              if (c.historyList.isEmpty) ...{
                Padding(
                  padding: EdgeInsetsGeometry.symmetric(vertical: 20.h),
                  child: NoDataLayout(message: LocalKeys.noCitations.tr),
                ),
              },
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

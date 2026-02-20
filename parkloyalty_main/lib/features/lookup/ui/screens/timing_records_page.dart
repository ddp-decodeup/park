import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/shared/widgets/no_data_layout.dart';
import 'package:park_enfoecement/features/lookup/ui/widgets/filter_section.dart';

import '../../../../app/core/theme/app_colors.dart';
import '../../../../app/core/theme/app_text_styles.dart';
import '../../../../app/shared/widgets/custom_check_box.dart';
import '../../controllers/timing_records_controller.dart';
import '../widgets/timing_record_list_item.dart';

class TimingRecordsPage extends StatelessWidget {
  const TimingRecordsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<TimingRecordsController>(
      builder: (c) {
        return Column(
          children: [
            FilterSection(
              streetDropDownItems: c.streetList,
              searchHint: LocalKeys.lPRNumber.tr,
              req: c.timingRecordReq,
              isFromCitation: false,
              timingTypeDropDownItems: c.timeTypeList,
              onApply: () => c.applyFilter(),
              onSearchComplete: (s) => c.searchRecord(s),
            ),

            Padding(
              padding: EdgeInsets.only(top: 10.h, left: 14.w, right: 14.w),
              child: Column(
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        LocalKeys.recentAdded.tr,
                        style: textStyles.titleMedium?.copyWith(color: AppColors.textHeader, fontWeight: .w600),
                      ),
                      GestureDetector(
                        onTap: () => c.mark(),
                        child: Container(
                          padding: EdgeInsets.symmetric(horizontal: 15.w, vertical: 8.h),
                          decoration: BoxDecoration(
                            color: c.selectedRecordList.isNotEmpty ? AppColors.primaryBlue : AppColors.buttonDisabled,
                            borderRadius: BorderRadius.circular(5.r),
                          ),
                          child: Text(
                            LocalKeys.markGOA.tr,
                            style: textStyles.bodySmall?.copyWith(
                              color: c.selectedRecordList.isNotEmpty ? AppColors.pureWhite : null,
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                  SizedBox(height: 10.h),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      CustomCheckBox(
                        key: ValueKey(c.checkBoxKey),
                        label: LocalKeys.selectAll.tr,
                        isSelected: c.isAllSelected(),
                        onChanged: (b) {
                          c.checkUncheckAll(b);
                        },
                        textStyle: textStyles.bodyMedium?.copyWith(color: AppColors.textSubtitle, fontWeight: .w500),
                      ),
                      if (c.selectedRecordList.isNotEmpty)
                        Text(
                          '${c.selectedRecordList.length} ${LocalKeys.itemsSelected.tr}',
                          style: textStyles.bodyMedium?.copyWith(color: AppColors.textSubtitle, fontWeight: .w500),
                        ),
                    ],
                  ),
                ],
              ),
            ),
            Expanded(
              child: (!c.isLoading && c.timingRecordList.isEmpty)
                  ? NoDataLayout(message: LocalKeys.noTimingRecords.tr)
                  : ListView.builder(
                      key: ValueKey(c.listValueKey),
                      controller: c.scrollController,
                      itemCount: c.timingRecordList.length + (c.isLoading ? 1 : 0),
                      itemBuilder: (context, index) {
                        if (index < c.timingRecordList.length) {
                          final model = c.timingRecordList[index];
                          return TimingRecordListItem(
                            data: model,
                            onSelectionChanged: (b) => c.selectRecord(model),
                            isSelected: c.selectedRecordList.contains(model.id),
                          );
                        } else {
                          return Padding(
                            padding: EdgeInsets.symmetric(vertical: 16.h, horizontal: 16.w),
                            child: const Center(child: CircularProgressIndicator()),
                          );
                        }
                      },
                    ),
            ),
          ],
        );
      },
    );
  }
}

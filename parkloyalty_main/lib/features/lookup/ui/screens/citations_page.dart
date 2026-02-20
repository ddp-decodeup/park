import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/widgets/no_data_layout.dart';
import 'package:park_enfoecement/features/lookup/controllers/citations_controller.dart';
import 'package:park_enfoecement/features/lookup/ui/widgets/citation_list_item.dart';
import 'package:park_enfoecement/features/lookup/ui/widgets/filter_section.dart';

import '../../../../app/core/theme/app_text_styles.dart';

class CitationsPage extends StatelessWidget {
  const CitationsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CitationsController>(
      builder: (c) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            FilterSection(
              streetDropDownItems: c.streetList,
              searchHint: LocalKeys.ticketNo.tr,
              req: c.citationReq,
              onApply: () => c.applyCitationFilter(),
              onSearchComplete: (s) => c.searchTicket(s),
            ),
            Padding(
              padding: EdgeInsets.only(top: 10.h, left: 14.w),
              child: Text(
                LocalKeys.recentAdded.tr,
                style: textStyles.titleMedium?.copyWith(
                  color: AppColors.textHeader,
                  fontWeight: .w600,
                ),
              ),
            ),
            Expanded(
              child: (!c.isLoading && c.citationList.isEmpty)
                  ? NoDataLayout(message: LocalKeys.noCitations.tr)
                  : ListView.builder(
                      controller: c.scrollController,
                      itemCount: c.citationList.length + (c.isLoading ? 1 : 0),
                      itemBuilder: (context, index) {
                        if (index < c.citationList.length) {
                          final model = c.citationList[index];
                          return CitationListItem(data: model);
                        } else {
                          return Padding(
                            padding: EdgeInsets.all(AppSizes.verticalSpacing),
                            child: const Center(
                              child: CircularProgressIndicator(),
                            ),
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

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_tab_bar.dart';
import 'package:park_enfoecement/features/lookup/ui/screens/citations_page.dart';
import 'package:park_enfoecement/features/lookup/ui/screens/timing_records_page.dart';

import '../../../../app/core/localization/local_keys.dart';

class CitationResultPage extends StatelessWidget {
  const CitationResultPage({super.key});

  @override
  Widget build(BuildContext context) {
    return AppScaffold(
      onBack: Get.back,
      body: DefaultTabController(
        length: 2,
        child: Column(
          children: [
            CustomTabBar(
              tabList: [
                Tab(text: LocalKeys.citations.tr),
                Tab(text: LocalKeys.timingRecords.tr),
              ],
            ),
            Expanded(child: TabBarView(children: [CitationsPage(), TimingRecordsPage()])),
          ],
        ),
      ),
    );
  }
}

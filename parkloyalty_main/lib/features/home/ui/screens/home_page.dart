import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart' hide Response;
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_bottom_nav_bar.dart';
import 'package:park_enfoecement/app/shared/widgets/form_section.dart';
import 'package:park_enfoecement/app/shared/widgets/loader.dart';
import 'package:park_enfoecement/app/shared/widgets/outline_button.dart';
import 'package:park_enfoecement/features/home/ui/widgets/officer_details_widget.dart';

import '../../controller/home_controller.dart';

class HomePage extends GetView<HomeController> {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Loader(
      child: GetBuilder<HomeController>(
        builder: (c) => AppScaffold(
          showShadowWhileScroll: true,
          userName: '${c.user?.officerFirstName ?? ''} ${c.user?.officerLastName ?? ''}'.trim(),
          body: Visibility(
            visible: c.res.isNotEmpty,
            child: SingleChildScrollView(
              child: Padding(
                padding: AppSizes.defaultVertical,
                child: Column(
                  spacing: 12.h,
                  children: [
                    Column(
                      crossAxisAlignment: .start,
                      spacing: 10.h,
                      children: [
                        Padding(
                          padding: EdgeInsets.symmetric(horizontal: AppSizes.horizontalSpacing),
                          child: RichText(
                            text: TextSpan(
                              children: [
                                TextSpan(text: LocalKeys.welcomeBack.tr + ",\n", style: textStyles.titleSmall),
                                TextSpan(
                                  text: (c.user?.officerFirstName ?? "") + " " + (c.user?.officerLastName ?? ""),
                                  style: textStyles.titleMedium,
                                ),
                              ],
                            ),
                          ),
                        ),
                        Padding(
                          padding: EdgeInsetsGeometry.symmetric(horizontal: AppSizes.horizontalSpacing),
                          child: OfficerDetailsWidget(
                            previousLoginDate: c.previousLogin,
                            currentLoginDate: c.currentLogin,
                            officerName: '${c.user?.officerFirstName ?? ''} ${c.user?.officerLastName ?? ''}'.trim(),
                            officerId: (c.user?.officerBadgeId ?? '').toString(),
                          ),
                        ),

                        Form(
                          key: c.formKey,
                          child: Column(
                            crossAxisAlignment: .start,
                            spacing: AppSizes.verticalSpacing * 1.5,
                            children: List.generate(c.res.length, (index) {
                              final section = c.res[index];
                              return FormSection(section: section);
                            }),
                          ),
                        ),
                      ],
                    ),
                    // TODO: Add inventory widget
                    // InventoryWidget(),
                    Padding(
                      padding: AppSizes.defaultHorizontal,
                      child: Row(
                        children: [
                          Expanded(
                            child: OutlineButton(onPressed: c.updateOfficer, label: LocalKeys.done.tr),
                          ),
                          SizedBox(width: 20.w),
                          Expanded(
                            child: ColorButton(onPressed: c.onScanPressed, label: LocalKeys.scan.tr),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
          bottomNavigationBar: CustomBottomNavBar(),
        ),
      ),
    );
  }
}

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/widgets/app_divider.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_appbar.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_text_field.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';
import 'package:park_enfoecement/features/scan/controllers/manual_data_entry_controller.dart';
import 'package:park_enfoecement/features/scan/ui/screens/status_list_page.dart';
import 'package:park_enfoecement/features/scan/ui/widgets/alphabet_cell.dart';

import '../../../../app/core/theme/app_text_styles.dart';
import '../../../../app/shared/widgets/custom_tab_bar.dart';
import '../../../../app/shared/widgets/outline_button.dart';
import 'cite_history_list_page.dart';

/*class ManualDataEntryPage extends StatelessWidget {
  const ManualDataEntryPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(centerTitle: true, onBack: () {}),
      body: Padding(
        padding: AppSizes.defaultHorizontal,
        child: SingleChildScrollView(
          child: Column(
            children: [
              AppDivider(marginBottom: 10.h,),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(LocalKeys.lprImage.tr, style: textStyles.titleMedium),
                  RenderSvgImage(assetName: AppIcons.cameraIcon),
                ],
              ),
              SizedBox(height: 20.h),
              Container(
                width: double.infinity,
                height: 100.h,
                clipBehavior: Clip.hardEdge,
                decoration: BoxDecoration(borderRadius: BorderRadius.circular(10.r), color: AppColors.borderDotted),
              ),
              Row(
                children: ['S','S','S'].map((e) => AlphabetCell(alphabet: e),).toList(),
              ),
              Row(
                children: [
                  Expanded(
                    child: CustomTextField(hintText: LocalKeys.lPRNumber.tr,suffixIcon: GestureDetector(
                      onTap: () {

                      },
                      child: Padding(
                        padding: EdgeInsets.only(right: 15.w),
                        child: RenderSvgImage(assetName: AppIcons.clearText),
                      )
                    )),
                  ),
                  Padding(
                    padding: EdgeInsets.only(left:15.w),
                    child: RenderSvgImage(assetName: AppIcons.warningIcon),
                  ),

                ],
              ),
              SizedBox(height: 20.h,),
              Row(
                children: [
                  Expanded(
                    child: ColorButton(onPressed: () {

                    }, label: LocalKeys.reScan.tr,bgColor:AppColors.accentPurple,icon: AppIcons.reScan,),
                  ),
                  SizedBox(width: 15.w,),
                  Expanded(
                    child: OutlineButton(onPressed: () {

                    },label: LocalKeys.check.tr,color: AppColors.errorRed,),
                  ),
                ],
              ),
              AppDivider(marginTop: 15.h,marginBottom: 15.h),
              Row(
                children: [
                  Expanded(
                    child: ColorButton(onPressed: () {

                    }, label: LocalKeys.permit.tr,bgColor:AppColors.buttonDisabled),
                  ),
                  SizedBox(width: 10.w,),
                  Expanded(
                    child: ColorButton(onPressed: () {

                    }, label: LocalKeys.payment.tr,bgColor:AppColors.buttonDisabled),
                  )
                ],
              ),
              SizedBox(height: 10.w),
              ColorButton(onPressed: () {

              }, label: LocalKeys.exempt.tr,bgColor:AppColors.buttonDisabled),
              SizedBox(height: 10.w),
              Row(
                children: [
                  Expanded(
                    child: ColorButton(onPressed: () {

                    }, label: LocalKeys.scofflaw.tr,bgColor:AppColors.buttonDisabled),
                  ),
                  SizedBox(width: 10.w,),
                  Expanded(
                    child: ColorButton(onPressed: () {

                    }, label: LocalKeys.timings.tr,bgColor:AppColors.buttonDisabled),
                  )
                ],
              ),
              AppDivider(marginTop: 15.h,marginBottom: 15.h,),
              DefaultTabController(
                length: 2,
                child: Column(
                  children: [
                    CustomTabBar(tabList: [
                      Tab(text: LocalKeys.citations.tr),
                      Tab(text: LocalKeys.timingRecords.tr),
                    ],),
                    SizedBox(
                      height: 100,
                      child: TabBarView(
                        children: [Text('data'), Text('data')],
                      ),
                    ),
                  ],
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}*/

class ManualDataEntryPage extends StatelessWidget {
  const ManualDataEntryPage({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<ManualDataEntryController>(
      builder: (c) {
        return DefaultTabController(
          length: 2,
          child: Scaffold(
            appBar: CustomAppBar(centerTitle: true, onBack: Get.back,showDivider: true,),
            body: NestedScrollView(
              headerSliverBuilder: (context, innerBoxIsScrolled) {
                return [
                  SliverToBoxAdapter(
                    child: Padding(
                      padding: AppSizes.defaultHorizontal,
                      child: Column(
                        children: [
                          SizedBox(height: AppSizes.verticalSpacing,),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Text(LocalKeys.lprImage.tr, style: textStyles.titleMedium),
                              RenderSvgImage(assetName: AppIcons.cameraIcon),
                            ],
                          ),
                          SizedBox(height: 20.h),
                          Container(
                            width: double.infinity,
                            height: 100.h,
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(10.r),
                              color: AppColors.borderDotted,
                            ),
                          ),
                          if (c.lprNumber.isEmpty) ...{SizedBox(height: 15.h)},
                          Row(
                            children: [
                              for (String s in c.lprNumber.split('')) ...{if (s.isNotEmpty) AlphabetCell(alphabet: s)},
                            ],
                          ),
                          Row(
                            children: [
                              Expanded(
                                child: CustomTextField(
                                  controller: TextEditingController(text: c.lprNumber),
                                  onChanged: c.onChangeLpr,
                                  hintText: LocalKeys.lPRNumber.tr,
                                  suffixIcon: GestureDetector(
                                    onTap: c.clearLprNumber,
                                    child: Padding(
                                      padding: EdgeInsets.only(right: 15.w),
                                      child: RenderSvgImage(assetName: AppIcons.clearText),
                                    ),
                                  ),
                                ),
                              ),
                              Padding(
                                padding: EdgeInsets.only(left: 15.w),
                                child: RenderSvgImage(assetName: AppIcons.warningIcon),
                              ),
                            ],
                          ),
                          SizedBox(height: 20.h),
                          Row(
                            children: [
                              Expanded(
                                child: ColorButton(
                                  onPressed: () {},
                                  label: LocalKeys.reScan.tr,
                                  bgColor: AppColors.accentPurple,
                                  icon: AppIcons.reScan,
                                ),
                              ),
                              SizedBox(width: 15.w),
                              Expanded(
                                child: OutlineButton(
                                  onPressed: () {
                                    c.check();
                                  },
                                  label: LocalKeys.check.tr,
                                  color: AppColors.errorRed,
                                ),
                              ),
                            ],
                          ),
                          AppDivider(marginTop: 15.h, marginBottom: 15.h),
                          Row(
                            children: [
                              Expanded(
                                child: ColorButton(
                                  onPressed: () {},
                                  label: LocalKeys.permit.tr,
                                  bgColor: AppColors.buttonDisabled,
                                ),
                              ),
                              SizedBox(width: 10.w),
                              Expanded(
                                child: ColorButton(
                                  onPressed: () {},
                                  label: LocalKeys.payment.tr,
                                  bgColor: AppColors.buttonDisabled,
                                ),
                              ),
                            ],
                          ),
                          SizedBox(height: 10.w),
                          ColorButton(onPressed: () {}, label: LocalKeys.exempt.tr, bgColor: AppColors.buttonDisabled),
                          SizedBox(height: 10.w),
                          Row(
                            children: [
                              Expanded(
                                child: ColorButton(
                                  onPressed: () {},
                                  label: LocalKeys.scofflaw.tr,
                                  bgColor: AppColors.buttonDisabled,
                                ),
                              ),
                              SizedBox(width: 10.w),
                              Expanded(
                                child: ColorButton(
                                  onPressed: () {},
                                  label: LocalKeys.timings.tr,
                                  bgColor: AppColors.buttonDisabled,
                                ),
                              ),
                            ],
                          ),
                          AppDivider(marginTop: 15.h, marginBottom: 15.h),
                        ],
                      ),
                    ),
                  ),

                  /// 🔥 STICKY TAB BAR
                  SliverPersistentHeader(
                    pinned: true,
                    delegate: _StickyTabBarDelegate(
                      CustomTabBar(
                        tabList: [
                          Tab(text: LocalKeys.status.tr),
                          Tab(text: LocalKeys.citeHistory.tr),
                        ],
                      ),
                    ),
                  ),
                ];
              },

              /// TAB CONTENT
              body: TabBarView(children: [StatusListPage(), CiteHistoryListPage()]),
            ),
          ),
        );
      },
    );
  }
}

class _StickyTabBarDelegate extends SliverPersistentHeaderDelegate {
  final Widget tabBar;

  _StickyTabBarDelegate(this.tabBar);

  @override
  double get minExtent => 48;

  @override
  double get maxExtent => 48;

  @override
  Widget build(BuildContext context, double shrinkOffset, bool overlapsContent) {
    return Container(color: Theme.of(context).scaffoldBackgroundColor, child: tabBar);
  }

  @override
  bool shouldRebuild(covariant SliverPersistentHeaderDelegate oldDelegate) {
    return false;
  }
}

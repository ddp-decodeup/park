import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_drop_down.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_text_field.dart';
import 'package:park_enfoecement/app/shared/widgets/loader.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';
import 'package:park_enfoecement/features/ticketing/controller/pay_by_space_controller.dart';
import 'package:park_enfoecement/features/ticketing/ui/widgets/empty_state_widget.dart';

class PayBySpaceScreen extends GetView<PayBySpaceController> {
  const PayBySpaceScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Loader(
      child: AppScaffold(
        onBack: () {
          Get.back();
        },
        showDivider: true,

        body: Padding(
          padding: EdgeInsets.symmetric(horizontal: 16.w, vertical: 16.h),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            spacing: 16.h,
            children: [
              CustomTextField(
                hintText: LocalKeys.lPRNumber.tr,
                controller: controller.lpNumberController,
                onChanged: controller.onChangePlateNumber,
                prefix: Padding(
                  padding: EdgeInsetsGeometry.only(left: 16.w, right: 8.w),
                  child: RenderSvgImage(assetName: AppIcons.searchIcon),
                ),
              ),
              CustomDropDown(
                items: controller.zoneList,
                onChanged: controller.onZoneChanged,
                hint: "Select Lot",
                labelText: "Select Lot",
              ),
              EmptyStateWidget(),
            ],
          ),
        ),
      ),
    );
  }
}

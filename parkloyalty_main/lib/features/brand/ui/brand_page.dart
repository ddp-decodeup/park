import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/app_logo.dart';
import 'package:park_enfoecement/app/shared/widgets/app_version_and_refresh_button.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_text_field.dart';
import 'package:park_enfoecement/features/brand/controller/brand_controller.dart';
import 'package:park_enfoecement/features/login/ui/widgets/custom_background.dart';

class BrandPage extends GetView<BrandController> {
  const BrandPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: CustomBackground(
        child: SafeArea(
          child: SingleChildScrollView(
            child: Padding(
              padding: EdgeInsets.symmetric(horizontal: 16.w, vertical: 32.h),
              child: Form(
                child: GetBuilder<BrandController>(
                  builder: (controller) {
                    if (controller.isLoading) {
                      return const Padding(
                        padding: EdgeInsets.symmetric(vertical: 40),
                        child: Center(child: CircularProgressIndicator()),
                      );
                    }
                    return Column(
                      spacing: AppSizes.verticalSpacing,
                      children: [
                        const AppLogo(),
                        Text(LocalKeys.brandDetails.tr, style: textStyles.headlineSmall),
                        CustomTextField(
                          controller: controller.siteIdController,
                          hintText: LocalKeys.idHint.tr,
                          labelText: LocalKeys.id.tr,
                        ),
                        CustomTextField(
                          controller: controller.customerNameController,
                          hintText: LocalKeys.customerHint.tr,
                          labelText: LocalKeys.customerName.tr,
                        ),
                        // Dropdown<String>(
                        //   dropdownMenuEntries: controller.siteIds
                        //       .map((siteId) => DropdownMenuEntry<String>(value: siteId, label: siteId))
                        //       .toList(),
                        //   isRequired: true,
                        //   hintText: LocalKeys.idHint.tr,
                        //   labelText: LocalKeys.id.tr,
                        //   initialSelection: controller.selectedSiteId,
                        //   onSelected: controller.onSiteChanged,
                        //   searchController: SearchController(),
                        // ),
                        // Dropdown<String>(
                        //   dropdownMenuEntries: controller.customers
                        //       .map((customer) => DropdownMenuEntry<String>(value: customer, label: customer))
                        //       .toList(),
                        //   isRequired: true,
                        //   hintText: LocalKeys.customerHint.tr,
                        //   labelText: LocalKeys.customerName.tr,
                        //   initialSelection: controller.selectedCustomer,
                        //   onSelected: controller.onCustomerChanged,
                        //   searchController: SearchController(),
                        // ),
                        CustomTextField(
                          controller: controller.baseUrlController,
                          hintText: LocalKeys.urlHint.tr,
                          labelText: LocalKeys.url.tr,
                        ),
                        SizedBox.shrink(),
                        ColorButton(
                          onPressed: () async {
                            final isSaved = await controller.persistSelection();
                            if (!isSaved) return;
                            Get.offAllNamed(Routes.splash);
                          },
                          label: LocalKeys.next.tr,
                          width: double.infinity,
                        ),
                        SizedBox(height: 30.h),
                        AppVersionAndRefreshButtonWidget(),
                      ],
                    );
                  },
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

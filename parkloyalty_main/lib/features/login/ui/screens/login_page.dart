import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/app_logo.dart';
import 'package:park_enfoecement/app/shared/widgets/app_version_and_refresh_button.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_bottom_sheet_field.dart';
import 'package:park_enfoecement/app/shared/widgets/loader.dart';
import 'package:park_enfoecement/features/login/ui/widgets/custom_background.dart';

import '../../../../app/shared/widgets/color_button.dart';
import '../../../../app/shared/widgets/custom_text_field.dart';
import '../../controller/login_controller.dart';

class LoginPage extends GetView<LoginController> {
  LoginPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Loader(
      child: Scaffold(
        body: CustomBackground(
          child: SafeArea(
            child: SingleChildScrollView(
              child: Padding(
                padding: EdgeInsets.symmetric(
                  horizontal: AppSizes.horizontalSpacing,
                  vertical: AppSizes.verticalSpacing * 2,
                ),
                child: Form(
                  key: controller.formKey,
                  child: Column(
                    spacing: AppSizes.verticalSpacing,
                    children: [
                      const AppLogo(),
                      Text(LocalKeys.login.tr, style: textStyles.headlineSmall),
                      CustomTextField(
                        labelText: LocalKeys.userId.tr,
                        onChanged: (v) => controller.username.value = v,
                        hintText: LocalKeys.userIdHint.tr,

                        validator: (s) {
                          if (s == null || (s.isEmpty)) {
                            return LocalKeys.userIdIsRequired.tr;
                          } else {
                            return null;
                          }
                        },
                      ),
                      Obx(
                        () => CustomTextField(
                          labelText: LocalKeys.password.tr,
                          onChanged: (v) => controller.password.value = v,
                          obscureText: controller.obscureText.value,
                          suffixIcon: Padding(
                            padding: const EdgeInsets.all(10.0),
                            child: InkWell(
                              onTap: controller.togglePasswordVisibility,
                              child: Icon(
                                controller.obscureText.value
                                    ? Icons.visibility_outlined
                                    : Icons.visibility_off_outlined,
                                color: AppColors.iconGray,
                              ),
                            ),
                          ),
                          hintText: LocalKeys.passwordHint.tr,
                          validator: (s) {
                            if (s == null || (s.isEmpty)) {
                              return LocalKeys.passwordIsRequired.tr;
                            }
                            return null;
                          },
                        ),
                      ),
                      Align(
                        alignment: Alignment.centerRight,
                        child: GestureDetector(
                          onTap: () => Get.toNamed(Routes.forgotPassword),
                          child: Text(
                            LocalKeys.forgotPassword.tr + "?",
                            style: textStyles.bodyMedium?.copyWith(color: AppColors.primaryBlue, fontWeight: .bold),
                          ),
                        ),
                      ),

                      // GetBuilder<LoginController>(
                      //   builder: (controller) {

                      //     return Visibility(
                      //       visible: controller.shiftList.isNotEmpty,
                      //       replacement: const SizedBox(),
                      //       child: CustomDropDown(
                      //         items: controller.shiftList,
                      //         labelText: LocalKeys.shiftTime.tr,
                      //         hint: LocalKeys.shiftTimeHint.tr,
                      //         validator: (s) {
                      //           if (s == null) {
                      //             return LocalKeys.shiftIsRequired.tr;
                      //           }
                      //           return null;
                      //         },
                      //         onChanged: controller.onShiftChange,
                      //       ),
                      //     );
                      //   },
                      // ),
                      GetBuilder<LoginController>(
                        builder: (controller) {
                          return Visibility(
                            visible: controller.shiftList.isNotEmpty,
                            replacement: const SizedBox(),
                            child: CustomBottomSheetField(
                              isDismissible: false,
                              isScrollControlled: false,

                              labelText: LocalKeys.shiftTime.tr,
                              hintText: LocalKeys.shiftTimeHint.tr,
                              controller: controller.shiftController,
                              bottomSheetBuilder: (context) {
                                return GetBuilder<LoginController>(
                                  builder: (controller) {
                                    return Container(
                                      height: 0.45.sh,
                                      width: .infinity,
                                      decoration: BoxDecoration(borderRadius: AppSizes.defaultBorderRadius),
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.center,
                                        children: [
                                          Padding(
                                            padding: EdgeInsets.only(
                                              top: AppSizes.verticalSpacing,
                                              left: AppSizes.horizontalSpacing,
                                              right: AppSizes.horizontalSpacing,
                                            ),

                                            child: Text(LocalKeys.shiftTimeHint.tr, style: textStyles.bodyLarge),
                                          ),
                                          Expanded(
                                            child: ListView.builder(
                                              padding: EdgeInsets.symmetric(
                                                vertical: AppSizes.verticalSpacing,
                                                horizontal: AppSizes.horizontalSpacing,
                                              ),

                                              itemCount: controller.shiftList.length,
                                              itemBuilder: (context, index) {
                                                return InkWell(
                                                  onTap: () {
                                                    controller.onShiftChange(controller.shiftList[index]);
                                                  },
                                                  child: Container(
                                                    alignment: .center,
                                                    padding: EdgeInsets.symmetric(
                                                      vertical: AppSizes.verticalSpacing * 0.8,
                                                      horizontal: AppSizes.horizontalSpacing,
                                                    ),
                                                    decoration: BoxDecoration(
                                                      color:
                                                          controller.shiftController.text ==
                                                              controller.shiftList[index].label1
                                                          ? AppColors.selectedTileColor
                                                          : AppColors.pureWhite,
                                                      borderRadius: AppSizes.defaultBorderRadius,
                                                      border:
                                                          controller.shiftController.text ==
                                                              controller.shiftList[index].label1
                                                          ? Border.all(color: AppColors.borderPrimary.withAlpha(255))
                                                          : Border.all(width: 0, color: Colors.transparent),
                                                    ),

                                                    child: Center(
                                                      child: Text(
                                                        controller.shiftList[index].label1,
                                                        style: textStyles.titleSmall?.copyWith(
                                                          color: AppColors.textPrimary,
                                                        ),
                                                      ),
                                                    ),
                                                  ),
                                                );
                                              },
                                            ),
                                          ),
                                          Divider(height: 1, thickness: 1, color: AppColors.borderDotted),
                                          Padding(
                                            padding: EdgeInsets.symmetric(
                                              vertical: AppSizes.verticalSpacing,
                                              horizontal: AppSizes.horizontalSpacing,
                                            ),
                                            child: ColorButton(
                                              onPressed: controller.shiftController.text.isEmpty
                                                  ? null
                                                  : () {
                                                      Get.back();
                                                    },
                                              label: "Apply",
                                            ),
                                          ),
                                        ],
                                      ),
                                    );
                                  },
                                );
                              },
                            ),
                          );
                        },
                      ),
                      ColorButton(
                        onPressed: () {

                          controller.login();
                        },
                        label: LocalKeys.login.tr,
                        width: double.infinity,
                      ),

                      Obx(() => Text(controller.errorMessage.value, style: const TextStyle(color: Colors.red))),

                      AppVersionAndRefreshButtonWidget(),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

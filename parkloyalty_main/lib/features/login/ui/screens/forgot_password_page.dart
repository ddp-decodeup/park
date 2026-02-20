import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_back_button.dart';
import 'package:park_enfoecement/app/shared/widgets/loader.dart';
import 'package:park_enfoecement/features/login/ui/widgets/custom_background.dart';

import '../../../../app/shared/widgets/color_button.dart';
import '../../../../app/shared/widgets/custom_text_field.dart';
import '../../controller/login_controller.dart';

class ForgotPasswordPage extends GetView<LoginController> {
  ForgotPasswordPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Loader(
      child: Scaffold(
        body: CustomBackground(
          child: SafeArea(
            child: Padding(
              padding: EdgeInsets.symmetric(
                horizontal: AppSizes.horizontalSpacing,
              ),
              child: SingleChildScrollView(
                child: Column(
                  crossAxisAlignment: .start,
                  spacing: 12.h,
                  children: [
                    CustomBackButton(onBack: Get.back),
                    Center(
                      child: Text(
                        LocalKeys.forgotPassword.tr + "?",
                        style: textStyles.headlineSmall,
                      ),
                    ),
                    Center(
                      child: Text(
                        LocalKeys.forgotPasswordMessage.tr,
                        style: context.textTheme.titleSmall,
                        textAlign: .center,
                      ),
                    ),
                    CustomTextField(
                      onChanged: (v) => controller.username.value = v,
                      hintText: LocalKeys.userIdHint.tr,
                      labelText: LocalKeys.userId.tr,
                    ),
                    CustomTextField(
                      onChanged: (v) => controller.password.value = v,
                      hintText: LocalKeys.emailHint.tr,
                      labelText: LocalKeys.email.tr,
                    ),
                    SizedBox(height: 10.h),
                    ColorButton(
                      onPressed: () {},
                      label: LocalKeys.continueLabel.tr,
                      width: double.infinity,
                    ),
                    Row(
                      mainAxisAlignment: .center,
                      children: [
                        Text(
                          LocalKeys.rememberPassword.tr,
                          style: textStyles.labelLarge?.copyWith(
                            color: AppColors.textSubtitle,
                            fontWeight: .w500,
                          ),
                        ),
                        TextButton(
                          onPressed: () {},
                          child: Text(
                            LocalKeys.login.tr,
                            style: textStyles.labelLarge?.copyWith(
                              color: AppColors.primaryBlue,
                              fontWeight: .w500,
                            ),
                          ),
                        ),
                      ],
                    ),

                    SizedBox(height: 10),

                    Obx(
                      () => Text(
                        controller.errorMessage.value,
                        style: const TextStyle(color: Colors.red),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

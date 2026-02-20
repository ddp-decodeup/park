import 'package:flutter/material.dart' hide DateUtils;
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/app_divider.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/app/shared/widgets/form_section.dart';
import 'package:park_enfoecement/app/shared/widgets/loader.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';
import 'package:park_enfoecement/features/ticketing/controller/ticket_issue_controller.dart';
import 'package:park_enfoecement/features/ticketing/controller/ticket_preview_controller.dart';
import 'package:park_enfoecement/features/ticketing/ui/widgets/ticket_and_officer_details_widget.dart';

import '../../../../app/core/routes/app_routes.dart';
import '../widgets/image_section.dart';

class TicketIssueScreen extends GetView<TicketPreviewController> {
  const TicketIssueScreen({super.key});

  static final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    return Loader(
      child: AppScaffold(
        showDivider: true,
        onBack: () {
          Get.back();
        },
        body: Form(
          key: _formKey,
          child: SingleChildScrollView(
            child: GetBuilder<TicketIssueController>(
              builder: (controller) {
                return Padding(
                  padding: .only(top: 20.h, bottom: 30.h),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    // spacing: AppSizes.verticalSpacing,
                    children: [
                      Padding(
                        padding: AppSizes.defaultHorizontal,
                        child: Row(
                          mainAxisAlignment: .spaceBetween,
                          children: [
                            Text(LocalKeys.enforcement.tr, style: textStyles.titleMedium),
                            RenderSvgImage(assetName: AppIcons.cameraIcon),
                          ],
                        ),
                      ),
                      SizedBox(height: AppSizes.verticalSpacing),

                      Padding(
                        padding: AppSizes.defaultHorizontal,
                        child: TicketAndOfficerDetailsWidget(
                          citationNumber: controller.citationNumber,
                          ticketDate: controller.ticketDate,
                          agency: controller.agency,
                          officerId: controller.officerId,
                        ),
                      ),

                      ListView.separated(
                        physics: const NeverScrollableScrollPhysics(),
                        shrinkWrap: true,
                        padding: .zero,
                        itemBuilder: (context, index) {
                          final component = controller.template[index];
                          return FormSection(
                            section: component,
                            showVerticalPadding: true,
                            backgroundColor: (index + 1) % 2 == 0 ? AppColors.backgroundForm : Colors.transparent,
                            onChanged: (dataset, fieldList, dropDownList, name) {
                              controller.autoFillField(dataset, fieldList, component.component, dropDownList, name);
                            },
                          );
                        },
                        separatorBuilder: (context, index) {
                          return Divider(height: 1, thickness: 1, color: AppColors.borderDotted);
                        },
                        itemCount: controller.template.length,
                      ),
                      AppDivider(),
                      SizedBox(height: 15.h),
                      ImageSection(
                        onTap: () {
                          controller.openCamera();
                        },
                        fileList: controller.imageFileList,
                        onDelete: (file) {
                          controller.removeImageFile(file);
                        },
                      ),
                      Padding(
                        padding: EdgeInsetsGeometry.symmetric(
                          horizontal: AppSizes.horizontalSpacing,
                          vertical: AppSizes.verticalSpacing,
                        ),
                        child: ColorButton(
                          width: double.infinity,
                          onPressed: () {
                            if (_formKey.currentState!.validate())
                              Get.toNamed(
                                Routes.ticketIssuePreview,
                                arguments: {'model': controller.model, 'images': controller.imageFileList},
                              );
                          },
                          label: LocalKeys.preview.tr,
                        ),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ),
      ),
    );
  }
}

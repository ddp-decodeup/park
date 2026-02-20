import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/models/template_model.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/color_button.dart';
import 'package:park_enfoecement/app/shared/widgets/form_section.dart';
import 'package:park_enfoecement/app/shared/widgets/loader.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';
import 'package:park_enfoecement/features/municipal_citation/controller/municipal_citation_controller.dart';
import 'package:park_enfoecement/features/ticketing/ui/widgets/image_section.dart';
import 'package:park_enfoecement/features/ticketing/ui/widgets/ticket_and_officer_details_widget.dart';

class MunicipalCitationScreen extends GetView<MunicipalCitationController> {
  const MunicipalCitationScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final divider = Divider(height: 1, thickness: 1, color: AppColors.borderDotted);
    return Loader(
      child: AppScaffold(
        onBack: Get.back,
        showDivider: true,
        body: SingleChildScrollView(
          child: Padding(
            padding: .only(top: AppSizes.verticalSpacing * 0.7, bottom: 30.h),
            child: Column(
              crossAxisAlignment: .start,
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
                GetBuilder<MunicipalCitationController>(
                  id: controller.ticketAndOfficerDetailId,
                  builder: (c) {
                    return Padding(
                      padding: AppSizes.defaultHorizontal,
                      child: TicketAndOfficerDetailsWidget(
                        officerIdHeadingName: LocalKeys.badgeId.tr,
                        citationHeadingName: LocalKeys.issueNo.tr,
                        citationNumber: c.citationNumber,
                        ticketDate: c.ticketDate,
                        agency: c.agency,
                        officerId: c.officerId,
                        beat: "--",
                        officerName: c.officerName,
                      ),
                    );
                  },
                ),

                GetBuilder<MunicipalCitationController>(
                  id: controller.formId,

                  builder: (c) {
                    return Form(
                      key: c.formKey,
                      child: Column(
                        crossAxisAlignment: .start,
                        children: [
                          FormSection(
                            showVerticalPadding: true,
                            showHorizontalPadding: true,
                            buildFormHorizontal: true,
                            buttonText: LocalKeys.checkTicket.tr,
                            section: TemplateRes(fields: c.citationTypeField, component: LocalKeys.warning.tr),
                          ),
                          divider,
                          FormSection(
                            backgroundColor: AppColors.backgroundLightTeal,
                            showHorizontalPadding: true,
                            showVerticalPadding: true,
                            showButton: true,
                            disableButtonBgColor: AppColors.primaryBlue.withAlpha(190),
                            disableButtonFgColor: AppColors.pureWhite.withAlpha(190),
                            buttonText: LocalKeys.checkTicket.tr,
                            onButtonPressed: c.buttonDisabled ? null : c.checkIssuedCitation,
                            section: TemplateRes(
                              fields: c.personalInformationField,
                              component: LocalKeys.personalInformation.tr,
                            ),
                          ),
                          divider,
                          FormSection(
                            backgroundColor: AppColors.backgroundForm,
                            showHorizontalPadding: true,
                            showVerticalPadding: true,
                            section: TemplateRes(
                              fields: c.locationDetailsField,
                              component: LocalKeys.locationDetails.tr,
                            ),
                          ),
                          divider,
                          FormSection(
                            backgroundColor: AppColors.backgroundForm,
                            showHorizontalPadding: true,
                            showVerticalPadding: true,
                            section: TemplateRes(
                              fields: c.violationDetailsField,
                              component: LocalKeys.violationDetails.tr,
                            ),
                            onDropdownChange: c.onViolationDropdownChange,
                          ),
                          divider,
                          FormSection(
                            backgroundColor: AppColors.backgroundForm,
                            showHorizontalPadding: true,
                            showVerticalPadding: true,
                            section: TemplateRes(fields: c.commentsField, component: LocalKeys.comments.tr),
                          ),
                          divider,
                          Padding(
                            padding: EdgeInsetsGeometry.symmetric(vertical: AppSizes.verticalSpacing),
                            child: GetBuilder<MunicipalCitationController>(
                              id: controller.imageId,
                              builder: (c) {
                                return ImageSection(
                                  onTap: () {
                                    c.openCamera();
                                  },
                                  fileList: c.imageFileList,
                                  onDelete: (file) {
                                    c.removeImageFile(file);
                                  },
                                );
                              },
                            ),
                          ),
                        ],
                      ),
                    );
                  },
                ),
                Padding(
                  padding: EdgeInsets.symmetric(
                    horizontal: AppSizes.horizontalSpacing,
                    vertical: AppSizes.verticalSpacing,
                  ),
                  child: ColorButton(onPressed: controller.onPreview, label: LocalKeys.preview.tr),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

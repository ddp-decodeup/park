import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/shared/widgets/app_divider.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/features/municipal_citation/controller/municipal_citation_controller.dart';
import 'package:park_enfoecement/features/municipal_citation/ui/widgets/barcode_and_officer_details_widget.dart';
import 'package:park_enfoecement/features/municipal_citation/ui/widgets/build_preview_section.dart';

class MunicipalCitationPreview extends GetView<MunicipalCitationController> {
  const MunicipalCitationPreview({super.key});

  @override
  Widget build(BuildContext context) {
    return GetBuilder<MunicipalCitationController>(
      builder: (controller) {
        return AppScaffold(
          showDivider: true,
          onBack: Get.back,
          body: SingleChildScrollView(
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: AppSizes.verticalSpacing),
              child: GetBuilder<MunicipalCitationController>(
                builder: (c) {
                  return Column(
                    spacing: AppSizes.verticalSpacing,
                    children: [
                      BarcodeAndOfficerDetailsWidget(
                        officerName: c.model.officerDetails?.officerName ?? "",
                        officerId: c.model.officerDetails?.badgeId ?? "",
                        zone: c.model.officerDetails?.zone ?? "",
                        agency: c.model.officerDetails?.agency ?? "",
                      ),

                      BuildPreviewSection(
                        previewData: [
                          {LocalKeys.issueNo.tr: c.model.headerDetails?.citationNumber ?? ""},
                          {LocalKeys.ticketDate.tr: c.model.headerDetails?.timestamp ?? ""},
                        ],
                        sectionTitle: LocalKeys.ticketDetails.tr,
                        showEditIcon: false,
                      ),
                      AppDivider(),

                      BuildPreviewSection(
                        previewData: [
                          {LocalKeys.firstName.tr: c.model.motoristDetails?.motoristFirstName ?? ""},
                          {LocalKeys.middleName.tr: c.model.motoristDetails?.motoristMiddleName ?? ""},
                          {LocalKeys.lastName.tr: c.model.motoristDetails?.motoristLastName ?? ""},
                          {LocalKeys.dateOfBirth.tr: c.model.motoristDetails?.motoristDateOfBirth ?? ""},
                          {LocalKeys.dlNumber.tr: c.model.motoristDetails?.motoristDlNumber ?? ""},
                          {LocalKeys.block.tr: c.model.motoristDetails?.motoristAddressBlock ?? ""},
                          {LocalKeys.street.tr: c.model.motoristDetails?.motoristAddressStreet ?? ""},
                          {LocalKeys.city.tr: c.model.motoristDetails?.motoristAddressCity ?? ""},
                          {LocalKeys.state.tr: c.model.motoristDetails?.motoristAddressState ?? ""},
                          {LocalKeys.zip.tr: c.model.motoristDetails?.motoristAddressZip ?? ""},
                        ],
                        sectionTitle: LocalKeys.personalInformation.tr,
                      ),
                      AppDivider(),

                      BuildPreviewSection(
                        previewData: [
                          {LocalKeys.lot.tr: c.model.locationDetails?.lot ?? ""},
                          {LocalKeys.street.tr: c.model.locationDetails?.street ?? ""},
                          {LocalKeys.block.tr: c.model.locationDetails?.block ?? ""},
                          {LocalKeys.zone.tr: c.model.officerDetails?.zone ?? ""},
                        ],
                        sectionTitle: LocalKeys.locationDetails.tr,
                      ),
                      AppDivider(),

                      BuildPreviewSection(
                        previewData: [
                          {LocalKeys.violation.tr: c.model.violationDetails?.violation ?? "","flex":1},
                          {LocalKeys.description.tr: c.model.violationDetails?.description ?? "","flex":1},
                          {LocalKeys.fine.tr: c.model.violationDetails?.fine ?? ""},
                          {LocalKeys.lateFine.tr: c.model.violationDetails?.lateFine ?? ""},
                          {LocalKeys.due15Days.tr: c.model.violationDetails?.due15Days ?? ""},
                          {LocalKeys.due30Days.tr: c.model.violationDetails?.due30Days ?? ""},
                        ],
                        sectionTitle: LocalKeys.violationDetails.tr,
                      ),
                      AppDivider(),

                      BuildPreviewSection(
                        previewData: [
                          {LocalKeys.remark1.tr: c.model.commentDetails?.remark1 ?? ""},
                          {LocalKeys.note1.tr: c.model.commentDetails?.note1 ?? ""},
                        ],
                        sectionTitle: LocalKeys.comments.tr,
                      ),
                    ],
                  );
                },
              ),
            ),
          ),
        );
      },
    );
  }
}

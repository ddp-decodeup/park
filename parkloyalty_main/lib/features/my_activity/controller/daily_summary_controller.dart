import 'package:bluetooth_enable_fork/bluetooth_enable_fork.dart';
import 'package:flutter/cupertino.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/app/shared/utils/date_utils.dart';
import 'package:park_enfoecement/app/shared/utils/snackbar_utils.dart';
import 'package:park_enfoecement/features/my_activity/data/models/daily_summary_model.dart';
import 'package:park_enfoecement/features/my_activity/data/models/daily_summary_table_model.dart';
import 'package:park_enfoecement/features/my_activity/data/repositories/daily_summary_repository.dart';

class DailySummaryController extends BaseController {
  final DailySummaryRepository dailySummaryRepository;
  final AuthService authService;
  final LoaderController loaderController;

  DailySummaryController({
    required this.authService,
    required this.dailySummaryRepository,
    required this.loaderController,
  });

  List<List<DailySummaryTableModel>> officerDetails = [];
  List<List<DailySummaryTableModel>> shiftDetails = [];
  List<List<DailySummaryTableModel>> issuanceDetails = [];
  List<List<DailySummaryTableModel>> scanHitDetails = [];
  int totalScans = 0;
  int totalCount = 0;
  TextEditingController commentController = TextEditingController();

  @override
  void onInit() {
    getDailySummary();
    super.onInit();
  }

  Future<void> getDailySummary() async {
    loaderController.showLoader();
    final shift = await authService.user!.officerShift.toString();
    try {
      await run(() async {
        final response = await dailySummaryRepository.getDailySummary(
          shift: shift,
        );
        if (response.isOk) {
          final model = DailySummaryModel.fromJson(response.body);
          final officerDetailsModel =
              model.data?.officerDailySummary?.officerDetails;
          final shiftDetailsModel =
              model.data?.officerDailySummary?.shifts?.first.shiftDetails;
          final issuanceDetailsModel =
              model.data?.officerDailySummary?.shifts?.first.issuanceMetrics;
          final scanHitDetailsModel =
              model.data?.officerDailySummary?.shifts?.first.scanMetrics;
          final officerDetailsList = [
            [
              DailySummaryTableModel(
                heading: "Officer Name",
                value: officerDetailsModel?.username ?? "N/A",
              ),
              DailySummaryTableModel(
                heading: "Officer ID",
                value: (officerDetailsModel?.badgeId.toString()) ?? "N/A",
              ),
              DailySummaryTableModel(
                heading: "Beat",
                value: officerDetailsModel?.beat ?? "N/A",
              ),
              DailySummaryTableModel(
                heading: "Squad",
                value: officerDetailsModel?.squad ?? "N/A",
              ),
            ],
            [
              DailySummaryTableModel(
                heading: "Supervisor",
                value: officerDetailsModel?.supervisor ?? "N/A",
              ),
              DailySummaryTableModel(
                heading: "Device",
                value: (officerDetailsModel?.deviceName.toString()) ?? "N/A",
              ),
              DailySummaryTableModel(
                heading: "Printer",
                value: officerDetailsModel?.printer ?? "N/A",
              ),
              DailySummaryTableModel(
                heading: "Radio",
                value: officerDetailsModel?.radio ?? "N/A",
              ),
            ],
          ];
          final shiftDetailsList = [
            [
              DailySummaryTableModel(
                heading: "Login",
                value: _formatTime(shiftDetailsModel?.loginTimestamp),
              ),
              DailySummaryTableModel(
                heading: "First Cite",
                value: _formatTime(
                  issuanceDetailsModel?.firstIssuanceTimestamp,
                ),
              ),
              DailySummaryTableModel(
                heading: "Last Cite",
                value: _formatTime(issuanceDetailsModel?.lastIssuanceTimestamp),
              ),
              DailySummaryTableModel(
                heading: "First Scan",
                value: _formatTime(scanHitDetailsModel?.firstScanTimestamp),
              ),
            ],
            [
              DailySummaryTableModel(
                heading: "Lunch",
                value: _formatTime(shiftDetailsModel?.lunchTimestamp),
              ),
              DailySummaryTableModel(
                heading: "Break 1",
                value: _formatTime(shiftDetailsModel?.break1Timestamp),
              ),
              DailySummaryTableModel(
                heading: "Break 2",
                value: _formatTime(shiftDetailsModel?.break2Timestamp),
              ),
              DailySummaryTableModel(
                heading: "Departure",
                value: _formatTime(shiftDetailsModel?.lunchTimestamp),
              ),
            ],
            [
              DailySummaryTableModel(
                heading: "Drop Off",
                value: _formatTime(shiftDetailsModel?.lunchTimestamp),
              ),
              DailySummaryTableModel(
                heading: "Logout",
                value: _formatTime(shiftDetailsModel?.logoutTimestamp),
              ),
            ],
          ];
          final issuanceDetailsList = [
            [
              DailySummaryTableModel(
                heading: "Citation",
                value: (issuanceDetailsModel?.issuanceValid ?? 0).toString(),
              ),
              DailySummaryTableModel(
                heading: "Total Cancel",
                value: (issuanceDetailsModel?.totalCancel ?? 0).toString(),
              ),
              DailySummaryTableModel(
                heading: "Refused TVR",
                value: (issuanceDetailsModel?.tvrCount ?? 0).toString(),
              ),
              DailySummaryTableModel(
                heading: "Drive Off",
                value: (issuanceDetailsModel?.driveOffCount ?? 0).toString(),
              ),
            ],
            [
              DailySummaryTableModel(
                heading: "Reissue",
                value: (issuanceDetailsModel?.reissueCount ?? 0).toString(),
              ),
              DailySummaryTableModel(
                heading: "Rescind",
                value: (issuanceDetailsModel?.issuanceRescind ?? 0).toString(),
              ),
              DailySummaryTableModel(
                heading: "Cancel",
                value: (issuanceDetailsModel?.cancel ?? 0).toString(),
              ),
              DailySummaryTableModel(
                heading: "PBC Cancel",
                value: (issuanceDetailsModel?.pbcCancelCount ?? 0).toString(),
              ),
            ],
          ];
          final scanHitInfoList = [
            [
              DailySummaryTableModel(
                heading: "Payment",
                value: (scanHitDetailsModel?.scanPaymentHit ?? 0).toString(),
              ),
              DailySummaryTableModel(
                heading: "Permit",
                value: (scanHitDetailsModel?.scanPermitHit ?? 0).toString(),
              ),
              DailySummaryTableModel(
                heading: "Scofflaw",
                value: (scanHitDetailsModel?.scanScofflawHit ?? 0).toString(),
              ),
              DailySummaryTableModel(
                heading: "Mark",
                value: (scanHitDetailsModel?.scanTimingHit ?? 0).toString(),
              ),
            ],
          ];
          officerDetails = officerDetailsList;
          shiftDetails = shiftDetailsList;
          issuanceDetails = issuanceDetailsList;
          scanHitDetails = scanHitInfoList;
          totalScans = scanHitDetailsModel?.scanTotalHits ?? 0;
          totalCount = issuanceDetailsModel?.issuanceTotal ?? 0;
        } else {
          final message = response.body["detail"] ?? "Something went wrong";
          SnackBarUtils.showSnackBar(
            message: message,
            color: AppColors.errorRed,
          );
        }
      });
      update();
    } finally {
      loaderController.hideLoader();
    }
  }

  Future<void> openBluetooth() async {
    await BluetoothEnable.enableBluetooth.then((result) {
      if (result == "true") {
        // Bluetooth has been enabled
      } else if (result == "false") {
        // Bluetooth has not been enabled
      }
    });
  }

  String _formatTime(DateTime? date) {
    if (date == null) {
      return "N/A";
    }
    return DateUtil.hhmm(date);
  }
}

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:latlong2/latlong.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/core/services/location_service.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import 'package:park_enfoecement/app/shared/utils/snackbar_utils.dart';
import 'package:park_enfoecement/features/home/data/models/welcome_model.dart' show User;
import 'package:park_enfoecement/features/my_activity/data/models/activity_log_model.dart';
import 'package:park_enfoecement/features/my_activity/data/models/chart_data_model.dart';
import 'package:park_enfoecement/features/my_activity/data/models/location_model.dart';
import 'package:park_enfoecement/features/my_activity/data/models/violation_data_model.dart';
import 'package:park_enfoecement/features/my_activity/data/repositories/graph_view_repositories.dart';

class GraphViewController extends BaseController {
  final AuthService authService;
  final GraphViewRepository graphViewRepository;
  final LoaderController loaderController;

  GraphViewController({required this.authService, required this.graphViewRepository, required this.loaderController});

  User? user;
  List<ChartDataModel> barChartData = <ChartDataModel>[];
  List<LineChartModel> lineChartData = <LineChartModel>[];
  Rx<ViolationDataModel> violationData = ViolationDataModel().obs;
  List<LatLng> locationData = <LatLng>[];
  List<ActivityUpdate> activityLogData = <ActivityUpdate>[];

  final List<Map<String, dynamic>> lineChartLabels = [
    {'color': AppColors.warningYellowDark, 'text': 'Scans'},
    {'color': AppColors.primaryBlue, 'text': 'Timings'},
    {'color': AppColors.errorRed, 'text': 'Tickets'},
  ];

  @override
  void onInit() {
    user = authService.user;
    getGraphViewAllData();
    super.onInit();
  }

  double _parseNum(dynamic value) {
    if (value is num) return value.toDouble();
    final parsed = num.tryParse(value?.toString() ?? '');
    return parsed?.toDouble() ?? 0.0;
  }

  List<ChartDataModel> _defaultBarChartData() => <ChartDataModel>[
    ChartDataModel('Scan', 0),
    ChartDataModel('Tickets', 0),
    ChartDataModel('Timing', 0),
    ChartDataModel('permits', 0),
    ChartDataModel('scofflaws', 0),
    ChartDataModel('Drive off', 0),
  ];

  void _populateDefaultLineCharts() {
    lineChartData.assignAll(
      lineChartLabels.map((item) {
        final color = item['color'] as Color;
        final label = (item['text'] as String).capitalize!;
        return LineChartModel(color: color, label: label, data: <ChartDataModel>[]);
      }).toList(),
    );
  }

  Future<void> getGraphViewAllData() async {
    loaderController.showLoader();
    try {
      if (user == null) {
        _populateDefaultLineCharts();
        barChartData.assignAll(_defaultBarChartData());
        return;
      }

      await run(
        () async {
          final responses = await Future.wait([
            graphViewRepository.getBarChartData(shift: user!.officerShift),
            graphViewRepository.getLineChartData(shift: user!.officerShift, timeline: 'daily'),
            graphViewRepository.getViolationCountData(shift: user!.officerShift),
            graphViewRepository.getLocationsData(shift: user!.officerShift),
            graphViewRepository.getActivityLogData(shift: user!.officerShift),
          ]);

          final barChartResponse = responses[0];
          final lineChartResponse = responses[1];
          final violationCountResponse = responses[2];
          final locationResponse = responses[3];
          final activityLogResponse = responses[4];

          ///--------------------------------------------------------
          /// Bar Chart Response
          ///--------------------------------------------------------
          if (barChartResponse.statusCode == 200) {
            final dataList = barChartResponse.body['data'];
            final Map<String, dynamic>? dataMap = (dataList is List && dataList.isNotEmpty)
                ? (dataList.first as Map<String, dynamic>?)
                : null;

            if (dataMap != null) {
              barChartData.assignAll(
                dataMap.entries.map((e) => ChartDataModel(e.key.replaceAll('_', ' '), _parseNum(e.value))).toList(),
              );
            } else {
              barChartData.assignAll(_defaultBarChartData());
            }
          } else {
            final message = barChartResponse.body["detail"];
            logging(message);
            SnackBarUtils.showSnackBar(message: message, color: AppColors.errorRed);
          }

          ///--------------------------------------------------------

          ///--------------------------------------------------------
          /// Line Chart Response
          ///--------------------------------------------------------
          if (lineChartResponse.statusCode == 200) {
            final responseData = lineChartResponse.body['data'];
            final res = (responseData is List && responseData.isNotEmpty)
                ? (responseData[0]['response'] as List<dynamic>?) ?? <dynamic>[]
                : <dynamic>[];

            if (res.isNotEmpty) {
              lineChartData.clear();
              for (final item in res) {
                final datasetRaw = item['dataset_name']?.toString() ?? '';
                final datasetName = datasetRaw.toLowerCase().trim() == 'citations' ? 'tickets' : datasetRaw;
                final aggregate = (item['aggregate'] is List) ? item['aggregate'] as List<dynamic> : <dynamic>[];

                final labelMatch = lineChartLabels.firstWhere(
                  (element) => element['text'].toString().toLowerCase().trim() == datasetName.toLowerCase().trim(),
                  orElse: () => lineChartLabels.first,
                );

                final color = labelMatch['color'] as Color;
                final data = <ChartDataModel>[];

                for (var i = 0; i < aggregate.length; i++) {
                  data.add(ChartDataModel(i.toString(), _parseNum(aggregate[i])));
                }

                lineChartData.add(
                  LineChartModel(color: color, label: datasetName.toString().trim().capitalize!, data: data),
                );
              }
            } else {
              _populateDefaultLineCharts();
            }
          } else {
            final message = lineChartResponse.body["detail"];
            logging(message);
            SnackBarUtils.showSnackBar(message: message, color: AppColors.errorRed);
          }

          ///--------------------------------------------------------

          ///--------------------------------------------------------
          /// Violation Count Response
          ///--------------------------------------------------------
          if (violationCountResponse.statusCode == 200) {
            violationData(ViolationDataModel.fromJson(violationCountResponse.body));
          } else {
            final message = violationCountResponse.body["detail"];
            logging(message);
            violationData(ViolationDataModel(data: []));
            SnackBarUtils.showSnackBar(message: message, color: AppColors.errorRed);
          }

          ///--------------------------------------------------------

          ///--------------------------------------------------------
          /// Location Response
          ///--------------------------------------------------------
          if (locationResponse.statusCode == 200) {
            locationData.clear();
            final locations = (LocationModel.fromJson(locationResponse.body));
            final currentLocation = await LocationService().getCurrentLocation();
            if (currentLocation != null) {
              final currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude);
              locationData.add(currentLatLng);
            }
            for (final location in locations.data!) {
              final latLng = LatLng(location.latitude!, location.longitude!);
              locationData.add(latLng);
            }
          } else {
            final message = locationResponse.body["detail"];
            final currentLocation = await LocationService().getCurrentLocation();
            if (currentLocation != null) {
              final currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude);
              locationData.add(currentLatLng);
            }
            SnackBarUtils.showSnackBar(message: message, color: AppColors.errorRed);
          }

          ///--------------------------------------------------------

          ///--------------------------------------------------------
          /// Activity Log Response
          ///--------------------------------------------------------
          if (activityLogResponse.statusCode == 200) {
            activityLogData.assignAll(ActivityLogModel.fromJson(activityLogResponse.body).data!.first.activityUpdates!);
          } else {
            final message = activityLogResponse.body["detail"]??"Activity data not found";
            activityLogData = ([]);
            SnackBarUtils.showSnackBar(message: message, color: AppColors.errorRed);
          }
        },
        whenOffline: () async {
          _populateDefaultLineCharts();
          barChartData.assignAll(_defaultBarChartData());
          final currentLocation = await LocationService().getCurrentLocation();
          if (currentLocation != null) {
            final currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude);
            locationData.add(currentLatLng);
          }
        },
      );
    } catch (e) {
      _populateDefaultLineCharts();
      final currentLocation = await LocationService().getCurrentLocation();
      if (currentLocation != null) {
        final currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude);

        locationData.add(currentLatLng);
      }
      barChartData.assignAll(_defaultBarChartData());
    } finally {
      update();
      update();
      loaderController.hideLoader();
    }
  }
}

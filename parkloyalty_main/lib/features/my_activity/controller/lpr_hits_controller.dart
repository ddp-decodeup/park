import 'package:latlong2/latlong.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/core/services/location_service.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/app/shared/utils/snackbar_utils.dart';
import 'package:park_enfoecement/features/my_activity/data/models/location_model.dart';
import 'package:park_enfoecement/features/my_activity/data/repositories/graph_view_repositories.dart';

class LprHitsController extends BaseController {
  final GraphViewRepository repository;
  final AuthService authService;
  final LoaderController loaderController;

  LprHitsController({
    required this.repository,
    required this.authService,
    required this.loaderController,
  });

  final List<LatLng> locations = [];

  @override
  void onInit() {
    getLocationData();
    super.onInit();
  }

  Future<void> getLocationData() async {
    loaderController.showLoader();
    try {
      final currentLocation = await LocationService().getCurrentLocation();
      if (currentLocation != null) {
        locations.add(
          LatLng(currentLocation.latitude, currentLocation.longitude),
        );
      }

      await run(() async {
        final shift = authService.user!.officerShift.toString();
        final response = await repository.getLocationsData(shift: shift);
        if (response.isOk) {
          final res = LocationModel.fromJson(response.body);
          res.data?.forEach((location) {
            locations.add(LatLng(location.latitude!, location.longitude!));
          });
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
}

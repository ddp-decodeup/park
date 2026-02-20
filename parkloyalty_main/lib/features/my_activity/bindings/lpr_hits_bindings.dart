import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/my_activity/controller/lpr_hits_controller.dart';
import 'package:park_enfoecement/features/my_activity/data/repositories/graph_view_repositories.dart';

class LprHitsBindings extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<GraphViewRepository>(
      () => GraphViewRepository(Get.find<ApiClient>()),
    );
    Get.lazyPut<LprHitsController>(
      () => LprHitsController(
        repository: Get.find<GraphViewRepository>(),
        authService: Get.find<AuthService>(),
        loaderController: Get.find<LoaderController>(),
      ),
    );
  }
}

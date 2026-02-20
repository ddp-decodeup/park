import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/services/auth_service.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/my_activity/controller/graph_view_controller.dart';
import 'package:park_enfoecement/features/my_activity/data/repositories/graph_view_repositories.dart';

class GraphViewBindings extends Bindings {
  @override
  void dependencies() {
    Get.lazyPut<GraphViewRepository>(
      () => GraphViewRepository(Get.find<ApiClient>()),
    );
    Get.lazyPut<GraphViewController>(
      () => GraphViewController(
        authService: Get.find<AuthService>(),
        graphViewRepository: Get.find<GraphViewRepository>(),
        loaderController: Get.find<LoaderController>(),
      ),
    );
  }
}

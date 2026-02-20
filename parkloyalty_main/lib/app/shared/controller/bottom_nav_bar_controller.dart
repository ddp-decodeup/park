import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';

class BottomNavBarController extends GetxController {
  RxInt currentIndex = 0.obs;
  final List<Map<String, String>> items = [
    {
      "icon": AppIcons.homeIcon,
      "label": LocalKeys.home.tr,
      "page": Routes.home,
    },
    {
      "icon": AppIcons.ticketIcon,
      "label": LocalKeys.ticketing.tr,
      "page": Routes.ticketing,
    },
    {
      "icon": AppIcons.activityIcon,
      "label": LocalKeys.myActivity.tr,
      "page": Routes.myActivity,
    },
    {
      "icon": AppIcons.settingsIcon,
      "label": LocalKeys.settings.tr,
      "page": Routes.settings,
    },
    {
      "icon": AppIcons.reportIcon,
      "label": LocalKeys.reports.tr,
      "page": Routes.reports,
    },
  ];

  void changeIndex(int index) {
    currentIndex.value = index;
    Get.offNamedUntil(items[index]['page'] as String, (route) {
      return false;
    });
  }
}

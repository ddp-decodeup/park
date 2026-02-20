import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/shared/controller/bottom_nav_bar_controller.dart';

class DrawerController extends GetxController {
  final BottomNavBarController bottomNavBarController =
      Get.find<BottomNavBarController>();

  void closeDrawer(BuildContext context) {
    Scaffold.of(context).closeEndDrawer();
  }

  void onTapDrawerRow(int index) {
    bottomNavBarController.changeIndex(index);
  }
}

/// -----------------
/// Drawer Model
/// -----------------
class DrawerModel {
  final String label;
  final String? icon;

  DrawerModel({required this.label, this.icon});
}

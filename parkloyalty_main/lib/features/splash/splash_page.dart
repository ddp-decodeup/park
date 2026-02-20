import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/shared/widgets/app_logo.dart';
import 'package:park_enfoecement/features/splash/splash_controller.dart';

class SplashPage extends GetView<SplashController> {
  const SplashPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(body: Center(child: const AppLogo()));
  }
}

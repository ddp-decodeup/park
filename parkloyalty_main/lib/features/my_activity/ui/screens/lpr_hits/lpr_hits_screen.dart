import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/shared/widgets/app_scaffold.dart';
import 'package:park_enfoecement/app/shared/widgets/loader.dart';
import 'package:park_enfoecement/features/my_activity/controller/lpr_hits_controller.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/graph_view_widgets/route_details_widget.dart';
import 'package:shimmer/shimmer.dart';

class LprHitScreen extends StatelessWidget {
  const LprHitScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Loader(
      child: AppScaffold(
        onBack: () {
          Get.back();
        },
        body: GetBuilder<LprHitsController>(
          builder: (controller) {
            return controller.locations.isNotEmpty
                ? RouteDetailsWidget(
                    locations: controller.locations,
                    titleVisible: false,
                    applyBoarder: false,
                    height: Get.height - 0.15.sh,
                    bottom: 20.h,
                    right: 20.w,
                  )
                : Shimmer.fromColors(
                    child: Container(
                      height: double.infinity,
                      width: double.infinity,
                      color: Colors.white,
                    ),
                    baseColor: Colors.grey.shade50,
                    highlightColor: Colors.grey.shade300,
                  );
          },
        ),
      ),
    );
  }
}

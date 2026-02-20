import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:flutter_map_animations/flutter_map_animations.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get_utils/src/extensions/internacionalization.dart';
import 'package:latlong2/latlong.dart';
import 'package:park_enfoecement/app/core/constants/app_sizes.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';

class RouteDetailsWidget extends StatefulWidget {
  final List<LatLng> locations;
  final double? height;
  final Function(TapPosition, LatLng)? onTap;
  final bool titleVisible;
  final bool applyBoarder;
  final double? bottom;
  final double? right;

  const RouteDetailsWidget({
    super.key,
    required this.locations,
    this.height,
    this.onTap,
    this.titleVisible = true,
    this.applyBoarder = true,
    this.bottom,
    this.right,
  });

  @override
  State<RouteDetailsWidget> createState() => _RouteDetailsWidgetState();
}

class _RouteDetailsWidgetState extends State<RouteDetailsWidget>
    with TickerProviderStateMixin {
  late AnimatedMapController _animatedMapController;
  static const _useTransformerId = 'useTransformerId';
  final bool _useTransformer = true;

  @override
  void initState() {
    _animatedMapController = AnimatedMapController(vsync: this);
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: .start,
      spacing: widget.titleVisible ? 10.h : 0,
      children: [
        Visibility(
          visible: widget.titleVisible,
          child: Text(LocalKeys.routeDetails.tr, style: textStyles.titleMedium),
        ),
        Stack(
          children: [
            Container(
              alignment: .center,
              height: widget.height ?? 0.25.sh,
              width: double.infinity,
              decoration: BoxDecoration(
                borderRadius: widget.applyBoarder
                    ? AppSizes.defaultBorderRadius
                    : BorderRadius.zero,
              ),
              child: ClipRRect(
                borderRadius: widget.applyBoarder
                    ? AppSizes.defaultBorderRadius
                    : BorderRadius.zero,
                child: FlutterMap(
                  mapController: _animatedMapController.mapController,
                  options: MapOptions(
                    cameraConstraint: const CameraConstraint.containLatitude(),
                    onTap: widget.onTap,
                    initialCenter: widget.locations.isNotEmpty
                        ? widget.locations.first
                        : LatLng(50.5, 30.51),
                    initialZoom: 18,
                    maxZoom: 100.0,
                    minZoom: 2.0,
                  ),
                  children: [
                    TileLayer(
                      urlTemplate:
                          'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                      userAgentPackageName:
                          "com.du.park.enforcement.park_enfoecement",
                    ),
                    MarkerLayer(
                      markers: List.generate(widget.locations.length, (index) {
                        final location = widget.locations[index];
                        return Marker(
                          point: location,
                          child: Icon(
                            Icons.location_on,
                            size: 30,
                            color: AppColors.primaryBlue,
                          ),
                        );
                      }),
                    ),
                  ],
                ),
              ),
            ),
            Positioned(
              bottom: widget.bottom ?? 10.h,
              right: widget.right ?? 10.w,
              child: SeparatedColumn(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.end,
                separator: const SizedBox(height: 8),
                children: [
                  SizedBox(
                    width: 40,
                    height: 40,
                    child: FloatingActionButton(
                      shape: CircleBorder(),

                      onPressed: () => _animatedMapController.animatedZoomIn(
                        customId: _useTransformer ? _useTransformerId : null,
                      ),
                      child: const Icon(Icons.zoom_in, size: 25),
                    ),
                  ),
                  SizedBox(
                    width: 40,
                    height: 40,
                    child: FloatingActionButton(
                      shape: CircleBorder(),

                      onPressed: () => _animatedMapController.animatedZoomOut(
                        customId: _useTransformer ? _useTransformerId : null,
                      ),
                      child: const Icon(Icons.zoom_out, size: 25),
                    ),
                  ),
                  SizedBox(
                    width: 40,
                    height: 40,
                    child: FloatingActionButton(
                      shape: CircleBorder(),
                      onPressed: () => _animatedMapController.animateTo(
                        dest: widget.locations.first,
                        customId: _useTransformer ? _useTransformerId : null,
                        duration: const Duration(milliseconds: 500),
                      ),
                      child: const Icon(Icons.my_location, size: 25),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class SeparatedColumn extends StatelessWidget {
  const SeparatedColumn({
    super.key,
    required this.separator,
    this.children = const [],
    this.mainAxisSize = MainAxisSize.max,
    this.crossAxisAlignment = CrossAxisAlignment.start,
  });

  final Widget separator;
  final List<Widget> children;
  final MainAxisSize mainAxisSize;
  final CrossAxisAlignment crossAxisAlignment;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: mainAxisSize,
      crossAxisAlignment: crossAxisAlignment,
      children: [..._buildChildren()],
    );
  }

  Iterable<Widget> _buildChildren() sync* {
    for (var i = 0; i < children.length; i++) {
      yield children[i];
      if (i < children.length - 1) yield separator;
    }
  }
}

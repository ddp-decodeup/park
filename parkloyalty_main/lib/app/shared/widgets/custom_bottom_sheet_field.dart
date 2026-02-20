import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_text_field.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

class CustomBottomSheetField extends StatelessWidget {
  final String hintText;
  final String? labelText;
  final TextEditingController controller;
  final WidgetBuilder bottomSheetBuilder;
  final bool isScrollControlled;

  final bool isDismissible;

  final ShapeBorder? bottomSheetShape;

  final Color? bottomSheetBackgroundColor;

  final Color? bottomSheetBarrierColor;

  const CustomBottomSheetField({
    super.key,
    required this.hintText,
    this.labelText,
    required this.controller,
    required this.bottomSheetBuilder,
    this.isScrollControlled = true,
    this.isDismissible = true,
    this.bottomSheetShape,
    this.bottomSheetBackgroundColor,
    this.bottomSheetBarrierColor,
  });

  @override
  Widget build(BuildContext context) {
    return CustomTextField(
      hintText: hintText,
      controller: controller,
      labelText: labelText,
      readOnly: true,
      suffixIcon: Padding(
        padding: const EdgeInsets.all(20.0),
        child: RenderSvgImage(assetName: AppIcons.arrowDownIcon),
      ),
      onTap: () {
        _openBottomSheet(context, bottomSheetBuilder);
      },
    );
  }

  void _openBottomSheet(
    BuildContext context,
    WidgetBuilder builder, {
    bool isScrollControlled = true,
    bool isDismissible = true,
    ShapeBorder? shape,
    Color? color,
    Color? barrierColor,
  }) {
    showModalBottomSheet(
      isDismissible: isDismissible,
      isScrollControlled: isScrollControlled,
      shape: shape,
      backgroundColor: color,
      useSafeArea: true,
      barrierColor: barrierColor,
      context: context,
      builder: builder,
    );
  }
}

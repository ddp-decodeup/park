// import 'dart:math' as Math;
//
// import 'package:flutter/material.dart';
// import 'package:flutter_screenutil/flutter_screenutil.dart';
// import 'package:get/get.dart';
// import 'package:park_enfoecement/app/core/constants/app_icons.dart';
// import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
// import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';
//
// import '../../core/theme/app_colors.dart';
//
// class Dropdown<T> extends StatelessWidget {
//   final String? labelText;
//
//   final bool isRequired;
//
//   final List<DropdownMenuEntry<T>> dropdownMenuEntries;
//
//   final bool isEnabled;
//
//   final TextEditingController? searchController;
//
//   final String? hintText;
//
//   final AutovalidateMode autoValidateMode;
//
//   final String? Function(T?)? validator;
//
//   final T? initialSelection;
//
//   final void Function(T?)? onSelected;
//
//   const Dropdown({
//     super.key,
//     this.labelText,
//     this.isRequired = false,
//     required this.dropdownMenuEntries,
//     this.isEnabled = true,
//     this.searchController,
//     this.hintText,
//     this.autoValidateMode = AutovalidateMode.onUserInteraction,
//     this.validator,
//     this.initialSelection,
//     this.onSelected,
//   });
//
//   @override
//   Widget build(BuildContext context) {
//     final style = textStyles.bodyMedium;
//     return Column(
//       mainAxisAlignment: .start,
//       crossAxisAlignment: .start,
//       mainAxisSize: .min,
//       spacing: 5.h,
//       children: [
//         if (labelText != null && (labelText?.isNotEmpty ?? false))
//           RichText(
//             text: TextSpan(
//               text: labelText,
//               style: style,
//               children: isRequired
//                   ? [
//                       TextSpan(
//                         text: " *",
//                         style: style?.copyWith(color: AppColors.errorRed, fontWeight: FontWeight.bold),
//                       ),
//                     ]
//                   : [],
//             ),
//           ),
//
//         LayoutBuilder(
//           builder: (context, constraints) {
//             return DropdownMenuFormField<T>(
//               initialSelection: initialSelection,
//               dropdownMenuEntries: dropdownMenuEntries,
//               enabled: isEnabled,
//               controller: searchController,
//               enableFilter: searchController != null,
//               enableSearch: searchController != null,
//               width: constraints.maxWidth,
//               requestFocusOnTap: searchController != null,
//               selectedTrailingIcon: Padding(
//                 padding: EdgeInsets.only(right: 10.w),
//                 child: Row(
//                   mainAxisSize: .min,
//                   spacing: 10.w,
//                   children: [
//                     if (searchController != null)
//                       InkWell(
//                         customBorder: CircleBorder(),
//                         onTap: () => searchController?.clear(),
//
//                         child: Container(
//                           padding: EdgeInsets.all(5.h),
//                           height: 40.h,
//                           child: RenderSvgImage(
//                             assetName: AppIcons.clearFilledIcon,
//                             height: 12.h,
//                             width: 12.h,
//                             fit: .contain,
//                           ),
//                         ),
//                       ),
//
//                     Transform.rotate(
//                       angle: Math.pi,
//                       child: RenderSvgImage(assetName: AppIcons.arrowDownIcon),
//                     ),
//                   ],
//                 ),
//               ),
//               trailingIcon: RenderSvgImage(assetName: AppIcons.arrowDownIcon),
//
//               hintText: hintText,
//               textStyle: style,
//               closeBehavior: DropdownMenuCloseBehavior.self,
//               autovalidateMode: autoValidateMode,
//               validator: isRequired && validator == null
//                   ? (s) => s == null ? "${labelText ?? "This"} field is required" : null
//                   : validator,
//               onSelected: onSelected,
//               menuHeight: 200.h,
//               inputDecorationTheme: InputDecorationThemeData(
//                 hintStyle: context.textTheme.labelLarge,
//                 fillColor: AppColors.pureWhite,
//                 filled: true,
//                 suffixIconConstraints: BoxConstraints(maxHeight: 40.h),
//                 visualDensity: VisualDensity.adaptivePlatformDensity,
//
//               ),
//             );
//           },
//         ),
//       ],
//     );
//   }
// }

import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

class Dropdown<T> extends StatefulWidget {
  final String? labelText;
  final bool isRequired;
  final List<DropdownMenuEntry<T>> dropdownMenuEntries;
  final bool isEnabled;
  final TextEditingController? searchController;
  final String? hintText;
  final AutovalidateMode autoValidateMode;
  final String? Function(T?)? validator;
  final void Function()? onClear;
  final T? initialSelection;
  final void Function(T?)? onSelected;
  final double? menuHeight;

  const Dropdown({
    super.key,
    this.labelText,
    this.isRequired = false,
    required this.dropdownMenuEntries,
    this.isEnabled = true,
    this.searchController,
    this.hintText,
    this.autoValidateMode = AutovalidateMode.onUserInteraction,
    this.validator,
    this.initialSelection,
    this.onSelected,
    this.menuHeight,
    this.onClear,
  });

  @override
  State<Dropdown<T>> createState() => _DropdownState<T>();
}

class _DropdownState<T> extends State<Dropdown<T>> {
  late List<DropdownMenuEntry<T>> _entries;
  late T? initialSelection;
  final GlobalKey<FormFieldState<T>> _fieldKey = GlobalKey<FormFieldState<T>>();

  @override
  void initState() {
    super.initState();
    _entries = widget.dropdownMenuEntries;
    initialSelection = widget.initialSelection;
  }

  @override
  void didUpdateWidget(covariant Dropdown<T> oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (oldWidget.dropdownMenuEntries != widget.dropdownMenuEntries) {
      _entries = widget.dropdownMenuEntries;
    }
  }

  void _resetSearch() {
    widget.searchController?.clear();
    initialSelection = null;
    widget.onClear?.call();
    _fieldKey.currentState?.didChange(null);
    setState(() {
      _entries = widget.dropdownMenuEntries;
    });
  }

  @override
  Widget build(BuildContext context) {
    final style = AppTextStyles.fontSize14px;

    return TapRegion(
      onTapOutside: (event) {
        FocusManager.instance.primaryFocus?.unfocus();
      },
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          if (widget.labelText != null && widget.labelText!.trim().isNotEmpty)
            Padding(
              padding: EdgeInsets.only(bottom: 5.h),
              child: RichText(
                text: TextSpan(
                  text: widget.labelText,
                  style: style,
                  children: widget.isRequired
                      ? [
                          TextSpan(
                            text: " *",
                            style: style.copyWith(color: AppColors.errorRed, fontWeight: FontWeight.bold),
                          ),
                        ]
                      : [],
                ),
              ),
            ),

          LayoutBuilder(
            builder: (context, constraints) {
              return DropdownMenuFormField<T>(
                key: _fieldKey,

                width: constraints.maxWidth,
                enabled: widget.isEnabled,
                initialSelection: initialSelection,
                dropdownMenuEntries: _entries,
                controller: widget.searchController,
                enableFilter: widget.searchController != null,
                enableSearch: widget.searchController != null,
                requestFocusOnTap: widget.searchController != null,
                hintText: widget.hintText,
                textStyle: style,
                closeBehavior: DropdownMenuCloseBehavior.self,
                autovalidateMode: widget.autoValidateMode,
                validator: widget.isRequired && widget.validator == null
                    ? (value) {
                        return value == null ? "${widget.labelText ?? "This"} field is required" : null;
                      }
                    : widget.validator,
                onSelected: widget.onSelected,
                menuHeight: widget.menuHeight ?? 200.h,
                trailingIcon: RenderSvgImage(assetName: AppIcons.arrowDownIcon),

                selectedTrailingIcon: Padding(
                  padding: EdgeInsets.only(right: 10.w),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      if (widget.searchController != null)
                        InkWell(
                          customBorder: const CircleBorder(),
                          onTap: _resetSearch,
                          child: Padding(
                            padding: const EdgeInsets.all(5.0),
                            child: RenderSvgImage(
                              assetName: AppIcons.clearFilledIcon,
                              height: 30.h,
                              fit: BoxFit.contain,
                            ),
                          ),
                        ),

                      Transform.rotate(
                        angle: math.pi,
                        child: RenderSvgImage(assetName: AppIcons.arrowDownIcon),
                      ),
                    ],
                  ),
                ),

                inputDecorationTheme: InputDecorationTheme(
                  hintStyle: context.textTheme.labelLarge,
                  fillColor: AppColors.pureWhite,
                  filled: true,

                  suffixIconConstraints: BoxConstraints(maxHeight: 46.h),
                  visualDensity: VisualDensity.adaptivePlatformDensity,
                ),
              );
            },
          ),
        ],
      ),
    );
  }
}

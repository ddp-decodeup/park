import 'package:flutter/cupertino.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:park_enfoecement/app/core/constants/app_icons.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/core/theme/app_text_styles.dart';
import 'package:park_enfoecement/app/shared/widgets/render_svg_image.dart';

final List<Map<String, dynamic>> inventory = [
  {"key": "Vehicle Keys", "value": "Not Scanned"},
  {"key": "Radio", "value": "1562"},
  {"key": "Printer", "value": "Not Scanned"},
];

class InventoryWidget extends StatelessWidget {
  const InventoryWidget({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 16.w, vertical: 16.h),
      decoration: BoxDecoration(
        color: AppColors.backgroundForm,
        border: Border.symmetric(
          horizontal: BorderSide(color: AppColors.backgroundForm, width: 1.5),
        ),
      ),
      child: Column(
        spacing: 16.h,
        crossAxisAlignment: .start,
        children: [
          Row(
            mainAxisAlignment: .spaceBetween,
            children: [
              Text("QR Code inventory", style: textStyles.titleMedium),
              RenderSvgImage(assetName: AppIcons.scanIcon),
            ],
          ),
          SingleChildScrollView(
            scrollDirection: .horizontal,
            child: Row(
              spacing: 12.w,
              children: List.generate(inventory.length, (index) {
                final item = inventory[index];
                return _buildInventoryBox(item: item);
              }),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInventoryBox({required Map<String, dynamic> item}) {
    return Container(
      width: 0.4.sw,
      padding: EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppColors.pureWhite,
        borderRadius: BorderRadius.circular(8.r),
        border: Border.all(color: AppColors.borderCard, width: 1.5),
      ),
      child: Column(
        crossAxisAlignment: .start,
        children: [
          Text(item['key'], style: textStyles.bodyMedium),
          SizedBox(height: 5.h),
          Text(item['value'], style: textStyles.labelLarge),
        ],
      ),
    );
  }
}

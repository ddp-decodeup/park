import 'package:flutter/cupertino.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/shared/widgets/custom_text_field.dart';
import 'package:park_enfoecement/features/my_activity/ui/widgets/daily_summary_widgets/section_header.dart';

class CommentSection extends StatelessWidget {
  final TextEditingController controller;

  const CommentSection({super.key, required this.controller});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        SectionHeader(title: LocalKeys.comments.tr),
        SizedBox(height: 12.h),
        CustomTextField(
          hintText: LocalKeys.commentsHint.tr,
          controller: controller,
          minLines: 5,
          maxLines: 5,
        ),
      ],
    );
  }
}

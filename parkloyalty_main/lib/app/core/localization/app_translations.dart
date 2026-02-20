import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:get/get.dart';

class AppTranslations extends Translations {
  final Map<String, Map<String, String>> keysMap;

  AppTranslations(this.keysMap);

  static Future<Map<String, Map<String, String>>> load() async {
    final en = await rootBundle.loadString(
      'lib/app/core/localization/translations/en_US.json',
    );
    // final hi = await rootBundle.loadString('assets/lang/hi_IN.json');

    return {
      'en_US': Map<String, String>.from(json.decode(en)),
      // 'hi_IN': Map<String, String>.from(json.decode(hi)),
    };
  }

  @override
  Map<String, Map<String, String>> get keys => keysMap;
}

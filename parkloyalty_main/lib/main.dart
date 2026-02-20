import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:hive/hive.dart';
import 'package:park_enfoecement/app/core/constants/app_images.dart';
import 'package:park_enfoecement/app/core/di/di.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/app/core/services/brand_config_service.dart';
import 'package:park_enfoecement/app/shared/widgets/restart_widget.dart';
import 'package:path_provider/path_provider.dart';

import 'app/core/localization/app_locales.dart';
import 'app/core/localization/app_translations.dart';
import 'app/core/models/offline_request_model.dart';
import 'app/core/routes/app_pages.dart';
import 'app/core/services/local_storage_service.dart';
import 'app/core/theme/app_theme.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // final siteKey = Platform.isAndroid ? "6LehlLAcAAAAAOWuW3qLLbUKE53Wqe2oCmvtFHRk" : "<IOS_SITE_KEY>";

  SystemChrome.setPreferredOrientations([DeviceOrientation.portraitDown, DeviceOrientation.portraitUp]);

  await GetStorage.init();
  final translations = await AppTranslations.load();

  final appDocDir = await getApplicationDocumentsDirectory();
  Hive.init(appDocDir.path);

  Hive.registerAdapter(OfflineRequestAdapter());

  await LocalStorageService.init();
  Get.lazyPut<BrandConfigService>(() => BrandConfigService());
  final brandConfig = Get
      .find<BrandConfigService>()
      .selectedBrand;
   String initialPage = Routes.brandPage;
  if(brandConfig!=null) initialPage = Routes.splash;

    runApp(RestartWidget(child: MyApp(translations: translations, initialPage: initialPage,)));
}

class MyApp extends StatelessWidget {
  final Map<String, Map<String, String>> translations;
  final String initialPage;


  const MyApp({super.key, required this.translations, required this.initialPage});

  @override
  Widget build(BuildContext context) {
    precacheImage(AssetImage(AppImages.customBackGround), context);
    precacheImage(AssetImage(AppImages.logo), context);
    return ScreenUtilInit(
      minTextAdapt: true,
      splitScreenMode: true,
      builder: (context, child) {
        return GetMaterialApp(
          debugShowCheckedModeBanner: false,
          getPages: AppPages.routes,
          theme: AppTheme.lightTheme,
          translations: AppTranslations(translations),
          locale: AppLocales.english,
          fallbackLocale: AppLocales.fallback,
          initialRoute: initialPage,
          home: child,
          initialBinding: Di(),
        );
      },
    );
  }
}

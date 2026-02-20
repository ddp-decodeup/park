import 'dart:io';

import 'package:get/get.dart';
import 'package:park_enfoecement/features/brand/bindings/brand_binding.dart';
import 'package:park_enfoecement/features/brand/ui/brand_page.dart';
import 'package:park_enfoecement/features/home/home_binding.dart';
import 'package:park_enfoecement/features/login/login_binding.dart';
import 'package:park_enfoecement/features/login/ui/screens/forgot_password_page.dart';
import 'package:park_enfoecement/features/lookup/ui/screens/citation_result_page.dart';
import 'package:park_enfoecement/features/municipal_citation/bindings/municipal_citation_bindings.dart';
import 'package:park_enfoecement/features/municipal_citation/ui/screens/municipal_citation_preview.dart';
import 'package:park_enfoecement/features/municipal_citation/ui/screens/municipal_citation_screen.dart';
import 'package:park_enfoecement/features/my_activity/bindings/daily_summary_bindings.dart';
import 'package:park_enfoecement/features/my_activity/bindings/graph_view_bindings.dart';
import 'package:park_enfoecement/features/my_activity/bindings/lpr_hits_bindings.dart';
import 'package:park_enfoecement/features/my_activity/ui/screens/daily_summary/daily_summary_screen.dart';
import 'package:park_enfoecement/features/my_activity/ui/screens/graph_view/graph_view.dart';
import 'package:park_enfoecement/features/my_activity/ui/screens/lpr_hits/lpr_hits_screen.dart';
import 'package:park_enfoecement/features/my_activity/ui/screens/my_activity_page.dart';
import 'package:park_enfoecement/features/reports/ui/screens/reports.dart';
import 'package:park_enfoecement/features/scan/bindings/scan_bindings.dart';
import 'package:park_enfoecement/features/settings/ui/screens/settings.dart';
import 'package:park_enfoecement/features/ticketing/bindings/pay_by_plate_bindings.dart';
import 'package:park_enfoecement/features/ticketing/bindings/pay_by_space_bindings.dart';
import 'package:park_enfoecement/features/ticketing/bindings/ticket_issue_bindings.dart';
import 'package:park_enfoecement/features/ticketing/bindings/ticket_preview_bindings.dart';
import 'package:park_enfoecement/features/ticketing/ui/screens/pay_by_plate_screen.dart';
import 'package:park_enfoecement/features/ticketing/ui/screens/pay_by_space_screen.dart';
import 'package:park_enfoecement/features/ticketing/ui/screens/ticket_issue_screen.dart';
import 'package:park_enfoecement/features/ticketing/ui/screens/ticketing_page.dart';

import '../../../features/home/ui/screens/home_page.dart';
import '../../../features/login/ui/screens/login_page.dart';
import '../../../features/lookup/bindings/citation_result_binding.dart';
import '../../../features/scan/ui/screens/manual_data_entry_page.dart';
import '../../../features/splash/splash_binding.dart';
import '../../../features/splash/splash_page.dart';
import '../../../features/ticketing/bindings/ticket_issue_preview_bindings.dart';
import '../../../features/ticketing/data/models/ticket_creation_request.dart';
import '../../../features/ticketing/ui/screens/ticket_issue_preview_page.dart';
import '../../../features/ticketing/ui/screens/ticket_preview_screen.dart';
import '../models/template_model.dart';
import 'app_routes.dart';

class AppPages {
  static const initial = Routes.brandPage;

  static final routes = <GetPage>[
    /// Initial pages
    GetPage(name: Routes.brandPage, page: () => const BrandPage(), binding: BrandBinding()),
    GetPage(name: Routes.splash, page: () => const SplashPage(), binding: SplashBinding()),

    /// auth pages
    GetPage(name: Routes.login, page: () => LoginPage(), binding: LoginBinding()),
    GetPage(name: Routes.forgotPassword, page: () => ForgotPasswordPage(), binding: LoginBinding()),

    /// main pages
    GetPage(
      name: Routes.home,
      page: () => const HomePage(),
      transition: Transition.noTransition,
      binding: HomeBinding(),
    ),

    /// ticketing pages
    GetPage(
      name: Routes.ticketing,
      page: () => const TicketingPage(),
      transition: Transition.noTransition,
      binding: TicketIssueBindings(),
    ),
    GetPage(name: Routes.ticketIssue, page: () => const TicketIssueScreen(), binding: TicketIssueBindings()),
    GetPage(
      name: Routes.ticketIssuePreview,
      page: () => const TicketIssuePreviewPage(),
      arguments: TemplateModel,
      binding: TicketIssuePreviewBindings(),
    ),
    GetPage(
      name: Routes.ticketPreview,
      page: () => const TicketPreviewScreen(),
      arguments: {'model': TicketCreationRequest, 'images': List<File>},
      binding: TicketPreviewBindings(),
    ),
    GetPage(
      name: Routes.citationResult,
      page: () => const CitationResultPage(),
      arguments: {'model': TemplateModel, 'images': List<File>},
      binding: CitationResultBinding(),
    ),

    GetPage(
      name: Routes.municipalCitation,
      page: () => const MunicipalCitationScreen(),
      binding: MunicipalCitationBindings(),
    ),  GetPage(
      name: Routes.municipalCitationPreview,
      page: () => const MunicipalCitationPreview(),
      binding: MunicipalCitationBindings(),
    ),

    GetPage(name: Routes.payByPlate, page: () => const PayByPlateScreen(), binding: PayByPlateBindings()),

    GetPage(name: Routes.payBySpace, page: () => const PayBySpaceScreen(), binding: PayBySpaceBindings()),

    /// My Activity Pages
    GetPage(name: Routes.myActivity, page: () => const MyActivityPage(), transition: Transition.noTransition),
    GetPage(name: Routes.graphView, page: () => const GraphView(), binding: GraphViewBindings()),
    GetPage(name: Routes.lprHits, page: () => const LprHitScreen(), binding: LprHitsBindings()),
    GetPage(name: Routes.dailySummary, page: () => const DailySummaryScreen(), binding: DailySummaryBindings()),

    ///scanning pages
    GetPage(name: Routes.manualEntry, page: () => const ManualDataEntryPage(), binding: ScanBindings()),

    /// Settings Pages
    GetPage(name: Routes.settings, page: () => const SettingsPage(), transition: Transition.noTransition),

    /// Reports Pages
    GetPage(name: Routes.reports, page: () => const ReportsPage(), transition: Transition.noTransition),
  ];
}

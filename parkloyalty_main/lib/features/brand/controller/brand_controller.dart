import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/routes/app_routes.dart';
import 'package:park_enfoecement/app/core/services/brand_config_service.dart';
import 'package:park_enfoecement/app/core/theme/app_colors.dart';
import 'package:park_enfoecement/app/shared/utils/snackbar_utils.dart';
import 'package:park_enfoecement/features/brand/data/brand_option.dart';

class BrandController extends GetxController {
  final BrandConfigService brandConfigService;

  BrandController(this.brandConfigService);

  final TextEditingController baseUrlController = TextEditingController();
  final TextEditingController customerNameController = TextEditingController();
  final TextEditingController siteIdController = TextEditingController();
  bool isLoading = true;
  String? selectedSiteId;
  String? selectedCustomer;

  List<BrandOption> get options => brandConfigService.options;

  List<String> get siteIds {
    final values = options.map((e) => e.siteId).toSet().toList();
    values.sort();
    return values;
  }

  List<String> get customers {
    final values = options.map((e) => e.customerName).toSet().toList();
    values.sort();
    return values;
  }

  @override
  void onInit() {
    _initialize();
    super.onInit();
  }

  Future<void> _initialize() async {
    await brandConfigService.loadCatalog();
    final selected = brandConfigService.selectedBrand;

    if (selected != null) {
      Get.offAllNamed(Routes.splash);
      return;
    }

    // final fallback = options.isNotEmpty ? options.firstWhere((element) => element.customerName.contains("A_BOBS_TOWING_UAT"),) : null;
    final fallback = options.isNotEmpty
        ? options.firstWhere((element) => element.customerName.contains("SANIBEL_UAT"))
        : null;
    final current = fallback;
    if (current != null) {
      final validSite = siteIds.contains(current.siteId);
      final validCustomer = customers.contains(current.customerName);
      selectedSiteId = validSite ? current.siteId : null;
      siteIdController.text = selectedSiteId ?? '';
      selectedCustomer = validCustomer ? current.customerName : null;
      customerNameController.text = selectedCustomer ?? '';
      baseUrlController.text = BrandConfigService.normalizeBaseUrl(current.baseUrl);
    }
    isLoading = false;
    update();
  }

  @override
  void onClose() {
    baseUrlController.dispose();
    super.onClose();
  }

  void onSiteChanged(String? siteId) {
    if (siteId == null) return;
    final option = _resolveBySite(siteId);
    if (option == null) return;
    selectedSiteId = option.siteId;
    selectedCustomer = option.customerName;
    baseUrlController.text = option.baseUrl;
    update();
  }

  void onCustomerChanged(String? customer) {
    if (customer == null) return;
    final option = _resolveByCustomer(customer);
    if (option == null) return;
    selectedSiteId = option.siteId;
    selectedCustomer = option.customerName;
    baseUrlController.text = option.baseUrl;
    update();
  }

  Future<bool> persistSelection() async {
    final siteId = selectedSiteId?.trim() ?? '';
    final customer = selectedCustomer?.trim() ?? '';
    final normalizedBaseUrl = BrandConfigService.normalizeBaseUrl(baseUrlController.text);

    if (siteId.isEmpty || customer.isEmpty || normalizedBaseUrl.isEmpty) {
      SnackBarUtils.showSnackBar(message: 'Please select Site ID, Customer and Base URL.', color: AppColors.errorRed);
      return false;
    }

    await brandConfigService.saveConfiguration(siteId: siteId, customerName: customer, baseUrl: normalizedBaseUrl);
    baseUrlController.text = normalizedBaseUrl;
    return true;
  }

  BrandOption? _resolveBySite(String siteId) {
    final matches = options.where((e) => e.siteId == siteId).toList();
    if (matches.isEmpty) return null;
    final currentCustomer = selectedCustomer?.trim() ?? '';
    if (currentCustomer.isNotEmpty) {
      final byCustomer = matches.firstWhereOrNull((e) => e.customerName == currentCustomer);
      if (byCustomer != null) return byCustomer;
    }
    final currentBaseUrl = BrandConfigService.normalizeBaseUrl(baseUrlController.text);
    if (currentBaseUrl.isNotEmpty) {
      final byBaseUrl = matches.firstWhereOrNull(
        (e) => BrandConfigService.normalizeBaseUrl(e.baseUrl) == currentBaseUrl,
      );
      if (byBaseUrl != null) return byBaseUrl;
    }
    matches.sort((a, b) => a.customerName.compareTo(b.customerName));
    return matches.first;
  }

  BrandOption? _resolveByCustomer(String customer) {
    final matches = options.where((e) => e.customerName == customer).toList();
    if (matches.isEmpty) return null;
    final currentSiteId = selectedSiteId?.trim() ?? '';
    if (currentSiteId.isNotEmpty) {
      final bySite = matches.firstWhereOrNull((e) => e.siteId == currentSiteId);
      if (bySite != null) return bySite;
    }
    final currentBaseUrl = BrandConfigService.normalizeBaseUrl(baseUrlController.text);
    if (currentBaseUrl.isNotEmpty) {
      final byBaseUrl = matches.firstWhereOrNull(
        (e) => BrandConfigService.normalizeBaseUrl(e.baseUrl) == currentBaseUrl,
      );
      if (byBaseUrl != null) return byBaseUrl;
    }
    matches.sort((a, b) => a.siteId.compareTo(b.siteId));
    return matches.first;
  }
}

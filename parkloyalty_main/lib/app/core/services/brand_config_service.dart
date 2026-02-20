import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:park_enfoecement/app/core/api/api_client.dart';
import 'package:park_enfoecement/app/core/constants/api_endpoints.dart';
import 'package:park_enfoecement/app/core/constants/consts.dart';
import 'package:park_enfoecement/app/core/constants/storage_keys.dart';
import 'package:park_enfoecement/features/brand/data/brand_option.dart';

class BrandConfigService extends GetxService {
  final GetStorage _storage = GetStorage();
  bool _catalogLoaded = false;

  static const List<BrandOption> _defaultOptions = [
    BrandOption(
      customerName: 'A_BOBS_TOWING_UAT',
      siteId: '2a57096c-8d43-11f0-bd74-8269c32d74e2',
      baseUrl: 'https://devapi.parkloyalty.com/',
    ),
    BrandOption(
      customerName: 'A_BOBS_TOWING_PROD',
      siteId: '871de3e0-8d44-11f0-b5e4-ee775f4253ca',
      baseUrl: 'https://api.parkloyalty.com/',
    ),
    BrandOption(
      customerName: 'GLENDALE_UAT',
      siteId: '884f1f0b-765c-40b6-be5d-22bb6768e4fe',
      baseUrl: 'https://devapi.parkloyalty.com/',
    ),
    BrandOption(
      customerName: 'GLENDALE_PROD',
      siteId: '2d689158-dcca-4e49-94f3-5ef82439228e',
      baseUrl: 'https://api.parkloyalty.com/',
    ),
    BrandOption(
      customerName: 'PARK',
      siteId: '68ea3b5c-172f-4f33-8d55-e76c301ad212',
      baseUrl: 'https://devapi.parkloyalty.com/',
    ),
  ];

  List<BrandOption> _options = [];

  List<BrandOption> get options =>
      _options.isNotEmpty ? _options : _defaultOptions;

  static String normalizeBaseUrl(String baseUrl) {
    final trimmed = baseUrl.trim();
    if (trimmed.isEmpty) return trimmed;
    return trimmed.endsWith('/') ? trimmed : '$trimmed/';
  }

  Future<void> loadCatalog() async {
    if (_catalogLoaded) return;
    try {
      final raw = await rootBundle.loadString(
        'assets/config/brand_options.json',
      );
      final data = jsonDecode(raw);
      if (data is List) {
        _options =
            data
                .whereType<Map>()
                .map((e) => BrandOption.fromJson(Map<String, dynamic>.from(e)))
                .map((e) => e.copyWith(baseUrl: normalizeBaseUrl(e.baseUrl)))
                .where(
                  (e) =>
                      e.customerName.trim().isNotEmpty &&
                      e.siteId.trim().isNotEmpty &&
                      e.baseUrl.trim().isNotEmpty,
                )
                .toList()
              ..sort((a, b) {
                final customerCompare = a.customerName.compareTo(
                  b.customerName,
                );
                if (customerCompare != 0) return customerCompare;
                return a.siteId.compareTo(b.siteId);
              });
      }
    } catch (_) {}
    _catalogLoaded = true;
  }

  BrandOption? get selectedBrand {
    final dynamic raw = _storage.read(StorageKeys.brandConfig);
    if (raw is Map) {
      final selected = BrandOption.fromJson(Map<String, dynamic>.from(raw));
      return selected.copyWith(baseUrl: normalizeBaseUrl(selected.baseUrl));
    }
    return null;
  }

  void applyCurrentConfiguration() {
    final selected = selectedBrand;
    if (selected == null) {
      _applyToRuntime(siteId: Consts.sideId, baseUrl: ApiEndpoints.baseUrl);
      return;
    }
    _applyToRuntime(siteId: selected.siteId, baseUrl: selected.baseUrl);
  }

  Future<void> saveConfiguration({
    required String siteId,
    required String customerName,
    required String baseUrl,
  }) async {
    final normalizedBaseUrl = normalizeBaseUrl(baseUrl);
    final option = BrandOption(
      customerName: customerName,
      siteId: siteId,
      baseUrl: normalizedBaseUrl,
    );
    await _storage.write(StorageKeys.brandConfig, option.toJson());
    _applyToRuntime(siteId: siteId, baseUrl: normalizedBaseUrl);
  }

  void _applyToRuntime({required String siteId, required String baseUrl}) {
    Consts.sideId = siteId;
    ApiEndpoints.baseUrl = normalizeBaseUrl(baseUrl);
    if (Get.isRegistered<ApiClient>()) {
      Get.find<ApiClient>().httpClient.baseUrl = ApiEndpoints.baseUrl;
    }
  }
}

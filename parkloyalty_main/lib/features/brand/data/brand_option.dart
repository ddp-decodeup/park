class BrandOption {
  final String customerName;
  final String siteId;
  final String baseUrl;

  const BrandOption({
    required this.customerName,
    required this.siteId,
    required this.baseUrl,
  });

  BrandOption copyWith({
    String? customerName,
    String? siteId,
    String? baseUrl,
  }) {
    return BrandOption(
      customerName: customerName ?? this.customerName,
      siteId: siteId ?? this.siteId,
      baseUrl: baseUrl ?? this.baseUrl,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'customer_name': customerName,
      'site_id': siteId,
      'base_url': baseUrl,
    };
  }

  factory BrandOption.fromJson(Map<String, dynamic> json) {
    return BrandOption(
      customerName: json['customer_name'] ?? '',
      siteId: json['site_id'] ?? '',
      baseUrl: json['base_url'] ?? '',
    );
  }
}

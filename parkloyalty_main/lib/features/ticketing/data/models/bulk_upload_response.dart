class BulkUploadResponse {
  final List<UploadDataItem> data;
  final bool status;
  final String message;

  BulkUploadResponse({
    required this.data,
    required this.status,
    required this.message,
  });

  factory BulkUploadResponse.fromJson(Map<String, dynamic> json) {
    return BulkUploadResponse(
      data: (json['data'] as List<dynamic>)
          .map((e) => UploadDataItem.fromJson(e))
          .toList(),
      status: json['status'] as bool,
      message: json['message'] as String,
    );
  }
}

class UploadDataItem {
  final bool status;
  final UploadResponseData response;
  final dynamic metadata; // can be null or any type

  UploadDataItem({
    required this.status,
    required this.response,
    this.metadata,
  });

  factory UploadDataItem.fromJson(Map<String, dynamic> json) {
    return UploadDataItem(
      status: json['status'] as bool,
      response: UploadResponseData.fromJson(json['response']),
      metadata: json['metadata'],
    );
  }
}

class UploadResponseData {
  final List<String> links;

  UploadResponseData({required this.links});

  factory UploadResponseData.fromJson(Map<String, dynamic> json) {
    return UploadResponseData(
      links: List<String>.from(json['links'] as List),
    );
  }
}

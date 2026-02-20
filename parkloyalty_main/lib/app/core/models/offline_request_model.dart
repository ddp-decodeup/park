import 'package:hive/hive.dart';
part 'offline_request_model.g.dart';

@HiveType(typeId: 1)
class OfflineRequest extends HiveObject {
  @HiveField(0)
  final String url;

  @HiveField(1)
  final String method;

  @HiveField(2)
  final Map<String, dynamic> body;

  @HiveField(3)
  final String createdAt;

  OfflineRequest({
    required this.url,
    required this.method,
    required this.body,
    required this.createdAt,
  });
}

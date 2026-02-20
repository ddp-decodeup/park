import 'dart:convert';

import 'package:connectivity_plus/connectivity_plus.dart';

import '../../../features/home/data/repository/home_storage_repository.dart';
import '../api/api_client.dart';

class OfflineSyncService {
  final ApiClient apiClient;
  final HomeStorageRepository localStorage;

  OfflineSyncService({required this.apiClient, required this.localStorage});

  void startListening() {
    Connectivity().onConnectivityChanged.listen((result) {
      if (!result.contains(ConnectivityResult.none)) {
        syncPendingPosts();
      }
    });
  }

  Future<void> syncPendingPosts() async {
    final requests = localStorage.getOfflineRequests();
    if (requests.isEmpty) return;

    for (final req in requests) {
      try {
        final body = jsonDecode(jsonEncode(req.body));

        final response = await apiClient.postRequest(req.url, body);

        if (response.isOk) {
          await localStorage.deleteOfflineRequest(req.createdAt);
        }
      } catch (e) {
        // stop syncing, try later again
        break;
      }
    }
  }
}

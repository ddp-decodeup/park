import 'package:hive/hive.dart';

import '../models/offline_request_model.dart';

class LocalStorageService {
  static const String dropDown = 'dropdown_box';
  static const String offlineRequests = 'offline_requests';
  static const String templates = 'templates';
  static const String citationBook = 'citation_book';

  static Box<OfflineRequest> get offlineRequestsBox =>
      Hive.box<OfflineRequest>(offlineRequests);

  static Box<String> get templateBox => Hive.box<String>(templates);

  static Box<String> get citationBookBox => Hive.box<String>(citationBook);

  static Box<String> get dropDownBox => Hive.box<String>(dropDown);

  static Future<void> init() async {
    await Hive.openBox<OfflineRequest>(offlineRequests);
    await Hive.openBox<String>(templates);
    await Hive.openBox<String>(citationBook);
    await Hive.openBox<String>(dropDown);
  }
}

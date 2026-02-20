import 'package:park_enfoecement/app/core/controllers/base_controller.dart';

import '../../lookup/data/models/timing_record.dart';
import '../data/repositories/status_and_history_repository.dart';

class StatusController extends BaseController {
  StatusController(this.repository);

  StatusAndHistoryRepository repository;
  List<TimingData> statusList = [];

  Future<void> fetchStatusList(String lprNumber) async {
    run(() async {
      var res = await repository.fetchStatusList(lprNumber);
      if (res.data.isNotEmpty) statusList = res.data;
      update();
    });
  }

  void clearStatusList() {
    statusList.clear();
    update();
  }
}

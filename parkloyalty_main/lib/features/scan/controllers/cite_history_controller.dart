import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/features/scan/data/models/lpr_model.dart';

import '../data/repositories/status_and_history_repository.dart';

class CiteHistoryController extends BaseController {
  CiteHistoryController(this.repository);

  StatusAndHistoryRepository repository;
  List<Result> historyList = [];

  Future<void> fetchHistory(String lprNumber) async {
    run(() async {
      var res = await repository.fetchHistory(lprNumber, 1);
      if (res.data.isNotEmpty) historyList = res.data[0].response.results;
      update();
    });
  }

  void clearHistoryList() {
    historyList.clear();
    update();
  }
}

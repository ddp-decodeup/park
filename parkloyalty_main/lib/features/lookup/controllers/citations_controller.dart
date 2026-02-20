import 'dart:async';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/features/lookup/data/models/citation.dart';
import 'package:park_enfoecement/features/lookup/data/models/request_model.dart';
import 'package:park_enfoecement/features/lookup/data/repositories/citations_repository.dart';

class CitationsController extends BaseController {
  CitationsController({required this.citationsRepository});

  final CitationsRepository citationsRepository;

  List<DataSet> streetList = [];
  List<CitationData> citationList = [];

  int _page = 1;
  bool isLoading = false;
  bool hasMore = true;
  final ScrollController scrollController = ScrollController();
  RequestModel citationReq = RequestModel(), timingRecordReq = RequestModel();
  Timer? _debounce;

  @override
  void onInit() {
    fetchStreets();
    fetchCitations();

    scrollController.addListener(_onScroll);
    super.onInit();
  }

  void _onScroll() {
    if (scrollController.position.pixels >=
            scrollController.position.maxScrollExtent - 200 &&
        !isLoading &&
        hasMore) {
      fetchCitations();
    }
  }

  Future fetchStreets() async {
    var model = await citationsRepository.fetchDropDownData('StreetList');
    streetList = model.data[0].response;
    update();
  }

  Future fetchCitations({bool refresh = false}) async {
    if (isLoading) return;

    if (refresh) {
      _page = 1;
      hasMore = true;
      citationList.clear();
    }

    isLoading = true;
    update();

    final model = await citationsRepository.fetchCitations(_page, citationReq);

    if (model.data.isEmpty) {
      hasMore = false;
    } else {
      citationList.addAll(model.data);
      _page++;
    }

    isLoading = false;
    update();
  }

  Future fetchTimingRecords({bool refresh = false}) async {
    if (isLoading) return;

    if (refresh) {
      _page = 1;
      hasMore = true;
      citationList.clear();
    }

    isLoading = true;
    update();

    final model = await citationsRepository.fetchCitations(_page, citationReq);

    if (model.data.isEmpty) {
      hasMore = false;
    } else {
      citationList.addAll(model.data);
      _page++;
    }

    isLoading = false;
    update();
  }

  @override
  void onClose() {
    scrollController.dispose();
    super.onClose();
  }

  void applyCitationFilter() {
    fetchCitations(refresh: true);
    Get.back();
  }

  void searchTicket(String s) {
    if (_debounce?.isActive ?? false) _debounce!.cancel();

    _debounce = Timer(const Duration(milliseconds: 500), () {
      citationReq.ticketNo = s;
      fetchCitations(refresh: true);
    });
  }
}

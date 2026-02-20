import 'dart:async';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/consts.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/localization/local_keys.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/features/lookup/data/models/request_model.dart';
import 'package:park_enfoecement/features/lookup/data/models/timing_record.dart';
import 'package:park_enfoecement/features/lookup/data/repositories/citations_repository.dart';

import '../../../app/shared/controller/loader_controller.dart';

class TimingRecordsController extends BaseController {
  TimingRecordsController({
    required this.citationsRepository,
    required this.loaderController,
  });

  final CitationsRepository citationsRepository;
  final LoaderController loaderController;

  List<DataSet> streetList = [], timeTypeList = [], sideList = [];
  List<TimingData> timingRecordList = [];
  List<String> selectedRecordList = [];

  int _page = 1;
  bool isLoading = false;
  bool hasMore = true;
  final ScrollController scrollController = ScrollController();
  RequestModel timingRecordReq = RequestModel();
  Timer? _debounce;
  int listValueKey = DateTime.now().microsecondsSinceEpoch,
      checkBoxKey = DateTime.now().microsecondsSinceEpoch;

  @override
  void onInit() {
    fetchTimingRecords();
    fetchStreets();
    fetchRegulationTime();
    fetchSideList();

    scrollController.addListener(_onScroll);
    super.onInit();
  }

  void _onScroll() {
    if (scrollController.position.pixels >=
            scrollController.position.maxScrollExtent - 200 &&
        !isLoading &&
        hasMore) {
      fetchTimingRecords();
    }
  }

  Future fetchStreets() async {
    run(() async {
      var model = await citationsRepository.fetchDropDownData('StreetList');
      streetList = model.data[0].response;
      update();
    });
  }

  Future fetchRegulationTime() async {
    run(() async {
      var model = await citationsRepository.fetchDropDownData(
        'RegulationTimeList',
      );
      timeTypeList = model.data[0].response;
    });
  }

  Future fetchSideList() async {
    run(() async {
      var model = await citationsRepository.fetchDropDownData(
        'SideList',
        shift: Consts.sideId,
      );
      sideList = model.data[0].response;
    });
  }

  Future fetchTimingRecords({bool refresh = false}) async {
    run(() async {
      if (isLoading) return;

      if (refresh) {
        _page = 1;
        hasMore = true;
        timingRecordList.clear();
      }

      isLoading = true;
      update();

      final model = await citationsRepository.fetchTimingRecords(
        _page,
        timingRecordReq,
      );

      if (model.data.isEmpty) {
        hasMore = false;
      } else {
        timingRecordList.addAll(model.data);
        _page++;
      }

      isLoading = false;
      update();
    });
  }

  void selectRecord(TimingData data) {
    if (selectedRecordList.contains(data.id)) {
      selectedRecordList.remove(data.id);
    } else {
      selectedRecordList.add(data.id);
    }
    checkBoxKey = DateTime.now().microsecondsSinceEpoch;
    update();
  }

  @override
  void onClose() {
    scrollController.dispose();
    super.onClose();
  }

  void applyFilter() {
    fetchTimingRecords(refresh: true);
    Get.back();
  }

  void searchRecord(String s) {
    if (_debounce?.isActive ?? false) _debounce!.cancel();

    _debounce = Timer(const Duration(milliseconds: 500), () {
      timingRecordReq.licenseNo = s;
      fetchTimingRecords(refresh: true);
    });
  }

  bool isAllSelected() {
    return timingRecordList.isNotEmpty &&
        selectedRecordList.length == timingRecordList.length;
  }

  void checkUncheckAll(bool b) {
    if (b) {
      selectedRecordList = timingRecordList.map((e) => e.id).toList();
    } else {
      selectedRecordList.clear();
    }
    listValueKey = DateTime.now().microsecondsSinceEpoch;
    update();
  }

  Future<void> mark() async {
    loaderController.showLoader();
    try {
      await run(() async {
        final res = await citationsRepository.mark(selectedRecordList);
        if (res['success']) {
          Get.snackbar(LocalKeys.markGOA.tr, LocalKeys.markGoaSuccess.tr);
          timingRecordList.removeWhere(
            (element) => selectedRecordList.contains(element.id),
          );
          selectedRecordList.clear();
          listValueKey = DateTime.now().microsecondsSinceEpoch;
          checkBoxKey = DateTime.now().microsecondsSinceEpoch;
          update();
        }
      });
    } finally {
      loaderController.hideLoader();
    }
  }
}

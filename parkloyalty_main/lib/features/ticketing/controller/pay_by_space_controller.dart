import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/enums.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/pay_by_space_repository.dart';

class PayBySpaceController extends BaseController {
  final PayBySpaceRepository payBySpaceRepository;
  final LoaderController loaderController;
  final HomeStorageRepository homeStorageRepository;

  PayBySpaceController({
    required this.payBySpaceRepository,
    required this.loaderController,
    required this.homeStorageRepository,
  });

  List<DataSet> zoneList = <DataSet>[].obs;
  TextEditingController lpNumberController = TextEditingController();
  DataSet? selectedZone;
  Timer? _debounce;

  @override
  void onInit() {
    getZoneList();
    super.onInit();
  }

  void getZoneList() {
    loaderController.showLoader();
    final zoneDataSetTypes = DataSetTypes.pBCZoneList;
    final res = homeStorageRepository.getDropDownData(zoneDataSetTypes);
    if (res != null) {
      final dropDownModel = DropDownModel.fromJson(jsonDecode(res));
      zoneList = List.from(dropDownModel.data[0].response);
    } else {
      final res = homeStorageRepository.getDropDownData(zoneDataSetTypes);
      if (res != null) {
        final dropDownModel = DropDownModel.fromJson(jsonDecode(res));
        zoneList = List.from(dropDownModel.data[0].response);
      } else {
        zoneList = [];
      }
    }
    loaderController.hideLoader();
    update();
  }

  void onZoneChanged(DataSet? data) {
    selectedZone = data;
    fetchPayBySpaceAnalytics();
    update();
  }

  void onChangePlateNumber(String? value) {
    if (value != null && value.isNotEmpty && value.length >= 4) {
      _debounce?.cancel();
      _debounce = Timer(const Duration(milliseconds: 700), () {
        fetchPayBySpaceAnalytics();
      });
    }
  }

  Future<void> fetchPayBySpaceAnalytics() async {
    loaderController.showLoader();
    try {
      await run(() async {
        final res = await payBySpaceRepository.getPayBySpaceAnalytics(
          zone: selectedZone?.label1.toString().trim(),
          lpNumber: lpNumberController.text.trim(),
        );

        if (res.statusCode == 200) {
        } else {}
      });
    } finally {
      loaderController.hideLoader();
    }
  }
}

import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:park_enfoecement/app/core/constants/enums.dart';
import 'package:park_enfoecement/app/core/controllers/base_controller.dart';
import 'package:park_enfoecement/app/core/models/drop_down_model.dart';
import 'package:park_enfoecement/app/shared/controller/loader_controller.dart';
import 'package:park_enfoecement/features/home/data/repository/home_storage_repository.dart';
import 'package:park_enfoecement/features/ticketing/data/repository/pay_by_plate_repository.dart';

class PayByPlateController extends BaseController {
  final PayByPlateRepository payByPlateRepository;
  final LoaderController loaderController;
  final HomeStorageRepository homeStorageRepository;

  PayByPlateController({
    required this.payByPlateRepository,
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
    fetchPayByPlateAnalytics();
    update();
  }

  void onChangePlateNumber(String? value) {
    if (value != null && value.isNotEmpty && value.length >= 4) {
      _debounce?.cancel();
      _debounce = Timer(const Duration(milliseconds: 700), () {
        fetchPayByPlateAnalytics();
      });
    }
  }

  Future<void> fetchPayByPlateAnalytics() async {
    loaderController.showLoader();
    try {
      await run(() async {
        final res = await payByPlateRepository.getPayByPlateAnalytics(
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

import 'package:flutter/material.dart';

class ChartDataModel {
  final String key;
  final double value;

  ChartDataModel(this.key, this.value);

  @override
  String toString() {
    return '$key: $value';
  }
}

class LineChartModel {
  final Color color;
  final String label;
  final List<ChartDataModel> data;

  LineChartModel({required this.color, required this.label, required this.data});
}

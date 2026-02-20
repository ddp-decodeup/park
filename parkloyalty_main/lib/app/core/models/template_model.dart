// To parse this JSON data, do
//
//     final templateModel = templateModelFromJson(jsonString);

import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:park_enfoecement/app/core/constants/field_types.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';

import 'drop_down_model.dart' as dataSet;

TemplateModel templateModelFromJson(String str) =>
    TemplateModel.fromJson(json.decode(str));

String templateModelToJson(TemplateModel data) => json.encode(data.toJson());

class TemplateModel {
  List<TemplateData> data;
  bool? status;
  String? message;

  TemplateModel({required this.data, this.status, this.message});

  factory TemplateModel.fromJson(Map<String, dynamic> json) => TemplateModel(
    data: List<TemplateData>.from(
      json["data"].map((x) => TemplateData.fromJson(x)),
    ).where((element) => element.response.isNotEmpty).toList(),
    status: json["status"],
    message: json["message"],
  );

  Map<String, dynamic> toJson() => {
    "data": List<dynamic>.from(data.map((x) => x.toJson())),
    "status": status,
    "message": message,
  };
}

class TemplateData {
  bool? status;
  List<TemplateRes> response;
  dynamic metadata;

  TemplateData({this.status, required this.response, this.metadata});

  factory TemplateData.fromJson(Map<String, dynamic> json) => TemplateData(
    status: json["status"] ?? false,
    response: List<TemplateRes>.from(
      json["response"].map((x) => TemplateRes.fromJson(x)),
    ),
    metadata: json["metadata"],
  );

  Map<String, dynamic> toJson() => {
    "status": status,
    "response": List<dynamic>.from(response.map((x) => x.toJson())),
    "metadata": metadata,
  };
}

class TemplateRes {
  String? id;
  String? component;
  List<Field> fields;

  TemplateRes({this.id, this.component, required this.fields});

  factory TemplateRes.fromJson(Map<String, dynamic> json) {
    return TemplateRes(
      id: json["_id"],
      component: json["component"],
      fields: List<Field>.from(
        (json["fields"] ?? []).map((x) => Field.fromJson(x)),
      ),
    );
  }

  Map<String, dynamic> toJson() => {
    "_id": id,
    "component": component,
    "fields": List<dynamic>.from(fields.map((x) => x.toJson())),
  };
}

class Field {
  String name;
  String repr;
  String aligned;
  bool isEditable;
  bool isRequired;
  String type;
  List<dataSet.DataSet>? dropDownOptionList;
  String? collectionName;
  String? fieldName;
  dynamic defaultValue;
  bool clickable;
  String tag;
  dataSet.DataSet? selectedDropDownOption;
  TextEditingController? enteredData;
  bool deleteEnable;
  String formLayoutOrder;
  String printLayoutOrder;
  dynamic dependentField;
  String? calculatedField;
  String? maxLength;
  String? minLength;
  String? dataTypeValidation;
  bool isChecked;

  Field({
    required this.name,
    required this.repr,
    required this.aligned,
    required this.isEditable,
    required this.isRequired,
    required this.type,
    required this.collectionName,
    required this.fieldName,
    required this.defaultValue,
    required this.clickable,
    required this.tag,
    required this.deleteEnable,
    required this.formLayoutOrder,
    required this.printLayoutOrder,
    this.dependentField,
    this.calculatedField,
    this.maxLength,
    this.minLength,
    this.dataTypeValidation,
    this.dropDownOptionList,
    this.enteredData,
    this.selectedDropDownOption,
    this.isChecked = false,
  });

  getValue(Field f) {
    if (f.tag == FieldTypes.editView ||
        f.tag == FieldTypes.textarea ||
        f.tag == FieldTypes.textview ||
        (f.dropDownOptionList == null && f.tag == FieldTypes.dropdown)) {
      logging('field is :: ${f.name} ${f.enteredData == null}');
      return f.enteredData?.text;
    } else {
      return f.selectedDropDownOption!.label1;
    }
  }

  factory Field.fromJson(Map<String, dynamic> json) => Field(
    name: json["name"],
    repr: json["repr"],
    aligned: json["aligned"],
    isEditable: json["is_editable"],
    isRequired: json["is_required"],
    type: json["type"],
    collectionName: json["collection_name"] ?? '',
    fieldName: json["field_name"],
    defaultValue: json["default_value"],
    clickable: json["clickable"],
    tag: json["tag"],
    deleteEnable: json["delete_enable"],
    formLayoutOrder: json["form_layout_order"],
    printLayoutOrder: json["print_layout_order"],
    dependentField: json["dependent_field"],
    calculatedField: json["calculated_field"],
    maxLength: json["max_length"],
    minLength: json["min_length"],
    dataTypeValidation: json["data_type_validation"],
  );

  Map<String, dynamic> toJson() => {
    "name": name,
    "repr": repr,
    "aligned": aligned,
    "is_editable": isEditable,
    "is_required": isRequired,
    "type": type,
    "collection_name": collectionName,
    "field_name": fieldName,
    "default_value": defaultValue,
    "clickable": clickable,
    "tag": tag,
    "delete_enable": deleteEnable,
    "form_layout_order": formLayoutOrder,
    "print_layout_order": printLayoutOrder,
    "dependent_field": dependentField,
    "calculated_field": calculatedField,
    "max_length": maxLength,
    "min_length": minLength,
    "data_type_validation": dataTypeValidation,
  };

  Map<String, dynamic> buildInitialFormValues(Map user) {
    return {
      "officer_id": user["site_officer_id"],
      "badge_id": user["officer_badge_id"],
      "supervisor": user["officer_supervisor"],
      "device_id": user["officer_device_id"]["device_id"],
      "agency": user["officer_agency"],
    };
  }
}

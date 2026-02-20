// To parse this JSON data, do
//
//     final citationTemplateModel = citationTemplateModelFromJson(jsonString);

import 'dart:convert';

CitationTemplateModel citationTemplateModelFromJson(String str) =>
    CitationTemplateModel.fromJson(json.decode(str));

String citationTemplateModelToJson(CitationTemplateModel data) =>
    json.encode(data.toJson());

class CitationTemplateModel {
  List<Datum> data;
  bool status;
  String message;

  CitationTemplateModel({
    required this.data,
    required this.status,
    required this.message,
  });

  factory CitationTemplateModel.fromJson(Map<String, dynamic> json) =>
      CitationTemplateModel(
        data: List<Datum>.from(json["data"].map((x) => Datum.fromJson(x))),
        status: json["status"],
        message: json["message"],
      );

  Map<String, dynamic> toJson() => {
    "data": List<dynamic>.from(data.map((x) => x.toJson())),
    "status": status,
    "message": message,
  };
}

class Datum {
  String id;
  String component;
  String componentName;
  List<Field> fields;

  Datum({
    required this.id,
    required this.component,
    required this.componentName,
    required this.fields,
  });

  factory Datum.fromJson(Map<String, dynamic> json) => Datum(
    id: json["_id"],
    component: json["component"],
    componentName: json["component_name"],
    fields: List<Field>.from(json["fields"].map((x) => Field.fromJson(x))),
  );

  Map<String, dynamic> toJson() => {
    "_id": id,
    "component": component,
    "component_name": componentName,
    "fields": List<dynamic>.from(fields.map((x) => x.toJson())),
  };
}

class Field {
  String name;
  String repr;
  Aligned aligned;
  bool print;
  bool removable;
  bool isEditable;
  bool isRequired;
  String? defaultValue;
  String formLayoutOrder;
  String printLayoutOrder;
  CalculatedField calculatedField;
  String maxLength;
  String minLength;
  String dataTypeValidation;
  String fieldName;
  bool deleteEnable;
  Type type;
  String collectionName;
  bool clickable;
  String? dependentField;
  String tag;
  int? defaultLength;
  String positionXYFont;
  String displayColumn;
  String? defaultValues;

  Field({
    required this.name,
    required this.repr,
    required this.aligned,
    required this.print,
    required this.removable,
    required this.isEditable,
    required this.isRequired,
    required this.defaultValue,
    required this.formLayoutOrder,
    required this.printLayoutOrder,
    required this.calculatedField,
    required this.maxLength,
    required this.minLength,
    required this.dataTypeValidation,
    required this.fieldName,
    required this.deleteEnable,
    required this.type,
    required this.collectionName,
    required this.clickable,
    required this.dependentField,
    required this.tag,
    this.defaultLength,
    required this.positionXYFont,
    required this.displayColumn,
    this.defaultValues,
  });

  factory Field.fromJson(Map<String, dynamic> json) => Field(
    name: json["name"],
    repr: json["repr"],
    aligned: alignedValues.map[json["aligned"]]!,
    print: json["print"],
    removable: json["removable"],
    isEditable: json["is_editable"],
    isRequired: json["is_required"],
    defaultValue: json["default_value"],
    formLayoutOrder: json["form_layout_order"],
    printLayoutOrder: json["print_layout_order"],
    calculatedField: calculatedFieldValues.map[json["calculated_field"]]!,
    maxLength: json["max_length"],
    minLength: json["min_length"],
    dataTypeValidation: json["data_type_validation"],
    fieldName: json["field_name"],
    deleteEnable: json["delete_enable"],
    type: typeValues.map[json["type"]]!,
    collectionName: json["collection_name"] ?? '',
    clickable: json["clickable"],
    dependentField: json["dependent_field"],
    tag: json["tag"],
    defaultLength: json["default_length"],
    positionXYFont: json["position_x_y_font"],
    displayColumn: json["display_column"],
    defaultValues: json["default_values"],
  );

  Map<String, dynamic> toJson() => {
    "name": name,
    "repr": repr,
    "aligned": alignedValues.reverse[aligned],
    "print": print,
    "removable": removable,
    "is_editable": isEditable,
    "is_required": isRequired,
    "default_value": defaultValue,
    "form_layout_order": formLayoutOrder,
    "print_layout_order": printLayoutOrder,
    "calculated_field": calculatedFieldValues.reverse[calculatedField],
    "max_length": maxLength,
    "min_length": minLength,
    "data_type_validation": dataTypeValidation,
    "field_name": fieldName,
    "delete_enable": deleteEnable,
    "type": typeValues.reverse[type],
    "collection_name": collectionName,
    "clickable": clickable,
    "dependent_field": dependentField,
    "tag": tag,
    "default_length": defaultLength,
    "position_x_y_font": positionXYFont,
    "display_column": displayColumn,
    "default_values": defaultValues,
  };
}

enum Aligned { CENTER, LEFT }

final alignedValues = EnumValues({
  "center": Aligned.CENTER,
  "left": Aligned.LEFT,
});

enum CalculatedField { AMOUNT0, EMPTY, SAVE }

final calculatedFieldValues = EnumValues({
  "Amount0": CalculatedField.AMOUNT0,
  "": CalculatedField.EMPTY,
  "save": CalculatedField.SAVE,
});

enum Type { BOOLEAN, NUMBER, STRING }

final typeValues = EnumValues({
  "boolean": Type.BOOLEAN,
  "number": Type.NUMBER,
  "string": Type.STRING,
});

class EnumValues<T> {
  Map<String, T> map;
  late Map<T, String> reverseMap;

  EnumValues(this.map);

  Map<T, String> get reverse {
    reverseMap = map.map((k, v) => MapEntry(v, k));
    return reverseMap;
  }
}

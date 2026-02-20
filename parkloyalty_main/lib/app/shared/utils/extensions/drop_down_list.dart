import '../../../core/models/drop_down_model.dart';

extension DropDownList on List<DataSet>? {
  List<DataSet> uniqueByLabel1() {

    if (this == null || this!.isEmpty) return <DataSet>[];

    final seen = <String?>{};
    final result = <DataSet>[];

    for (final item in this!) {
      if (!seen.contains(item.label1)) {
        seen.add(item.label1);
        result.add(item);
      }
    }

    return result;
  }
}

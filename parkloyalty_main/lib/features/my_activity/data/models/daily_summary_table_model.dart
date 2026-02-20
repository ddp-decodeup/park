class DailySummaryTableModel {
  final String heading;
  final String value;

  const DailySummaryTableModel({required this.heading, required this.value});

  factory DailySummaryTableModel.fromJson(Map<String, dynamic> json) {
    return DailySummaryTableModel(
      heading: json['heading'] as String? ?? '',
      value: json['value'] as String? ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {'heading': heading, 'value': value};
  }

  factory DailySummaryTableModel.empty() {
    return const DailySummaryTableModel(heading: '', value: '');
  }

  DailySummaryTableModel copyWith({String? heading, String? value}) {
    return DailySummaryTableModel(
      heading: heading ?? this.heading,
      value: value ?? this.value,
    );
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is DailySummaryTableModel &&
        other.heading == heading &&
        other.value == value;
  }

  @override
  int get hashCode => Object.hash(heading, value);

  @override
  String toString() {
    return 'DailySummaryTableModel(heading: $heading, value: $value)';
  }
}

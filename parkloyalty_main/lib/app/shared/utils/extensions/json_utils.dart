extension JsonParsing on Object? {
  String? asString() {
    if (this == null) return null;
    return toString();
  }

  int? asInt() {
    if (this == null) return null;
    if (this is int) return this as int;
    if (this is double) return (this as double).toInt();
    return int.tryParse(toString());
  }

  double? asDouble() {
    if (this == null) return null;
    if (this is double) return this as double;
    if (this is int) return (this as int).toDouble();
    return double.tryParse(toString());
  }

  bool? asBool() {
    if (this == null) return null;
    if (this is bool) return this as bool;
    if (this is String) {
      return (this as String).toLowerCase() == 'true';
    }
    return null;
  }

  List<String>? asStringList() {
    if (this == null) return null;
    if (this is List) {
      return (this as List).map((e) => e.toString()).toList();
    }
    return null;
  }
}

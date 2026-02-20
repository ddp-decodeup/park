import 'package:flutter/material.dart';

extension ContextExtensions on BuildContext {
  // screens size
  double get width => MediaQuery.of(this).size.width;
  double get height => MediaQuery.of(this).size.height;

  // padding
  EdgeInsets get padding => MediaQuery.of(this).padding;

  // theme
  ThemeData get theme => Theme.of(this);

  // navigation
  void pop<T extends Object?>([T? result]) {
    Navigator.of(this).pop(result);
  }
}

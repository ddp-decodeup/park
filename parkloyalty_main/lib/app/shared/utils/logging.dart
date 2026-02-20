import 'dart:developer';

import 'package:flutter/foundation.dart';

void logging(String object){
  if(kDebugMode){
    log(object,name: "logging");
  }
}
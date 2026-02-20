import 'package:get/get.dart';
import 'package:park_enfoecement/app/shared/utils/logging.dart';
import '../../core/models/drop_down_model.dart';

class AppController extends GetxController {
  Map<String,DropDownModel> allDropdownList = <String,DropDownModel>{};

  void setDropdowns(DropDownModel model,String key) {
    if(allDropdownList.containsKey(key)){
      allDropdownList.remove(key);
    }
    allDropdownList.addEntries([MapEntry(key,model)]);
    logging('dropdown list length :: ${allDropdownList.length}');
  }
}

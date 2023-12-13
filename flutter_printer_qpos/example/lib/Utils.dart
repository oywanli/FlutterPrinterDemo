import 'dart:typed_data';

class Utils {
  static String HEXES = "0123456789ABCDEF";

  static String? Uint8ListToHexStr(Uint8List list) {
    if (list == null) {
      return null;
    }
    var hex = StringBuffer();

    for (int i = 0; i < list.length; i++) {
      hex.write(HEXES[((list[i] & 0xF0) >> 4)]);
      hex.write(HEXES[((list[i] & 0x0F))]);
    }
    return hex.toString();
  }
}
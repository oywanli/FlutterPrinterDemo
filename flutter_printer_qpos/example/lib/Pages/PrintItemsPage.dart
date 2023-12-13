import 'dart:collection';
import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_printer_qpos/flutter_printer_qpos.dart';
import 'package:flutter_printer_qpos/QPOSPrintModel.dart';

import '../Utils.dart';



class PrintItemsPage extends StatefulWidget {
  @override
  _PrintState createState() => _PrintState();

}

class _PrintState extends State<PrintItemsPage> {
  FlutterPrinterQpos _flutterPrinterQpos = FlutterPrinterQpos();
  StreamSubscription? _subscription;
  @override
  void initState() {
    super.initState();
    _flutterPrinterQpos.initPrinter();
    _subscription =
        _flutterPrinterQpos.onPosPrintListenerCalled!.listen((QPOSPrintModel datas) {
          parasPrintListener(datas);
        });
  }

  @override
  void dispose() {
    super.dispose();
    //取消监听
    if (_subscription != null) {
      _subscription!.cancel();
    }
  }

  @override
  Widget build(BuildContext context) {
    var content;
    Widget buttonSection = Container(
      child: Column(
        children: [

          ElevatedButton(
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(250, 40),
            shape: const StadiumBorder(),
            textStyle: const TextStyle(fontSize: 18),
          ),

            onPressed: () async {
              printText();
            },
            child: Text("Print Text"),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(250, 40),
              shape: const StadiumBorder(),
              textStyle: const TextStyle(fontSize: 18),
            ),
            onPressed: () async {
              printBarcode();
            },
            child: Text("Print Barcode"),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(250, 40),
              shape: const StadiumBorder(),
              textStyle: const TextStyle(fontSize: 18),
            ),
            onPressed: () async {
              printQRcode();
            },
            child: Text("Print QRcode"),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
              minimumSize: const Size(250, 40),
              shape: const StadiumBorder(),
              textStyle: const TextStyle(fontSize: 18),
            ),
            onPressed: () async {
              printPicture();
            },
            child: Text("Print Picture"),
          )
        ],
      ),
    );
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Print page'),
          ),
          body: ListView(
            children: [
              buttonSection,
            ],
            padding: const EdgeInsets.all(5.0),
          )),
    );


  }

  void printText() {
    _flutterPrinterQpos.setAlign(PrintLine.CENTER.name);
    _flutterPrinterQpos.setFontSize(16);
    _flutterPrinterQpos.setFontStyle(FontStyle.NORMAL.name);
    _flutterPrinterQpos.setPrintStyle();

    _flutterPrinterQpos.printText("123123123");
  }

  void printBarcode() {
    _flutterPrinterQpos.printBarCode(Symbology.CODE_128.name, "400", "100", "test123", PrintLine.CENTER.name);
  }

  void printQRcode() {
    _flutterPrinterQpos.printQRCode(ErrorLevel.L.name, "300", "test123", PrintLine.CENTER.name);
  }

  Future<void> printPicture() async {
    // final ByteData bytes = await rootBundle.load('assets/images/1/image.jpg');
    _flutterPrinterQpos.setAlign(PrintLine.RIGHT.name);

    final ByteData bytes = await rootBundle.load('configs/demo.jpg');
    final bitmip = bytes.buffer.asUint8List(0);

    _flutterPrinterQpos.printBitmap(bitmip);
  }

  void parasPrintListener(QPOSPrintModel datas) {
    String? method = datas.method;
    List<String> paras = new List.empty();
    String? parameters = datas.parameters;
    if (parameters != null && parameters.length > 0) {
      paras = parameters.split("||");
    }

    if (method == "printResult") {
      print("printResult:"+parameters!);
    } else {
      print("method:"+method!);
    }
  }

}
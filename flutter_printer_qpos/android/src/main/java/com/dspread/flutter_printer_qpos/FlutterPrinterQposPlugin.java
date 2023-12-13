package com.dspread.flutter_printer_qpos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterPrinterQposPlugin */
public class FlutterPrinterQposPlugin implements FlutterPlugin, MethodCallHandler,EventChannel.StreamHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private Context mContext;
  private MethodChannel methodChannel;
  private EventChannel eventChannel;

  public FlutterPrinterQposPlugin(){

  }


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    TRACE.d("FlutterPrinterQposPlugin：onAttachedToEngine");

    this.mContext = flutterPluginBinding.getApplicationContext();
    methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_printer_qpos");
    eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_printer_qpos_event");
    eventChannel.setStreamHandler(this);
    methodChannel.setMethodCallHandler(this);
  }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    TRACE.d("onListen");
    PosPrinterPluginHandler.initEventSender(events, arguments);

  }

  @Override
  public void onCancel(Object arguments) {
    TRACE.d("onCancel");

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    TRACE.d("FlutterPrinterQposPlugin：onMethodCall" + call.method);

    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if(call.method.equals("initPrinter")){
      TRACE.d("initPrinter");
      PosPrinterPluginHandler.initPrinter(mContext);
    } else if(call.method.equals("setAlign")){
      TRACE.d("setAlign");
      String align = call.argument("align");
      PosPrinterPluginHandler.setAlign(align);
    } else if(call.method.equals("setFontStyle")){
      TRACE.d("setFontStyle");
      String bold = call.argument("bold");
      PosPrinterPluginHandler.setAlign(bold);
    } else if(call.method.equals("setFontSize")){
      TRACE.d("setFontSize");
      int fontsize = call.argument("fontsize");
      PosPrinterPluginHandler.setFontSize(fontsize);
    } else if(call.method.equals("setPrintStyle")){
      TRACE.d("setPrintStyle");
      PosPrinterPluginHandler.setPrintStyle();
    } else if(call.method.equals("setPrintDensity")){
      TRACE.d("setPrintDensity");
      int printDensityLevel = call.argument("printDensityLevel");
      PosPrinterPluginHandler.setPrintDensity(printDensityLevel);
    } else if(call.method.equals("printText")){
      TRACE.d("printText");
      String text = call.argument("text");
      PosPrinterPluginHandler.printText(text);
    } else if(call.method.equals("printBarCode")) {
      TRACE.d("printBarCode");
      String symbology = call.argument("symbology");
      int width = Integer.parseInt(call.argument("width"));
      int height = Integer.parseInt(call.argument("height"));
      String content = call.argument("content");
      String position = call.argument("position");
      PosPrinterPluginHandler.printBarCode(symbology, width, height, content, position);
    } else if(call.method.equals("printQRCode")) {
      TRACE.d("printQRCode");
      String errorLevel = call.argument("errorLevel");
      int width = Integer.parseInt(call.argument("width"));
      String content = call.argument("content");
      String position = call.argument("position");
      PosPrinterPluginHandler.printQRCode(errorLevel, width, content, position);
    } else if(call.method.equals("printBitmap")){
      TRACE.d("printBitmap");
      byte[] bitmapByte= call.argument("bitmap");
      Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
      PosPrinterPluginHandler.printBitmap(bitmap);
    }
    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    TRACE.d("FlutterPrinterQposPlugin：onDetachedFromEngine");
    mContext = null;
    PosPrinterPluginHandler.initPrinter(null);
    methodChannel.setMethodCallHandler(null);
    methodChannel = null;
    eventChannel.setStreamHandler(null);
    eventChannel = null;
  }
}

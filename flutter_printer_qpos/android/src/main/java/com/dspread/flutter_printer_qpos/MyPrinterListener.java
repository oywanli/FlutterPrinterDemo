package com.dspread.flutter_printer_qpos;

import android.os.Handler;
import android.os.Looper;

import com.dspread.print.device.PrintListener;
import com.dspread.print.device.PrinterDevice;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyPrinterListener implements PrintListener {
    @Override
    public void printResult(boolean isSuccess, String status, PrinterDevice.ResultType resultType) {
        TRACE.d("printResult():" + isSuccess + " -" + status + " -" + resultType.getValue());
        Map<String, String> map = new HashMap<String, String>();
        map.put("method", "printResult");
        StringBuffer parameters = new StringBuffer();
        if (status == null) {
            status = "";
        }
        parameters.append(String.valueOf(isSuccess).concat("||").concat(status).concat("||").concat(String.valueOf(resultType.getValue())));
        map.put("parameters", parameters.toString());
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                TRACE.d("current thread name:" + Thread.currentThread().getName());
                PosPrinterPluginHandler.mEvents.success(JSONObject.toJSONString(map));

            }
        });

    }
}

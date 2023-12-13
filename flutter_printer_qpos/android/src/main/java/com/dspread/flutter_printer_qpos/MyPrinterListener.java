package com.dspread.flutter_printer_qpos;

import android.os.Handler;
import android.os.Looper;

import com.dspread.print.device.PrintListener;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MyPrinterListener implements PrintListener {
    @Override
    public void printResult(boolean b, String status, int type) {
        TRACE.d("printResult():"+b+" "+status+" "+type);
        Map<String,String> map = new HashMap<String,String>();
        map.put("method","printResult");
        StringBuffer parameters = new StringBuffer();
        if(status == null) {
            status = "";
        }
        parameters.append(String.valueOf(b).concat("||").concat(status).concat("||").concat(String.valueOf(type)));
        map.put("parameters",parameters.toString());
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                TRACE.d("current thread name:"+Thread.currentThread().getName());
                PosPrinterPluginHandler.mEvents.success(JSONObject.toJSONString(map));

            }
        });

    }
}

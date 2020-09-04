/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wwc2.ttxassist.utils;

import android.content.Context;
import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
    private static Boolean LOG_SWITCH = true; // 日志文件总开关
    private static Boolean LOG_TO_FILE = true; // 日志写入文件开关
    private static String LOG_TAG = "ttxassist"; // 默认的tag
    private static char LOG_TYPE = 'v';// 输入日志类型，v代表输出所有信息,w则只输出警告...
    private static int FILE_MAX_SIZE = 1024 * 1024 * 10;//文件最大大小 10M
    private final static SimpleDateFormat LOG_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static String LOG_FILE_PATH; // 日志文件保存路径
    private static String LOG_FILE_NAME;// 日志文件保存名称
    public String path = "storage/emulated/0/debug.h264";

    public static void init(Context context) {
        LOG_FILE_PATH = "storage/emulated/0/";
        LOG_FILE_NAME = "debug.h264";
    }


    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    public synchronized static void log2FileOnly(String text) {

        if(LOG_FILE_PATH == null || LOG_FILE_PATH.equals("") || LOG_FILE_PATH.contains("null")){
            return;
        }
        try {
            File destDir = new File(LOG_FILE_PATH);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            File file = new File(LOG_FILE_PATH, LOG_FILE_NAME);
            if(!file.exists()) {
                file.createNewFile();
            }

            long size = file.length();
            //大于10m直接删除
            if (size > FILE_MAX_SIZE) {
                file.delete();
            }
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(text);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (Exception e) {
            Log.d("log2File...e=",  e.toString());
//            .e=java.io.FileNotFoundException: /data/user/0/com.wwc2.networks/files/LOGS.txt (Too many open files)
        }
    }

    public synchronized static void log2FileOnlyhex(byte data[],int size) {

        if(LOG_FILE_PATH == null || LOG_FILE_PATH.equals("") || LOG_FILE_PATH.contains("null")){
            return;
        }
        try {
            File destDir = new File(LOG_FILE_PATH);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            File file = new File(LOG_FILE_PATH, LOG_FILE_NAME);
            if(!file.exists()) {
                file.createNewFile();
            }

//            FileWriter filerWriter = new FileWriter(file, true);
//            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            DataOutputStream mDataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
                    file,true)));

            mDataOut.write(data,0,size);
            mDataOut.close();

        } catch (Exception e) {
            Log.d("log2File...e=",  e.toString());
//            .e=java.io.FileNotFoundException: /data/user/0/com.wwc2.networks/files/LOGS.txt (Too many open files)
        }
    }


}

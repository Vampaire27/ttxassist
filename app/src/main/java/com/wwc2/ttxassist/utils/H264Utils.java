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

public class H264Utils {


    private static String LOG_FILE_PATH ="/storage/emulated/0/";
    private static String LOG_FILE_NAME ="debug.h264";// 日志文件保存名称

    public static void init(Context context) {

    }

    public static void setLogToFilName(String file){
          LOG_FILE_NAME = file;
    }

    public static String getLogFilePath(){
        return LOG_FILE_PATH;
    }

    public static String getLogFileName(){
        return LOG_FILE_NAME;
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

            DataOutputStream mDataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
                    file,true)));

            mDataOut.write(data,0,size);
            mDataOut.close();

        } catch (Exception e) {
            Log.d("log2File...e=",  e.toString());
        }
    }


}

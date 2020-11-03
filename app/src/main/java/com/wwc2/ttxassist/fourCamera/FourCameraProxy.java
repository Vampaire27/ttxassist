package com.wwc2.ttxassist.fourCamera;


import com.wwc2.ttxassist.utils.FileUtils;

public class FourCameraProxy {

    final static String CAMERA_ACTION_NODE = "/sys/devices/platform/wwc2_camera_combine/camera_action";

    final static String displayModeOb = "/sys/devices/platform/wwc2_camera_combine/display_mode";

    final static String record_latency = "/sys/devices/platform/wwc2_camera_combine/record_latency";

    final static String water_mask = "/sys/devices/platform/wwc2_camera_combine/water_mask";

    final static String audio_enable = "/sys/devices/platform/wwc2_camera_combine/audio_enable";

//    final static String CAMERA_PLATFORM_TYPE = "ro.wwc2camera.platformtype";
    final static String CAMERA_PLATFORM_TYPE = "persist.wwc2camera.platformtype";//需要DVR设置
    final static String DVR_LOCATION = "persist.wwc2dvr.location";//存储位置

//    write channel_id mode param > IC_PARAM
//
//    channel_id:摄像头通道号，取值 0~3
//
//    mode:模式, 取值 0~3, 0:对比度，1:亮度，2:饱和度，3:色调
//
//    param:对应模式数值，取值0~255

//    获取默认值 read IC_PARAM
//     read的数据格式类似下面数值
//
//        128 128 144 128
//        128 128 144 128
//        128 128 144 128
//        128 128 144 128
//
//        0~3行对应到通道号，数值分别表示 对比度、亮度、饱和度、色调数值
    final static String display_ic_param = "/sys/devices/platform/wwc2_camera_combine/ic_param";

    final static String CAPTURE_FILE = "/sys/devices/platform/wwc2_camera_combine/capture_file";

    final static String CAR_NUMBER_DATA = "/sys/devices/platform/wwc2_camera_combine/card_data";

    //打开Camera前设置一次全部参数
    final static String CAMERA_PARAM  = "/sys/devices/platform/wwc2_camera_combine/camera_param";


   private static class InstanceHolder{
       private static FourCameraProxy mFourCameraProxy = new FourCameraProxy();
       public static FourCameraProxy getFourCameraProxy(){
            return mFourCameraProxy;
       };

   }

     public static FourCameraProxy getInstance(){
           return InstanceHolder.getFourCameraProxy();
     }

    private static void setPhotoLocation(int deviceId) {
        CameraBean bean = new CameraBean(CAMERA_ACTION_NODE, Config.WWC2_CAPTURE_DIR, deviceId);
        bean.Action();
    }


    public static String TakePhoto(int channel) {
        int PHNOE_Location = 1; // phone storage .
        setPhotoLocation(PHNOE_Location);
        CameraBean bean = new CameraBean(CAMERA_ACTION_NODE, Config.MODE_WWC2_CAPTURE,channel);
        bean.Action();
        return FileUtils.readTextFile(CAPTURE_FILE);
    }


}

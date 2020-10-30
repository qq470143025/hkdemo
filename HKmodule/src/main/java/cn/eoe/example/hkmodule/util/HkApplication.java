package cn.eoe.example.hkmodule.util;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;

/**
 * Created by jiang at 2020/10/14
 */
   public  class HkApplication extends Application {
    String TAG=this.getClass().getSimpleName();
       @Override
       public void onCreate() {
         super.onCreate();
           initSDK();

      }

       @Override
       public void onTerminate() {
         super.onTerminate();
          UnInitSDK();
        }

    /**
     * 初始化HCNet SDK
     *
     *  测试使用
     *
     * @return
     */
    private   boolean initSDK() {

        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK ---------初始化失败!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_GetSDKVersion();
        Log.e(TAG, "HCNetSDK版本"+HCNetSDK.getInstance().NET_DVR_GetSDKVersion());
        Log.e(TAG, "HCNetSDKBuildVersion版本"+HCNetSDK.getInstance().NET_DVR_GetSDKBuildVersion());
        return true;
    }
    /**
     * 关闭HCNet SDK
     *
     * @return
     */
    private  boolean UnInitSDK() {

        if (!HCNetSDK.getInstance().NET_DVR_Cleanup()) {
            //Log.e(TAG, "HCNetSDK ---------退出失败!");
            return false;
        }
        return true;
    }
}

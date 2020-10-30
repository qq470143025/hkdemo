package cn.eoe.example.hkmodule.util;


import android.util.Log;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;

/**
 * Created by jiang at 2020/10/14
 */
public  class Hk_Login {
    private String TAG=this.getClass().getSimpleName();

    static Hk_Login hk_Loging = null;
    //唯一标识的登录id
    private  int logId = -1;
    //摄像头的起始通道
    private  int m_iStartChan = 0;
    //摄像头通道数（监控主机）
    int m_iChanNum;
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

    public Hk_Login() {
    }

    public boolean login(String ipAddress, int portNum, String userName, String passWord) {
        try {
            if (logId < 0) {
                // 登录设备
                logId = loginDevice(ipAddress, portNum, userName, passWord);
                if (logId < 0) {
                    Log.e(TAG, "设备登录失败！");
                    return false;
                }
                // 获取异常回调和异常设置的回调
                ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                if (oexceptionCbf == null) {
                    Log.e(TAG, "异常回调对象失败！");
                    return false;
                }

                if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
                    Log.e(TAG, "注册接收异常、重连消息回调函数失败 !");
                    return false;
                }

//                loginButton.setText("注销");
                Log.i(TAG, "登录成功 ！");
                Log.i(TAG, "logId ！"+logId);
                return true;
            } else {
                // 是否登出
                if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(logId)) {
                    Log.e(TAG, " 用户注销失败!");
                    return false;
                }
//                loginButton.setText("登录");
                logId = -1;
                return true;
            }
        } catch (Exception err) {
            Log.e(TAG, "错误: " + err.toString());
            return false;
        }
    }

    private  int loginDevice(String ipAddress, int portNum, String userName, String passWord) {
        //实例化设备信息对象
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            Log.e(TAG, "实例化设备信息(NET_DVR_DEVICEINFO_V30)失败!");
            return -1;
        }
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(ipAddress, portNum, userName, passWord, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "网络设备登录失败!-------------Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {//byChanNum 设备模拟通道个数，//说明是摄像头登录摄像头
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;//byStartChan 模拟通道的起始通道号
            Log.i(TAG, "设备模拟通道个数:"+m_oNetDvrDeviceInfoV30.byChanNum );
            Log.i(TAG, "模拟通道的起始通道号:"+m_iStartChan);
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {//说明是登录的是监控主机  //byIPChanNum 设备最大数字通道个数 //说明是数字摄像头
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;//byStartDChan 起始数字通道号，0表示无数字通道，比如DVR或IPC
             m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
            Log.d(TAG, "数字通道个数 :"+m_oNetDvrDeviceInfoV30.byIPChanNum );
            Log.d(TAG, "数字通道个数m_iChanNum :"+m_iChanNum );
            Log.d(TAG, "起始数字通道号:"+m_iStartChan);
            Log.d(TAG, "硬盘数量:"+m_oNetDvrDeviceInfoV30.byDiskNum);
            Log.d(TAG, "设备类型:"+m_oNetDvrDeviceInfoV30.byDVRType);
            Log.d(TAG, "设备模拟通道个数:"+(m_oNetDvrDeviceInfoV30.byChanNum+m_oNetDvrDeviceInfoV30.byHighDChanNum * 256));
        }
        Log.i(TAG, "网络设备登录成功!");

        return iLogID;
    }

    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception------------------------------, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

    public boolean LoginOut(){
        // 是否登出
        if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(logId)) {
            Log.e(TAG, " 用户注销失败!");
            return false;
        }
        logId=-1;
        m_iStartChan=0;
        return true;
    }

    public int getLogId() {
        return logId;
    }

    public int getM_iStartChan() {
        return m_iStartChan;
    }

    public int getM_iChanNum() {
        return m_iChanNum;
    }

    public static synchronized Hk_Login getInstance() {
        if (hk_Loging == null) {
            hk_Loging = new Hk_Login();
        }
        return hk_Loging;
    }

}

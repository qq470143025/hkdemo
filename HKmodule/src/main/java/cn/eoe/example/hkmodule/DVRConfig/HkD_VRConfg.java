package cn.eoe.example.hkmodule.DVRConfig;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_ACTIVATECFG;
import com.hikvision.netsdk.NET_DVR_DEVICECFG;
import com.hikvision.netsdk.NET_DVR_DIGITAL_CHANNEL_STATE;
import com.hikvision.netsdk.NET_DVR_NETCFG_V30;

import cn.eoe.example.hkmodule.util.Hk_Login;

/**
 * Created by jiang at 2020/10/17
 */
public class HkD_VRConfg {
    private String TAG = this.getClass().getSimpleName();
    private int logId;

    public HkD_VRConfg() {
        logId= Hk_Login.getInstance().getLogId();
    }

    /**
     * 数字信道状态
     */
    public static void DigitalChannelState(int iUserID)
    {
        NET_DVR_DIGITAL_CHANNEL_STATE struChanState = new NET_DVR_DIGITAL_CHANNEL_STATE();
        if(!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDK.getInstance().NET_DVR_GET_DIGITAL_CHANNEL_STATE, 0, struChanState))
        {
            System.out.println("NET_DVR_GET_DIGITAL_CHANNEL_STATE faild!" + " err: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        else
        {
            System.out.println("NET_DVR_GET_DIGITAL_CHANNEL_STATE succ!");
            System.out.println("analog channel 1 and 2:" + (int)struChanState.byAnalogChanState[0] + "-" + (int)struChanState.byAnalogChanState[1] +
                    ",digital channel 1 and 2:" + (int)struChanState.byDigitalChanState[0] + "-" + (int)struChanState.byDigitalChanState[1]);
        }
    }

    public static void DeviceCfg(int iUserID)
    {
        NET_DVR_DEVICECFG struDeviceCfg = new NET_DVR_DEVICECFG();
        if(!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_DEVICECFG, 0, struDeviceCfg))
        {
            System.out.println("NET_DVR_GET_DEVICECFG faild!" + " err: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        else
        {
            System.out.println("NET_DVR_GET_DEVICECFG succ!" );
            System.out.println(new String(struDeviceCfg.sDVRName));
            System.out.println("NET_DVR_GET_DEVICECFG succ!"+  struDeviceCfg.byDVRType);
           // Log.d(TAG,struDeviceCfg.sDVRName.toString());
            //sDVRName 设备名称
           // Log.e(TAG,struDeviceCfg.sDVRName.toString());

        }
    }
    /**
     * 监控主机网络设置
     */
    public static void NetCfg(int iUserID)
    {
        NET_DVR_NETCFG_V30 NetCfg = new NET_DVR_NETCFG_V30();
        if (!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(iUserID,HCNetSDK.NET_DVR_GET_NETCFG_V30, 0, NetCfg))
        {
            System.out.println("get net cfg faied!"+ " err: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }else
        {
            System.out.println("get net cfg succ!");
            System.out.println("alarm host ip: " + new String(NetCfg.struAlarmHostIpAddr.sIpV4));
            System.out.println("Etherner host ip: " + new String(NetCfg.struEtherNet[0].struDVRIP.sIpV4));
            System.out.println("Etherner mask: " + new String(NetCfg.struEtherNet[0].struDVRIPMask.sIpV4));
        }
    }
    /**
     * 重启监控主机
     */
    public void RebootDVR(){
        if(HCNetSDK.getInstance().NET_DVR_RebootDVR(logId)){
            //重启命令下达成功
        }

    }
    /**
     * 监控主机关机
     */
    public void ShutDownDVR(){
        if(HCNetSDK.getInstance().NET_DVR_ShutDownDVR(logId)){
            //重启命令下达成功
        }

    }
    /**
     *
     * 远程格式化设备硬盘。
     */
    public static void Test_FormatDisk(int iUserID)
    {
        int lFormatHandle = HCNetSDK.getInstance().NET_DVR_FormatDisk(iUserID, 0);
        if(lFormatHandle >= 0)
        {
            INT_PTR ptrCurrentDisk = new INT_PTR();
            INT_PTR ptrCurrentPos = new INT_PTR();
            INT_PTR ptrFormatStatic = new INT_PTR();
            ptrCurrentDisk.iValue = 0;
            ptrCurrentPos.iValue = 0;
            while(ptrFormatStatic.iValue == 0)
            {
                if(!HCNetSDK.getInstance().NET_DVR_GetFormatProgress(lFormatHandle, ptrCurrentDisk, ptrCurrentPos, ptrFormatStatic))
                {
                    System.out.println("NET_DVR_GetFormatProgress failed with:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    break;
                }
                else
                {
                    System.out.println("NET_DVR_GetFormatProgress succ Disk:" + ptrCurrentDisk.iValue + " Pos:" + ptrCurrentPos.iValue + " Static:" + ptrFormatStatic.iValue);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            System.out.println("Get progress over Disk:" + ptrCurrentDisk.iValue + " Pos:" + ptrCurrentPos.iValue + " Static:" + ptrFormatStatic.iValue);

            if(!HCNetSDK.getInstance().NET_DVR_CloseFormatHandle(lFormatHandle))
            {
                System.out.println("NET_DVR_CloseFormatHandle failed with:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            }
        }
        else
        {
            System.out.println("NET_DVR_FormatDisk failed with:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
    }

    /**
     * 激活设备
     *
     */
    public static void ActivateDevice()
    {
        NET_DVR_ACTIVATECFG activateCfg = new NET_DVR_ACTIVATECFG();
        System.arraycopy("Abcd1234".getBytes(), 0, activateCfg.sPassword, 0, "Abcd1234".getBytes().length);
        if(!HCNetSDK.getInstance().NET_DVR_ActivateDevice("10.10.35.16", 8000, activateCfg))
        {
            System.out.println("NET_DVR_ActivateDevice failed:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        else
        {
            System.out.println("NET_DVR_ActivateDevice succ");
        }
    }
}

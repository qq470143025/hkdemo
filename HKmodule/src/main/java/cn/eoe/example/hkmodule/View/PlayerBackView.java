package cn.eoe.example.hkmodule.View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PLAYBACK_INFO;
import com.hikvision.netsdk.NET_DVR_TIME;
import com.hikvision.netsdk.NET_DVR_VOD_PARA;
import com.hikvision.netsdk.PlaybackControlCommand;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import cn.eoe.example.hkmodule.util.Hk_Login;
import cn.eoe.example.hkmodule.util.PlayerBackTime;

/**
 * Created by jiang at 2020/10/16
 *  监控回放，仅支持单个通道
 */
public class PlayerBackView extends SurfaceView implements SurfaceHolder.Callback {
    private String TAG = this.getClass().getSimpleName();
    private int m_iLogID = -1; // return by NET_DVR_Login_v30
    private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
    private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime

    private int m_iStartChan = 0; // start channel no

    private  int m_iPort = -1;
    private  int playId = -1;
    private  NET_DVR_TIME timeStart =null;
    private  NET_DVR_TIME timeStop = null;
    private boolean bCreate = false;
    public PlayerBackView(Context context) {
        this(context,null);
    }

    public PlayerBackView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PlayerBackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }
    public void  SetPlayPlayerBackTime(PlayerBackTime BackTime,int m_iStartChan){
        timeStart= BackTime.getTimeStart();
        timeStop=BackTime.getTimeStop();
        m_iLogID= Hk_Login.getInstance().getLogId();
        this.m_iStartChan= m_iStartChan;
    }
    public void playOrStopSback() {
        try {
            if (m_iLogID < 0) {
                Log.e(TAG, "please login on a device first");
                return;
            }
            if (m_iPlaybackID < 0) {
                if (m_iPlayID >= 0) {
                    // m_iPlayID 说明已经启动了（preview）预览模式
                    Log.i(TAG, "Please stop preview first");
                    return;
                }
                if (timeStart==null&&timeStop==null){
                    Log.i(TAG, "Please set timeStart and timeStop");
                    return;
                }

                Log.i(TAG, "m_iStartChan:" + m_iStartChan);
                while (!bCreate) {
                    try {
                        Thread.sleep(100);
                        Log.i(TAG, "wait for surface create");
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                NET_DVR_VOD_PARA vodParma = new NET_DVR_VOD_PARA();
                vodParma.struBeginTime = timeStart;
                vodParma.struEndTime = timeStop;
                vodParma.byStreamType = 0;
                vodParma.struIDInfo.dwChannel = m_iStartChan;
                vodParma.hWnd =this.getHolder().getSurface();

                m_iPlaybackID = HCNetSDK.getInstance().NET_DVR_PlayBackByTime_V40(m_iLogID, vodParma);

                if (m_iPlaybackID >= 0){
                    NET_DVR_PLAYBACK_INFO struPlaybackInfo = null;
                    if (!HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(m_iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYSTART, null, 0, null))
                    {
                        Log.e(TAG, "net sdk playback start failed!");
                        return;
                    }

                    Log.e(TAG, "is Stop");
                    Thread thread = new Thread()
                    {
                        public void run()
                        {
                            int nProgress = -1;
                            while (true) {
                                nProgress = HCNetSDK.getInstance().NET_DVR_GetPlayBackPos(m_iPlaybackID);
                                System.out.println("NET_DVR_GetPlayBackPos:" + nProgress);
                                if (nProgress < 0 || nProgress >= 100) {
                                    break;
                                }

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    thread.start();
                } else {
                    Log.i(TAG,"NET_DVR_PlayBackByTime failed, error code: "+ HCNetSDK.getInstance().NET_DVR_GetLastError());
                }
            }
            else {

                if (!HCNetSDK.getInstance().NET_DVR_StopPlayBack(m_iPlaybackID)) {
                    Log.e(TAG, "net sdk stop playback failed");
                }
                Log.e(TAG, "is can Playback");
                m_iPlaybackID = -1;
            }
        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        bCreate = true;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

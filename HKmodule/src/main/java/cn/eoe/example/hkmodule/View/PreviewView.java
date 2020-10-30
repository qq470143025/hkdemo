package cn.eoe.example.hkmodule.View;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;

import org.MediaPlayer.PlayM4.Player;

import cn.eoe.example.hkmodule.util.Hk_Login;

/**
 * Created by jiang at 2020/10/21
 */
public  class PreviewView extends SurfaceView {
    private String TAG=this.getClass().getSimpleName();
    private static final int HIK_SUB_STREAM_CODE = 1;
    private  int m_iPort = -1;
    private  int playId = -1;
    private SurfaceView mSurfaceView;
    //唯一标识的登录id
    private  int logId =-1;
    //摄像头的起始通道
    private   int m_iStartChan = 0;
    //摄像头通道数（监控主机）
    int m_iChanNum=0;
    private  int m_iPlaybackID = -1;
    private boolean bCreate = false;
    public PreviewView(Context context) {
        this(context,null);
    }

    public PreviewView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(this);
    }

    private void initView(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                bCreate = true;
                Log.i(TAG, "surface is created" + m_iPort);
                if (-1 == m_iPort) {
                    return;
                }
                Surface surface = holder.getSurface();
                if (surface.isValid()) {
                    if (!Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                        Log.e(TAG, "播放器设置或销毁显示区域失败!");
                    }
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
                if (-1 == m_iPort) {
                    return;
                }
                if (holder.getSurface().isValid()) {
                    if (!Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                        Log.e(TAG, "播放器设置或销毁显示区域失败!");
                    }
                }
            }
        });
    }
    public void startPreview(int iChan) {
        setadta(iChan);
        playOrStopStream();
    }

    public void stopPreview() {
        playOrStopStream();
    }

    /**
     * 主动设置
     * 要预览的通道
     */
    private void setadta(int m_iStartChan){
        this.logId = Hk_Login.getInstance().getLogId();
        this.m_iStartChan = m_iStartChan;
    }
    /**
     * 播放或者停止播放视频流
     */
    private  void playOrStopStream() {

        Log.i(TAG, "logId ！"+logId);
        if (logId < 0) {
            Log.e(TAG, "请先登录设备");
            return;
        }
        if (playId < 0) {   //播放
            RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
            if (fRealDataCallBack == null) {
                Log.e(TAG, "fRealDataCallBack object is failed!");
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


            NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
            previewInfo.lChannel = m_iStartChan;
            previewInfo.dwStreamType = HIK_SUB_STREAM_CODE;                                                             //子码流
            previewInfo.bBlocked = 1;
            // HCNetSDK start preview
            playId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(logId, previewInfo, fRealDataCallBack);
            if (playId < 0) {
                Log.e(TAG, "实时预览失败!-----------------Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                return;
            }

            Log.i(TAG, "NetSdk 播放成功 ！");
//            mPlayButton.setText("停止");
        } else {    //停止播放
            if (playId < 0) {
                Log.e(TAG, "m_iPlayID < 0");
                return;
            }

            //  net sdk stop preview
            if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(playId)) {
                Log.e(TAG, "停止预览失败!----------------错误:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                return;
            }

            playId = -1;
            Player.getInstance().stopSound();
            // player stop play
            if (!Player.getInstance().stop(m_iPort)) {
                Log.e(TAG, "-------------------暂停失败!");
                return;
            }

            if (!Player.getInstance().closeStream(m_iPort)) {
                Log.e(TAG, "-------------------关流失败!");
                return;
            }
            if (!Player.getInstance().freePort(m_iPort)) {
                Log.e(TAG, "-------------------释放播放端口失败!" + m_iPort);
                return;
            }
            m_iPort = -1;
           // logId = -1;
            playId = -1;
//            mPlayButton.setText("播放");
        }

    }

    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                // 播放接口回调，会不断循环调用
                processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }
    private   void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        // 初始化 Player
        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            if (m_iPort >= 0) {
                return;
            }
            // 获取 Player 通信端口
            m_iPort = Player.getInstance().getPort();
            if (m_iPort == -1) {
                Log.e(TAG, "获取端口失败！: " + Player.getInstance().getLastError(m_iPort));
                return;
            }
            Log.i(TAG, "获取端口成功！: " + m_iPort);
            if (iDataSize > 0) {
                // 设置流播放模式
                if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
                {
                    Log.e(TAG, "设置流播放模式失败！");
                    return;
                }
                if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) //open stream
                {
                    Log.e(TAG, "打开流失败！");
                    return;
                }
                // 设置播放器显示窗口（窗口句柄）
                if (!Player.getInstance().play(m_iPort, mSurfaceView.getHolder())) {
                    Log.e(TAG, "播放失败！");
                    return;
                }
                //设置声音播放端口
                if (!Player.getInstance().playSound(m_iPort)) {
                    Log.e(TAG, "以独占方式播放音频失败！失败码 :" + Player.getInstance().getLastError(m_iPort));
                    return;
                }
            }
        }
        else {
            // 向Player中输入视频流数据
            if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
//		    		Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
                //主要用于缓冲，当传输速率不够时进行数据缓冲，缓冲4000次，或者缓冲成功为止
                for (int i = 0; i < 4000 && m_iPlaybackID >= 0; i++) {
                    if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
                        Log.e(TAG, "输入流数据失败: " + Player.getInstance().getLastError(m_iPort));
                    else
                        break;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                }
            }

        }

    }

}

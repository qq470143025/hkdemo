package cn.eoe.example.hkmodule;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import cn.eoe.example.hkmodule.View.PlayerBackView;
import cn.eoe.example.hkmodule.View.PreviewView;
import cn.eoe.example.hkmodule.util.Hk_Login;
import cn.eoe.example.hkmodule.util.PlayerBackTime;

public class HKmodule_mainactivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();
    private static final String IP_ADDRESS = "";
    private static final int PORT = 8000;
    private static final String USER_NAME = "";
    private static final String PASSWORD = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hkmodule_activity_h_kmodule_mainactivity);
        previewView =findViewById(R.id.hkmodule_camera);
        playerBackView=findViewById(R.id.hkmodule_surfaceview1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean loginState = Hk_Login.getInstance().login(IP_ADDRESS, PORT, USER_NAME, PASSWORD);
                if (loginState) {
                    Log.d("MainActivity", "登录成功");
                    play();
                    playBack();
                }
            }
        }).start();
    }

    PreviewView previewView;
    PlayerBackView playerBackView;
    @Override
    protected void onStart() {
        super.onStart();
    }

    private void play(){
        previewView.startPreview(Hk_Login.getInstance().getM_iStartChan());
    }

    private void playBack(){
        PlayerBackTime Time=new PlayerBackTime();
        Time.setTimeStart(2020,10,23);
        Time.setTimeStop(2020,10,24);
        playerBackView.SetPlayPlayerBackTime(Time,Hk_Login.getInstance().getM_iStartChan());
        playerBackView.playOrStopSback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewView.stopPreview();
        playerBackView.playOrStopSback();
        Hk_Login.getInstance().LoginOut();
    }
}
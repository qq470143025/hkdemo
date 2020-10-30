package cn.eoe.example.hkdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private static final String IP_ADDRESS = "10.44.87.180";

    private static final int PORT = 8000;
    private static final String USER_NAME = "admin";
    private static final String PASSWORD = "a12345678";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean loginState = Hk_Login.getInstance().login(IP_ADDRESS, PORT, USER_NAME, PASSWORD);
                if (loginState) {
                    Log.d("MainActivity", "登录成功");
                    play();
                      playBack();
                }
            }
        }).start();*/
    }

    private void play(){

    }

    private void playBack(){
        //playerBack.playOrStopSback();

    }
}
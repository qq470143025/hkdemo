package cn.eoe.example.hkmodule;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import cn.eoe.example.hkmodule.View.PreviewView;
import cn.eoe.example.hkmodule.util.Hk_Login;

/**
 * 4路画面预览
 *
 */

public class PreviewModule_FourPrevieActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private static final String IP_ADDRESS = "10.44.87.180";

    private static final int PORT = 8000;
    private static final String USER_NAME = "admin";
    private static final String PASSWORD = "a12345678";

    PreviewView previewmoduleFourPrevieView1,previewmoduleFourPrevieView2,previewmoduleFourPrevieView3,previewmoduleFourPrevieView4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_module__four_previe);
        previewmoduleFourPrevieView1=findViewById(R.id.previewmodule_FourPrevieView1);
        previewmoduleFourPrevieView2=findViewById(R.id.previewmodule_FourPrevieView2);
        previewmoduleFourPrevieView3=findViewById(R.id.previewmodule_FourPrevieView3);
        previewmoduleFourPrevieView4=findViewById(R.id.previewmodule_FourPrevieView4);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean loginState = Hk_Login.getInstance().login(IP_ADDRESS, PORT, USER_NAME, PASSWORD);
                if (loginState) {
                    Log.d("MainActivity", "登录成功");
                    play();
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    private void play(){
        previewmoduleFourPrevieView1.startPreview( Hk_Login.getInstance().getM_iStartChan());
        previewmoduleFourPrevieView2.startPreview( Hk_Login.getInstance().getM_iStartChan()+1);
        previewmoduleFourPrevieView3.startPreview( Hk_Login.getInstance().getM_iStartChan()+2);
        previewmoduleFourPrevieView4.startPreview( Hk_Login.getInstance().getM_iStartChan()+4);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewmoduleFourPrevieView1.stopPreview();
        previewmoduleFourPrevieView2.stopPreview();
        previewmoduleFourPrevieView3.stopPreview();
        previewmoduleFourPrevieView4.stopPreview();
        Hk_Login.getInstance().LoginOut();
    }

}

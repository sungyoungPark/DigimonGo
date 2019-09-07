package example.asus.digimongo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {
    private Handler handler;
    private int i;
    private ImageView imageView;
    private Context context;
    private int backgroundframe[] = {R.drawable.bg1,R.drawable.bg1,R.drawable.bg2, R.drawable.bg2, R.drawable.bg3};
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        i=0;
        context = this.getApplicationContext();
        imageView = findViewById(R.id.bgbg);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                imageView.setImageDrawable(getResources().getDrawable(backgroundframe[i], null));
                i++;

            }
        };
        nextImage();
    }
    public void nextImage(){
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int j=0;j<5;j++){
                    try {

                        handler.sendMessage(handler.obtainMessage());
                        Log.d("splash","time"+j);
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        Log.d("splash", "Error");
                        e.printStackTrace();
                    }
                }
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });
        myThread.start();

    }
}

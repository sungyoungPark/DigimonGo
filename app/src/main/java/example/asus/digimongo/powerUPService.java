package example.asus.digimongo;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class powerUPService extends IntentService {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Intent  sendIntent = new Intent("ServiceFinish");

    public powerUPService() {
        super("powerUpService");
        Log.d("4449","서비스 실행");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        for (int i = 0; i < 300; i++) {  //파워업 아이템은 5분간 유지 원래는=300
        try{
            Thread.sleep(1000);
            } catch (InterruptedException e) {
            e.printStackTrace();
        }
        }
    }

    @Override
    public void onDestroy() {
        sendIntent.putExtra("finish",0);
        sendBroadcast(sendIntent);
        super.onDestroy();
        Log.d("4449","onDestroy()");
    }
}

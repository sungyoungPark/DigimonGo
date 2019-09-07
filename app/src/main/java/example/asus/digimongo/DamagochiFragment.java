package example.asus.digimongo;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public class DamagochiFragment extends Fragment {

    private ImageView mImage;

    private enum State {LV1, LV2, LV3, LV4}

    ;
    private float x;
    private float y;
    private int i;
    private int mod;
    private int exp;
    private int hungryPoint;
    private int level;
    private int position;
    private FrameLayout frameLayout;
    private Handler handler;
    private int frameWidth;
    private int frameHeight;
    private int imageSize;
    private Timer timer = new Timer();
    private boolean moveflag = true;
    private List<ImageView> digimon;
    private Button menuBtn, btnLeft, btnSelect, btnRight;
    private Drawable frame0, frame1, frame2;
    Context context = getActivity();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private int curimage;
    private int[] imagesArr = new int[]{R.drawable.egg1_1, R.drawable.egg1_2, R.drawable.egg1_3};
    BroadcastReceiver mReceiver;
    BroadcastReceiver battleReceiver;
    int cnt;
    Intent battleIntent;  //배틀 인텐트, 배틀시 매인으로 메세지 보냄

    LinearLayout Battle_layOut;
    EditText BattleID;
    Button conbtn;

    MainActivity mainActivity;

    public DamagochiFragment() {
        // Required empty public constructor
    }

    public void selectDigimon() {
        //frame0 = new ImageView(this).setImageDrawable(getResources().getDrawable(R.drawable.egg1_3));
    }


    //saveInfo
    private void saveInfo() {
        editor.putInt(getString(R.string.level), level);
        editor.putInt(getString(R.string.exp), exp);
        editor.commit();
    }

    public void loadInfo() {   //파이어베이스에서 레벨과 경험치를 갖고와서 level과 exp에 저장
        int defaultInt = 0;
        // long ilevel = pref.getInt(getString(R.string.level),defaultInt);
        int ilevel = pref.getInt("LV", defaultInt);
        level = ilevel;
        //long exp = pref.getInt(getString(R.string.exp), defaultInt);
        int iexp = pref.getInt("EXP", defaultInt);
        exp = iexp;
    }

    public static DamagochiFragment newInstance() {
        return new DamagochiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mainActivity=(MainActivity)MainActivity.mContext;
        final ViewGroup damagochiView = (ViewGroup) inflater.inflate(R.layout.fragment_damagochi, container, false);
        mImage = damagochiView.findViewById(R.id.mImage);
        frameLayout = damagochiView.findViewById(R.id.framelayout);
        btnLeft = damagochiView.findViewById(R.id.btnLeft);
        btnRight = damagochiView.findViewById(R.id.btnRight);
        btnSelect = damagochiView.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evolution();
            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (level > 0) level--;
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (level < 5) level++;
            }
        });

        position = 0;
        frameWidth = 500;
        frameHeight = 330;
        // level = 0;
        mod = i;
        hungryPoint = 100;
        digimon = new ArrayList<>();
        pref = this.getActivity().getSharedPreferences(getString(R.string.info), context.MODE_PRIVATE);
        editor = pref.edit();
        loadInfo();  //성영 추가: 파이어베이스에서 계정에 저장된 레벨, 경험치 받아와서 저장
        Log.d("5525", "lv:" + level + "/ EXP:" + exp);
        //digimon.add(digimonArray.setImageDrawable(getResources().getDrawable(R.drawable.monster2_1)));
        //setImageDrawable(getResources().getDrawable(R.drawable.monster1, null));
        // exp = 0;
        State state = State.LV1;
        imageSize = mImage.getWidth();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mod = i % 3;
                animateImage(mod);
                imageMove();
            }
        };
        x = 0;
        mImage.setX(235);
        mImage.setY(frameHeight);
        mImage.setX(70.0f);
        menuBtn = damagochiView.findViewById(R.id.menuBtn);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu();
            }
        });
        onThreadStart();
        return damagochiView;
    }

    void onThreadStart() {
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        handler.sendMessage(handler.obtainMessage());
                        Thread.sleep(500);
                    } catch (Throwable t) {
                    }
                }
            }
        });
        myThread.start();
    }

    //level=1
    public void switchDigimon1(int mod) {
        switch (mod) {

            case 0:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster1, null));
                break;
            case 1:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster2, null));
                break;
            case 2:
                i = 0;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster3, null));
                break;
        }
    }

    //level=2
    public void switchDigimon2(int mod) {
        switch (mod) {
            case 0:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster2_1, null));
                break;
            case 1:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster2_2, null));
                break;
            case 2:
                i = 0;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster2_1, null));
                break;
        }
    }

    //level=3
    public void switchDigimon3(int mod) {
        switch (mod) {
            case 0:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster3_1, null));
                break;
            case 1:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster3_2, null));
                break;
            case 2:
                i = 0;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster3_1, null));
                break;
        }
    }

    public void switchDigimon4(int mod) {
        switch (mod) {
            case 0:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster4_1, null));
                break;
            case 1:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster4_2, null));
                break;
            case 2:
                i = 0;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster4_1, null));
                break;
        }
    }

    public void switchDigimon5(int mod) {
        switch (mod) {
            case 0:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster5_1, null));
                break;
            case 1:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster5_2, null));
                break;
            case 2:
                i = 0;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.monster5_1, null));
                break;
        }
    }

    //level=0
    public void showEgg1(int mod) {
        switch (mod) {
            case 0:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.egg3_1, null));
                break;
            case 1:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.egg3_2, null));
                break;
            case 2:
                i = 0;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.egg3_3, null));
                break;
        }
    }

    public void showEgg2(int mod) {
        switch (mod) {
            case 0:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.egg1_1, null));
                break;
            case 1:
                i++;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.egg1_2, null));
                break;
            case 2:
                i = 0;
                mImage.setImageDrawable(getResources().getDrawable(R.drawable.egg1_3, null));
                break;
        }
    }


    //Move
    public void animateImage(int mod) {
        this.mod = mod;
        if (level == 1)
            switchDigimon1(mod); //유년기
        else if (level == 2)
            switchDigimon2(mod);
        else if (level == 3)
            switchDigimon3(mod);
        else if (level == 4)
            switchDigimon4(mod);
        else if (level == 5)
            switchDigimon5(mod);
        else showEgg1(mod);
    }

    //horizontalMove
    public void imageMove() {
        if (level > 0) {
            if (moveflag) {
                x += 10;
            } else if (!moveflag) {
                x -= 10;
            }
            if (x < 70) {
                x = 70;
                moveflag = true;
            } else if (430 < x) {
                x = frameWidth - mImage.getWidth() + 20;
                moveflag = false;
            }
        } else x = 230;
        mImage.setX(x);
    }


    public void updateThread() {
        //mImage.setImageDrawable(frame0);
    }

    public void evolution() {
        if (level < 5) level++;
        saveInfo();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void Menu() {  //구글맵과 배틀관련 메뉴 실행
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("지도");
        ListItems.add("배틀");
        ListItems.add("밥 주기");
        ListItems.add("로그아웃");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Menu");
        //builder.setMessage("무엇을 하시겠습니까?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {  //which는 다이얼로그에서 누른 위치
                //switch문을 통해 옵션 구현
                switch (which) {
                    case 0:  //구글맵 구현
                        Intent intent = new Intent(getContext(), MapsActivity.class);
                        startActivity(intent);
                        break;
                    case 1:  //배틀 구현
                        connectServer connectServer=new connectServer("192.168.0.7");

                        battleIntent = new Intent("StartBattle");  //메인으로 배틀 버튼 눌린 것을 알림

                       // Log.d("4449","배틀 상대 id="+userId);

                        IntentFilter intentFilter = new IntentFilter();
                        intentFilter.addAction("battle_start");
                        battleReceiver=new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String user=intent.getStringExtra("start_signal");
                                Log.d("4449","메인에서 받은 아이디"+user);
                            }
                        };
                        getActivity().registerReceiver(battleReceiver,intentFilter);
                        /*if(!userId.equals("NO")) {
                            battleSet();
                        }*/
                        break;
                    case 2:  //밥주기 구현
                        break;
                    case 3: //로그아웃 구현
                        break;
                }
                String selectedText = items[which].toString();
                Toast.makeText(getActivity(), selectedText, Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    public void notify_BattleDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("배틀 불가능").setMessage("레벨이 낮아 배틀이 불가능합니다.(성숙기부터 배틀이 가능합니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    public int calc_battlePower(int level, int cnt, int item) {
        int ATK = (level * cnt) / 10 + item;
        editor.putInt("ATK", ATK);
        editor.commit();
        return ATK;
    }

    public void battleSet(){
        if (level > 2) {   //배틀 공격력 측정 부분
            Intent shake_Service = new Intent(getContext(), shakeService.class);
            getActivity().startService(shake_Service);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("shaking_Service");
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    cnt = intent.getIntExtra("finish", 0);
                    int item = pref.getInt("item", 0);
                    Log.d("4449", "다마고치 프래그먼트에서 item값:" + item);
                    Log.d("4449", "shaek service 끝남:" + cnt);
                    int ATK = calc_battlePower(level, cnt, item);
                    Log.d("4449", "총 공격력은 :" + ATK);
                    battleIntent.putExtra("ATK",ATK);
                    getActivity().sendBroadcast(battleIntent);
                }
            };
            getActivity().registerReceiver(mReceiver, intentFilter);
        } else
            notify_BattleDialog();
    }


}

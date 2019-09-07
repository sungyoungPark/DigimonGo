package example.asus.digimongo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseExceptionMapper;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;
    FragmentTransaction fragmentTransaction ;
    LoginFragment loginFragment;
    Fragment currentFragment;
  //  Bundle bundle;
    public final String id = "idKey";
    public final String pass = "passwordKey";
    public final String level = "levelKey";
    public final String exp = "expKey";
    public final String hungry = "hungryKey";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public final String info = "Info";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int loginflag=0;    //로그인 flag
    Intent intent;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    public static final int REQUEST_CODE_PERMISSIONS=1000;
    BroadcastReceiver mReceiver;
    int BattleFlag=0;
    String value;  //battle 부분에서 uid저장 하는 부분

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        currentFragment = getFragmentManager().findFragmentById(R.id.loginFrame);  //현재 프레그먼트 갖고 오기
        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        mDatabase=database.getReference();

        //강호 수정 끝
        sharedPreferences = getSharedPreferences(getString(R.string.info), Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    //강호수정 끝

    mAuthListener=new FirebaseAuth.AuthStateListener()

    {
        @Override
        public void onAuthStateChanged (@NonNull FirebaseAuth firebaseAuth){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            Log.d("5525", "onAuthStateChanged: signed_in: " + user.getUid());   //사용자 uid를 갖고옴.
        } else {
            Log.d("5525", "onAuthStateChanged: signed out");
        }
    }
    };

    loginFragment=new LoginFragment();

    LinearLayout openingScreen = findViewById(R.id.openingScreen);
        openingScreen.setOnTouchListener(new View.OnTouchListener()

    {
        @Override
        public boolean onTouch (View v, MotionEvent event){   //화면을 누르면 로그인 프레그먼트생성, 로그인 화면으로 넘어감
        //Toast.makeText(getApplicationContext(), "눌림", Toast.LENGTH_LONG).show();
        if (loginflag == 0) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.loginFrame, loginFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        return true;
    }
    });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("StartBattle");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
             Log.d("4449","스타트 배틀로 불림");
             String id=intent.getStringExtra("BattleID");
             int ATK=intent.getIntExtra("ATK",0);
             Log.d("4449","battleid로 넘어온 값:"+id);
             Log.d("4449","ATK로 넘어온 값:"+ATK);
              Battle battle=new Battle(id,ATK);
             // waitBattle();
            }
        };
        registerReceiver(mReceiver, intentFilter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null)
            mAuth.removeAuthStateListener(mAuthListener);
    }


   public void correctID(String email, String password){   //서버와 로그인
      // final int[] flag = {0};
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"로그인 성공.",Toast.LENGTH_LONG).show();
                            readFireBase();
                            loginflag=1;
                            Fragment fg=DamagochiFragment.newInstance();
                            setChildFragment(fg);
                        }

                        else{
                            Toast.makeText(MainActivity.this,"로그인 실패.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void signUp(final String email, String password){
        if(email==null ||password==null){
            Toast.makeText(this,"빈칸을 채우시오",Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(MainActivity.this,"회원가입 실패",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this,"회원가입 성공",Toast.LENGTH_SHORT).show();
                        loginflag=1;
                        Fragment fg=DamagochiFragment.newInstance();
                        setChildFragment(fg);
                        writeFireBase(email,0,0);
                    }
                }
            });
        }
    }

    private void setChildFragment(Fragment child){
        FragmentTransaction childFrag=getFragmentManager().beginTransaction();
        childFrag.replace(R.id.loginFrame,child);
        childFrag.commit();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void clickFindPassword(){     //이메일 입력하면 이메일로 비밀번호 바꾸는 메일 보내줌, 파이어베이스 내에서 알아서 처리
        final AutoCompleteTextView email=new AutoCompleteTextView(this);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(email)
                .setTitle("계정을 입력하세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emailAddress=email.getText().toString();
                        if(!emailAddress.equals("")){
                            FirebaseAuth auth=FirebaseAuth.getInstance();
                            auth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(MainActivity.this,"메일을 보냈습니다.",Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(MainActivity.this,"존재하지 않은 계정입니다.",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    else{
                            Toast.makeText(MainActivity.this,"계정을 입력하세요",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    public void writeFireBase(String email,int Lv_flag,int EXP_flag){  //파이어베이스에 데이터 저장
        mDatabase.child(mAuth.getUid()).child("LV_flag").setValue(Lv_flag);
        mDatabase.child(mAuth.getUid()).child("EXP_flag").setValue(EXP_flag);
    }

    public void readFireBase(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int LV=dataSnapshot.child(mAuth.getUid()).child("LV_flag").getValue(int.class);
                int EXP=dataSnapshot.child(mAuth.getUid()).child("EXP_flag").getValue(int.class);
                editor.putInt("LV",LV);
                editor.putInt("EXP",EXP);
                editor.commit();

             //   Log.d("4449","Value is: "+LV+"/"+EXP);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d("4449","Failed to read value.", databaseError.toException());
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {  //권한요청
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE_PERMISSIONS:
                if(grantResults.length>0&&
                        grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"권한이 허용됨",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this,"권한이 거부됨",Toast.LENGTH_LONG).show();
                }
        }
    }


    @Override
    protected void onDestroy() {
    unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}

package example.asus.digimongo;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class LoginFragment extends Fragment {//로그인 화면을 품은 프레그먼트

    MainActivity activity;   //프레그먼트에서 메인엑티비티를 갖고 옴


    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(){
        return new LoginFragment();
    }


    @Override
    public void onAttach(Context context) {  //메인 엑티비티와 연결
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {   //프레그먼트 꺼지면 액티비티와 연결 종료
        super.onDetach();
        activity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final ViewGroup loginView = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);
        final Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.loginFrame);  //현재 프레그먼트 갖고 오기

        Button backBtn = loginView.findViewById(R.id.backBtn);
        Button loginBtn = loginView.findViewById(R.id.loginBtn);
        Button registerBtn = loginView.findViewById(R.id.registerBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {   //뒤로가기 버튼, 누르면 메인 화면으로 가짐(디지몬고 그림)
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().remove(LoginFragment.this).commit();  //버튼 누르면 현재 띄어 놓은 프레그먼트 삭제
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                Fragment fg;
                fg=loginIDFragment.newInstance();
                setLoginFragment(fg);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fg;
                fg=RegisterFragment.newInstance();
                setRegisterFragment(fg);
            }
        });

        return loginView;
    }

    private void setLoginFragment(Fragment child){
        FragmentTransaction childFrag=getFragmentManager().beginTransaction();
        childFrag.replace(R.id.loginFrame,child);
        childFrag.commit();
    }
    private void setRegisterFragment(Fragment child){
        FragmentTransaction childFrag=getFragmentManager().beginTransaction();
        childFrag.replace(R.id.loginFrame,child);
        childFrag.commit();
    }
}

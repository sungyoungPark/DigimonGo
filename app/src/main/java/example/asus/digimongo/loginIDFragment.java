package example.asus.digimongo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class loginIDFragment extends Fragment {

    MainActivity activity;
    Button OkButton;
    EditText IDText;
    EditText pwText;

    //강호수정
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public loginIDFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {   //프레그먼트 꺼지면 액티비티와 연결 종료
        super.onDetach();
        activity = null;
    }

    public static loginIDFragment newInstance() {
        return new loginIDFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup loginIDView = (ViewGroup) inflater.inflate(R.layout.fragment_login_id, container, false);
        final Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.loginIDLayOut);  //현재 프레그먼트 갖고 오기
        pref = this.getActivity().getSharedPreferences(getString(R.string.info), Activity.MODE_PRIVATE);
        editor = pref.edit();
        activity = (MainActivity) getActivity();



        OkButton = (Button) loginIDView.findViewById(R.id.OKBtn);
        IDText = loginIDView.findViewById(R.id.IDText);
        IDText.setHint("메일을 입력하시오");
        IDText.setText("leekangho@naver.com");
        pwText = loginIDView.findViewById(R.id.pwText);
        pwText.setHint("비밀번호를 입력하시오");
        pwText.setText("123456");
        OkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = IDText.getText().toString();
                String passwd = pwText.getText().toString();
                Log.d("3838", "버튼 눌림");
                editor.putString(getString(R.string.id),email);
                editor.putString(getString(R.string.pass), passwd);
                editor.commit();
               activity.correctID(email, passwd);

            }
        });
        return loginIDView;
    }

}

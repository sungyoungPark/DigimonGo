package example.asus.digimongo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class RegisterFragment extends Fragment {

    MainActivity activity;   //프레그먼트에서 메인엑티비티를 갖고 옴
    Button registerBtn;
    Button findPwdBtn;
    EditText IDText;
    EditText pwText;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup registerView = (ViewGroup) inflater.inflate(R.layout.fragment_register, container, false);
        final Fragment currentFragment = getActivity().getFragmentManager().findFragmentById(R.id.loginIDLayOut);  //현재 프레그먼트 갖고 오기

       registerBtn=registerView.findViewById(R.id.registerBtn);
        findPwdBtn=registerView.findViewById(R.id.findPwdBtn);
        activity=(MainActivity)getActivity();
        IDText=registerView.findViewById(R.id.IDText);
        IDText.setText("메일을 입력하시오");
        pwText=registerView.findViewById(R.id.pwText);
        pwText.setText("비밀번호를 입력하시오");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = IDText.getText().toString();
                String passwd = pwText.getText().toString();
                Log.d("3838", "버튼 눌림");
                activity.signUp(email, passwd);
            }
        });

        findPwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("3838", "버튼 눌림");
            activity.clickFindPassword();
            }
        });


        return registerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }



}

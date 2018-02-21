package view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cafemobile.waffle.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CafeListFragment extends Fragment {

    View v;
    @BindView(R.id.go_intro_btn) Button goIntroBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_cafe_list, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.go_intro_btn) void goIntro(){
        Intent intent = new Intent(getContext(), IntroActivity.class);
        startActivity(intent);
    }



}

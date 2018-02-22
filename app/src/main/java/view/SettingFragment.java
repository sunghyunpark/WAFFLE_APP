package view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cafemobile.waffle.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import presenter.LoginPresenter;

public class SettingFragment extends Fragment {

    View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.logout_btn) void logoutClicked(){
        LoginPresenter loginPresenter = new LoginPresenter(getContext());
        loginPresenter.logout();
    }

}

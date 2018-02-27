package view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

import com.cafemobile.waffle.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(Activity activity) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //다이얼 로그 제목을 삭제하자
        setContentView(R.layout.activity_loading_dialog); // 다이얼로그 보여줄 레이아웃

    }
}


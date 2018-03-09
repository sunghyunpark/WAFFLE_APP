package view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cafemobile.waffle.R;

import api.ApiClient;
import api.ApiInterface;
import api.response.CommonResponse;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteCommentActivity extends AppCompatActivity implements TextWatcher {

    @BindView(R.id.back_btn) ImageButton backBtn;
    @BindView(R.id.comment_edit_box) EditText comment_et;
    @BindView(R.id.comment_length_txt) TextView comment_length_tv;
    @BindView(R.id.write_btn) Button writeBtn;
    @BindString(R.string.network_error_txt) String networkErrorStr;
    @BindString(R.string.write_comment_activity_error_empty_comment_txt) String emptyCommentErrorStr;

    private String user_id, cafe_id;
    private String beforeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        user_id = intent.getExtras().getString("user_id");
        cafe_id = intent.getExtras().getString("cafe_id");

        comment_et.addTextChangedListener(this);
    }

    private void PostCafeComment(String cafe_id, String uid, String comment_text){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommonResponse> call = apiService.WriteCafeComment("write_cafe_comment", uid, cafe_id, comment_text);
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                CommonResponse commonResponse = response.body();
                if(!commonResponse.isError()){
                    Toast.makeText(getApplicationContext(), commonResponse.getError_msg(),Toast.LENGTH_SHORT).show();

                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), commonResponse.getError_msg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), networkErrorStr, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.back_btn) void goBack(){
        finish();
    }
    @OnClick(R.id.write_btn) void commentWrite(){
        String commentStr = comment_et.getText().toString();
        commentStr = commentStr.trim();

        if(commentStr.equals("")){
            Toast.makeText(getApplicationContext(), emptyCommentErrorStr, Toast.LENGTH_SHORT).show();
        }else{
            PostCafeComment(cafe_id, user_id, commentStr);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length() >= 150)
        {
            Toast.makeText(getApplicationContext(), "150자까지 입력 가능합니다.", Toast.LENGTH_SHORT).show();
            comment_et.setText(beforeStr);
        }
        comment_length_tv.setText(s.length() + "/150");
        comment_length_tv.setTextColor(getResources().getColor(R.color.colorTextHintGray));
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        beforeStr = s.toString();

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}

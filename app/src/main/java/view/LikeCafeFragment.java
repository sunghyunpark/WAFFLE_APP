package view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cafemobile.waffle.R;

import api.ApiClient;
import api.ApiInterface;
import api.response.MyFavoriteCntResponse;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeCafeFragment extends Fragment {

    View v;
    @BindView(R.id.my_favorite_cafe_img) ImageView my_favorite_cafe_iv;
    @BindView(R.id.my_comment_cafe_img) ImageView my_comment_cafe_iv;
    @BindView(R.id.like_cnt_txt) TextView my_favorite_cnt_tv;
    @BindView(R.id.comment_cnt_txt) TextView my_comment_cnt_tv;
    @BindView(R.id.recent_favorite_created_at_txt) TextView recent_favorite_created_at_tv;
    @BindView(R.id.recent_favorite_cafe_name_txt) TextView recent_favorite_cafe_name_tv;
    @BindView(R.id.recent_comment_created_at_txt) TextView recent_comment_created_at_tv;
    @BindView(R.id.recent_comment_cafe_name_txt) TextView recent_comment_cafe_name_tv;
    @BindString(R.string.network_error_txt) String networkErrorStr;
    @BindString(R.string.like_cafe_my_favorite_cafe_time) String myFavoriteCafeTimeStr;
    @BindString(R.string.like_cafe_my_favorite_cafe_name) String myFavoriteCafeNameStr;
    @BindString(R.string.like_cafe_my_comment_cafe_time) String myCommentTimeStr;
    @BindString(R.string.like_cafe_my_comment_cafe_name) String myCommentCafeNameStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume(){
        super.onResume();
        LoadData(UserModel.getInstance().getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_like_cafe, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, v);


        //Glide Options
        RequestOptions requestOptions = new RequestOptions();

        Glide.with(getActivity())
                .setDefaultRequestOptions(requestOptions)
                .load(R.drawable.like_cafe_myfavorite_cafe_round)
                .into(my_favorite_cafe_iv);

        Glide.with(getActivity())
                .setDefaultRequestOptions(requestOptions)
                .load(R.drawable.like_cafe_mycomment_cafe_round)
                .into(my_comment_cafe_iv);

        return v;
    }

    private void LoadData(String uid){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<MyFavoriteCntResponse> call = apiService.GetMyFavoriteCnt("my_favorite_cafe_cnt", uid);
        call.enqueue(new Callback<MyFavoriteCntResponse>() {
            @Override
            public void onResponse(Call<MyFavoriteCntResponse> call, Response<MyFavoriteCntResponse> response) {
                MyFavoriteCntResponse myFavoriteCntResponse = response.body();
                if(!myFavoriteCntResponse.isError()){
                    my_favorite_cnt_tv.setText(myFavoriteCntResponse.getMyFavoriteCafeCnt());
                    my_comment_cnt_tv.setText(myFavoriteCntResponse.getMyCommentCafeCnt());

                    recent_favorite_created_at_tv.setText(myFavoriteCafeTimeStr+myFavoriteCntResponse.getMyFavoriteCafeCreated_at());
                    recent_favorite_cafe_name_tv.setText(myFavoriteCafeNameStr+myFavoriteCntResponse.getMyFavoriteCafeName());
                    recent_comment_created_at_tv.setText(myCommentTimeStr+myFavoriteCntResponse.getMyCommentCafeCreated_at());
                    recent_comment_cafe_name_tv.setText(myCommentCafeNameStr+myFavoriteCntResponse.getMyCommentCafeName());
                }else{
                    Toast.makeText(getActivity(), myFavoriteCntResponse.getError_msg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyFavoriteCntResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getActivity(), networkErrorStr,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    @OnClick(R.id.my_favorite_cafe_img) void goFavoriteCafe(){
        Intent intent = new Intent(getActivity(), MyFavoriteCafeActivity.class);
        startActivity(intent);
    }*/
    @OnClick(R.id.my_comment_cafe_img) void goCommentCafe(){
        Intent intent = new Intent(getActivity(), MyCommentCafeActivity.class);
        startActivity(intent);
    }

}

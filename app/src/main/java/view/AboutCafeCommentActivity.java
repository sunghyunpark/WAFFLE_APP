package view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cafemobile.waffle.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import api.ApiClient;
import api.ApiInterface;
import api.response.CafeEtcInfoResponse;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.CommentModel;
import model.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.CommonUtil;

public class AboutCafeCommentActivity extends AppCompatActivity {

    @BindView(R.id.title_txt) TextView title_tv;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.empty_comment_layout) ViewGroup emptyCommentLayout;
    @BindView(R.id.go_comment_btn) Button goCommentBtn;
    @BindView(R.id.back_btn) ImageButton backBtn;
    @BindString(R.string.network_error_txt) String networkErrorStr;

    private String cafeId, cafeName;
    //RecyclerView
    RecyclerAdapter adapter;
    private ArrayList<CommentModel> commentModelArrayList;
    CommonUtil commonUtil = new CommonUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_cafe_comment);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        cafeId = intent.getExtras().getString("cafe_id");
        cafeName = intent.getExtras().getString("cafe_name");

        SetUI();
    }

    private void SetUI(){
        //recyclerview 초기화
        commentModelArrayList = new ArrayList<CommentModel>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new RecyclerAdapter(commentModelArrayList);
        recyclerView.setLayoutManager(linearLayoutManager);

        title_tv.setText(cafeName);

        LoadCafeEtcInfo(UserModel.getInstance().getUid(), cafeId);
    }

    /**
     * Load Cafe Etc Info
     * @param cafe_id
     */
    private void LoadCafeEtcInfo(String uid, String cafe_id){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CafeEtcInfoResponse> call = apiService.GetCafeEtcInfo("cafe_etc_info", cafe_id, uid, "Y");
        call.enqueue(new Callback<CafeEtcInfoResponse>() {
            @Override
            public void onResponse(Call<CafeEtcInfoResponse> call, Response<CafeEtcInfoResponse> response) {
                CafeEtcInfoResponse cafeEtcInfoResponse = response.body();
                if(!cafeEtcInfoResponse.isError()){
                    try{
                        int comment_size = cafeEtcInfoResponse.getComment_text_list().size();
                        if(comment_size > 0){
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyCommentLayout.setVisibility(View.GONE);
                            for(int i=0;i<comment_size;i++){
                                commentModelArrayList.add(cafeEtcInfoResponse.getComment_text_list().get(i));
                            }
                            recyclerView.setAdapter(adapter);
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            emptyCommentLayout.setVisibility(View.VISIBLE);
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), cafeEtcInfoResponse.getError_msg(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CafeEtcInfoResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), networkErrorStr,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 0;

        List<CommentModel> listItems;

        private RecyclerAdapter(List<CommentModel> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_comment_item, parent, false);
                return new RecyclerAdapter.Comment_VH(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private CommentModel getItem(int position) {
            return listItems.get(position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof RecyclerAdapter.Comment_VH) {
                final CommentModel currentItem = getItem(position);
                final RecyclerAdapter.Comment_VH VHitem = (RecyclerAdapter.Comment_VH)holder;

                VHitem.userName_tv.setText(currentItem.getName());
                VHitem.comment_tv.setText(currentItem.getCommentText());
                Date to = null;
                try{
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    to = transFormat.parse(currentItem.getCreatedAt());
                }catch (ParseException p){
                    p.printStackTrace();
                }
                VHitem.updated_tv.setText(commonUtil.formatTimeString(to));

            }
        }

        /**
         * 리뷰 아이템
         */
        private class Comment_VH extends RecyclerView.ViewHolder{
            TextView userName_tv;
            TextView updated_tv;
            TextView comment_tv;
            private Comment_VH(View itemView){
                super(itemView);
                userName_tv = (TextView)itemView.findViewById(R.id.user_name_txt);
                updated_tv = (TextView)itemView.findViewById(R.id.updated_at_txt);
                comment_tv = (TextView)itemView.findViewById(R.id.comment_txt);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_ITEM;
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size();
        }
    }

    @OnClick(R.id.back_btn) void goBack(){
        finish();
    }

    @OnClick(R.id.go_comment_btn) void goComment(){
        Intent intent = new Intent(getApplicationContext(), WriteCommentActivity.class);
        intent.putExtra("user_id", UserModel.getInstance().getUid());
        intent.putExtra("cafe_id", cafeId);
        startActivity(intent);
        finish();
    }
}

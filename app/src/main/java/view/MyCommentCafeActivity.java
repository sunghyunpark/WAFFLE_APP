package view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cafemobile.waffle.R;
import com.cafemobile.waffle.WaffleApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import api.ApiClient;
import api.ApiInterface;
import api.response.CommentResponse;
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

public class MyCommentCafeActivity extends AppCompatActivity {

    //RecyclerView
    RecyclerAdapter adapter;
    private ArrayList<CommentModel> commentModelArrayList;
    CommonUtil commonUtil = new CommonUtil();
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.back_btn) ImageButton backBtn;
    @BindView(R.id.my_comment_cafe_empty_txt) TextView emptyStr;
    @BindString(R.string.network_error_txt) String networkErrorStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment_cafe);

        ButterKnife.bind(this);

        //recyclerview 초기화
        commentModelArrayList = new ArrayList<CommentModel>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new RecyclerAdapter(commentModelArrayList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        LoadData();
    }

    private void LoadData(){
        if(commentModelArrayList != null)
            commentModelArrayList.clear();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommentResponse> call = apiService.GetMyCommentCafeList("my_comment_cafe", UserModel.getInstance().getUid());
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                if(!commentResponse.isError()){
                    emptyStr.setVisibility(View.GONE);
                    int listSize = commentResponse.getCommentList().size();
                    for(int i=0;i<listSize;i++){
                        commentModelArrayList.add(commentResponse.getCommentList().get(i));
                    }
                    adapter.notifyDataSetChanged();
                }else{

                }
                //Toast.makeText(getApplicationContext(), myCommentResponse.getError_msg(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_my_comment_item, parent, false);
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
                //Glide Options
                RequestOptions requestOptions_cafe = new RequestOptions();
                //requestOptions_cafe.placeholder(R.mipmap.not_cafe_img);
                //requestOptions_cafe.error(R.mipmap.not_cafe_img);
                requestOptions_cafe.circleCrop();    //circle

                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(requestOptions_cafe)
                        .load(WaffleApplication.SERVER_BASE_PATH+currentItem.getCafeThumbnail())
                        .into(VHitem.cafeProfile_iv);

                VHitem.cafeName_tv.setText(currentItem.getCafeName());

                VHitem.userName_tv.setText(currentItem.getName());

                Date to = null;
                try{
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    to = transFormat.parse(currentItem.getCreatedAt());
                }catch (ParseException p){
                    p.printStackTrace();
                }
                VHitem.updated_tv.setText(commonUtil.formatTimeString(to));

                VHitem.comment_tv.setText(currentItem.getCommentText());

                VHitem.comment_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), AboutCafeActivity.class);
                        intent.putExtra("isData", "N");
                        intent.putExtra("cafe_id", getItem(position).getCafeId());
                        startActivity(intent);
                    }
                });
            }
        }

        /**
         * 리뷰 아이템
         */
        private class Comment_VH extends RecyclerView.ViewHolder{
            ImageView cafeProfile_iv;
            TextView cafeName_tv;
            TextView userName_tv;
            TextView updated_tv;
            TextView comment_tv;
            ViewGroup comment_layout;
            private Comment_VH(View itemView){
                super(itemView);
                cafeProfile_iv = (ImageView)itemView.findViewById(R.id.cafe_profile_img);
                cafeName_tv = (TextView)itemView.findViewById(R.id.cafe_name_txt);
                userName_tv = (TextView)itemView.findViewById(R.id.user_name_txt);
                updated_tv = (TextView)itemView.findViewById(R.id.created_at_txt);
                comment_tv = (TextView)itemView.findViewById(R.id.comment_txt);
                comment_layout = (ViewGroup)itemView.findViewById(R.id.comment_layout);
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
}

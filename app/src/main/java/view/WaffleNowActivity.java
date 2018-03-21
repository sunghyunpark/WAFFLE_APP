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
import android.widget.ImageView;
import android.widget.TextView;

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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.CommentModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.CommonUtil;

public class WaffleNowActivity extends AppCompatActivity {

    private static final int LOAD_DATA_COUNT = 10;
    private ArrayList<CommentModel> listItems;
    private String lastCommentId = "0";
    private CommentRecyclerAdapter commentRecyclerAdapter;
    CommonUtil commonUtil = new CommonUtil();
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waffle_now);

        ButterKnife.bind(this);

        init();
    }

    /**
     * init
     */
    private void init(){
        listItems = new ArrayList<CommentModel>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        commentRecyclerAdapter = new CommentRecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(commentRecyclerAdapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                LoadData();
            }
        });

        LoadData();
    }

    /**
     * Load WAFFLE NOW Data
     */
    private void LoadData(){
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommentResponse> call = apiService.GetRecentCommentList("recent_comment", "all", lastCommentId);
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                if(!commentResponse.isError()){
                    int listSize = commentResponse.getCommentList().size();
                    for (int i=0;i<listSize;i++){
                        listItems.add(commentResponse.getCommentList().get(i));
                        Log.d("WAFFLE_NOW", commentResponse.getCommentList().get(i).getCafeName());
                    }
                    lastCommentId = commentResponse.getLastCommentId();
                    commentRecyclerAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
            }
        });
    }

    private abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
        
        private int previousTotal = 0; // The total number of items in the dataset after the last load
        private boolean loading = true; // True if we are still waiting for the last set of data to load.
        private int visibleThreshold = LOAD_DATA_COUNT; // The minimum amount of items to have below your current scroll position before loading more.
        int firstVisibleItem, visibleItemCount, totalItemCount;

        private int current_page = 1;

        private LinearLayoutManager mLinearLayoutManager;

        public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
            this.mLinearLayoutManager = linearLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                // Do something
                current_page++;

                onLoadMore(current_page);

                loading = true;
            }
        }

        public abstract void onLoadMore(int current_page);
    }

    /**
     * WAFFLE, NOW recyclerView Adapter
     */
    private class CommentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 0;
        List<CommentModel> listItems;

        private CommentRecyclerAdapter(List<CommentModel> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_recent_comment_item, parent, false);
                return new CommentRecyclerAdapter.Comment_VH(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private CommentModel getItem(int position) {
            return listItems.get(position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof CommentRecyclerAdapter.Comment_VH) {
                final CommentModel currentItem = getItem(position);
                final CommentRecyclerAdapter.Comment_VH VHitem = (CommentRecyclerAdapter.Comment_VH)holder;

                //Glide Options
                RequestOptions requestOptions_cafe = new RequestOptions();
                //requestOptions_cafe.placeholder(R.mipmap.not_cafe_img);
                //requestOptions_cafe.error(R.mipmap.not_cafe_img);
                requestOptions_cafe.circleCrop();    //circle

                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(requestOptions_cafe)
                        .load(WaffleApplication.SERVER_BASE_PATH+currentItem.getCafeThumbnail())
                        .into(VHitem.cafeProfile_iv);

                VHitem.userNickName_tv.setText(currentItem.getName());
                VHitem.comment_tv.setText(currentItem.getCommentText());
                VHitem.cafeName_tv.setText(currentItem.getCafeName());

                Date to = null;
                try{
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    to = transFormat.parse(currentItem.getCreatedAt());
                }catch (ParseException p){
                    p.printStackTrace();
                }
                VHitem.created_at_tv.setText(commonUtil.formatTimeString(to));

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

        private class Comment_VH extends RecyclerView.ViewHolder{

            TextView userNickName_tv;
            TextView created_at_tv;
            TextView comment_tv;
            ImageView cafeProfile_iv;
            TextView cafeName_tv;
            ViewGroup comment_layout;

            private Comment_VH(View itemView){
                super(itemView);
                userNickName_tv = (TextView)itemView.findViewById(R.id.user_nickname_txt);
                created_at_tv = (TextView)itemView.findViewById(R.id.created_at_txt);
                comment_tv = (TextView)itemView.findViewById(R.id.comment_txt);
                cafeProfile_iv = (ImageView)itemView.findViewById(R.id.cafe_profile_img);
                cafeName_tv = (TextView)itemView.findViewById(R.id.cafe_name_txt);
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

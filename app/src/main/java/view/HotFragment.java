package view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import api.response.CafeResponse;
import api.response.CommentResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CafeModel;
import model.CommentModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.CommonUtil;


public class HotFragment extends Fragment {

    @BindView(R.id.recommend_pager) ViewPager recommendPager;
    @BindView(R.id.recent_comment_recyclerView) RecyclerView recentCommentRecyclerView;
    @BindView(R.id.go_to_all_comment_btn_bottom) Button goToAllCommentBtn;
    CommonUtil commonUtil = new CommonUtil();
    View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_hot, container, false);

        ButterKnife.bind(this, v);

        init();

        return v;
    }

    /**
     * 초기화
     */
    private void init(){

        LoadRecommendCafeList();

        LoadRecentCafeList();
    }

    /**
     * 이런 카페는 어때요? 데이터를 불러온 뒤 viewPager 에 적용
     */
    private void LoadRecommendCafeList(){
        final ArrayList<CafeModel> recommendCafeList = new ArrayList<CafeModel>();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CafeResponse> call = apiService.GetRecommendCafeList("recommend_cafe");
        call.enqueue(new Callback<CafeResponse>() {
            @Override
            public void onResponse(Call<CafeResponse> call, Response<CafeResponse> response) {
                CafeResponse cafeResponse = response.body();
                if(!cafeResponse.isError()){
                    int listSize = cafeResponse.getCafeList().size();
                    for (int i=0;i<listSize;i++){
                        recommendCafeList.add(cafeResponse.getCafeList().get(i));
                    }

                    PagerAdapter mPagerAdapter = new PagerAdapter(getFragmentManager(), recommendCafeList);
                    float density = getResources().getDisplayMetrics().density;
                    int pageMargin = 8 * (int)density; // 8dp

                    recommendPager.setPageMargin(pageMargin);
                    recommendPager.setClipToPadding(false);
                    recommendPager.setPadding(80, 0, 80, 0);
                    recommendPager.setAdapter(mPagerAdapter);
                }else{

                }
            }
            @Override
            public void onFailure(Call<CafeResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
            }
        });
    }

    /**
     * WAFFLE, NOW 데이터 받아옴
     */
    private void LoadRecentCafeList(){
        final ArrayList<CommentModel> commentList = new ArrayList<CommentModel>();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CommentResponse> call = apiService.GetRecentCommentList("recent_comment", "N", "0");
        call.enqueue(new Callback<CommentResponse>() {
            @Override
            public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                CommentResponse commentResponse = response.body();
                if(!commentResponse.isError()){
                    int listSize = commentResponse.getCommentList().size();
                    if(listSize > 0)
                        goToAllCommentBtn.setVisibility(View.VISIBLE);
                    for (int i=0;i<listSize;i++){
                        commentList.add(commentResponse.getCommentList().get(i));
                        Log.d("recentComment", commentResponse.getCommentList().get(i).getCafeName());
                    }
                    /*
                     * WAFFLE NOW recyclerView
                     */
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    CommentRecyclerAdapter commentRecyclerAdapter = new CommentRecyclerAdapter(commentList);
                    recentCommentRecyclerView.setLayoutManager(linearLayoutManager);
                    recentCommentRecyclerView.setNestedScrollingEnabled(false);
                    recentCommentRecyclerView.setAdapter(commentRecyclerAdapter);

                }else{

                }
            }
            @Override
            public void onFailure(Call<CommentResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
            }
        });
    }

    /**
     * 이런 카페는 어떄요? viewPager Adapter
     */
    private class PagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<CafeModel> listItem;

        public PagerAdapter(android.support.v4.app.FragmentManager fm, ArrayList<CafeModel> listItems) {
            super(fm);
            this.listItem = listItems;

        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        @Override
        public Fragment getItem(int position) {
            // 해당하는 page의 Fragment를 생성합니다.
            return RecommendCafeFragment.create(listItem, position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                android.support.v4.app.FragmentManager fm = fragment.getFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                //this.notifyDataSetChanged();
                ft.commitAllowingStateLoss();
            }
        }
        @Override
        public int getCount() {
            return 5;
        }

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

                Glide.with(getActivity())
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
                        Intent intent = new Intent(getActivity(), AboutCafeActivity.class);
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

}

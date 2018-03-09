package view;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import api.ApiClient;
import api.ApiInterface;
import api.response.CafeResponse;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import model.CafeModel;
import model.UserModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavoriteCafeActivity extends AppCompatActivity {

    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<CafeModel> listItems;

    @BindView(R.id.back_btn) ImageButton backBtn;
    @BindView(R.id.my_favorite_cafe_empty_txt) TextView empty_tv;
    @BindString(R.string.cafe_info_weekdays_time_txt) String cafeInfoWeekdaysTimeStr;
    @BindString(R.string.cafe_info_weekend_time_txt) String cafeInfoWeekendTimeStr;
    @BindString(R.string.cafe_open_state_txt) String cafeOpenStateStr;
    @BindString(R.string.cafe_close_state_txt) String cafeCloseStateStr;
    @BindString(R.string.network_error_txt) String networkErrorStr;

    @Override
    public void onResume(){
        super.onResume();
        LoadCafeList();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite_cafe);

        ButterKnife.bind(this);

        //recyclerview 초기화
        listItems = new ArrayList<CafeModel>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void LoadCafeList(){
        if(listItems != null)
            listItems.clear();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CafeResponse> call = apiService.GetMyFavoriteCafeList("my_favorite_cafe", UserModel.getInstance().getUid());
        call.enqueue(new Callback<CafeResponse>() {
            @Override
            public void onResponse(Call<CafeResponse> call, Response<CafeResponse> response) {
                CafeResponse cafeResponse = response.body();
                if(!cafeResponse.isError()){
                    empty_tv.setVisibility(View.GONE);
                    int listSize = cafeResponse.getCafeList().size();
                    for (int i=0;i<listSize;i++){
                        listItems.add(cafeResponse.getCafeList().get(i));
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    empty_tv.setVisibility(View.VISIBLE);
                }
                //Toast.makeText(getActivity(), cafeResponse.getError_msg(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<CafeResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getApplicationContext(), networkErrorStr,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 0;

        Calendar cal= Calendar.getInstance();
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss a");
        List<CafeModel> listItems;

        private RecyclerAdapter(List<CafeModel> listItems) {
            this.listItems = listItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_tab1_item, parent, false);
                return new RecyclerAdapter.Cafe_VH(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private CafeModel getItem(int position) {
            return listItems.get(position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof RecyclerAdapter.Cafe_VH) {
                final CafeModel currentItem = getItem(position);
                final RecyclerAdapter.Cafe_VH VHitem = (RecyclerAdapter.Cafe_VH)holder;

                VHitem.cafeName.setText(currentItem.getCafeName());

                //Glide Options
                RequestOptions requestOptions = new RequestOptions();
                //requestOptions.placeholder(R.mipmap.not_cafe_img);
                //requestOptions.error(R.mipmap.not_cafe_img);
                requestOptions.circleCrop();    //circle

                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(WaffleApplication.SERVER_BASE_PATH+currentItem.getCafeThumbnail())
                        .into(VHitem.cafeThumbnail);

                //내가 좋아요한 맛집 화면에서는 거리는 미노출
                VHitem.cafe_distance_tv.setVisibility(View.GONE);
                VHitem.cafe_weekdays_open_close_time_tv.setText(cafeInfoWeekdaysTimeStr + " " +currentItem.getCafeWeekDaysOpenTime() + " ~ "+currentItem.getCafeWeekDaysCloseTime());
                VHitem.cafe_weekend_open_close_time_tv.setText(cafeInfoWeekendTimeStr + " " + currentItem.getCafeWeekendOpenTime() + " ~ "+currentItem.getCafeWeekendCloseTime());

                try{
                    VHitem.cafe_day_off_tv.setText(currentItem.getCafeDayOff());
                }catch (NullPointerException e){

                }

                VHitem.cafe_address_tv.setText(currentItem.getCafeAddress());
                VHitem.cafe_phone_tv.setText(currentItem.getCafePhoneNum());

                VHitem.cafe_phone_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CallPhone(currentItem.getCafePhoneNum());
                    }
                });
                if(isOpenState(position)){
                    //open
                    VHitem.cafe_open_state_layout.setBackgroundResource(R.drawable.cafe_open_state_shape);
                    VHitem.cafe_open_state_tv.setText(cafeOpenStateStr);
                    VHitem.cafe_open_state_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                }else{
                    //close
                    VHitem.cafe_open_state_layout.setBackgroundResource(R.drawable.cafe_close_state_shape);
                    VHitem.cafe_open_state_tv.setText(cafeCloseStateStr);
                    VHitem.cafe_open_state_tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGray));
                }

                if(cafeFeature(position, 1)){
                    VHitem.full_time_img.setBackgroundResource(R.mipmap.cafe_full_time_img);
                }else{
                    VHitem.full_time_img.setBackgroundResource(R.mipmap.cafe_not_full_time_img);
                }
                if(cafeFeature(position, 2)){
                    VHitem.wifi_img.setBackgroundResource(R.mipmap.cafe_wifi_img);
                }else{
                    VHitem.wifi_img.setBackgroundResource(R.mipmap.cafe_not_wifi_img);
                }
                if(cafeFeature(position, 3)){
                    VHitem.smoke_img.setBackgroundResource(R.mipmap.cafe_smoke_img);
                }else{
                    VHitem.smoke_img.setBackgroundResource(R.mipmap.cafe_not_smoke_img);
                }
                if(cafeFeature(position, 4)){
                    VHitem.parking_img.setBackgroundResource(R.mipmap.cafe_parking_img);
                }else{
                    VHitem.parking_img.setBackgroundResource(R.mipmap.cafe_not_parking_img);
                }

                VHitem.cafe_item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), AboutCafeActivity.class);
                        intent.putExtra("isData", "Y");
                        intent.putExtra("CafeModel", getItem(position));
                        startActivity(intent);
                    }
                });
            }
        }

        /**
         * 카페 아이템
         */
        private class Cafe_VH extends RecyclerView.ViewHolder{

            TextView cafeName;
            ImageView cafeThumbnail;
            TextView cafe_weekdays_open_close_time_tv;
            TextView cafe_weekend_open_close_time_tv;
            TextView cafe_day_off_tv;
            TextView cafe_address_tv;
            TextView cafe_phone_tv;
            ViewGroup cafe_phone_btn;
            ViewGroup cafe_open_state_layout;
            TextView cafe_open_state_tv;
            ImageView full_time_img;
            ImageView wifi_img;
            ImageView smoke_img;
            ImageView parking_img;
            ViewGroup cafe_item_layout;
            TextView cafe_distance_tv;

            private Cafe_VH(View itemView){
                super(itemView);
                cafeName = (TextView)itemView.findViewById(R.id.cafe_name_txt);
                cafeThumbnail = (ImageView)itemView.findViewById(R.id.cafe_thumb_img);
                cafe_weekdays_open_close_time_tv = (TextView)itemView.findViewById(R.id.weekdays_open_close_time_txt);
                cafe_weekend_open_close_time_tv = (TextView)itemView.findViewById(R.id.weekend_open_close_time_txt);
                cafe_day_off_tv = (TextView)itemView.findViewById(R.id.cafe_day_off_txt);
                cafe_address_tv = (TextView)itemView.findViewById(R.id.cafe_address_txt);
                cafe_phone_tv = (TextView)itemView.findViewById(R.id.cafe_phone_txt);
                cafe_phone_btn = (ViewGroup)itemView.findViewById(R.id.cafe_phone_btn);
                cafe_open_state_layout = (ViewGroup)itemView.findViewById(R.id.cafe_open_state_layout);
                cafe_open_state_tv = (TextView)itemView.findViewById(R.id.cafe_open_state_txt);
                full_time_img = (ImageView)itemView.findViewById(R.id.full_time_img);
                wifi_img = (ImageView)itemView.findViewById(R.id.wifi_img);
                smoke_img = (ImageView)itemView.findViewById(R.id.smoke_img);
                parking_img = (ImageView)itemView.findViewById(R.id.parking_img);
                cafe_item_layout = (ViewGroup)itemView.findViewById(R.id.cafe_item_layout);
                cafe_distance_tv = (TextView)itemView.findViewById(R.id.distance_txt);
            }
        }

        /**
         * Cafe Feature 분기처리
         * @param position -> Item position
         * @param featureNo -> Feature Number(full time/wifi/smoke/parking)
         * @return
         */
        private boolean cafeFeature(int position, int featureNo){
            boolean flag = false;
            switch (featureNo){
                case 1:
                    return flag = getItem(position).getCafeFullTimeState().equals("Y");

                case 2:
                    return flag = getItem(position).getCafeWifiState().equals("Y");

                case 3:
                    return flag = getItem(position).getCafeSmokeState().equals("Y");

                case 4:
                    return flag = getItem(position).getCafeParkingState().equals("Y");

            }
            return flag;
        }

        /**
         * Call Cafe Phone
         * @param phoneNum
         */
        private void CallPhone(String phoneNum){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNum));
            startActivity(intent);
        }

        private boolean isOpenState(int position){
            int day = cal.get(Calendar.DAY_OF_WEEK);    //일요일 -> 1, 월요일 -> 2...
            int today_hour = Integer.parseInt(dayTime.format(new Date(time)).substring(11,13));    //오늘 현재 시간
            int open_hour, close_hour;    //카페 오픈, 마감 시간
            String cafe_open, cafe_close, timeState;
            //Log.d("cafeTime", "today_hour : "+today_hour);
            if((day == 1) || (day == 7)){
                //주말
                cafe_open = getItem(position).getCafeWeekendOpenTime();
                cafe_close = getItem(position).getCafeWeekendCloseTime();
                timeState = cafe_close.substring(0,2);

                open_hour = Integer.parseInt(cafe_open.substring(3,5));
                close_hour = (timeState.equals("AM")) ? Integer.parseInt(cafe_close.substring(3,5)) + 24 : Integer.parseInt(cafe_close.substring(3,5));
                /*
                Log.d("cafeTime","주말");
                Log.d("cafeTime", "open_hour : "+open_hour);
                Log.d("cafeTime", "close_hour : "+close_hour);
                */
            }else{
                //주중
                cafe_open = getItem(position).getCafeWeekDaysOpenTime();
                cafe_close = getItem(position).getCafeWeekDaysCloseTime();
                timeState = cafe_close.substring(0,2);

                open_hour = Integer.parseInt(cafe_open.substring(3,5));
                close_hour = (timeState.equals("AM")) ? Integer.parseInt(cafe_close.substring(3,5)) + 24 : Integer.parseInt(cafe_close.substring(3,5));
                /*
                Log.d("cafeTime","주중");
                Log.d("cafeTime", "open_hour : "+open_hour);
                Log.d("cafeTime", "close_hour : "+close_hour);
                */
            }

            return ((today_hour >= open_hour) && (today_hour <= close_hour));
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

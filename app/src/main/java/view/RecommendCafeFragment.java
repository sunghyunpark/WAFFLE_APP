package view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import api.ApiClient;
import api.ApiInterface;
import api.response.CafeResponse;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CafeModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecommendCafeFragment extends Fragment {


    private ArrayList<CafeModel> listItems;
    private Calendar cal;
    private long time;
    private SimpleDateFormat dayTime;
    private int position;

    @BindView(R.id.cafe_item_layout) ViewGroup cafe_item_layout;
    @BindView(R.id.cafe_thumb_img) ImageView cafe_thumb_iv;
    @BindView(R.id.full_time_img) ImageView full_time_iv;
    @BindView(R.id.wifi_img) ImageView wifi_iv;
    @BindView(R.id.smoke_img) ImageView smoke_iv;
    @BindView(R.id.parking_img) ImageView parking_iv;
    @BindView(R.id.cafe_name_txt) TextView cafe_name_txt;
    @BindView(R.id.cafe_open_state_layout) ViewGroup cafe_open_state_layout;
    @BindView(R.id.cafe_open_state_txt) TextView cafe_open_state_tv;
    @BindView(R.id.weekdays_open_close_time_txt) TextView weekdays_open_close_time_tv;
    @BindView(R.id.weekend_open_close_time_txt) TextView weekend_open_close_time_tv;

    @BindString(R.string.cafe_info_weekdays_time_txt) String cafeInfoWeekdaysTimeStr;
    @BindString(R.string.cafe_info_weekend_time_txt) String cafeInfoWeekendTimeStr;
    @BindString(R.string.cafe_open_state_txt) String cafeOpenStateStr;
    @BindString(R.string.cafe_close_state_txt) String cafeCloseStateStr;

    View v;

    public static RecommendCafeFragment create(int position) {
        RecommendCafeFragment fragment = new RecommendCafeFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_recommend_cafe, container, false);

        ButterKnife.bind(this, v);

        init();

        LoadRecommendCafeList();

        return v;
    }

    private void init(){
        listItems = new ArrayList<CafeModel>();
        cal = Calendar.getInstance();
        time = System.currentTimeMillis();
        dayTime = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss a");
    }

    private void setData(){

        //Glide Options
        RequestOptions requestOptions = new RequestOptions();
        //requestOptions.placeholder(R.mipmap.not_cafe_img);
        //requestOptions.error(R.mipmap.not_cafe_img);
        requestOptions.centerCrop();

        Glide.with(getActivity())
                .setDefaultRequestOptions(requestOptions)
                .load(WaffleApplication.SERVER_BASE_PATH+listItems.get(position).getCafeThumbnail())
                .into(cafe_thumb_iv);

        cafe_name_txt.setText(listItems.get(position).getCafeName());

        weekdays_open_close_time_tv.setText(cafeInfoWeekdaysTimeStr + " "
                +listItems.get(position).getCafeWeekDaysOpenTime() + " ~ "
                +listItems.get(position).getCafeWeekDaysCloseTime());
        weekend_open_close_time_tv.setText(cafeInfoWeekendTimeStr + " "
                + listItems.get(position).getCafeWeekendOpenTime() + " ~ "
                + listItems.get(position).getCafeWeekendCloseTime());

        if(isOpenState(position)){
            //open
            cafe_open_state_layout.setBackgroundResource(R.drawable.cafe_open_state_shape);
            cafe_open_state_tv.setText(cafeOpenStateStr);
            cafe_open_state_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        }else{
            //close
            cafe_open_state_layout.setBackgroundResource(R.drawable.cafe_close_state_shape);
            cafe_open_state_tv.setText(cafeCloseStateStr);
            cafe_open_state_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGray));
        }

        if(cafeFeature(position, 1)){
            full_time_iv.setBackgroundResource(R.mipmap.cafe_full_time_img);
        }else{
            full_time_iv.setBackgroundResource(R.mipmap.cafe_not_full_time_img);
        }
        if(cafeFeature(position, 2)){
            wifi_iv.setBackgroundResource(R.mipmap.cafe_wifi_img);
        }else{
            wifi_iv.setBackgroundResource(R.mipmap.cafe_not_wifi_img);
        }
        if(cafeFeature(position, 3)){
            smoke_iv.setBackgroundResource(R.mipmap.cafe_smoke_img);
        }else{
            smoke_iv.setBackgroundResource(R.mipmap.cafe_not_smoke_img);
        }
        if(cafeFeature(position, 4)){
            parking_iv.setBackgroundResource(R.mipmap.cafe_parking_img);
        }else{
            parking_iv.setBackgroundResource(R.mipmap.cafe_not_parking_img);
        }

        cafe_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutCafeActivity.class);
                intent.putExtra("isData", "N");
                intent.putExtra("cafe_id", listItems.get(position).getCafeId());
                startActivity(intent);
            }
        });


    }

    private void LoadRecommendCafeList(){
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
                        listItems.add(cafeResponse.getCafeList().get(i));
                        Log.d("recommend", cafeResponse.getCafeList().get(i).getCafeName());
                        Log.d("recommend", cafeResponse.getCafeList().get(i).getCafeWeekDaysOpenTime());
                    }
                    setData();
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
     * Cafe Feature 분기처리
     * @param position -> Item position
     * @param featureNo -> Feature Number(full time/wifi/smoke/parking)
     * @return
     */
    private boolean cafeFeature(int position, int featureNo){
        boolean flag = false;
        switch (featureNo){
            case 1:
                return flag = listItems.get(position).getCafeFullTimeState().equals("Y");

            case 2:
                return flag = listItems.get(position).getCafeWifiState().equals("Y");

            case 3:
                return flag = listItems.get(position).getCafeSmokeState().equals("Y");

            case 4:
                return flag = listItems.get(position).getCafeParkingState().equals("Y");

        }
        return flag;
    }

    private boolean isOpenState(int position){
        int day = cal.get(Calendar.DAY_OF_WEEK);    //일요일 -> 1, 월요일 -> 2...
        int today_hour = Integer.parseInt(dayTime.format(new Date(time)).substring(11,13));    //오늘 현재 시간
        int open_hour, close_hour;    //카페 오픈, 마감 시간
        String cafe_open, cafe_close, timeState;
        //Log.d("cafeTime", "today_hour : "+today_hour);
        if((day == 1) || (day == 7)){
            //주말
            cafe_open = listItems.get(position).getCafeWeekendOpenTime();
            cafe_close = listItems.get(position).getCafeWeekendCloseTime();
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
            cafe_open = listItems.get(position).getCafeWeekDaysOpenTime();
            cafe_close = listItems.get(position).getCafeWeekDaysCloseTime();
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


}

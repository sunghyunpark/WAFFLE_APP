package view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CafeModel;

public class HotFragment extends Fragment {

    private ArrayList<CafeModel> listItems;
    RecyclerAdapter adapter;
    @BindView(R.id.recomment_cafe_recyclerView) RecyclerView recyclerView;
    @BindString(R.string.cafe_info_weekdays_time_txt) String cafeInfoWeekdaysTimeStr;
    @BindString(R.string.cafe_info_weekend_time_txt) String cafeInfoWeekendTimeStr;
    @BindString(R.string.cafe_open_state_txt) String cafeOpenStateStr;
    @BindString(R.string.cafe_close_state_txt) String cafeCloseStateStr;
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
        listItems = new ArrayList<CafeModel>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // 기본값이 VERTICAL
        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_HEADER = 1;

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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_recommend_cafe_item, parent, false);
                return new RecyclerAdapter.Cafe_VH(v);
            }else if(viewType == TYPE_HEADER){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_recommend_cafe_header, parent, false);
                return new RecyclerAdapter.Header_Vh(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private CafeModel getItem(int position) {
            return listItems.get(position-1);
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

                Glide.with(getActivity())
                        .setDefaultRequestOptions(requestOptions)
                        .load(WaffleApplication.SERVER_BASE_PATH+currentItem.getCafeThumbnail())
                        .into(VHitem.cafeThumbnail);

                VHitem.cafe_weekdays_open_close_time_tv.setText(cafeInfoWeekdaysTimeStr + " " +currentItem.getCafeWeekDaysOpenTime() + " ~ "+currentItem.getCafeWeekDaysCloseTime());
                VHitem.cafe_weekend_open_close_time_tv.setText(cafeInfoWeekendTimeStr + " " + currentItem.getCafeWeekendOpenTime() + " ~ "+currentItem.getCafeWeekendCloseTime());

                VHitem.cafe_distance_tv.setText(currentItem.getCafeDistance().substring(0,4)+" Km");

                if(isOpenState(position)){
                    //open
                    VHitem.cafe_open_state_layout.setBackgroundResource(R.drawable.cafe_open_state_shape);
                    VHitem.cafe_open_state_tv.setText(cafeOpenStateStr);
                    VHitem.cafe_open_state_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                }else{
                    //close
                    VHitem.cafe_open_state_layout.setBackgroundResource(R.drawable.cafe_close_state_shape);
                    VHitem.cafe_open_state_tv.setText(cafeCloseStateStr);
                    VHitem.cafe_open_state_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGray));
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
                        Intent intent = new Intent(getActivity(), AboutCafeActivity.class);
                        intent.putExtra("isData", "Y");
                        intent.putExtra("CafeModel", getItem(position));
                        startActivity(intent);
                    }
                });


            }else if(holder instanceof RecyclerAdapter.Header_Vh){

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
            TextView cafe_distance_tv;
            //TextView cafe_day_off_tv;
            ViewGroup cafe_open_state_layout;
            TextView cafe_open_state_tv;
            ImageView full_time_img;
            ImageView wifi_img;
            ImageView smoke_img;
            ImageView parking_img;
            ViewGroup cafe_item_layout;

            private Cafe_VH(View itemView){
                super(itemView);
                cafeName = (TextView)itemView.findViewById(R.id.cafe_name_txt);
                cafeThumbnail = (ImageView)itemView.findViewById(R.id.cafe_thumb_img);
                cafe_weekdays_open_close_time_tv = (TextView)itemView.findViewById(R.id.weekdays_open_close_time_txt);
                cafe_weekend_open_close_time_tv = (TextView)itemView.findViewById(R.id.weekend_open_close_time_txt);
                cafe_distance_tv = (TextView)itemView.findViewById(R.id.distance_txt);
                //cafe_day_off_tv = (TextView)itemView.findViewById(R.id.cafe_day_off_txt);
                cafe_open_state_layout = (ViewGroup)itemView.findViewById(R.id.cafe_open_state_layout);
                cafe_open_state_tv = (TextView)itemView.findViewById(R.id.cafe_open_state_txt);
                full_time_img = (ImageView)itemView.findViewById(R.id.full_time_img);
                wifi_img = (ImageView)itemView.findViewById(R.id.wifi_img);
                smoke_img = (ImageView)itemView.findViewById(R.id.smoke_img);
                parking_img = (ImageView)itemView.findViewById(R.id.parking_img);
                cafe_item_layout = (ViewGroup)itemView.findViewById(R.id.cafe_item_layout);
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
         * 상단 헤더
         */
        private class Header_Vh extends RecyclerView.ViewHolder{
            private Header_Vh(View itemView){
                super(itemView);

            }
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


        private boolean isPositionHeader(int position){
            return position == 0;
        }

        @Override
        public int getItemViewType(int position) {
            if(isPositionHeader(position)){
                return TYPE_HEADER;
            }else{
                return TYPE_ITEM;
            }
        }
        //increasing getItemcount to 1. This will be the row of header.
        @Override
        public int getItemCount() {
            return listItems.size()+1;
        }
    }

}

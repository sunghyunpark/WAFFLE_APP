package view;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import model.CafeModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    View v;
    //리사이클러뷰
    RecyclerAdapter adapter;
    private ArrayList<CafeModel> listItems;
    //내 위치 정보
    public static double my_latitude = 0;
    public static double my_longitude = 0;
    GpsInfo gps;
    private static final int LOAD_DATA_COUNT = 5;
    private static final int REQUEST_CODE_LOCATION = 10;
    private String lastCafeId = "0";
    @BindView(R.id.swipe_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindString(R.string.cafe_info_weekdays_time_txt) String cafeInfoWeekdaysTimeStr;
    @BindString(R.string.cafe_info_weekend_time_txt) String cafeInfoWeekendTimeStr;
    @BindString(R.string.cafe_open_state_txt) String cafeOpenStateStr;
    @BindString(R.string.cafe_close_state_txt) String cafeCloseStateStr;
    @BindString(R.string.network_error_txt) String networkErrorStr;


    @Override
    public void onRefresh() {
        //새로고침시 이벤트 구현
        lastCafeId = "0";
        initView();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_cafe_list, container, false);
        ButterKnife.bind(this, v);

        initView();

        return v;
    }

    private void initView(){
        //recyclerview 초기화
        listItems = new ArrayList<CafeModel>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new RecyclerAdapter(listItems);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this);

        setGPS();

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...
                //Toast.makeText(getActivity(),"불러오는중...", Toast.LENGTH_SHORT).show();
                LoadCafeList(my_latitude, my_longitude, lastCafeId);

            }
        });
    }

    private void setGPS(){
        // 사용자의 위치 수신을 위한 세팅 //
        gps = new GpsInfo(getContext());

        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            my_latitude = gps.getLatitude();
            my_longitude = gps.getLongitude();
            if((my_latitude==0.0) || (my_longitude == 0.0)){
                Toast.makeText(getContext(),"정보를 불러오고 있습니다. 다시 새로고침 해주세요.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(
                        getContext(),
                        "당신의 위치 - \n위도: " + my_latitude + "\n경도: " + my_longitude,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            // GPS 를 사용할수 없으므로
            //gps.showSettingsAlert();
            alertCheckGPS();
        }
    }

    private void alertCheckGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("GPS를 설정하시면 거리가 가까운 순으로 카페들을 볼 수 있어요!")
                .setCancelable(false)
                .setPositiveButton("GPS 사용",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveConfigGPS();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // GPS 설정화면으로 이동
    private void moveConfigGPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

    private void LoadCafeList(Double lati, Double lon, final String last_cafe_id){
        //Log.d("LoadCafeList", "lati : "+lati+"\nlon : "+lon);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<CafeResponse> call = apiService.GetCafeListFromMyLocation("cafe_list_from_user_location", "0", lati, lon, last_cafe_id);
        call.enqueue(new Callback<CafeResponse>() {
            @Override
            public void onResponse(Call<CafeResponse> call, Response<CafeResponse> response) {
                CafeResponse cafeResponse = response.body();
                if(!cafeResponse.isError()){
                    recyclerView.setVisibility(View.VISIBLE);
                    int listSize = cafeResponse.getCafeList().size();
                    for (int i=0;i<listSize;i++){
                        listItems.add(cafeResponse.getCafeList().get(i));
                    }
                    lastCafeId = cafeResponse.getLast_cafe_id();
                }else{

                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<CafeResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("tag", t.toString());
                Toast.makeText(getActivity(), networkErrorStr,Toast.LENGTH_SHORT).show();
            }
        });
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_tab1_item, parent, false);
                return new Cafe_VH(v);
            }else if(viewType == TYPE_HEADER){
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_tab1_header, parent, false);
                return new Header_Vh(v);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        private CafeModel getItem(int position) {
            return listItems.get(position-1);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof Cafe_VH) {
                final CafeModel currentItem = getItem(position);
                final Cafe_VH VHitem = (Cafe_VH)holder;

                VHitem.cafeName.setText(currentItem.getCafeName());

                //Glide Options
                RequestOptions requestOptions = new RequestOptions();
                //requestOptions.placeholder(R.mipmap.not_cafe_img);
                //requestOptions.error(R.mipmap.not_cafe_img);
                requestOptions.circleCrop();    //circle

                Glide.with(getActivity())
                        .setDefaultRequestOptions(requestOptions)
                        .load(WaffleApplication.SERVER_BASE_PATH+currentItem.getCafeThumbnail())
                        .into(VHitem.cafeThumbnail);

                VHitem.cafe_weekdays_open_close_time_tv.setText(cafeInfoWeekdaysTimeStr + " " +currentItem.getCafeWeekDaysOpenTime() + " ~ "+currentItem.getCafeWeekDaysCloseTime());
                VHitem.cafe_weekend_open_close_time_tv.setText(cafeInfoWeekendTimeStr + " " + currentItem.getCafeWeekendOpenTime() + " ~ "+currentItem.getCafeWeekendCloseTime());

                VHitem.cafe_distance_tv.setText(currentItem.getCafeDistance().substring(0,4)+" Km");

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
                    VHitem.cafe_open_state_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                }else{
                    //close
                    VHitem.cafe_open_state_layout.setBackgroundResource(R.drawable.cafe_close_state_shape);
                    VHitem.cafe_open_state_tv.setText(cafeCloseStateStr);
                    VHitem.cafe_open_state_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorGray));
                }

                if(cafeFeature(position, 1)){
                    VHitem.full_time_img.setBackgroundResource(R.mipmap.ic_launcher);
                }else{
                    VHitem.full_time_img.setBackgroundResource(R.mipmap.ic_launcher);
                }
                if(cafeFeature(position, 2)){
                    VHitem.wifi_img.setBackgroundResource(R.mipmap.ic_launcher);
                }else{
                    VHitem.wifi_img.setBackgroundResource(R.mipmap.ic_launcher);
                }
                if(cafeFeature(position, 3)){
                    VHitem.smoke_img.setBackgroundResource(R.mipmap.ic_launcher);
                }else{
                    VHitem.smoke_img.setBackgroundResource(R.mipmap.ic_launcher);
                }
                if(cafeFeature(position, 4)){
                    VHitem.parking_img.setBackgroundResource(R.mipmap.ic_launcher);
                }else{
                    VHitem.parking_img.setBackgroundResource(R.mipmap.ic_launcher);
                }

                VHitem.cafe_item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent intent = new Intent(getActivity(), AboutCafeActivity.class);
                        //intent.putExtra("isData", "Y");
                        //intent.putExtra("CafeModel", getItem(position));
                        //startActivity(intent);
                    }
                });


            }else if(holder instanceof Header_Vh){

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

            private Cafe_VH(View itemView){
                super(itemView);
                cafeName = (TextView)itemView.findViewById(R.id.cafe_name_txt);
                cafeThumbnail = (ImageView)itemView.findViewById(R.id.cafe_thumb_img);
                cafe_weekdays_open_close_time_tv = (TextView)itemView.findViewById(R.id.weekdays_open_close_time_txt);
                cafe_weekend_open_close_time_tv = (TextView)itemView.findViewById(R.id.weekend_open_close_time_txt);
                cafe_distance_tv = (TextView)itemView.findViewById(R.id.distance_txt);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION:
                //권한이 있는 경우
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                }
                //권한이 없는 경우
                else {
                    Toast.makeText(getActivity(), "퍼미션을 허용해야 이용할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    public class GpsInfo extends Service implements LocationListener {

        private final Context mContext;
        private static final int REQUEST_CODE_LOCATION = 10;
        // 현재 GPS 사용유무
        boolean isGPSEnabled = false;

        // 네트워크 사용유무
        boolean isNetworkEnabled = false;

        // GPS 상태값
        boolean isGetLocation = false;

        Location location;
        double lat; // 위도
        double lon; // 경도

        // 최소 GPS 정보 업데이트 거리 10미터
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

        // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

        protected LocationManager locationManager;

        public GpsInfo(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 사용자 권한 요청
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            }else{
                try {
                    locationManager = (LocationManager) mContext
                            .getSystemService(LOCATION_SERVICE);

                    // GPS 정보 가져오기
                    isGPSEnabled = locationManager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);

                    // 현재 네트워크 상태 값 알아오기
                    isNetworkEnabled = locationManager
                            .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (!isGPSEnabled && !isNetworkEnabled) {
                        // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
                    } else {
                        this.isGetLocation = true;
                        // 네트워크 정보로 부터 위치값 가져오기
                        if (isNetworkEnabled) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    // 위도 경도 저장
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                }
                            }
                        }

                        if (isGPSEnabled) {
                            if (location == null) {
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                                if (locationManager != null) {
                                    location = locationManager
                                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    if (location != null) {
                                        lat = location.getLatitude();
                                        lon = location.getLongitude();
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return location;
        }

        /**
         * GPS 종료
         * */
        public void stopUsingGPS(){
            if(locationManager != null){
                locationManager.removeUpdates(GpsInfo.this);
            }
        }

        /**
         * 위도값을 가져옵니다.
         * */
        public double getLatitude(){
            if(location != null){
                lat = location.getLatitude();
            }
            return lat;
        }

        /**
         * 경도값을 가져옵니다.
         * */
        public double getLongitude(){
            if(location != null){
                lon = location.getLongitude();
            }
            return lon;
        }

        /**
         * GPS 나 wife 정보가 켜져있는지 확인합니다.
         * */
        public boolean isGetLocation() {
            return this.isGetLocation;
        }

        /**
         * GPS 정보를 가져오지 못했을때
         * 설정값으로 갈지 물어보는 alert 창
         * */
        public void showSettingsAlert(){
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mContext);

            alertDialog.setTitle("GPS 사용유무셋팅");
            alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

            // OK 를 누르게 되면 설정창으로 이동합니다.
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mContext.startActivity(intent);
                        }
                    });
            // Cancle 하면 종료 합니다.
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }

        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }
    }
}

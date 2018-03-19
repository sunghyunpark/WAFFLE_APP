package view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cafemobile.waffle.R;

import java.util.ArrayList;

import api.ApiClient;
import api.ApiInterface;
import api.response.CafeResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.CafeModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HotFragment extends Fragment {

    @BindView(R.id.recommend_pager) ViewPager recommendPager;
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
                        Log.d("recommend", cafeResponse.getCafeList().get(i).getCafeName());
                        Log.d("recommend", cafeResponse.getCafeList().get(i).getCafeWeekDaysOpenTime());
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

}

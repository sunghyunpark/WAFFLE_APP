package view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cafemobile.waffle.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;


public class HotFragment extends Fragment {

    @BindView(R.id.recommend_pager) ViewPager recommendPager;
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

        PagerAdapter mPagerAdapter = new PagerAdapter(getFragmentManager());
        float density = getResources().getDisplayMetrics().density;
        int pageMargin = 8 * (int)density; // 8dp

        recommendPager.setPageMargin(pageMargin);
        recommendPager.setClipToPadding(false);
        recommendPager.setPadding(80, 0, 80, 0);
        recommendPager.setAdapter(mPagerAdapter);

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        @Override
        public Fragment getItem(int position) {
            // 해당하는 page의 Fragment를 생성합니다.
            return RecommendCafeFragment.create(position);
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

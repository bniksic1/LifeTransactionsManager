package ba.unsa.etf.rma.rma20niksicbenjamin63.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import ba.unsa.etf.rma.rma20niksicbenjamin63.R;
import ba.unsa.etf.rma.rma20niksicbenjamin63.adapters.TransactionScreenSlideAdapter;

public class OnePaneFragment extends Fragment{
    private ViewPager viewPager;
    private static TransactionScreenSlideAdapter screenSlideAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onepane, container, false);
        viewPager = view.findViewById(R.id.pager);
        screenSlideAdapter = new TransactionScreenSlideAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(screenSlideAdapter);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                    viewPager.setCurrentItem(3);
                else if(position == 4)
                    viewPager.setCurrentItem(1);
                else if(position == 3)
                    Toast.makeText(getActivity().getApplicationContext(),"Graph takes date which is set on primary page",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    public static void refreshAdapter(){
        screenSlideAdapter.notifyDataSetChanged();
    }

    public static TransactionScreenSlideAdapter getScreenSlideAdapter() {
        return screenSlideAdapter;
    }
}

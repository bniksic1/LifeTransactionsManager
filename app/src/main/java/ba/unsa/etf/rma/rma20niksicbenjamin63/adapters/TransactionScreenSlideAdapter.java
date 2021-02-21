package ba.unsa.etf.rma.rma20niksicbenjamin63.adapters;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ba.unsa.etf.rma.rma20niksicbenjamin63.fragments.BudgetFragment;
import ba.unsa.etf.rma.rma20niksicbenjamin63.fragments.GraphsFragment;
import ba.unsa.etf.rma.rma20niksicbenjamin63.fragments.TransactionListFragment;

public class TransactionScreenSlideAdapter extends FragmentStatePagerAdapter {
    public TransactionScreenSlideAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position){
            case 1:
                fragment = new TransactionListFragment();
                break;
            case 2:
                fragment = new BudgetFragment();
                break;
            case 3:
                fragment = new GraphsFragment();
                break;
            default:
                fragment = new Fragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    // ovo je dodano zbog modifikacija i automatskog update drugog fragmenta kada se promijeni budget/transakcija jer notifydatasetchanged poziva ovu funkciju
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int getItemPosition(Object object){
        if (object instanceof TransactionListFragment)
            ((TransactionListFragment) object).update();
        else if(object instanceof BudgetFragment)
            ((BudgetFragment) object).update();
        else if(object instanceof GraphsFragment)
            ((GraphsFragment) object).update();
        return super.getItemPosition(object);
    }
}

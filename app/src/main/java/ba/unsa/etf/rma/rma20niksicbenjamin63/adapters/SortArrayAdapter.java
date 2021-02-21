package ba.unsa.etf.rma.rma20niksicbenjamin63.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import ba.unsa.etf.rma.rma20niksicbenjamin63.R;

public class SortArrayAdapter extends ArrayAdapter<String> implements SpinnerAdapter {
    LayoutInflater flater;

    public SortArrayAdapter(Activity context, int resouceId, int textviewId, List<String> list){

        super(context,resouceId,textviewId, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return type(convertView,position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return type(convertView,position);
    }

    private View type(View convertView , int position){

        String type = getItem(position);

        viewHolder holder ;
        View view = convertView;
        if (view==null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = flater.inflate(R.layout.custom_layout, null, false);

            holder.txtTitle = view.findViewById(R.id.title);
            holder.imageView = view.findViewById(R.id.ikonica);
            view.setTag(holder);
        }else{
            holder = (viewHolder) view.getTag();
        }
        holder.imageView.setImageResource(getImageId(position));
        holder.txtTitle.setText(type.toString());

        return view;
    }

    private class viewHolder {
        TextView txtTitle;
        ImageView imageView;
    }

    private int getImageId(int type){
        switch(type){
            case 1:
                return R.drawable.pa;
            case 2:
                return R.drawable.pd;
            case 3:
                return R.drawable.ta;
            case 4:
                return R.drawable.td;
            case 5:
                return R.drawable.da;
            case 6:
                return R.drawable.dd;
            default:
                return R.drawable.sorticon;
        }
    }
}

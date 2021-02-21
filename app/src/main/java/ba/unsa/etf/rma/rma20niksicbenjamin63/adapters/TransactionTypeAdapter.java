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
import ba.unsa.etf.rma.rma20niksicbenjamin63.data.Transaction;

public class TransactionTypeAdapter extends ArrayAdapter<Transaction.TYPE> implements SpinnerAdapter {
    LayoutInflater flater;

    public TransactionTypeAdapter(Activity context, int resouceId, int textviewId, List<Transaction.TYPE> list){

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

        Transaction.TYPE type = getItem(position);

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
        holder.imageView.setImageResource(getImageId(type));
        holder.txtTitle.setText(type.toString());

        return view;
    }

    private class viewHolder {
        TextView txtTitle;
        ImageView imageView;
    }

    private int getImageId(Transaction.TYPE type){
        switch(type){
            case REGULARPAYMENT:
                return R.drawable.regularpayment;
            case REGULARINCOME:
                return R.drawable.regularincome;
            case PURCHASE:
                return R.drawable.purchase;
            case INDIVIDUALINCOME:
                return R.drawable.individualincome;
            case INDIVIDUALPAYMENT:
                return R.drawable.individualpayment;
            default:
                return R.drawable.anytype;
        }
    }
}

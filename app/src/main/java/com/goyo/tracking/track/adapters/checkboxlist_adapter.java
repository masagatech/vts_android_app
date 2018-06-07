package com.goyo.tracking.track.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.goyo.tracking.track.R;
import com.goyo.tracking.track.model.model_checkboxlist;

import java.util.ArrayList;

/**
 * Created by llc on 12/4/2017.
 */

public class checkboxlist_adapter extends BaseAdapter {

    private ArrayList<model_checkboxlist> dataSet;
    Context mContext;

    public checkboxlist_adapter(ArrayList<model_checkboxlist> data, Context context) {

        this.dataSet = data;
        this.mContext = context;

    }



    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        CheckBox checkBox;
    }


    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public model_checkboxlist getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_checkboxlist, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            result=convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        model_checkboxlist item = getItem(position);


        viewHolder.txtName.setText(item.name);
        viewHolder.checkBox.setChecked(item.checked);


        return result;
    }
}

package com.goyo.tracking.tracking.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.model.ddl_model;
import com.goyo.tracking.tracking.model.vts_vh_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ddl_adapter extends BaseAdapter

{

    List<ddl_model> list;
    LayoutInflater inflater;
    Context context;
    Resources _rs;
    public boolean multicheck = false;

    public ddl_adapter(Context context, List<ddl_model> lst, Resources rs) {

        this.list = lst;
        this.context = context;
        this._rs = rs;
        this.inflater = LayoutInflater.from(context);
    }

    public void setDataSource(List<ddl_model> lst) {
        this.list.clear();
        this.list = lst;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ddl_adapter.MyViewHolder mViewHolder;
        ddl_model vh = this.list.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ddl_layout, parent, false);
            mViewHolder = new ddl_adapter.MyViewHolder(convertView);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ddl_adapter.MyViewHolder) convertView.getTag();
        }

        mViewHolder.txtTitle.setText(vh.Value);


        return convertView;
    }

    private class MyViewHolder {
        private TextView txtTitle;
        public MyViewHolder(View item) {

            txtTitle = (TextView) item.findViewById(R.id.txtTitle);
        }
    }


    public void clear() {
        this.list.clear();
        this.notifyDataSetChanged();
    }

}

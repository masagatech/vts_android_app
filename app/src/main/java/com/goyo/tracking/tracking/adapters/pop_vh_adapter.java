package com.goyo.tracking.tracking.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.model.history_item_model;
import com.goyo.tracking.tracking.model.vts_vh_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by llc on 11/20/2017.
 */


public class pop_vh_adapter extends BaseAdapter {

    List<vts_vh_model> list;
    LayoutInflater inflater;
    Context context;
    Resources _rs;
    public boolean multicheck = false;

    public pop_vh_adapter(Context context, List<vts_vh_model> lst, Resources rs) {

        this.list = lst;
        this.context = context;
        this._rs = rs;
        this.inflater = LayoutInflater.from(context);
    }

    public void setDataSource(List<vts_vh_model> lst) {
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
        pop_vh_adapter.MyViewHolder mViewHolder;
        vts_vh_model vh = this.list.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_vh_item_popup, parent, false);
            mViewHolder = new pop_vh_adapter.MyViewHolder(convertView);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (pop_vh_adapter.MyViewHolder) convertView.getTag();
        }

        mViewHolder.txtTitle.setText(vh.vno);

        if (multicheck) {
            mViewHolder.chkCheck.setVisibility(View.VISIBLE);

        }
        mViewHolder.chkCheck.setChecked(vh.ischeck);
        return convertView;
    }

    public HashMap<String, String> vhsIndex = new HashMap<String, String>();

    public String[] getCheckdVehicle() {
        ArrayList<String> vhlist = new ArrayList<String>();

        for (int i = 0; i < this.list.size(); i++) {
            vts_vh_model vhmod = this.list.get(i);
            if (vhsIndex.size() != this.list.size()) {
                vhsIndex.put(vhmod.vhid, vhmod.vno);
            }
            if (vhmod.ischeck) {
                vhlist.add(vhmod.vhid);
            }
        }
        return vhlist.toArray(new String[0]);
    }


    public String[] getCheckdVehicleVTSId() {
        ArrayList<String> vhlist = new ArrayList<String>();

        for (int i = 0; i < this.list.size(); i++) {
            vts_vh_model vhmod = this.list.get(i);
            if (vhmod.ischeck) {
                vhlist.add(vhmod.id + "");
            }
        }
        return vhlist.toArray(new String[0]);
    }

    private class MyViewHolder {
        private TextView txtTitle;
        private CheckBox chkCheck;

        public MyViewHolder(View item) {

            txtTitle = (TextView) item.findViewById(R.id.txtTitle);
            chkCheck = (CheckBox) item.findViewById(R.id.chkcheck);
        }
    }

    public String getVehicleNo(String imei) {
        return vhsIndex.get(imei);
    }

    public void clear() {
        this.list.clear();
        this.notifyDataSetChanged();
    }

}

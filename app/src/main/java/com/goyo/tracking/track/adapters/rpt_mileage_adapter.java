package com.goyo.tracking.track.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goyo.tracking.track.R;
import com.goyo.tracking.track.model.rpt_mileage_model;
import com.goyo.tracking.track.model.vts_vh_model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by llc on 12/5/2017.
 */

public class rpt_mileage_adapter extends BaseAdapter {

    List<rpt_mileage_model> list;
    LayoutInflater inflater;
    Context context;
    Resources _rs;
    DateFormat dispformat = new SimpleDateFormat("dd-MMM-yy");

    DateFormat gpsdtfromat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public rpt_mileage_adapter(Context context, List<rpt_mileage_model> lst, Resources rs) {
        gpsdtfromat.setTimeZone(TimeZone.getTimeZone("IST"));
        this.list = lst;
        this.context = context;
        this._rs = rs;
        this.inflater = LayoutInflater.from(context);
    }

    public void setDataSource(List<rpt_mileage_model> lst) {
        this.list = lst;
    }
    public HashMap<String, String> vhad;

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
        rpt_mileage_adapter.MyViewHolder mViewHolder;
        rpt_mileage_model vh = this.list.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_milege_rpt_row, parent, false);
            mViewHolder = new rpt_mileage_adapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (rpt_mileage_adapter.MyViewHolder) convertView.getTag();
        }

        if (vhad != null && vh.vno == null) {
            vh.vno = vhad.get(vh.id.vhid);
        }
        mViewHolder.txtVhno.setText(vh.vno);

        try {
            Date frmdt = gpsdtfromat.parse(vh.frmdate);
            Date todt = gpsdtfromat.parse(vh.todate);
            mViewHolder.txtfrmdt.setText(dispformat.format(frmdt));
            mViewHolder.txttodt.setText(dispformat.format(todt));
        } catch (Exception ex) {

        }
        mViewHolder.txtVhno.setText(vh.vno);
        //mViewHolder.txtIMEI.setText(vh.id.vhid);

        mViewHolder.txtmileage.setText(String.format("%.2f", vh.milege));

        return convertView;
    }


    private class MyViewHolder {
        private TextView txtVhno, txtfrmdt, txttodt, txtmileage, txtIMEI;

        public MyViewHolder(View item) {

            txtVhno = (TextView) item.findViewById(R.id.txtVhno);
            txtfrmdt = (TextView) item.findViewById(R.id.txtfrmdt);
            txttodt = (TextView) item.findViewById(R.id.txttodt);
            txtmileage = (TextView) item.findViewById(R.id.txtmileage);
            //txtIMEI = (TextView) item.findViewById(R.id.txtIMEI);
        }
    }

    public void clear() {
        this.list.clear();
        this.notifyDataSetChanged();
    }
}

package com.goyo.tracking.tracking.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.model.history_item_model;
import com.goyo.tracking.tracking.model.vts_vh_model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by llc on 11/17/2017.
 */

public class vh_history_tripline extends BaseAdapter {

    List<history_item_model> list;
    LayoutInflater inflater;
    Context context;
    Resources _rs;
    DecimalFormat precision = new DecimalFormat("0.00");
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    DateFormat dispformat = new SimpleDateFormat("hh:mm a");
    //TimeZone timeZone = TimeZone.getTimeZone("+530");

    Drawable solid_sqr, dashed_sqr;
    Integer driving, stop;

    public vh_history_tripline(Context context, List<history_item_model> lst, Resources rs) {

        this.list = lst;

        this.context = context;
        this._rs = rs;

        format.setTimeZone(TimeZone.getTimeZone("IST"));
       // dispformat.setTimeZone(timeZone);

        solid_sqr = _rs.getDrawable(R.drawable.ic_sqr_solid);
        dashed_sqr = _rs.getDrawable(R.drawable.ic_sqr_dashed);

        driving = _rs.getColor(R.color.driving);
        stop = _rs.getColor(R.color.stop);

        this.inflater = LayoutInflater.from(context);
    }

    public void setDataSource(List<history_item_model> lst) {
        this.list.clear();
        this.list = lst;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public history_item_model getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        vh_history_tripline.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_timeline, parent, false);
            mViewHolder = new vh_history_tripline.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (vh_history_tripline.MyViewHolder) convertView.getTag();
        }
        history_item_model vh = this.list.get(position);
        mViewHolder.txtmaxspeed.setText("");
        if (vh.trktyp.equals("solid")) {
            mViewHolder.item_title.setText("DRIVING");
            mViewHolder.item_title.setTextColor(driving);
            mViewHolder.item_subtitle.setText(vh.dur + " - " + precision.format(vh.dist) + " km");
            mViewHolder.vh_square.setBackgroundDrawable(solid_sqr);
            mViewHolder.txtmaxspeed.setText("mx speed - " + vh.mxspd + " km/h");
        } else {
            mViewHolder.item_title.setText("STOP / NO INFO");
            mViewHolder.item_title.setTextColor(stop);
            mViewHolder.item_subtitle.setText(vh.dur);
            mViewHolder.vh_square.setBackgroundDrawable(dashed_sqr);

        }
        try {
            mViewHolder.item_Time.setText(dispformat.format(format.parse(vh.sttm)));

        } catch (ParseException e) {
            e.printStackTrace();
        }



        return convertView;
    }


    private class MyViewHolder {
        private TextView item_title, item_subtitle, item_Time,txtmaxspeed;
        private View vh_square;

        public MyViewHolder(View item) {

            //thisitem = (LinearLayout) item.findViewById(R.id.lstItem);
//            hidid = (TextView) item.findViewById(R.id.txtHidId);
            item_title = (TextView) item.findViewById(R.id.item_title);
            //acc = (RelativeLayout) item.findViewById(R.id.unread_messages_wrapper);
            item_subtitle = (TextView) item.findViewById(R.id.item_subtitle);
            vh_square = (View) item.findViewById(R.id.vh_square);
            item_Time = (TextView) item.findViewById(R.id.item_Time);
            txtmaxspeed = (TextView) item.findViewById(R.id.txtmaxspeed);
//            txtIcon = (TextView) item.findViewById(R.id.txtIcon);
        }
    }

    public void clear(){
        this.list.clear();
        this.notifyDataSetChanged();
    }
}
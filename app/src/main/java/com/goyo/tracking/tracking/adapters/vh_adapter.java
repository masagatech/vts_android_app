package com.goyo.tracking.tracking.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.model.vts_vh_model;

import java.util.List;

/**
 * Created by llc on 10/28/2017.
 */

public class vh_adapter extends BaseAdapter {

    List<vts_vh_model> list;
    LayoutInflater inflater;
    Context context;
    Resources _rs;

    public vh_adapter(Context context, List<vts_vh_model> lst, Resources rs) {

        this.list = lst;
        this.context = context;
        this._rs = rs;
        this.inflater = LayoutInflater.from(context);
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
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.vh_item_layout, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        vts_vh_model vh = this.list.get(position);

        mViewHolder.imgacc.setImageDrawable(null);
        if(vh.acc == null || vh.acc.equals("0")){
            mViewHolder.imgacc.setBackgroundDrawable(_rs.getDrawable(R.drawable.ic_action_offline));
        }else{
            mViewHolder.imgacc.setBackgroundDrawable(_rs.getDrawable(R.drawable.ic_action_online_acc));
        }

        if(vh.btr != null) {
            mViewHolder.imgbattry.setImageDrawable(null);
            if(vh.btrst.equals("CHRG")){
                mViewHolder.imgbattry.setImageDrawable(_rs.getDrawable(getBatrry(vh.btrst)));
            }else {
                mViewHolder.imgbattry.setImageDrawable(_rs.getDrawable(getBatrry(vh.btr)));
            }

        }
        if(vh.gsmsig != null){
            mViewHolder.imgnetwork.setImageDrawable(null);
            mViewHolder.imgnetwork.setImageDrawable(_rs.getDrawable(getNetwork(vh.gsmsig)));
        }

        mViewHolder.txtTitle.setText(vh.vno);

        return convertView;
    }


    private int getBatrry(String btry){
        switch (btry) {
            case "CHRG":
                return R.drawable.ic_action_btrychrg;
            case "100":
                return R.drawable.ic_action_btry100;
            case "70":
                return R.drawable.ic_action_btry80;
            case "50":
                return R.drawable.ic_action_btry50;
            case "30":
                return R.drawable.ic_action_btry20;
            case "10":
                return R.drawable.ic_action_btry20;
            case "1":
                return R.drawable.ic_action_btry0;
            case "0":
                return R.drawable.ic_action_btry0;
            default:
                return R.drawable.ic_action_btry0;

        }

    }


    private int getNetwork(String ntwrk){
        switch (ntwrk) {
            case "100":
                return R.drawable.ic_action_netw4;
            case "75":
                return R.drawable.ic_action_netw3;
            case "50":
                return R.drawable.ic_action_netw2;
            case "25":
                return R.drawable.ic_action_netw1;
            case "0":
                return R.drawable.ic_action_netw0;
            default:
                return R.drawable.ic_action_netw0;
        }

    }


    private class MyViewHolder {
        private TextView txtTitle, txtSubTitle, txtIcon, hidid, txtType, txtStatus, txtFromToTime, txtColor;
        private LinearLayout thisitem;
        private RelativeLayout acc;
        private ImageView imgnetwork, imgbattry, imgacc;

        public MyViewHolder(View item) {

            //thisitem = (LinearLayout) item.findViewById(R.id.lstItem);
//            hidid = (TextView) item.findViewById(R.id.txtHidId);
            txtTitle = (TextView) item.findViewById(R.id.header);
            //acc = (RelativeLayout) item.findViewById(R.id.unread_messages_wrapper);
             imgbattry =(ImageView)item.findViewById(R.id.imgbattry);
            imgnetwork =(ImageView)item.findViewById(R.id.imgnetwork);
            imgacc = (ImageView)item.findViewById(R.id.imgacc);
//            txtSubTitle = (TextView) item.findViewById(R.id.txtsubtitle);
//            txtIcon = (TextView) item.findViewById(R.id.txtIcon);
//            txtType = (TextView) item.findViewById(R.id.txtType);
//            txtStatus = (TextView) item.findViewById(R.id.txtStatus);
//            txtColor= (TextView) item.findViewById(R.id.txtColor);
//            txtFromToTime = (TextView) item.findViewById(R.id.txtFromToTime);
        }
    }



}

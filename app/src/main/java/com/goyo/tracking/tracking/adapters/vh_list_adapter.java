package com.goyo.tracking.tracking.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.enums.CarStatus;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.model.vts_vh_status_model;
import com.goyo.tracking.tracking.utils.vehicleres;

import java.util.ArrayList;
import java.util.List;

public class vh_list_adapter extends RecyclerView.Adapter<vh_list_adapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(vts_vh_model item);
    }

    private OnItemClickListener listener;


    public void setOnItemClickListner(OnItemClickListener _listner) {
        this.listener = _listner;
    }

    private List<vts_vh_model> list;
    private Resources _rs;
    private Context _c;

    public vh_list_adapter(List<vts_vh_model> lst, Resources rs, Context c) {
        this.list = lst;
        this._rs = rs;
        this._c = c;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_vehicle_list_master, parent, false);
        vh_list_adapter.MyViewHolder m = new vh_list_adapter.MyViewHolder(v);
        return m;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder hld, int position) {

        vts_vh_model vh = this.list.get(position);
        hld.bind(vh, listener);
        hld.imgimage.setImageResource(vehicleres.getVehIcon(vh.ico, CarStatus.Green));
        hld.txtTitle.setText(vh.vno);
//        if (vh.vhd != null)
        hld.txtSubtitle.setText(vh.regno.toUpperCase());

    }

    public void setUpdatesList(List<vts_vh_model> lst) {
        this.list = lst;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgimage;
        private TextView txtTitle, txtSubtitle;

        public MyViewHolder(View view) {
            super(view);

            imgimage = (ImageView) view.findViewById(R.id.imgVehicle);
            txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            txtSubtitle = (TextView) view.findViewById(R.id.txtSubtitle);


        }

        public void bind(final vts_vh_model item, final OnItemClickListener listener) {
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    listener.onItemClick(item);
//                }
//            });
        }
    }
}

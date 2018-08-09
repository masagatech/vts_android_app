package com.goyo.tracking.tracking.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.model.fuel_modal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class fuel_list_adapter extends RecyclerView.Adapter<fuel_list_adapter.MyViewHolder> {

    private List<fuel_modal> list;
    private Resources _rs;
    private Context _c;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat format = new SimpleDateFormat("dd\nMMM yy");
    public void setDataSource(List<fuel_modal> fuleentry) {
        this.list = fuleentry;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClick(fuel_modal item);
    }

    private fuel_list_adapter.OnItemClickListener listener;


    public void setOnItemClickListner(fuel_list_adapter.OnItemClickListener _listner) {
        this.listener = _listner;
    }


    public fuel_list_adapter(List<fuel_modal> lst, Resources rs, Context c) {
        this.list = lst;
        this._rs = rs;
        this._c = c;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_fuel_list, parent, false);
        fuel_list_adapter.MyViewHolder m = new fuel_list_adapter.MyViewHolder(v);
        return m;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder hld, int position) {
        fuel_modal vh = this.list.get(position);
        hld.bind(vh, listener);
        Date dt;
        try {
            dt = sdf.parse(vh.date);
            hld.txtDate.setText(format.format(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        hld.txtUOM.setText(String.format("%.1f " + vh.uom.toUpperCase() + " " + vh.fueltyp.toUpperCase(), vh.liter));
        hld.txtAmount.setText(String.format("Rs %.2f", vh.amount));
        hld.txtVhno.setText(vh.vhnm);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUOM, txtDate, txtAmount, txtVhno;

        public MyViewHolder(View view) {
            super(view);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtUOM = (TextView) view.findViewById(R.id.txtUOM);
            txtAmount = (TextView) view.findViewById(R.id.txtAmount);
            txtVhno = (TextView) view.findViewById(R.id.txtVhno);
        }

        public void bind(final fuel_modal item, final fuel_list_adapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}

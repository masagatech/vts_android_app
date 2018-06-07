package com.goyo.tracking.track.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.goyo.tracking.track.R;
import com.goyo.tracking.track.realmmodel.vehiclesettings_model;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class vh_settings_adapter extends RealmRecyclerViewAdapter<vehiclesettings_model, vh_settings_adapter.MyViewHolder> {
    private boolean onBind;

    public vh_settings_adapter(OrderedRealmCollection<vehiclesettings_model> data) {
        super(data, true);

    }

    private onItemClickLitner mListener;

    public interface onItemClickLitner {
        void onClick(String imei);
    }

    public void setOnItemClickLitner(onItemClickLitner listener) {
        mListener = listener;

    }


    private onSpeedAlertListner monSpeedAlertListner;

    public interface onSpeedAlertListner {
        void onClick(vehiclesettings_model vh, boolean checked);
    }

    public void setOnSpeedAlertListner(onSpeedAlertListner listener) {
        monSpeedAlertListner = listener;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_setting_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final vehiclesettings_model obj = getItem(position);
        holder.data = obj;
        holder.txtVhname.setText(obj.getVhregno());
        onBind = true;
        holder.chkisspeedAlert.setChecked(obj.isSpeedAlert());
        onBind = false;
        holder.txtVhname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(obj.getVhId());
            }
        });

        holder.chkisspeedAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!onBind) {
                    monSpeedAlertListner.onClick(obj, isChecked);
                }
            }
        });

    }

    public void selectAll(Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < getData().size(); i++) {
                    final int k = i;

                    vehiclesettings_model vs = getData().get(k);
                    vs.setSpeedAlert(true);
                    FirebaseMessaging.getInstance().subscribeToTopic("speed_" + vs.getImei());
                }


            }
        });
        notifyDataSetChanged();
    }

    public void desSelectAll(Realm rm) {
        rm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < getData().size(); i++) {
                    final int k = i;

                    vehiclesettings_model vs = getData().get(k);
                    vs.setSpeedAlert(false);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("speed_" + vs.getImei());
                }

            }
        });
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return index;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtVhname;
        CheckBox chkisspeedAlert;
        public vehiclesettings_model data;

        MyViewHolder(View view) {
            super(view);
            txtVhname = view.findViewById(R.id.txtvhname);

            chkisspeedAlert = view.findViewById(R.id.chkisspeedAlert);
        }
    }
}

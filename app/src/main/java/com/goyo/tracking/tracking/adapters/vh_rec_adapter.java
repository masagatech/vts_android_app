package com.goyo.tracking.tracking.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.enums.CarStatus;
import com.goyo.tracking.tracking.interfaces.VHOnStatusChangeListner;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.model.vts_vh_status_model;
import com.goyo.tracking.tracking.utils.deviceUtils;
import com.goyo.tracking.tracking.utils.vehicleres;

;
import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.widget.Toast;

/**
 * Created by llc on 10/31/2017.
 */


public class vh_rec_adapter extends
        RecyclerView.Adapter<com.goyo.tracking.tracking.adapters.vh_rec_adapter.MyViewHolder> {
    private List<MyViewHolder> lstHolders;
    private vts_vh_status_model _chsStsCount;
    final int redCar = R.drawable.redcar;
    final int yellowCar = R.drawable.yellowcar;
    final int greenCar = R.drawable.green;


    private VHOnStatusChangeListner _vhOnStatusChangeListner;

    public void setVHOnStatusChangeListner(VHOnStatusChangeListner eventListener) {
        _vhOnStatusChangeListner = eventListener;
    }

    public vhsts filtersts = vhsts.all;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private TimeZone tz = TimeZone.getTimeZone("GMT");
    private Context _c;

    private Handler mHandler = new Handler();

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            //Toast.makeText(_c, "Calling", Toast.LENGTH_SHORT).show();
            synchronized (lstHolders) {
                updateCounter();
            }
        }
    };

    public void updateCounter() {
        _chsStsCount.setMoving(0);
        _chsStsCount.setOffline(0);
        _chsStsCount.setOnline(0);
        _chsStsCount.setSpeedvoi(0);
//        for (vts_vh_model holder : list) {
        for (int i = 0; i < list.size(); i++) {
            vts_vh_model holder = list.get(i);
            holder.timeago = updateTimeRemaining(holder);
        }


        // }
        notifyDataSetChanged();

        if (_vhOnStatusChangeListner != null) {
            _vhOnStatusChangeListner.onCountChange(_chsStsCount);
        }

    }


    public String updateTimeRemaining(vts_vh_model vhs) {

        if (vhs.sertm == null) {

            return "";
        }

        dateFormat.setTimeZone(tz);
        try {
            Date date = dateFormat.parse(vhs.sertm);

            Calendar cal = Calendar.getInstance(tz);
            Long difference = cal.getTimeInMillis() - date.getTime();
            String res = deviceUtils.getPassedTimeFromCreation(difference, vhs);
            if (vhs.acc.equals("1")) {
                addcount(vhsts.engon, 1, vhs, cal);


            } else {
                addcount(vhs.vhst, 1, vhs, cal);
            }
            return res;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "-";
    }

    Timer status_tmr;

    private void startUpdateTimer() {
        status_tmr = new Timer();
        status_tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);

            }
        }, 1000, 25000);
    }

    private List<vts_vh_model> list;
    private Resources _rs;

    /**
     * View holder class
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgnetwork, imgbattry, imgacc, imgoil;
        private TextView txtTitle, subheader1, txtStsLine, txtSpeed, subheader2, isCreated, txtSpd, txtD1, txtregno;
        private String serverTime = "";
        //private int olsts = 0;
        private RelativeLayout rlolsts, wrapper;
        private String sts = "offline";
        private View vw;

        public MyViewHolder(View view) {
            super(view);
            vw = view;
            wrapper = (RelativeLayout) view.findViewById(R.id.wrapper);
            txtTitle = (TextView) view.findViewById(R.id.header);
            //acc = (RelativeLayout) item.findViewById(R.id.unread_messages_wrapper);
            imgbattry = (ImageView) view.findViewById(R.id.imgbattry);
            imgnetwork = (ImageView) view.findViewById(R.id.imgnetwork);
            imgacc = (ImageView) view.findViewById(R.id.imgacc);
            subheader1 = (TextView) view.findViewById(R.id.subheader1);
            txtStsLine = (TextView) view.findViewById(R.id.txtStsLine);
            rlolsts = (RelativeLayout) view.findViewById(R.id.thumbnail);
            txtSpeed = (TextView) view.findViewById(R.id.txtSpeed);
            subheader2 = (TextView) view.findViewById(R.id.txtregno);
            txtSpd = (TextView) view.findViewById(R.id.txtSpd);
            txtD1 = (TextView) view.findViewById(R.id.txtd1);
            imgoil = (ImageView) view.findViewById(R.id.imgoil);
            //txtregno = (TextView) view.findViewById(R.id.txtregno);
        }

        private void hide() {
            vw.setVisibility(View.VISIBLE);
            vw.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }

        private void show() {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) vw.getLayoutParams();
            ViewGroup.LayoutParams ltLayoutParams = vw.getLayoutParams();
            marginLayoutParams.setMargins(3, 3, 3, 5);
            vw.setLayoutParams(marginLayoutParams);
            ltLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            ltLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            vw.setLayoutParams(ltLayoutParams);
            vw.setPadding(0, padding, 0, 0);
            vw.setVisibility(View.VISIBLE);
        }


    }

    private void addcount(vhsts _s, int count, vts_vh_model actsts, Calendar cal) {
        if (_s.equals(vhsts.engon) && actsts.vhst.equals(vhsts.online)) {
            actsts.vhst = vhsts.engon;
            _chsStsCount.addOnline(count);
            _chsStsCount.addMoving(count);
            if (actsts.vhmarker != null)
                actsts.vhmarker.setIcon(BitmapDescriptorFactory.fromResource(vehicleres.getVehIcon(actsts.vehtyp, CarStatus.Yello)));

        } else if (_s.equals(vhsts.online)) {
            _chsStsCount.addOnline(count);
            if (actsts.vhmarker != null) {
                actsts.vhmarker.setIcon(BitmapDescriptorFactory.fromResource(vehicleres.getVehIcon(actsts.vehtyp, CarStatus.Green)));
            }


        } else if (_s.equals(vhsts.offline)) {
            _chsStsCount.addOffline(count);
            if (actsts.vhmarker != null)
                actsts.vhmarker.setIcon(BitmapDescriptorFactory.fromResource(vehicleres.getVehIcon(actsts.vehtyp, CarStatus.Red)));
        } else if (_s.equals(vhsts.engon)) {
            _chsStsCount.addOffline(count);
            if (actsts.vhmarker != null)
                actsts.vhmarker.setIcon(BitmapDescriptorFactory.fromResource(vehicleres.getVehIcon(actsts.vehtyp, CarStatus.Red)));
        }

        actsts.isp = false;
        if (actsts.lstspdtm != null && !actsts.lstspdtm.isEmpty()) {
            try {
                Date spdtime = dateFormat.parse(actsts.lstspdtm);
                Long difference = cal.getTimeInMillis() - spdtime.getTime();
                Long min = TimeUnit.MILLISECONDS.toMinutes(difference);
                if (min < (24 * 60)) {
                    _chsStsCount.addSpeedvoi(1);
                    actsts.isp = true;
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (actsts.vhmarker != null) {
            if (filtersts.equals(vhsts.all)) {
                actsts.vhmarker.setVisible(true);
            } else if (filtersts.equals(vhsts.spd) && actsts.isp) {
                actsts.vhmarker.setVisible(true);
            } else if (filtersts.equals(vhsts.online) && actsts.vhst == vhsts.engon) {
                actsts.vhmarker.setVisible(true);
            } else if (filtersts.equals(actsts.vhst)) {
                actsts.vhmarker.setVisible(true);
            } else {
                actsts.vhmarker.setVisible(false);
            }
        }
    }

    private int padding = 0;

    public vh_rec_adapter(List<vts_vh_model> lst, Resources rs, Context c) {
        this.list = lst;
        this._rs = rs;
        this._c = c;

        float scale = rs.getDisplayMetrics().density;
        padding = (int) (5 * scale + 0.5f);


        lstHolders = new ArrayList<>();
        _chsStsCount = new vts_vh_status_model();
        _chsStsCount.setAll(lst.size());
        startUpdateTimer();

    }

    //AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);

    SimpleDateFormat gpsdtfromat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onBindViewHolder(MyViewHolder mViewHolder, int position) {


        vts_vh_model vh = this.list.get(position);

        if (filtersts.equals(vhsts.all)) {
            mViewHolder.show();
        } else if (filtersts.equals(vhsts.spd) && vh.isp) {
            mViewHolder.show();
        } else if (filtersts.equals(vhsts.online) && vh.vhst == vhsts.engon) {
            mViewHolder.show();
        } else if (filtersts.equals(vh.vhst)) {
            mViewHolder.show();
        } else {
            mViewHolder.hide();

        }
        mViewHolder.imgacc.setImageDrawable(null);
        mViewHolder.imgacc.setBackgroundDrawable(_rs.getDrawable(deviceUtils.getAcc(vh.acc)));
        mViewHolder.subheader2.setText(vh.regno);
        if (vh.btr != null) {
            mViewHolder.imgbattry.setImageDrawable(null);
            if (vh.btrst.equals("CHRG")) {
                mViewHolder.imgbattry.setImageDrawable(_rs.getDrawable(deviceUtils.getBatrry(vh.btrst)));
            } else {
                mViewHolder.imgbattry.setImageDrawable(_rs.getDrawable(deviceUtils.getBatrry(vh.btr)));
            }
        } else {
            mViewHolder.imgbattry.setImageDrawable(_rs.getDrawable(deviceUtils.getBatrry("0")));
        }
        if (vh.gsmsig != null) {
            mViewHolder.imgnetwork.setImageDrawable(null);
            mViewHolder.imgnetwork.setImageDrawable(_rs.getDrawable(deviceUtils.getNetwork(vh.gsmsig)));
        } else {
            mViewHolder.imgnetwork.setImageDrawable(null);
            mViewHolder.imgnetwork.setImageDrawable(_rs.getDrawable(deviceUtils.getNetwork("0")));
        }
        mViewHolder.serverTime = vh.sertm;
        mViewHolder.txtTitle.setText(vh.vno);
        if (vh.speed > 0) {

            mViewHolder.txtSpeed.setTextColor(_rs.getColor(R.color.rsc_chat_row_subheader2));
        } else {
            mViewHolder.txtSpeed.setTextColor(_rs.getColor(R.color.colordisable));
        }
        mViewHolder.txtSpeed.setText(vh.speed + " km/h");
        try {
            if (vh.gpstm != null) {
                Log.v("GPSTM", vh.vno + " " + vh.gpstm);
                gpsdtfromat.setTimeZone(tz);
                Date date = gpsdtfromat.parse(vh.gpstm);
                SimpleDateFormat dformatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");

                // mViewHolder.subheader2.setText("Last Location: " + dformatter.format(date));
            } else {
                // mViewHolder.subheader2.setText("---");
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // mViewHolder.subheader2.setText("---");
        }


        if (vh.olsts == 1) {
            mViewHolder.txtStsLine.setBackgroundResource(R.color.sts_ol);
            mViewHolder.rlolsts.setBackgroundResource(R.drawable.grn);
        } else if (vh.olsts == 2) {
            mViewHolder.txtStsLine.setBackgroundResource(R.color.sts_wl);
            mViewHolder.rlolsts.setBackgroundResource(R.drawable.red);
        } else {
            mViewHolder.txtStsLine.setBackgroundResource(R.color.sts_of);
            mViewHolder.rlolsts.setBackgroundResource(R.drawable.red);
        }

        if (vh.isp) {
            mViewHolder.txtSpd.setTextColor(_rs.getColor(R.color.colorHolo));
        } else {
            mViewHolder.txtSpd.setTextColor(_rs.getColor(R.color.colordisable));
        }

        mViewHolder.txtD1.setPaintFlags(0);
        if (vh.extra != null && vh.extra.d1str != null && !vh.extra.d1str.equals("")) {
            mViewHolder.txtD1.setText(vh.extra.d1str);
            if (vh.d1 == 1) {
                mViewHolder.txtD1.setTextColor(_rs.getColor(R.color.sts_ol));
            } else {
                mViewHolder.txtD1.setTextColor(_rs.getColor(R.color.colordisable));
            }
        } else {
            mViewHolder.txtD1.setTextColor(_rs.getColor(R.color.colordisable));
            mViewHolder.txtD1.setText("AC");
            mViewHolder.txtD1.setPaintFlags(mViewHolder.txtD1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


        mViewHolder.subheader1.setText(vh.timeago);

        mViewHolder.wrapper.setBackgroundResource(R.drawable.shape_item_border);
        if (vh.isselected) {
            mViewHolder.wrapper.setBackgroundResource(R.drawable.shape_item_border_selected);
        }

        //mViewHolder.updateTimeRemaining();
        mViewHolder.imgoil.setImageDrawable(_rs.getDrawable(R.drawable.ic_oil_con));
        if (vh.oe == 1) {
            mViewHolder.imgoil.setImageDrawable(_rs.getDrawable(R.drawable.ic_oil_discon));
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_item_layout, parent, false);
        MyViewHolder m = new MyViewHolder(v);
//        if (!m.isCreated.getText().equals("y")) {
//            m.isCreated.setText("y");
//            lstHolders.add(m);
//        }
        return m;
    }

    public void filter(vhsts query, String typ) {

        this.filtersts = query;
        this.notifyDataSetChanged();

    }

    public enum vhsts {
        all,
        online,
        offline,
        engon,
        spd

    }

    public void killAll() {


        if (status_tmr != null) {
            status_tmr.cancel();
            status_tmr = null;
        }
    }
}
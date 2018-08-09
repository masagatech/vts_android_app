package com.goyo.tracking.tracking.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Ack;
import com.goyo.tracking.tracking.R;
import com.goyo.tracking.tracking.globals.Global;
import com.goyo.tracking.tracking.model.vts_vh_command_model;
import com.goyo.tracking.tracking.model.vts_vh_model;
import com.goyo.tracking.tracking.service.livesocketService;
import com.goyo.tracking.tracking.utils.SHP;
import com.goyo.tracking.tracking.utils.common;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class dialogCommands extends Dialog {

    Context context;
    livesocketService _service;
    vts_vh_model _vh;




    public vts_vh_model get_vh() {
        return _vh;
    }

    public void set_vh(vts_vh_model _vh) {
        this._vh = _vh;
    }

    public void setmCallback(OnDialogValuesListener mCallback) {
        this.mCallback = mCallback;
    }


    OnDialogValuesListener mCallback;

    public interface OnDialogValuesListener {
        public void DialogValues(String Message);
    }
    private static final int  theme = android.R.style.Theme_Black_NoTitleBar_Fullscreen;

    public dialogCommands(@NonNull Context context, livesocketService service) {

        super(context, theme);
        this.context = context;
        this._service = service;
        getWindow().getAttributes().windowAnimations = R.style.DialogSlideUpDownAnim;
        this.setTitle("Select delivery address");


    }



    @Override
    public void dismiss() {
        super.dismiss();
        // mCallback.DialogValues(selectedModel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_command_screen);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnoilcut)
    void onBtnoilcut_Click(View v) {
        callCommand("Relay,1#");
    }

    @OnClick(R.id.btnoilrestore)
    void onbtnoilrestore_Click(View v) {
        callCommand("Relay,0#");
    }

    @OnClick(R.id.btnStatus)
    void onbtnstatus_Click(View v) {
        callCommand("Status#");
    }

    @OnClick(R.id.btnFind)
    void onbtnfind_Click(View v) {
        callCommand("Find#");
    }

    private void callCommand(String callcmd) {
        String cmd = getCommandWrap(callcmd);
        this._service.SendCommand(cmd, new Ack() {
            @Override
            public void call(Object... args) {
                mCallback.DialogValues(args[0].toString());
            }
        });
    }


    private String getCommandWrap(String Command) {
        vts_vh_command_model o = new vts_vh_command_model();
        o.setUID(SHP.get(context, SHP.ids.uid, "").toString());
        o.setUcode(Global.loginusr.getUcode());
        o.setSrc("sch_user");
        o.setImei(this._vh.vhid);
        o.setCmd(Command);

        long cmduid = Long.parseLong(SHP.get(context, SHP.ids.commanduid, "0").toString());
        o.setUniqid(cmduid);
        o.setPlatform("ma");
        String Deviceid = common.getDeviceUniqueID((Activity) context);
        o.setDeviceID(Deviceid);

        return o.toJsonString();
    }

}

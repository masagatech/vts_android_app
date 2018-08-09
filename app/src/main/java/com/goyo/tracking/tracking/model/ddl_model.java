package com.goyo.tracking.tracking.model;

import android.support.annotation.Nullable;

public class ddl_model {
    public String Key;
    public String Value;
    public String Extra1;


    public ddl_model(String _key, String _val) {
        this.Key = _key;
        this.Value = _val;
    }

    public ddl_model(String _key, String _val, String _extra1) {
        this.Key = _key;
        this.Value = _val;
        this.Extra1 = _extra1;
    }
}

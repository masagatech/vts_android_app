package com.goyo.tracking.tracking.helpers;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.goyo.tracking.tracking.R;

/**
 * Created by llc on 11/22/2017.
 */
public class RootActivity extends AppCompatActivity {
      int onStartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onStartCount = 1;
        if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.animator.left_to_right,
                    R.animator.right_to_left);
        } else // already created so reverse animation
        {
            onStartCount = 2;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (onStartCount > 1) {
            this.overridePendingTransition(R.animator.right_to_left,
                    R.animator.left_to_right);

        } else if (onStartCount == 1) {
            onStartCount++;
        }

    }

}
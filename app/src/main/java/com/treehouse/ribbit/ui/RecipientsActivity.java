package com.treehouse.ribbit.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.treehouse.ribbit.R;
import com.treehouse.ribbit.utils.ParseConstants;

/**
 * Created by lbalmaceda on 8/23/15.
 */
public class RecipientsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Uri mediaUri = getIntent().getData();
        String fileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment recipientsFragment = new RecipientsFragment();

        Bundle args = new Bundle();
        args.putParcelable(ParseConstants.KEY_MEDIA_URI, mediaUri);
        args.putString(ParseConstants.KEY_FILE_TYPE, fileType);
        recipientsFragment.setArguments(args);

        ft.replace(android.R.id.content, recipientsFragment);
        ft.commit();
    }
}

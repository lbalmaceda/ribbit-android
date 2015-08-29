package com.treehouse.ribbit.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.treehouse.ribbit.R;
import com.treehouse.ribbit.utils.FileHelper;
import com.treehouse.ribbit.utils.ParseConstants;

import java.util.ArrayList;
import java.util.List;

public class RecipientsFragment extends ListFragment {


    private static final String TAG = RecipientsFragment.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;

    protected MenuItem mSendMenuItem;

    protected Uri mMediaUri;
    protected String mFileType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        mMediaUri = args.getParcelable(ParseConstants.KEY_MEDIA_URI);
        mFileType = args.getString(ParseConstants.KEY_FILE_TYPE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipients, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.findItem(R.id.action_send);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.action_send:
                ParseObject message = createMessage();
                if (message == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setMessage(getString(R.string.error_selecting_file))
                            .setTitle(getString(R.string.error_selecting_file_title))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    send(message);
                    getActivity().finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void send(ParseObject message) {
        final Context localContext = getActivity();
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Success
                    Toast.makeText(localContext,
                            R.string.success_message,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Error
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(getString(R.string.error_sending_message))
                            .setTitle(getString(R.string.error_selecting_file_title))
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    protected ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENTS_ID, getRecipientsId());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(getContext(), mMediaUri);
        if (fileBytes == null) {
            Log.e(TAG, "FileHelper error");
            return null;
        } else {
            if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }

            String fileName = FileHelper.getFileName(getContext(), mMediaUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
            return message;
        }
    }

    protected ArrayList<String> getRecipientsId() {
        ArrayList<String> recipientsId = new ArrayList<>();
        for (int i = 0; i < getListView().getCount(); i++) {
            if (getListView().isItemChecked(i)) {
                recipientsId.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientsId;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        getActivity().setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (e == null) {
                    // Success
                    mFriends = friends;

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);
                } else {
                    // Error
                    Log.e(TAG, e.getMessage());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mSendMenuItem.setVisible(l.getCheckedItemCount() > 0);
    }
}

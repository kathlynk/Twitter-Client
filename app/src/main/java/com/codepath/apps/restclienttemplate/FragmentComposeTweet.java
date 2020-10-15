package com.codepath.apps.restclienttemplate;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentComposeTweet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentComposeTweet extends DialogFragment {

    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "FragmentComposeTweet";

    User user;


    EditText etCompose;
    Button btnTweet;
    TextView tvCharCount;
    TwitterClient client;
    Button bClose;
    ImageView ivUserProfileImage;
    TextView tvUserName;
    TextView tvUserScreenName;


    public FragmentComposeTweet() {
        // Required empty public constructor
    }

    public static FragmentComposeTweet newInstance(User user) {
        FragmentComposeTweet fragment = new FragmentComposeTweet();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    public interface ComposeTweetListener {
        void onFinishComposeTweet(Tweet tweet);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = Parcels.unwrap(getArguments().getParcelable("user"));
        } else {
            Log.e(TAG, "Arguments were null again");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //get twitter rest client
        client = TwitterApp.getRestClient(getContext());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose_tweet, container, false);
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCompose = (EditText) view.findViewById(R.id.etCompose);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);
        tvCharCount.setText(Integer.toString(MAX_TWEET_LENGTH));
        bClose = (Button) view.findViewById(R.id.bClose);
        ivUserProfileImage = (ImageView) view.findViewById(R.id.ivUserProfileImage);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvUserScreenName = (TextView) view.findViewById(R.id.tvUserScreenName);

        //set user info
        tvUserName.setText(user.name);
        tvUserScreenName.setText(user.screenName);

        //profile image loaded with Glide
        Glide.with(this).load(user.profileImageUrl).transform(new RoundedCornersTransformation(10, 0)).into(ivUserProfileImage);

        //set click listener on tweet button
        btnTweet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(getContext(), "Tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(getContext(), "Tweet character limit exceeded", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Make API call
                client.postTweet(tweetContent, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess: Tweet Posted");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published Tweet:" + tweet);
                            //pass data to timeline activity
                            ComposeTweetListener listener = (ComposeTweetListener) getActivity();
                            listener.onFinishComposeTweet(tweet);
                            dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });

        //listener for char count in compose tweet.  Updates remaining character count next to tweet button
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(String.valueOf(MAX_TWEET_LENGTH-s.length()));
                if (MAX_TWEET_LENGTH-s.length() < 0) {
                    tvCharCount.setTextColor(getResources().getColor(R.color.medium_red));
                    btnTweet.setEnabled(false);
                } else {
                    tvCharCount.setTextColor(getResources().getColor(R.color.medium_gray));
                    btnTweet.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.80), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        // Call super onResume after sizing

        super.onResume();
    }
}
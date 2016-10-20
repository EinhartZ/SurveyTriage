package com.qaelum.einhart.qaelumsurvey;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    private SharedPreferences prefs;
    private Toast toast;
    private TextView questionTxt;
    private ImageView logo;

    private ImageView img_positive;
    private ImageView img_neutral;
    private ImageView img_negative;

    private Animator animFlip_x;
    private Animator animFlip_y;
    private Animator animRotate;
    private AnimatorListener blackHoleListener;
    private TouchBlackHoleView black_hole;

    private String submitUrl;
    private String location;
    private String questionId;
    private String questionnaireId;

    final Handler handler = new Handler();
    Runnable mLongPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideStatusBar();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        questionTxt = (TextView)findViewById(R.id.question_text);
        //questionTxt.setOnTouchListener(this);
        logo = (ImageView)findViewById(R.id.img_logo);
        logo.setOnTouchListener(this);

        img_positive = (ImageView)findViewById(R.id.img_positive);
        img_neutral = (ImageView)findViewById(R.id.img_neutral);
        img_negative = (ImageView)findViewById(R.id.img_negative);

        animFlip_x = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flip_x);
        animFlip_y = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flip_y);
        animRotate = ObjectAnimator.ofFloat(null, "rotation", 0f, 360f);
        animRotate.setDuration(getResources().getInteger(R.integer.anim_length));

        black_hole = (TouchBlackHoleView) findViewById(R.id.black_hole);
        blackHoleListener = new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                black_hole.disable_touch(true);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                black_hole.disable_touch(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                black_hole.disable_touch(false);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

        img_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Click" ,"Clicked on img_positive");

                doPost(getString(R.string.jsonValue_positive));
                flipImageX(img_positive);
                toast.setText(R.string.feedback_pos);
                toast.show();
            }
        });

        img_neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Click" ,"Clicked on neutral");

                doPost(getString(R.string.jsonValue_neutral));
                rotateImage(img_neutral);
                toast.setText(R.string.feedback_nul);
                toast.show();
            }
        });

        img_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Click" ,"Clicked on img_negative");

                doPost(getString(R.string.jsonValue_negative));
                flipImageY(img_negative);
                toast.setText(R.string.feedback_neg);
                toast.show();
            }
        });

        setParameters();
        mLongPressed = new Runnable() {
            public void run() {
                openSettings(null);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        setParameters();

        hideStatusBar();
    }

    private void setParameters() {
        questionTxt.setText(prefs.getString("pref_questionTxt", getString(R.string.default_questionTxt)));
        submitUrl = prefs.getString("pref_submitURL", getString(R.string.default_submitURL));
        location = prefs.getString("pref_location", getString(R.string.default_location));
        questionId = prefs.getString("pref_questionID", getString(R.string.default_questionID));
        questionnaireId = prefs.getString("pref_questionnaireID", getString(R.string.default_questionnaireID));
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //android.app.ActionBar actionBar = getActionBar();
        //actionBar.hide();
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            handler.postDelayed(mLongPressed, getResources().getInteger(R.integer.longPress_length));
        if(event.getAction() == MotionEvent.ACTION_UP)
            handler.removeCallbacks(mLongPressed);
        return true;
    }


    /*
    * REST calls
    * */
    private void doPost(String answer) {
        final TextView mTextDisplay = (TextView) findViewById(R.id.text_test);

        JSONObject answers = new JSONObject();
        JSONObject params = new JSONObject();

        try {
//            answers.put(getString(R.string.config_questionID), answer);
//            params.put("questionnaireId", getString(R.string.config_questionnaireID));
            answers.put(questionId, answer);
            params.put("questionnaireId", questionnaireId);
            params.put("answers", answers);
            params.put("location", location);
            params.put("cmt", questionTxt.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, submitUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mTextDisplay.setText("");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextDisplay.setText(String.format("Network error: %s%nCouldn't submit your answer at the moment", error.toString()));
                Log.e("Error", error.toString(), error);
            }
        });

        Log.i("Post",  submitUrl + " <= " + params.toString());
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    //TODO-ein testing
    private void doGet() {
        String url = "http://192.168.1.67:10525/PATIENT_SURVEY/triage_stat";
        final TextView mTextDisplay = (TextView) findViewById(R.id.text_test);

        JsonObjectRequest jsonObjectRequuest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mTextDisplay.setText("Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextDisplay.setText("Err: " + error.toString());
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequuest);
    }

    /*
    * Animations
    * */
    private void rotateImage(ImageView imageView) {
        animRotate.setTarget(imageView);
        animRotate.addListener(blackHoleListener);
        animRotate.start();
    }

    private void flipImageX(ImageView imageView) {
        animFlip_x.setTarget(imageView);
        animFlip_x.addListener(blackHoleListener);
        animFlip_x.start();
    }

    private void flipImageY(ImageView imageView) {
        animFlip_y.setTarget(imageView);
        animFlip_y.addListener(blackHoleListener);
        animFlip_y.start();
    }

}

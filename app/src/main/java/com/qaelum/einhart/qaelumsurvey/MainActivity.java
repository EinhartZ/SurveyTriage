package com.qaelum.einhart.qaelumsurvey;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toast toast;

    private ImageView img_positive;
    private ImageView img_neutral;
    private ImageView img_negative;

    private Animator animFlip_y;
    private Animator animRotate;
    private AnimatorListener blackHoleListener;
    private TouchBlackHoleView black_hole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideStatusBar();

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        img_positive = (ImageView)findViewById(R.id.face_positive);
        img_neutral = (ImageView)findViewById(R.id.face_neutral);
        img_negative = (ImageView)findViewById(R.id.face_negative);

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

                doPost("app_positive");
                rotateImage(img_positive);
                toast.setText(":)");
                toast.show();
            }
        });

        img_neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Click" ,"Clicked on neutral");

                doPost("app_neutral");
                toast.setText(":|");
                toast.show();
            }
        });

        img_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Click" ,"Clicked on img_negative");

                doPost("app_negative");
                flipImage(img_negative);
                toast.setText(":(");
                toast.show();
            }
        });

    }

    private void rotateImage(ImageView imageView) {
        animRotate.setTarget(imageView);
        animRotate.addListener(blackHoleListener);
        animRotate.start();
    }

    private void flipImage(ImageView imageView) {
        animFlip_y.setTarget(imageView);
        animFlip_y.addListener(blackHoleListener);
        animFlip_y.start();
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //android.app.ActionBar actionBar = getActionBar();
        //actionBar.hide();
    }

    private void doPost(String answer) {
        String url = "http://192.168.1.23:10525/PATIENT_SURVEY/submit";
        final TextView mTextDisplay = (TextView) findViewById(R.id.text_test);

        Map<String, String> answers = new HashMap<>();
        answers.put("41", answer);

        Map<String, Object> params = new HashMap<>();
        params.put("questionnaireId", "24");
        params.put("answers", answers);

        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mTextDisplay.setText("Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextDisplay.setText("Error: " + error.toString());
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

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
}

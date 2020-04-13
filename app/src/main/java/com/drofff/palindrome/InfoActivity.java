package com.drofff.palindrome;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.drofff.palindrome.enums.InfoStatus;

import static com.drofff.palindrome.constants.UiConstants.INFO_MESSAGE_PARAM;
import static com.drofff.palindrome.constants.UiConstants.INFO_RETURN_ACTIVITY_CLASS_PARAM;
import static com.drofff.palindrome.constants.UiConstants.INFO_STATUS_PARAM;
import static com.drofff.palindrome.enums.InfoStatus.SUCCESS;
import static com.drofff.palindrome.utils.ReflectionUtils.getClassByName;
import static com.drofff.palindrome.utils.ValidationUtils.validateNotNull;

public class InfoActivity extends AppCompatActivity {

    private Class<?> returnActivityClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setTitle("");
        enableHomeButton();
        returnActivityClass = getReturnActivityClass();
        String message = getMessage();
        setInfoMessage(message);
        InfoStatus status = getInfoStatus();
        displayStatus(status);
    }

    private void enableHomeButton() {
        ActionBar actionBar = getSupportActionBar();
        validateNotNull(actionBar, "Can not reach the action bar");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private Class<?> getReturnActivityClass() {
        Intent intent = getIntent();
        String returnActivityClassName = intent.getStringExtra(INFO_RETURN_ACTIVITY_CLASS_PARAM);
        validateNotNull(returnActivityClassName, "Return activity class is required");
        return getClassByName(returnActivityClassName);
    }

    private String getMessage() {
        Intent intent = getIntent();
        return intent.getStringExtra(INFO_MESSAGE_PARAM);
    }

    private void setInfoMessage(String text) {
        TextView infoMessage = findViewById(R.id.info_message);
        infoMessage.setText(text);
    }

    private InfoStatus getInfoStatus() {
        Intent intent = getIntent();
        String infoStatusStr = intent.getStringExtra(INFO_STATUS_PARAM);
        return InfoStatus.valueOf(infoStatusStr);
    }

    private void displayStatus(InfoStatus status) {
        if(isSuccessStatus(status)) {
            displaySuccessAnimation();
        }
    }

    private boolean isSuccessStatus(InfoStatus status) {
        return status == SUCCESS;
    }

    private void displaySuccessAnimation() {
        ImageView statusImage = findViewById(R.id.status_animation);
        statusImage.setBackgroundResource(R.drawable.done_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable) statusImage.getBackground();
        animationDrawable.start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        redirectToReturnActivity();
        return true;
    }

    private void redirectToReturnActivity() {
        Intent intent = new Intent(this, returnActivityClass);
        startActivity(intent);
    }

}

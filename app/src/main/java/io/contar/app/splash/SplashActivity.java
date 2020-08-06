package io.contar.app.splash;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import io.contar.app.activities.WebsiteViewActivity;

public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, WebsiteViewActivity.class);
        startActivity(intent);
        finish();

    }
}

package ga.softogi.themoviecatalogue.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.fragment.ThePreferenceFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.setting_holder, new ThePreferenceFragment()).commit();
    }
}

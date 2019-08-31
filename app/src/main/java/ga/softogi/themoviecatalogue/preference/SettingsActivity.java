package ga.softogi.themoviecatalogue.preference;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ga.softogi.themoviecatalogue.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.setting_holder, new ThePreferenceFragment()).commit();
    }
}

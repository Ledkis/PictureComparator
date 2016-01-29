package ledkis.module.picturecomparator.example;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";

    MainScreenFragment mainScreenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mainScreenFragment = MainScreenFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mainScreenFragment)
                    .commit();
        }
    }

}
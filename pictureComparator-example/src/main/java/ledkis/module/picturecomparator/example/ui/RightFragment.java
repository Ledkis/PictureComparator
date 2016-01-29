package ledkis.module.picturecomparator.example.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ledkis.module.picturecomparator.example.R;

public class RightFragment extends Fragment {

    public static final String TAG = "PictureComparatorFragment";


    public RightFragment() {
    }

    public static RightFragment newInstance() {
        RightFragment f = new RightFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_right, container, false);

        return rootView;

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}

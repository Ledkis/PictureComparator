package ledkis.module.picturecomparator.example;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class PictureComparatorApplication extends Application {
    public static final String TAG = "PictureComparatorApplication";

    private ObjectGraph objectGraph;

    public static PictureComparatorApplication get(Context context) {
        return (PictureComparatorApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Perform injection
        buildObjectGraphAndInject();
    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(Modules.list(this));
        objectGraph.inject(this);
    }

    public void inject(Object target) {
        objectGraph.inject(target);
    }
}

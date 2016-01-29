package ledkis.module.picturecomparator.example.core;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

//  https://github.com/soomla/soomla-android-core/blob/master/src/com/soomla/AndroidBus.java
//  https://github.com/square/otto/issues/38
public class AndroidBus extends Bus {
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }

    public void postAsync(final Object event) {
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                post(event);
            }
        });
    }
}
package ledkis.module.picturecomparator.example;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ledkis.module.picturecomparator.example.core.CoreModule;
import ledkis.module.picturecomparator.example.ui.UiModule;

@Module(
        includes = {
                CoreModule.class,
                UiModule.class,
        },
        injects = {
                PictureComparatorApplication.class,
        },
        complete = false,
        library = true
)
public class PictureComparatorModule {
    private final PictureComparatorApplication app;

    public PictureComparatorModule(PictureComparatorApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return app;
    }
}

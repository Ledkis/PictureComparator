package ledkis.module.picturecomparator.example.core;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {

        },
        complete = false,

        library = true
)
public class CoreModule {

    @Singleton
    @Provides
    AndroidBus provideOttoBus() {
        return new AndroidBus();
    }

}

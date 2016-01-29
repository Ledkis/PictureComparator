package ledkis.module.picturecomparator.example.ui;

import dagger.Module;

@Module(
        injects = {
                MainActivity.class,
                MainScreenFragment.class,
                PictureComparatorFragment.class,

        },
        complete = false,
        library = true
)
public class UiModule {

}

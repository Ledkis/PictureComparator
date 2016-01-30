package ledkis.module.picturecomparator.example.event;

import android.graphics.Bitmap;

public class PictureTakenEvent {

    private Bitmap bitmap;
    private int pictureClass;

    public PictureTakenEvent(Bitmap bitmap, int pictureClass) {
        this.bitmap = bitmap;
        this.pictureClass = pictureClass;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getPictureClass() {
        return pictureClass;
    }
}

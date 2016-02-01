package ledkis.module.picturecomparator.util;

import android.graphics.Bitmap;

public class TextureChange {
    private int resourceId;
    private byte[] picturesBytes;
    private String filePath;
    private int reqWidth;
    private int reqHeight;
    private String imageAssetName;
    private Bitmap bitmap;

    public TextureChange(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public TextureChange(String filePath, int reqHeight, int reqWidth) {
        this.filePath = filePath;
        this.reqHeight = reqHeight;
        this.reqWidth = reqWidth;
    }

    public TextureChange(String imageAssetName) {
        this.imageAssetName = imageAssetName;
    }

    public TextureChange(int resourceId) {
        this.resourceId = resourceId;
    }

    public TextureChange(byte[] picturesBytes) {
        this.picturesBytes = picturesBytes;
    }

    public int getResourceId() {
        return resourceId;
    }

    public byte[] getPicturesBytes() {
        return picturesBytes;
    }

    public String getFilePath() {
        return filePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getImageAssetName() {
        return imageAssetName;
    }

    public int getReqHeight() {
        return reqHeight;
    }

    public int getReqWidth() {
        return reqWidth;
    }
}

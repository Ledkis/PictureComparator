package ledkis.module.picturecomparator.example.event;

public class RequestTakePictureEvent {

    private int pictureClass;

    public RequestTakePictureEvent(int pictureClass) {
        this.pictureClass = pictureClass;
    }

    public int getPictureClass() {
        return pictureClass;
    }
}

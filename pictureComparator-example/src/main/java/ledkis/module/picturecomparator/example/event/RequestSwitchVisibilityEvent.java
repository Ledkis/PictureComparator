package ledkis.module.picturecomparator.example.event;

public class RequestSwitchVisibilityEvent {

    private static boolean visibilityFlag;

    public RequestSwitchVisibilityEvent() {
        visibilityFlag = !visibilityFlag;
    }

    public boolean getVisibilityFlag() {
        return visibilityFlag;
    }
}

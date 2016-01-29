package ledkis.module.picturecomparator.example.event;

public class RequestSwitchVisibilityEvent {

    private boolean visibility;

    public RequestSwitchVisibilityEvent(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isVisibility() {
        return visibility;
    }
}

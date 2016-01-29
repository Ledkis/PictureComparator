package ledkis.module.picturecomparatortaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;

import org.joda.time.DateTime;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
import ledkis.shouldi.app.ShouldIApplication;
import ledkis.shouldi.app.core.AndroidBus;
import ledkis.shouldi.app.core.BuildManager;
import ledkis.shouldi.app.core.Constants;
import ledkis.shouldi.app.core.MyLocationManager;
import ledkis.shouldi.app.core.ShouldIManager;
import ledkis.shouldi.app.core.ShouldIPictureManager;
import ledkis.shouldi.app.core.ShouldIQuestionManager;
import ledkis.shouldi.app.core.TimeExecution;
import ledkis.shouldi.app.data.HelperViewPreference;
import ledkis.shouldi.app.data.model.CurrentUser;
import ledkis.shouldi.app.ui.dialogs.InfoDialog;
import ledkis.shouldi.app.ui.events.ChangeMainScreenFragmentFocusEvent;
import ledkis.shouldi.app.ui.events.PictureSetEvent;
import ledkis.shouldi.app.ui.events.RequestBrowseShouldIEvent;
import ledkis.shouldi.app.ui.events.RequestCameraFlipEvent;
import ledkis.shouldi.app.ui.events.RequestEnableMainViewPagerEvent;
import ledkis.shouldi.app.ui.events.RequestStartMainCameraEvent;
import ledkis.shouldi.app.ui.events.RequestStartSettingsActivityEvent;
import ledkis.shouldi.app.ui.events.RequestTakePictureEvent;
import ledkis.shouldi.app.ui.events.ResetShouldIEvent;
import ledkis.shouldi.app.ui.events.ShouldIExpiredEvent;
import ledkis.shouldi.app.ui.events.ShouldIReceivedEvent;
import ledkis.shouldi.app.ui.events.ShouldIsSeenEvent;
import ledkis.shouldi.app.ui.events.TimeChangedEvent;
import ledkis.shouldi.app.ui.helpers.Toaster;
import ledkis.shouldi.app.ui.views.PictureComparatorLayout;
import ledkis.shouldi.app.ui.views.PictureTakenLayout;
import ledkis.shouldi.app.ui.views.TakePictureLayout;
import ledkis.shouldi.app.util.Strings;
import ledkis.shouldi.app.util.TimeBuffer;
import ledkis.shouldi.app.util.UiUtil;
import ledkis.shouldi.app.util.log.Ln;

import static ledkis.shouldi.app.core.Constants.Events.SHOULDI_EVENT;
import static ledkis.shouldi.app.core.Constants.Events.SHOULDI_PICTURE_RESET;
import static ledkis.shouldi.app.core.Constants.ShouldI.ANSWER_CHOICE_1;
import static ledkis.shouldi.app.core.Constants.ShouldI.ANSWER_CHOICE_2;
import static ledkis.shouldi.app.core.Constants.ShouldI.CHOICE_QUESTION_TYPE;
import static ledkis.shouldi.app.core.Constants.ShouldI.PICTURES_NOT_TAKEN;
import static ledkis.shouldi.app.core.Constants.ShouldI.PICTURE_CLASS_1;
import static ledkis.shouldi.app.core.Constants.ShouldI.PICTURE_CLASS_2;
import static ledkis.shouldi.app.core.Constants.ShouldI.PICTURE_NOT_TAKEN;
import static ledkis.shouldi.app.core.Constants.Ui.CLIP_ANSWER_TEXT_SHORT_MAX_SIZE;
import static ledkis.shouldi.app.core.Constants.Ui.SHORT_CLIPPED_ENDER;
import static ledkis.shouldi.app.core.Constants.Ui.SH_SEND_POSITION;
import static ledkis.shouldi.app.core.Constants.Ui.SH_VIEWER_POSITION;
import static ledkis.shouldi.app.ui.views.TakePictureLayout.STATE_TAKE_PICTURE;
import static ledkis.shouldi.app.ui.views.TakePictureLayout.STATE_TAKE_PICTURE_BIS;

public class PictureTakerLayout extends RelativeLayout {

    public static final String TAG = "PictureTakerFragment";

    public static final int TAKE_PICTURE_1 = 1;
    public static final int TAKE_PICTURE_2 = 2;


    @Inject AndroidBus bus;
    @Inject CurrentUser currentUser;
    @Inject ShouldIManager shouldIManager;
    @Inject ShouldIPictureManager shouldIPictureManager;
    @Inject MyLocationManager locationManager;
    @Inject HelperViewPreference helperViewPreference;
    @Inject BuildManager buildManager;

    @InjectView(R.id.container) View container;
    @InjectView(R.id.takePictureLayout) TakePictureLayout takePictureLayout;
    @InjectView(R.id.pictureTakenLayout) PictureTakenLayout pictureTakenLayout;
    @InjectView(R.id.tempButton) Button tempButton;

    int action;
    int picturesState;

    TimeBuffer refreshNotificationDrawableTimeBuffer;

    boolean tempButtonFlag;

    public PictureTakerFragment() {
        screenName = "PictureTakerFragment";
    }

    public static MainScreenBaseFragment newInstance() {
        return new PictureTakerFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        fragmentName = "PictureTakerFragment";
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        // resume opengl part
        pictureTakenLayout.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
        locationManager.cancelTimer();
        // pause opengl part
        pictureTakenLayout.pause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShouldIApplication.get(getActivity()).inject(this);

        refreshNotificationDrawableTimeBuffer = new TimeBuffer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_sh_taker, container, false);
        ButterKnife.inject(this, rootView);

        action = TAKE_PICTURE_1;
        picturesState = PICTURES_NOT_TAKEN;

        takePictureLayout.init(STATE_TAKE_PICTURE);

        takePictureLayout.setCallback(new TakePictureLayout.Callback() {

            @Override
            public void requestToast(String message) {
                Toaster.showLong(getActivity(), message);
            }

            @Override
            public void requestSettingsActivity() {
                bus.post(new RequestStartSettingsActivityEvent());
            }

            @Override
            public void onCapture() {
                locationManager.updateCurrentShouldILocation();
                bus.post(new RequestTakePictureEvent());
            }

            @Override
            public void onLongCapture() {
                locationManager.updateCurrentShouldILocation();
            }

            @Override
            public void onNotificationClick() {
                bus.post(new ChangeMainScreenFragmentFocusEvent(SH_VIEWER_POSITION));
            }

            @Override
            public void requestBrowseCustomPicture() {
                buildManager.featureWrapper(getActivity(), buildManager.FEATURE_BROWSE_PHONE_PICTURE, new Runnable() {
                    @Override
                    public void run() {
                        bus.post(new RequestBrowseShouldIEvent());
                    }
                });
            }

            @Override
            public void onCustomPictureSelected(int pictureResId, int customPictureState) {
                PictureTakerFragment.this.onCustomPictureSelected(pictureResId, customPictureState);
            }

            @Override
            public void onCustomPictureSelected(String picturePath, int customPictureState) {
                PictureTakerFragment.this.onCustomPictureSelected(picturePath, customPictureState);
            }

            @Override
            public void requestResetCapture() {
                resetCapture();
            }

            @Override
            public void requestResetCaptureBis() {
                displayPictureTakenLayout(false);
            }

            @Override
            public void requestSwipeControl(boolean getControl) {
                bus.post(new RequestEnableMainViewPagerEvent(!getControl));
            }

            @Override
            public void requestFlipCamera() {
                bus.post(new RequestCameraFlipEvent());
            }
        });

        pictureTakenLayout.init(currentUser, shouldIManager, new PictureTakenLayout.Callback() {
            @Override
            public void onExpiryDateChanged(DateTime expiryDate) {
                currentUser.setCurrentShouldIExpiryDate(expiryDate);
            }

            @Override
            public void onShouldIHeaderClose() {
                final String FUNCTION_TAG = "onShouldIHeaderClose";

                final TimeExecution texec = new TimeExecution();

                bus.post(new RequestStartMainCameraEvent());

                resetCapture();

                Ln.event(SHOULDI_EVENT, TAG, FUNCTION_TAG, SHOULDI_PICTURE_RESET, "", texec);
            }

            @Override
            public void onExpiryDateViewClick() {

            }

            @Override
            public void onSettingExpiryIntervalMillis(int expiryIntervalMillis) {
            }

            @Override
            public void onExpiryIntervalMillisSet(int expiryIntervalMillis) {
            }

            @Override
            public void onShouldIQuestionChange(String question) {
                shouldIManager.setCurrentQuestion(question.trim());
                if (question.length() == Constants.Core.MAX_SHOULDI_QUESTION_SIZE) {
                    Toaster.showShort(getActivity(), String.format(getActivity().getString(R.string.question_max_size_error), Constants.Core.MAX_SHOULDI_QUESTION_SIZE));
                }


                if (CHOICE_QUESTION_TYPE == ShouldIQuestionManager.getShouldIQuestionType(question)) {
                    String choice1 = ShouldIQuestionManager.getQuestionChoice(question, ANSWER_CHOICE_1);
                    String clippedChoice1Text = (String) Strings.clipText(choice1, CLIP_ANSWER_TEXT_SHORT_MAX_SIZE, SHORT_CLIPPED_ENDER);
                    String choice2 = ShouldIQuestionManager.getQuestionChoice(question, ANSWER_CHOICE_2);
                    String clippedChoice2Text = (String) Strings.clipText(choice2, CLIP_ANSWER_TEXT_SHORT_MAX_SIZE, SHORT_CLIPPED_ENDER);
                    pictureTakenLayout.setChoicesText(clippedChoice1Text, clippedChoice2Text);
                } else {
                    pictureTakenLayout.setChoicesText(getString(R.string.choice_1_picture), getString(R.string.choice_2_picture));
                }
            }

            @Override
            public void onCenterButtonClick(PictureComparatorLayout pictureComparatorLayout) {
                swapePicture();
            }

            @Override
            public void onPictureSelected(int pictureClass) {

            }

            @Override
            public void requestClose(final int picture) {
                PictureTakerFragment.this.requestClose(picture);
            }

            @Override
            public void requestRetake(int picture) {
                bus.post(new RequestStartMainCameraEvent());
                switch (picture) {
                    case PICTURE_CLASS_1:
                        displayTakePictureLayout(TAKE_PICTURE_1);
                        break;

                    case PICTURE_CLASS_2:
                        displayTakePictureLayout(TAKE_PICTURE_2);
                        break;
                }
            }

            @Override
            public void requestContinue() {
//                bus.post(new RequestEnableMainViewPagerEvent(true));
                bus.post(new ChangeMainScreenFragmentFocusEvent(SH_SEND_POSITION));
//                pictureTakenLayout.setSwipePictureChooser(false);
            }
        });

        displayTakePictureLayout(TAKE_PICTURE_1);
        setShouldINotificationLayout();


        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tempButtonFlag) {
//                    PictureTakerFragment.this.container.setVisibility(View.GONE);
                    pictureTakenLayout.getPicturesLayout().setVisibility(View.GONE);
                }else {
//                    PictureTakerFragment.this.container.setVisibility(View.VISIBLE);
                    pictureTakenLayout.getPicturesLayout().setVisibility(View.VISIBLE);
                }

                tempButtonFlag = !tempButtonFlag;
            }
        });

        return rootView;
    }

    private void onCustomPictureSelected(int resId, final int customPictureState) {
        setPictureAsync(resId, customPictureState);
        displayPictureTakenLayout(true);
    }

    private void onCustomPictureSelected(String picturePath, final int customPictureState) {
        setPictureAsync(picturePath, customPictureState);
        displayPictureTakenLayout(true);
    }

    private void resetCapture() {
        picturesState = PICTURES_NOT_TAKEN;
        currentUser.setCurrentShouldIPicturesState(picturesState);
        displayTakePictureLayout(TAKE_PICTURE_1);
    }

    private void requestClose(final int picture) {

        switch (picture) {
            case PICTURE_CLASS_1:
                picturesState = ShouldIPictureManager.getPicturesStateFromPic2(picturesState, PICTURE_NOT_TAKEN);
                currentUser.setCurrentShouldIPicturesState(picturesState);

                Drawable picture2 = pictureTakenLayout.getPictureDrawable(PICTURE_CLASS_2);

                if (null != pictureTakenLayout.getPicturesLayout())
                    pictureTakenLayout.getPicturesLayout().setPicture(picture2, PICTURE_CLASS_1);

                ShouldIPictureManager.swapePictures(getActivity());
                break;

            case PICTURE_CLASS_2:
                picturesState = ShouldIPictureManager.getPicturesStateFromPic2(picturesState, PICTURE_NOT_TAKEN);
                currentUser.setCurrentShouldIPicturesState(picturesState);
                break;
        }

        displayPictureTakenLayout(false);

        if (null != pictureTakenLayout.getPicturesLayout())
            pictureTakenLayout.getPicturesLayout().setPicturesLayout(picturesState);
    }

    public void displayTakePictureLayout(int action) {
        bus.post(new RequestEnableMainViewPagerEvent(true));
        UiUtil.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        this.action = action;

        takePictureLayout.setVisibility(View.VISIBLE);
        // pictureTakenLayout INVISIBLE fot making sur glSurfaceView is initialized before using it
//        pictureTakenLayout.setVisibility(View.INVISIBLE);

        pictureTakenLayout.getPicturesLayout().setVisibility(View.INVISIBLE);


        if (TAKE_PICTURE_1 == action) {
            if (ShouldIPictureManager.isPic2Taken(picturesState)) {
                takePictureLayout.setLayout(STATE_TAKE_PICTURE_BIS);
                takePictureLayout.setPictureBis(STATE_TAKE_PICTURE_BIS, pictureTakenLayout.getPictureDrawable(PICTURE_CLASS_2));
            } else {
                takePictureLayout.setLayout(STATE_TAKE_PICTURE);
            }
        } else if (TAKE_PICTURE_2 == action) {
            if (ShouldIPictureManager.isPic1Taken(picturesState)) {
                takePictureLayout.setLayout(STATE_TAKE_PICTURE_BIS);
                takePictureLayout.setPictureBis(STATE_TAKE_PICTURE_BIS, pictureTakenLayout.getPictureDrawable(PICTURE_CLASS_1));
            } else {
                takePictureLayout.setLayout(STATE_TAKE_PICTURE);
            }
        } else {
            this.action = TAKE_PICTURE_1;
            takePictureLayout.setLayout(STATE_TAKE_PICTURE);
        }

        setShouldINotificationLayout();

    }

    public void displayPictureTakenLayout(boolean fadeAnimation) {
        bus.post(new RequestEnableMainViewPagerEvent(false));

        takePictureLayout.setVisibility(View.GONE);
        pictureTakenLayout.setVisibility(View.VISIBLE);
        pictureTakenLayout.getPicturesLayout().setVisibility(View.VISIBLE);

        pictureTakenLayout.setLayout(picturesState, fadeAnimation);

        pictureTakenLayout.setSwipePictureChooser(true);

        bus.post(new PictureSetEvent());
    }

    private int setPicture(int pictureState) {

        int pictureClass = TAKE_PICTURE_1 == action ? PICTURE_CLASS_1 : PICTURE_CLASS_2;
        picturesState = ShouldIPictureManager.getPicturesStateFromPic(picturesState, pictureState, pictureClass);
        currentUser.setCurrentShouldIPicturesState(picturesState);

        return pictureClass;
    }

    private void setPictureAsync(final byte[] picturesBytes, final int pictureState) {
        int pictureClass = setPicture(pictureState);

        shouldIPictureManager.setCurrentPictureAsync(getActivity(), picturesBytes, pictureClass, pictureState);

        if (null != pictureTakenLayout.getPicturesLayout())
            pictureTakenLayout.getPicturesLayout().setPictureAsync(picturesBytes, picturesState, pictureClass);

    }

    private void setPictureAsync(final int pictureResId, final int pictureState) {
        int pictureClass = setPicture(pictureState);

        shouldIPictureManager.setCurrentPictureAsync(getActivity(), pictureResId, pictureClass, pictureState);

        if (null != pictureTakenLayout.getPicturesLayout())
            pictureTakenLayout.getPicturesLayout().setPictureAsync(getResources(), pictureResId, picturesState, pictureClass);
    }

    private void setPictureAsync(final String picturePath, final int pictureState) {
        int pictureClass = setPicture(pictureState);
        int screenWidth = UiUtil.getScreenWidth(getActivity());
        int screenHeight = UiUtil.getScreenHeight(getActivity());

        shouldIPictureManager.setCurrentPictureAsync(getActivity(), picturePath, screenWidth, screenHeight, pictureClass, pictureState);

        if (null != pictureTakenLayout.getPicturesLayout())
            pictureTakenLayout.getPicturesLayout().setPictureAsync(picturePath, picturesState, pictureClass);
    }

    private void swapePicture() {

        int picState1 = ShouldIPictureManager.getPicture2State(picturesState);
        int picState2 = ShouldIPictureManager.getPicture1State(picturesState);

        picturesState = ShouldIPictureManager.getPicturesStateFromPic2(picturesState, picState1);
        picturesState = ShouldIPictureManager.getPicturesStateFromPic2(picturesState, picState2);

        ShouldIPictureManager.swapePictures(getActivity());
    }

    private void setShouldINotificationLayout() {

        if (isResumed()) {
            refreshNotificationDrawableTimeBuffer.start(Constants.Ui.SH_TAKER_REFRESH_TIME_INTERVAL_BUFFER, new Runnable() {
                @Override
                public void run() {

                    Task.callInBackground(new Callable<Integer[]>() {
                        @Override
                        public Integer[] call() throws Exception {
                            return new Integer[]{shouldIManager.getUnSeenNotExpiredShouldIsNbr(), shouldIManager.getUnSeenExpiredShouldIsNbr()};
                        }
                    }).continueWith(new Continuation<Integer[], Object>() {
                        @Override
                        public Object then(Task<Integer[]> task) throws Exception {
                            Integer[] unSeenShouldIsNbrArray = task.getResult();

                            int unSeenNotExpiredShouldIsNbr = unSeenShouldIsNbrArray[0];
                            int unSeenExpiredShouldIsNbr = unSeenShouldIsNbrArray[1];

                            takePictureLayout.setShouldINotificationLayout(unSeenNotExpiredShouldIsNbr, unSeenExpiredShouldIsNbr);

                            return null;
                        }
                    }, Task.UI_THREAD_EXECUTOR);

                }
            });
        }
    }


    @Override
    public void dispatchTouchEvent(MotionEvent event) {
        takePictureLayout.dispatchParentTouchEvent(event);
    }

    public void onPictureTaken(final byte[] picturesBytes, Camera camera, final int cameraId) {

        int pictureState = ShouldIPictureManager.getPictureStateFromCameraId(cameraId);


        setPicture(pictureState);
        displayPictureTakenLayout(false);
        setPictureAsync(picturesBytes, pictureState);
        // Pictures must be set before displaying layout : use of isPicTaken
    }

    public void onRequestBrowseShouldIPicture(int resultCode, Intent data) {
        takePictureLayout.onPhonePictureBrowsed(resultCode, data);
    }


    private void showInfo() {
        if (!helperViewPreference.isCustomCaptureInfoSeen()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InfoDialog.show(getActivity(), getActivity().getString(R.string.custom_capture_info));
                }
            }, Constants.Ui.SHOW_INFO_TIME_DELAY);
        }
    }

    // MainScreenBaseFragment


    @Override
    public void onFocus() {
        if (isResumed()) {
            setShouldINotificationLayout();
            showInfo();
        }
    }


    @Override
    public void refresh() {
        super.refresh();
    }


    // Subscribe

    @Subscribe
    public void onResetShouldIEvent(ResetShouldIEvent event) {
        resetCapture();
        bus.post(new RequestStartMainCameraEvent());
        displayTakePictureLayout(STATE_TAKE_PICTURE);
        pictureTakenLayout.setSwipePictureChooser(false);
    }

    @Subscribe
    public void onTimeChangedEvent(TimeChangedEvent event) {
        // TODO ui : create custom countdown view because notifyDataSetChanged is not cool with RecyclerView behavior

        DateTime expiryDate = currentUser.getCurrentShoulIExpiryDate();
        if (expiryDate != null) {
            pictureTakenLayout.onExpiryDateChanged(expiryDate);
        }
    }

    @Subscribe
    public void onShouldIReceivedEvent(ShouldIReceivedEvent event) {
        setShouldINotificationLayout();
    }

    @Subscribe
    public void onShouldIsSeenEvent(ShouldIsSeenEvent event) {
        setShouldINotificationLayout();
    }

    @Subscribe
    public void onShouldIExpiredEvent(ShouldIExpiredEvent event) {
        setShouldINotificationLayout();
    }

}
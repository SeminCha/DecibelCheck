package ensharp.decibelcheck;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Semin on 2017-01-31.
 */
public class MusicAccessibilityService extends AccessibilityService{

    static final String TAG = "출력값";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        config.flags = AccessibilityServiceInfo.DEFAULT;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
        Log.v("onServiceConnected 실행","ㄱㄱ");
    }

    public String getContentDescription(AccessibilityEvent event) {
        String eventText = null;
        eventText = eventText + event.getContentDescription();
        return eventText;
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, String.format(
                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s [contentdescription] %s" ,
                getEventType(event), event.getClassName(), event.getPackageName(),
                event.getEventTime(), getEventText(event), getContentDescription(event)));
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
        }
        return "default";
    }
}

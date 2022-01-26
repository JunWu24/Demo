package com.junwu.demo.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.junwu.demo.BaseApplication;
import com.junwu.demo.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * 创建日期：2022/1/26
 *
 * @author jun.wu
 * 文件名称：Utils.java
 * 类说明：
 */
public final class Utils {
    private static final String PERMISSION_ACTIVITY_CLASS_NAME =
            "com.blankj.utilcode.util.PermissionUtils$PermissionActivity";
    private static final ActivityLifecycleImpl ACTIVITY_LIFECYCLE = new ActivityLifecycleImpl();
    private static final String CLASS_NAME_ACTIVITYTHREAD = "android.app.ActivityThread";
    private static final String METHOD_NAME_CURRENTACTIVITYTHREAD = "currentActivityThread";
    private static final String METHOD_NAME_GETAPPLICATION = "getApplication";
    private static final String UNKNOWN = "Unknown";

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    private Utils() {
        throw new UnsupportedOperationException(BaseApplication.sApplicationContext.getString(
                R.string.can_not_instantiate));
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param context context
     */
    public static void init(final Context context) {
        if (context == null) {
            init(getApplicationByReflect());
            return;
        }
        init((Application) context.getApplicationContext());
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param app application
     */
    public static void init(final Application app) {
        if (sApplication == null) {
            if (app == null) {
                sApplication = getApplicationByReflect();
            } else {
                sApplication = app;
            }
            sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        } else {
            if (app != null && app.getClass() != sApplication.getClass()) {
                sApplication.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
                ACTIVITY_LIFECYCLE.mActivityList.clear();
                sApplication = app;
                sApplication.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
            }
        }
    }

    /**
     * Return the context of Application object.
     *
     * @return the context of Application object
     */
    public static Application getApp() {
        if (sApplication != null) {
            return sApplication;
        }
        Application app = getApplicationByReflect();
        init(app);
        return app;
    }

    private static Application getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName(CLASS_NAME_ACTIVITYTHREAD);
            Object thread = activityThread.getMethod(METHOD_NAME_CURRENTACTIVITYTHREAD)
                    .invoke(null);
            Object app = activityThread.getMethod(METHOD_NAME_GETAPPLICATION).invoke(thread);
            if (app == null) {
                throw new NullPointerException(BaseApplication.sApplicationContext.getString(
                        R.string.should_init_first));
            }
            return (Application) app;
        } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException
                | InvocationTargetException exception) {
            exception.printStackTrace();
        }
        throw new NullPointerException(BaseApplication.sApplicationContext.getString(
                R.string.should_init_first));
    }

    public interface OnAppStatusChangedListener {
        void onForeground();

        void onBackground();
    }

    public interface OnActivityDestroyedListener {
        void onActivityDestroyed(Activity activity);
    }

    static class ActivityLifecycleImpl implements ActivityLifecycleCallbacks {
        private static final String FIELD_MLASTSRVVIEW = "mLastSrvView";
        private static final String FIELD_MCURROOTVIEW = "mCurRootView";
        private static final String FIELD_MSERVEDVIEW = "mServedView";
        private static final String FIELD_MNEXTSERVEDVIEW = "mNextServedView";
        private static final String FIELD_MACTIVITYLIST = "mActivityList";
        private static final String FIELD_PAUSED = "paused";
        private static final String FIELD_ACTIVITY = "activity";
        final LinkedList<Activity> mActivityList = new LinkedList<>();
        final Map<Object, OnAppStatusChangedListener> mStatusListenerMap = new HashMap<>();
        final Map<Activity, Set<OnActivityDestroyedListener>> mDestroyedListenerMap
                = new HashMap<>();
        private int mForegroundCount = 0;
        private int mConfigCount = 0;
        private boolean mIsBackground = false;

        private static void fixSoftInputLeaks(final Activity activity) {
            if (activity == null) {
                return;
            }
            InputMethodManager imm = (InputMethodManager) Utils.getApp().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }
            String[] leakViews = new String[]{FIELD_MLASTSRVVIEW, FIELD_MCURROOTVIEW,
                    FIELD_MSERVEDVIEW, FIELD_MNEXTSERVEDVIEW};
            for (String leakView : leakViews) {
                try {
                    Field leakViewField = InputMethodManager.class.getDeclaredField(leakView);
                    if (leakViewField == null) {
                        continue;
                    }
                    if (!leakViewField.isAccessible()) {
                        leakViewField.setAccessible(true);
                    }
                    Object obj = leakViewField.get(imm);
                    if (!(obj instanceof View)) {
                        continue;
                    }
                    View view = (View) obj;
                    if (view.getRootView() == activity.getWindow().getDecorView().getRootView()) {
                        leakViewField.set(imm, null);
                    }
                } catch (Throwable ignore) {
                    ignore.printStackTrace();
                }
            }
        }

        @Override
        public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
            setTopActivity(activity);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if (!mIsBackground) {
                setTopActivity(activity);
            }
            if (mConfigCount < 0) {
                ++mConfigCount;
            } else {
                ++mForegroundCount;
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            setTopActivity(activity);
            if (mIsBackground) {
                mIsBackground = false;
                postStatus(true);
            }
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {/**/

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity.isChangingConfigurations()) {
                --mConfigCount;
            } else {
                --mForegroundCount;
                if (mForegroundCount <= 0) {
                    mIsBackground = true;
                    postStatus(false);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(
                @NonNull Activity activity, @NonNull Bundle outState) {
            /**/
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            mActivityList.remove(activity);
            consumeOnActivityDestroyedListener(activity);
            fixSoftInputLeaks(activity);
        }

        Activity getTopActivity() {
            if (!mActivityList.isEmpty()) {
                final Activity topActivity = mActivityList.getLast();
                if (topActivity != null) {
                    return topActivity;
                }
            }
            Activity topActivityByReflect = getTopActivityByReflect();
            if (topActivityByReflect != null) {
                setTopActivity(topActivityByReflect);
            }
            return topActivityByReflect;
        }

        private void setTopActivity(final Activity activity) {
            if (PERMISSION_ACTIVITY_CLASS_NAME.equals(activity.getClass().getName())) {
                return;
            }
            if (mActivityList.contains(activity)) {
                if (!mActivityList.getLast().equals(activity)) {
                    mActivityList.remove(activity);
                    mActivityList.addLast(activity);
                }
            } else {
                mActivityList.addLast(activity);
            }
        }

        void addOnAppStatusChangedListener(final Object object,
                                           final OnAppStatusChangedListener listener) {
            mStatusListenerMap.put(object, listener);
        }

        void removeOnAppStatusChangedListener(final Object object) {
            mStatusListenerMap.remove(object);
        }

        void removeOnActivityDestroyedListener(final Activity activity) {
            if (activity == null) {
                return;
            }
            mDestroyedListenerMap.remove(activity);
        }

        void addOnActivityDestroyedListener(final Activity activity,
                                            final OnActivityDestroyedListener listener) {
            if (activity == null || listener == null) {
                return;
            }
            Set<OnActivityDestroyedListener> listeners;
            if (!mDestroyedListenerMap.containsKey(activity)) {
                listeners = new HashSet<>();
                mDestroyedListenerMap.put(activity, listeners);
            } else {
                listeners = mDestroyedListenerMap.get(activity);
                if (listeners.contains(listener)) {
                    return;
                }
            }
            listeners.add(listener);
        }

        private void postStatus(final boolean isForeground) {
            if (mStatusListenerMap.isEmpty()) {
                return;
            }
            for (OnAppStatusChangedListener onAppStatusChangedListener
                    : mStatusListenerMap.values()) {
                if (onAppStatusChangedListener == null) {
                    return;
                }
                if (isForeground) {
                    onAppStatusChangedListener.onForeground();
                } else {
                    onAppStatusChangedListener.onBackground();
                }
            }
        }

        private void consumeOnActivityDestroyedListener(Activity activity) {
            Iterator<Map.Entry<Activity, Set<OnActivityDestroyedListener>>> iterator
                    = mDestroyedListenerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Activity, Set<OnActivityDestroyedListener>> entry = iterator.next();
                if (entry.getKey() == activity) {
                    Set<OnActivityDestroyedListener> value = entry.getValue();
                    for (OnActivityDestroyedListener listener : value) {
                        listener.onActivityDestroyed(activity);
                    }
                    iterator.remove();
                }
            }
        }

        private Activity getTopActivityByReflect() {
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThreadClass = Class.forName(CLASS_NAME_ACTIVITYTHREAD);
                Object currentActivityThreadMethod = activityThreadClass.getMethod(
                        METHOD_NAME_CURRENTACTIVITYTHREAD).invoke(null);
                Field activityListField = activityThreadClass.getDeclaredField(FIELD_MACTIVITYLIST);
                activityListField.setAccessible(true);
                Map activities = (Map) activityListField.get(currentActivityThreadMethod);
                if (activities == null) {
                    return null;
                }
                for (Object activityRecord : activities.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field pausedField = activityRecordClass.getDeclaredField(FIELD_PAUSED);
                    pausedField.setAccessible(true);
                    if (!pausedField.getBoolean(activityRecord)) {
                        Field activityField = activityRecordClass.getDeclaredField(FIELD_ACTIVITY);
                        activityField.setAccessible(true);
                        return (Activity) activityField.get(activityRecord);
                    }
                }
            } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException
                    | InvocationTargetException | IllegalAccessException exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }

    public static final class FileProvider4UtilCode extends FileProvider {

        @Override
        public boolean onCreate() {
            Utils.init(getContext());
            return true;
        }
    }

    /**
     * Get version name of the package.
     *
     * @param context Context
     * @return Version or Unknown if error
     */
    public static String getVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.MATCH_UNINSTALLED_PACKAGES);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException exception) {
            exception.printStackTrace();
        }
        return UNKNOWN;
    }
}
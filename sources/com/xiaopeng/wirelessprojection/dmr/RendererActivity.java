package com.xiaopeng.wirelessprojection.dmr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseActivity;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.apirouter.IpcRouterService;
import com.xiaopeng.wirelessprojection.core.event.TrafficStatusChangeEvent;
import com.xiaopeng.wirelessprojection.core.interfaces.IOnLoadingListener;
import com.xiaopeng.wirelessprojection.core.interfaces.IRendererPresenter;
import com.xiaopeng.wirelessprojection.core.interfaces.IRendererView;
import com.xiaopeng.wirelessprojection.core.manager.ProtocolManager;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.ThreadUtils;
import com.xiaopeng.wirelessprojection.core.utils.ToastUtils;
import com.xiaopeng.wirelessprojection.dmr.databinding.ActivityDlnaRendererBinding;
import com.xiaopeng.wirelessprojection.dmr.manager.RendererControlManager;
import com.xiaopeng.wirelessprojection.dmr.player.TouchController;
import com.xiaopeng.wirelessprojection.dmr.player.VideoTouchCallback;
import com.xiaopeng.wirelessprojection.dmr.view.AutoScaleSurfaceView;
import com.xiaopeng.wirelessprojection.dmr.view.ExoPlayerFragment;
import com.xiaopeng.xui.app.XDialog;
import com.xiaopeng.xui.app.XDialogInterface;
import com.xpeng.airplay.service.MediaMetaData;
import com.xpeng.airplay.service.MediaPlayInfo;
import com.xpeng.airplay.service.MediaPlaybackInfo;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: classes2.dex */
public class RendererActivity extends BaseActivity implements IRendererView, IOnLoadingListener {
    public static final String DEFAULT_TITLE = " ";
    private static final String KEY_EXTRA_CURRENT_URI = "Renderer.KeyExtra.CurrentUri";
    private static final String KEY_EXTRA_POSITION = "Renderer.KeyExtra.Position";
    private static final String KEY_EXTRA_TITLE = "Renderer.KeyExtra.VideoTitle";
    private static final int LOADING_TIMEOUT = 30000;
    private static final String STOP_REASON_BTN_BACK_MIRROR = "btn_back_mirror";
    private static final String STOP_REASON_BTN_BACK_VIDEO = "btn_back_video";
    private static final String STOP_REASON_COMPLETE = "complete";
    private static final String STOP_REASON_DEFAULT = "default";
    private static final String STOP_REASON_DEFAULT_MIRROR = "default_mirror";
    private static final String STOP_REASON_DEFAULT_VIDEO = "default_video";
    private static final String STOP_REASON_FROM_PHONE = "from_phone";
    private static final String STOP_REASON_KILLED = "killed";
    private static final String STOP_REASON_STOP_MIRROR = "stop_mirror";
    private static final String TAG = "RendererActivity";
    public static final String TEST_URI_AIRPLAY = "http://192.168.1.101:7001/1/f0d5ac23-ef88-5405-aa8d-13b25c556a66.m4v";
    public static final String TEST_URI_LANDSCAPE = "http://s07.xiaopeng.com/semantic_platform/staging/qa/video/1662692873069.mp4";
    public static final String TEST_URI_PORTRAIT = "http://s07.xiaopeng.com/semantic_platform/staging/qa/video/1629425916257.mp4";
    private static String mStopReason = "default";
    private static RendererActivity sInstance;
    private ActivityDlnaRendererBinding mBinding;
    private View mExoPlayerFragParent;
    private ExoPlayerFragment mExoPlayerFragment;
    private Handler mHandler;
    private boolean mIsPaused;
    private IRendererPresenter mPresenter;
    private AutoScaleSurfaceView mSurfaceView;
    private TouchController mTouchController;
    private float mRecordBrightness = 0.4f;
    private VideoTouchCallback mVideoTouchCallback = new VideoTouchCallback(this);

    /* loaded from: classes2.dex */
    public interface onVideoStateChangedListener {
        void getPlaybackInfo(MediaPlaybackInfo mediaPlaybackInfo);

        void onClientDisconnected();

        void onVideoRateChanged(int i);

        void onVideoScrubChanged(int i);

        void onVideoStop();

        void onVideoUrlUpdated();

        void onVolumeChanged(float f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initRenderer() {
    }

    private void openMedia(String str, long j) {
    }

    public static final RendererActivity instance() {
        return sInstance;
    }

    public static void show() {
        LogUtils.i(TAG, "show");
        Context context = BaseApp.getContext();
        Intent intent = new Intent(context, RendererActivity.class);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }

    public static void hide() {
        LogUtils.i(TAG, "hide");
        setStopReason(STOP_REASON_FROM_PHONE);
        RendererActivity rendererActivity = sInstance;
        if (rendererActivity == null || rendererActivity.isFinishing()) {
            return;
        }
        sInstance.exitRenderer("hide");
    }

    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LogUtils.i(TAG, "onCreate activity=" + this);
        ProtocolManager.instance().checkSessionAvailable();
        ActivityDlnaRendererBinding activityDlnaRendererBinding = (ActivityDlnaRendererBinding) DataBindingUtil.setContentView(this, R.layout.activity_dlna_renderer);
        this.mBinding = activityDlnaRendererBinding;
        try {
            activityDlnaRendererBinding.ibBtnBack.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.wirelessprojection.dmr.-$$Lambda$RendererActivity$rAOcYcBVjdPlUkP4JGsNrkxWjt8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    RendererActivity.this.lambda$onCreate$0$RendererActivity(view);
                }
            });
            this.mBinding.getRoot().setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
            this.mBinding.tvVideoTitle.setText(DEFAULT_TITLE);
            this.mSurfaceView = this.mBinding.svMirror;
            this.mExoPlayerFragParent = this.mBinding.flFragmentVideoParent;
            this.mHandler = new Handler(Looper.getMainLooper(), new MainHandlerCallback());
            HandlerThread handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            this.mPresenter = new RendererPresenter(this, handlerThread.getLooper());
            this.mSurfaceView.getHolder().addCallback(this.mPresenter);
            RendererControlManager.instance().setRendererPresenter(this.mPresenter);
            this.mTouchController = new TouchController(this.mBinding.xvTouchArea, this.mBinding.llAdjustIndicatorWrapper, this.mVideoTouchCallback);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setStopReason(STOP_REASON_DEFAULT);
        EventBusUtils.registerSafely(this);
        this.mRecordBrightness = getWindow().getAttributes().screenBrightness;
        onLoadingStarted();
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.-$$Lambda$RendererActivity$y2PT3eCITfW7Vc27dVIzPKDHhWM
            @Override // java.lang.Runnable
            public final void run() {
                RendererActivity.this.initRenderer();
            }
        });
    }

    public /* synthetic */ void lambda$onCreate$0$RendererActivity(View view) {
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment != null && exoPlayerFragment.isAdded()) {
            setStopReason(STOP_REASON_BTN_BACK_VIDEO);
        } else {
            setStopReason(STOP_REASON_BTN_BACK_MIRROR);
        }
        if (isFinishing()) {
            return;
        }
        finish();
    }

    private void postMainThread(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.i(TAG, "onNewIntent activity=" + this);
        setIntent(intent);
    }

    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        LogUtils.i(TAG, "onStart activity=" + this);
        if (sInstance != this) {
            sInstance = this;
        }
        if (RendererControlManager.instance().isMirrorStart()) {
            Log.i("tangrh", "onStart: mirrorstatus true");
            onMirrorStarted();
            int lastMirrorHeight = RendererControlManager.instance().getLastMirrorHeight();
            int lastMirrorWidth = RendererControlManager.instance().getLastMirrorWidth();
            if (lastMirrorHeight != 0 && lastMirrorWidth != 0) {
                setMirrorSize(lastMirrorWidth, lastMirrorHeight);
            }
        }
        this.mPresenter.start();
        updatePlayState(6);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = this.mRecordBrightness;
        window.setAttributes(attributes);
        this.mIsPaused = true;
    }

    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mIsPaused = false;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        LogUtils.i(TAG, "onStop activity=" + this);
        this.mPresenter.stop();
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        boolean z = exoPlayerFragment != null && exoPlayerFragment.isAdded();
        if (z) {
            this.mExoPlayerFragment.pause();
        }
        if (STOP_REASON_DEFAULT.equals(mStopReason) && z) {
            mStopReason = STOP_REASON_DEFAULT_VIDEO;
        } else if (STOP_REASON_DEFAULT.equals(mStopReason) && this.mSurfaceView.getVisibility() == 0) {
            mStopReason = STOP_REASON_DEFAULT_MIRROR;
        }
        judgeStopReason();
        super.onStop();
    }

    private void openMedia(Intent intent) {
        LogUtils.i(TAG, "openMedia onNewIntent");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String string = extras.getString(KEY_EXTRA_CURRENT_URI);
            String string2 = extras.getString(KEY_EXTRA_TITLE);
            long j = extras.getLong(KEY_EXTRA_POSITION);
            if (!TextUtils.isEmpty(string2)) {
                this.mBinding.tvVideoTitle.setText(string2);
            } else {
                this.mBinding.tvVideoTitle.setText(DEFAULT_TITLE);
            }
            openMedia(string, j);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity
    public int getRootLayoutId() {
        return R.id.xfl_renderer_main;
    }

    @Override // com.xiaopeng.wirelessprojection.core.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "onDestroy");
        setStopReason(STOP_REASON_DEFAULT);
        this.mPresenter.onClientDisconnected();
        EventBusUtils.unregisterSafely(this);
        if (sInstance == this) {
            sInstance = null;
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        boolean onKeyDown = super.onKeyDown(i, keyEvent);
        LogUtils.i(TAG, "onKeyDown keyCode=" + i);
        return onKeyDown;
    }

    public void exitRenderer(String str) {
        LogUtils.i(TAG, "exitPlayer:" + str);
        if (isFinishing()) {
            return;
        }
        finish();
    }

    private static void enterFullscreen(Activity activity, int i) {
        if (i > 0) {
            activity.requestWindowFeature(i);
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(5894);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrafficStatusChangeEvent(TrafficStatusChangeEvent trafficStatusChangeEvent) {
        ExoPlayerFragment exoPlayerFragment;
        LogUtils.i(TAG, "onTrafficStatusChangeEvent");
        if (trafficStatusChangeEvent == null || !trafficStatusChangeEvent.isRunOut() || (exoPlayerFragment = this.mExoPlayerFragment) == null || !exoPlayerFragment.isVisible()) {
            return;
        }
        showFlowWarningDialog();
    }

    private void showFlowWarningDialog() {
        new XDialog(this).setTitle(BaseApp.getContext().getString(R.string.flow_warning_title)).setMessage(BaseApp.getContext().getString(R.string.flow_warning_content)).setPositiveButton(BaseApp.getContext().getString(R.string.flow_warning_button), new XDialogInterface.OnClickListener() { // from class: com.xiaopeng.wirelessprojection.dmr.-$$Lambda$RendererActivity$Y0-V5D5TKGznhSpT9mOGOKgvgnQ
            @Override // com.xiaopeng.xui.app.XDialogInterface.OnClickListener
            public final void onClick(XDialog xDialog, int i) {
                RendererActivity.lambda$showFlowWarningDialog$1(xDialog, i);
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$showFlowWarningDialog$1(XDialog xDialog, int i) {
        LogUtils.d(TAG, "confirm");
        xDialog.setNegativeButtonEnable(false);
        IpcRouterService.sendOpenBuyFlow(TAG);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onSingleTap() {
        LogUtils.d(TAG, "onSingleTap");
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment == null || !exoPlayerFragment.isVisible()) {
            return;
        }
        this.mExoPlayerFragment.onSingleTap();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onCallStateChange(boolean z) {
        LogUtils.d(TAG, "onCallStateChange isCalling=" + z);
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment == null || !exoPlayerFragment.isVisible()) {
            return;
        }
        this.mExoPlayerFragment.onCallStateChange(z);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public boolean isAlive() {
        boolean z = (isFinishing() || isDestroyed()) ? false : true;
        if (!z) {
            LogUtils.e(TAG, "RendererActivity not alive");
        }
        return z;
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onVideoPlay(MediaPlayInfo mediaPlayInfo) {
        LogUtils.d(TAG, "onVideoPlay(): url " + mediaPlayInfo.getUrl());
        this.mHandler.obtainMessage(1, mediaPlayInfo).sendToTarget();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onVideoPlayInfoUpdated(MediaPlayInfo mediaPlayInfo) {
        LogUtils.d(TAG, "onVideoPlayInfoUpdated()");
        this.mHandler.obtainMessage(16, mediaPlayInfo).sendToTarget();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onVideoStop() {
        LogUtils.d(TAG, "onVideoStop()");
        this.mHandler.sendEmptyMessage(5);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onVideoScrub(int i) {
        LogUtils.d(TAG, "onVideoScrub()");
        this.mHandler.obtainMessage(2, Integer.valueOf(i)).sendToTarget();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onVideoRate(int i) {
        LogUtils.d(TAG, "onVideoRate(): rate = " + i);
        this.mHandler.obtainMessage(3, Integer.valueOf(i)).sendToTarget();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onVolumeChanged(float f) {
        LogUtils.d(TAG, "onVolumeChanged(): vol = " + f);
        Message obtainMessage = this.mHandler.obtainMessage(8);
        obtainMessage.obj = Float.valueOf(f);
        obtainMessage.sendToTarget();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onMetaDataUpdated(MediaMetaData mediaMetaData) {
        LogUtils.d(TAG, "onMetaDataUpdated()");
        Message obtainMessage = this.mHandler.obtainMessage(20);
        obtainMessage.obj = mediaMetaData;
        obtainMessage.sendToTarget();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void getPlaybackInfo(MediaPlaybackInfo mediaPlaybackInfo) {
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment != null) {
            exoPlayerFragment.getPlaybackInfo(mediaPlaybackInfo);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onSurfaceCreated() {
        LogUtils.d(TAG, "onSurfaceCreated()");
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onMirrorStarted() {
        LogUtils.d(TAG, "onMirrorStarted()");
        if (!this.mIsPaused && !isDestroyed() && !isFinishing()) {
            this.mHandler.sendEmptyMessage(4);
        } else {
            LogUtils.w(TAG, "Activity is paused, cannot mirror screen");
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onMirrorStopped() {
        LogUtils.d(TAG, "onMirrorStopped()");
        this.mHandler.sendEmptyMessage(19);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onMirrorSizeChanged(int i, int i2) {
        LogUtils.d(TAG, "onMirrorSizeChanged()");
        this.mHandler.obtainMessage(9, i, i2).sendToTarget();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onClientConnected(int i) {
        LogUtils.d(TAG, "onClientConnected()");
        this.mHandler.obtainMessage(7, Integer.valueOf(i)).sendToTarget();
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IRendererView
    public void onClientDisconnected(int i, int i2) {
        LogUtils.d(TAG, "onClientDisconnected()");
        this.mHandler.obtainMessage(6, i, i2).sendToTarget();
    }

    private void attachFragment(Fragment fragment) {
        LogUtils.d(TAG, "attachFragment()");
        if (getSupportFragmentManager().isStateSaved()) {
            return;
        }
        getSupportFragmentManager().beginTransaction().attach(fragment).commit();
    }

    private void detachFragment(Fragment fragment) {
        LogUtils.d(TAG, "detachFragment()");
        getSupportFragmentManager().beginTransaction().detach(this.mExoPlayerFragment).commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startScreenMirror() {
        LogUtils.d(TAG, "startScreenMirror()");
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment != null && exoPlayerFragment.isVisible()) {
            detachFragment(this.mExoPlayerFragment);
        }
        this.mBinding.flTitleLayout.setVisibility(4);
        this.mExoPlayerFragParent.setVisibility(4);
        onLoadingStopped();
        AutoScaleSurfaceView autoScaleSurfaceView = this.mSurfaceView;
        if (autoScaleSurfaceView != null) {
            autoScaleSurfaceView.createSurface();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMirrorSize(int i, int i2) {
        if (this.mSurfaceView.getVisibility() != 0) {
            Log.i("tangrh", "setMirrorSize: mSurfaceView is GONE or inVisible");
            onLoadingStopped();
            this.mSurfaceView.setVisibility(0);
        }
        LogUtils.d(TAG, "setMirrorSize(): w = " + i + ", h = " + i);
        this.mSurfaceView.setAspectRatio(i, i2);
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setInterpolator(new DecelerateInterpolator());
        alphaAnimation.setDuration(300L);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 0, 0.0f, 0, this.mSurfaceView.getBottom());
        scaleAnimation.setInterpolator(new DecelerateInterpolator());
        scaleAnimation.setDuration(300L);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);
        this.mSurfaceView.setAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() { // from class: com.xiaopeng.wirelessprojection.dmr.RendererActivity.1
            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationRepeat(Animation animation) {
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationStart(Animation animation) {
                LogUtils.d(RendererActivity.TAG, "onAnimationStart()");
            }

            @Override // android.view.animation.Animation.AnimationListener
            public void onAnimationEnd(Animation animation) {
                LogUtils.d(RendererActivity.TAG, "onAnimationEnd()");
            }
        });
        animationSet.startNow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVideoRate(int i) {
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment == null || !exoPlayerFragment.isVisible()) {
            return;
        }
        this.mExoPlayerFragment.onVideoRateChanged(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVideoPosition(int i) {
        LogUtils.d(TAG, "setVideoPosition pos=" + i);
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment != null && exoPlayerFragment.isVisible()) {
            this.mExoPlayerFragment.onVideoScrubChanged(i);
        } else {
            ExoPlayerFragment.setSeekPos(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleVolumeChanged(float f) {
        LogUtils.d(TAG, "handleVolumeChanged()");
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment == null || !exoPlayerFragment.isVisible()) {
            return;
        }
        this.mExoPlayerFragment.onVolumeChanged(f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlayVideo(MediaPlayInfo mediaPlayInfo) {
        LogUtils.d(TAG, "startPlayVideo(): url = " + mediaPlayInfo.getUrl() + ", pos = " + mediaPlayInfo.getPosition());
        AutoScaleSurfaceView autoScaleSurfaceView = this.mSurfaceView;
        if (autoScaleSurfaceView != null) {
            autoScaleSurfaceView.cleanSurface();
            this.mSurfaceView.setVisibility(8);
        }
        if (mediaPlayInfo.getStreamType() == 1 && mediaPlayInfo.getUrl().isEmpty()) {
            LogUtils.i(TAG, "startPlayVideo(): url is empty is audio");
            onMirrorStarted();
            setVideoTitle(mediaPlayInfo.getTitle());
            this.mBinding.ivAudioCover.setVisibility(0);
            return;
        }
        this.mBinding.ivAudioCover.setVisibility(8);
        this.mExoPlayerFragParent.setVisibility(0);
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment == null) {
            this.mExoPlayerFragment = ExoPlayerFragment.newInstance(mediaPlayInfo.getUrl(), mediaPlayInfo.getPosition(), mediaPlayInfo.getVolume(), mediaPlayInfo.getTitle());
            LogUtils.i(TAG, "setMediaType: " + mediaPlayInfo.getStreamType());
            setDefaultCover(mediaPlayInfo.getStreamType() == 1);
            setVideoTitle(mediaPlayInfo.getTitle());
            if (!getSupportFragmentManager().isStateSaved()) {
                getSupportFragmentManager().beginTransaction().add(R.id.fl_fragment_video_parent, this.mExoPlayerFragment, TAG).commit();
            }
            this.mExoPlayerFragment.setControlViewListener(new StyledPlayerView.ControllerVisibilityListener() { // from class: com.xiaopeng.wirelessprojection.dmr.-$$Lambda$RendererActivity$UZWLrbUbCJ63BQRCJkClA5y_69A
                @Override // com.google.android.exoplayer2.ui.StyledPlayerView.ControllerVisibilityListener
                public final void onVisibilityChanged(int i) {
                    RendererActivity.this.lambda$startPlayVideo$2$RendererActivity(i);
                }
            });
            this.mExoPlayerFragment.setOnLoadingListener(this);
            this.mExoPlayerFragment.addPlaybackStateObserver(new AnonymousClass2());
            return;
        }
        Bundle arguments = exoPlayerFragment.getArguments();
        arguments.putString(ExoPlayerFragment.EXTRA_URL, mediaPlayInfo.getUrl());
        arguments.putInt(ExoPlayerFragment.EXTRA_POS, mediaPlayInfo.getPosition());
        arguments.putFloat(ExoPlayerFragment.EXTRA_VOL, mediaPlayInfo.getVolume());
        arguments.putString(ExoPlayerFragment.EXTRA_TITLE, mediaPlayInfo.getTitle());
        setVideoTitle(mediaPlayInfo.getTitle());
        attachFragment(this.mExoPlayerFragment);
    }

    public /* synthetic */ void lambda$startPlayVideo$2$RendererActivity(int i) {
        if (i != this.mBinding.flTitleLayout.getVisibility()) {
            this.mBinding.flTitleLayout.setVisibility(i);
            this.mBinding.ibBtnBack.setVisibility(i);
            this.mExoPlayerFragment.setControlVisibility(i == 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.wirelessprojection.dmr.RendererActivity$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    public class AnonymousClass2 implements ExoPlayerFragment.PlaybackStateObserver {
        AnonymousClass2() {
        }

        @Override // com.xiaopeng.wirelessprojection.dmr.view.ExoPlayerFragment.PlaybackStateObserver
        public void onSeekComplete() {
            LogUtils.d(RendererActivity.TAG, "onSeekComplete()");
            RendererActivity.this.onLoadingStopped();
            if (RendererActivity.this.mPresenter != null) {
                RendererActivity.this.mPresenter.setVideoPlaybackState(0);
            }
        }

        @Override // com.xiaopeng.wirelessprojection.dmr.view.ExoPlayerFragment.PlaybackStateObserver
        public void onPlaybackError(int i) {
            LogUtils.d(RendererActivity.TAG, "onPlaybackError()");
            if (RendererActivity.this.mPresenter != null) {
                RendererActivity.this.mPresenter.setVideoPlaybackState(2);
            }
            RendererActivity.this.runOnUiThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.-$$Lambda$RendererActivity$2$R9htwiV2LCjOiSIpIuCg6gdIqVA
                @Override // java.lang.Runnable
                public final void run() {
                    ToastUtils.showToast(R.string.render_fail_tips);
                }
            });
            RendererActivity.this.exitRenderer("onPlaybackError");
        }

        @Override // com.xiaopeng.wirelessprojection.dmr.view.ExoPlayerFragment.PlaybackStateObserver
        public void onPlaybackComplete() {
            LogUtils.d(RendererActivity.TAG, "onPlaybackComplete()");
            if (RendererActivity.this.mPresenter != null) {
                RendererActivity.this.mPresenter.setVideoPlaybackState(1);
                String unused = RendererActivity.mStopReason = RendererActivity.STOP_REASON_COMPLETE;
            }
            RendererActivity.this.exitRenderer("onPlaybackComplete");
        }

        @Override // com.xiaopeng.wirelessprojection.dmr.view.ExoPlayerFragment.PlaybackStateObserver
        public void onIsPlayingChange(boolean z) {
            LogUtils.d(RendererActivity.TAG, "onIsPlayingChange isPlaying=" + z);
            if (RendererActivity.this.mPresenter != null) {
                RendererActivity.this.mPresenter.setVideoPlaybackState(z ? 0 : 4);
                if (z) {
                    RendererActivity.this.mPresenter.startRefreshInfo();
                }
            }
        }
    }

    private void setDefaultCover(boolean z) {
        if (z) {
            this.mBinding.ivAudioCover.setVisibility(0);
        } else {
            this.mBinding.ivAudioCover.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVideoPlayInfo(MediaPlayInfo mediaPlayInfo) {
        LogUtils.d(TAG, "updateVideoPlayInfo(): " + mediaPlayInfo);
        Bundle arguments = this.mExoPlayerFragment.getArguments();
        arguments.putString(ExoPlayerFragment.EXTRA_URL, mediaPlayInfo.getUrl());
        arguments.putInt(ExoPlayerFragment.EXTRA_POS, mediaPlayInfo.getPosition());
        arguments.putFloat(ExoPlayerFragment.EXTRA_VOL, mediaPlayInfo.getVolume());
        arguments.putString(ExoPlayerFragment.EXTRA_TITLE, mediaPlayInfo.getTitle());
        setVideoTitle(mediaPlayInfo.getTitle());
        setDefaultCover(mediaPlayInfo.getStreamType() == 1);
        this.mExoPlayerFragment.onVideoUrlUpdated();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopPlayVideo() {
        LogUtils.d(TAG, "stopPlayVideo()");
        ExoPlayerFragment exoPlayerFragment = this.mExoPlayerFragment;
        if (exoPlayerFragment != null) {
            exoPlayerFragment.onVideoStop();
        }
    }

    private static void setStopReason(String str) {
        LogUtils.i(TAG, "setStopReason reason=" + str);
        mStopReason = str;
    }

    private void judgeStopReason() {
        LogUtils.i(TAG, "judgeStopReason mStopReason=" + mStopReason);
        String str = mStopReason;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1797402471:
                if (str.equals(STOP_REASON_FROM_PHONE)) {
                    c = 0;
                    break;
                }
                break;
            case -1314866532:
                if (str.equals(STOP_REASON_STOP_MIRROR)) {
                    c = 1;
                    break;
                }
                break;
            case -1131353987:
                if (str.equals(STOP_REASON_KILLED)) {
                    c = 2;
                    break;
                }
                break;
            case -1130333978:
                if (str.equals(STOP_REASON_BTN_BACK_VIDEO)) {
                    c = 3;
                    break;
                }
                break;
            case -937847628:
                if (str.equals(STOP_REASON_BTN_BACK_MIRROR)) {
                    c = 4;
                    break;
                }
                break;
            case -599445191:
                if (str.equals(STOP_REASON_COMPLETE)) {
                    c = 5;
                    break;
                }
                break;
            case 1318902013:
                if (str.equals(STOP_REASON_DEFAULT_VIDEO)) {
                    c = 6;
                    break;
                }
                break;
            case 1974024061:
                if (str.equals(STOP_REASON_DEFAULT_MIRROR)) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                updatePlayState(3);
                return;
            case 1:
            case 4:
                Log.i("tangrh", "judgeStopReason: enter reset method with reason: " + mStopReason);
                updatePlayState(5);
                resetMirrorSurface();
                return;
            case 2:
            case 3:
            case 6:
                updatePlayState(5);
                updatePlayState(4);
                return;
            case 5:
                updatePlayState(1);
                return;
            case 7:
                Log.i("tangrh", "judgeStopReason: default_mirror stop");
                updatePlayState(5);
                AutoScaleSurfaceView autoScaleSurfaceView = this.mSurfaceView;
                if (autoScaleSurfaceView == null || autoScaleSurfaceView.getVisibility() != 0) {
                    return;
                }
                this.mSurfaceView.cleanSurface();
                return;
            default:
                return;
        }
    }

    private void updatePlayState(int i) {
        IRendererPresenter iRendererPresenter = this.mPresenter;
        if (iRendererPresenter != null) {
            iRendererPresenter.setVideoPlaybackState(i);
        }
    }

    private void resetMirrorSurface() {
        if (this.mPresenter != null) {
            Log.i("tangrh", "resetMirrorSurface: enter reset ");
            this.mPresenter.setMirrorSurface(null);
            return;
        }
        Log.i("tangrh", "mPresenter is null ");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleClientConnected(int i) {
        LogUtils.d(TAG, "handleClientConnected(): type = " + i);
        onLoadingStarted();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleClientRequestTimeout() {
        LogUtils.d(TAG, "handleClientRequestTimeout()");
        onLoadingStopped();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleLoadingTimeout() {
        LogUtils.d(TAG, "handleLoadingTimeout");
        if (isAlive()) {
            runOnUiThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.-$$Lambda$RendererActivity$MQOfXgzP5Fd5vxEub5RfRvr9d88
                @Override // java.lang.Runnable
                public final void run() {
                    ToastUtils.showToast(R.string.render_fail_tips);
                }
            });
            exitRenderer("handleLoadingTimeout");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMirrorStopped() {
        LogUtils.d(TAG, "handleMirrorStopped");
        if (isAlive()) {
            setStopReason(STOP_REASON_STOP_MIRROR);
            exitRenderer("handleMirrorStopped");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleMetaDataUpdate(final MediaMetaData mediaMetaData) {
        LogUtils.d(TAG, "handleMetaDataUpdate");
        if (isAlive()) {
            ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.-$$Lambda$RendererActivity$zCtK6gDvHyN08TgJuYvfxL2QlPo
                @Override // java.lang.Runnable
                public final void run() {
                    RendererActivity.this.lambda$handleMetaDataUpdate$4$RendererActivity(mediaMetaData);
                }
            });
        }
    }

    public /* synthetic */ void lambda$handleMetaDataUpdate$4$RendererActivity(MediaMetaData mediaMetaData) {
        if (mediaMetaData != null) {
            setVideoTitle(mediaMetaData.getTitle());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleClientDisconnect(int i, int i2) {
        LogUtils.d(TAG, "handleClientDisconnect(): type = " + i);
        exitRenderer("handleClientDisconnect");
    }

    private void setVideoTitle(String str) {
        LogUtils.d(TAG, "setVideoTitle title=" + str);
        if (!TextUtils.isEmpty(str)) {
            this.mBinding.tvVideoTitle.setText(str);
        } else {
            this.mBinding.tvVideoTitle.setText(DEFAULT_TITLE);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IOnLoadingListener
    public void onLoadingStarted() {
        LogUtils.i(TAG, "onLoadingStarted");
        this.mBinding.videoLayoutLoading.setVisibility(0);
        this.mBinding.videoLoading.setVisibility(0);
        this.mBinding.videoLayoutLoading.setBackgroundColor(getResources().getColor(R.color.video_loading_bg_color, null));
        if (this.mHandler.hasMessages(18)) {
            return;
        }
        this.mHandler.sendEmptyMessageDelayed(18, 30000L);
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IOnLoadingListener
    public void onLoadingStopped() {
        LogUtils.i(TAG, "onLoadingStopped");
        this.mBinding.videoLayoutLoading.setVisibility(8);
        this.mBinding.videoLoading.setVisibility(8);
        if (this.mHandler.hasMessages(18)) {
            this.mHandler.removeMessages(18);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IOnLoadingListener
    public void onBufferingLoading() {
        LogUtils.i(TAG, "BufferonLoadingStarted");
        this.mBinding.videoLayoutLoading.setBackgroundColor(getResources().getColor(R.color.video_loading_buffer_bg_color, null));
        this.mBinding.videoLayoutLoading.setVisibility(0);
        this.mBinding.videoLoading.setVisibility(0);
        if (this.mHandler.hasMessages(18)) {
            return;
        }
        this.mHandler.sendEmptyMessageDelayed(18, 30000L);
    }

    /* loaded from: classes2.dex */
    private final class MainHandlerCallback implements Handler.Callback {
        public static final int EVENT_CLIENT_CONNECTED = 7;
        public static final int EVENT_CLIENT_DISCONNECTED = 6;
        public static final int EVENT_CLIENT_REQ_TIMEOUT = 17;
        public static final int EVENT_LOADING_TIMEOUT = 18;
        public static final int EVENT_META_DATA_UPDATE = 20;
        public static final int EVENT_MIRROR_SIZE_CHANGED = 9;
        public static final int EVENT_START_SCREEN_MIRROR = 4;
        public static final int EVENT_START_VIDEO_PLAY = 1;
        public static final int EVENT_STOP_SCREEN_MIRROR = 19;
        public static final int EVENT_UPDATE_PLAYINFO = 16;
        public static final int EVENT_VIDEO_RATE_CHANGED = 3;
        public static final int EVENT_VIDEO_SCRUB_CHANGED = 2;
        public static final int EVENT_VIDEO_STOPPED = 5;
        public static final int EVENT_VOLUME_CHANGED = 8;

        private MainHandlerCallback() {
        }

        @Override // android.os.Handler.Callback
        public boolean handleMessage(Message message) {
            int i = message.what;
            switch (i) {
                case 1:
                    RendererActivity.this.startPlayVideo((MediaPlayInfo) message.obj);
                    return true;
                case 2:
                    RendererActivity.this.setVideoPosition(((Integer) message.obj).intValue());
                    return true;
                case 3:
                    RendererActivity.this.setVideoRate(((Integer) message.obj).intValue());
                    return true;
                case 4:
                    RendererActivity.this.startScreenMirror();
                    return true;
                case 5:
                    RendererActivity.this.stopPlayVideo();
                    return true;
                case 6:
                    RendererActivity.this.handleClientDisconnect(message.arg1, message.arg2);
                    return true;
                case 7:
                    RendererActivity.this.handleClientConnected(((Integer) message.obj).intValue());
                    return true;
                case 8:
                    RendererActivity.this.handleVolumeChanged(((Float) message.obj).floatValue());
                    return true;
                case 9:
                    RendererActivity.this.setMirrorSize(message.arg1, message.arg2);
                    return true;
                default:
                    switch (i) {
                        case 16:
                            RendererActivity.this.updateVideoPlayInfo((MediaPlayInfo) message.obj);
                            return true;
                        case 17:
                            RendererActivity.this.handleClientRequestTimeout();
                            return true;
                        case 18:
                            RendererActivity.this.handleLoadingTimeout();
                            return true;
                        case 19:
                            RendererActivity.this.handleMirrorStopped();
                            return true;
                        case 20:
                            RendererActivity.this.handleMetaDataUpdate((MediaMetaData) message.obj);
                            return true;
                        default:
                            return true;
                    }
            }
        }
    }
}

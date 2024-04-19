package com.xiaopeng.wirelessprojection.dmr.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.wirelessprojection.core.BaseApp;
import com.xiaopeng.wirelessprojection.core.event.AudioFocusChangeEvent;
import com.xiaopeng.wirelessprojection.core.interfaces.IOnLoadingListener;
import com.xiaopeng.wirelessprojection.core.interfaces.IPlayerControl;
import com.xiaopeng.wirelessprojection.core.manager.AudioControlManager;
import com.xiaopeng.wirelessprojection.core.utils.EventBusUtils;
import com.xiaopeng.wirelessprojection.core.utils.ThreadUtils;
import com.xiaopeng.wirelessprojection.dmr.R;
import com.xiaopeng.wirelessprojection.dmr.RendererActivity;
import com.xiaopeng.wirelessprojection.dmr.player.VideoTouchCallback;
import com.xiaopeng.xui.widget.XRelativeLayout;
import com.xpeng.airplay.service.MediaPlayInfo;
import com.xpeng.airplay.service.MediaPlaybackInfo;
import java.util.concurrent.atomic.AtomicBoolean;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: classes2.dex */
public class ExoPlayerFragment extends Fragment implements RendererActivity.onVideoStateChangedListener, IPlayerControl {
    public static final String EXTRA_POS = "video_pos";
    public static final String EXTRA_TITLE = "video_title";
    public static final String EXTRA_URL = "video_url";
    public static final String EXTRA_VOL = "video_vol";
    private static final String KEY_EXTRA_CURRENT_URI = "Renderer.KeyExtra.CurrentUri";
    public static final int MEDIA_TYPE_AUDIO = 1;
    private static final String TAG = "ExoPlayerFragment";
    public static final float VOLUME_DEFAULT = 1.0f;
    public static final float VOLUME_MUTE_HALF = 0.3f;
    private static long sPlaybackSeekPos;
    private XRelativeLayout mAudioCover;
    private StyledPlayerControlView mControlView;
    private StyledPlayerView.ControllerVisibilityListener mControlVisibilityListener;
    private DataSource.Factory mDataSourceFactory;
    private boolean mIsPlayingVideo;
    private boolean mIsVideoValid;
    private int mMediaType;
    private IOnLoadingListener mOnLoadingListener;
    private ExoPlayer mPlayer;
    private StyledPlayerView mPlayerView;
    private PlaybackStateObserver mStateObserver;
    private VideoTouchCallback mVideoTouchCallback;
    private final int ACTION_TIME_OUT = 100;
    private final int CONTROL_SHOW_TIME_OUT = PathInterpolatorCompat.MAX_NUM_POINTS;
    private final String FORMAT_M3U8 = "m3u8";
    private float mDefaultVolume = 1.0f;
    private float mPlaybackVolume = -1.0f;
    private boolean mPausedByPhone = false;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private MediaPlayInfo mVideoPlayInfo = new MediaPlayInfo();
    private final AtomicBoolean mIsControlVisible = new AtomicBoolean(true);

    /* loaded from: classes2.dex */
    public interface PlaybackStateObserver {
        void onIsPlayingChange(boolean z);

        void onPlaybackComplete();

        void onPlaybackError(int i);

        void onSeekComplete();
    }

    public static ExoPlayerFragment newInstance(String str, int i, float f, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, str);
        bundle.putInt(EXTRA_POS, i);
        bundle.putFloat(EXTRA_VOL, f);
        bundle.putString(EXTRA_TITLE, str2);
        ExoPlayerFragment exoPlayerFragment = new ExoPlayerFragment();
        exoPlayerFragment.setArguments(bundle);
        return exoPlayerFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.layout_player_view_container, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        LogUtils.i(TAG, "onViewCreated");
        this.mPlayerView = (StyledPlayerView) view.findViewById(R.id.spv_player_view);
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$pM8unFxzG-ByJ63aR4u7S8jmKU8
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.initPlayerView();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initPlayerView() {
        long currentTimeMillis = System.currentTimeMillis();
        StyledPlayerView styledPlayerView = this.mPlayerView;
        if (styledPlayerView != null && styledPlayerView.getVideoSurfaceView() != null) {
            this.mControlView = (StyledPlayerControlView) this.mPlayerView.findViewById(R.id.exo_controller);
            this.mAudioCover = (XRelativeLayout) this.mPlayerView.findViewById(R.id.iv_audio_cover);
            this.mPlayerView.setControllerShowTimeoutMs(PathInterpolatorCompat.MAX_NUM_POINTS);
            this.mControlView.setAnimationEnabled(false);
        }
        preparePlayer();
        LogUtils.i(TAG, "initPlayerView: cost=" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
    }

    public void postMainThread(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.RendererActivity.onVideoStateChangedListener
    public void onVideoUrlUpdated() {
        Log.d(TAG, "onVideoUrlUpdated()");
        if (this.mPlayerView != null) {
            startPlayVideo();
        }
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.RendererActivity.onVideoStateChangedListener
    public void onVideoScrubChanged(int i) {
        Log.d(TAG, "onVideoScrub(): pos = " + i);
        if (this.mPlayerView != null) {
            seek(i);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.RendererActivity.onVideoStateChangedListener
    public void onVideoRateChanged(int i) {
        Log.d(TAG, "onVideoRateChanged(): rate = " + i);
        if (this.mPlayerView != null) {
            if (i == 0) {
                pause();
            } else {
                play();
            }
        }
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.RendererActivity.onVideoStateChangedListener
    public void onVolumeChanged(float f) {
        Log.d(TAG, "onVolumeChanged()");
        try {
            try {
                lambda$setVideoVolume$8$ExoPlayerFragment(f, "XP_AIRPLAY_SERVICE_VOLUM_CHANGE");
            } catch (Exception e) {
                Log.e(TAG, "fail to set volume: e" + e.getMessage());
            }
        } finally {
            this.mPlaybackVolume = f;
        }
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.RendererActivity.onVideoStateChangedListener
    public void onVideoStop() {
        Log.d(TAG, "onVideoStop()");
        if (this.mPlayerView != null) {
            stop();
            this.mPlayerView.setVisibility(4);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.RendererActivity.onVideoStateChangedListener
    public void getPlaybackInfo(MediaPlaybackInfo mediaPlaybackInfo) {
        if (this.mPlayer != null) {
            mediaPlaybackInfo.setDuration(getDuration());
            mediaPlaybackInfo.setPosition(getPosition());
            mediaPlaybackInfo.setRate(isPlayingVideo() ? 1 : 0);
            mediaPlaybackInfo.setVolume(AudioControlManager.instance().getAdjustVolume(3));
        }
    }

    @Override // com.xiaopeng.wirelessprojection.dmr.RendererActivity.onVideoStateChangedListener
    public void onClientDisconnected() {
        Log.d(TAG, "onClientDisconnected()");
        StyledPlayerView styledPlayerView = this.mPlayerView;
        if (styledPlayerView != null) {
            styledPlayerView.setVisibility(4);
        }
    }

    public void addPlaybackStateObserver(PlaybackStateObserver playbackStateObserver) {
        LogUtils.i(TAG, "addPlaybackStateObserver observer=" + playbackStateObserver);
        this.mStateObserver = playbackStateObserver;
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        EventBusUtils.registerSafely(this);
        LogUtils.d(TAG, "onStart()");
        startPlayVideo();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume()");
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        LogUtils.d(TAG, "onPause()");
        StyledPlayerView styledPlayerView = this.mPlayerView;
        if (styledPlayerView != null) {
            styledPlayerView.onPause();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mPlayerView != null) {
            postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$LVXf4kDc7F_SUDe4pAmoxETM8cc
                @Override // java.lang.Runnable
                public final void run() {
                    ExoPlayerFragment.this.lambda$onDestroy$0$ExoPlayerFragment();
                }
            });
        }
        EventBusUtils.unregisterSafely(this);
        LogUtils.d(TAG, "onDestroy()");
    }

    public /* synthetic */ void lambda$onDestroy$0$ExoPlayerFragment() {
        abandonAudioFocus("onDestroy");
        releasePlayer();
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.d(TAG, "onAttach()");
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        StyledPlayerView styledPlayerView = this.mPlayerView;
        if (styledPlayerView != null) {
            styledPlayerView.onPause();
        }
        LogUtils.d(TAG, "onDetach()");
    }

    public void setVideoPlayInfo(MediaPlayInfo mediaPlayInfo) {
        LogUtils.i(TAG, "setVideoPlayInfo mVideoPlayInfo=" + mediaPlayInfo.toString());
        this.mVideoPlayInfo = mediaPlayInfo;
    }

    public void setMediaType(int i) {
        LogUtils.i(TAG, "set media type = " + i);
        this.mMediaType = i;
    }

    public MediaPlayInfo getVideoPlayInfo() {
        MediaPlayInfo mediaPlayInfo = this.mVideoPlayInfo;
        if (mediaPlayInfo != null) {
            mediaPlayInfo.setVolume(AudioControlManager.instance().getAdjustVolume(3));
        }
        return this.mVideoPlayInfo;
    }

    private void preparePlayer() {
        LogUtils.i(TAG, "preparePlayer mPlayer exist " + (this.mPlayer != null));
        if (this.mPlayer == null) {
            this.mPlayer = new ExoPlayer.Builder(BaseApp.getContext()).build();
            this.mDataSourceFactory = new DefaultDataSourceFactory(BaseApp.getContext(), Util.getUserAgent(BaseApp.getContext(), TAG));
            this.mPlayerView.setPlayer(this.mPlayer);
            StyledPlayerView.ControllerVisibilityListener controllerVisibilityListener = this.mControlVisibilityListener;
            if (controllerVisibilityListener != null) {
                this.mPlayerView.setControllerVisibilityListener(controllerVisibilityListener);
            }
            this.mDefaultVolume = this.mPlayer.getVolume();
        }
    }

    private void prepareVideo() {
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer == null) {
            LogUtils.i(TAG, "openMedia error, mPlayer is null");
            return;
        }
        exoPlayer.seekToDefaultPosition();
        Bundle arguments = getArguments();
        String string = arguments.getString(EXTRA_URL);
        if (!TextUtils.isEmpty(string) && this.mPlayerView != null) {
            setSeekPos(arguments.getInt(EXTRA_POS, 0));
            this.mPlaybackVolume = arguments.getFloat(EXTRA_VOL);
            Log.i(TAG, "prepareVideo: " + this.mPlaybackVolume);
            AudioControlManager.instance().setAdjustVolume(3, this.mPlaybackVolume);
            setVideoPlayInfo(new MediaPlayInfo(string, arguments.getString(EXTRA_TITLE), this.mPlaybackVolume, arguments.getInt(EXTRA_POS, 0)));
            if (this.mMediaType == 1) {
                this.mAudioCover.setVisibility(0);
            } else {
                this.mAudioCover.setVisibility(8);
            }
        }
        LogUtils.i(TAG, "prepareVideo url=" + string + ", positionSecond=" + sPlaybackSeekPos);
        this.mPlayer.setMediaSource(getMediaSource(string));
        this.mPlayer.prepare();
        seek(sPlaybackSeekPos);
        this.mPlayer.setPlayWhenReady(true);
        this.mPlayer.addAnalyticsListener(new AnalyticsListener() { // from class: com.xiaopeng.wirelessprojection.dmr.view.ExoPlayerFragment.1
            private boolean lastPlayWhenReadyState;
            private int lastPlaybackState;

            {
                this.lastPlaybackState = ExoPlayerFragment.this.mPlayer.getPlaybackState();
                this.lastPlayWhenReadyState = ExoPlayerFragment.this.mPlayer.getPlayWhenReady();
            }

            @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
            public void onPlaybackStateChanged(AnalyticsListener.EventTime eventTime, int i) {
                LogUtils.i(ExoPlayerFragment.TAG, "AnalyticsListener onPlaybackStateChanged:state=" + i);
                ExoPlayerFragment.this.checkPlayState(this.lastPlayWhenReadyState, i);
                this.lastPlaybackState = i;
            }

            @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
            public void onPlayWhenReadyChanged(AnalyticsListener.EventTime eventTime, boolean z, int i) {
                LogUtils.i(ExoPlayerFragment.TAG, "AnalyticsListener onPlayWhenReadyChanged playWhenReady=" + z + ", reason=" + i);
                if (z) {
                    ExoPlayerFragment.this.mPausedByPhone = false;
                }
                ExoPlayerFragment.this.checkPlayState(z, this.lastPlaybackState);
                this.lastPlayWhenReadyState = z;
            }

            @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
            public void onPlayerError(AnalyticsListener.EventTime eventTime, PlaybackException playbackException) {
                LogUtils.i(ExoPlayerFragment.TAG, "AnalyticsListener onPlayerError=" + playbackException.errorCode + ", " + playbackException.getErrorCodeName());
                if (ExoPlayerFragment.this.mStateObserver != null) {
                    ExoPlayerFragment.this.mStateObserver.onPlaybackError(playbackException.errorCode);
                }
                ExoPlayerFragment.this.hideLoading();
            }

            @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
            public void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int i, long j) {
                LogUtils.i(ExoPlayerFragment.TAG, "AnalyticsListener onDroppedVideoFrames droppedFrames=" + i);
            }

            @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
            public void onVideoDecoderInitialized(AnalyticsListener.EventTime eventTime, String str, long j, long j2) {
                LogUtils.i(ExoPlayerFragment.TAG, "AnalyticsListener onVideoDecoderInitialized decoderName=" + str + ", initializationDurationMs=" + j2);
            }

            @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
            public void onVideoDecoderReleased(AnalyticsListener.EventTime eventTime, String str) {
                LogUtils.i(ExoPlayerFragment.TAG, "AnalyticsListener onVideoDecoderReleased decoderName=" + str);
            }

            @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
            public void onVideoCodecError(AnalyticsListener.EventTime eventTime, Exception exc) {
                LogUtils.i(ExoPlayerFragment.TAG, "AnalyticsListener onVideoCodecError");
                exc.printStackTrace();
            }

            @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
            public void onIsPlayingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
                LogUtils.i(ExoPlayerFragment.TAG, "AnalyticsListener onIsPlayingChanged isPlaying=" + z);
                ExoPlayerFragment.this.setIsPlayingVideo(z);
            }

            @Override // com.google.android.exoplayer2.analytics.AnalyticsListener
            public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Object obj, long j) {
                LogUtils.i(ExoPlayerFragment.TAG, "AnalyticsListener onRenderedFirstFrame renderTimeMs=" + j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkPlayState(boolean z, int i) {
        LogUtils.i(TAG, "checkPlayState playWhenReady=" + z + ",playbackState=" + i);
        if (z && i == 3) {
            hideLoading();
            requestAudioFocus("playWhenReady");
            PlaybackStateObserver playbackStateObserver = this.mStateObserver;
            if (playbackStateObserver != null) {
                playbackStateObserver.onSeekComplete();
            }
            long j = sPlaybackSeekPos;
            if (j != 0) {
                seek(j);
            }
        } else if (i == 4) {
            ThreadUtils.postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$pEwEjYZ_-R0JZZUysTCRE6hNNw8
                @Override // java.lang.Runnable
                public final void run() {
                    ExoPlayerFragment.this.lambda$checkPlayState$1$ExoPlayerFragment();
                }
            }, 1000L);
        } else if (!z) {
            if (i == 3) {
                hideLoading();
            }
            abandonAudioFocus("checkPlayState");
        } else if (i == 2) {
            Log.i(TAG, "Buffering PlayState: " + i);
            showBufferingLoading();
        } else {
            Log.i(TAG, "OtherPlayState: playbackState " + i);
            showLoading();
        }
    }

    public /* synthetic */ void lambda$checkPlayState$1$ExoPlayerFragment() {
        PlaybackStateObserver playbackStateObserver = this.mStateObserver;
        if (playbackStateObserver != null) {
            playbackStateObserver.onPlaybackComplete();
        }
    }

    private void startPlayVideo() {
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$C8tkMEVwXRivDoyv2Ol1ipoNFII
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.startPlayVideoInternal();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPlayVideoInternal() {
        LogUtils.d(TAG, "startPlayVideoInternal");
        setIsVideoValid(true);
        prepareVideo();
    }

    private void releasePlayer() {
        setIsVideoValid(false);
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            try {
                exoPlayer.release();
            } catch (Throwable th) {
                LogUtils.e(TAG, "releasePlayer", th);
            }
            this.mPlayer = null;
        }
    }

    public static void setSeekPos(int i) {
        LogUtils.i(TAG, "setSeekPos pos=", Integer.valueOf(i));
        sPlaybackSeekPos = i;
    }

    private MediaSource getMediaSource(String str) {
        if (str.toLowerCase().contains("m3u8")) {
            LogUtils.i(TAG, "getMediaSource HlsMediaSource");
            return new HlsMediaSource.Factory(this.mDataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(str)));
        }
        LogUtils.i(TAG, "getMediaSource ProgressiveMediaSource");
        return new ProgressiveMediaSource.Factory(this.mDataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(str)));
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IPlayerControl
    public void play() {
        LogUtils.i(TAG, "play");
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$aW9ZRNasnoHN41eyzhVbdicqVxQ
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.lambda$play$2$ExoPlayerFragment();
            }
        });
    }

    public /* synthetic */ void lambda$play$2$ExoPlayerFragment() {
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IPlayerControl
    public void pause() {
        LogUtils.i(TAG, "pause");
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$QCuQi74qwJj5Y0oPcWrlyzDOqOU
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.lambda$pause$3$ExoPlayerFragment();
            }
        });
    }

    public /* synthetic */ void lambda$pause$3$ExoPlayerFragment() {
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IPlayerControl
    public void seek(final long j) {
        LogUtils.i(TAG, "seekTo position=" + j);
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$tk_wY-REYgbXw0x6zsN8Lu3ODKM
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.lambda$seek$4$ExoPlayerFragment(j);
            }
        });
    }

    public /* synthetic */ void lambda$seek$4$ExoPlayerFragment(long j) {
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            exoPlayer.seekTo(j * 1000);
            setSeekPos(0);
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IPlayerControl
    public void stop() {
        LogUtils.i(TAG, "stop");
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$hSC0R2aC1enDMX8K9VLWOB0hm0U
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.lambda$stop$5$ExoPlayerFragment();
            }
        });
    }

    public /* synthetic */ void lambda$stop$5$ExoPlayerFragment() {
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            exoPlayer.stop();
            this.mPlayer.clearMediaItems();
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IPlayerControl
    public long getPosition() {
        LogUtils.d(TAG, "getPosition");
        final ConditionVariable conditionVariable = new ConditionVariable();
        final long[] jArr = {0};
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$Wgj8FUUVFqADQDPHTUik8CWLIBc
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.lambda$getPosition$6$ExoPlayerFragment(jArr, conditionVariable);
            }
        });
        if (!conditionVariable.block(100L)) {
            LogUtils.i(TAG, "getPosition time out");
        }
        if (jArr[0] > 0) {
            MediaPlayInfo mediaPlayInfo = this.mVideoPlayInfo;
            if (mediaPlayInfo != null) {
                mediaPlayInfo.setPosition((int) (jArr[0] / 1000));
            }
            return jArr[0] / 1000;
        }
        MediaPlayInfo mediaPlayInfo2 = this.mVideoPlayInfo;
        if (mediaPlayInfo2 != null) {
            mediaPlayInfo2.setPosition(0);
        }
        LogUtils.i(TAG, "timeline is not prepared");
        return 0L;
    }

    public /* synthetic */ void lambda$getPosition$6$ExoPlayerFragment(long[] jArr, ConditionVariable conditionVariable) {
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            jArr[0] = exoPlayer.getCurrentPosition();
            conditionVariable.open();
        }
    }

    @Override // com.xiaopeng.wirelessprojection.core.interfaces.IPlayerControl
    public long getDuration() {
        LogUtils.d(TAG, "getDuration");
        final ConditionVariable conditionVariable = new ConditionVariable();
        final long[] jArr = {0};
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$VQFQ6SFR3MHSRy-KYO_33BfVIiA
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.lambda$getDuration$7$ExoPlayerFragment(jArr, conditionVariable);
            }
        });
        if (!conditionVariable.block(100L)) {
            LogUtils.i(TAG, "getDuration time out");
        }
        if (jArr[0] > 0) {
            return jArr[0] / 1000;
        }
        LogUtils.i(TAG, "timeline is not prepared");
        return 0L;
    }

    public /* synthetic */ void lambda$getDuration$7$ExoPlayerFragment(long[] jArr, ConditionVariable conditionVariable) {
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            jArr[0] = exoPlayer.getDuration();
            conditionVariable.open();
        }
    }

    public void onSingleTap() {
        StyledPlayerControlView styledPlayerControlView;
        LogUtils.i(TAG, "onSingleTap isControlVisible=" + isControlVisible());
        if (!isControlVisible() && (styledPlayerControlView = this.mControlView) != null) {
            styledPlayerControlView.show();
        } else if (isPlayingVideo()) {
            pause();
        } else {
            play();
        }
    }

    public void onCallStateChange(boolean z) {
        LogUtils.i(TAG, "onCallStateChange isCalling=" + z + ", mPausedByPhone=" + this.mPausedByPhone);
        if (z && isPlayingVideo()) {
            this.mPausedByPhone = true;
            pause();
        } else if (z || !this.mPausedByPhone) {
        } else {
            this.mPausedByPhone = false;
            play();
        }
    }

    private void requestAudioFocus(String str) {
        AudioControlManager.instance().requestAudioFocus(str);
    }

    private void abandonAudioFocus(String str) {
        AudioControlManager.instance().abandonAudioFocus(str);
    }

    public void setVideoVolume(final float f, final String str) {
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$DrOCZWyJyuKwKYgjGDb2T0EFWAY
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.lambda$setVideoVolume$8$ExoPlayerFragment(f, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: setVideoVolumeInternal */
    public void lambda$setVideoVolume$8$ExoPlayerFragment(float f, String str) {
        LogUtils.i(TAG, "setVideoVolume:" + f + ", from=" + str);
        ExoPlayer exoPlayer = this.mPlayer;
        if (exoPlayer != null) {
            exoPlayer.setVolume(f);
        }
    }

    public void setControlViewListener(StyledPlayerView.ControllerVisibilityListener controllerVisibilityListener) {
        this.mControlVisibilityListener = controllerVisibilityListener;
    }

    public void setControlVisibility(boolean z) {
        LogUtils.i(TAG, "setControlVisibility isVisible=" + z);
        this.mIsControlVisible.set(z);
        StyledPlayerControlView styledPlayerControlView = this.mControlView;
        if (styledPlayerControlView == null || !z) {
            return;
        }
        styledPlayerControlView.bringToFront();
    }

    public boolean isControlVisible() {
        LogUtils.i(TAG, "getControlVisibility " + this.mIsControlVisible.get());
        return this.mIsControlVisible.get();
    }

    public boolean isPlayingVideo() {
        return this.mIsPlayingVideo;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIsPlayingVideo(boolean z) {
        LogUtils.i(TAG, "setIsPlayingVideo isPlaying=" + z);
        this.mIsPlayingVideo = z;
        PlaybackStateObserver playbackStateObserver = this.mStateObserver;
        if (playbackStateObserver != null) {
            playbackStateObserver.onIsPlayingChange(z);
        }
    }

    public boolean isVideoValid() {
        return this.mIsVideoValid;
    }

    private void setIsVideoValid(boolean z) {
        LogUtils.i(TAG, "setIsVideoValid isValid=" + z);
        this.mIsVideoValid = z;
    }

    private void showLoading() {
        IOnLoadingListener iOnLoadingListener = this.mOnLoadingListener;
        if (iOnLoadingListener != null) {
            iOnLoadingListener.onLoadingStarted();
        }
    }

    private void showBufferingLoading() {
        IOnLoadingListener iOnLoadingListener = this.mOnLoadingListener;
        if (iOnLoadingListener != null) {
            iOnLoadingListener.onBufferingLoading();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideLoading() {
        IOnLoadingListener iOnLoadingListener = this.mOnLoadingListener;
        if (iOnLoadingListener != null) {
            iOnLoadingListener.onLoadingStopped();
        }
    }

    public void setOnLoadingListener(IOnLoadingListener iOnLoadingListener) {
        this.mOnLoadingListener = iOnLoadingListener;
    }

    public /* synthetic */ void lambda$showController$9$ExoPlayerFragment() {
        this.mPlayerView.showController();
    }

    public void showController() {
        postMainThread(new Runnable() { // from class: com.xiaopeng.wirelessprojection.dmr.view.-$$Lambda$ExoPlayerFragment$9xXVWD8c3kAt_p4fYXf-ah7mRkU
            @Override // java.lang.Runnable
            public final void run() {
                ExoPlayerFragment.this.lambda$showController$9$ExoPlayerFragment();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioFocusChangeEvent(AudioFocusChangeEvent audioFocusChangeEvent) {
        if (audioFocusChangeEvent == null) {
            LogUtils.e(TAG, "onAudioFocusChangeEvent event null");
            return;
        }
        LogUtils.i(TAG, "onAudioFocusChangeEvent status=" + audioFocusChangeEvent.getStatus());
        int status = audioFocusChangeEvent.getStatus();
        if (status == -1 || status == -2) {
            pause();
        } else if (status == -3) {
            setVideoVolume(0.3f, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
        } else if (status == 1) {
            setVideoVolume(this.mDefaultVolume, "AUDIOFOCUS_GAIN");
            if (isPlayingVideo()) {
                play();
            }
        }
    }
}

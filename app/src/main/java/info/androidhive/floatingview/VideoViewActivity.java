package info.androidhive.floatingview;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import info.androidhive.floatingview.customvideoview.UniversalMediaController;
import info.androidhive.floatingview.customvideoview.UniversalVideoView;
import info.androidhive.floatingview.customvideoview.YoutubeLayout;

public class VideoViewActivity extends AppCompatActivity implements UniversalVideoView.VideoViewCallback{

    private static final String TAG = "MainActivity";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";

    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;

    View mBottomLayout;
    View mVideoLayout;
    TextView viewDesc;

    private int mSeekPosition;
    private int cachedHeight;
    private boolean isFullscreen;
    YoutubeLayout youtubeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        youtubeLayout=(YoutubeLayout)findViewById(R.id.youtubeLayout);
        mVideoLayout = findViewById(R.id.video_layout);
        mBottomLayout = findViewById(R.id.bottom_layout);

        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        mVideoView.setMediaController(mMediaController);
        mMediaController.show();
      //  setVideoAreaSize();
        mVideoView.setVideoViewCallback(this);
        viewDesc = (TextView) findViewById(R.id.viewDesc);


        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion ");
            }
        });


        setupListView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ");
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            Log.d(TAG, "onPause mSeekPosition=" + mSeekPosition);
            mVideoView.pause();
        }
    }

    /**
     * 置视频区域大小
     */
    private void setVideoAreaSize(final String VIDEO_URL) {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
//                cachedHeight = (int) (width * 3f / 4f);
//                cachedHeight = (int) (width * 9f / 16f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);

                mVideoView.setVideoPath(VIDEO_URL);
                mVideoView.requestFocus();
                mVideoView.start();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState Position=" + mVideoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        Log.d(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
    }


    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.GONE);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.VISIBLE);
        }

        switchTitleBar(!isFullscreen);
    }

    private void switchTitleBar(boolean show) {
        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (show) {
                supportActionBar.show();
            } else {
                supportActionBar.hide();
            }
        }
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPause UniversalVideoView callback");
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingEnd UniversalVideoView callback");
    }

    @Override
    public void onBackPressed() {

        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);

        } else {
            if(youtubeLayout.isMinimized||youtubeLayout.getVisibility()==View.GONE){
                super.onBackPressed();
            }else {
                youtubeLayout.minimize();
            }

        }
    }
    private void setupListView() {
        ListView   mListView = (ListView) findViewById(R.id.listView);
        final SampleAdapter adapter = new SampleAdapter(this);
        for (int i = 0; i < 20; i++) {
            adapter.add(new SampleItem("Tutorial Video  "+i, android.R.drawable.star_on));
        }
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SampleItem sampleItem=adapter.getItem(i);
                viewDesc.setText(sampleItem.tag);
                String VIDEO_URL= "android.resource://" + getPackageName() + "/" + R.raw.test_video;
                youtubeLayout.setVisibility(View.VISIBLE);
                youtubeLayout.maximize();
                mMediaController.setTitle(sampleItem.tag);
                setVideoAreaSize(VIDEO_URL);

            }
        });
    }

private class SampleItem {
    public String tag;
    public int iconRes;
    public SampleItem(String tag, int iconRes) {
        this.tag = tag;
        this.iconRes = iconRes;
    }
}

public class SampleAdapter extends ArrayAdapter<SampleItem> {

    public SampleAdapter(Context context) {
        super(context, 0);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_simple, null);
        }
        ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
        icon.setImageResource(getItem(position).iconRes);
        TextView title = (TextView) convertView.findViewById(R.id.row_title);
        title.setText(getItem(position).tag);

        return convertView;
    }

}
}

package endurance.witness.android.activities;

import com.example.Android.R;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

import endurance.witness.android.fragments.FragmentBase;
import endurance.witness.android.fragments.MainFragment;

public class MainActivity extends FragmentBase {
    private SoundPool soundPool;
    private AudioManager audioManager;
    private int scanSoundId;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.initSound();
        this.setContentView(R.layout.main_activity);
        this.initUHF();
        this.initViewPager();
        this.initViewPageData();
	}

    @Override
	protected void onDestroy() {
		if (reader != null) {
			this.reader.free();
		}
		super.onDestroy();
	}

    protected void initViewPageData() {
        this.fragments.add(new MainFragment());
    }

    private void initSound(){
        this.soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        this.scanSoundId = soundPool.load(this, R.raw.barcodebeep, 1);
        this.audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
    }

    public void playScanSound() {
        float audioMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        float volumeRatio = audioCurrentVolume / audioMaxVolume;
        try {
            soundPool.play(this.scanSoundId, volumeRatio, volumeRatio, 1, 0, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

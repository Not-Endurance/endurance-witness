package com.example.uhf.activity;

import com.example.uhf.R;
import com.example.uhf.fragment.UHFReadTagFragment;
import com.rscja.utility.StringUtility;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import java.util.HashMap;

public class UHFMainActivity extends BaseTabFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	    	initSound();
	        initUHF();
	        initViewPageData();
	        initViewPager();
	        initTabs();

	}

	 @Override
	    protected void initViewPageData() {
	        lstFrg.add(new UHFReadTagFragment());
	    }

	@Override
	protected void onDestroy() {

		if (mReader != null) {
			mReader.free();
		}
		super.onDestroy();
	}

	public boolean vailHexInput(String str) {
		if (str == null || str.length() == 0) {
			return false;
		}
		if (str.length() % 2 == 0) {
			return StringUtility.isHexNumberRex(str);
		}

		return false;
	}
	HashMap<Integer, Integer> soundMap = new HashMap<>();
	private SoundPool soundPool;
    private AudioManager am;
	private void initSound(){
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
		soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
		soundMap.put(2, soundPool.load(this, R.raw.serror, 1));
		am = (AudioManager) this.getSystemService(AUDIO_SERVICE);
	}

    public void playSound(int id) {

		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = audioCurrentVolumn / audioMaxVolumn;
		try {
			soundPool.play(soundMap.get(id), volumnRatio, volumnRatio, 1, 0, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

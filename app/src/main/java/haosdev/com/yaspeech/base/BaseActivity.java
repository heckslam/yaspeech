package haosdev.com.yaspeech.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import haosdev.com.yaspeech.AppDelegate;

public abstract class BaseActivity extends AppCompatActivity{
	private AppDelegate app;

	private final Handler handler = new Handler();
	private Runnable pauseCallback = new Runnable() {
		@Override
		public void run() {
			app.onActivityPaused();
		}
	};

	private static final long PAUSE_CALLBACK_DELAY = 500;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (AppDelegate) getApplication();
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.onActivityResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.postDelayed(pauseCallback, PAUSE_CALLBACK_DELAY);
	}



	protected <T extends View> T $(@IdRes int id) {
		return (T) findViewById(id);
	}
}

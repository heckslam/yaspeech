package haosdev.com.yaspeech;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import haosdev.com.yaspeech.util.BluetoothController;

/**
 * AppDelegate
 */

public class AppDelegate extends Application {

	private BluetoothControllerImpl bluetoothController;
	private int activitiesCount;
	private static final String TAG = AppDelegate.class.getSimpleName();

	@Override
	public void onCreate() {
		super.onCreate();
		/* Enable disk persistence  */
		FirebaseDatabase.getInstance().setPersistenceEnabled(true);

		bluetoothController = new BluetoothControllerImpl(this);
	}

	public void onActivityResume() {
		if (activitiesCount++ == 0) { // on become foreground
			bluetoothController.start();
		}
	}

	public void onActivityPaused() {
		if (--activitiesCount == 0) { // on become background
			bluetoothController.stop();
		}
	}

	private boolean isInForeground() {
		return activitiesCount > 0;
	}

	private class BluetoothControllerImpl extends BluetoothController {

		BluetoothControllerImpl(Context context) {
			super(context);
		}

		@Override
		public void onHeadsetDisconnected() {
			Log.d("TAG", "Bluetooth headset disconnected");
		}

		@Override
		public void onHeadsetConnected() {
			Log.d(TAG, "Bluetooth headset connected");

			if (isInForeground() && !bluetoothController.isOnHeadsetSco()) {
				bluetoothController.start();
			}
		}

		@Override
		public void onScoAudioDisconnected() {
			Log.d(TAG, "Bluetooth sco audio finished");
			bluetoothController.stop();

			if (isInForeground()) {
				bluetoothController.start();
			}
		}

		@Override
		public void onScoAudioConnected() {
			Log.d(TAG, "Bluetooth sco audio started");
		}

	}
}

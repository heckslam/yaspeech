package haosdev.com.yaspeech;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import haosdev.com.yaspeech.transitions.FabTransform;
import haosdev.com.yaspeech.util.AppConfig;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Recognition;
import ru.yandex.speechkit.Recognizer;
import ru.yandex.speechkit.RecognizerListener;
import ru.yandex.speechkit.SpeechKit;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RecognizerFragment extends haosdev.com.yaspeech.base.BaseFragment implements RecognizerListener {

	private static final int REQUEST_PERMISSION_CODE = 1;

	private ProgressBar progressBar;
	private TextView currentStatus;
	private EditText recognitionResult;

	private Recognizer recognizer;
	private boolean muted = false;
	Button pauseBtn;
	ImageButton fab;


	public RecognizerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//get your own api key on https://tech.yandex.ru/speechkit/mobilesdk/
		SpeechKit.getInstance().configure(getContext(), AppConfig.SPEECHKIT_API_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final Button startBtn = (Button) view.findViewById(R.id.start_btn);
		pauseBtn = (Button) view.findViewById(R.id.pause_btn);
		startBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseBtn.setVisibility(View.VISIBLE);
				createAndStartRecognizer();
				startBtn.setVisibility(View.GONE);
			}
		});
		pauseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pauseRecognizer();
			}
		});

		progressBar = (ProgressBar) view.findViewById(R.id.voice_power_bar);
		currentStatus = (TextView) view.findViewById(R.id.current_state);
		recognitionResult = (EditText) view.findViewById(R.id.result);
		fab = $(view, R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String result = recognitionResult.getText().toString();
				if (TextUtils.isEmpty(result)) {
					Toast.makeText(getContext(), "Ваша лекция пуста", Toast.LENGTH_SHORT).show();
					return;
				}
				resetRecognizer();
				Intent intent = new Intent(getActivity(), SaveDialogActivity.class);
				intent.putExtra(Const.LECTION_TEXT, result);
				FabTransform.addExtras(intent,
						ContextCompat.getColor(getContext(), R.color.colorAccent), R.drawable.ic_cloud_upload_black_24dp);
				ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), fab,
						getString(R.string.transition_designer_news_login));
				startActivity(intent, options.toBundle());
			}
		});
	}

	private void pauseRecognizer(){
		if (recognizer != null) {
			if (muted) {
				recognizer.unmute();
				muted = false;
				updateStatus("Запись возобновлена");
				pauseBtn.setText(R.string.pause_btn);
			}
			else {
				recognizer.mute();
				muted = true;
				updateProgress(0);
				updateStatus("Запись приостановлена");
				pauseBtn.setText(R.string.pause_btn_on);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		if (requestCode != REQUEST_PERMISSION_CODE) {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
			return;
		}

		if (grantResults.length == 1 && grantResults[0] == PERMISSION_GRANTED) {
			createAndStartRecognizer();
		} else {
			updateStatus("Разрешение запись аудио не было предоставлено");
		}
	}

	private void resetRecognizer() {
		if (recognizer != null) {
			recognizer.finishRecording();
			recognizer.cancel();
			recognizer = null;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		resetRecognizer();
	}

	@Override
	public void onRecordingBegin(Recognizer recognizer) {
		updateStatus("Запись началась");
	}

	@Override
	public void onSpeechDetected(Recognizer recognizer) {
		updateStatus("Речь обнаружена");
	}

	@Override
	public void onSpeechEnds(Recognizer recognizer) {
		updateStatus("Речь остановлена");
	}

	@Override
	public void onRecordingDone(Recognizer recognizer) {
		updateStatus("Запись завершена");
	}

	@Override
	public void onSoundDataRecorded(Recognizer recognizer, byte[] bytes) {
	}

	@Override
	public void onPowerUpdated(Recognizer recognizer, float power) {
		updateProgress((int) (power * progressBar.getMax()));
	}

	@Override
	public void onPartialResults(Recognizer recognizer, Recognition recognition, boolean b) {
		updateStatus("Частичный результат " + recognition.getBestResultText());
		if (b) recognitionResult.append(recognition.getBestResultText());
	}

	@Override
	public void onRecognitionDone(Recognizer recognizer, Recognition recognition) {
		recognitionResult.setText(recognition.getBestResultText());
		updateProgress(0);
	}

	@Override
	public void onError(Recognizer recognizer, ru.yandex.speechkit.Error error) {
		if (error.getCode() == Error.ERROR_CANCELED) {
				updateStatus("Отменено");
			updateProgress(0);
		} else {
			updateStatus("Произошла ошибка " + error.getString());
			resetRecognizer();
		}
	}

	private void createAndStartRecognizer() {
		final Context context = getContext();
		if (context == null) {
			return;
		}

		if (ContextCompat.checkSelfPermission(context, RECORD_AUDIO) != PERMISSION_GRANTED) {
			requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
		} else {
			// Reset the current recognizer.
			resetRecognizer();
			// To create a new recognizer, specify the language, the model - a scope of recognition to get the most appropriate results,
			// set the listener to handle the recognition events.
			recognizer = Recognizer.create(Recognizer.Language.RUSSIAN, Recognizer.Model.NOTES, RecognizerFragment.this, true);
			recognizer.setVADEnabled(false);
			// Don't forget to call start on the created object.
			recognizer.start();
		}
	}

	private void updateStatus(final String text) {
		currentStatus.setText(text);
	}

	private void updateProgress(int progress) {
		progressBar.setProgress(progress);
	}
}
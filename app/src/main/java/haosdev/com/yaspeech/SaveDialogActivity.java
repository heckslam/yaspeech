package haosdev.com.yaspeech;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import haosdev.com.yaspeech.model.Lection;
import haosdev.com.yaspeech.transitions.FabTransform;
import haosdev.com.yaspeech.transitions.MorphTransform;

import static com.google.android.gms.internal.zzs.TAG;

public class SaveDialogActivity extends Activity {
	boolean isDismissing = false;
	ViewGroup container;
	TextView title;
	TextInputLayout usernameLabel;
	TextInputLayout lectorNameLabel;
	AutoCompleteTextView username;
	EditText lectorName;
	FrameLayout actionsContainer;
	Button dismissBtn;
	Button saveBtn;
	String lectionText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_save_lection);
		container = $(R.id.container);
		title = $(R.id.dialog_title);
		usernameLabel = $(R.id.username_float_label);
		username = $(R.id.username);
		lectorNameLabel = $(R.id.lectorName_float_label);
		lectorName = $(R.id.lectorName);
		actionsContainer = $(R.id.actions_container);
		dismissBtn = $(R.id.dismiss);
		saveBtn = $(R.id.saveBtn);

		if (!FabTransform.setup(this, container)) {
			MorphTransform.setup(this, container,
					ContextCompat.getColor(this, R.color.background_light),
					getResources().getDimensionPixelSize(R.dimen.dialog_corners));
		}
		if (getWindow().getSharedElementEnterTransition() != null) {
			getWindow().getSharedElementEnterTransition().addListener(new android.transition.Transition.TransitionListener() {
				@Override
				public void onTransitionStart(android.transition.Transition transition) {
				}
				@Override
				public void onTransitionEnd(android.transition.Transition transition) {
					getWindow().getSharedElementEnterTransition().removeListener(this);
				}
				@Override
				public void onTransitionCancel(android.transition.Transition transition) {
				}
				@Override
				public void onTransitionPause(android.transition.Transition transition) {
				}
				@Override
				public void onTransitionResume(android.transition.Transition transition) {
				}
			});
		}

		lectionText = getIntent().getStringExtra(Const.LECTION_TEXT);

		username.addTextChangedListener(loginFieldWatcher);
	}

	@Override
	public void onBackPressed() {
		dismiss(null);
	}

	public void doSave(View view) {
		showLoading();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		String currentDateandTime = sdf.format(new Date());
		Lection lection = new Lection(lectionText, username.getText().toString(), currentDateandTime, lectorName.getText().toString());
		Log.d(TAG, "doSave: " + lection.toString());
		FirebaseDatabase.getInstance().getReference().child("lections").push().setValue(lection);
		Toast.makeText(this, "Лекция сохранена", Toast.LENGTH_SHORT).show();
		setResult(Activity.RESULT_OK);
		finish();
	}

	public void dismiss(View view) {
		isDismissing = true;
		setResult(Activity.RESULT_CANCELED);
		finishAfterTransition();
	}

	protected <T extends View> T $(@IdRes int id) {
		return (T) findViewById(id);
	}

	private TextWatcher loginFieldWatcher = new TextWatcher() {
		@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

		@Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

		@Override
		public void afterTextChanged(Editable s) {
			saveBtn.setEnabled(username.getText().toString().length() > 0);
		}
	};

	private void showLoading() {
		TransitionManager.beginDelayedTransition(container);
		title.setVisibility(View.GONE);
		usernameLabel.setVisibility(View.GONE);
		lectorNameLabel.setVisibility(View.GONE);
		actionsContainer.setVisibility(View.GONE);
	}

	private void showLogin() {
		TransitionManager.beginDelayedTransition(container);
		title.setVisibility(View.VISIBLE);
		usernameLabel.setVisibility(View.VISIBLE);
		lectorNameLabel.setVisibility(View.VISIBLE);
		actionsContainer.setVisibility(View.VISIBLE);
	}


	private void showLoginFailed() {
		Snackbar.make(container, "Ошибка", Snackbar.LENGTH_SHORT).show();
		showLogin();
		username.requestFocus();
	}
}
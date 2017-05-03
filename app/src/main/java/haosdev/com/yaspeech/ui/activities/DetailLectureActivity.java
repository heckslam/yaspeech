package haosdev.com.yaspeech.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import haosdev.com.yaspeech.Const;
import haosdev.com.yaspeech.R;
import haosdev.com.yaspeech.base.BaseActivity;
import haosdev.com.yaspeech.model.Lection;

public class DetailLectureActivity extends BaseActivity {

	private Lection lection;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_lection);
		lection = getIntent().getParcelableExtra(Const.LECTION_OBJECT);
		TextView textView = $(R.id.textView);
		textView.setText(lection.getText());
		getSupportActionBar().setTitle(lection.getName());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.detail_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_share:
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, lection.getText());
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, "Поделиться"));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}

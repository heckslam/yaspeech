package haosdev.com.yaspeech;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import haosdev.com.yaspeech.base.BaseActivity;
import haosdev.com.yaspeech.decorator.HorizontalDividerItemDecoration;
import haosdev.com.yaspeech.model.Lection;
import haosdev.com.yaspeech.ui.activities.DetailLectureActivity;

public class LecturesActivity extends BaseActivity {

	private FirebaseRecyclerAdapter<Lection, LectureViewHolder>
			mFirebaseAdapter;
	private RecyclerView mLecturesRecyclerView;
	private LinearLayoutManager mLinearLayoutManager;
	private ProgressBar mProgressBar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lections);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mLecturesRecyclerView = (RecyclerView) findViewById(R.id.lectionsRecyclerView);
		mLinearLayoutManager = new LinearLayoutManager(this);
		mLinearLayoutManager.setStackFromEnd(true);
		mLecturesRecyclerView.setLayoutManager(mLinearLayoutManager);

		getSupportActionBar().setTitle(R.string.lectures_title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mLecturesRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
				.color(Color.parseColor("#EFEFEF")).sizeResId(R.dimen.custom_recycler_divider)
				.marginResId(R.dimen.leftmargin, R.dimen.rightmargin).build());

		DatabaseReference lectureDatabaseReference = FirebaseDatabase.getInstance().getReference();
		lectureDatabaseReference.keepSynced(true);
		mFirebaseAdapter = new FirebaseRecyclerAdapter<Lection,
				LectureViewHolder>(
				Lection.class,
				R.layout.item_lection,
				LectureViewHolder.class,
				lectureDatabaseReference.child("lections")) {

			@Override
			protected void populateViewHolder(final LectureViewHolder viewHolder, Lection lection, final int position) {
				mProgressBar.setVisibility(ProgressBar.INVISIBLE);
				viewHolder.valueTextView.setText(lection.getText());
				viewHolder.nameTextView.setText(lection.getName());
				viewHolder.wrapper.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(LecturesActivity.this, DetailLectureActivity.class);
						intent.putExtra(Const.LECTION_OBJECT, mFirebaseAdapter.getItem(position));
						startActivity(intent);
					}
				});
				viewHolder.removeBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						DatabaseReference itemRef = mFirebaseAdapter.getRef(position);
						itemRef.removeValue();
					}
				});
			}
		};

		mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				int lecturesCount = mFirebaseAdapter.getItemCount();
				int lastVisiblePosition =
						mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
				if (lastVisiblePosition == -1 ||
						(positionStart >= (lecturesCount - 1) &&
								lastVisiblePosition == (positionStart - 1))) {
					mLecturesRecyclerView.scrollToPosition(positionStart);
				}
			}
		});

		mLecturesRecyclerView.setLayoutManager(mLinearLayoutManager);
		mLecturesRecyclerView.setAdapter(mFirebaseAdapter);
	}

	private static class LectureViewHolder extends RecyclerView.ViewHolder {
		TextView valueTextView;
		TextView nameTextView;
		RelativeLayout wrapper;
		ImageButton removeBtn;

		public LectureViewHolder(View v) {
			super(v);
			wrapper = (RelativeLayout) itemView.findViewById(R.id.recycler_item);
			removeBtn = (ImageButton) itemView.findViewById(R.id.removeBtn);
			valueTextView = (TextView) itemView.findViewById(R.id.valueTextView);
			nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
		}
	}
}

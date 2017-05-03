package haosdev.com.yaspeech.base;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {
	protected <T extends View> T $(View view, @IdRes int id) {
		return (T) view.findViewById(id);
	}
}

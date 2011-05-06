package cn.perfectgames.jewels.activities;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import cn.perfectgames.jewels.sound.SoundManager;
import cn.perfectgames.jewels.ui.LeaderBoardView;
import cn.perfectgames.jewels.ui.LeaderBoardView.ButtonListener;
import cn.perfectgames.jewels.util.AdDaemon;

import com.scoreloop.client.android.core.controller.RequestController;
import com.scoreloop.client.android.core.controller.RequestControllerObserver;
import com.scoreloop.client.android.core.controller.ScoresController;
import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.Session;

public class LeaderBoardActivity extends Activity implements OnClickListener,
		ButtonListener {

	private static final String TAG = "LeaderBoardActivity";

	static final int DIALOG_PROGRESS = 12;

	public static final int  ITEMS_PER_PAGE = 10;
	
	private Handler adHandler = new Handler();
	private AdDaemon adDaemon;

	enum CurrentOperationType {
		me, none, other;
	}

	private static final int FIXED_OFFSET = 3;

	private CurrentOperationType currentOperation = CurrentOperationType.none;
	private ScoresController scoresController;

	private LeaderBoardView leaderBoard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		leaderBoard = new LeaderBoardView(this);
		leaderBoard.addButtonListener(this);
		setContentView(leaderBoard);
		//
		this.scoresController = new ScoresController(new ScoresControllerObserver());
		this.scoresController.setRangeLength(ITEMS_PER_PAGE);
		//
		loadListFromStart();
	}

	private boolean alreadyLoading() {
		return currentOperation != CurrentOperationType.none;
	}

	private void loadListFromStart() {
		if (alreadyLoading()) {
			return;
		}
		showDialog(DIALOG_PROGRESS);
		currentOperation = CurrentOperationType.other;
		scoresController.loadRangeAtRank(1);
	}

	private void loadNextRange() {
		if (alreadyLoading()) {
			return;
		}
		showDialog(DIALOG_PROGRESS);
		currentOperation = CurrentOperationType.other;
		scoresController.loadNextRange();
	}

	private void loadPreviousRange() {
		if (alreadyLoading()) {
			return;
		}
		showDialog(DIALOG_PROGRESS);
		currentOperation = CurrentOperationType.other;
		scoresController.loadPreviousRange();
	}
	private void loadTopRange() {
		if (alreadyLoading()) {
			return;
		}
		showDialog(DIALOG_PROGRESS);
		currentOperation = CurrentOperationType.other;
		scoresController.loadRangeAtRank(1);
	}
	private void loadRangeForUser() {
		if (alreadyLoading()) {
			return;
		}
		showDialog(DIALOG_PROGRESS);
		currentOperation = CurrentOperationType.me;
		scoresController.loadRangeForUser(Session.getCurrentSession().getUser());
	}


	@Override
	protected void onDestroy() {
		// adDaemon.stop();
		super.onDestroy();
		SoundManager.release();
	}

	@Override
	protected void onPause() {
		// adDaemon.stop();
		super.onPause();
	}

	protected void onResume() {
		// adDaemon.run();
		super.onResume();
	}

	public void onClick(View view) {

	}

	public void buttonClickced(int id) {
		Log.v(TAG, "Button Clicked : " + id);
		switch (id){
		case LeaderBoardView.BTN_ME:
			loadRangeForUser();
			break;
		case LeaderBoardView.BTN_NEXT:
			loadNextRange();
			break;
		case LeaderBoardView.BTN_PREV:
			loadPreviousRange();
			break;
		case LeaderBoardView.BTN_TOP:
			loadTopRange();
		}
	}

	public void buttonPressed(int id) {
		Log.v(TAG, "Button Pressed : " + id);

	}

	public void buttonReleased(int id) {
		
		Log.v(TAG, "Button Realeased : " + id);

	}
	
	
	

	private class ScoresControllerObserver implements RequestControllerObserver {

		
		public void requestControllerDidFail(final RequestController requestController, final Exception exception) {
			dismissDialog(DIALOG_PROGRESS);
			currentOperation = CurrentOperationType.none;
			exception.printStackTrace();
		}

		
		public void requestControllerDidReceiveResponse(final RequestController requestController) {
			final List<Score> scores = scoresController.getScores();
			Log.v(TAG, "successfully get scores, size = "+scores.size());
			leaderBoard.setScores(scores);

			leaderBoard.setButtonEnabled(LeaderBoardView.BTN_PREV, scoresController.hasPreviousRange());
			leaderBoard.setButtonEnabled(LeaderBoardView.BTN_NEXT, scoresController.hasNextRange());
			
			if (currentOperation == CurrentOperationType.me) {
				boolean loginFound = false;
				int idx = 0;
				for (final Score score : scores) {
					if (score.getUser().equals(Session.getCurrentSession().getUser())) {
						loginFound = true;
						break;
					}
					++idx;
				}

				if (!loginFound) {
					//TODO show error message
					
				}

			}
			
			currentOperation = CurrentOperationType.none;
			dismissDialog(DIALOG_PROGRESS);
		}
		
	}
	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case DIALOG_PROGRESS:
			return createProgressDialog();
		default:
			return null;
		}
	}

	
	private Dialog createProgressDialog() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage("Retreiving Data...");
		return dialog;
	}

}

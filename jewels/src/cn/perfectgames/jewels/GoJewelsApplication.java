/*
 * In derogation of the Scoreloop SDK - License Agreement concluded between
 * Licensor and Licensee, as defined therein, the following conditions shall
 * apply for the source code contained below, whereas apart from that the
 * Scoreloop SDK - License Agreement shall remain unaffected.
 * 
 * Copyright: Scoreloop AG, Germany (Licensor)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package cn.perfectgames.jewels;

import java.util.List;

import android.app.Application;
import android.util.Log;
import cn.perfectgames.jewels.scoreloop.ScoreloopManager;

import com.scoreloop.client.android.core.model.Score;
import com.scoreloop.client.android.core.model.User;

public class GoJewelsApplication extends Application {

	private static Score score;
	private static List<User> users;
	
	
	static Score getScore() { // pass data from GamePlayActivity to GameResultActivity
		return score;
	}

	static List<User> getUsers() {
		return users;
	}


	static User popUser() {
		if (users == null) {
			return null;
		}
		if (users.isEmpty()) {
			return null;
		}
		return users.remove(0);
	}

	
	static void setScore(final Score score) { // pass data from GamePlayActivity to GameResultActivity
		GoJewelsApplication.score = score;
	}

	static void setUsers(final List<User> users) {
		GoJewelsApplication.users = users;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v("GoJewelsApplication", "Start Up Initialization");
		ScoreloopManager.init(this.getApplicationContext());
		
	}
}

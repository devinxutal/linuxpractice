package cn.perfectgames.jewels.util;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.perfectgames.jewels.R;

public class DialogUtil {
	public static void showDialog(Context context, int title, int content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(content).setCancelable(false)
				.setPositiveButton(R.string.common_ok, null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void showDialog(Context context, String title, String content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(content).setCancelable(false)
				.setPositiveButton(R.string.common_ok, null);
		AlertDialog alert = builder.create();
		alert.show();

	}

	public static TextView createTableCell(Context context, String content) {
		TextView view = new TextView(context);
		view.setText(content);
		view.setPadding(5, 2, 5, 2);
		return view;
	}

	public static void showDialogWithView(Context context, String title,
			int view_id) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout l = new LinearLayout(context);
		inflater.inflate(R.layout.help, l);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setCancelable(false)
				.setPositiveButton(R.string.common_ok, null).setView(l);
		AlertDialog alert = builder.create();

		// alert.setContentView(view_id);
		alert.show();

	}

	public static void showHelpDialog(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		LinearLayout l = new LinearLayout(context);
//		inflater.inflate(R.layout.help, l);
//		WebView web = (WebView)l.findViewById(R.id.web_view);
//		web.setMinimumHeight(300);
//		l.setMinimumHeight(300);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		WebView web = new WebView(context);
		builder.setTitle("Go Jewels Help").setCancelable(false)
				.setPositiveButton(R.string.common_ok, null).setView(web);
		AlertDialog alert = builder.create();
		web.loadUrl("file:///android_asset/htmls/help/help.html");
		alert.show();
	}

}

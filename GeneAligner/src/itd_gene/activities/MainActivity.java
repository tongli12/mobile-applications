package itd_gene.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import itd_gene.activities.R;
import itd_gene.aligner.ValidateInput;

public class MainActivity extends Activity {

	public final static String SEQ1 = "FirstSeq";
	public final static String SEQ2 = "SecondSeq";
	public final static String GAP_P = "10";
	public final static String GAP_E = "1";
	public final static String TypeOfAlignment = "Global";
	public final static String TypeOfMatrix = "NUC4.2";

	/*
	 * This is the main Activity, and the onCreate method is the first method
	 * called when created.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_options_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			startActivity(new Intent(this, HelpActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/** Called when the user selects the Send button */
	public void sendMessage(View view) {

		EditText editText1 = (EditText) findViewById(R.id.sequence1);
		EditText editText2 = (EditText) findViewById(R.id.sequence2);
		String sequenceOne = editText1.getText().toString();
		String sequenceTwo = editText2.getText().toString();

		Spinner spinner1 = (Spinner) findViewById(R.id.matrix_type);
		String matrixType = String.valueOf(spinner1.getSelectedItem());

		EditText editText3 = (EditText) findViewById(R.id.gap_penalty);
		String gapPenalty = null;
		if (editText3.getText().toString().equals("")) {
			gapPenalty = "10";
		} else {
			gapPenalty = editText3.getText().toString();
		}

		EditText editText4 = (EditText) findViewById(R.id.gap_extension);
		String gapExtension = null;
		if (editText4.getText().toString().equals("")) {
			gapExtension = "1";
		} else {
			gapExtension = editText4.getText().toString();
		}

		Spinner spinner2 = (Spinner) findViewById(R.id.alignment_type);
		String alignmentType = String.valueOf(spinner2.getSelectedItem());

		// The string check either has "ERROR ..... " message or both the
		// sequences separated by comma
		String check = "ERROR";
		String errorsGopGex = "ERROR";

		try {
			check = ValidateInput.pairwiseAlignment(sequenceOne, sequenceTwo);
			errorsGopGex = ValidateInput.penaltyParameters(gapPenalty, gapExtension);
		} catch (Exception e) {
			// the try/catch was added to avoid that the program crashes when
			// there is no internet connection
		}

		if (check.startsWith("ERROR") || errorsGopGex.startsWith("ERROR")) {
			// if there are errors, the message is displayed using PopUp with a
			// message

			String errorMessage = null;
			if (check.startsWith("ERROR")) {
				errorMessage = check;
				// ivetth: 8/20/2012 added to check internet connection
				if (!isNetworkAvailable() || errorMessage.equalsIgnoreCase("ERROR")) {
					errorMessage += ". Check your internet connection.";
				}
			} else {
				errorMessage = errorsGopGex;
			}
			popIt(errorMessage);

			// restart the activity after displaying the popup message
			// ivetth(8/15/2012): removed by me!!
			// final Intent intent2 = new Intent(this, MainActivity.class);
			// Handler handler = new Handler();
			// handler.postDelayed(new Runnable() {
			// public void run() {
			// startActivity(intent2);
			// }
			// }, 3000); // New activity starts after 3000 miliseconds (3
			// seconds)
		} else {
			// if there are no errors, the sequences are separated and used for
			// alignment
			String check1 = check.split(",")[0];
			String check2 = check.split(",")[1];

			Intent intent = new Intent(this, DisplayMessageActivity.class);

			intent.putExtra(SEQ1, check1);
			intent.putExtra(SEQ2, check2);
			intent.putExtra(TypeOfMatrix, matrixType);
			intent.putExtra(GAP_P, gapPenalty);
			intent.putExtra(GAP_E, gapExtension);
			intent.putExtra(TypeOfAlignment, alignmentType);

			startActivity(intent);

			// popIt(check);
			// Intent intent2 = new Intent(this, MyFirstActivity.class);
			// startActivity(intent2);
		}

	}

	/*
	 * This method is called whenever there was an error in the input
	 * parameters. It shows the error message.
	 */
	public void popIt(String title) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (title.endsWith("."))
			builder.setMessage(title);
		else
			builder.setMessage(title + ". Try Again.");
		// builder.setNeutralButton("Try Again!", null);
		// builder.show();

		final AlertDialog alert = builder.create();
		alert.show();

	}

	// ivetth (8/20/2012): added to check internet connection
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
}

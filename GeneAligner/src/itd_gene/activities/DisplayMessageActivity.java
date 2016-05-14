package itd_gene.activities;

import itd_gene.aligner.Aligner;

import org.biojava3.alignment.template.SequencePair;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;

import itd_gene.activities.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class DisplayMessageActivity extends Activity {

	private static String queryTargetLength;
	public static String passQueryTargetLength;
	private static int nextLineCut = 30; // int nextLineCut = width;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		TextView resultView = (TextView) findViewById(R.id.resultAlign);

		// Get the message from the intent
		Intent intent = getIntent();

		String s1 = intent.getStringExtra(MainActivity.SEQ1);
		String s2 = intent.getStringExtra(MainActivity.SEQ2);
		String s3 = intent.getStringExtra(MainActivity.TypeOfMatrix);
		String s4 = intent.getStringExtra(MainActivity.GAP_P);
		String s5 = intent.getStringExtra(MainActivity.GAP_E);
		String s6 = intent.getStringExtra(MainActivity.TypeOfAlignment);

		SequencePair<DNASequence, NucleotideCompound> pair = Aligner.alignPair(s1, s2, s3, s4, s5, s6);

		String query = (String) Aligner.getQuery(pair);
		String target = (String) Aligner.getTarget(pair);
		String length = (String) Aligner.getLength(pair);

		queryTargetLength = query + "," + target + "," + length + "," + nextLineCut;

		String finalOrderedAlignment = orderedAlignment(query, target, length);

		/*
		 * This next part colors the sequences according to the physio-chemical
		 * properties of the amino acids. The colors are defined in the method
		 * orderedAlignment.
		 */
		SpannableString coloredText = new SpannableString(finalOrderedAlignment);
		for (int i = 0; i < finalOrderedAlignment.length(); i++) {
			coloredText.setSpan(new ForegroundColorSpan(Color.WHITE), i, i + 1, 0);
			char currentCharacter = finalOrderedAlignment.charAt(i);

			// ivetth (8/15/2012): see if we will assign colors to letters
			/*
			 * if (currentCharacter == 'H' || currentCharacter == 'R' ||
			 * currentCharacter == 'K') { coloredText.setSpan(new
			 * ForegroundColorSpan(Color.CYAN), i, i + 1, 0); } else if
			 * (currentCharacter == 'D' || currentCharacter == 'E') {
			 * coloredText.setSpan(new ForegroundColorSpan(Color.MAGENTA), i, i
			 * + 1, 0); } else if (currentCharacter == 'S' || currentCharacter
			 * == 'T' || currentCharacter == 'N' || currentCharacter == 'Q') {
			 * coloredText.setSpan(new ForegroundColorSpan(Color.YELLOW), i, i +
			 * 1, 0); } else if (currentCharacter == 'A' || currentCharacter ==
			 * 'V' || currentCharacter == 'I' || currentCharacter == 'L' ||
			 * currentCharacter == 'M' || currentCharacter == 'F' ||
			 * currentCharacter == 'Y' || currentCharacter == 'W') {
			 * coloredText.setSpan(new ForegroundColorSpan(Color.GREEN), i, i +
			 * 1, 0); }
			 */

		}

		resultView.setText(coloredText, BufferType.SPANNABLE);
	}

	/*
	 * This method receives the aligned (with gaps) query and target sequences
	 * and also the length of the alignment.
	 * 
	 * It breaks the sequences according to the nextLineCut parameter. It also
	 * calculates sequenceIdentity and sequenceMatch of the alignment. It adds
	 * the color legend.
	 * 
	 * It produces the text as it appears in the result alignment screen.
	 */
	public String orderedAlignment(String query, String target, String length) {
		int alignedLength = Integer.parseInt(length);
		StringBuffer finalAlignment = new StringBuffer();
		int i = 0;
		int queryTraverse = 0;
		int targetTraverse = 0;
		int sequenceIdentity = 0;
		int sequenceMatch = 0;

		for (i = 0; i < alignedLength - nextLineCut; i = i + nextLineCut) {

			finalAlignment.append(query.substring(i, i + nextLineCut));
			for (int j = i; j < i + nextLineCut; j++) {
				if (query.charAt(j) != '-') {
					queryTraverse++;
				}

				if (query.charAt(j) != '-' && target.charAt(j) != '-') {
					sequenceMatch++;
				}
			}
			if (queryTraverse < 100) {
				finalAlignment.append("   " + Integer.toString(queryTraverse) + "\n");
			}
			if (queryTraverse >= 100) {
				finalAlignment.append("  " + Integer.toString(queryTraverse) + "\n");
			}

			finalAlignment.append(target.substring(i, i + nextLineCut));
			for (int j = i; j < i + nextLineCut; j++) {
				if (target.charAt(j) != '-') {
					targetTraverse++;
				}
			}
			if (targetTraverse < 100) {
				finalAlignment.append("   " + Integer.toString(targetTraverse) + "\n");
			}
			if (targetTraverse >= 100) {
				finalAlignment.append("  " + Integer.toString(targetTraverse) + "\n");
			}

			for (int j = i; j < i + nextLineCut; j++) {
				if (query.charAt(j) == target.charAt(j)) {
					finalAlignment.append("*");
					sequenceIdentity++;
				}
				if (query.charAt(j) != target.charAt(j)) {
					finalAlignment.append(" ");
				}
			}
			finalAlignment.append("\n\n");

		}

		finalAlignment.append(query.substring(i, alignedLength));
		for (int j = i; j < alignedLength; j++) {
			if (query.charAt(j) != '-') {
				queryTraverse++;
			}

			if (query.charAt(j) != '-' && target.charAt(j) != '-') {
				sequenceMatch++;
			}
		}
		if (queryTraverse < 100) {
			finalAlignment.append("   " + Integer.toString(queryTraverse) + "\n");
		}
		if (queryTraverse >= 100) {
			finalAlignment.append("  " + Integer.toString(queryTraverse) + "\n");
		}

		finalAlignment.append(target.substring(i, alignedLength));
		for (int j = i; j < alignedLength; j++) {
			if (target.charAt(j) != '-') {
				targetTraverse++;
			}
		}
		if (targetTraverse < 100) {
			finalAlignment.append("   " + Integer.toString(targetTraverse) + "\n");
		}
		if (targetTraverse >= 100) {
			finalAlignment.append("  " + Integer.toString(targetTraverse) + "\n");
		}

		for (int j = i; j < alignedLength; j++) {
			if (query.charAt(j) == target.charAt(j)) {
				finalAlignment.append("*");
				sequenceIdentity++;
			}
			if (query.charAt(j) != target.charAt(j)) {
				finalAlignment.append(" ");
			}
		}

		finalAlignment.append("\n\n");

		sequenceIdentity = (sequenceIdentity * 100) / alignedLength;
		sequenceMatch = (sequenceMatch * 100) / alignedLength;

		finalAlignment.append("sequence identity: " + Integer.toString(sequenceIdentity) + "%\n");
		finalAlignment.append("sequence match: " + Integer.toString(sequenceMatch) + "%\n\n\n");

		// ivetth(8/15/2012): colors?
		// finalAlignment.append("HRK = positively charged\n");
		// finalAlignment.append("DE  = negatively charged\n");
		// finalAlignment.append("STNQ  =  polar uncharged\n");
		// finalAlignment.append("AVILMFYW  =  hydrophobic\n");

		return finalAlignment.toString();
	}

	/*
	 * This method is executed when you press the button
	 * "Dot plot for this alignment" in the result alignment screen. It passes
	 * the DisplayMessageActivity intent to the DotPlotActivity. The parameters
	 * that are passed are query, target and length, as a single String
	 * separated by commas.
	 */
	public void startDotPlotActivity(View view) {
		Intent alignedIntent = new Intent(this, DotPlotActivity.class);
		alignedIntent.putExtra(passQueryTargetLength, queryTargetLength);
		startActivity(alignedIntent);
	}
}

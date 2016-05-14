package itd_gene.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import android.content.Intent;
import android.graphics.Paint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import itd_gene.activities.R;

public class DotPlotActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dotplot);
		Intent intent = getIntent();
		String queryTargetLength = intent
				.getStringExtra(DisplayMessageActivity.passQueryTargetLength);

		String[] cols = queryTargetLength.split(",");

		String query = cols[0];
		String target = cols[1];
		String length = cols[2];
		int nextLineCut = Integer.parseInt(cols[3]);
		saveImage(query, target, length, nextLineCut);

		// ImageView jpgView = (ImageView)findViewById(R.id.resultDotPlot);

		/*
		 * This part reads the image file saved in the saveImage method. It
		 * produces an object to allow zooming the image, and finally it sets
		 * the image as a content in this Activity.
		 */
		File pictureDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		// String myJpgPath = pictureDir.toString() +
		// "/big-white-image-with-view.png"; //UPDATE WITH YOUR OWN JPG FILE

		String myImagePath = pictureDir.getAbsolutePath().toString()
				+ "/pairwise_gene_aligner.png";
		TouchImageView img = new TouchImageView(this);
		Bitmap dotPlotImage = BitmapFactory.decodeFile(myImagePath);
		img.setImageBitmap(dotPlotImage);
		img.setMaxZoom(4f);
		setContentView(img);

		/*
		 * CODE FOR DISPLAYING IMAGES IN WEBVIEW <WebView
		 * android:id="@+id/resultDotPlot" android:layout_width="match_parent"
		 * android:layout_height="match_parent" />
		 * 
		 * WebView wv = (WebView)findViewById(R.id.resultDotPlot);
		 * wv.getSettings().setBuiltInZoomControls(true);
		 * wv.getSettings().setAllowFileAccess(true);
		 * wv.getSettings().setJavaScriptEnabled(true); wv.clearHistory();
		 * wv.clearFormData(); wv.clearCache(true); String myJpgPath = "file:/"
		 * + pictureDir.getAbsolutePath().toString() +
		 * "/pairwise_protein_aligner.png"; String html =
		 * "<html><head></head><body><img src=\""+ myJpgPath +
		 * "\"></body></html>"; wv.loadDataWithBaseURL(myJpgPath, html,
		 * "text/html","utf-8", "");
		 */

		/*
		 * CODE FOR DISPLAYING IMAGES IN IMAGEVIEW <ImageView
		 * android:id="@+id/pngview1" android:layout_width="fill_parent"
		 * android:layout_height="fill_parent" />
		 * 
		 * ImageView jpgView = (ImageView)findViewById(R.id.pngview1); String
		 * myJpgPath = pictureDir.getAbsolutePath().toString() +
		 * "/pairwise_protein_aligner_HQ.png"; BitmapFactory.Options options =
		 * new BitmapFactory.Options(); options.inSampleSize = 2; Bitmap bm =
		 * BitmapFactory.decodeFile(myJpgPath, options);
		 * jpgView.setImageBitmap(bm);
		 */

	}

	/*
	 * This method receives the query and target sequences. With this it creates
	 * a canvas and plots the pixels (for exact matches only) on the canvas. The
	 * ticks numbering follow the sequence indexing from DisplayMessageActivity
	 * using the nextLineCut variable passed through the intent. Finally, the
	 * canvas is saved as a png file in the external SD card, in the folder
	 * Environment.DIRECTORY_PICTURES. The filename is
	 * pairwise_protein_aligner.png.
	 */
	public void saveImage(String query, String target, String length,
			int nextLineCut) {

		int canvasSize = Integer.parseInt(length) * 4;

		canvasSize = canvasSize + 70;

		int offsetDatapoints = 52;

		Bitmap largeWhiteBitmap = Bitmap.createBitmap(canvasSize, canvasSize,
				Bitmap.Config.ARGB_8888);

		// Make a canvas with which we can draw to the bitmap
		Canvas canvas = new Canvas(largeWhiteBitmap);
		canvas.drawColor(Color.WHITE);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		// Make plot boundary
		paint.setColor(Color.BLACK);
		canvas.drawRect((offsetDatapoints - 2), (offsetDatapoints - 2),
				(Integer.parseInt(length) * 4 + offsetDatapoints + 2),
				(Integer.parseInt(length) * 4 + offsetDatapoints + 2), paint);

		// Plot text
		if (Integer.parseInt(length) <= nextLineCut) {
			float textSize = (float) 16;
			paint.setColor(Color.BLACK);
			paint.setTypeface(Typeface.MONOSPACE);
			paint.setTextSize(textSize);
			paint.setTextScaleX(1);

			int queryCounter = 0;
			int targetCounter = 0;
			for (int i = 0; i < (Integer.parseInt(length)); i++) {

				if (query.subSequence(i, i + 1).equals("-")) {
				} else {
					queryCounter++;
				}

				if (target.subSequence(i, i + 1).equals("-")) {
				} else {
					targetCounter++;
				}

			}

			canvas.drawText(Integer.toString(queryCounter), offsetDatapoints
					+ (textSize / 4 * (Integer.parseInt(length) - 1)) - 2,
					offsetDatapoints + 2 - 16, paint);
			canvas.drawText(
					"|",
					offsetDatapoints
							+ (textSize / 4 * (Integer.parseInt(length) - 1))
							- 2, offsetDatapoints + 2, paint);

			canvas.drawText(Integer.toString(targetCounter),
					offsetDatapoints - 2 - 8 - 32, offsetDatapoints
							+ (textSize / 4 * (Integer.parseInt(length) - 1))
							+ 8, paint);
			canvas.drawText("_", offsetDatapoints - 2 - 8, offsetDatapoints
					+ (textSize / 4 * (Integer.parseInt(length) - 1)), paint);

		}

		if (Integer.parseInt(length) > nextLineCut) {
			float textSize = (float) 16;
			paint.setColor(Color.BLACK);
			paint.setTypeface(Typeface.MONOSPACE);
			paint.setTextSize(textSize);
			paint.setTextScaleX(1);

			int queryCounter = 0;
			int targetCounter = 0;
			for (int i = 0; i < (Integer.parseInt(length)); i++) {

				if (query.subSequence(i, i + 1).equals("-")) {
				} else {
					queryCounter++;
				}

				if (target.subSequence(i, i + 1).equals("-")) {
				} else {
					targetCounter++;
				}

				if ((i + 1) % nextLineCut == 0) {
					canvas.drawText(Integer.toString(queryCounter),
							offsetDatapoints + (textSize / 4 * i) - 2,
							offsetDatapoints + 2 - 16, paint);
					canvas.drawText("|", offsetDatapoints + (textSize / 4 * i)
							- 2, offsetDatapoints + 2, paint);

					canvas.drawText(Integer.toString(targetCounter),
							offsetDatapoints - 2 - 8 - 32, offsetDatapoints
									+ (textSize / 4 * i) + 8, paint);
					canvas.drawText("_", offsetDatapoints - 2 - 8,
							offsetDatapoints + (textSize / 4 * i), paint);
				}
			}
		}

		// Plot all the points
		for (int i = 0; i < (Integer.parseInt(length)); i++) {
			for (int j = 0; j < (Integer.parseInt(length)); j++) {

				paint.setColor(getColorShade(query.substring(i, i + 1),
						target.substring(j, j + 1), i, j));
				// paint.setColor(Color.WHITE);
				canvas.drawRect((i * 4 + offsetDatapoints),
						(j * 4 + offsetDatapoints),
						(i * 4 + 4 + offsetDatapoints),
						(j * 4 + 4 + offsetDatapoints), paint);

			}
		}

		canvas.save();
		canvas.restore();

		// Write the file (don't forget
		// android.permission.WRITE_EXTERNAL_STORAGE)
		File pictureDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File pngFileHigh = new File(pictureDir, "pairwise_gene_aligner.png");
		try {
			largeWhiteBitmap.compress(Bitmap.CompressFormat.PNG, 0,
					new FileOutputStream(pngFileHigh));
		} catch (FileNotFoundException e) {
			Log.e(" ", "Could not write " + pngFileHigh, e);
		}

		// Immediately release the bitmap memory to avoid OutOfMemory exception
		largeWhiteBitmap.recycle();

		/*
		 * THIS PIECE OF CODE CAN BE USED TO REDUCE IMAGE SIZE AND QUALITY int
		 * rescale_factor = Integer.parseInt(length) / 50; if(rescale_factor <
		 * 1) {rescale_factor = 1;}
		 * 
		 * float new_size =
		 * ((float)(canvasSize/rescale_factor))/(float)(canvasSize); Matrix
		 * matrix = new Matrix(); matrix.postScale(new_size, new_size);
		 * 
		 * Bitmap resizedBitmap = Bitmap.createBitmap(largeWhiteBitmap, 0, 0,
		 * canvasSize, canvasSize, matrix, true);
		 * 
		 * File pngFile_low = new File(pictureDir,
		 * "pairwise_protein_aligner.png");
		 * 
		 * try { resizedBitmap.compress(Bitmap.CompressFormat.PNG, 0, new
		 * FileOutputStream(pngFile_low)); } catch (FileNotFoundException e) {
		 * Log.e(" ", "Could not write " + pngFile_low, e); }
		 * resizedBitmap.recycle();
		 */

	}

	/*
	 * It returns the integer value for the color. For exact match it returns
	 * black, else it returns white. aaQuery and aaTarget are just one
	 * character.
	 */
	public int getColorShade(String aaQuery, String aaTarget, int i, int j) {

		if (aaQuery.equals("-") || aaTarget.equals("-")) {
			return Color.WHITE;
		}

		if (aaQuery.equals(aaTarget)) {
			return Color.BLACK;
		}

		return Color.WHITE;

		/*
		 * if(aaQuery.equals(aaTarget)) { if(aaQuery.equals("H") ||
		 * aaQuery.equals("R") || aaQuery.equals("K")) { return Color.CYAN;}
		 * if(aaQuery.equals("D") || aaQuery.equals("E")) { return
		 * Color.MAGENTA;} if(aaQuery.equals("S") || aaQuery.equals("T") ||
		 * aaQuery.equals("N") || aaQuery.equals("Q")) { return Color.YELLOW;}
		 * if(aaQuery.equals("A") || aaQuery.equals("V") || aaQuery.equals("I")
		 * || aaQuery.equals("L") || aaQuery.equals("M") || aaQuery.equals("F")
		 * || aaQuery.equals("Y") || aaQuery.equals("W")) { return Color.GREEN;}
		 * }
		 */

		/*
		 * if(aaQuery.equals("-") || aaTarget.equals("-")) {return Color.BLACK;}
		 * 
		 * int query_group = 0; if(aaQuery.equals("H") || aaQuery.equals("R") ||
		 * aaQuery.equals("K")) { query_group = 1;} if(aaQuery.equals("D") ||
		 * aaQuery.equals("E")) { query_group = 2;} if(aaQuery.equals("S") ||
		 * aaQuery.equals("T") || aaQuery.equals("N") || aaQuery.equals("Q")) {
		 * query_group = 3;} if(aaQuery.equals("A") || aaQuery.equals("V") ||
		 * aaQuery.equals("I") || aaQuery.equals("L") || aaQuery.equals("M") ||
		 * aaQuery.equals("F") || aaQuery.equals("Y") || aaQuery.equals("W")) {
		 * query_group = 4;}
		 * 
		 * int target_group = 0; if(aaTarget.equals("H") || aaTarget.equals("R")
		 * || aaTarget.equals("K")) { target_group = 1;} if(aaTarget.equals("D")
		 * || aaTarget.equals("E")) { target_group = 2;} if(aaTarget.equals("S")
		 * || aaTarget.equals("T") || aaTarget.equals("N") ||
		 * aaTarget.equals("Q")) { target_group = 3;} if(aaTarget.equals("A") ||
		 * aaTarget.equals("V") || aaTarget.equals("I") || aaTarget.equals("L")
		 * || aaTarget.equals("M") || aaTarget.equals("F") ||
		 * aaTarget.equals("Y") || aaTarget.equals("W")) { target_group = 4;}
		 * 
		 * if(query_group==1 && target_group==1) {return Color.BLUE;}
		 * if(query_group==2 && target_group==2) {return Color.RED;}
		 * if(query_group==3 && target_group==3) {return Color.YELLOW;}
		 * if(query_group==4 && target_group==4) {return Color.GREEN;}
		 * 
		 * if(i==j) {return Color.WHITE;}
		 * 
		 * if(i!=j && query_group!=target_group) {return Color.BLACK;}
		 * 
		 * return Color.BLACK;
		 */

	}

}

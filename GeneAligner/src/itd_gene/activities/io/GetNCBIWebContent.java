package itd_gene.activities.io;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.AsyncTask;

// Get sequence from NCBI web site. Read the XML format of the web site
public class GetNCBIWebContent extends AsyncTask<String, Integer, String> {
	@Override
	protected String doInBackground(String... sUrl) {
		String body = "";
		try {
			Document doc = Jsoup.connect(sUrl[0]).get();
			Elements Tseq = doc.select("TSeq_sequence");
			body = Tseq.get(0).text();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return body;
	}
}
package itd_gene.aligner;

import itd_gene.activities.io.GetNCBIWebContent;

import java.util.concurrent.ExecutionException;

import org.biojava3.core.sequence.compound.DNACompoundSet;
import org.biojava3.core.sequence.compound.NucleotideCompound;

/*
 * This class validates the parameters for the aligment.
 */
public class ValidateInput {

	// private static String AMINO_ACID_LETTERS = "ACDEFGHIKLMNPQRSTVWYXB";
	private static String AMINO_ACID_LETTERS = null;
	public final static String PDBID = "downloadPdbId";

	/*
	 * This method receives the open gap and extension gap penalty values as
	 * Strings and validates the parameters according to the following rules.
	 * 0<=gop<=20; if(gop==0){ 0<=gex<=20; } else{ 0<=gex<=gop; }
	 */
	public static String penaltyParameters(String sgop, String sgex) {
		boolean isGopValid = true;
		boolean isGexValid = true;
		double gop = 0, gex;
		try {
			gop = Double.parseDouble(sgop);
			isGopValid = (0 <= gop) && (gop <= 20);
		} catch (Exception e) {
			isGopValid = false;
		}

		try {
			gex = Double.parseDouble(sgex);
			if (gex < 0 || gex > 20 || (gop > 0 && gex > gop))
				isGexValid = false;
		} catch (Exception e) {
			isGexValid = false;
		}
		if (!isGopValid && !isGexValid)
			return "ERROR: both Gap Penalties are NOT valid";
		if (!isGopValid)
			return "ERROR: the Gap Opening Penlaty is NOT valid";
		if (!isGexValid)
			return "ERROR: the Gap Extension Penalty is NOT valid";
		return "OK";
	}

	/*
	 * This method received two Strings and validate the Strings separately. If
	 * there is an error in any of the two Strings the resulting String should
	 * start with "ERROR". A valid input String is one of the follows: 1) a
	 * valid protein sequence (see method isAminoAcidSequence), 2) a valid pdbId
	 * (see method getSequenceFromPDB), and 3) a valid protein accession number
	 * (see method getSequenceFromNCBI)
	 */
	public static String pairwiseAlignment(String s1, String s2) {

		setAminoAcidLetters();

		String tmp1 = validateInput(s1);
		String tmp2 = validateInput(s2);
		String result = null;
		if (tmp1.startsWith("ERROR") && tmp2.startsWith("ERROR")) {
			result = tmp1.replaceAll("input", "input 1") + " " + tmp2.replaceAll("input", "input 2").replaceAll("ERROR:", "and");
		} else if (tmp1.startsWith("ERROR")) {
			result = tmp1.replaceAll("input", "input 1");
		} else if (tmp2.startsWith("ERROR")) {
			result = tmp2.replaceAll("input", "input 2");
		} else {
			result = tmp1.toUpperCase() + "," + tmp2.toUpperCase();
		}

		return result;
	}

	/*
	 * This method reads the characters allowed by biojava for a ProteinSequence
	 * object and fills the String AMINO_ACID_LETTERS that will be used in the
	 * method isAminoAcidSequence to validate is a String is an amino acid
	 * sequence.
	 */
	private static void setAminoAcidLetters() {
		org.biojava3.core.sequence.compound.DNACompoundSet aacs = DNACompoundSet.getDNACompoundSet();
		for (NucleotideCompound aac : aacs.getAllCompounds()) {
			if (AMINO_ACID_LETTERS == null)
				AMINO_ACID_LETTERS = "";
			AMINO_ACID_LETTERS += aac.getShortName();
		}
	}

	/*
	 * This method receives one input String and validates if is either a valid
	 * amino acid sequence, a valid pdb id, or a valid NCBI accession number. If
	 * it is not valid it will return a String starting with "ERROR".
	 */
	private static String validateInput(String input) {

		if (input == null || input.length() == 0) {
			return "ERROR: input is EMPTY";
		} else if (isAminoAcidSequence(input)) {
			return input;
		} else if (isAccessionNumberOfNCBI(input)) {
			String tmp = getSequenceFromNCBI(input);
			if (tmp.equals("BAD")) {
				return "ERROR: input is NOT VALID";
				// return "ERROR: input sequence or ncbiId is NOT VALID";
			} else if (tmp != null) {
				return tmp;
			}
		}

		return "ERROR: input is NOT VALID";
	}

	/*
	 * This method compares each character of the input String with the allowed
	 * characters by biojava stored in AMINO_ACID_LETTERS.
	 */
	private static boolean isAminoAcidSequence(String sequence) {
		if (sequence == null || sequence.length() == 0)
			return false;
		sequence = sequence.toUpperCase();
		int l = sequence.length();
		for (int i = 0; i < l; i++) {
			if (AMINO_ACID_LETTERS.indexOf(sequence.charAt(i)) < 0) {
				return false;
			}
		}
		return true;
	}

	/*
	 * This method returns true iff the input String has either 4 or 5
	 * characters. This method is not used in Gene Aligner
	 */
	/*
	 * private static boolean isPdbName(String pdbName) { if (pdbName.length()
	 * == 4 || pdbName.length() == 5) { return true; } return false; }
	 */

	/*
	 * This method returns true iff the input String has either 6 or 8
	 * characters.
	 */
	private static boolean isAccessionNumberOfNCBI(String accssionNumber) {
		if (accssionNumber.length() == 6 || accssionNumber.length() == 8) {
			return true;
		}
		return false;
	}

	/*
	 * This method receives a String, reads the fasta file from the NCBI sever
	 * using the GetNCBIContent class. If there is an error the resulting String
	 * = "BAD".
	 */
	private static String getSequenceFromNCBI(String input) {
		String accesionNum = input;
		// Copy the fasta file content from internet.

		StringBuffer url = new StringBuffer();
		url.append("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi");
		url.append("?db=sequences");
		url.append("&id=");
		url.append(accesionNum);
		url.append("&rettype=fasta");
		url.append("&retmode=xml");

		GetNCBIWebContent getNCBIWeb = new GetNCBIWebContent();
		getNCBIWeb.execute(url.toString());
		// getNCBIWeb.execute("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=sequences&id="
		// + accesionNum + "&rettype=fasta&retmode=xml");

		String result = "";
		try {
			result = getNCBIWeb.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "BAD";
		} catch (ExecutionException e) {
			e.printStackTrace();
			return "BAD";
		}

		if (result.equals("")) {
			return "BAD";
		} else {
			return result;
		}
	}

}

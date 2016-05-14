package itd_gene.aligner;

import org.biojava3.alignment.Alignments;
import org.biojava3.alignment.SimpleGapPenalty;
import org.biojava3.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava3.alignment.SubstitutionMatrixHelper;
import org.biojava3.alignment.template.AlignedSequence;
import org.biojava3.alignment.template.SequencePair;
import org.biojava3.alignment.template.SubstitutionMatrix;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;

public class Aligner {

	/*
	 * This method receives two protein sequences and calls the biojava method
	 * to do global or local pairwise alignment.
	 * 
	 * By default matrixType = "Blosum60", gapP = 10, gapE = 1, and
	 * alignmentType = "Global"
	 * 
	 * It returns an object SequencePair that has the result of the alignment as
	 * provided by the biojava method.
	 */

	public static SequencePair<DNASequence, NucleotideCompound> alignPair(String seq1, String seq2, String matrixType, String gapP, String gapE, String alignmentType) {

		DNASequence s1 = new DNASequence(seq1);
		DNASequence s2 = new DNASequence(seq2);

		SimpleGapPenalty gapPenaltyExtension = new SimpleGapPenalty(Short.valueOf(gapP), Short.valueOf(gapE));

		SubstitutionMatrix<NucleotideCompound> matrix = null;

		if (matrixType.equals("NUC4.2")) {
			matrix = SubstitutionMatrixHelper.getNuc4_2();
		} else if (matrixType.equals("NUC4.4")) {
			matrix = SubstitutionMatrixHelper.getNuc4_4();
		}

		SequencePair<DNASequence, NucleotideCompound> pair = null;
		if (alignmentType.equals("Global")) {
			pair = Alignments.getPairwiseAlignment(s1, s2, PairwiseSequenceAlignerType.GLOBAL, gapPenaltyExtension, matrix);
		} else if (alignmentType.equals("Local")) {
			pair = Alignments.getPairwiseAlignment(s1, s2, PairwiseSequenceAlignerType.LOCAL, gapPenaltyExtension, matrix);
		}

		return pair;
	}

	public static String getAlignment(SequencePair<DNASequence, NucleotideCompound> pair) {
		return pair.toString();

	}

	public static String getQuery(SequencePair<DNASequence, NucleotideCompound> pair) {
		AlignedSequence<DNASequence, NucleotideCompound> query = pair.getQuery();
		return query.toString();

	}

	public static String getTarget(SequencePair<DNASequence, NucleotideCompound> pair) {
		AlignedSequence<DNASequence, NucleotideCompound> target = pair.getTarget();
		return target.toString();

	}

	public static String getLength(SequencePair<DNASequence, NucleotideCompound> pair) {
		int length = pair.getLength();
		return Integer.toString(length);
	}

}

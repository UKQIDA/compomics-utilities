package com.compomics.util.experiment.biology;

import com.compomics.util.experiment.personalization.ExperimentObject;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.compomics.util.protein.Header.DatabaseType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class models a protein.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class Protein extends ExperimentObject {

    /**
     * The version UID for Serialization/Deserialization compatibility.
     */
    static final long serialVersionUID = 1987224639519365761L;
    /**
     * The protein accession.
     */
    private String accession;
    /**
     * Boolean indicating if the protein is not existing (decoy protein for
     * instance).
     */
    private boolean decoy;
    /**
     * The protein sequence.
     */
    private String sequence;
    /**
     * The protein database type.
     */
    private DatabaseType databaseType;

    /**
     * Constructor for a protein.
     */
    public Protein() {
    }

    /**
     * Simplistic constructor for a protein (typically used when loading
     * identification files).
     *
     * @param accession The protein accession
     * @param isDecoy boolean indicating whether the protein is a decoy
     */
    public Protein(String accession, boolean isDecoy) {
        this.accession = accession;
        this.decoy = isDecoy;
    }

    /**
     * Constructor for a protein.
     *
     * @param accession The protein accession
     * @param sequence The protein sequence
     * @param isDecoy boolean indicating whether the protein is a decoy
     */
    public Protein(String accession, String sequence, boolean isDecoy) {
        this.accession = accession;
        this.sequence = sequence;
        this.decoy = isDecoy;
    }

    /**
     * Constructor for a protein.
     *
     * @param accession The protein accession
     * @param databaseType The protein database the protein comes from
     * @param sequence The protein sequence
     * @param isDecoy boolean indicating whether the protein is a decoy
     */
    public Protein(String accession, DatabaseType databaseType, String sequence, boolean isDecoy) {
        this.accession = accession;
        this.databaseType = databaseType;
        this.sequence = sequence;
        this.decoy = isDecoy;
    }

    /**
     * Indicates if the protein is factice (from a decoy database for instance).
     *
     * @return a boolean indicating if the protein is factice
     */
    public boolean isDecoy() {
        return decoy;
    }

    /**
     * Getter for the protein accession.
     *
     * @return the protein accession
     */
    public String getAccession() {
        return accession;
    }

    /**
     * Getter for the protein database type.
     *
     * @return the protein database type
     */
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    /**
     * Getter for the protein sequence.
     *
     * @return the protein sequence
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * A method to compare proteins. For now accession based.
     *
     * @param anotherProtein an other protein
     * @return a boolean indicating if the proteins are identical
     */
    public boolean isSameAs(Protein anotherProtein) {
        return accession.equals(anotherProtein.getAccession());
    }

    /**
     * Returns the key for protein indexing. For now the protein accession.
     *
     * @return the key for protein indexing.
     */
    public String getProteinKey() {
        return accession;
    }

    /**
     * Returns the number of amino acids in the sequence.
     *
     * @return the number of amino acids in the sequence
     */
    public int getLength() {
        return sequence.length();
    }

    /**
     * Returns the number of observable amino acids in the sequence.
     *
     * @param enzyme the enzyme to use
     * @param pepMaxLength the max peptide length
     *
     * @return the number of observable amino acids of the sequence
     */
    public int getObservableLength(Enzyme enzyme, double pepMaxLength) {
        int length = 0, tempLength = 1;
        for (int i = 0; i < sequence.length() - 1; i++) {
            if (enzyme.isCleavageSite(sequence.charAt(i), sequence.charAt(i + 1))) {
                if (tempLength <= pepMaxLength) {
                    length += tempLength;
                }
                tempLength = 0;
            }
            tempLength++;
        }
        if (tempLength < pepMaxLength) {
            length += tempLength;
        }
        return length;
    }

    /**
     * Returns the number of cleavage sites.
     *
     * @param enzyme The selected enzyme
     *
     * @return the number of possible peptides
     */
    public int getNCleavageSites(Enzyme enzyme) {
        int nCleavageSites = 0;
        for (int i = 0; i < sequence.length() - 1; i++) {
            if (enzyme.isCleavageSite(sequence.charAt(i), sequence.charAt(i + 1))) {
                nCleavageSites++;
            }
        }
        return nCleavageSites;
    }

    /**
     * Returns the protein's molecular weight. (Note that when using a
     * SequenceFactory it is recommended to use the SequenceFactory's
     * computeMolecularWeight method instead, as that method stored the computed
     * molecular weights instead of recalculating them every time.)
     *
     * @return the protein's molecular weight in Da
     */
    public double computeMolecularWeight() {

        double mass = Atom.H.getMonoisotopicMass();

        for (int iaa = 0; iaa < sequence.length(); iaa++) {
            char aa = sequence.charAt(iaa);
            try {
                if (aa != '*') {
                    AminoAcid currentAA = AminoAcid.getAminoAcid(aa);
                    mass += currentAA.getMonoisotopicMass();
                }
            } catch (NullPointerException e) {
                if (aa == '>') {
                    throw new IllegalArgumentException("Error parsing the sequence of " + accession);
                } else {
                    throw new IllegalArgumentException("Unknown amino acid: " + aa);
                }
            } catch (IllegalArgumentException e) {
                if (aa == '>') {
                    throw new IllegalArgumentException("Error parsing the sequence of " + accession + ". Protein sequence: " + sequence + ".");
                } else {
                    throw new IllegalArgumentException("Unknown amino acid: " + aa);
                }
            }
        }

        mass += Atom.H.getMonoisotopicMass() + Atom.O.getMonoisotopicMass();

        return mass;
    }

    /**
     * Returns the list of indexes where a peptide can be found in the protein
     * sequence. 1 is the first amino acid.
     *
     * @param peptideSequence the sequence of the peptide of interest
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return the list of indexes where a peptide can be found in a protein
     * sequence
     */
    public ArrayList<Integer> getPeptideStart(String peptideSequence, SequenceMatchingPreferences sequenceMatchingPreferences) {
        AminoAcidPattern aminoAcidPattern = new AminoAcidPattern(peptideSequence);
        return aminoAcidPattern.getIndexes(sequence, sequenceMatchingPreferences);
    }

    /**
     * Returns a boolean indicating whether the protein starts with the given
     * peptide.
     *
     * @param peptideSequence the peptide sequence
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return a boolean indicating whether the protein starts with the given
     * peptide
     */
    public boolean isNTerm(String peptideSequence, SequenceMatchingPreferences sequenceMatchingPreferences) {
        String subSequence = sequence.substring(0, peptideSequence.length());
        AminoAcidSequence aminoAcidPattern = new AminoAcidSequence(peptideSequence);
        if (aminoAcidPattern.matchesIn(subSequence, sequenceMatchingPreferences)) {
            return true;
        }
        if (sequence.charAt(0) == 'M' && sequence.length() > peptideSequence.length()) {
            subSequence = sequence.substring(1, peptideSequence.length() + 1);
            if (aminoAcidPattern.matchesIn(subSequence, sequenceMatchingPreferences)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a boolean indicating whether the protein ends with the given
     * peptide.
     *
     * @param peptideSequence the peptide sequence
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return a boolean indicating whether the protein ends with the given
     * peptide
     */
    public boolean isCTerm(String peptideSequence, SequenceMatchingPreferences sequenceMatchingPreferences) {
        String subSequence;

        if (sequence.length() <= peptideSequence.length()) {
            subSequence = sequence;
        } else {
            subSequence = sequence.substring(sequence.length() - peptideSequence.length() - 1);
        }

        AminoAcidSequence aminoAcidPattern = new AminoAcidSequence(peptideSequence);
        return aminoAcidPattern.matchesIn(subSequence, sequenceMatchingPreferences);
    }

    /**
     * Returns true of the peptide is non-enzymatic, i.e., has one or more end
     * points that cannot be caused by the enzyme alone. False means that both
     * the endpoints of the peptides could be caused by the selected enzyme, or
     * that it is a terminal peptide (where one end point is most likely not
     * enzymatic). Note that if a peptide maps to multiple locations in the
     * protein sequence this method returns true if one or more of these
     * peptides are enzymatic, even if not all mappings are enzymatic.
     *
     * @param peptideSequence the peptide sequence to check
     * @param enzyme the enzyme to use
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return true of the peptide is non-enzymatic
     *
     * @throws IOException if an IOException occurs
     */
    public boolean isEnzymaticPeptide(String peptideSequence, Enzyme enzyme, SequenceMatchingPreferences sequenceMatchingPreferences) throws IOException {

        // get the surrounding amino acids
        HashMap<Integer, String[]> surroundingAminoAcids = getSurroundingAA(peptideSequence, 1, sequenceMatchingPreferences);

        String firstAA = peptideSequence.charAt(0) + "";
        String lastAA = peptideSequence.charAt(peptideSequence.length() - 1) + "";

        // iterate the possible extended peptide sequences
        for (int index : surroundingAminoAcids.keySet()) {

            String before = surroundingAminoAcids.get(index)[0];
            String after = surroundingAminoAcids.get(index)[1];

            // @TODO: how to handle semi-specific enzymes??
            if ((enzyme.isCleavageSite(before, firstAA) && enzyme.isCleavageSite(lastAA, after)
                    || (before.length() == 0 && enzyme.isCleavageSite(lastAA, after)
                    || (enzyme.isCleavageSite(before, firstAA) && after.length() == 0)))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the amino acids surrounding a peptide in the sequence of the
     * given protein in a map: peptide start index &gt; (amino acids before,
     * amino acids after).
     *
     * @param peptide the sequence of the peptide of interest
     * @param nAA the number of amino acids to include
     * @param sequenceMatchingPreferences the sequence matching preferences
     *
     * @return the amino acids surrounding a peptide in the protein sequence
     *
     * @throws IOException Exception thrown whenever an error occurred while
     * parsing the protein sequence
     */
    public HashMap<Integer, String[]> getSurroundingAA(String peptide, int nAA, SequenceMatchingPreferences sequenceMatchingPreferences) throws IOException {

        ArrayList<Integer> startIndexes = getPeptideStart(peptide, sequenceMatchingPreferences);
        HashMap<Integer, String[]> result = new HashMap<Integer, String[]>();

        for (int startIndex : startIndexes) {

            result.put(startIndex, new String[2]);
            String subsequence = "";

            int stringIndex = startIndex - 1;
            for (int aa = stringIndex - nAA; aa < stringIndex; aa++) {
                if (aa >= 0 && aa < sequence.length()) {
                    subsequence += sequence.charAt(aa);
                }
            }

            result.get(startIndex)[0] = subsequence;
            subsequence = "";

            for (int aa = stringIndex + peptide.length(); aa < stringIndex + peptide.length() + nAA; aa++) {
                if (aa >= 0 && aa < sequence.length()) {
                    subsequence += sequence.charAt(aa);
                }
            }

            result.get(startIndex)[1] = subsequence;
        }

        return result;
    }
}

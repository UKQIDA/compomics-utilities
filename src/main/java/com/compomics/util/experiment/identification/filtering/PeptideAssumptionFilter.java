package com.compomics.util.experiment.identification.filtering;

import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.Util;
import com.compomics.util.experiment.biology.Enzyme;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.protein_sequences.SequenceFactory;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.protein_inference.PeptideMapper;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class filters peptide assumptions based on various properties.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class PeptideAssumptionFilter implements Serializable {

    /**
     * Serial number for backward compatibility.
     */
    static final long serialVersionUID = 8416219001106063781L;
    /**
     * The minimal peptide length allowed.
     */
    private int minPepLength;
    /**
     * The maximal peptide length allowed.
     */
    private int maxPepLength;
    /**
     * The maximal m/z deviation allowed.
     */
    private double maxMassDeviation;
    /**
     * Boolean indicating the unit of the allowed m/z deviation (true: ppm,
     * false: Da).
     */
    private boolean isPpm;
    /**
     * Boolean indicating whether peptides presenting unknown PTMs should be
     * ignored.
     */
    private boolean unknownPtm;
    /**
     * The minimum number of missed cleavages allowed. Null means no lower
     * limit.
     */
    private Integer minMissedCleavages;
    /**
     * The maximum number of missed cleavages allowed. Null means no upper
     * limit.
     */
    private Integer maxMissedCleavages;
    /**
     * The minimum number of isotopes allowed. Null means no lower limit.
     */
    private Integer minIsotopes;
    /**
     * The maximum number of isotopes allowed. Null means no upper limit.
     */
    private Integer maxIsotopes;

    /**
     * Constructor with default settings.
     */
    public PeptideAssumptionFilter() {
        minPepLength = 8;
        maxPepLength = 30;
        maxMassDeviation = -1;
        isPpm = true;
        unknownPtm = true;
        minMissedCleavages = null;
        maxMissedCleavages = null;
        minIsotopes = null;
        maxIsotopes = null;
    }

    /**
     * Constructor for an Identification filter.
     *
     * @param minPepLength the minimal peptide length allowed (0 or less for
     * disabled)
     * @param maxPepLength the maximal peptide length allowed (0 or less for
     * disabled)
     * @param maxMzDeviation the maximal m/z deviation allowed (0 or less for
     * disabled)
     * @param isPpm boolean indicating the unit of the allowed m/z deviation
     * (true: ppm, false: Da)
     * @param unknownPTM shall peptides presenting unknownPTMs be ignored
     * @param minMissedCleavages the minimum number of missed cleavages allowed
     * (null for disabled)
     * @param maxMissedCleavages the maximum number of missed cleavages allowed
     * (null for disabled)
     * @param minIsotopes the minimum number of isotopes allowed (null for
     * disabled)
     * @param maxIsotopes the maximum number of isotopes allowed (null for
     * disabled)
     */
    public PeptideAssumptionFilter(int minPepLength, int maxPepLength, double maxMzDeviation, boolean isPpm, boolean unknownPTM, Integer minMissedCleavages, Integer maxMissedCleavages, Integer minIsotopes, Integer maxIsotopes) {
        this.minPepLength = minPepLength;
        this.maxPepLength = maxPepLength;
        this.maxMassDeviation = maxMzDeviation;
        this.isPpm = isPpm;
        this.unknownPtm = unknownPTM;
        this.minMissedCleavages = minMissedCleavages;
        this.maxMissedCleavages = maxMissedCleavages;
        this.minIsotopes = minIsotopes;
        this.maxIsotopes = maxIsotopes;
    }

    /**
     * Updates the filter based on the search parameters.
     *
     * @param searchParameters the search parameters where to take the
     * information from
     */
    public void setFilterFromSearchParameters(SearchParameters searchParameters) {
        this.isPpm = searchParameters.isPrecursorAccuracyTypePpm();
        this.maxMassDeviation = searchParameters.getPrecursorAccuracy();
        this.minIsotopes = searchParameters.getMinIsotopicCorrection();
        this.maxIsotopes = searchParameters.getMaxIsotopicCorrection();
        this.unknownPtm = true;
    }

    /**
     * Validates the peptide based on the peptide length, the share of X's in
     * the sequence and the allowed number of missed cleavages.
     *
     * @param peptide the peptide to validate
     * @param sequenceMatchingPreferences the sequence matching preferences
     * containing the maximal share of X's allowed
     * @param enzyme the enzyme
     *
     * @return a boolean indicating whether the peptide passed the test
     */
    public boolean validatePeptide(Peptide peptide, SequenceMatchingPreferences sequenceMatchingPreferences, Enzyme enzyme) {

        String peptideSequence = peptide.getSequence();
        int sequenceLength = peptideSequence.length();

        if ((maxPepLength > 0 && sequenceLength > maxPepLength)
                || (minPepLength > 0 && sequenceLength < minPepLength)) {
            return false;
        }

        double xShare = ((double) Util.getOccurrence(peptideSequence, 'X')) / sequenceLength;
        if (sequenceMatchingPreferences.hasLimitX() && xShare > sequenceMatchingPreferences.getLimitX()) {
            return false;
        }

        if (minMissedCleavages != null || maxMissedCleavages != null) {

            int numberOfMissedCleavages = peptide.getNMissedCleavages(enzyme);

            if (minMissedCleavages != null && numberOfMissedCleavages < minMissedCleavages) {
                return false;
            }
            if (maxMissedCleavages != null && numberOfMissedCleavages > maxMissedCleavages) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates a peptide depending on its protein inference status. Maps the
     * peptide to proteins in case it was not done before using the default
     * protein tree of the sequence factory
     *
     * @param peptide the peptide
     * @param sequenceMatchingPreferences the sequence matching preferences
     * @return a boolean indicating whether the peptide passed the test
     *
     * @throws IOException if an IOException occurs
     * @throws SQLException if an SQLException occurs
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws InterruptedException if an InterruptedException occurs
     */
    public boolean validateProteins(Peptide peptide, SequenceMatchingPreferences sequenceMatchingPreferences) throws IOException, SQLException, ClassNotFoundException, InterruptedException {
        return validateProteins(peptide, sequenceMatchingPreferences, SequenceFactory.getInstance().getDefaultPeptideMapper());
    }

    /**
     * Validates a peptide depending on its protein inference status. Maps the
     * peptide to proteins in case it was not done before
     *
     * @param peptide the peptide
     * @param sequenceMatchingPreferences the sequence matching preferences
     * @param peptideMapper the peptide mapper to use for peptide to protein mapping
     *
     * @return a boolean indicating whether the peptide passed the test
     *
     * @throws IOException if an IOException occurs
     * @throws SQLException if an SQLException occurs
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws InterruptedException if an InterruptedException occurs
     */
    public boolean validateProteins(Peptide peptide, SequenceMatchingPreferences sequenceMatchingPreferences, PeptideMapper peptideMapper)
            throws IOException, SQLException, ClassNotFoundException, InterruptedException {

        ArrayList<String> accessions = peptide.getParentProteins(sequenceMatchingPreferences, peptideMapper);

        if (accessions != null && accessions.size() > 1) {
            boolean target = false;
            boolean decoy = false;
            for (String accession : accessions) {
                if (SequenceFactory.getInstance().isDecoyAccession(accession)) {
                    decoy = true;
                } else {
                    target = true;
                }
            }
            if (target && decoy) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates the modifications of a peptide.
     *
     * @param peptide the peptide of interest
     * @param sequenceMatchingPreferences the sequence matching preferences for
     * peptide to protein mapping
     * @param ptmSequenceMatchingPreferences the sequence matching preferences
     * for PTM to peptide mapping
     * @param modificationProfile the modification profile of the identification
     *
     * @return a boolean indicating whether the peptide passed the test
     */
    public boolean validateModifications(Peptide peptide, SequenceMatchingPreferences sequenceMatchingPreferences,
            SequenceMatchingPreferences ptmSequenceMatchingPreferences, PtmSettings modificationProfile) {

        // check if it is an unknown peptide
        if (unknownPtm) {
            ArrayList<ModificationMatch> modificationMatches = peptide.getModificationMatches();
            if (modificationMatches != null) {
                for (ModificationMatch modMatch : modificationMatches) {
                    String ptmName = modMatch.getTheoreticPtm();
                    if (ptmName.equals(PTMFactory.unknownPTM.getName())) {
                        return false;
                    }
                }
            }
        }

        PTMFactory ptmFactory = PTMFactory.getInstance();

        // get the variable ptms and the number of times they occur
        HashMap<Double, Integer> modMatches = new HashMap<Double, Integer>(peptide.getNModifications());
        if (peptide.isModified()) {
            for (ModificationMatch modMatch : peptide.getModificationMatches()) {
                if (modMatch.isVariable()) {
                    String modName = modMatch.getTheoreticPtm();
                    PTM ptm = ptmFactory.getPTM(modName);
                    double mass = ptm.getMass();
                    if (!modMatches.containsKey(mass)) {
                        modMatches.put(mass, 1);
                    } else {
                        modMatches.put(mass, modMatches.get(mass) + 1);
                    }
                }
            }
        }

        // check if there are more ptms than ptm sites
        for (double mass : modMatches.keySet()) {
            try {
                ArrayList<Integer> possiblePositions = peptide.getPotentialModificationSites(mass, sequenceMatchingPreferences, ptmSequenceMatchingPreferences, modificationProfile);
                if (possiblePositions.size() < modMatches.get(mass)) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    /**
     * Validates the mass deviation of a peptide assumption.
     *
     * @param assumption the considered peptide assumption
     * @param spectrumKey the key of the spectrum used to get the precursor the
     * precursor should be accessible via the spectrum factory
     * @param spectrumFactory the spectrum factory
     * @param searchParameters the search parameters
     *
     * @return a boolean indicating whether the given assumption passes the
     * filter
     *
     * @throws IOException if an error occurs while reading the spectrum
     * @throws MzMLUnmarshallerException if an MzMLUnmarshallerException occurs
     * reading while the spectrum
     */
    public boolean validatePrecursor(PeptideAssumption assumption, String spectrumKey, SpectrumFactory spectrumFactory, SearchParameters searchParameters) throws IOException, MzMLUnmarshallerException {
        double precursorMz = spectrumFactory.getPrecursorMz(spectrumKey);
        int isotopeNumber = assumption.getIsotopeNumber(precursorMz, searchParameters.getMinIsotopicCorrection(), searchParameters.getMaxIsotopicCorrection());
        if (minIsotopes != null && isotopeNumber < minIsotopes) {
            return false;
        }
        if (maxIsotopes != null && isotopeNumber > maxIsotopes) {
            return false;
        }
        Double mzDeviation = assumption.getDeltaMass(precursorMz, isPpm, searchParameters.getMinIsotopicCorrection(), searchParameters.getMaxIsotopicCorrection());
        return (maxMassDeviation <= 0 || Math.abs(mzDeviation) <= maxMassDeviation);
    }

    /**
     * Returns a boolean indicating whether unknown PTMs shall be removed.
     *
     * @return a boolean indicating whether unknown PTMs shall be removed
     */
    public boolean removeUnknownPTMs() {
        return unknownPtm;
    }

    /**
     * Set whether unknown PTMs shall be removed.
     *
     * @param unknownPtm whether unknown PTMs shall be removed
     */
    public void setRemoveUnknownPTMs(boolean unknownPtm) {
        this.unknownPtm = unknownPtm;
    }

    /**
     * Indicates whether the mass tolerance is in ppm (true) or Dalton (false).
     *
     * @return a boolean indicating whether the mass tolerance is in ppm (true)
     * or Dalton (false)
     */
    public boolean isIsPpm() {
        return isPpm;
    }

    /**
     * Sets whether the mass tolerance is in ppm (true) or Dalton (false).
     *
     * @param isPpm a boolean indicating whether the mass tolerance is in ppm
     * (true) or Dalton (false)
     */
    public void setIsPpm(boolean isPpm) {
        this.isPpm = isPpm;
    }

    /**
     * Returns the maximal m/z deviation allowed.
     *
     * @return the maximal mass deviation allowed
     */
    public double getMaxMzDeviation() {
        return maxMassDeviation;
    }

    /**
     * Sets the maximal m/z deviation allowed.
     *
     * @param maxMzDeviation the maximal mass deviation allowed
     */
    public void setMaxMzDeviation(double maxMzDeviation) {
        this.maxMassDeviation = maxMzDeviation;
    }

    /**
     * Returns the maximal peptide length allowed.
     *
     * @return the maximal peptide length allowed
     */
    public int getMaxPepLength() {
        return maxPepLength;
    }

    /**
     * Sets the maximal peptide length allowed.
     *
     * @param maxPepLength the maximal peptide length allowed
     */
    public void setMaxPepLength(int maxPepLength) {
        this.maxPepLength = maxPepLength;
    }

    /**
     * Returns the maximal peptide length allowed.
     *
     * @return the maximal peptide length allowed
     */
    public int getMinPepLength() {
        return minPepLength;
    }

    /**
     * Sets the maximal peptide length allowed.
     *
     * @param minPepLength the maximal peptide length allowed
     */
    public void setMinPepLength(int minPepLength) {
        this.minPepLength = minPepLength;
    }

    /**
     * Returns the minimal number of isotopes allowed (inclusive).
     *
     * @return the minimal number of isotopes allowed
     */
    public Integer getMinIsotopes() {
        return minIsotopes;
    }

    /**
     * Sets the minimal number of isotopes allowed (inclusive).
     *
     * @param minIsotopes the minimal number of isotopes allowed
     */
    public void setMinIsotopes(Integer minIsotopes) {
        this.minIsotopes = minIsotopes;
    }

    /**
     * Returns the maximal number of isotopes allowed (inclusive).
     *
     * @return the maximal number of isotopes allowed
     */
    public Integer getMaxIsotopes() {
        return maxIsotopes;
    }

    /**
     * Sets the maximal number of isotopes allowed (inclusive).
     *
     * @param maxIsotopes the maximal number of isotopes allowed
     */
    public void setMaxIsotopes(Integer maxIsotopes) {
        this.maxIsotopes = maxIsotopes;
    }

    /**
     * Indicates whether this filter is the same as another one.
     *
     * @param anotherFilter another filter
     * @return a boolean indicating that the filters have the same parameters
     */
    public boolean isSameAs(PeptideAssumptionFilter anotherFilter) {

        if (minMissedCleavages != null && anotherFilter.getMinMissedCleavages() != null) {
            if (!minMissedCleavages.equals(anotherFilter.getMinMissedCleavages())) {
                return false;
            }
        }
        if (minMissedCleavages != null && anotherFilter.getMinMissedCleavages() == null) {
            return false;
        }
        if (minMissedCleavages == null && anotherFilter.getMinMissedCleavages() != null) {
            return false;
        }
        if (maxMissedCleavages != null && anotherFilter.getMaxMissedCleavages() != null) {
            if (maxMissedCleavages.equals(anotherFilter.getMaxMissedCleavages())) {
                return false;
            }
        }
        if (maxMissedCleavages != null && anotherFilter.getMaxMissedCleavages() == null) {
            return false;
        }
        if (maxMissedCleavages == null && anotherFilter.getMaxMissedCleavages() != null) {
            return false;
        }

        if (minIsotopes != null && anotherFilter.getMinIsotopes() != null) {
            if (!minIsotopes.equals(anotherFilter.getMinIsotopes())) {
                return false;
            }
        }
        if (minIsotopes != null && anotherFilter.getMinIsotopes() == null) {
            return false;
        }
        if (minIsotopes == null && anotherFilter.getMinIsotopes() != null) {
            return false;
        }
        if (maxIsotopes != null && anotherFilter.getMaxIsotopes() != null) {
            if (!maxIsotopes.equals(anotherFilter.getMaxIsotopes())) {
                return false;
            }
        }
        if (maxIsotopes != null && anotherFilter.getMaxIsotopes() == null) {
            return false;
        }
        if (maxIsotopes == null && anotherFilter.getMaxIsotopes() != null) {
            return false;
        }

        return isPpm == anotherFilter.isPpm
                && unknownPtm == anotherFilter.removeUnknownPTMs()
                && minPepLength == anotherFilter.getMinPepLength()
                && maxPepLength == anotherFilter.getMaxPepLength()
                && maxMassDeviation == anotherFilter.getMaxMzDeviation();
    }

    /**
     * Returns a short description of the parameters.
     *
     * @return a short description of the parameters
     */
    public String getShortDescription() {

        String newLine = System.getProperty("line.separator");

        StringBuilder output = new StringBuilder();

        output.append("Peptide Length: ").append(minPepLength).append("-").append(maxPepLength).append(".").append(newLine);
        if (maxMassDeviation >= 0) {
            output.append("Precursor m/z Deviation: ").append(maxMassDeviation);
            if (isPpm) {
                output.append(" ppm.").append(newLine);
            } else {
                output.append(" Da.").append(newLine);
            }
        }
        output.append("Ignore Unknown PTMs: ").append(unknownPtm).append(".").append(newLine);

        if (minMissedCleavages != null || maxMissedCleavages != null) {

            output.append("Missed Cleavages: ");

            if (minMissedCleavages != null) {
                output.append(minMissedCleavages);
            } else {
                output.append("0");
            }

            output.append("-");

            if (maxMissedCleavages != null) {
                output.append(maxMissedCleavages);
            } else {
                output.append("n");
            }

            output.append(".").append(newLine);
        }

        if (minIsotopes != null || maxIsotopes != null) {

            output.append("Isotopes: ");

            if (minIsotopes != null) {
                output.append(minIsotopes);
            } else {
                output.append("n");
            }

            output.append("-");

            if (maxIsotopes != null) {
                output.append(maxIsotopes);
            } else {
                output.append("n");
            }

            output.append(".").append(newLine);
        }

        return output.toString();
    }

    /**
     * Returns the minimum number of missed cleavages. Null means no limit.
     *
     * @return the minMissedCleavages
     */
    public Integer getMinMissedCleavages() {
        return minMissedCleavages;
    }

    /**
     * Set the minimum number of missed cleavages. Null means no limit.
     *
     * @param minMissedCleavages the minMissedCleavages to set
     */
    public void setMinMissedCleavages(Integer minMissedCleavages) {
        this.minMissedCleavages = minMissedCleavages;
    }

    /**
     * Returns the maximum number of missed cleavages. Null means no limit.
     *
     * @return the maxMissedCleavages
     */
    public Integer getMaxMissedCleavages() {
        return maxMissedCleavages;
    }

    /**
     * Set the maximum number of missed cleavages. Null means no limit.
     *
     * @param maxMissedCleavages the maxMissedCleavages to set
     */
    public void setMaxMissedCleavages(Integer maxMissedCleavages) {
        this.maxMissedCleavages = maxMissedCleavages;
    }
}

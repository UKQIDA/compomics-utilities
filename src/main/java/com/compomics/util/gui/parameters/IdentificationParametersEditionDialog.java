package com.compomics.util.gui.parameters;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.NeutralLoss;
import com.compomics.util.experiment.biology.ions.ReporterIon;
import com.compomics.util.experiment.identification.filtering.PeptideAssumptionFilter;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationSettings;
import com.compomics.util.gui.gene_mapping.SpeciesDialog;
import com.compomics.util.gui.parameters.identification_parameters.AnnotationSettingsDialog;
import com.compomics.util.gui.parameters.identification_parameters.FractionSettingsDialog;
import com.compomics.util.gui.parameters.identification_parameters.MatchesImportFiltersDialog;
import com.compomics.util.gui.parameters.identification_parameters.PTMLocalizationParametersDialog;
import com.compomics.util.gui.parameters.identification_parameters.ProteinInferenceSettingsDialog;
import com.compomics.util.gui.parameters.identification_parameters.PsmScoringSettingsDialog;
import com.compomics.util.gui.parameters.identification_parameters.SearchSettingsDialog;
import com.compomics.util.gui.parameters.identification_parameters.SequenceMatchingSettingsDialog;
import com.compomics.util.gui.parameters.identification_parameters.ValidationQCPreferencesDialog;
import com.compomics.util.gui.parameters.identification_parameters.ValidationQCPreferencesDialogParent;
import com.compomics.util.gui.parameters.identification_parameters.ValidationSettingsDialog;
import com.compomics.util.io.ConfigurationFile;
import com.compomics.util.preferences.FractionSettings;
import com.compomics.util.preferences.GenePreferences;
import com.compomics.util.preferences.IdMatchValidationPreferences;
import com.compomics.util.preferences.IdentificationParameters;
import com.compomics.util.preferences.LastSelectedFolder;
import com.compomics.util.preferences.PTMScoringPreferences;
import com.compomics.util.preferences.ProteinInferencePreferences;
import com.compomics.util.preferences.PsmScoringPreferences;
import com.compomics.util.preferences.SequenceMatchingPreferences;
import com.compomics.util.preferences.ValidationQCPreferences;
import java.awt.Dialog;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JOptionPane;

/**
 * IdentificationParametersEditionDialog.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class IdentificationParametersEditionDialog extends javax.swing.JDialog {

    /**
     * The parent frame.
     */
    private java.awt.Frame parentFrame;
    /**
     * Boolean indicating whether the user canceled the editing.
     */
    private boolean canceled = false;
    /**
     * The normal icon.
     */
    private Image normalIcon;
    /**
     * The waiting icon.
     */
    private Image waitingIcon;
    /**
     * The last selected folder
     */
    private LastSelectedFolder lastSelectedFolder;
    /**
     * Boolean indicating whether the parameters can be edited.
     */
    private boolean editable;
    /**
     * The configuration file containing the modification use.
     */
    private ConfigurationFile configurationFile;
    /**
     * The peak annotation settings.
     */
    private AnnotationSettings annotationSettings;
    /**
     * The parameters used for the spectrum matching.
     */
    private SearchParameters searchParameters;
    /**
     * The peptide to protein matching preferences.
     */
    private SequenceMatchingPreferences sequenceMatchingPreferences;
    /**
     * The gene preferences.
     */
    private GenePreferences genePreferences;
    /**
     * The PSM scores to use.
     */
    private PsmScoringPreferences psmScoringPreferences;
    /**
     * The PSM filter.
     */
    private PeptideAssumptionFilter peptideAssumptionFilter = new PeptideAssumptionFilter();
    /**
     * The PTM localization scoring preferences.
     */
    private PTMScoringPreferences ptmScoringPreferences = new PTMScoringPreferences();
    /**
     * The protein inference preferences.
     */
    private ProteinInferencePreferences proteinInferencePreferences;
    /**
     * The identification validation preferences.
     */
    private IdMatchValidationPreferences idValidationPreferences = new IdMatchValidationPreferences();
    /**
     * The fraction settings.
     */
    private FractionSettings fractionSettings;
    /**
     * A parent handling the edition of QC filters.
     */
    private ValidationQCPreferencesDialogParent validationQCPreferencesDialogParent;

    /**
     * Creates a new IdentificationParametersEditionDialog with a frame as
     * owner.
     *
     * @param parentFrame the parent frame
     * @param identificationParameters the identification parameters to display
     * @param configurationFile the configuration file containing the PTM usage
     * preferences
     * @param normalIcon the normal icon
     * @param waitingIcon the waiting icon
     * @param lastSelectedFolder the last selected folder
     * @param validationQCPreferencesDialogParent a parent handling the edition
     * of QC filters
     * @param editable boolean indicating whether the parameters can be edited
     */
    public IdentificationParametersEditionDialog(java.awt.Frame parentFrame, IdentificationParameters identificationParameters,
            ConfigurationFile configurationFile, Image normalIcon, Image waitingIcon, LastSelectedFolder lastSelectedFolder,
            ValidationQCPreferencesDialogParent validationQCPreferencesDialogParent, boolean editable) {
        super(parentFrame, true);

        this.parentFrame = parentFrame;
        this.annotationSettings = identificationParameters.getAnnotationPreferences();
        this.searchParameters = identificationParameters.getSearchParameters();
        this.sequenceMatchingPreferences = identificationParameters.getSequenceMatchingPreferences();
        this.genePreferences = identificationParameters.getGenePreferences();
        this.psmScoringPreferences = identificationParameters.getPsmScoringPreferences();
        this.peptideAssumptionFilter = identificationParameters.getPeptideAssumptionFilter();
        this.ptmScoringPreferences = identificationParameters.getPtmScoringPreferences();
        this.proteinInferencePreferences = identificationParameters.getProteinInferencePreferences();
        this.idValidationPreferences = identificationParameters.getIdValidationPreferences();
        this.fractionSettings = identificationParameters.getFractionSettings();
        this.normalIcon = normalIcon;
        this.waitingIcon = waitingIcon;
        this.configurationFile = configurationFile;
        this.lastSelectedFolder = lastSelectedFolder;
        this.validationQCPreferencesDialogParent = validationQCPreferencesDialogParent;
        this.editable = editable;

        initComponents();
        setUpGui();
        populateGUI(identificationParameters);
        setLocationRelativeTo(parentFrame);
        setVisible(true);
    }

    /**
     * Creates a new IdentificationParametersEditionDialog with a dialog as
     * owner.
     *
     * @param owner the dialog owner
     * @param parentFrame the parent frame
     * @param identificationParameters the identification parameters to display
     * @param configurationFile the configuration file containing the PTM usage
     * preferences
     * @param normalIcon the normal icon
     * @param waitingIcon the waiting icon
     * @param lastSelectedFolder the last selected folder
     * @param validationQCPreferencesDialogParent a parent handling the edition
     * of QC filters
     * @param editable boolean indicating whether the parameters can be edited
     */
    public IdentificationParametersEditionDialog(Dialog owner, java.awt.Frame parentFrame, IdentificationParameters identificationParameters,
            ConfigurationFile configurationFile, Image normalIcon, Image waitingIcon, LastSelectedFolder lastSelectedFolder,
            ValidationQCPreferencesDialogParent validationQCPreferencesDialogParent, boolean editable) {
        super(owner, true);

        this.parentFrame = parentFrame;
        this.annotationSettings = identificationParameters.getAnnotationPreferences();
        this.searchParameters = identificationParameters.getSearchParameters();
        this.sequenceMatchingPreferences = identificationParameters.getSequenceMatchingPreferences();
        this.genePreferences = identificationParameters.getGenePreferences();
        this.psmScoringPreferences = identificationParameters.getPsmScoringPreferences();
        this.peptideAssumptionFilter = identificationParameters.getPeptideAssumptionFilter();
        this.ptmScoringPreferences = identificationParameters.getPtmScoringPreferences();
        this.proteinInferencePreferences = identificationParameters.getProteinInferencePreferences();
        this.idValidationPreferences = identificationParameters.getIdValidationPreferences();
        this.fractionSettings = identificationParameters.getFractionSettings();
        this.normalIcon = normalIcon;
        this.waitingIcon = waitingIcon;
        this.configurationFile = configurationFile;
        this.lastSelectedFolder = lastSelectedFolder;
        this.validationQCPreferencesDialogParent = validationQCPreferencesDialogParent;
        this.editable = editable;

        initComponents();
        setUpGui();
        populateGUI(identificationParameters);
        setLocationRelativeTo(owner);
        setVisible(true);
    }

    /**
     * Set up the GUI.
     */
    private void setUpGui() {
        nameTxt.setEditable(editable);
    }

    /**
     * Populates the GUI using the given identification parameters.
     *
     * @param identificationParameters the identification parameters to display
     */
    public void populateGUI(IdentificationParameters identificationParameters) {
        nameTxt.setText(identificationParameters.getName());

        // show the parameter details
        int columnWidth = 150;
        int maxDescriptionLength = 150;

        spectrumMatchingButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + spectrumMatchingButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(searchParameters.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        spectrumAnnotationButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + spectrumAnnotationButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(annotationSettings.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        sequenceMatchingButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + sequenceMatchingButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(sequenceMatchingPreferences.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        geneMappingButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + geneMappingButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(genePreferences.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        matchesFiltersButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + matchesFiltersButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(peptideAssumptionFilter.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        psmScoringButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + psmScoringButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(psmScoringPreferences.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        ptmLocalizationButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + ptmLocalizationButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(ptmScoringPreferences.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        proteinInferenceButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + proteinInferenceButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(proteinInferencePreferences.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        validationButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + validationButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(idValidationPreferences.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        fractionsButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + fractionsButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(fractionSettings.getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");

        qualityControlButton.setText("<html><table><tr><td width=\"" + columnWidth + "\"><b>" + qualityControlButton.getText() + "</b></td>"
                + "<td><font size=2>" + formatDescription(idValidationPreferences.getValidationQCPreferences().getShortDescription(), maxDescriptionLength) + "</font></td></tr></table></html>");
    }

    /**
     * Make sure that the parameter description is not too long.
     *
     * @param description original description
     * @param maxDescriptionLength max number of characters
     * @return the new description
     */
    private String formatDescription(String description, int maxDescriptionLength) {
        if (description.length() > maxDescriptionLength) {
            description = description.substring(0, maxDescriptionLength) + "...";
        }
        return description;
    }

    /**
     * Indicates whether the user canceled the editing.
     *
     * @return a boolean indicating whether the user canceled the editing
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Returns the identification parameters as set by the user.
     *
     * @return the identification parameters as set by the user
     */
    public IdentificationParameters getIdentificationParameters() {
        IdentificationParameters identificationParameters = new IdentificationParameters();
        identificationParameters.setName(nameTxt.getText());
        identificationParameters.setDescription(searchParameters.getShortDescription(), true);
        identificationParameters.setAnnotationSettings(annotationSettings);
        identificationParameters.setSearchParameters(searchParameters);
        identificationParameters.setSequenceMatchingPreferences(sequenceMatchingPreferences);
        identificationParameters.setGenePreferences(genePreferences);
        identificationParameters.setIdFilter(peptideAssumptionFilter);
        identificationParameters.setPsmScoringPreferences(psmScoringPreferences);
        identificationParameters.setPtmScoringPreferences(ptmScoringPreferences);
        identificationParameters.setProteinInferencePreferences(proteinInferencePreferences);
        identificationParameters.setIdValidationPreferences(idValidationPreferences);
        identificationParameters.setFractionSettings(fractionSettings);
        return identificationParameters;
    }

    /**
     * Updates the identification settings in case the selected PTMs have been
     * changed.
     */
    private void selectedPtmsChanged() {
        PtmSettings ptmSettings = searchParameters.getPtmSettings();
        HashMap<Ion.IonType, HashSet<Integer>> ionTypes = annotationSettings.getIonTypes();
        if (annotationSettings.getReporterIons()) {
            HashSet<Integer> reporterIons = IonFactory.getReporterIons(ptmSettings);
            ionTypes.put(ReporterIon.IonType.REPORTER_ION, reporterIons);
        }
        if (annotationSettings.isAutomaticAnnotation() || annotationSettings.areNeutralLossesSequenceAuto()) {
            ArrayList<NeutralLoss> neutralLosses = IonFactory.getNeutralLosses(searchParameters.getPtmSettings());
            for (NeutralLoss neutralLoss : neutralLosses) {
                annotationSettings.addNeutralLoss(neutralLoss);
            }
        }
    }

    /**
     * Validates the user input.
     *
     * @return a boolean indicating whether the user input is valid
     */
    public boolean validateInput() {

        String name = nameTxt.getText();
        for (char character : name.toCharArray()) {
            String charAsString = character + "";
            if (charAsString.matches("[^\\dA-Za-z ]")) {
                JOptionPane.showMessageDialog(this, "Unsupported character in parameters name (" + character + "). Please avoid special characters in parameters name.",
                        "Special Character", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }

        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        attributesPanel = new javax.swing.JPanel();
        nameTxt = new javax.swing.JTextField();
        settingsPanel = new javax.swing.JPanel();
        spectrumMatchingButton = new javax.swing.JButton();
        spectrumAnnotationButton = new javax.swing.JButton();
        sequenceMatchingButton = new javax.swing.JButton();
        geneMappingButton = new javax.swing.JButton();
        matchesFiltersButton = new javax.swing.JButton();
        psmScoringButton = new javax.swing.JButton();
        ptmLocalizationButton = new javax.swing.JButton();
        proteinInferenceButton = new javax.swing.JButton();
        validationButton = new javax.swing.JButton();
        fractionsButton = new javax.swing.JButton();
        qualityControlButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Identification Settings");

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        attributesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        attributesPanel.setOpaque(false);

        nameTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout attributesPanelLayout = new javax.swing.GroupLayout(attributesPanel);
        attributesPanel.setLayout(attributesPanelLayout);
        attributesPanelLayout.setHorizontalGroup(
            attributesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, attributesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nameTxt)
                .addContainerGap())
        );
        attributesPanelLayout.setVerticalGroup(
            attributesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attributesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        settingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Identification Settings"));
        settingsPanel.setOpaque(false);

        spectrumMatchingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        spectrumMatchingButton.setText("Spectrum Matching");
        spectrumMatchingButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        spectrumMatchingButton.setIconTextGap(15);
        spectrumMatchingButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        spectrumMatchingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spectrumMatchingButtonActionPerformed(evt);
            }
        });

        spectrumAnnotationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        spectrumAnnotationButton.setText("Spectrum Annotation");
        spectrumAnnotationButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        spectrumAnnotationButton.setIconTextGap(15);
        spectrumAnnotationButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        spectrumAnnotationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spectrumAnnotationButtonActionPerformed(evt);
            }
        });

        sequenceMatchingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        sequenceMatchingButton.setText("Sequence Matching");
        sequenceMatchingButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sequenceMatchingButton.setIconTextGap(15);
        sequenceMatchingButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        sequenceMatchingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sequenceMatchingButtonActionPerformed(evt);
            }
        });

        geneMappingButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        geneMappingButton.setText("Gene Annotation");
        geneMappingButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        geneMappingButton.setIconTextGap(15);
        geneMappingButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        geneMappingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                geneMappingButtonActionPerformed(evt);
            }
        });

        matchesFiltersButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        matchesFiltersButton.setText("Import Filters");
        matchesFiltersButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        matchesFiltersButton.setIconTextGap(15);
        matchesFiltersButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        matchesFiltersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matchesFiltersButtonActionPerformed(evt);
            }
        });

        psmScoringButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        psmScoringButton.setText("PSM Scoring");
        psmScoringButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        psmScoringButton.setIconTextGap(15);
        psmScoringButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        psmScoringButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                psmScoringButtonActionPerformed(evt);
            }
        });

        ptmLocalizationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        ptmLocalizationButton.setText("PTM Localization");
        ptmLocalizationButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ptmLocalizationButton.setIconTextGap(15);
        ptmLocalizationButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        ptmLocalizationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ptmLocalizationButtonActionPerformed(evt);
            }
        });

        proteinInferenceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        proteinInferenceButton.setText("Protein Inference");
        proteinInferenceButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        proteinInferenceButton.setIconTextGap(15);
        proteinInferenceButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        proteinInferenceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proteinInferenceButtonActionPerformed(evt);
            }
        });

        validationButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        validationButton.setText("Validation Levels");
        validationButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        validationButton.setIconTextGap(15);
        validationButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        validationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validationButtonActionPerformed(evt);
            }
        });

        fractionsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        fractionsButton.setText("Fraction Analysis");
        fractionsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        fractionsButton.setIconTextGap(15);
        fractionsButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        fractionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fractionsButtonActionPerformed(evt);
            }
        });

        qualityControlButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit_gray.png"))); // NOI18N
        qualityControlButton.setText("Quality Control");
        qualityControlButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        qualityControlButton.setIconTextGap(15);
        qualityControlButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        qualityControlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qualityControlButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spectrumMatchingButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fractionsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(validationButton, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(proteinInferenceButton, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(ptmLocalizationButton, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(psmScoringButton, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(matchesFiltersButton, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(geneMappingButton, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(sequenceMatchingButton, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(qualityControlButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addComponent(spectrumAnnotationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spectrumMatchingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(spectrumAnnotationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(sequenceMatchingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(geneMappingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(matchesFiltersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(psmScoringButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(ptmLocalizationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(proteinInferenceButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(validationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(fractionsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(qualityControlButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(attributesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(attributesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Open the AnnotationSettingsDialog.
     *
     * @param evt
     */
    private void spectrumAnnotationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spectrumAnnotationButtonActionPerformed
        ArrayList<NeutralLoss> neutralLosses = IonFactory.getNeutralLosses(searchParameters.getPtmSettings());
        ArrayList<Integer> reporterIons = new ArrayList<Integer>(IonFactory.getReporterIons(searchParameters.getPtmSettings()));
        AnnotationSettingsDialog annotationSettingsDialog = new AnnotationSettingsDialog(this, parentFrame, annotationSettings, neutralLosses, reporterIons, editable);
        if (!annotationSettingsDialog.isCanceled()) {
            annotationSettings = annotationSettingsDialog.getAnnotationSettings();
        }
    }//GEN-LAST:event_spectrumAnnotationButtonActionPerformed

    /**
     * Open the SearchSettingsDialog.
     *
     * @param evt
     */
    private void spectrumMatchingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spectrumMatchingButtonActionPerformed
        String name = nameTxt.getText();
        SearchSettingsDialog searchSettingsDialog = new SearchSettingsDialog(this, parentFrame, searchParameters,
                normalIcon, waitingIcon, editable, editable, configurationFile, lastSelectedFolder, name, editable);
        if (!searchSettingsDialog.isCanceled()) {
            PtmSettings oldPtms = searchParameters.getPtmSettings();
            searchParameters = searchSettingsDialog.getSearchParameters();
            PtmSettings newPtms = searchParameters.getPtmSettings();
            if (!oldPtms.equals(newPtms)) {
                selectedPtmsChanged();
            }
        }
    }//GEN-LAST:event_spectrumMatchingButtonActionPerformed

    /**
     * Open the SequenceMatchingSettingsDialog.
     *
     * @param evt
     */
    private void sequenceMatchingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sequenceMatchingButtonActionPerformed
        SequenceMatchingSettingsDialog sequenceMatchingSettingsDialog = new SequenceMatchingSettingsDialog(this, parentFrame, sequenceMatchingPreferences, editable);
        if (!sequenceMatchingSettingsDialog.isCanceled()) {
            sequenceMatchingPreferences = sequenceMatchingSettingsDialog.getSequenceMatchingPreferences();
        }
    }//GEN-LAST:event_sequenceMatchingButtonActionPerformed

    /**
     * Open the SpeciesDialog.
     *
     * @param evt
     */
    private void geneMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_geneMappingButtonActionPerformed
        SpeciesDialog speciesDialog = new SpeciesDialog(this, null, genePreferences, true, waitingIcon, normalIcon);
        // @TODO decouple the gene factory from the preferences
    }//GEN-LAST:event_geneMappingButtonActionPerformed

    /**
     * Open the MatchesImportFiltersDialog.
     *
     * @param evt
     */
    private void matchesFiltersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_matchesFiltersButtonActionPerformed
        MatchesImportFiltersDialog matchesImportFiltersDialog = new MatchesImportFiltersDialog(this, parentFrame, peptideAssumptionFilter, editable);
        if (!matchesImportFiltersDialog.isCanceled()) {
            peptideAssumptionFilter = matchesImportFiltersDialog.getFilter();
        }
    }//GEN-LAST:event_matchesFiltersButtonActionPerformed

    /**
     * Open the PsmScoringSettingsDialog.
     *
     * @param evt
     */
    private void psmScoringButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_psmScoringButtonActionPerformed
        PsmScoringSettingsDialog psmScoringSettingsDialog = new PsmScoringSettingsDialog(this, parentFrame, psmScoringPreferences, editable);
        if (!psmScoringSettingsDialog.isCanceled()) {
            psmScoringPreferences = psmScoringSettingsDialog.getPsmScoringPreferences();
        }
    }//GEN-LAST:event_psmScoringButtonActionPerformed

    /**
     * Open the PTMLocalizationParametersDialog.
     *
     * @param evt
     */
    private void ptmLocalizationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ptmLocalizationButtonActionPerformed
        PTMLocalizationParametersDialog ptmLocalizationParametersDialog = new PTMLocalizationParametersDialog(this, parentFrame, ptmScoringPreferences, editable);
        if (!ptmLocalizationParametersDialog.isCanceled()) {
            ptmScoringPreferences = ptmLocalizationParametersDialog.getPtmScoringPreferences();
        }
    }//GEN-LAST:event_ptmLocalizationButtonActionPerformed

    /**
     * Open the ProteinInferenceSettingsDialog.
     *
     * @param evt
     */
    private void proteinInferenceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proteinInferenceButtonActionPerformed
        ProteinInferenceSettingsDialog proteinInferenceSettingsDialog = new ProteinInferenceSettingsDialog(this, parentFrame, proteinInferencePreferences, normalIcon, waitingIcon, lastSelectedFolder, editable);
        if (!proteinInferenceSettingsDialog.isCanceled()) {
            proteinInferencePreferences = proteinInferenceSettingsDialog.getProteinInferencePreferences();
        }
    }//GEN-LAST:event_proteinInferenceButtonActionPerformed

    /**
     * Open the ValidationSettingsDialog.
     *
     * @param evt
     */
    private void validationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validationButtonActionPerformed
        ValidationSettingsDialog validationSettingsDialog = new ValidationSettingsDialog(this, parentFrame, idValidationPreferences, editable);
        if (!validationSettingsDialog.isCanceled()) {
            ValidationQCPreferences validationQCPreferences = idValidationPreferences.getValidationQCPreferences();
            idValidationPreferences = validationSettingsDialog.getIdMatchValidationPreferences();
            idValidationPreferences.setValidationQCPreferences(validationQCPreferences);
        }
    }//GEN-LAST:event_validationButtonActionPerformed

    /**
     * Open the ValidationQCPreferencesDialog.
     *
     * @param evt
     */
    private void qualityControlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qualityControlButtonActionPerformed
        ValidationQCPreferences validationQCPreferences = idValidationPreferences.getValidationQCPreferences();
        ValidationQCPreferencesDialog validationQCPreferencesDialog = new ValidationQCPreferencesDialog(this, parentFrame, validationQCPreferencesDialogParent, validationQCPreferences, editable && validationQCPreferencesDialogParent != null);
        if (!validationQCPreferencesDialog.isCanceled()) {
            idValidationPreferences.setValidationQCPreferences(validationQCPreferencesDialog.getValidationQCPreferences());
        }
    }//GEN-LAST:event_qualityControlButtonActionPerformed

    /**
     * Open the FractionSettingsDialog.
     *
     * @param evt
     */
    private void fractionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fractionsButtonActionPerformed
        FractionSettingsDialog fractionSettingsDialog = new FractionSettingsDialog(this, parentFrame, fractionSettings, editable);
        if (!fractionSettingsDialog.isCanceled()) {
            fractionSettings = fractionSettingsDialog.getFractionSettings();
        }
    }//GEN-LAST:event_fractionsButtonActionPerformed

    /**
     * Close the dialog.
     *
     * @param evt
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (validateInput()) {
            dispose();
        }
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Cancel the dialog.
     *
     * @param evt
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        canceled = true;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel attributesPanel;
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton fractionsButton;
    private javax.swing.JButton geneMappingButton;
    private javax.swing.JButton matchesFiltersButton;
    private javax.swing.JTextField nameTxt;
    private javax.swing.JButton okButton;
    private javax.swing.JButton proteinInferenceButton;
    private javax.swing.JButton psmScoringButton;
    private javax.swing.JButton ptmLocalizationButton;
    private javax.swing.JButton qualityControlButton;
    private javax.swing.JButton sequenceMatchingButton;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JButton spectrumAnnotationButton;
    private javax.swing.JButton spectrumMatchingButton;
    private javax.swing.JButton validationButton;
    // End of variables declaration//GEN-END:variables

}
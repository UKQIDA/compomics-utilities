package com.compomics.util.experiment.filters.massspectrometry.spectrumfilters.filtercreation;

import com.compomics.util.experiment.filters.massspectrometry.SpectrumFilter;
import com.compomics.util.experiment.filters.massspectrometry.spectrumfilters.MzFilter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This dialog allows the creation of an MzFilter.
 *
 * @author Marc Vaudel
 */
public class MzDialog extends javax.swing.JDialog {

    /**
     * The created filter.
     */
    private SpectrumFilter spectrumFilter = null;

    /**
     * Constructor.
     *
     * @param parent the parent frame
     * @param mzTolerance the default mzTolerance. Can be null.
     * @param intensityQuantile the default intensity quantile. Can be null.
     * @param isPpm  
     */
    public MzDialog(JFrame parent, Double mzTolerance, Double intensityQuantile, Boolean isPpm) {
        super(parent, true);
        initComponents();
        if (mzTolerance != null) {
            mzTolTxt.setText(mzTolerance + "");
        }
        if (intensityQuantile != null) {
            intTxt.setText(intensityQuantile + "");
        }
        if (isPpm != null) {
            if (!isPpm) {
                ppmCmb.setSelectedIndex(0);
            } else {
                ppmCmb.setSelectedIndex(1);
            }
        }
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * Validates the user input.
     *
     * @return a boolean indicating whether the user input can be used
     */
    public boolean validateInput() {
        try {
            new Double(mzTxt.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please verify the input for the m/z.",
                    "Wrong m/z", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            new Double(mzTolTxt.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please verify the input for the m/z tolerance.",
                    "Wrong m/z tolerance", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            new Double(intTxt.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please verify the input for the intensity quantile.",
                    "Wrong intensity quantile", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Returns the filter created. Null if none.
     *
     * @return the filter created. Null if none.
     */
    public SpectrumFilter getFilter() {
        return spectrumFilter;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ppmCmb = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        intTxt = new javax.swing.JTextField();
        mzTolTxt = new javax.swing.JTextField();
        mzTxt = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jLabel1.setText("m/z:");

        jLabel2.setText("m/z Accuracy:");

        ppmCmb.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Da", "ppm" }));

        jLabel3.setText("Intensity Quantile:");

        jLabel4.setText("%");

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

        intTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        intTxt.setText("0");

        mzTolTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mzTolTxt.setText("0.01");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(intTxt)
                            .addComponent(mzTolTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .addComponent(mzTxt))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ppmCmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel4)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(mzTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ppmCmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mzTolTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(intTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog without saving.
     * 
     * @param evt 
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Saves and then closes the dialog.
     * 
     * @param evt 
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if (validateInput()) {
            Double mz = new Double(mzTxt.getText());
            Double mzTol = new Double(mzTolTxt.getText());
            Double intQuantile = new Double(intTxt.getText());
            spectrumFilter = new MzFilter(mz, mzTol, ppmCmb.getSelectedIndex() == 1, intQuantile);
            String name = "m/z (" + mz + ")";
            spectrumFilter.setName(name);
            dispose();
        }
    }//GEN-LAST:event_okButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField intTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField mzTolTxt;
    private javax.swing.JTextField mzTxt;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox ppmCmb;
    // End of variables declaration//GEN-END:variables
}

package com.compomics.util.experiment.io.massspectrometry.proteowizard.gui;

import com.compomics.util.examples.BareBonesBrowserLaunch;
import com.compomics.util.experiment.io.massspectrometry.proteowizard.MsConvertParameters;
import com.compomics.util.experiment.io.massspectrometry.proteowizard.MsFormat;
import com.compomics.util.experiment.io.massspectrometry.proteowizard.ProteoWizardFilter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 * Dialog for the creation and edition of msconvert parameters.
 *
 * @author Marc Vaudel
 */
public class MsConvertParametersDialog extends javax.swing.JDialog {

    /**
     * Boolean indicating whether the editing was canceled.
     */
    private boolean canceled = false;
    /**
     * Map of the filters to use.
     */
    private HashMap<Integer, String> filters;
    /**
     * List of the indexes of the filters to use.
     */
    private ArrayList<Integer> filterIndexes;

    /**
     * Constructor.
     *
     * @param parent the parent frame
     * @param msConvertParameters initial parameters, ignored if null
     */
    public MsConvertParametersDialog(java.awt.Frame parent, MsConvertParameters msConvertParameters) {
        super(parent, true);
        initComponents();
        setUpGUI(msConvertParameters);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * Sets up the GUI components.
     * 
     * @param msConvertParameters parameters to display on the interface, ignored if null
     */
    private void setUpGUI(MsConvertParameters msConvertParameters) {
        filtersTableScrollPane.getViewport().setOpaque(false);
        if (msConvertParameters != null) {
            outputFormatCmb.setSelectedItem(msConvertParameters.getMsFormat());
            filters = (HashMap<Integer, String>) msConvertParameters.getFiltersMap().clone();
        } else {
            outputFormatCmb.setSelectedItem(MsFormat.mzML);
            filters = new HashMap<Integer, String>(2);
        }

        DefaultTableModel tableModel = new FiltersTableModel();
        filtersTable.setModel(tableModel);
        TableColumnModel tableColumnModel = filtersTable.getColumnModel();
        tableColumnModel.getColumn(0).setMaxWidth(50);

    }

    private void updateTable() {
        filterIndexes = new ArrayList<Integer>(filters.keySet());
        Collections.sort(filterIndexes);
        ((DefaultTableModel) filtersTable.getModel()).fireTableDataChanged();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filtersPopupMenu = new javax.swing.JPopupMenu();
        addItemMenuItem = new javax.swing.JMenuItem();
        removeItemMenuItem = new javax.swing.JMenuItem();
        popupSeparator = new javax.swing.JPopupMenu.Separator();
        helpMenuItem = new javax.swing.JMenuItem();
        backgourdPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        MsconvertParameters = new javax.swing.JPanel();
        filtersTableScrollPane = new javax.swing.JScrollPane();
        filtersTable = new javax.swing.JTable();
        outputFormatLbl = new javax.swing.JLabel();
        outputFormatCmb = new javax.swing.JComboBox();
        filtersLbl = new javax.swing.JLabel();

        addItemMenuItem.setText("Add Item");
        addItemMenuItem.setToolTipText("Add a new filter item");
        addItemMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addItemMenuItemActionPerformed(evt);
            }
        });
        filtersPopupMenu.add(addItemMenuItem);

        removeItemMenuItem.setText("Remove Item");
        removeItemMenuItem.setToolTipText("Removes the filter item");
        removeItemMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                removeItemMenuItemMouseReleased(evt);
            }
        });
        filtersPopupMenu.add(removeItemMenuItem);
        filtersPopupMenu.add(popupSeparator);

        helpMenuItem.setText("jMenuItem1");
        helpMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                helpMenuItemMouseReleased(evt);
            }
        });
        filtersPopupMenu.add(helpMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        backgourdPanel.setBackground(new java.awt.Color(230, 230, 230));

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

        MsconvertParameters.setBorder(javax.swing.BorderFactory.createTitledBorder("msconvert Parameters"));
        MsconvertParameters.setOpaque(false);

        filtersTableScrollPane.setOpaque(false);

        filtersTable.setModel(new FiltersTableModel());
        filtersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                filtersTableMouseReleased(evt);
            }
        });
        filtersTableScrollPane.setViewportView(filtersTable);

        outputFormatLbl.setText("File conversion output format:");

        outputFormatCmb.setModel(new DefaultComboBoxModel(MsFormat.values()));

        filtersLbl.setText("File processing options:");

        javax.swing.GroupLayout MsconvertParametersLayout = new javax.swing.GroupLayout(MsconvertParameters);
        MsconvertParameters.setLayout(MsconvertParametersLayout);
        MsconvertParametersLayout.setHorizontalGroup(
            MsconvertParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MsconvertParametersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MsconvertParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filtersTableScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 923, Short.MAX_VALUE)
                    .addGroup(MsconvertParametersLayout.createSequentialGroup()
                        .addGroup(MsconvertParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MsconvertParametersLayout.createSequentialGroup()
                                .addComponent(outputFormatLbl)
                                .addGap(18, 18, 18)
                                .addComponent(outputFormatCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(filtersLbl))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        MsconvertParametersLayout.setVerticalGroup(
            MsconvertParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MsconvertParametersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MsconvertParametersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outputFormatLbl)
                    .addComponent(outputFormatCmb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(filtersLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filtersTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout backgourdPanelLayout = new javax.swing.GroupLayout(backgourdPanel);
        backgourdPanel.setLayout(backgourdPanelLayout);
        backgourdPanelLayout.setHorizontalGroup(
            backgourdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgourdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgourdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(backgourdPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(MsconvertParameters, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        backgourdPanelLayout.setVerticalGroup(
            backgourdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgourdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MsconvertParameters, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(backgourdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgourdPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgourdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        canceled = true;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void addItemMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addItemMenuItemActionPerformed

        ProteoWizardFilter[] filterItems = ProteoWizardFilter.values();
        for (ProteoWizardFilter filter : filterItems) {
            if (!filters.containsKey(filter.number)) {
                filters.put(filter.number, "");
                updateTable();
                break;
            }
        }
    }//GEN-LAST:event_addItemMenuItemActionPerformed

    private void removeItemMenuItemMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_removeItemMenuItemMouseReleased
        int row = filtersTable.getSelectedRow();
        if (row >= 0) {
            String itemName = filtersTable.getValueAt(row, 1).toString();
            ProteoWizardFilter proteoWizardFilter = ProteoWizardFilter.getFilter(itemName);
            if (proteoWizardFilter != null) {
                filters.remove(proteoWizardFilter.number);
                updateTable();
            }
        }
    }//GEN-LAST:event_removeItemMenuItemMouseReleased

    private void filtersTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_filtersTableMouseReleased
        
        if (evt != null && filtersTable.rowAtPoint(evt.getPoint()) != -1) {
            int row = filtersTable.rowAtPoint(evt.getPoint());
            filtersTable.setRowSelectionInterval(row, row);
        }
        if (evt != null && evt.getButton() == MouseEvent.BUTTON3) {
            filtersPopupMenu.show(filtersTable, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_filtersTableMouseReleased

    private void helpMenuItemMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpMenuItemMouseReleased
        BareBonesBrowserLaunch.openURL("http://proteowizard.sourceforge.net/tools/filters.html");
    }//GEN-LAST:event_helpMenuItemMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel MsconvertParameters;
    private javax.swing.JMenuItem addItemMenuItem;
    private javax.swing.JPanel backgourdPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel filtersLbl;
    private javax.swing.JPopupMenu filtersPopupMenu;
    private javax.swing.JTable filtersTable;
    private javax.swing.JScrollPane filtersTableScrollPane;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox outputFormatCmb;
    private javax.swing.JLabel outputFormatLbl;
    private javax.swing.JPopupMenu.Separator popupSeparator;
    private javax.swing.JMenuItem removeItemMenuItem;
    // End of variables declaration//GEN-END:variables

    /**
     * Indicates whether the editing was canceled by the user.
     *
     * @return a boolean indicating whether the editing was canceled by the user
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Returns the parameters as created by the user.
     *
     * @return the parameters as created by the user
     */
    public MsConvertParameters getMsConvertParameters() {
        MsConvertParameters msConvertParameters = new MsConvertParameters();
        msConvertParameters.setMsFormat((MsFormat) outputFormatCmb.getSelectedItem());
        for (Integer filterIndex : filters.keySet()) {
            msConvertParameters.addFilter(filterIndex, filters.get(filterIndex));
        }
        return msConvertParameters;
    }

    /**
     * Table model for the filters.
     */
    private class FiltersTableModel extends DefaultTableModel {

        public FiltersTableModel() {
        }

        @Override
        public int getRowCount() {
            if (filters == null) {
                return 0;
            }
            return filters.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return " ";
                case 1:
                    return "Name";
                case 2:
                    return "Value";
                default:
                    return "";
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            switch (column) {
                case 0:
                    return row + 1;
                case 1:
                    Integer index = filterIndexes.get(row);
                    ProteoWizardFilter proteoWizardFilter = ProteoWizardFilter.getFilter(index);
                    String itemName = proteoWizardFilter.name;
                    return itemName;
                case 2:
                    index = filterIndexes.get(row);
                    String value = filters.get(index);
                    if (value == null) {
                        value = null;
                    }
                    return value;
                default:
                    return "";
            }
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            switch (column) {
                case 1:
                    Integer index = filterIndexes.get(row);
                    filters.remove(index);
                    ProteoWizardFilter proteoWizardFilter = (ProteoWizardFilter) value;
                    filters.put(proteoWizardFilter.number, "");
                    break;
                case 2:
                    index = filterIndexes.get(row);
                    filters.put(index, (String) value);
                    break;
            }
            updateTable();
        }

        @Override
        public Class getColumnClass(int columnIndex) {
            for (int i = 0; i < getRowCount(); i++) {
                if (getValueAt(i, columnIndex) != null) {
                    return getValueAt(i, columnIndex).getClass();
                }
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column > 0;
        }
    }
}

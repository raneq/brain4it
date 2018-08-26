/*
 * Brain4it
 * 
 * Copyright (C) 2018, Ajuntament de Sant Feliu de Llobregat
 * 
 * This program is licensed and may be used, modified and redistributed under 
 * the terms of the European Public License (EUPL), either version 1.1 or (at 
 * your option) any later version as soon as they are approved by the European 
 * Commission.
 * 
 * Alternatively, you may redistribute and/or modify this program under the 
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation; either  version 3 of the License, or (at your option) 
 * any later version. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *   
 * See the licenses for the specific language governing permissions, limitations 
 * and more details.
 *   
 * You should have received a copy of the EUPL1.1 and the LGPLv3 licenses along 
 * with this program; if not, you may find them at: 
 *   
 *   https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *   http://www.gnu.org/licenses/ 
 *   and 
 *   https://www.gnu.org/licenses/lgpl.txt
 */
package org.brain4it.manager.swing;

import java.awt.Dimension;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import org.brain4it.manager.swing.text.TextUtils;

/**
 *
 * @author realor
 */
public class ImportModuleDialog extends javax.swing.JDialog
{
  protected ManagerApp managerApp;
  protected String url;

  /**
   * Creates new form ImportDialog
   */
  public ImportModuleDialog(ManagerApp managerApp, boolean modal)
  {
    super(managerApp, modal);
    this.managerApp = managerApp;
    initComponents();
    TextUtils.updateInputMap(urlTextField);
    Dimension dim = urlTextField.getPreferredSize();
    urlTextField.setPreferredSize(new Dimension(dim.height * 15, dim.height));
    urlTextField.getDocument().addDocumentListener(new DocumentListener()
    {
      @Override
      public void insertUpdate(DocumentEvent e)
      {
        updateImportButton();
      }

      @Override
      public void removeUpdate(DocumentEvent e)
      {
        updateImportButton();
      }

      @Override
      public void changedUpdate(DocumentEvent e)
      {
        updateImportButton();
      }
    });
  }

  public String getURL()
  {
    return url;
  }
  
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents()
  {
    java.awt.GridBagConstraints gridBagConstraints;

    centerPanel = new javax.swing.JPanel();
    urlLabel = new javax.swing.JLabel();
    urlTextField = new javax.swing.JTextField();
    toolBar = new javax.swing.JToolBar();
    exploreButton = new javax.swing.JButton();
    southPanel = new javax.swing.JPanel();
    importButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/brain4it/manager/swing/resources/Manager"); // NOI18N
    setTitle(bundle.getString("ImportModule")); // NOI18N

    centerPanel.setLayout(new java.awt.GridBagLayout());

    urlLabel.setText("URL:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    centerPanel.add(urlLabel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    centerPanel.add(urlTextField, gridBagConstraints);

    toolBar.setBorder(null);
    toolBar.setFloatable(false);
    toolBar.setRollover(true);
    toolBar.setBorderPainted(false);

    exploreButton.setIcon(IconCache.getIcon("folder"));
    exploreButton.setText(bundle.getString("Explore")); // NOI18N
    exploreButton.setFocusable(false);
    exploreButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        exploreButtonActionPerformed(evt);
      }
    });
    toolBar.add(exploreButton);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    centerPanel.add(toolBar, gridBagConstraints);

    getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

    southPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

    importButton.setText(bundle.getString("Import")); // NOI18N
    importButton.setEnabled(false);
    importButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        importButtonActionPerformed(evt);
      }
    });
    southPanel.add(importButton);

    cancelButton.setText(bundle.getString("Cancel")); // NOI18N
    cancelButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        cancelButtonActionPerformed(evt);
      }
    });
    southPanel.add(cancelButton);

    getContentPane().add(southPanel, java.awt.BorderLayout.PAGE_END);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void exploreButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exploreButtonActionPerformed
  {//GEN-HEADEREND:event_exploreButtonActionPerformed
    JFileChooser fileChooser = new JFileChooser();
    FileFilter[] filters = fileChooser.getChoosableFileFilters();
    if (filters.length > 0)
    {
      fileChooser.removeChoosableFileFilter(filters[0]);
    }
    fileChooser.addChoosableFileFilter(new FileFilter()
    {
      @Override
      public boolean accept(File f)
      {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".snp");
      }

      @Override
      public String getDescription()
      {
        return "Brain4it snapshot (*.snp)";
      }
    });
    if (filters.length > 0)
    {
      fileChooser.addChoosableFileFilter(filters[0]);
    }
    fileChooser.setDialogTitle(managerApp.getLocalizedMessage("ImportModule"));
    int result = fileChooser.showDialog(
      this, managerApp.getLocalizedMessage("Select"));
    if (result == JFileChooser.APPROVE_OPTION)
    {
      try
      {
        File file = fileChooser.getSelectedFile();
        urlTextField.setText(file.toURI().toURL().toString());
      }
      catch (Exception ex)
      {
      }
    }
  }//GEN-LAST:event_exploreButtonActionPerformed

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
  {//GEN-HEADEREND:event_cancelButtonActionPerformed
    dispose();
  }//GEN-LAST:event_cancelButtonActionPerformed

  private void importButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_importButtonActionPerformed
  {//GEN-HEADEREND:event_importButtonActionPerformed
    url = urlTextField.getText();
    dispose();
  }//GEN-LAST:event_importButtonActionPerformed

  private void updateImportButton()
  {
    String text = urlTextField.getText();
    if (text != null && text.trim().length() > 4)
    {
      importButton.setEnabled(true);
    }
    else
    {
      importButton.setEnabled(false);      
    }
  }
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cancelButton;
  private javax.swing.JPanel centerPanel;
  private javax.swing.JButton exploreButton;
  private javax.swing.JButton importButton;
  private javax.swing.JPanel southPanel;
  private javax.swing.JToolBar toolBar;
  private javax.swing.JLabel urlLabel;
  private javax.swing.JTextField urlTextField;
  // End of variables declaration//GEN-END:variables
}

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

package org.brain4it.manager.swing.splitter;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import org.brain4it.manager.swing.IconCache;

/**
 *
 * @author realor
 */
public class TabComponent extends javax.swing.JPanel
{
  private static final int MAX_TAB_NAME_LENGTH = 20;
  private final Splitter splitter;
  private final Component component;
  
  /**
   * Creates new form TabPanel
   * @param splitter the parent splitter
   * @param component the associated component
   * @param title the tab title
   * @param icon the tab icon
   */
  public TabComponent(Splitter splitter, Component component,
    String title, Icon icon)
  {
    initComponents();
    this.splitter = splitter;
    this.component = component;

    label.setIcon(icon);
    label.setText(shorten(title));
    label.setToolTipText(title);
    label.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mousePressed(MouseEvent e)
      {
        Component component = TabComponent.this.component;
        TabContainer tabContainer = (TabContainer)component.getParent();
        tabContainer.setSelectedComponent(component);
        getSplitter().setActiveTabContainer(tabContainer);
      }
    });
    closeButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent event)
      {
        getSplitter().removeComponent(TabComponent.this.component);
      }
    });
  }
  
  public Splitter getSplitter()
  {
    return splitter;
  }
  
  public Component getComponent()
  {
    return component;
  }
  
  public void setTitle(String title)
  {
    label.setText(shorten(title));
    label.setToolTipText(title);
  }
  
  public String getTitle()
  {
    return label.getText();
  }
  
  public void setIcon(Icon icon)
  {
    label.setIcon(icon);
  }
  
  public Icon getIcon()
  {
    return label.getIcon();
  }
  
  public void setModified(boolean modified)
  {
    if (modified)
    {
      label.setFont(label.getFont().deriveFont(Font.BOLD));
    }
    else
    {
      label.setFont(label.getFont().deriveFont(Font.PLAIN));      
    }
  }
  
  private String shorten(String title)
  {
    int length = title.length();
    if (length > MAX_TAB_NAME_LENGTH)
    {
      title = "..." + title.substring(length - MAX_TAB_NAME_LENGTH);
    }
    return title;
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

    label = new javax.swing.JLabel();
    closeButton = new javax.swing.JButton();

    setOpaque(false);
    setLayout(new java.awt.BorderLayout());

    label.setText("tabName");
    label.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 4));
    add(label, java.awt.BorderLayout.CENTER);

    closeButton.setIcon(IconCache.getIcon("close_tab"));
    closeButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    closeButton.setBorderPainted(false);
    closeButton.setContentAreaFilled(false);
    closeButton.setIconTextGap(0);
    closeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
    closeButton.setSelectedIcon(IconCache.getIcon("onclose_tab"));
    add(closeButton, java.awt.BorderLayout.LINE_END);
  }// </editor-fold>//GEN-END:initComponents


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton closeButton;
  private javax.swing.JLabel label;
  // End of variables declaration//GEN-END:variables
}

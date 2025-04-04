/*
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2022 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 * 4. The University of California, San Diego, La Jolla, CA, USA
 * 5. The Babraham Institute, Cambridge, UK
 * 6. Marquette University, Milwaukee, WI, USA
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.celldesigner;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import jp.sbi.celldesigner.plugin.PluginMenu;
import jp.sbi.celldesigner.plugin.PluginMenuItem;


/**
 * A simple plugin for CellDesigner that visualizes and exports SBML files.
 * 
 * @author Ibrahim Vazirabad
 * @since 1.0
 */
public class SBMLExportPlugin extends AbstractCellDesignerPlugin  {

  /**
   * 
   */
  public static final String ACTION = "Display SBML Representation";
  /**
   * 
   */
  public static final String APPLICATION_NAME = "SBML Export";

  /**
   * Creates a new CellDesigner plug-in with an entry in the menu bar.
   */
  public SBMLExportPlugin() {
    super();
    addPluginMenu();
  }

  /* (non-Javadoc)
   * @see jp.sbi.celldesigner.plugin.CellDesignerPlug#addPluginMenu()
   */
  @Override
  public void addPluginMenu() {
    // Initializing CellDesigner's menu entries
    PluginMenu menu = new PluginMenu(APPLICATION_NAME);
    PluginMenuItem menuItem = new PluginMenuItem(
      ACTION, new SBMLExportPluginAction(this));
    menuItem.setToolTipText("Displays the raw SBML representation of this model");
    menu.add(menuItem);
    addCellDesignerPluginMenu(menu);
  }

  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  /**
   * Initializes plugin and sets WindowClosed() events.
   */
  @Override
  public void run() {
    SBMLLayoutVisualizer SBMLVisualizer = null;
    try {
      SBMLVisualizer = new SBMLLayoutVisualizer(getSBMLDocument());
      SBMLVisualizer.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent event) {
          getReader().clearMap();
          setStarted(false);
        }
      });
    }
    catch (Throwable e) {
      new GUIErrorConsole(e);
    }
  }
}

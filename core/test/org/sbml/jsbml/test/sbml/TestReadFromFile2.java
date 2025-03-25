/*
 *
 * @file    TestReadFromFile1.java
 * @brief   Reads tests/l1v1-branch.xml into memory and tests it.
 *
 * This test file was converted from libsbml http://sbml.org/software/libsbml
 *
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
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.test.sbml;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.xml.stax.SBMLReader;

/**
 * 
 * @author  Nicolas Rodriguez
 * @author  Akiya Jouraku
 * @author  Ben Bornstein
 * @since 0.8
 */
public class TestReadFromFile2 {

  /**
   * 
   * @throws XMLStreamException
   * @throws ClassNotFoundException
   * @throws IOException
   * @throws InvalidPropertiesFormatException
   */
  @SuppressWarnings("deprecation")
  @Test
  public void test_read_bad_annotation() throws XMLStreamException, InvalidPropertiesFormatException, IOException, ClassNotFoundException {
    SBMLDocument d;
    Model m;
    InputStream fileStream = TestReadFromFile1.class.getResourceAsStream("/org/sbml/jsbml/xml/test/data/l2v1/Ota2015_GDI-integrated.xml");

    SBMLReader sBMLReader = new SBMLReader();
    // the xml document is malformed, some application specific annotation is not being parsed properly
    // SBMLReader used to crash with a class cast exception
    // this test verifies the workaround, which will skip any XMLNode entity that is not an instance of Annotation class
    // the workaround will also log a warning: logger.warn("lastElement cannot be cast to Annotation, skipping...");
    d = sBMLReader.readSBMLFromStream(fileStream);
    assertTrue(d != null);
    m = d.getModel();
    assertTrue(m != null);
    assertTrue(m.getName().startsWith("Ota2015"));
    d = null;
  }
}

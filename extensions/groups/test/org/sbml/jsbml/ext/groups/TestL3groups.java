/*
 *
 * @file    TestL3Group.java
 * @brief   L3 Groups package unit tests
 *
 * @author  Nicolas Rodriguez (JSBML conversion)
 * @author  Akiya Jouraku (Java conversion)
 * @author  Sarah Keating 
 *
 * This test file was converted from libsbml http://sbml.org/software/libsbml
 *
 * $Id$
 * $URL$
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2011 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */

package org.sbml.jsbml.ext.groups;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

import javax.xml.stream.XMLStreamException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.ext.groups.Group;
import org.sbml.jsbml.ext.groups.Member;
import org.sbml.jsbml.ext.groups.ModelGroupExtension;
import org.sbml.jsbml.xml.stax.SBMLReader;
import org.sbml.jsbml.xml.stax.SBMLWriter;
import org.xml.sax.SAXException;

/**
 * @author Nicolas Rodriguez
 * @since 0.8
 * @version $Rev$
 *
 */
public class TestL3groups {

	public static String DATA_FOLDER = null;
	public static String GROUPS_NAMESPACE = "http://www.sbml.org/sbml/level3/version1/groups/version1";

	static {

		if (DATA_FOLDER == null) {
			DATA_FOLDER = System.getenv("DATA_FOLDER");
		}
		if (DATA_FOLDER == null) {
			DATA_FOLDER = System.getProperty("DATA_FOLDER");
		}

	}

	public boolean isNaN(double x) {
		return Double.isNaN(x);
	}

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 * @throws XMLStreamException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InvalidPropertiesFormatException
	 */
	@Test
	public void test_L3_Groups_read1() throws XMLStreamException,
			InvalidPropertiesFormatException, IOException,
			ClassNotFoundException {
		String fileName = DATA_FOLDER + "/groups/groups1.xml";

		SBMLDocument doc = new SBMLReader().readSBMLFile(fileName);
		Model model = doc.getModel();

		System.out.println("Model extension objects : "
				+ model.getExtension(GROUPS_NAMESPACE));
		ModelGroupExtension extendedModel = (ModelGroupExtension) model
				.getExtension(GROUPS_NAMESPACE);

		System.out.println("Nb Groups = "
				+ extendedModel.getListOfGroups().size());

		Group group = extendedModel.getGroup(0);

		System.out.println("Group sboTerm, id = " + group.getSBOTermID() + ", "
				+ group.getId());
		System.out.println("Nb Members = " + group.getListOfMembers().size());

		Member member = group.getMember(0);

		System.out.println("Member(0).symbol = " + member.getSymbol());

	}

	/**
	 * 
	 * @throws XMLStreamException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws InvalidPropertiesFormatException
	 * @throws SBMLException
	 * @throws SAXException 
	 */
	@Test
	public void test_L3_Groups_write1() throws XMLStreamException,
			InstantiationException, IllegalAccessException,
			InvalidPropertiesFormatException, IOException,
			ClassNotFoundException, SBMLException, SAXException {
		String fileName = DATA_FOLDER + "/groups/groups1.xml";

		SBMLDocument doc = new SBMLReader().readSBMLFile(fileName);

		new SBMLWriter().write(doc, DATA_FOLDER + "/groups/groups1_write.xml");
	}
}

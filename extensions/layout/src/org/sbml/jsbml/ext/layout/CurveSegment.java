/*
 * $Id$
 * $URL$
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2012 jointly by the following organizations:
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
package org.sbml.jsbml.ext.layout;

import java.util.Map;

import org.apache.log4j.Logger;
import org.sbml.jsbml.util.TreeNodeChangeEvent;

/**
 * @author Sebastian Fr&oum;hlich
 * @since 1.0
 * @version $Rev$
 */
public class CurveSegment extends CubicBezier {

	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = -5085246314333062152L;

	/**
	 * 
	 */
	private Logger logger = Logger.getLogger(CurveSegment.class);

	/**
	 * 
	 */
	private String type;

	/**
	 * 
	 */
	public CurveSegment() {
	  super();
	  initDefaults();
	}

  /**
	 * 
	 * @param curveSegment
	 */
	public CurveSegment(CurveSegment curveSegment) {
		super(curveSegment);
		if (curveSegment.isSetStart()) {
			this.start = curveSegment.getStart().clone();
		}
		if (curveSegment.isSetEnd()) {
			this.end = curveSegment.getEnd().clone();
		}
		if (curveSegment.isSetType()) {
			this.type = curveSegment.getType();
		}
		// Note: basePoints 1 & 2 are already copied in super.
	}

	/**
	 * 
	 * @param level
	 * @param version
	 */
	public CurveSegment(int level, int version) {
    super(level, version);
    initDefaults();
  }

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.AbstractSBase#clone()
	 */
	@Override
	public CurveSegment clone() {
		return new CurveSegment(this);
	}

  /* (non-Javadoc)
   * @see org.sbml.jsbml.AbstractNamedSBase#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object object) {
    boolean equals = super.equals(object);
    if (equals) {
      CurveSegment curveSegment = (CurveSegment) object;
      equals &= curveSegment.isSetType() == isSetType();
      if (equals && isSetType()) {
        equals &= curveSegment.getType().equals(getType());
      }
    }
    return equals;
  }

	/**
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

  /* (non-Javadoc)
   * @see org.sbml.jsbml.AbstractSBase#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 937;
    int hashCode = super.hashCode();
    if (isSetType()) {
      hashCode += prime * getType().hashCode();
    }
    return hashCode;
  }


	/**
	 * 
	 */
	private void initDefaults() {
	  addNamespace(LayoutConstants.namespaceURI);
  }

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.NamedSBase#isIdMandatory()
	 */
	@Override
	public boolean isIdMandatory() {
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSetType() {
		return type != null;
	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.AbstractMathContainer#readAttribute(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean readAttribute(String attributeName, String prefix,
			String value) {
		boolean isAttributeRead = super.readAttribute(attributeName, prefix,
				value);
		logger.debug("reading CurveSegment: " + prefix + " : " + attributeName);

		if (!isAttributeRead) {
			isAttributeRead = true;
			if ((prefix.equals("xsi") || prefix.equals(""))
					&& attributeName.equals("type")) {
				setType(value);
				return true;
			}
		}
		return isAttributeRead;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		String oldType = this.type;
		this.type = type;
		firePropertyChange(TreeNodeChangeEvent.type, oldType, this.type);
	}

	/* (non-Javadoc)
	 * @see org.sbml.jsbml.AbstractNamedSBase#writeXMLAttributes()
	 */
	@Override
	public Map<String, String> writeXMLAttributes() {
		Map<String, String> attributes = super.writeXMLAttributes();
		logger.debug("process attributes of CurveSegment");
		logger.debug("isSetType: " + isSetType());
		if (isSetType()) {
			attributes.put("xsi:type", getType());
		}
		return attributes;
	}

}

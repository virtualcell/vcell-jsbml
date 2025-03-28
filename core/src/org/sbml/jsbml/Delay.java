/*
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
package org.sbml.jsbml;

/**
 * Represents the delay subnode of an event element.
 * 
 * @author Andreas Dr&auml;ger
 * @since 0.8
 * 
 */
public class Delay extends AbstractMathContainer implements UniqueSId {

  /**
   * Generated serial version identifier.
   */
  private static final long serialVersionUID = -1578051749680028593L;

  /**
   * Creates a Delay instance.
   */
  public Delay() {
    super();
  }

  /**
   * Creates a {@link Delay} instance from an {@link ASTNode}, level and version.
   * 
   * @param math
   * @param level
   * @param version
   */
  public Delay(ASTNode math, int level, int version) {
    super(math, level, version);
  }

  /**
   * Creates a Delay instance from a given Delay.
   * 
   * @param sb
   */
  public Delay(Delay sb) {
    super(sb);
  }

  /**
   * Creates a Delay instance from a level and version.
   * 
   * @param level
   * @param version
   */
  public Delay(int level, int version) {
    super(level, version);
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.AbstractMathContainer#clone()
   */
  @Override
  public Delay clone() {
    return new Delay(this);
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.AbstractSBase#getParent()
   */
  @Override
  public Event getParent() {
    return (Event) super.getParent();
  }

}

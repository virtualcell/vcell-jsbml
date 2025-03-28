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
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation. A copy of the license agreement is provided
 * in the file named "LICENSE.txt" included with this software distribution
 * and also available online as <http://sbml.org/Software/JSBML/License>.
 * ----------------------------------------------------------------------------
 */
package org.sbml.jsbml.validator.offline.constraints;

import java.util.Set;

import org.sbml.jsbml.ext.render.LinearGradient;
import org.sbml.jsbml.ext.render.RenderConstants;
import org.sbml.jsbml.validator.SBMLValidator.CHECK_CATEGORY;
import org.sbml.jsbml.validator.offline.ValidationContext;
import org.sbml.jsbml.validator.offline.constraints.helper.UnknownCoreAttributeValidationFunction;
import org.sbml.jsbml.validator.offline.constraints.helper.UnknownCoreElementValidationFunction;
import org.sbml.jsbml.validator.offline.constraints.helper.UnknownPackageAttributeValidationFunction;

/**
 * Defines validation rules (as {@link ValidationFunction} instances) for the
 * {@link LinearGradient} class.
 * 
 * @author David Emanuel Vetter
 */
public class LinearGradientConstraints extends AbstractConstraintDeclaration {

  @Override
  public void addErrorCodesForCheck(Set<Integer> set, int level, int version,
    CHECK_CATEGORY category, ValidationContext context) {
    switch(category) {
    case GENERAL_CONSISTENCY:
      addRangeToSet(set, RENDER_21401, RENDER_21409);
      break;
    default:
      break;
    }
  }
  

  @Override
  public void addErrorCodesForAttribute(Set<Integer> set, int level,
    int version, String attributeName, ValidationContext context) {
    // TODO Auto-generated method stub
  }


  @Override
  public ValidationFunction<?> getValidationFunction(int errorCode,
    ValidationContext context) {
    ValidationFunction<LinearGradient> func; 
    switch(errorCode) {
    case RENDER_21401:
      func = new UnknownCoreAttributeValidationFunction<LinearGradient>();
      break;
    case RENDER_21402:
      func = new UnknownCoreElementValidationFunction<LinearGradient>();
      break;
    case RENDER_21403:
      func = new UnknownPackageAttributeValidationFunction<LinearGradient>(RenderConstants.shortLabel);
      break;
    case RENDER_21404:
      func = new ValidationFunction<LinearGradient>() {
        @Override
        public boolean check(ValidationContext ctx, LinearGradient gradient) {
          return !gradient.isSetX1() || gradient.getX1().isSetAbsoluteValue()
            || gradient.getX1().isSetRelativeValue();
        }
      }; 
      break;
    case RENDER_21405:
      func = new ValidationFunction<LinearGradient>() {
        @Override
        public boolean check(ValidationContext ctx, LinearGradient gradient) {
          return !gradient.isSetY1() || gradient.getY1().isSetAbsoluteValue()
            || gradient.getY1().isSetRelativeValue();
        }
      };
      break;
    case RENDER_21406:
      func = new ValidationFunction<LinearGradient>() {
        @Override
        public boolean check(ValidationContext ctx, LinearGradient gradient) {
          return !gradient.isSetZ1() || gradient.getZ1().isSetAbsoluteValue()
            || gradient.getZ1().isSetRelativeValue();
        }
      };
      break;
    case RENDER_21407:
      func = new ValidationFunction<LinearGradient>() {
        @Override
        public boolean check(ValidationContext ctx, LinearGradient gradient) {
          return !gradient.isSetX2() || gradient.getX2().isSetAbsoluteValue()
            || gradient.getX2().isSetRelativeValue();
        }
      }; 
      break;
    case RENDER_21408:
      func = new ValidationFunction<LinearGradient>() {
        @Override
        public boolean check(ValidationContext ctx, LinearGradient gradient) {
          return !gradient.isSetY2() || gradient.getY2().isSetAbsoluteValue()
            || gradient.getY2().isSetRelativeValue();
        }
      };
      break;
    case RENDER_21409:
      func = new ValidationFunction<LinearGradient>() {
        @Override
        public boolean check(ValidationContext ctx, LinearGradient gradient) {
          return !gradient.isSetZ2() || gradient.getZ2().isSetAbsoluteValue()
            || gradient.getZ2().isSetRelativeValue();
        }
      };
      break;

    default:
      func = null;
      break;
    }
    return func;
  }
}

/*
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 * 
 * Copyright (C) 2009-2019 jointly by the following organizations:
 * 1. The University of Tuebingen, Germany
 * 2. EMBL European Bioinformatics Institute (EBML-EBI), Hinxton, UK
 * 3. The California Institute of Technology, Pasadena, CA, USA
 * 4. The Babraham Institute, Cambridge, UK
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

import org.sbml.jsbml.ext.spatial.Geometry;
import org.sbml.jsbml.ext.spatial.SpatialConstants;
import org.sbml.jsbml.ext.spatial.SpatialModelPlugin;
import org.sbml.jsbml.ext.spatial.SpatialSymbolReference;
import org.sbml.jsbml.validator.SBMLValidator.CHECK_CATEGORY;
import org.sbml.jsbml.validator.offline.ValidationContext;
import org.sbml.jsbml.validator.offline.constraints.helper.UnknownCoreAttributeValidationFunction;
import org.sbml.jsbml.validator.offline.constraints.helper.UnknownCoreElementValidationFunction;
import org.sbml.jsbml.validator.offline.constraints.helper.UnknownPackageAttributeValidationFunction;

/**
 * Defines validation rules (as {@link ValidationFunction} instances) for the {@link SpatialSymbolReference} class.
 * 
 * @author Bhavye Jain
 * @since 1.5
 */
public class SpatialSymbolReferenceConstraints extends AbstractConstraintDeclaration {

  /* (non-Javadoc)
   * @see org.sbml.jsbml.validator.offline.constraints.ConstraintDeclaration#addErrorCodesForAttribute(java.util.Set, int, int, java.lang.String)
   */
  @Override
  public void addErrorCodesForAttribute(Set<Integer> set, int level,
    int version, String attributeName, ValidationContext context) 
  {
    // TODO 

  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.validator.offline.constraints.ConstraintDeclaration#addErrorCodesForCheck(java.util.Set, int, int, org.sbml.jsbml.validator.SBMLValidator.CHECK_CATEGORY)
   */
  @Override
  public void addErrorCodesForCheck(Set<Integer> set, int level, int version,
    CHECK_CATEGORY category, ValidationContext context) {
    switch (category) {
    case GENERAL_CONSISTENCY:
      if(level >= 3){		   
        addRangeToSet(set, SPATIAL_23301, SPATIAL_23304);
      }
      break;
    case IDENTIFIER_CONSISTENCY:
      break;
    case MATHML_CONSISTENCY:
      break;
    case MODELING_PRACTICE:
      break;
    case OVERDETERMINED_MODEL:
      break;
    case SBO_CONSISTENCY:
      break;
    case UNITS_CONSISTENCY:
      break;
    }
  }

  @Override
  public ValidationFunction<?> getValidationFunction(int errorCode, ValidationContext context){
    ValidationFunction<SpatialSymbolReference> func = null;

    switch (errorCode) {

    case SPATIAL_23301:
    {
      // A SpatialSymbolReference object may have the optional SBML Level 3 Core attributes metaid 
      // and sboTerm. No other attributes from the SBML Level 3 Core namespaces are permitted on a 
      // SpatialSymbolReference.

      func = new UnknownCoreAttributeValidationFunction<SpatialSymbolReference>();
      break;
    }

    case SPATIAL_23302:
    {
      // A SpatialSymbolReference object may have the optional SBML Level 3 Core subobjects for 
      // notes and annotations. No other elements from the SBML Level 3 Core namespaces are permitted
      // on a SpatialSymbolReference.

      func = new UnknownCoreElementValidationFunction<SpatialSymbolReference>();
      break;
    }

    case SPATIAL_23303:
    {
      // A SpatialSymbolReference object must have the required attribute spatial:spatialRef. No 
      // other attributes from the SBML Level 3 Spatial Processes namespaces are permitted on a 
      // SpatialSymbolReference object.

      func = new UnknownPackageAttributeValidationFunction<SpatialSymbolReference>(SpatialConstants.shortLabel) {

        @Override
        public boolean check(ValidationContext ctx, SpatialSymbolReference ssr) {

          if(!ssr.isSetSpatialRef()) {
            return false;
          }
          return super.check(ctx, ssr);
        }
      };
      break;
    }

    case SPATIAL_23304:
    {
      // The value of the attribute spatial:spatialRef of a SpatialSymbolReference object must be 
      // the identifier of an existing Geometry object defined in the enclosing Model object.

      func = new ValidationFunction<SpatialSymbolReference>() {

        @Override
        public boolean check(ValidationContext ctx, SpatialSymbolReference ssr) {
          if(ssr.isSetSpatialRef()) {
            SpatialModelPlugin smp = (SpatialModelPlugin) ssr.getModel().getPlugin(SpatialConstants.shortLabel);
            if(smp.isSetGeometry()) {
              Geometry g = smp.getGeometry();	
              if(ssr.getSpatialRef().compareTo(g.getId()) == 0) {
                return true;
              }
            }		  					
            return false;
          }

          return true;
        }
      };
      break;
    }
    }

    return func;
  }

}
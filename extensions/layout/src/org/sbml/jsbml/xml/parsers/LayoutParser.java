/*
 * $Id$
 * $URL$
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2013 jointly by the following organizations:
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
package org.sbml.jsbml.xml.parsers;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.ext.SBasePlugin;
import org.sbml.jsbml.ext.layout.BasePoint1;
import org.sbml.jsbml.ext.layout.BasePoint2;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.CompartmentGlyph;
import org.sbml.jsbml.ext.layout.CubicBezier;
import org.sbml.jsbml.ext.layout.Curve;
import org.sbml.jsbml.ext.layout.CurveSegment;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.LayoutModelPlugin;
import org.sbml.jsbml.ext.layout.LineSegment;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.TextGlyph;
import org.sbml.jsbml.util.filters.Filter;
import org.sbml.jsbml.xml.stax.SBMLObjectForXML;


/**
 * This class is used to parse the layout extension package elements and
 * attributes. The namespaceURI URI of this parser is
 * "http://www.sbml.org/sbml/level3/version1/layout/version1". This parser is
 * able to read and write elements of the layout package (implements
 * ReadingParser and WritingParser).
 * 
 * @author Nicolas Rodriguez
 * @author Sebastian Fr&ouml;lich
 * @author Andreas Dr&auml;ger
 * @since 1.0
 * @version $Rev$
 * 
 */
public class LayoutParser implements ReadingParser, WritingParser {
	
	private Logger logger = Logger.getLogger(LayoutParser.class);
	
  /**
   *
   */
  enum LayoutList {
	  listOfLayouts,
	  listOfSpeciesGlyphs,
	  listOfReactionGlyphs,
	  listOfTextGlyphs,
	  listOfCompartmentGlyphs,
	  none,
	  listOfCurveSegments,
	  listOfSpeciesReferenceGlyphs
  }

  /**
   * The namespace URI of this parser.
   */
  private static final String namespaceURI = LayoutConstants.namespaceURI_L2;

  /**
   * 
   * @return the namespaceURI of this parser.
   */
  public static String getNamespaceURI() {
    return namespaceURI;
  }

  /**
   * The layoutList enum which represents the name of the list this parser is
   * currently reading.
   * 
   */
  private LayoutList groupList = LayoutList.none;

  /**
   * Log4j logger
   */
  private Logger log4jLogger = Logger.getLogger(LayoutParser.class);

  /**
   * This map contains all the relationships XML element name <=> matching
   * java class.
   */
  private HashMap<String, Class<? extends Object>> sbmlLayoutElements;

  /**
   * 
   */
  public LayoutParser() {
    super();
    sbmlLayoutElements = new HashMap<String, Class<? extends Object>>();
    JSBML.loadClasses(
        "org/sbml/jsbml/resources/cfg/SBMLLayoutElements.xml",
        sbmlLayoutElements);
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.WritingParser#getListOfSBMLElementsToWrite(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  public List<Object> getListOfSBMLElementsToWrite(Object sbase) {
    log4jLogger.debug(String.format("%s : getListOfSBMLElementsToWrite\n",
        getClass().getSimpleName()));

    List<Object> listOfElementsToWrite = new LinkedList<Object>();

    if (sbase instanceof SBMLDocument) {
      // nothing to do
      // TODO : the 'required' attribute is written even if there is no
      // plugin class for the SBMLDocument, so I am not totally sure how
      // this is done.
    } else if (sbase instanceof Model) {
    	LayoutModelPlugin layoutModel = (LayoutModelPlugin) ((Model) sbase)
          .getExtension(namespaceURI);

      if (layoutModel != null && layoutModel.isSetListOfLayouts()) {
        listOfElementsToWrite.add(layoutModel.getListOfLayouts());
      }

    } else if (sbase instanceof TreeNode) {
      Enumeration<?> children = ((TreeNode) sbase).children();
      while (children.hasMoreElements()) {
        listOfElementsToWrite.add(children.nextElement());
      }
    }

    if (sbase instanceof SBase) {
      SBase elem = (SBase) sbase;
      log4jLogger.info("add to write: " + elem.getElementName()
          + " namespace: " + elem.getNamespaces().toString());
      if (sbase instanceof ListOf<?>) {
        log4jLogger.info("process a ListOf instance");
        ListOf<SBase> listOf = (ListOf<SBase>) sbase;
        if (!listOf.isEmpty()) {
          listOfElementsToWrite = new LinkedList<Object>();
          for (int i = 0; i < listOf.size(); i++) {
            SBase element = listOf.get(i);
            if (element != null) {
              listOfElementsToWrite.add(element);
            }
          }
        }
      } else if (sbase instanceof Layout) {
        Layout layout = (Layout) sbase;

        if (layout.isSetListOfSpeciesGlyphs()) {
          listOfElementsToWrite.add(layout.getListOfSpeciesGlyphs());
          ListOf<SpeciesGlyph> listOfSpeciesGlyph = layout
              .getListOfSpeciesGlyphs();
          log4jLogger.info("found list of species glyph");
          log4jLogger.info("list of species glyph: "
              + listOfSpeciesGlyph.getElementName());
        } else if (layout.isSetListOfCompartmentGlyphs()) {
          listOfElementsToWrite.add(layout
              .getListOfCompartmentGlyphs());
        } else if (layout.isSetListOfReactionGlyphs()) {
          listOfElementsToWrite.add(layout.getListOfReactionGlyphs());
        } else if (layout.isSetListOfTextGlyphs()) {
          listOfElementsToWrite.add(layout.getListOfTextGlyphs());
        }
      } else if (sbase instanceof Point) {
        Point point = (Point) sbase;
        SBase parent = point.getParentSBMLObject();
        if (parent instanceof CurveSegment) {
          log4jLogger.debug(" curveSegment: point element name: "
              + point.getElementName());
        }
      }
    }

    if (listOfElementsToWrite.isEmpty()) {
      listOfElementsToWrite = null;
    }

    return listOfElementsToWrite;
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.WritingParser#writeAttributes(org.sbml.jsbml.xml.stax.SBMLObjectForXML, java.lang.Object)
   */
  public void writeAttributes(SBMLObjectForXML xmlObject,
      Object sbmlElementToWrite) {
    if (sbmlElementToWrite instanceof SBase) {
      SBase sbase = (SBase) sbmlElementToWrite;
      xmlObject.addAttributes(sbase.writeXMLAttributes());
    }
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.WritingParser#writeCharacters(org.sbml.jsbml.xml.stax.SBMLObjectForXML, java.lang.Object)
   */
  public void writeCharacters(SBMLObjectForXML xmlObject,
      Object sbmlElementToWrite) {}

  /* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.WritingParser#writeElement(org.sbml.jsbml.xml.stax.SBMLObjectForXML, java.lang.Object)
   */
  public void writeElement(SBMLObjectForXML xmlObject,
      Object sbmlElementToWrite) {
    log4jLogger.warn(MessageFormat.format("{0} : writeElement", getClass()
        .getSimpleName()));

    if (sbmlElementToWrite instanceof SBase) {
      SBase sbase = (SBase) sbmlElementToWrite;
      log4jLogger.info(MessageFormat.format("{0} ", sbase.getElementName()));
      if (!xmlObject.isSetName()) {
        if (sbase instanceof ListOf<?>) {
          xmlObject.setName(LayoutList.listOfLayouts.toString());
        } else {
          xmlObject.setName(sbase.getElementName());
        }
      }
      if (!xmlObject.isSetPrefix()) {
        xmlObject.setPrefix("layout");
      }
      xmlObject.setNamespace(namespaceURI);

    }

  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.WritingParser#writeNamespaces(org.sbml.jsbml.xml.stax.SBMLObjectForXML, java.lang.Object)
   */
  public void writeNamespaces(SBMLObjectForXML xmlObject,
      Object sbmlElementToWrite) {}

  /* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.ReadingParser#processAttribute(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.Object)
   */
  public void processAttribute(String elementName, String attributeName,
      String value, String prefix, boolean isLastAttribute,
      Object contextObject) {
    log4jLogger.debug("processAttribute\n");

    boolean isAttributeRead = false;

    if (contextObject instanceof SBase) {

      SBase sbase = (SBase) contextObject;

      log4jLogger.debug("processAttribute : level, version = "
          + sbase.getLevel() + ", " + sbase.getVersion());

      try {
        isAttributeRead = sbase.readAttribute(attributeName, prefix,
            value);
      } catch (Throwable exc) {
        System.err.println(exc.getMessage());
      }
    } else if (contextObject instanceof Annotation) {
      Annotation annotation = (Annotation) contextObject;
      isAttributeRead = annotation.readAttribute(attributeName, prefix,
          value);
    } else if (contextObject instanceof SBasePlugin) {
      isAttributeRead = ((SBasePlugin) contextObject).readAttribute(
          attributeName, prefix, value);
    }

    if (!isAttributeRead) {
      log4jLogger.warn("processAttribute : The attribute "
          + attributeName + " on the element " + elementName
          + " is not part of the SBML specifications");
    }
  }

  /* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.ReadingParser#processCharactersOf(java.lang.String, java.lang.String, java.lang.Object)
   */
  public void processCharactersOf(String elementName, String characters,
      Object contextObject) {}


	public void processEndDocument(SBMLDocument sbmlDocument) 
	{
		if (sbmlDocument.isSetModel() && sbmlDocument.getModel().getExtension(namespaceURI) != null) 
		{
			// going through the document to find all Curve objects
			// filtering only on the ListOfLayouts
			List<? extends TreeNode> curveElements = sbmlDocument.getModel().getExtension(namespaceURI).filter(new Filter() {

				public boolean accepts(Object o) 
				{
					if (o instanceof Curve)
					{
						return true;
					}

					return false;
				}
			});

			for (TreeNode curveNode : curveElements)
			{
				Curve curve = (Curve) curveNode;

				// transform the CubicBezier into LineSegment
				int i = 0;				
				for (CurveSegment curveSegment : curve.getListOfCurveSegments().clone()) 
				{
					// Making sure the type attribute is set
					if (! curveSegment.isSetType())
					{
						if (((CubicBezier) curveSegment).isSetBasePoint1() || ((CubicBezier) curveSegment).isSetBasePoint2())
						{
							// trick to set the 'type' attribute, although the setType method is not visible.
							curveSegment.readAttribute("type", "", CurveSegment.Type.CUBIC_BEZIER.toString());
						}
						else 
						{
							curveSegment.readAttribute("type", "", CurveSegment.Type.LINE_SEGMENT.toString());
						}
					}

					if (curveSegment.getType().equals(CurveSegment.Type.LINE_SEGMENT)) 
					{
						LineSegment realCurveSegment = new LineSegment(curveSegment);
						logger.debug("Transformed CubicBezier : " + curveSegment + " into LineSegment.");
						curve.getListOfCurveSegments().remove(i);
						curve.getListOfCurveSegments().add(i, realCurveSegment);
					}

					if (logger.isDebugEnabled())
					{
						logger.debug("Transformed CurveSegment : realCurveSegment = " + curve.getListOfCurveSegments().get(i));
					}

					i++;
				}
			}
		}
	}
	

  /* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.ReadingParser#processNamespace(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, boolean, java.lang.Object)
   */
  public void processNamespace(String elementName, String URI, String prefix,
      String localName, boolean hasAttributes, boolean isLastNamespace,
      Object contextObject) {}

  /* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.ReadingParser#processStartElement(java.lang.String, java.lang.String, boolean, boolean, java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  public Object processStartElement(String elementName, String prefix,
      boolean hasAttributes, boolean hasNamespaces, Object contextObject) {
    if (elementName.equals("basePoint1")) {
      log4jLogger.debug("processing basePoint1");
    }
    if (sbmlLayoutElements.containsKey(elementName)) {
      try {
        Object newContextObject = sbmlLayoutElements.get(elementName)
            .newInstance();
        if (contextObject instanceof Annotation) {
          Annotation annotation = (Annotation) contextObject;

          Model model = (Model) annotation.getParent();
          LayoutModelPlugin layoutModel = null;

          if (model.getExtension(namespaceURI) != null) {
            layoutModel = (LayoutModelPlugin) model
                .getExtension(namespaceURI);
          } else {
            layoutModel = new LayoutModelPlugin(model);
            model.addExtension(namespaceURI, layoutModel);
          }
        }
        if (contextObject instanceof SBase) {
          setLevelAndVersionFor(newContextObject,
              (SBase) contextObject);
        }
        if (contextObject instanceof Annotation) {
          Annotation annotation = (Annotation) contextObject;
          if (elementName.equals("listOfLayouts")) {
            ListOf<Layout> listOfLayouts = (ListOf<Layout>) newContextObject;
            setNamespace(listOfLayouts, namespaceURI);
            this.groupList = LayoutList.listOfLayouts;
            Model model = (Model) annotation.getParent();
            LayoutModelPlugin layoutModel = (LayoutModelPlugin) model
                .getExtension(namespaceURI);
            layoutModel.setListOfLayouts(listOfLayouts);
            return listOfLayouts;
          } else {
            log4jLogger.warn(MessageFormat.format(
                "Element {0} not recognized!", elementName));
          }
        } else if (contextObject instanceof ListOf<?>) {
          ListOf<?> listOf = (ListOf<?>) contextObject;
          if (listOf.getParentSBMLObject() instanceof Model) {
            if (elementName.equals("layout")
                && this.groupList.equals(LayoutList.listOfLayouts)) {
              ListOf<Layout> listOflayouts = (ListOf<Layout>) listOf;
              Layout layout = (Layout) newContextObject;
              setNamespace(layout, namespaceURI);
              listOflayouts.add(layout);
              return layout;
            }
          } else if (listOf.getParentSBMLObject() instanceof Layout) {
            if (elementName.equals("compartmentGlyph")
                && this.groupList.equals(LayoutList.listOfCompartmentGlyphs)) {
              ListOf<CompartmentGlyph> listOfCompartmentGlyph = (ListOf<CompartmentGlyph>) contextObject;
              CompartmentGlyph compartmentGlyph = (CompartmentGlyph) newContextObject;
              setNamespace(listOfCompartmentGlyph, namespaceURI);
              listOfCompartmentGlyph.add(compartmentGlyph);
              this.groupList = LayoutList.listOfCompartmentGlyphs;
              return compartmentGlyph;
            } else if (elementName.equals("textGlyph")
                && this.groupList.equals(LayoutList.listOfTextGlyphs)) {
              ListOf<TextGlyph> listOfTextGlyph = (ListOf<TextGlyph>) contextObject;
              TextGlyph textGlyph = (TextGlyph) newContextObject;
              setNamespace(listOfTextGlyph, namespaceURI);
              listOfTextGlyph.add(textGlyph);
              this.groupList = LayoutList.listOfTextGlyphs;
              return textGlyph;
            } else if (elementName.equals("speciesGlyph")
                && this.groupList.equals(LayoutList.listOfSpeciesGlyphs)) {
              ListOf<SpeciesGlyph> listOfSpeciesGlyph = (ListOf<SpeciesGlyph>) contextObject;
              SpeciesGlyph speciesGlyph = (SpeciesGlyph) newContextObject;
              setNamespace(listOfSpeciesGlyph, namespaceURI);
              listOfSpeciesGlyph.add(speciesGlyph);
              this.groupList = LayoutList.listOfSpeciesGlyphs;
              return speciesGlyph;
            } else if (elementName.equals("reactionGlyph")
                && this.groupList.equals(LayoutList.listOfReactionGlyphs)) {
              ListOf<ReactionGlyph> listOfReactionGlyph = (ListOf<ReactionGlyph>) contextObject;
              ReactionGlyph reactionGlyph = (ReactionGlyph) newContextObject;
              setNamespace(reactionGlyph, namespaceURI);
              listOfReactionGlyph.add(reactionGlyph);
              this.groupList = LayoutList.listOfReactionGlyphs;
              return reactionGlyph;
            } else if (elementName.equals("boundingBox")
                && this.groupList.equals(LayoutList.listOfCompartmentGlyphs)) {
              CompartmentGlyph compartmentGlyph = (CompartmentGlyph) contextObject;
              BoundingBox boundingBox = (BoundingBox) newContextObject;
              setNamespace(boundingBox, namespaceURI);
              compartmentGlyph.setBoundingBox(boundingBox);
              return boundingBox;
            }
          } else if (listOf.getParentSBMLObject() instanceof ReactionGlyph) {
            if (elementName.equals("speciesReferenceGlyph")
                && this.groupList.equals(LayoutList.listOfSpeciesReferenceGlyphs)) {
              SpeciesReferenceGlyph speciesReferenceGlyph = (SpeciesReferenceGlyph) newContextObject;
              ListOf<SpeciesReferenceGlyph> listOfSpeciesReferenceGlyph = (ListOf<SpeciesReferenceGlyph>) contextObject;
              listOfSpeciesReferenceGlyph
                  .add(speciesReferenceGlyph);
              return speciesReferenceGlyph;
            }
          } else if (elementName.equals("curveSegment")
              && this.groupList.equals(LayoutList.listOfCurveSegments)) {
            ListOf<CurveSegment> listOfLineSegment = (ListOf<CurveSegment>) contextObject;
            CubicBezier lineSegment = (CubicBezier) newContextObject;
            setNamespace(lineSegment, namespaceURI);
            listOfLineSegment.add(lineSegment);
            this.groupList = LayoutList.listOfCurveSegments;
            return lineSegment;
          } else if (listOf.getParentSBMLObject() instanceof Curve) {

            if (elementName.equals("curveSegment") || elementName.equals("cubicBezier")
            		|| elementName.equals("lineSegment"))
            {
              ListOf<CurveSegment> listOfLineSegment = (ListOf<CurveSegment>) contextObject;
              CubicBezier lineSegment = (CubicBezier) newContextObject;
              setNamespace(lineSegment, namespaceURI);
              listOfLineSegment.add(lineSegment);
              return lineSegment;
            }
          }

        } else if (contextObject instanceof Layout) {
          Layout layout = (Layout) contextObject;
          this.groupList = LayoutList.listOfLayouts;
          if (elementName.equals("dimensions")
              && this.groupList.equals(LayoutList.listOfLayouts)) {
            Dimensions dimensions = (Dimensions) newContextObject;
            setNamespace(dimensions, namespaceURI);
            layout.setDimensions(dimensions);
            return dimensions;
          } else if (elementName.equals("listOfCompartmentGlyphs")
              && this.groupList.equals(LayoutList.listOfLayouts)) {
            ListOf<CompartmentGlyph> listOfCompartmentGlyphs = (ListOf<CompartmentGlyph>) newContextObject;
            layout.setListOfCompartmentGlyphs(listOfCompartmentGlyphs);
            this.groupList = LayoutList.listOfCompartmentGlyphs;
            return listOfCompartmentGlyphs;
          } else if (elementName.equals("listOfSpeciesGlyphs")
              && this.groupList.equals(LayoutList.listOfLayouts)) {
            ListOf<SpeciesGlyph> listofSpeciesGlyph = (ListOf<SpeciesGlyph>) newContextObject;
            layout.setListOfSpeciesGlyphs(listofSpeciesGlyph);
            this.groupList = LayoutList.listOfSpeciesGlyphs;
            return listofSpeciesGlyph;
          } else if (elementName.equals("listOfReactionGlyphs")
              && this.groupList.equals(LayoutList.listOfLayouts)) {
            ListOf<ReactionGlyph> listOfReactionGlyphs = (ListOf<ReactionGlyph>) newContextObject;
            layout.setListOfReactionGlyphs(listOfReactionGlyphs);
            this.groupList = LayoutList.listOfReactionGlyphs;
            return listOfReactionGlyphs;
          } else if (elementName.equals("listOfTextGlyphs")
              && this.groupList.equals(LayoutList.listOfLayouts)) {
            ListOf<TextGlyph> listOfTextGlyphs = (ListOf<TextGlyph>) newContextObject;
            layout.setListOfTextGlyphs(listOfTextGlyphs);
            this.groupList = LayoutList.listOfTextGlyphs;
            return listOfTextGlyphs;
          }
        } else if (contextObject instanceof ReactionGlyph) {
          ReactionGlyph reactionGlyph = (ReactionGlyph) contextObject;
          if (elementName.equals("curve")
              && this.groupList.equals(LayoutList.listOfReactionGlyphs)) {
            Curve curve = (Curve) newContextObject;
            setNamespace(curve, namespaceURI);
            reactionGlyph.setCurve(curve);
            return curve;
          } else if (elementName
              .equals("listOfSpeciesReferenceGlyphs")
              && this.groupList.equals(LayoutList.listOfReactionGlyphs)) {
            ListOf<SpeciesReferenceGlyph> listOfSpeciesReferenceGlyphs = (ListOf<SpeciesReferenceGlyph>) newContextObject;
            reactionGlyph
                .setListOfSpeciesReferencesGlyph(listOfSpeciesReferenceGlyphs);
            this.groupList = LayoutList.listOfSpeciesReferenceGlyphs;
            return listOfSpeciesReferenceGlyphs;
          } else if (elementName.equals("boundingBox")) {
        	  BoundingBox boundingBox = (BoundingBox) newContextObject;
        	  setNamespace(boundingBox, namespaceURI);
        	  reactionGlyph.setBoundingBox(boundingBox);
        	  return boundingBox;
          }
        } else if (contextObject instanceof SpeciesGlyph) {
          SpeciesGlyph speciesGlyph = (SpeciesGlyph) contextObject;
          if (elementName.equals("boundingBox")) {
            BoundingBox boundingBox = (BoundingBox) newContextObject;
            setNamespace(boundingBox, namespaceURI);
            speciesGlyph.setBoundingBox(boundingBox);
            return boundingBox;
          }
        } else if (contextObject instanceof CompartmentGlyph) {
          if (elementName.equals("boundingBox")
              && this.groupList.equals(LayoutList.listOfCompartmentGlyphs)) {
            CompartmentGlyph compartmentGlyph = (CompartmentGlyph) contextObject;
            BoundingBox boundingBox = (BoundingBox) newContextObject;
            setNamespace(boundingBox, namespaceURI);
            compartmentGlyph.setBoundingBox(boundingBox);
            return boundingBox;
          }
        } else if (contextObject instanceof TextGlyph) {
          if (elementName.equals("boundingBox")
              && this.groupList.equals(LayoutList.listOfTextGlyphs)) {
            TextGlyph textGlyph = (TextGlyph) contextObject;
            BoundingBox boundingBox = (BoundingBox) newContextObject;
            setNamespace(boundingBox, namespaceURI);
            textGlyph.setBoundingBox(boundingBox);
            return boundingBox;
          }
        } else if (contextObject instanceof Curve) {
          if (elementName.equals("listOfCurveSegments")
              && this.groupList.equals(LayoutList.listOfReactionGlyphs)) {
            Curve curve = (Curve) contextObject;
            ListOf<CurveSegment> listOfCurveSegments = (ListOf<CurveSegment>) newContextObject;
            curve.setListOfCurveSegments(listOfCurveSegments);
            this.groupList = LayoutList.listOfReactionGlyphs;
            return listOfCurveSegments;
          } else if (elementName.equals("listOfCurveSegments")
              && this.groupList.equals(LayoutList.listOfSpeciesReferenceGlyphs)) {
            Curve curve = (Curve) contextObject;
            ListOf<CurveSegment> listOfCurveSegments = (ListOf<CurveSegment>) newContextObject;
            curve.setListOfCurveSegments(listOfCurveSegments);
            this.groupList = LayoutList.listOfSpeciesReferenceGlyphs;
            return listOfCurveSegments;
          }
        } else if (contextObject instanceof BoundingBox) {
          BoundingBox boundingBox = (BoundingBox) contextObject;
          if (elementName.equals("position")) {
            Point point = (Point) newContextObject;
            boundingBox.setPosition(point);
            return point;
          } else if (elementName.equals("dimensions")) {
            Dimensions dimensions = (Dimensions) newContextObject;
            boundingBox.setDimensions(dimensions);
            return dimensions;
          }
        } else if (contextObject instanceof CurveSegment) {
        	if (elementName.equals("start")) {
        	  CurveSegment lineSegment = (CurveSegment) contextObject;
            Point start = (Point) newContextObject;
            lineSegment.setStart(start);
            setNamespace(start, namespaceURI);
            return start;
          } else if (elementName.equals("end")) {
        	  CurveSegment lineSegment = (CurveSegment) contextObject;
            Point end = (Point) newContextObject;
            lineSegment.setEnd(end);
            setNamespace(end, namespaceURI);
            return end;
          } else if (elementName.equals("basePoint1")) {
            CubicBezier lineSegment = (CubicBezier) contextObject;

            BasePoint1 basePoint1 = (BasePoint1) newContextObject;
            lineSegment.setBasePoint1(basePoint1);
            setNamespace(basePoint1, namespaceURI);
            return basePoint1;
          } else if (elementName.equals("basePoint2")) {
            CubicBezier lineSegment = (CubicBezier) contextObject;
            BasePoint2 basePoint2 = (BasePoint2) newContextObject;
            lineSegment.setBasePoint2(basePoint2);
            setNamespace(basePoint2, namespaceURI);
            return basePoint2;
          }
        } else if (contextObject instanceof SpeciesReferenceGlyph
            && this.groupList.equals(LayoutList.listOfSpeciesReferenceGlyphs)) {
            SpeciesReferenceGlyph speciesReferenceGlyph = (SpeciesReferenceGlyph) contextObject;
          if (elementName.equals("curve")) {
            Curve curve = (Curve) newContextObject;
            speciesReferenceGlyph.setCurve(curve);
            setNamespace(curve, namespaceURI);
            return curve;
          } else if (elementName.equals("boundingBox")) {
              BoundingBox boundingBox = (BoundingBox) newContextObject;
              setNamespace(boundingBox, namespaceURI);
              speciesReferenceGlyph.setBoundingBox(boundingBox);
              return boundingBox;
            }
        } else {
          log4jLogger.info("Tag " + elementName
              + " could not be recognized.");
          log4jLogger.info("contextObject: "
              + contextObject.toString());
          log4jLogger.info("newContextObject: "
              + newContextObject.toString());
        }
      } catch (Exception e) {
        log4jLogger.error(e.getLocalizedMessage(), e);
      }
    }
    return contextObject;
  }

  private void setNamespace(SBase sbase, String namespace) 
  {
	  // removing the namespace declared by default on the object.
	  // Will allow to read older packages when we have several versions for each packages.
	  sbase.getNamespaces().clear();
	  
	  // Setting the correct namespace to the object
	  sbase.addNamespace(namespace);
  }

/* (non-Javadoc)
   * @see org.sbml.jsbml.xml.parsers.ReadingParser#processEndElement(java.lang.String, java.lang.String, boolean, java.lang.Object)
   */
  public boolean processEndElement(String elementName, String prefix,
      boolean isNested, Object contextObject) 
  {
	  if (log4jLogger.isDebugEnabled())
	  {
		  log4jLogger.debug("contextObject: " + contextObject.getClass().getName());
		  log4jLogger.debug("elementName: " + elementName);
	  }
	  
    if (elementName.equals("listOfLayouts")
        || elementName.equals("listOfSpeciesGlyphs")
        || elementName.equals("listOfReactionGlyphs")
        || elementName.equals("listOfCompartmentGlyphs")
        || elementName.equals("listOfTextGlyphs")) {
      this.groupList = LayoutList.none;
      log4jLogger.debug("set listType to: none");
    } else if (elementName.equals("listOfSpeciesReferenceGlyphs")) {
      this.groupList = LayoutList.listOfReactionGlyphs;
      log4jLogger.debug("set listType to: listOfReactionGlyphs");
    }

    return true;
  }

  /**
   * Sets level and version properties of the new object according to the
   * value in the model.
   * 
   * @param newContextObject
   * @param parent
   */
  private void setLevelAndVersionFor(Object newContextObject, SBase parent) {
    if (newContextObject instanceof SBase) {
      SBase sb = (SBase) newContextObject;
      // Level and version will be -1 if not set, so we don't
      // have to check.
      sb.setLevel(parent.getLevel());
      sb.setVersion(parent.getVersion());
    }
  }

}

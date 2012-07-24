package org.sbml.jsbml.ext.layout;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;

public class TestSpeciesReferenceGlyphCurve {

	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws SBMLException 
	 */
	public static void main(String[] args) throws SBMLException, XMLStreamException {

		SBMLDocument d = new SBMLDocument(3,1);
		Model model = d.createModel("testLayoutWriting");
		
		ExtendedLayoutModel lModel = new ExtendedLayoutModel(model);
		Layout layout = lModel.createLayout("layout");
		
		model.addExtension(LayoutConstants.namespaceURI, lModel);
		
		ReactionGlyph rg = new ReactionGlyph("react_r1", model.getLevel(), model.getVersion());
		
		SpeciesReferenceGlyph srg1 = rg.createSpeciesReferenceGlyph("srg_r1_s1", "SPG1");
		SpeciesReferenceGlyph srg2 = rg.createSpeciesReferenceGlyph("srg_r1_s2", "SPG2");
		srg1.setRole(SpeciesReferenceRole.SUBSTRATE);
		srg2.setRole(SpeciesReferenceRole.PRODUCT);

		assert(model.findNamedSBase("srg_r1_s1") != null);
		
		BoundingBox bbRg = rg.createBoundingBox(10.0, 10.0, 0.0);
		bbRg.setPosition(new Point(100.0, 0.0, 0.0));

		CurveSegment cs1 = new CurveSegment();
		cs1.setStart(new Point(35.0, 10.0, 0.0));
		cs1.setEnd(new Point(100.0, 10.0, 0.0));
		Curve c = new Curve();
		ListOf<CurveSegment> csList = new ListOf<CurveSegment>();
		csList.add(cs1);
		c.setListOfCurveSegments(csList);
		srg1.setCurve(c);
				
		CurveSegment cs2 = new CurveSegment();
		cs2.createStart(110.0, 10.0, 0.0);
		cs2.createEnd(235.0, 10.0, 0.0);
		Curve c2 = new Curve();
		ListOf<CurveSegment> csList2 = new ListOf<CurveSegment>();
		csList2.add(cs2);
		c2.setListOfCurveSegments(csList2);
		srg2.setCurve(c2);
			
		BoundingBox bbSrg2 = srg2.createBoundingBox();
		bbSrg2.setPosition(new Point(110.0, 10.0, 0.0));
		bbSrg2.setDimensions(new Dimensions(200.0, 10.0, 0, model.getLevel(), model.getVersion()));
			
		layout.addReactionGlyph(rg);
		
		String writtenDocument = JSBML.writeSBMLToString(d);
		
		System.out.println(writtenDocument);
		
		SBMLDocument d2 = JSBML.readSBMLFromString(writtenDocument);
		
		assert(d2.getModel().findNamedSBase("srg_r1_s1") != null);

		SpeciesReferenceGlyph testSRG1 = (SpeciesReferenceGlyph) d2.getModel().findNamedSBase("srg_r1_s1");

		assert(testSRG1.getCurve().getListOfCurveSegments().size() == 1);

		CurveSegment cs1Bis = testSRG1.getCurve().getListOfCurveSegments().getFirst(); 
				
		assert(cs1Bis.getStart().getX() == 35);
		assert(cs1Bis.getStart().getZ() == 0);
		assert(cs1Bis.getEnd().getY() == 10);
	}

}

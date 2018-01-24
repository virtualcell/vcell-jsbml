/*
 * ----------------------------------------------------------------------------
 * This file is part of JSBML. Please visit <http://sbml.org/Software/JSBML>
 * for the latest version of JSBML and more information about SBML.
 *
 * Copyright (C) 2009-2018 jointly by the following organizations:
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

package org.sbml.jsbml.ext.comp.util;

import org.sbml.jsbml.*;
import org.sbml.jsbml.ext.comp.*;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The {@link CompFlatteningConverter} object...
 *
 * @author Christoph Blessing
 * @since 1.0
 */
public class CompFlatteningConverter {

    private final static Logger LOGGER = Logger.getLogger(CompFlatteningConverter.class.getName());

    private List<String> previousModelIDs;
    private List<String> previousModelMetaIDs;
    private ListOf<ModelDefinition> modelDefinitionListOf;
    private ListOf<ExternalModelDefinition> externalModelDefinitionListOf;

    private List<Submodel> listOfSubmodelsToFlatten;

    private Model flattenedModel;

    public CompFlatteningConverter() {
        this.listOfSubmodelsToFlatten = new ArrayList<>();

        this.previousModelIDs = new ArrayList<>();
        this.previousModelMetaIDs = new ArrayList<>();

        this.modelDefinitionListOf = new ListOf<>();
        this.externalModelDefinitionListOf = new ListOf<>();

        this.flattenedModel = new Model();

    }


    /**
     * Public method to call on a CompflatteningConverter object.
     * Takes a SBML Document and flattens the models of the comp plugin.
     * Returns the SBML Document with a flattend model.
     *
     * @param document SBML Document to flatten
     * @return SBML Document with flattened model
     */
    public SBMLDocument flatten(SBMLDocument document) {

        if (document.isPackageEnabled(CompConstants.shortLabel)) {

            SBase sBase = document.getExtension(CompConstants.shortLabel).getExtendedSBase();

            CompSBMLDocumentPlugin compSBMLDocumentPlugin = (CompSBMLDocumentPlugin) document.getExtension(CompConstants.shortLabel);

            this.modelDefinitionListOf = compSBMLDocumentPlugin.getListOfModelDefinitions();

            this.externalModelDefinitionListOf = compSBMLDocumentPlugin.getListOfExternalModelDefinitions();

            if (document.isSetModel() && document.getModel().getExtension(CompConstants.shortLabel) != null) {

                // TODO: the model itself has to be flattend (can hold a list of replacements etc.)
                CompModelPlugin compModelPlugin = (CompModelPlugin) document.getModel().getExtension(CompConstants.shortLabel);
                this.flattenedModel = instantiateSubModels(compModelPlugin);
            } else {
                LOGGER.warning("No comp package found in Model. Can not flatten.");
            }

        } else {
            LOGGER.warning("No comp package found in Document. Can not flatten.");
        }

        this.flattenedModel.unsetExtension(CompConstants.shortLabel);
        this.flattenedModel.unsetPlugin(CompConstants.shortLabel);

        document.setModel(this.flattenedModel);
        document.unsetExtension(CompConstants.shortLabel);
        document.disablePackage(CompConstants.shortLabel);
        return document;

    }


    /**
     * Initiates every Submodel in the CompModelPlugin recursively
     *
     * @param compModelPlugin
     */
    private Model instantiateSubModels(CompModelPlugin compModelPlugin) {

        // TODO: the first model is not always flat.

        Model model = compModelPlugin.getExtendedSBase().getModel();
        String modelID = model.getId();
        String modelMetaID = model.getMetaId();

        this.flattenedModel = flattenModel(model, modelID, modelMetaID);

        if (compModelPlugin.getSubmodelCount() > 0) {
            // check if submodel has submodel
            this.flattenedModel = initSubModels(compModelPlugin);
        } else {
            LOGGER.info("No more submodels");
        }

        return this.flattenedModel;
    }

    private void replaceElements(CompModelPlugin compModelPlugin, CompSBasePlugin compSBasePlugin) {

        ListOf<Submodel> subModelListOf = compModelPlugin.getListOfSubmodels().clone();

        ListOf<ReplacedElement> listOfReplacedElements = compSBasePlugin.getListOfReplacedElements();

        for (ReplacedElement replacedElement : listOfReplacedElements){

            Submodel submodel = subModelListOf.get(replacedElement.getSubmodelRef());
            ModelDefinition modelDefinition = this.modelDefinitionListOf.get(submodel.getModelRef());
            modelDefinition.remove(replacedElement.getIdRef());
        }

    }


    private Model initSubModels(CompModelPlugin compModelPlugin) {

        ListOf<Submodel> subModelListOf = compModelPlugin.getListOfSubmodels().clone();

        // TODO: replace elements
        ListOf<ReplacedElement> listOfReplacedElements = compModelPlugin.getListOfReplacedElements();

        for(ReplacedElement replacedElement : listOfReplacedElements){
            // pointer to a submodel object that should be considered “replaced”
            Submodel subModelToReplace = subModelListOf.get(replacedElement.getSubmodelRef());

            // get the object

            // object holding the ReplacedElement instance is the one doing the replacing

            // object pointed to by the ReplacedElement object is the object being replaced
            subModelToReplace.getElementBySId(replacedElement.getId()).removeFromParent();

            this.modelDefinitionListOf.remove(subModelToReplace.getId());

            // update dependencies

            // local parameters of reactions

        }

        // TODO: ports
//        ListOf<Port> listOfPorts =compModelPlugin.getListOfPorts();
//        for (Port port : listOfPorts){

//        }


        for (Submodel submodel : subModelListOf) {

            this.listOfSubmodelsToFlatten.add(submodel);

            ModelDefinition modelDefinition = this.modelDefinitionListOf.get(submodel.getModelRef());

            // TODO: how to initialize external model definitions?
            if (modelDefinition == null) {
                ExternalModelDefinition externalModelDefinition = this.externalModelDefinitionListOf.get(submodel.getModelRef());
                try {
                    SBMLDocument externalDocument = SBMLReader.read(externalModelDefinition.getSource());
                    Model flattendExternalModel = flatten(externalDocument).getModel(); //external model can also contain submodels etc.
                    this.flattenedModel = mergeModels(this.flattenedModel, flattendExternalModel);
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }

            if (modelDefinition != null && modelDefinition.getExtension(CompConstants.shortLabel) != null) {
                initSubModels((CompModelPlugin) modelDefinition.getExtension(CompConstants.shortLabel));
            } else {
                LOGGER.info("No model definition found in " + submodel.getId() + ".") ;
            }

        }


        // "else"
        return flattenAndMergeModels(this.listOfSubmodelsToFlatten);
    }

    private Model flattenAndMergeModels(List<Submodel> listOfSubmodelsToFlatten) {

        int sizeOfList = listOfSubmodelsToFlatten.size();

        if (sizeOfList >= 2) {
            Submodel last = listOfSubmodelsToFlatten.get(sizeOfList - 1);
            Submodel secondLast = listOfSubmodelsToFlatten.get(sizeOfList - 2);

            this.flattenedModel = mergeModels(this.flattenedModel, mergeModels(flattenSubModel(secondLast), flattenSubModel(last)));
            listOfSubmodelsToFlatten.remove(secondLast);
            listOfSubmodelsToFlatten.remove(last);

            flattenAndMergeModels(listOfSubmodelsToFlatten);

        } else if (sizeOfList == 1) {
            Submodel last = listOfSubmodelsToFlatten.get(sizeOfList - 1);

            this.flattenedModel = mergeModels(this.flattenedModel, flattenSubModel(last));
        }

        return this.flattenedModel;

    }

    /**
     * All remaining elements are placed in a single Model object
     * The original Model, ModelDefinition, and ExternalModelDefinition objects are all deleted
     *
     * @param previousModel
     * @param currentModel
     * @return mergedModel
     */
    private Model mergeModels(Model previousModel, Model currentModel) {

        Model mergedModel = new Model();

    // Merging of SBML models should be done in the order
    // Compartments -> Species -> Function Definitions -> Rules -> Events -> Units -> Reactions -> Parameters
    // If done in this order, potential conflicts are resolved incrementally along the way.

        if (previousModel != null) {

            // match versions and level
            mergedModel.setLevel(previousModel.getLevel());
            mergedModel.setVersion(previousModel.getVersion());

            mergeListsOfModels(previousModel.getListOfReactions(), previousModel, mergedModel);
            mergeListsOfModels(previousModel.getListOfCompartments(), previousModel, mergedModel);
            mergeListsOfModels(previousModel.getListOfSpecies(), previousModel, mergedModel);
            mergeListsOfModels(previousModel.getListOfFunctionDefinitions(), previousModel, mergedModel);
            mergeListsOfModels(previousModel.getListOfInitialAssignments(), previousModel, mergedModel);
            mergeListsOfModels(previousModel.getListOfParameters(), previousModel, mergedModel);
            mergeListsOfModels(previousModel.getListOfRules(), previousModel, mergedModel);
            mergeListsOfModels(previousModel.getListOfUnitDefinitions(), previousModel, mergedModel);

        }

        if (currentModel != null) {

            mergeListsOfModels(currentModel.getListOfReactions(), currentModel, mergedModel);
            mergeListsOfModels(currentModel.getListOfCompartments(), currentModel, mergedModel);
            mergeListsOfModels(currentModel.getListOfSpecies(), currentModel, mergedModel);
            mergeListsOfModels(currentModel.getListOfFunctionDefinitions(), currentModel, mergedModel);
            mergeListsOfModels(currentModel.getListOfInitialAssignments(), currentModel, mergedModel);
            mergeListsOfModels(currentModel.getListOfParameters(), currentModel, mergedModel);
            mergeListsOfModels(currentModel.getListOfRules(), currentModel, mergedModel);
            mergeListsOfModels(currentModel.getListOfUnitDefinitions(), currentModel, mergedModel);

        }

        // TODO: delete original model, ModelDefinition, and ExternalModelDefinition objects
        // QUESTION: can a model def be instantiated more than one time?
        return mergedModel;
    }


    private void mergeListsOfModels(ListOf listOfObjects, Model sourceModel, Model targetModel) {

        // TODO: generify with listOf SBase

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfReactions) {

            ListOf<Reaction> reactionListOf = sourceModel.getListOfReactions().clone();
            sourceModel.getListOfReactions().removeFromParent();

            for (Reaction reaction : reactionListOf) {
                targetModel.addReaction(reaction.clone());
            }
        }

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfCompartments) {

            ListOf<Compartment> compartmentListOf = sourceModel.getListOfCompartments().clone();
            sourceModel.getListOfCompartments().removeFromParent();

            for (Compartment compartment : compartmentListOf) {
                targetModel.addCompartment(compartment.clone());
            }
        }

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfConstraints) {

            ListOf<Constraint> constraintListOf = sourceModel.getListOfConstraints().clone();
            sourceModel.getListOfConstraints().removeFromParent();

            for (Constraint constraint : constraintListOf) {
                targetModel.addConstraint(constraint.clone());
            }
        }


        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfSpecies) {

            ListOf<Species> speciesListOf = sourceModel.getListOfSpecies().clone();
            sourceModel.getListOfSpecies().removeFromParent();

            for (Species species : speciesListOf) {
                targetModel.addSpecies(species.clone());
            }
        }

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfEvents) {

            ListOf<Event> eventListOf = sourceModel.getListOfEvents().clone();
            sourceModel.getListOfEvents().removeFromParent();

            for (Event event : eventListOf) {
                targetModel.addEvent(event.clone());
            }
        }

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfFunctionDefinitions) {

            ListOf<FunctionDefinition> functionalDefinitionsListOf = sourceModel.getListOfFunctionDefinitions().clone();
            sourceModel.getListOfFunctionDefinitions().removeFromParent();

            for (FunctionDefinition functionalDefinition : functionalDefinitionsListOf) {
                targetModel.addFunctionDefinition(functionalDefinition.clone());
            }
        }

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfInitialAssignments) {

            ListOf<InitialAssignment> initialAssignmentListOf = sourceModel.getListOfInitialAssignments().clone();
            sourceModel.getListOfInitialAssignments().removeFromParent();

            for (InitialAssignment initialAssignment : initialAssignmentListOf) {
                targetModel.addInitialAssignment(initialAssignment.clone());
            }
        }

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfParameters) {

            ListOf<Parameter> parameterListOf = sourceModel.getListOfParameters().clone();
            sourceModel.getListOfParameters().removeFromParent();

            for (Parameter parameter : parameterListOf) {
                targetModel.addParameter(parameter.clone());
            }
        }

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfRules) {

            ListOf<Rule> ruleListOf = sourceModel.getListOfRules().clone();
            sourceModel.getListOfRules().removeFromParent();

            for (Rule rule : ruleListOf) {
                targetModel.addRule(rule.clone());
            }
        }

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfUnitDefinitions) {

            ListOf<UnitDefinition> unitDefinitionListOf = sourceModel.getListOfUnitDefinitions().clone();
            sourceModel.getListOfUnitDefinitions().removeFromParent();

            for (UnitDefinition unit : unitDefinitionListOf) {
                targetModel.addUnitDefinition(unit.clone());
            }
        }

        if (listOfObjects.getSBaseListType() == ListOf.Type.listOfLocalParameters) {

            sourceModel.getLocalParameterCount();

        }



        //TODO:
        // no longer supported? there are no get methods for this
//        ListOf.Type.listOfCompartmentTypes
//        ListOf.Type.listOfEventAssignments
//        ListOf.Type.listOfLocalParameters: there is no getter method :(
//        ListOf.Type.listOfModifiers
//        ListOf.Type.listOfSpeciesTypes
//        ListOf.Type.listOfUnits

        // maybe they are already in listOfReactions?
//        ListOf.Type.listOfProducts
//        ListOf.Type.listOfReactants


    }

    /**
     * Performs the routine to flatten a submodel into a model
     *
     * @param subModel
     * @return
     */
    private Model flattenSubModel(Submodel subModel) {

        Model model = new Model();

        // StringBuilder stringBuilder = new StringBuilder();

        // 2
        // Let “M” be the identifier of a given submodel.
        String subModelID = subModel.getId() + "_";
        String subModelMetaID = subModel.getMetaId() + "_";

        // Verify that no object identifier or meta identifier of objects in that submodel
        // (i.e., the id or metaid attribute values)
        // begin with the character sequence “M__”;

        // if there is an existing identifier or meta identifier beginning with “M__”,
        // add an underscore to “M__” (i.e., to produce “M___”) and again check that the sequence is unique.
        // Continue adding underscores until you find a unique prefix. Let “P” stand for this final prefix.

        while (this.previousModelIDs.contains(subModelID)) {
            subModelID += "_";
        }

        while (this.previousModelMetaIDs.contains(subModelMetaID)) {
            subModelMetaID += "_";
        }

        if(!this.previousModelIDs.isEmpty()){ // because libSBML does it
            subModelID = this.previousModelIDs.get(this.previousModelIDs.size()-1) + subModelID;
        }

        this.previousModelIDs.add(subModelID);
        this.previousModelMetaIDs.add(subModelID);


        if (subModel.getModelRef() != null) {

            // initiate a clone of the referenced model
            Model modelOfSubmodel = this.modelDefinitionListOf.get(subModel.getModelRef()).clone();

            // 3
            // Remove all objects that have been replaced or deleted in the submodel.

            // TODO Delete Objects
            // each Deletion object identifies an object to “remove” from that Model instance
            for (Deletion deletion : subModel.getListOfDeletions()) {

                // TODO: search for element to remove in all model definitions?
                this.modelDefinitionListOf.remove(deletion.getMetaIdRef());
                //subModel.removeDeletion(deletion);

                //deletion.removeFromParent();
                //for (ModelDefinition modelDefinition : this.modelDefinitionListOf){
                //    modelDefinition.remove(deletion.getMetaIdRef());
                //}
               // modelOfSubmodel.remove(deletion.getMetaIdRef());

            }
            //subModel.getListOfDeletions().removeFromParent();

            // TODO: Replace Objects

            // 4
            // Transform the remaining objects in the submodel as follows:
            // a)
            // Change every identifier (id attribute)
            // to a new value obtained by prepending “P” to the original identifier.
            // b)
            // Change every meta identifier (metaid attribute)
            // to a new value obtained by prepending “P” to the original identifier.

            // like this for all the lists?
            model = flattenModel(modelOfSubmodel, subModelID, subModelMetaID);

//            for (Reaction reaction : modelOfSubmodel.getListOfReactions()) {
//                reaction.setId(subModelID + reaction.getId());
//                if (!reaction.getMetaId().equals("")) {
//                    reaction.setMetaId(subModelMetaID + reaction.getMetaId());
//                }
//
//                if(reaction.isPackageEnabled(CompConstants.shortLabel)){
//                    CompModelPlugin compModelPlugin = (CompModelPlugin) reaction.getExtension(CompConstants.shortLabel);
//                    replaceElements(compModelPlugin);
//                }
//            }
//
//            for (Species species : modelOfSubmodel.getListOfSpecies()) {
//                String flattenedSpeciesID = subModelID + species.getId();
//                species.setId(flattenedSpeciesID);
//                if (!species.getMetaId().equals("")) {
//                    species.setMetaId(subModelMetaID + species.getMetaId());
//                }
//            }
//
//            for (Compartment compartment : modelOfSubmodel.getListOfCompartments()) {
//                String flattenedCompartmentID = subModelID + compartment.getId();
//                compartment.setId(flattenedCompartmentID);
//                if (!compartment.getMetaId().equals("")) {
//                    compartment.setMetaId(subModelMetaID + compartment.getMetaId());
//                }
//
//                if(compartment.isPackageEnabled(CompConstants.shortLabel)){
//                    CompModelPlugin compModelPlugin = (CompModelPlugin) compartment.getExtension(CompConstants.shortLabel);
//                    replaceElements(compModelPlugin);
//                }
//            }
//
//            for (Constraint constraint : modelOfSubmodel.getListOfConstraints()) {
//                String flattenedContraintID = subModelID + constraint.getId();
//                constraint.setId(flattenedContraintID);
//                if (!constraint.getMetaId().equals("")) {
//                    constraint.setMetaId(subModelMetaID + constraint.getMetaId());
//                }
//            }
//
//            for (Parameter parameter : modelOfSubmodel.getListOfParameters()) {
//                String flattenedParameterID = subModelID + parameter.getId();
//                parameter.setId(flattenedParameterID);
//                if (!parameter.getMetaId().equals("")) {
//                    parameter.setMetaId(subModelMetaID + parameter.getMetaId());
//                }
//
//                if(parameter.isPackageEnabled(CompConstants.shortLabel)){
//                    CompModelPlugin compModelPlugin = (CompModelPlugin) parameter.getExtension(CompConstants.shortLabel);
//                    replaceElements(compModelPlugin);
//                }
//            }
//
//            for (Rule rule : modelOfSubmodel.getListOfRules()) {
//                if(!rule.getId().equals("")){ // TODO: is this only the case for rules? test 22 fails...
//                String flattenedRuleID = subModelID + rule.getId();
//                rule.setId(flattenedRuleID);
//                }
//                if (!rule.getMetaId().equals("")) {
//                    rule.setMetaId(subModelMetaID + rule.getMetaId());
//                }
//            }
//
//            for (UnitDefinition unitDefinition : modelOfSubmodel.getListOfUnitDefinitions()) {
//                String flattenedUnitDefinitionID = subModelID + unitDefinition.getId();
//                unitDefinition.setId(flattenedUnitDefinitionID);
//                if (!unitDefinition.getMetaId().equals("")) {
//                    unitDefinition.setMetaId(subModelMetaID + unitDefinition.getMetaId());
//                }
//            }
//
//            for (InitialAssignment initialAssignment : modelOfSubmodel.getListOfInitialAssignments()) {
//                if(!initialAssignment.getId().equals("")){ // TODO: is this only the case for initialassigments? test 22 fails...
//                    String flattenedInitialAssignmentID = subModelID + initialAssignment.getId();
//                    initialAssignment.setId(flattenedInitialAssignmentID);
//                }
//
//                if (!initialAssignment.getMetaId().equals("")) {
//                    initialAssignment.setMetaId(subModelMetaID + initialAssignment.getMetaId());
//                }
//            }


            // 5
            // Transform every SIdRef and IDREF type value in the remaining objects of the submodel as follows:
            // a)
            // If the referenced object has been replaced by the application of a ReplacedBy or ReplacedElement construct,
            // change the SIdRef value (respectively, IDREF value) to the SId value (respectively, ID value)
            // of the object replacing it.
            // b)
            // If the referenced object has not been replaced, change the SIdRef and IDREF value by prepending “P”
            // to the original value.

            // 6
            // After performing the tasks above for all remaining objects, merge the objects of the remaining submodels
            // into a single model.
            // Merge the various lists (list of species, list of compartments, etc.)
            // in this step, and preserve notes and annotations as well as constructs from other SBML Level 3 packages.

            //model = modelOfSubmodel;
            //model = mergeModels(this.previousModel, modelOfSubmodel); // initiate model (?)

        }

        return model;
    }

    private Model flattenModel(Model modelOfSubmodel, String subModelID, String subModelMetaID) {

        for (Reaction reaction : modelOfSubmodel.getListOfReactions()) {
            reaction.setId(subModelID + reaction.getId());
            if (!reaction.getMetaId().equals("")) {
                reaction.setMetaId(subModelMetaID + reaction.getMetaId());
            }

        }

        for (Species species : modelOfSubmodel.getListOfSpecies()) {
            String flattenedSpeciesID = subModelID + species.getId();
            species.setId(flattenedSpeciesID);
            if (!species.getMetaId().equals("")) {
                species.setMetaId(subModelMetaID + species.getMetaId());
            }
        }

        for (Compartment compartment : modelOfSubmodel.getListOfCompartments()) {
            String flattenedCompartmentID = subModelID + compartment.getId();
            compartment.setId(flattenedCompartmentID);
            if (!compartment.getMetaId().equals("")) {
                compartment.setMetaId(subModelMetaID + compartment.getMetaId());
            }

        }

        for (Constraint constraint : modelOfSubmodel.getListOfConstraints()) {
            String flattenedContraintID = subModelID + constraint.getId();
            constraint.setId(flattenedContraintID);
            if (!constraint.getMetaId().equals("")) {
                constraint.setMetaId(subModelMetaID + constraint.getMetaId());
            }
        }

        for (Parameter parameter : modelOfSubmodel.getListOfParameters()) {
            String flattenedParameterID = subModelID + parameter.getId();
            parameter.setId(flattenedParameterID);
            if (!parameter.getMetaId().equals("")) {
                parameter.setMetaId(subModelMetaID + parameter.getMetaId());
            }

            if(parameter.isPackageEnabled(CompConstants.shortLabel)){
                CompSBasePlugin compSBasePlugin = (CompSBasePlugin) parameter.getExtension(CompConstants.shortLabel);
                CompModelPlugin compModelPlugin = (CompModelPlugin) modelOfSubmodel.getExtension(CompConstants.shortLabel);
                replaceElements(compModelPlugin, compSBasePlugin);
            }
        }

        for (Rule rule : modelOfSubmodel.getListOfRules()) {
            if(!rule.getId().equals("")){ // TODO: is this only the case for rules? test 22 fails...
                String flattenedRuleID = subModelID + rule.getId();
                rule.setId(flattenedRuleID);
            }
            if (!rule.getMetaId().equals("")) {
                rule.setMetaId(subModelMetaID + rule.getMetaId());
            }
        }

        for (UnitDefinition unitDefinition : modelOfSubmodel.getListOfUnitDefinitions()) {
            String flattenedUnitDefinitionID = subModelID + unitDefinition.getId();
            unitDefinition.setId(flattenedUnitDefinitionID);
            if (!unitDefinition.getMetaId().equals("")) {
                unitDefinition.setMetaId(subModelMetaID + unitDefinition.getMetaId());
            }
        }

        for (InitialAssignment initialAssignment : modelOfSubmodel.getListOfInitialAssignments()) {
            if(!initialAssignment.getId().equals("")){ // TODO: is this only the case for initialassigments? test 22 fails...
                String flattenedInitialAssignmentID = subModelID + initialAssignment.getId();
                initialAssignment.setId(flattenedInitialAssignmentID);
            }

            if (!initialAssignment.getMetaId().equals("")) {
                initialAssignment.setMetaId(subModelMetaID + initialAssignment.getMetaId());
            }
        }

        return modelOfSubmodel;
    }


    public static void main(String[] args) throws IOException, XMLStreamException {

        File file = new File("/Users/Christoph/Documents/Studium Bioinformatik/08 WiSe 17 18/HiWi/Code/jsbml/extensions/comp/test/org/sbml/jsbml/ext/comp/test/testdataforflattening/test1.xml");
        //File file = new File(args[0]);

        SBMLReader reader = new SBMLReader();
        SBMLDocument document = reader.readSBML(file);

        CompFlatteningConverter compFlatteningConverter = new CompFlatteningConverter();

        SBMLDocument flattendSBML = compFlatteningConverter.flatten(document);

        SBMLWriter.write(flattendSBML, System.out, ' ', (short) 2);


    }

}



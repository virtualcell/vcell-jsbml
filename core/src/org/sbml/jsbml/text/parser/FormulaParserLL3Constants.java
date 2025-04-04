/* Generated By:JavaCC: Do not edit this line. FormulaParserLL3Constants.java */
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
package org.sbml.jsbml.text.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface FormulaParserLL3Constants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int INTEGER = 3;
  /** RegularExpression Id. */
  int DIGIT = 4;
  /** RegularExpression Id. */
  int NUMBER = 5;
  /** RegularExpression Id. */
  int EXPNUMBER = 6;
  /** RegularExpression Id. */
  int SPLITTER = 7;
  /** RegularExpression Id. */
  int PLUS = 8;
  /** RegularExpression Id. */
  int POWER = 9;
  /** RegularExpression Id. */
  int MINUS = 10;
  /** RegularExpression Id. */
  int TIMES = 11;
  /** RegularExpression Id. */
  int DIVIDE = 12;
  /** RegularExpression Id. */
  int MODULO = 13;
  /** RegularExpression Id. */
  int OPEN_PAR = 14;
  /** RegularExpression Id. */
  int CLOSE_PAR = 15;
  /** RegularExpression Id. */
  int LEFT_BRACES = 16;
  /** RegularExpression Id. */
  int RIGHT_BRACES = 17;
  /** RegularExpression Id. */
  int LEFT_BRACKET = 18;
  /** RegularExpression Id. */
  int RIGHT_BRACKET = 19;
  /** RegularExpression Id. */
  int COMPARISON = 20;
  /** RegularExpression Id. */
  int BOOLEAN_LOGIC = 21;
  /** RegularExpression Id. */
  int AND = 22;
  /** RegularExpression Id. */
  int OR = 23;
  /** RegularExpression Id. */
  int XOR = 24;
  /** RegularExpression Id. */
  int NOT = 25;
  /** RegularExpression Id. */
  int LOG = 26;
  /** RegularExpression Id. */
  int STRING = 27;
  /** RegularExpression Id. */
  int IDCHAR = 28;
  /** RegularExpression Id. */
  int LETTER = 29;
  /** RegularExpression Id. */
  int EOL = 30;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "<INTEGER>",
    "<DIGIT>",
    "<NUMBER>",
    "<EXPNUMBER>",
    "<SPLITTER>",
    "\"+\"",
    "\"^\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"%\"",
    "\"(\"",
    "\")\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "<COMPARISON>",
    "<BOOLEAN_LOGIC>",
    "<AND>",
    "<OR>",
    "<XOR>",
    "<NOT>",
    "\"log\"",
    "<STRING>",
    "<IDCHAR>",
    "<LETTER>",
    "<EOL>",
  };

}

/*******************************************************************************
 * Copyright (c) <2013>, California Institute of Technology ("Caltech").  
 * U.S. Government sponsorship acknowledged.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright notice, this list of 
 *    conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright notice, this list 
 *    of conditions and the following disclaimer in the documentation and/or other materials 
 *    provided with the distribution.
 *  - Neither the name of Caltech nor its operating division, the Jet Propulsion Laboratory, 
 *    nor the names of its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY 
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER  
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON 
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package gov.nasa.jpl.mbee.model;

import gov.nasa.jpl.mbee.DocGen3Profile;
import gov.nasa.jpl.mbee.generator.DocumentValidator;
import gov.nasa.jpl.mbee.generator.GenerationContext;
import gov.nasa.jpl.mbee.lib.Debug;
import gov.nasa.jpl.mbee.lib.GeneratorUtils;
import gov.nasa.jpl.mbee.lib.Utils;
import gov.nasa.jpl.mbee.lib.Utils2;
import gov.nasa.jpl.mgss.mbee.docgen.docbook.DBParagraph;
import gov.nasa.jpl.mgss.mbee.docgen.docbook.DocumentElement;
import gov.nasa.jpl.mgss.mbee.docgen.docbook.From;
import gov.nasa.jpl.ocl.OclEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.activities.mdbasicactivities.InitialNode;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.ActivityNode;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

public class Paragraph extends Query {

    private String         text;
    private List<Property> stereotypeProperties;
    private From           fromProperty;
    
    private boolean        tryOcl = false;
    private DocumentValidator validator = null;
    private boolean        iterate = true;

    public InitialNode       bnode;
    public ActivityNode      activityNode;
    public GenerationContext context    = null;

    public GenerationContext makeContext() {
        ActivityNode n = null;
        if (bnode != null && bnode.getOutgoing().iterator().hasNext()) { // should
                                                                         // check
                                                                         // next
                                                                         // node
                                                                         // is
                                                                         // collect/filter
                                                                         // node
            n = bnode.getOutgoing().iterator().next().getTarget();
        }
        Stack<List<Object>> in = new Stack<List<Object>>();
        // in.add( targets );
        context = new GenerationContext(in, n, getValidator(), Application.getInstance().getGUILog());
        return context;
    }

    
    public Paragraph(String t) {
        text = t;
    }

    public Paragraph() {
    }

    public Paragraph(DocumentValidator dv) {
        this.validator = dv;
    }

    public void setText(String t) {
        text = t;
    }

    public String getText() {
        return text;
    }

    public void setStereotypeProperties(List<Property> p) {
        stereotypeProperties = p;
    }

    public List<Property> getStereotypeProperties() {
        return stereotypeProperties;
    }

    public void setFrom(From f) {
        fromProperty = f;
    }

    public From getFrom() {
        return fromProperty;
    }

    public DocumentValidator getValidator() {
        return validator;
    }

//    /* (non-Javadoc)
//     * @see gov.nasa.jpl.mbee.model.Query#parse()
//     */
//    @Override
//    public void parse() {
//        super.parse();
//    }

    protected void addOclParagraph(List<DocumentElement> res, Object oclExpression, Object context) {
        Object result =
                DocumentValidator.evaluate( oclExpression, context,
                                            getValidator(), true );
        if (OclEvaluator.instance.isValid() && result != null) {
            res.add( new DBParagraph( result ) );
        } else {
            // REVIEW -- tablestructure adds nothing in this case; is that right?                    
        }

    }
    
    /**
     * Create DocBook paragraph(s) for this Paragraph.
     * 
     * @param forViewEditor
     * @param outputDir
     * @return Return one or more DBParagraphs for docgen or the view editor
     *         based on properties of the Paragraph UML stereotype.
     *         <p>
     *         <code>
     *  O=tryOcl && T=gotText && R=gotTargets && S=gotStereotypeProperties && D=don't care <br><br>
     * 
     *  1 &nbsp;D && !T && !R &&  D: return nothing <br>
     *  2     !O && !T &&  R && !S: return a paragraph of documentation for each target <br>
     *  3     !O && !T &&  R &&  S: return a paragraph for each target-property pair  <br>
     *  4     !O &&  T &&  D &&  D: return a paragraph of the text, tied to an attribute (the Documentation attribute as set from parseView) of dgElement <br> 
     *  <br>
     *  5 &nbsp;O && !T &&  R && !S: return a paragraph of the evaluation of the documentation of each target as OCL on dgElement <br>
     *  6 &nbsp;O && !T &&  R &&  S: return a paragraph of the evaluation of each target-property as OCL on dgElement <br>
     *  7 &nbsp;O &&  T && !R &&  D: return a paragraph of the evaluation of the text as OCL on dgElement <br>
     *  8 &nbsp;O &&  T &&  R && !S: return a paragraph of the evaluation of the text as OCL on each target <br>
     *  9 &nbsp;O &&  T &&  R &&  S: return a paragraph of the evaluation of the text as OCL on each target-property pair <br>
     * </code>
     *         <p>
     * @see gov.nasa.jpl.mbee.model.Query#visit(boolean, java.lang.String)
     */
    @Override
    public List<DocumentElement> visit(boolean forViewEditor, String outputDir) {
        List<DocumentElement> res = new ArrayList<DocumentElement>();
        List< Reference > refs = new ArrayList< Reference >();
        if (getIgnore())
            return res;
        boolean gotText = getText() != null;
        boolean gotTargets = getTargets() != null;
        boolean gotStereotypeProperties = 
                !Utils2.isNullOrEmpty( getStereotypeProperties() );
        if (gotText && !tryOcl) { // ignoring targets -- should be none -- REVIEW
            // case 4: return a paragraph of the text, tied to an attribute (the
            // Documentation attribute as set from parseView) of dgElement
            if (forViewEditor || !getText().trim().equals(""))
                res.add(new DBParagraph(getText(), getDgElement(), getFrom()));
        } else if (gotText && !gotTargets) { // tryOcl must be true
            // case 7: return a paragraph of the evaluation of the text as OCL on dgElement 
            addOclParagraph( res, getText(), dgElement );
        } else if ( gotTargets ) {
            // build up a list of References before generating DBParagraphs
            for (Object o: targets) {
                Element e = null;
                if ( o instanceof Element ) {
                    e = (Element)o;
                } else if ( !tryOcl ) continue;
                Reference ref = null;
                if ( gotStereotypeProperties ) {
                    // for cases 3, 6, and 9
                    for (Property p: getStereotypeProperties()) {
                        ref = Reference.getPropertyReference(e, p);
                        refs.add( ref );
                    }
                } else {
                    if ( tryOcl && gotText) {
                        // for case 8
                        ref = new Reference( o );
                    } else {
                        // for cases 2 and 5
                        ref = new Reference(e, From.DOCUMENTATION, ModelHelper.getComment(e));
                    }
                    refs.add( ref );
                }
            }
            if ( tryOcl && !iterate && gotText ) {
                // for cases 8 & 9 when !iterate
                // apply text as OCL to the collection as a whole
                ArrayList<Object> results = new ArrayList< Object >();
                for ( Reference r : refs ) {
                    results.add( r.getResult() );
                }
                addOclParagraph( res, getText(), results );
            } else { 
                if ( !iterate ) {
                    Debug.error( false, "The iterate property should be true when not using OCL or when the OCL is in the targets instead of the body: " + dgElement );
                    // REVIEW -- create a validation violation instead?
                    // getValidator().addViolationIfUnique( rule, element, comment, reported ); // no public rule to reference!
                }
                // creating paragraph for each reference
                for ( Reference r : refs ) {
                    if ( !tryOcl ) { // gotText is false
                        // cases 2 & 3: return a paragraph for each
                        // target-property pair (3) or for each target's
                        // documentation (2)
                        res.add( new DBParagraph( r.getResult(),
                                                  r.getElement(), r.getFrom() ) );
                    } else {
                        if ( gotText ) {
                            // cases 8 & 9: return a paragraph of the evaluation
                            // of the text as OCL on each target-property pair (9)
                            // or on each target (8)
                            addOclParagraph( res, getText(), r.getResult() );
                        } else {
                            // cases 5 & 6: add a paragraph of the evaluation of
                            // the value of each target-property (6) or of each target's
                            // documentation (5) as OCL on dgElement
                            addOclParagraph( res, r.getResult(), dgElement );
                        }
                    }
                }
            }
        } // else case 1: gotText and gotTarget are both false, so return nothing 
        
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize() {
        String body = (String)GeneratorUtils.getObjectProperty(dgElement, DocGen3Profile.paragraphStereotype,
                "body", null);
        setText(body);
        Object doOcl = GeneratorUtils.getObjectProperty(dgElement, DocGen3Profile.paragraphStereotype,
                                                               "evaluateOcl", null);
        if ( doOcl != null ) {
            tryOcl = Utils.isTrue( doOcl, true );
        }
        Object iter = GeneratorUtils.getObjectProperty(dgElement, DocGen3Profile.paragraphStereotype,
                                                        "iterate", null);
        if ( iter != null ) {
            iterate = Utils.isTrue( iter, false ); // TODO -- use this!
        }
        setStereotypeProperties((List<Property>)GeneratorUtils
                .getListProperty(dgElement, DocGen3Profile.stereotypePropertyChoosable,
                        "stereotypeProperties", new ArrayList<Property>()));
    }


}

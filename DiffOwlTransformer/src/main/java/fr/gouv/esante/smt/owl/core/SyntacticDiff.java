/*
 * Copyright (c) 2012 Czech Technical University in Prague.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package fr.gouv.esante.smt.owl.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.gouv.esante.smt.owl.core.ProgressListener;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.AxiomSubjectProviderEx;

import fr.gouv.esante.smt.owl.core.AxiomChanges;
import fr.gouv.esante.smt.owl.core.OWLChangeType;
import fr.gouv.esante.smt.owl.core.SyntacticAxiomChange;
import fr.gouv.esante.smt.owl.core.AbstractDiff;
import fr.gouv.esante.smt.owl.core.OntologyHandler;

public class SyntacticDiff  extends AbstractDiff {

    private static Logger LOG = Logger.getLogger(SyntacticDiff.class.getName());

    private SyntacticDiffOutput output = null;

    public SyntacticDiff(OntologyHandler ontologyHandler) {
        this(ontologyHandler, null);
    }

    public SyntacticDiff(OntologyHandler ontologyHandler, ProgressListener listener) {
        super(ontologyHandler, listener);
    }

    @Override
    public SyntacticDiffOutput diff() {

        if (output != null) {
            return output;
        }

        final OWLOntology original = ontologyHandler.getOriginalOntology();
        final OWLOntology update = ontologyHandler.getUpdateOntology();

        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Original # of all axioms="
                    + original.getAxiomCount()
                    + ", # of logical axioms="
                    + original.getLogicalAxiomCount());
            LOG.info("Update # of all axioms="
                    + update.getAxiomCount()
                    + ", # of logical axioms="
                    + update.getLogicalAxiomCount());
            
        }

        output = new SyntacticDiffOutput();

        reset(original.getAxiomCount() + update.getAxiomCount());
        
        List<String> listeConceptIRI = new ArrayList<String>();
        List<String> listeConceptIRIS = new ArrayList<String>();
        
        for (final OWLAxiom a : update.getAxioms()) {
       	 if (original.containsAxiom(a)) {
       		 listeConceptIRIS.add(getConceptIriDeclaration(AxiomSubjectProviderEx.getSubject(a).toString()));
            }
       	
       }
        
        
        

        for (final OWLAxiom a1 : original.getAxioms()) {
            updateProgress();
            if (update.containsAxiom(a1)) {
            	
            	listeConceptIRI.add(getConceptIriDeclaration(AxiomSubjectProviderEx.getSubject(a1).toString()));
                continue;
            }
            
              //System.out.println(a1.getNNF());
              output.getInOriginal().add(a1);
                            
              
              //Code AJOUTE
              a1.accept(new OWLAxiomVisitor() {
    	    	  
    	    	  
    	    	 public void visit(OWLSubClassOfAxiom arg0) {
    	              
    	    		  String conceptIri = getConceptIriSubClassOf(arg0.toString()).get(0);
    	    		  String oldValue = getConceptIriSubClassOf(arg0.toString()).get(1);
    	    		  String relation = "SubClassOf";
    	    		  
    	    		
    	    		  if(listeConceptIRIS.contains(conceptIri)) {
    	    		   output.getListaxiomsChanges().put(conceptIri+"|"+relation+"|"+oldValue, new AxiomChanges("Suppression", oldValue, ""));
    	    		  } 
    	    		   
    	    		  
    	    		  

    	            }
    	    	  
    	    	  
    	    	  
    	    	  public void visit(OWLDeclarationAxiom arg0) {
    	    		  
    	             String conceptIri = getConceptIriDeclaration(AxiomSubjectProviderEx.getSubject(a1).toString());
     	    		    
   	                 output.getListaxiomsChanges().put(conceptIri+"|"+" "+"|"+" ", new AxiomChanges("Suppression", "", ""));
 
     	    		   
     	    		 
    	            }
    	    	  
    	    	  
    	    	  public void visit(OWLAnnotationAssertionAxiom arg0) {
    	    		    
    	    		    String conceptIri ="";
    	                
    	    		    if (arg0.getSubject().isIRI()) {
    	                	
    	                	
    	                	conceptIri = AxiomSubjectProviderEx.getSubject(a1).toString();
    	                	
    	                }
    	    		    
    	    		    String prop = "";
      	    	        if(arg0.getValue().toString().contains("@")) {
      	    		    
      	    	        	
      	    	        	//System.out.println("value "+ arg0.getValue().toString().split("@")[1]);
      	    	        	
      	    	        	prop = "@"+arg0.getValue().toString().split("@")[1];
      	    	        	
      	    	        }
    	    		   
    	                
    	                OWLAnnotation annotation1 = arg0.getAnnotation();
      	    		    
      	    		   
    	                
    	                if(listeConceptIRIS.contains(conceptIri)) {
    	                	
    	                
    	                if("liste".equals(getRelationValue(annotation1, prop).get(2))) {	
    	                 output.getListaxiomsChanges().put(conceptIri+"|"+getRelationValue(annotation1, prop).get(0).trim()+"|"+getRelationValue(annotation1, prop).get(1),
    	                		 new AxiomChanges("Suppression", getRelationValue(annotation1, prop).get(1), ""));
    	                }else {
    	                	
    	                	 output.getListaxiomsChanges().put(conceptIri+"|"+getRelationValue(annotation1, prop).get(0),
        	                		 new AxiomChanges("Suppression", getRelationValue(annotation1, prop).get(1), ""));	
    	                }
    	                 
    	                }
      	    		   
    	            }
    	    	  
    	    	  
    	    	  
			});
            
            
             
            
        }

        for (final OWLAxiom a : update.getAxioms()) {
            updateProgress();
            if (original.containsAxiom(a)) {
                continue;
            }

            output.getInUpdate().add(a);
            
          //Code AJOUTE
            a.accept(new OWLAxiomVisitor() {
  	    	  
  	    	  
  	    	  public void visit(OWLSubClassOfAxiom arg0) {
  	              
  	    		  String conceptIri = getConceptIriSubClassOf(arg0.toString()).get(0);
  	    		  String newValue = getConceptIriSubClassOf(arg0.toString()).get(1);
  	    		  String relation = "SubClassOf";
  	    		 
  	    		
  	    		if(listeConceptIRIS.contains(conceptIri)) {
  	    			output.getListaxiomsChanges().put(conceptIri+"|"+relation+"|"+newValue, new AxiomChanges("Ajout", "", newValue));
  	    		}
  	    		
  	    		   
  	         }
  	    		  
  
  	    	  
  	    	    public void visit(OWLDeclarationAxiom arg0) {
  	                
  	    	    	
  	              String conceptIri = getConceptIriDeclaration(AxiomSubjectProviderEx.getSubject(a).toString());
   	    		     
   	    		  output.getListaxiomsChanges().put(conceptIri+"|"+" "+"|"+" ", new AxiomChanges("Ajout", "", ""));
   	    		     
   	    		   
  	                
  	            }
  	    	  
  	    	  
  	    	  public void visit(OWLAnnotationAssertionAxiom arg0) {
  	    		    
  	    		    String conceptIri ="";
  	                
  	    		    if (arg0.getSubject().isIRI()) {
  	                	
  	                	conceptIri = AxiomSubjectProviderEx.getSubject(a).toString();
  	                	
  	                }
  	    		    
  	    		  String prop = "";
  	    	        if(arg0.getValue().toString().contains("@")) {
  	    		    
  	    	        	
  	    	        //	System.out.println("value "+ arg0.getValue().toString().split("@")[1]);
  	    	        	
  	    	        	prop = "@"+arg0.getValue().toString().split("@")[1];
  	    	        	
  	    	        }
    	    		
  	                OWLAnnotation annotation1 = arg0.getAnnotation();
  	                
  	                
  	              //  System.out.println("* "+getRelationValue(annotation1, prop).get(0));
  	               // System.out.println("** "+getRelationValue(annotation1, prop).get(1));
  	               // System.out.println("*** "+getRelationValue(annotation1, prop).get(2));
  	                
  	               
  	                if(output.getListaxiomsChanges().containsKey(conceptIri+"|"+getRelationValue(annotation1, prop).get(0))
  	                		&& "nonliste".equals(getRelationValue(annotation1, prop).get(2)))
  	                			 {
  	                	
  	                	output.getListaxiomsChanges().get(conceptIri+"|"+getRelationValue(annotation1,prop).get(0)).setNew_Value(getRelationValue(annotation1,prop).get(1));
  	                	output.getListaxiomsChanges().get(conceptIri+"|"+getRelationValue(annotation1,prop).get(0)).setType("Modification");;

  	                	
  	                }else {
  	                
  	                 if(listeConceptIRI.contains(conceptIri)) {	
  	                
  	                		 
	                  output.getListaxiomsChanges().put(conceptIri+"|"+getRelationValue(annotation1,prop).get(0).trim()+"|"+getRelationValue(annotation1, prop).get(1)
	                		  , new AxiomChanges("Ajout", "",  getRelationValue(annotation1,prop).get(1)));
                    
  	                 }
	                
  	                }
    	    		    
    	    		 
  	            }
  	    	  
  	    	  
  	    	  
			});
               
            
        }

        return output;
    }
    
    
  //Code AJOUTE 
   private List<String> getRelationValue(OWLAnnotation annotation1, String prop) {
		
	   String annotation = write(annotation1, false, false);
	   
	 //  System.out.println("**annotaion "+ annotation);
	   
	   String[] tabE = annotation.split("\\^\\^");
	   
	   String[] tabG = tabE[0].split("\"");
	    
	    
		List<String> iriValue = new ArrayList<String>();
		
		
		
		iriValue.add(tabG[0].split(" ")[0]+prop);
		try {
		iriValue.add(tabG[1]);
		
		iriValue.add("nonliste");
		}catch(Exception e) {
		//iriValue.add("A verifier");
		iriValue.add(getValueURI(annotation1.toString()));
		iriValue.add("liste");

		}
		return iriValue;

    }
   
 //Code AJOUTE
   private String getValueURI(String annotation) {
	      
	   String iri = annotation.replace("Annotation(<", "").replace(">", "").replace("<", "").replace(")", "");
	   String[] iriSplit = iri.split(" ");
	   String iriCode =  iriSplit[1];
	   return iriCode;
   }
    
 //Code AJOUTE
    private String getConceptIriDeclaration(String declarationIRI) {
		
    	String iri = declarationIRI.replace("<", "").replace(">", "");
    	return iri;

    }
    
    
  //Code AJOUTE
    private List<String> getConceptIriSubClassOf(String declarationSubClassOf) {
		
    	
    	String iri = declarationSubClassOf.replace("SubClassOf(<", "").replace(">", "").replace("<", "").replace(")", "");
    	
    	String[] iriSplit = iri.split(" ");
    			
		List<String> iriValue = new ArrayList<String>();
	
		iriValue.add(iriSplit[0]);
		iriValue.add(iriSplit[1]);		
		
		return iriValue;

    }
    
    
    
    
    
    private static String getName(final IRI uri, final boolean fullURI) {
        if (fullURI) {
            return uri.toString();
        } else if (uri.getFragment() == null) {
            return uri.toURI().getPath().substring(
                    uri.toURI().getPath().lastIndexOf('/') + 1);
        } else {

            return uri.getFragment();
        }
    }
    
    
    public String write(OWLAnnotation arg0, final boolean fullURI, final boolean html) {
        final StringBuffer b = new StringBuffer();

       // b.append(keyword("Annotation", html)).append(" ");
        b.append(getName(arg0.getProperty().getIRI(), fullURI));

        arg0.getValue().accept(new OWLAnnotationValueVisitor() {

            public void visit(OWLLiteral arg0) {
                b.append(" ").append(write(arg0));
            }

            public void visit(OWLAnonymousIndividual arg0) {
                b.append(" ").append(write(arg0, fullURI, html));
            }

            public void visit(IRI arg0) {
                b.append(" ").append(getName(arg0, fullURI));
            }
        });

        return b.toString();
    }
    
    public String write(OWLIndividual individual, boolean fullURI, boolean html) {
        final StringBuffer b = new StringBuffer();

        if (html) {
            b.append("<i>");
        }

        if (individual.isAnonymous()) {
            b.append(individual.asOWLAnonymousIndividual().getID().getID());
        } else {
            b.append(getName(individual.asOWLNamedIndividual().getIRI(), fullURI));
        }

        if (html) {
            b.append("</i>");
        }

        return b.toString();
    }
    
    public String write(final OWLLiteral c) {
        if (c.isRDFPlainLiteral()) {
            return "\"" + c.getLiteral() + "\"" + ((c.getLang() != null && !c.getLang().isEmpty()) ? "@" + c.getLang() : "");
        } else {
            return "\"" + c.getLiteral() + "\"^^" + write(c.getDatatype());
        }
    }
    
    public String write(final OWLDataRange dt) {
        return dt.toString();
    }
    
    public String write(final OWLClassExpression concept,
            final boolean fullURI, final boolean html) {
if (concept == null) {
throw new IllegalArgumentException();
}

final StringBuilder b = new StringBuilder();

concept.accept(new OWLClassExpressionVisitor() {

public void visit(OWLClass arg0) {
    if (arg0.isOWLThing()) {
        b.append(keyword("Thing", html));
    } else if (arg0.isOWLNothing()) {
        b.append(keyword("Nothing", html));
    } else {
        b.append(getName(arg0.getIRI(), fullURI));
    }
}

public void visit(OWLObjectIntersectionOf arg0) {
    boolean first = true;
    boolean previous = false;

    for (final OWLClassExpression d : arg0.getOperands()) {
        if (!first) {
            b.append(" ");
            b.append(d.isAnonymous() && !previous ? keyword("that",
                    html) : keyword("and", html));
            b.append(" ");
        }
        previous = d.isAnonymous();
        first = false;
        b.append(write(d, fullURI, html));

    }
}

public void visit(OWLObjectUnionOf arg0) {
    boolean first = true;

    for (final OWLClassExpression d : arg0.getOperands()) {
        if (!first) {
            b.append(" ");
            b.append(keyword("or", html));
            b.append(" ");
        }
        first = false;
        b.append(write(d, fullURI, html));
    }
}

public void visit(OWLObjectComplementOf arg0) {
    b.append(keyword("not", html)).append(" ");
    b.append(write(arg0.getOperand(), fullURI, html));
}

public void visit(OWLObjectSomeValuesFrom arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("some", html)).append(" ").append(
            write(arg0.getFiller(), fullURI, html));
}

public void visit(OWLObjectAllValuesFrom arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("only", html)).append(" ").append(
            write(arg0.getFiller(), fullURI, html));
}

public void visit(OWLObjectHasValue arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("value", html)).append(" ").append(
            write(arg0.getValue(), fullURI, html));
}

public void visit(OWLObjectMinCardinality arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("min", html)).append(" ").append(
            arg0.getCardinality());

    if (!arg0.getFiller().isOWLThing()) {
        b.append(" ")
                .append(write(arg0.getFiller(), fullURI, html));
    }
}

public void visit(OWLObjectExactCardinality arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("exactly", html)).append(" ").append(
            arg0.getCardinality());

    if (!arg0.getFiller().isOWLThing()) {
        b.append(" ")
                .append(write(arg0.getFiller(), fullURI, html));
    }
}

public void visit(OWLObjectMaxCardinality arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("max", html)).append(" ").append(
            arg0.getCardinality());

    if (!arg0.getFiller().isOWLThing()) {
        b.append(" ")
                .append(write(arg0.getFiller(), fullURI, html));
    }
}

public void visit(OWLObjectHasSelf arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ");
}

public void visit(OWLObjectOneOf arg0) {
    b.append("{");
    boolean first = true;

    for (final OWLIndividual d : arg0.getIndividuals()) {
        if (!first) {
            b.append(", ");
        }
        first = false;
        b.append(write(d, fullURI, html));
    }

    b.append("}");
}

public void visit(OWLDataSomeValuesFrom arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("some", html)).append(" ").append(
            write(arg0.getFiller()));
}

public void visit(OWLDataAllValuesFrom arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("only", html)).append(" ").append(
            write(arg0.getFiller()));
}

public void visit(OWLDataHasValue arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("value", html)).append(" ").append(
            write(arg0.getValue()));
}

public void visit(OWLDataMinCardinality arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("min", html)).append(" ").append(
            arg0.getCardinality());

    b.append(" ").append(write(arg0.getFiller()));
}

public void visit(OWLDataExactCardinality arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("exactly", html)).append(" ").append(
            arg0.getCardinality());

    b.append(" ").append(write(arg0.getFiller()));
}

public void visit(OWLDataMaxCardinality arg0) {
    b.append(write(arg0.getProperty(), fullURI)).append(" ")
            .append(keyword("max", html)).append(" ").append(
            arg0.getCardinality());
    b.append(" ").append(write(arg0.getFiller()));
}
});

return b.toString();
}
    
    

    private String keyword(final String s, final boolean html) {
        if (html) {
            return "<b>" + s + "</b>";
        }
        return s;
    }
    
    
    public String write(final OWLPropertyExpression relation,
            final boolean fullURI) {
if (relation == null) {
throw new IllegalArgumentException("Relation is null.");
}

final StringBuilder b = new StringBuilder();

relation.accept(new OWLPropertyExpressionVisitor() {

public void visit(OWLObjectProperty arg0) {
    b.append(getName(arg0.getIRI(), fullURI));
}

public void visit(OWLObjectInverseOf arg0) {
    b.append(write(arg0.getInverse(), fullURI));
}

public void visit(OWLDataProperty arg0) {
    b.append(getName(arg0.getIRI(), fullURI));
}

});

return b.toString();
}
    
    
    
    
}

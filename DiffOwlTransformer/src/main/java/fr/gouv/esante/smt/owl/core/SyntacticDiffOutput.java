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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.gouv.esante.smt.owl.core.AxiomChanges;
import fr.gouv.esante.smt.owl.core.OWLAxiomChange;
import fr.gouv.esante.smt.owl.core.OWLChange;
import fr.gouv.esante.smt.owl.core.SyntacticAxiomChange;
import fr.gouv.esante.smt.owl.core.OWLDiffOutput;
import fr.gouv.esante.smt.owl.core.ManchesterSyntax;
import fr.gouv.esante.smt.owl.core.Syntax;
import org.semanticweb.owlapi.model.OWLAxiom;

public class SyntacticDiffOutput implements OWLDiffOutput {

    
    private Set<AxiomChanges> axiomsChanges = new HashSet<AxiomChanges>();
    
    private static HashMap<String, AxiomChanges> listaxiomsChanges = new HashMap<String, AxiomChanges>();


    private Syntax syntax;

    private Set<OWLAxiom> inOriginal;
    private Set<OWLAxiom> inUpdate;

    public SyntacticDiffOutput() {
        this(new ManchesterSyntax());
    }

    public SyntacticDiffOutput(Syntax syntax) {
        this.syntax = syntax;
        inOriginal = new HashSet<OWLAxiom>();
        inUpdate = new HashSet<OWLAxiom>();
    }

   

   

    public Set<OWLAxiom> getInOriginal() {
        return inOriginal;
    }

    public Set<OWLAxiom> getInUpdate() {
        return inUpdate;
    }
    
    
    
    

    public String outputToString(String prefix, Set<OWLAxiom> axioms) {
        String out = "";
        out += prefix + "additional axioms:\n--------\n\n";
        for (Iterator<OWLAxiom> i = axioms.iterator(); i.hasNext(); ) {
            OWLAxiom ax = i.next();
            out += prefix + "  ";
            out += syntax.writeAxiom(ax, false, null, false);
            out += "\n";
        }
        return out;
    }

    public String outputToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nOWLDiff - Syntactic Diff\n\n");

        String outOrig = outputToString("  ", inOriginal);
        if (!outOrig.isEmpty()) {
            builder.append(String.format("original:\n--------\n\n%s", outOrig));
        }

        String outUpdate = outputToString("  ", inUpdate);
        if (!outUpdate.isEmpty()) {
            builder.append(String.format("\n update:\n--------\n\n%s", outUpdate));
        }
        return builder.toString();
    }

	@Override
	public Set<AxiomChanges> getOWLAxiomsChanges() {
		
		return axiomsChanges;
	}

	public HashMap<String, AxiomChanges> getListaxiomsChanges() {
		return listaxiomsChanges;
	}

	
	
	
}

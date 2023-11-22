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
import java.util.Set;

import fr.gouv.esante.smt.owl.core.AxiomChanges;
import fr.gouv.esante.smt.owl.core.OWLChange;
import fr.gouv.esante.smt.owl.core.Syntax;

public interface OWLDiffOutput {
    
    Set<AxiomChanges> getOWLAxiomsChanges();

    String outputToString();
    
    HashMap<String, AxiomChanges> getListaxiomsChanges();
    
}
		
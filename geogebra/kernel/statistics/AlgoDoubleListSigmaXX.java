/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.statistics;

import geogebra.kernel.GeoList;
import geogebra.kernel.Construction;

/**
 * Sum of squares of a list
 * @author Michael Borcherds
 * @version 2008-02-23
 */

public class AlgoDoubleListSigmaXX extends AlgoStats2D {

	private static final long serialVersionUID = 1L;

	public AlgoDoubleListSigmaXX(Construction cons, String label, GeoList geoListx, GeoList geoListy) {
        super(cons,label,geoListx,geoListy,AlgoStats2D.STATS_SIGMAXX);
    }

    protected String getClassName() {
        return "AlgoDoubleListSigmaXX";
    }
}

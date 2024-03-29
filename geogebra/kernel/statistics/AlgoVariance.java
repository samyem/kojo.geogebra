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
import geogebra.kernel.statistics.AlgoStats1D;


/**
 * Variance of a list
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoVariance extends AlgoStats1D {

	private static final long serialVersionUID = 1L;

	public AlgoVariance(Construction cons, String label, GeoList geoList) {
        super(cons,label,geoList,AlgoStats1D.STATS_VARIANCE);
    }

    protected String getClassName() {
        return "AlgoVariance";
    }
}

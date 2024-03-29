/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.main;

import geogebra.kernel.GeoElement;

public interface View {
	public void add(GeoElement geo);
	public void remove(GeoElement geo);
	public void rename(GeoElement geo);
	public void update(GeoElement geo);	 
	public void updateAuxiliaryObject(GeoElement geo);
	public void repaintView();   
	public void reset(); 
	public void clearView();
}

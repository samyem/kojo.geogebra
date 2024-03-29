/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


import java.util.ArrayList;



public class GeoLocus extends GeoElement implements Path {

	private static final long serialVersionUID = 1L;

	public static final int MAX_PATH_RUNS = 10;
				
	private boolean defined;		
	
	// coords of points on locus
	private ArrayList myPointList;		
	
	public GeoLocus(Construction c) {
		super(c);				
		myPointList = new ArrayList(500);		
		setAlgebraVisible(false);
	}  
			
	public GeoElement copy() {
		GeoLocus ret = new GeoLocus(cons);
		ret.set(this);
		return ret; 
	}

	public void set(GeoElement geo) {
		GeoLocus locus = (GeoLocus) geo;			
		defined = locus.defined;		
		
		myPointList.clear();
		myPointList.addAll(locus.myPointList);
	}
		
	/**
	 * Number of valid points in x and y arrays.
	 * @return
	 */
	final public int getPointLength() {
		return myPointList.size();
	}	
	
	public void clearPoints() {		
		myPointList.clear();				
	}
	
	/**
	 * Adds a new point (x,y) to the end of the point list of this locus.	 
	 * @param x
	 * @param y
	 * @param lineTo: true to draw a line to (x,y); false to only move to (x,y)
	 */
	public void insertPoint(double x, double y, boolean lineTo) { 
		myPointList.add(new MyPoint(x, y, lineTo));	
	}
	
	public ArrayList getMyPointList() {
		return myPointList;
	}
	
	public String toString() {		
		return getLabel();
	}	

	protected boolean showInAlgebraView() {
		return false;
	}

	protected String getClassName() {
		return "GeoLocus";
	}
	
    protected String getTypeString() {
		return "Locus";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_LOCUS;
    }
	
	public boolean showToolTipText() {
		return true;
	}
	
	/**
	* returns all class-specific xml tags for getXML
	*/
   	protected String getXMLtags() {   
   		//return super.getXMLtags();
	   	StringBuffer sb = new StringBuffer();
	   	sb.append(getXMLvisualTags());		
	   	sb.append(getLineStyleXML());
		return sb.toString();   
   	}

	public void setMode(int mode) {
	}

	public int getMode() {	
		return 0;
	}

	public boolean isDefined() {
		return defined;
	}
	
	public void setDefined(boolean flag) {
		defined = flag;
	}

	public void setUndefined() {
		defined = false;		
	}

	public String toValueString() {
		return "";
	}

	protected boolean showInEuclidianView() {
		return isDefined();
	}	
	
	public boolean isGeoLocus() {
		return true;
	}

	public PathMover createPathMover() {
		return new PathMoverLocus(this);
	}

	public double getMaxParameter() {		
		return myPointList.size() - 1;
	}

	public double getMinParameter() {		
		return 0;
	}

	public boolean isClosedPath() {
		if (myPointList.size() > 0) {
			MyPoint first = (MyPoint) myPointList.get(0);
			MyPoint last = (MyPoint) myPointList.get(myPointList.size() - 1);
			return first.isEqual(last.x, last.y);
		} else
			return false;
	}

	public boolean isOnPath(GeoPoint P, double eps) {
		MyPoint closestPoint = getClosestPoint(P);
		if (closestPoint != null) {
			return Math.sqrt(closestPointDist) < eps;
		} else
			return false;
	}
	
	/**
	 * Returns the point of this locus that is closest
	 * to GeoPoint P.
	 */
	private MyPoint getClosestPoint(GeoPoint P) {		
		int size = myPointList.size();
		if (size == 0)
			return null;
		
		// can't use P.inhomX, P.inhomY in path updating yet, so compute them
		double px = P.x/P.z;
		double py = P.y/P.z;
		
		// handle undefined case: probably caused by path that became undefined
		if (Double.isNaN(px) || Double.isNaN(py)) {	
			// use previous path parameter
			double pathParameter = P.getPathParameter().t;
			if (Double.isNaN(pathParameter) || Double.isInfinite(pathParameter))
				closestPointIndex = 0;
			else {
				closestPointIndex = (int) pathParameter;				
				if (closestPointIndex < 0)
					closestPointIndex = 0;
				else if (closestPointIndex > size)
					closestPointIndex = size - 1;
			}			
			return (MyPoint) myPointList.get(closestPointIndex);
		}
		
		// search for closest point on path
		MyPoint closestPoint  = null;
		closestPointDist = Double.MAX_VALUE;
		closestPointIndex = -1;
		
		
		
		// search for closest point		
		for (int i=0; i < size; i++) {
			MyPoint locusPoint = (MyPoint) myPointList.get(i);
			double dist = locusPoint.distSqr(px, py);
			if (dist < closestPointDist) {
				closestPointDist = dist;
				closestPointIndex = i;
				closestPoint = locusPoint;
			}
		}
		
		return closestPoint;
	}
	private double closestPointDist;
	private int closestPointIndex;

	public void pathChanged(GeoPoint P) {	
		// find closest point on changed path to P
		pointChanged(P);
	}

	public void pointChanged(GeoPoint P) {
		// find closest point on path
		MyPoint closestPoint = getClosestPoint(P);
		
		PathParameter pp = P.getPathParameter();
		if (closestPoint != null) {
			P.x = closestPoint.x;
			P.y = closestPoint.y;
			P.z = 1.0;
			pp.t = closestPointIndex;					
		}		
		else {
			// remember last path parameter, don't delete it
			//pp.t = Double.NaN;			
		}		
	}
	
	public boolean isPath() {
		return true;
	}
	
    // Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise use equals() method
		return false;
		// TODO?
		//if (geo.isGeoLocus()) return xxx else return false;
	}

}

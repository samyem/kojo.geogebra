/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;


public class AlgoLineBisector extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint A, B;  // input    
    private GeoLine  g;     // output   
    
    // temp
    private double ax, ay, bx, by;
    private GeoPoint midPoint;
        
    /** Creates new AlgoLineBisector */
    AlgoLineBisector(Construction cons, String label,GeoPoint A,GeoPoint B) {
        super(cons);
        this.A = A;
        this.B = B;        
        g = new GeoLine(cons); 
        midPoint = new GeoPoint(cons);
        g.setStartPoint(midPoint);
        setInputOutput(); // for AlgoElement
        
        // compute bisector of A, B
        compute();      
        g.setLabel(label);
    }   
    
    protected String getClassName() {
        return "AlgoLineBisector";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;
        input[1] = B;
        
        output = new GeoElement[1];        
        output[0] = g;        
        setDependencies(); // done by AlgoElement
    }    
    
    GeoLine getLine() { return g; }
    GeoPoint getA() { return A; }
    GeoPoint getB() { return B; }
    GeoPoint getMidPoint() {
        return midPoint;
    }
    
    // line through P normal to v
    protected final void compute() { 
        // get inhomogenous coords
        ax = A.inhomX;
        ay = A.inhomY;
        bx = B.inhomX;
        by = B.inhomY;
         
        // comput line
        g.x = ax - bx;
        g.y = ay - by;
        midPoint.setCoords( (ax + bx), (ay + by), 2.0);   
        g.z = -(midPoint.x * g.x + midPoint.y * g.y)/2.0;     
    }   
    

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("LineBisectorAB",A.getLabel(),B.getLabel()));
        
        return sb.toString();
    }
}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Command.java
 *
 * Created on 05. September 2001, 12:05
 */

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.ArrayList;
import java.util.HashSet;
/**
 *
 * @author  Markus
 * @version 
 */
public class Command extends ValidExpression 
implements ExpressionValue {
    
     
    // list of arguments
    private ArrayList args = new ArrayList();
    private String name; // internal command name (in English)
    
    private Kernel kernel;
    private Application app;
    private GeoElement[] evalGeos; // evaluated Elements
    private Macro macro; // command may correspond to a macro 
    
    /** Creates new Command */
    public Command(Kernel kernel, String name, boolean translateName) {    
        this.kernel = kernel;
        app = kernel.getApplication();
            
        if (translateName) {           
            //  translate command name to internal name
            this.name = app.translateCommand(name);           
        } else {
            this.name = name;
        }               
    }
    
    public Kernel getKernel() {
        return kernel;
    }
    
    public void addArgument(ExpressionNode arg) {
        args.add( arg );
    }
    
    /**
     * Returns the name of the variable at the specified argument position.
     * If there is no variable name at this position, null is returned.
     */
    public String getVariableName(int i) {
    	if (i >= args.size())
    		return null;
    	
    	ExpressionValue ev = ((ExpressionNode) args.get(i)).getLeft();
    	if (ev instanceof Variable)
    		return ((Variable) ev).getName();
    	else if (ev instanceof GeoElement) {
    		// XML Handler looks up labels of GeoElements
    		// so we may end up having a GeoElement object here
    		// return its name to use as local variable name
    		GeoElement geo = ((GeoElement) ev);
    		if (geo.isLabelSet())
    			return ((GeoElement) ev).getLabel();
    	}     	    	
    	
    	return null;
    }
    
    public ExpressionNode [] getArguments() {
    	int size = args.size();
        ExpressionNode [] ret = new ExpressionNode[size];
        
        for (int i=0; i < args.size(); i++) {        
            ret[i] = (ExpressionNode) args.get(i);        
        }
        return ret;                
    }
    
  
    public ExpressionNode getArgument(int i) {
        return (ExpressionNode) args.get(i);
    }
    
    public void setArgument(int i, ExpressionNode en) {
        args.set(i, en);
    }
    
    public int getArgumentNumber() {
        return args.size();
    }
    
    public String getName() {
        return name;
    }
    
    final public String toString() {
    	
//    	// Jasymca cannot cope with GeoGebra command expression, 
//    	// e.g. Element(b, lb1 - 2 + 1) * x^(2), so we use the value string intead of
//    	// the command 
//    	if (casPrintForm == ExpressionNode.STRING_TYPE_JASYMCA) {
//    		return toValueString();
//    	}
    	
        int size = args.size();
        StringBuffer sb = new StringBuffer(); 
        if (kernel.isTranslateCommandName()) {
            sb.append(app.getCommand(name));                       
        } else {
            sb.append(name);
        }        
        
        // take "(" or "[" for command parentheses   
    	int casPrintForm = kernel.getCASPrintForm();   
        
        switch (casPrintForm) {
			case ExpressionNode.STRING_TYPE_MATH_PIPER:
				 sb.append('(');
				 break;
										
			default:
				 sb.append('[');
		}
        	
        for (int i = 0; i < size - 1; i++) {
            sb.append( args.get(i) );
            sb.append(", ");
        }
        if (size > 0)
        	sb.append( args.get(size-1));
        
        switch (casPrintForm) {
			case ExpressionNode.STRING_TYPE_MATH_PIPER:
				 sb.append(')');
				 break;
										
			default:
				 sb.append(']');
		}
        
        return sb.toString();
    }
    
    public GeoElement [] evaluateMultiple() {
            GeoElement [] geos = null;
            geos = kernel.getAlgebraProcessor().processCommand(this, false);          
            return geos;
     }
    
    
    public ExpressionValue evaluate() {
        // not yet evaluated: process command
        if (evalGeos == null) 
        	evalGeos = evaluateMultiple(); 
      
         if (evalGeos != null && evalGeos.length >= 1) {
            return evalGeos[0]; 
         } else {
            Application.debug("invalid command evaluation: " + name);
            throw new MyError(app, app.getError("InvalidInput") + ":\n" + this);                                                                 
         }          
    }
    
    public void resolveVariables() {
    	// nothing to do here: argument variables are resolved
    	// while command processing (see evaluate())
    	
//        for (int i=0; i < args.size(); i++) {        
//            ((ExpressionNode) args.get(i)).resolveVariables();        
//        }
    }
    
    // rewritten to cope with {Root[f]}
    // Michael Borcherds 2008-10-02	
    public boolean isConstant() {
    	
        // not yet evaluated: process command
        if (evalGeos == null) 
        	evalGeos = evaluateMultiple();   
    	
    	if (evalGeos == null || evalGeos.length == 0)
    		throw new MyError(app, app.getError("InvalidInput") + ":\n" + this);     
    	
    	for (int i = 0 ; i < evalGeos.length ; i++)
    		if (!evalGeos[i].isConstant()) return false;
    	return true;
    	
    }

    public boolean isLeaf() {
        //return evaluate().isLeaf();
        return true;
    }

    public boolean isNumberValue() {
        return evaluate().isNumberValue();
    }

    public boolean isVectorValue() {
        return evaluate().isVectorValue();
    }
    
    final public boolean isBooleanValue() {
        return evaluate().isBooleanValue();
    }

    public boolean isPolynomialInstance() {
        return false;
                
        //return evaluate().isPolynomial();
    }
    
    public boolean isTextValue() {
        return evaluate().isTextValue();
    }   

    public ExpressionValue deepCopy(Kernel kernel) {
        Command c = new Command(kernel, name, false);
        // copy arguments     
        int size = args.size();
        for (int i=0; i < size; i++) {
            c.addArgument(((ExpressionNode) args.get(i)).getCopy(kernel));
        }
        return c;
    }
    
    /**
     * Replaces geo and all its dependent geos in this tree by
     * copies of their values.
     */
    public void replaceChildrenByValues(GeoElement geo) {                                                
        int size = args.size();
        for (int i = 0 ; i < size; i++) {
        	((ExpressionNode)args.get(i)).replaceChildrenByValues(geo);                	                	
        }         
    }

    public HashSet getVariables() {             
        HashSet set = new HashSet();
        int size = args.size();
        for (int i=0; i < size; i++) {
            HashSet s = ((ExpressionNode)args.get(i)).getVariables();
            if (s != null) set.addAll(s);
        }
        return set;
    }

    public String toValueString() {
        return evaluate().toValueString();
    }
    
	public String toLaTeXString(boolean symbolic) {
		return evaluate().toLaTeXString(symbolic);
	}    

    final public boolean isExpressionNode() {
        return false;
    }
    
 
    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }

	public boolean isListValue() {		
		return false;
	}

	public final Macro getMacro() {
		return macro;
	}

	public final void setMacro(Macro macro) {
		this.macro = macro;
	}    
}

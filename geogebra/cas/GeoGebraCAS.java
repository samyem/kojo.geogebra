package geogebra.cas;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
import jasymca.GeoGebraJasymca;

import java.util.HashMap;

import org.mathpiper.interpreters.EvaluationResponse;
import org.mathpiper.interpreters.Interpreter;
import org.mathpiper.interpreters.Interpreters;


/**
 * This class provides an interface for GeoGebra to use the computer algebra
 * system MathPiper.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS {

	private Interpreter ggbMathPiper;
	private GeoGebraJasymca ggbJasymca;	
	private StringBuffer sbInsertSpecial, sbReplaceIndices, sbPolyCoeffs;
	private Application app;
	
	public GeoGebraCAS(Kernel kernel) {
		app = kernel.getApplication();
		sbInsertSpecial = new StringBuffer(80);
		sbReplaceIndices = new StringBuffer(80);
		
		initCAS();
	}
	
	private void initCAS() {
		ggbMathPiper = null;
		getMathPiper();
		ggbJasymca = new GeoGebraJasymca();
	}

	
    /** 
     * Evaluates a JASYMCA expression and returns the result as a string,
     * e.g. exp = "diff(x^2,x)" returns "2*x".
     * @return result string, null possible
     */ 
    final public String evaluateJASYMCA(String exp) {    
    	String result = ggbJasymca.evaluate(exp);      	
    	  
    	// to handle x(A) and x(B) they are converted
    	// to unicode strings in ExpressionNode, 
    	// we need to convert them back here
    	result = insertSpecialChars(result);
    	
    	//System.out.println("exp for JASYMCA: " + exp);  
    	//System.out.println("         result: " + result);  
    	        
        return result;
    }

    /**
	 * Evaluates a MathPiper expression and returns the result as a string in MathPiper syntax, 
	 * e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMathPiper(String exp) {
		try {
			String result;
						
			// MathPiper has problems with indices like a_3, b_{12}
			exp = replaceIndices(exp);
			
			// evaluate the MathPiper expression
			Interpreter mathpiper = getMathPiper();
			response = mathpiper.evaluate(exp);
			
			if (response.isExceptionThrown())
			{
				Application.debug("String for MathPiper: "+exp+"\nException from MathPiper: "+response.getExceptionMessage());
				return null;
			}
			result = response.getResult();
					
			// undo special character handling
			result = insertSpecialChars(result);

			//Application.debug("String for MathPiper: "+exp+"\nresult: "+result);

			return result;
		} catch (Throwable th) {
			//MathPiper.Evaluate("restart;");
			th.printStackTrace();
			return null;
		} 
	}
				
	/**
	 * Evaluates a MathPiper expression wrapped in a command and returns the result as a string, 
	 * e.g. wrapperCommand = "Factor", exp = "3*(a+b)" evaluates "Factor(3*(a+b)" and 
	 * returns "3*a+3*b".
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMathPiper(String wrapperCommand, String exp) {
		StringBuffer sb = new StringBuffer(exp.length()+wrapperCommand.length()+2);
		sb.append(wrapperCommand);
		sb.append('(');
		sb.append(exp);				
		sb.append(')');
		return evaluateMathPiper(sb.toString());
	}	
	
	/**
	 * Returns the error message of the last MathPiper evaluation.
	 * @return null if last evaluation was successful.
	 */
	final synchronized public String getMathPiperError() {
		if (response != null)
			return response.getExceptionMessage();
		else 
			return null;
	}
	
	EvaluationResponse response ;	
	
	private synchronized Interpreter getMathPiper() {				
		if (ggbMathPiper == null) {
			// where to find MathPiper scripts
			//eg docBase = "jar:http://www.geogebra.org/webstart/alpha/geogebra_cas.jar!/";
			String appCodeBase = app.getCodeBase().toString();
			String scriptBase = null;
			if (appCodeBase.startsWith("jar")) {
				scriptBase = appCodeBase + Application.CAS_JAR_NAME + "!/";			
			}
			else {
				scriptBase = "jar:" + appCodeBase + Application.CAS_JAR_NAME + "!/";			
			}
			ggbMathPiper = Interpreters.getSynchronousInterpreter(scriptBase);
			boolean success = initMyMathPiperFunctions();
			
			if (!success) {
				System.err.println("MathPiper creation failed with scriptbase: " + scriptBase);
				ggbMathPiper = Interpreters.getSynchronousInterpreter(scriptBase);
				success = initMyMathPiperFunctions();
				if (!success)
					System.out.println("MathPiper creation failed again with null scriptbase");
			}
		}
		
		return ggbMathPiper;
	}	
	
	/**
	 * Initialize special commands needed in our ggbMathPiper instance,e.g.
	 * getPolynomialCoeffs(exp,x).
	 */
	private synchronized boolean initMyMathPiperFunctions() {
// Expand expression and get polynomial coefficients using MathPiper:
//		getPolynomialCoeffs(expr,x) :=
//			       If( CanBeUni(expr),
//			           [
//							Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));			           ],
//			           {};
//			      );
		String strGetPolynomialCoeffs = "getPolynomialCoeffs(expr,x) := If( CanBeUni(expr),[ Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));],{});";
		EvaluationResponse resp = ggbMathPiper.evaluate(strGetPolynomialCoeffs);
		if (resp.isExceptionThrown()) {
			return false;
		}
		
		// make sure we get (x^n)^2 not x^n^2
		// (Issue 125)
		ggbMathPiper.evaluate("LeftPrecedence(\"^\",19);");

		// make sure Factor((((((((9.1) * ((x)^(7))) - ((32) * ((x)^(6)))) + ((48) * ((x)^(5)))) - ((40) * ((x)^(4)))) + ((20) * ((x)^(3)))) - ((6) * ((x)^(2)))) + (x))] works
		String initFactor = "Factors(p_IsRational)_(Denom(p) != 1) <-- {{Factor(Numer(p)) / Factor(Denom(p)) , 1}};";
		ggbMathPiper.evaluate(initFactor);

		// define constanct for Degree
		response = ggbMathPiper.evaluate("Degree := 180/pi;");
		
		return true;
	}
	

	final public String simplifyMathPiper(String exp) {
		return evaluateMathPiper("Simplify", exp );
	}
	
	final public String factorMathPiper(String exp) {
		return evaluateMathPiper("Factor", exp );
	}

	final public String expandMathPiper(String exp) {
		return evaluateMathPiper("ExpandBrackets", exp );
	}
	
	HashMap getPolynomialCoeffsCache = new HashMap(50);
	StringBuffer getPolynomialCoeffsSB = new StringBuffer();
	
	/**
	 * Expands the given MathPiper expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial, null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 */
	final public String[] getPolynomialCoeffs(String MathPiperExp, String variable) {
		//return ggbJasymca.getPolynomialCoeffs(MathPiperExp, variable);
		
		getPolynomialCoeffsSB.setLength(0);
		getPolynomialCoeffsSB.append(MathPiperExp);
		getPolynomialCoeffsSB.append(':');
		getPolynomialCoeffsSB.append(variable);
		
		String result = (String)(getPolynomialCoeffsCache.get(getPolynomialCoeffsSB.toString()));
		
		if (result != null) {
			//Application.debug("using cached result: "+result);
			// remove { } to get "b, 0, 3*a"
			result = result.substring(1, result.length()-1);
			
			// split to get coefficients array ["b", "0", "3*a"]
			String [] coeffs = result.split(",");				    
	        return coeffs;	
		}
		
		
		if (sbPolyCoeffs == null)
			sbPolyCoeffs = new StringBuffer();
		else
			sbPolyCoeffs.setLength(0);
		
		
		/* replaced Michael Borcherds 2009-02-08
		 * doesn't seem to work properly polyCoeffsbug.ggb
		 */
		sbPolyCoeffs.append("getPolynomialCoeffs(");
		sbPolyCoeffs.append(MathPiperExp);
		sbPolyCoeffs.append(',');
		sbPolyCoeffs.append(variable);
		sbPolyCoeffs.append(')');
		

		// Expand expression and get polynomial coefficients using MathPiper:
		// Prog( Local(exp), 
		//   	 exp := ExpandBrackets( 3*a*x^2 + b ), 
		//		 Coef(exp, x, 0 .. Degree(exp, x)) 
		// )		
		//sbPolyCoeffs.append("Prog( Local(exp), exp := ExpandBrackets(");
		//sbPolyCoeffs.append(MathPiperExp);
		//sbPolyCoeffs.append("), Coef(exp, x, 0 .. Degree(exp, x)))");
			
		try {
			// expand expression and get coefficients of
			// "3*a*x^2 + b" in form "{ b, 0, 3*a }" 
			result = evaluateMathPiper(sbPolyCoeffs.toString());
			
			// empty list of coefficients -> return null
			if ("{}".equals(result) || "".equals(result) || result == null) 
				return null;
			
			// cache result
			//Application.debug("caching result: "+result);		
			getPolynomialCoeffsCache.put(getPolynomialCoeffsSB.toString(), result);

			//Application.debug(sbPolyCoeffs+"");
			//Application.debug(result+"");
			
			// remove { } to get "b, 0, 3*a"
			result = result.substring(1, result.length()-1);
			
			// split to get coefficients array ["b", "0", "3*a"]
			String [] coeffs = result.split(",");				    
            return coeffs;						
		} 
		catch(Exception e) {
			Application.debug("GeoGebraCAS.getPolynomialCoeffs(): " + e.getMessage());
			//e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * Converts all index characters ('_', '{', '}') in the given String
	 * to "unicode" + charactercode + DELIMITER Strings. This is needed because
	 * MathPiper does not handle indices correctly.
	 */
	private synchronized String replaceIndices(String str) {
		int len = str.length();
		sbReplaceIndices.setLength(0);
		
		boolean foundIndex = false;

		// convert every single character and append it to sb
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			int code = (int) c;
			
			boolean replaceCharacter = false;			
			switch (c) {
				case '_': // start index
					foundIndex = true;
					replaceCharacter = true;
					break;
										
				case '{': 	
					if (foundIndex) {
						replaceCharacter = true;						
					}					
					break;					
					
				case '}':
					if (foundIndex) {
						replaceCharacter = true;
						foundIndex = false; // end of index
					}					
					break;
					
				default:
					replaceCharacter = false;
			}
			
			if (replaceCharacter) {
				sbReplaceIndices.append(ExpressionNode.UNICODE_PREFIX);
				sbReplaceIndices.append(code);
				sbReplaceIndices.append(ExpressionNode.UNICODE_DELIMITER);
			} else {
				sbReplaceIndices.append(c);
			}
		}
					
		return sbReplaceIndices.toString();
	}

	/**
	 * Reverse operation of removeSpecialChars().
	 * @see ExpressionNode.operationToString() for XCOORD, YCOORD
	 */
	private String insertSpecialChars(String str) {
		int len = str.length();
		sbInsertSpecial.setLength(0);

		// convert every single character and append it to sb
		char prefixStart = ExpressionNode.UNICODE_PREFIX.charAt(0);
		int prefixLen = ExpressionNode.UNICODE_PREFIX.length();
		boolean prefixFound;
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			prefixFound = false;

			// first character of prefix found
			if (c == prefixStart) {
				prefixFound = true;
				// check prefix
				int j = i;
				for (int k = 0; k < prefixLen; k++, j++) {
					if (ExpressionNode.UNICODE_PREFIX.charAt(k) != str
							.charAt(j)) {
						prefixFound = false;
						break;
					}
				}

				if (prefixFound) {
					// try to get the unicode
					int code = 0;
					char digit;
					while (j < len && Character.isDigit(digit = str.charAt(j))) {
						code = 10 * code + (digit - 48);
						j++;
					}

					if (code > 0 && code < 65536) { // valid unicode
						sbInsertSpecial.append((char) code);
						i = j;
					} else { // invalid
						sbInsertSpecial.append(ExpressionNode.UNICODE_PREFIX);
						i += prefixLen;
					}
				} else {
					sbInsertSpecial.append(c);
				}
			} else {
				sbInsertSpecial.append(c);
			}
		}
		return sbInsertSpecial.toString();
	}

	
	/*
	 * public static void main(String [] args) {
	 * 
	 * GeoGebraCAS cas = new GeoGebraCAS();
	 * 
	 * Application.debug("GGBCAS"); // Read/eval/print loop int i=1;
	 * while(true){ Application.debug( "(In"+i+") "); // Prompt try{ String line =
	 * readLine(System.in); //String result = MathPiper.Evaluate(line);
	 * 
	 * String result = cas.evaluateJASYMCA(line);
	 * 
	 * Application.debug( "(Out"+i+") "+result ); i++; }catch(Exception e){
	 * Application.debug("\n"+e); } } }
	 */

}
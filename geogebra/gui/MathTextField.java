package geogebra.gui;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;

import javax.swing.JTextField;

/*
 * Michael Borcherds
 * 
 * Extends JTextField
 * adds support for alt-codes (and alt-shift-) for special characters
 * (ctrl on MacOS)
 */

public class MathTextField extends JTextField implements KeyListener {


	public MathTextField() {
		super();
		this.addKeyListener(this);
	}

	public void keyPressed(KeyEvent e) {   
	}

	public void keyTyped(KeyEvent e) {      
		// we don't want to trap AltGr
		// as it is used eg for entering {[}] is some locales
		// NB e.isAltGraphDown() doesn't work
		if (e.isAltDown() && e.isControlDown())
			return;

		// swallow eg ctrl-a ctrl-b ctrl-p on Mac
		if (Application.MAC_OS && e.isControlDown())
			e.consume();

	}   



	public void keyReleased(KeyEvent e) {   
		
		// ctrl pressed on Mac
		// or alt on Windows
		boolean modifierKeyPressed = Application.isAltDown(e);

		if (modifierKeyPressed) {

			String insertStr = "";


			// works nicely for alt or ctrl pressed (Windows/Mac)  
			String keyString = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase(Locale.US);
			
			//Application.debug(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase().charAt(0)+"");
			//Application.debug(e+"");
			//Application.debug(keyString);
			
			
			// Numeric keypad numbers eg NumPad-8, NumPad *
			if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD)
				keyString = e.getKeyChar() + "";
			
			// workaround for different Java versions!!
			if (keyString.equals("minus"))
				keyString = "-";
			else if (keyString.equals("plus"))
				keyString = "+";
			else if (keyString.equals("comma"))
				keyString = ",";
			else if (keyString.equals("period"))
				keyString = ".";
			else if (keyString.equals("equals"))
				keyString = "=";
			
			// workaround for shifted characters:
			// (different in different locales)
			if (e.getKeyChar() == '+')
				keyString = "+";
			else if (e.getKeyChar() == '*')
				keyString = "*";
			else if (e.getKeyChar() == '=')
				keyString = "=";
			else if (e.getKeyChar() == '-')
				keyString = "-";
			else if (e.getKeyChar() == '>')
				keyString = ">";
			else if (e.getKeyChar() == '<')
				keyString = "<";
			

			
			//Application.debug(keyString);


			// don't want to act on eg "Shift"
			if (keyString.length() == 1)
				switch (keyString.charAt(0)) {

				case '*' :
					insertStr = "*";
					// was ExpressionNode.strCOMPLEXMULTIPLY; //  alt-m -> complex multiply "\u2297"
					break;
				case '=' :
					insertStr = "\u2260"; // alt-= -> notEqualTo
					break;
				case '+' :
					insertStr = "\u00b1"; // alt-+ -> minusOrPlus
					break;
				case '-' :
					insertStr = "\u2213"; // alt-- -> minusOrPlus
					break;
				case ',' : 
				case '<' : 
					insertStr = "\u2264"; // alt-< -> lessThanOrEqual
					break;
				case '.' : 
				case '>' : 
					insertStr = "\u2265"; // alt-> -> greaterThanOrEqual
					break;
				case 'a' :
					if (e.isShiftDown())
						insertStr = "\u0391"; // alt-A -> unicode alpha (upper case)
					else
						insertStr = "\u03b1"; // alt-a -> unicode alpha
					break;
				case 'b' :
					if (e.isShiftDown())
						insertStr = "\u0392"; // alt-B -> unicode beta (upper case)
					else
						insertStr = "\u03b2"; // alt-b -> unicode beta
					break;
				case 'd' :
					if (e.isShiftDown())
						insertStr = "\u0394"; // alt-D -> unicode delta (upper case)
					else
						insertStr = "\u03b4"; // alt-d -> unicode delta
					break;
				case 'e' :
					insertStr = Kernel.EULER_STRING; // alt-e -> unicode e
					break;
				case 'f' :
					if (e.isShiftDown())
						insertStr = "\u03a6"; // alt-F -> unicode phi (upper case)
					else
						insertStr = "\u03c6"; // alt-f -> unicode phi
					break;
				case 'g' :
					if (e.isShiftDown())
						insertStr = "\u0393"; // alt-G -> unicode gamma (upper case)
					else
						insertStr = "\u03b3"; // alt-g -> unicode gamma
					break;
				case 'i' :
					insertStr = "\u221e"; // alt-i -> infinity
					break;
				case 'l' :
					if (e.isShiftDown())
						insertStr = "\u039b"; // alt-L -> unicode lambda (upper case)
					else
						insertStr = "\u03bb"; // alt-l -> unicode lambda
					break;
				case 'm' :
					if (e.isShiftDown())
						insertStr = "\u039c"; // alt-P -> unicode pi (upper case)
					else
						insertStr = "\u03bc"; // alt-p -> unicode pi
					break;
				case 'o' :
					insertStr = "\u00b0"; // alt-o -> unicode degree sign
					break;
				case 'p' :
					if (e.isShiftDown())
						insertStr = "\u03a0"; // alt-P -> unicode pi (upper case)
					else
						insertStr = "\u03c0"; // alt-p -> unicode pi
					break;
				case 's' :
					if (e.isShiftDown())
						insertStr = "\u03a3"; // alt-S -> unicode theta (upper case)
					else
						insertStr = "\u03c3"; // alt-s -> unicode theta
					break;
				case 't' :
					if (e.isShiftDown())
						insertStr = "\u0398"; // alt-T -> unicode theta (upper case)
					else
						insertStr = "\u03b8"; // alt-t -> unicode theta
					break;
				case 'w' :
					if (e.isShiftDown())
						insertStr = "\u03a9"; // alt-W -> unicode omega (upper case)
					else
						insertStr = "\u03c9"; // alt-w -> unicode omega
					break;
				case '0' :
					insertStr = "\u2070"; // alt-0 -> unicode superscript 0
					break;
				case '1' :
					insertStr = "\u00b9"; // alt-1 -> unicode superscript 1
					break;
				case '2' :
					insertStr = "\u00b2"; // alt-2 -> unicode superscript 2
					break;
				case '3' :
					insertStr = "\u00b3"; // alt-3 -> unicode superscript 3
					break;
				case '4' :
					insertStr = "\u2074"; // alt-4 -> unicode superscript 4
					break;
				case '5' :
					insertStr = "\u2075"; // alt-5 -> unicode superscript 5
					break;
				case '6' :
					insertStr = "\u2076"; // alt-6 -> unicode superscript 6
					break;
				case '7' :
					insertStr = "\u2077"; // alt-7 -> unicode superscript 7
					break;
/*	care needed
On pc-keyboard:    7   8   9    0
AltGraph
or Ctrl Alt        {   [   ]    }

On Apple:          7   8   9    0
Alt                    [   ]
Ctrl Alt               {   }
 */
				case '8' :
					insertStr = "\u2078"; // alt-8 -> unicode superscript 8
					break;
				case '9' :
					insertStr = "\u2079"; // alt-9 -> unicode superscript 9
					break;
				}

			if (!insertStr.equals("")) {
				int pos = getCaretPosition();
				String oldText = getText();
				StringBuffer sb = new StringBuffer();
				sb.append(oldText.substring(0, pos));
				sb.append(insertStr);
				sb.append(oldText.substring(pos));            
				setText(sb.toString());

				setCaretPosition(pos + insertStr.length());
				e.consume();
			}
		}
	}
}

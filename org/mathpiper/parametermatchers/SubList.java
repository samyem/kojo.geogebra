/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:

package org.mathpiper.parametermatchers;

import org.mathpiper.lisp.ConsPointer;
import org.mathpiper.lisp.ConsTraverser;
import org.mathpiper.lisp.Environment;


/// Class for matching against a list of Parameter objects.
public class SubList extends Parameter
{
	protected Parameter[] iMatchers;
	protected int iNrMatchers;
	
	public SubList(Parameter[] aMatchers, int aNrMatchers)
	{
		iMatchers = aMatchers;
		iNrMatchers = aNrMatchers;
	}

	public boolean argumentMatches(Environment  aEnvironment,
	                               ConsPointer  aExpression,
	                               ConsPointer[]  arguments) throws Exception
	{
		if (aExpression.getCons().getSubList() == null)
			return false;
		int i;

		ConsTraverser iter = new ConsTraverser(aExpression);
		iter.goSub();

		for (i=0;i<iNrMatchers;i++)
		{
			ConsPointer  ptr = iter.ptr();
			if (ptr == null)
				return false;
			if (iter.getCons() == null)
				return false;
			if (!iMatchers[i].argumentMatches(aEnvironment,ptr,arguments))
				return false;
			iter.goNext();
		}
		if (iter.getCons() != null)
			return false;
		return true;
	}

	
}

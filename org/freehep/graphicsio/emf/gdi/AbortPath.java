// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * AbortPath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: AbortPath.java,v 1.3 2008-05-04 12:19:53 murkle Exp $
 */
public class AbortPath extends EMFTag {

	public AbortPath() {
        super(68, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return this;
    }
}

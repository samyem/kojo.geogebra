// Copyright 2007, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFConstants;

import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.Shape;

/**
 * base class for all tags that change the
 * clipping area of the {@link org.freehep.graphicsio.emf.EMFRenderer}
 *
 * @author Steffen Greiffenberg
 * @version $Id: AbstractClipPath.java,v 1.3 2008-05-04 12:18:07 murkle Exp $
 */
public abstract class AbstractClipPath extends EMFTag {

    private int mode;

    protected AbstractClipPath(int id, int version, int mode) {
        super(id, version);
        this.mode = mode;
    }

    public String toString() {
        return super.toString() + "\n  mode: " + mode;
    }

    public int getMode() {
        return mode;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     * @param shape shape to use as clipping area
     */
    public void render(EMFRenderer renderer, Shape shape) {
        if (shape != null) {
            // The new clipping region includes the intersection
            // (overlapping areas) of the current clipping region and the current shape.
            if (mode == EMFConstants.RGN_AND) {
                renderer.clip(shape);
            }
            // The new clipping region is the current shape
            else if (mode == EMFConstants.RGN_COPY) {
                // rest the clip ...
                AffineTransform at = renderer.getTransform();
                // temporarly switch to the base transformation to
                // aplly the base clipping area
                renderer.resetTransformation();
                // set the clip
                renderer.setClip(renderer.getInitialClip());
                renderer.setTransform(at);
                renderer.clip(shape);
            }
            // The new clipping region includes the areas of the
            // current clipping region with those of the current shape excluded.
            else if (mode == EMFConstants.RGN_DIFF) {
                Shape clip = renderer.getClip();
                if (clip != null) {
                    Area a = new Area(shape);
                    a.subtract(new Area(clip));
                    renderer.setClip(a);
                } else {
                    renderer.setClip(shape);
                }
            }
            // The new clipping region includes the union (combined areas)
            // of the current clipping region and the current shape.
            else if(mode == EMFConstants.RGN_OR) {
                GeneralPath path = new GeneralPath(shape);
                Shape clip = renderer.getClip();
                if (clip != null) {
                    path.append(clip, false);
                }
                renderer.setClip(path);
            }
            // The new clipping region includes the union of the current
            // clipping region and the current shape but without the overlapping areas.
            else if(mode == EMFConstants.RGN_XOR) {
                Shape clip = renderer.getClip();
                if (clip != null) {
                    Area a = new Area(shape);
                    a.exclusiveOr(new Area(clip));
                    renderer.setClip(a);
                } else {
                    renderer.setClip(shape);
                }
            }
        }

        // delete the current shape
        renderer.setPath(null);
    }
}

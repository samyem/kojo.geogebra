/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

/*
 * EuclidianController.java
 *
 * Created on 16. Oktober 2001, 15:41
 */

package geogebra.euclidian;

import geogebra.kernel.AlgoPolygon;
import geogebra.kernel.Dilateable;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoAxis;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.kernel.Mirrorable;
import geogebra.kernel.Path;
import geogebra.kernel.PointRotateable;
import geogebra.kernel.Translateable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import javax.swing.ToolTipManager;
import javax.swing.text.JTextComponent;

public class EuclidianController implements MouseListener,
MouseMotionListener, MouseWheelListener, ComponentListener {

	protected static final int MOVE_NONE = 101;

	protected static final int MOVE_POINT = 102;

	protected static final int MOVE_LINE = 103;

	protected static final int MOVE_CONIC = 104;

	protected static final int MOVE_VECTOR = 105;

	protected static final int MOVE_VECTOR_STARTPOINT = 205;

	protected static final int MOVE_VIEW = 106;

	protected static final int MOVE_FUNCTION = 107;

	protected static final int MOVE_LABEL = 108;

	protected static final int MOVE_TEXT = 109;

	protected static final int MOVE_NUMERIC = 110; // for number on slider

	protected static final int MOVE_SLIDER = 111; // for slider itself

	protected static final int MOVE_IMAGE = 112;

	protected static final int MOVE_ROTATE = 113;

	protected static final int MOVE_DEPENDENT = 114;

	protected static final int MOVE_MULTIPLE_OBJECTS = 115; // for multiple objects

	protected static final int MOVE_X_AXIS = 116;
	protected static final int MOVE_Y_AXIS = 117;

	protected static final int MOVE_BOOLEAN = 118; // for checkbox moving

	protected Application app;

	protected Kernel kernel;

	protected EuclidianView view;

	protected Point startLoc, mouseLoc, lastMouseLoc; // current mouse location
	
	protected double xZeroOld, yZeroOld, xTemp, yTemp;

	protected Point oldLoc = new Point();

	double xRW, yRW, // real world coords of mouse location
	xRWold = Double.NEGATIVE_INFINITY, yRWold = xRWold, temp;

	// for moving conics:
	protected Point2D.Double startPoint = new Point2D.Double();
	
	protected boolean useLineEndPoint = false;
	protected Point2D.Double lineEndPoint = null;

	protected Point selectionStartPoint = new Point();

	protected GeoConic tempConic;

	protected GeoFunction tempFunction;

	// protected GeoVec2D b;

	protected GeoPoint movedGeoPoint;
	protected boolean movedGeoPointDragged = false;

	protected GeoLine movedGeoLine;

	//protected GeoSegment movedGeoSegment;

	protected GeoConic movedGeoConic;

	protected GeoVector movedGeoVector;

	protected GeoText movedGeoText;

	protected GeoImage oldImage, movedGeoImage;	

	protected GeoFunction movedGeoFunction;

	protected GeoNumeric movedGeoNumeric;
	protected boolean movedGeoNumericDragged = false;

	protected GeoBoolean movedGeoBoolean;

	protected GeoElement movedLabelGeoElement;

	protected GeoElement movedGeoElement;	

	protected GeoElement rotGeoElement, rotStartGeo;
	protected GeoPoint rotationCenter;
	public GeoElement recordObject;
	protected MyDouble tempNum;
	protected double rotStartAngle;
	protected ArrayList translateableGeos;
	protected GeoVector translationVec;

	protected ArrayList tempArrayList = new ArrayList();
	protected ArrayList tempArrayList2 = new ArrayList();
	protected ArrayList tempArrayList3 = new ArrayList();
	protected ArrayList selectedPoints = new ArrayList();

	protected ArrayList selectedNumbers = new ArrayList();

	protected ArrayList selectedLines = new ArrayList();

	protected ArrayList selectedSegments = new ArrayList();

	protected ArrayList selectedConics = new ArrayList();

	protected ArrayList selectedFunctions = new ArrayList();
	protected ArrayList selectedCurves = new ArrayList();

	protected ArrayList selectedVectors = new ArrayList();

	protected ArrayList selectedPolygons = new ArrayList();

	protected ArrayList selectedGeos = new ArrayList();

	protected ArrayList selectedLists = new ArrayList();

	protected LinkedList highlightedGeos = new LinkedList();

	protected boolean selectionPreview = false;

	protected boolean TEMPORARY_MODE = false; // changed from QUICK_TRANSLATEVIEW Michael Borcherds 2007-10-08

	protected boolean DONT_CLEAR_SELECTION = false; // Michael Borcherds 2007-12-08

	protected boolean DRAGGING_OCCURED = false; // for moving objects

	protected boolean POINT_CREATED = false;

	protected boolean moveModeSelectionHandled;

	//protected MyPopupMenu popupMenu;

	protected int mode, oldMode, moveMode = MOVE_NONE;
	protected Macro macro;
	protected Class [] macroInput;

	protected int DEFAULT_INITIAL_DELAY;

	protected boolean toggleModeChangedKernel = false;

	protected boolean altDown = false;

	private static String defaultRotateAngle = "45\u00b0"; // 45 degrees

	/** Creates new EuclidianController */
	public EuclidianController(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();

		// for tooltip manager
		DEFAULT_INITIAL_DELAY = ToolTipManager.sharedInstance()
		.getInitialDelay();

		tempNum = new MyDouble(kernel);
	}

	Application getApplication() {
		return app;
	}

	Kernel getKernel() {
		return kernel;
	}

	void setView(EuclidianView view) {
		this.view = view;
	}

	public void setMode(int newMode) {
		endOfMode(mode);

		if (EuclidianView.usesSelectionRectangleAsInput(newMode))
		{
			initNewMode(newMode);
			processSelectionRectangle(null);			
		}
		else
		{
			if (!TEMPORARY_MODE) app.clearSelectedGeos(false);
			initNewMode(newMode);			
		}

		kernel.notifyRepaint();
	}

	protected void endOfMode(int mode) {
		switch (mode) {
		case EuclidianView.MODE_SHOW_HIDE_OBJECT:				
			// take all selected objects and hide them
			Collection coll = 	app.getSelectedGeos();				
			Iterator it = coll.iterator();
			while (it.hasNext()) {
				GeoElement geo = (GeoElement) it.next();					
				geo.setEuclidianVisible(false);
				geo.updateRepaint();								
			}				
			break;
		}

		if (recordObject != null) recordObject.setSelected(false);
		recordObject = null;

		if (toggleModeChangedKernel)
			app.storeUndoInfo();
	}

	protected void initNewMode(int mode) {
		this.mode = mode;
		initShowMouseCoords();
		// Michael Borcherds 2007-10-12
		//clearSelections();
		if (!TEMPORARY_MODE) clearSelections();
		//		 Michael Borcherds 2007-10-12
		moveMode = MOVE_NONE;

		Previewable previewDrawable = null;
		// init preview drawables
		switch (mode) {
		case EuclidianView.MODE_JOIN: // line through two points
			useLineEndPoint = false;
			previewDrawable = new DrawLine(view, selectedPoints);
			break;

		case EuclidianView.MODE_SEGMENT:
			useLineEndPoint = false;
			previewDrawable = new DrawSegment(view, selectedPoints);
			break;

		case EuclidianView.MODE_RAY:
			useLineEndPoint = false;
			previewDrawable = new DrawRay(view, selectedPoints);
			break;

		case EuclidianView.MODE_VECTOR:
			useLineEndPoint = false;
			previewDrawable = new DrawVector(view, selectedPoints);
			break;

		case EuclidianView.MODE_POLYGON:
			previewDrawable = new DrawPolygon(view, selectedPoints);
			break;

		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:		
			previewDrawable = new DrawConic(view, mode, selectedPoints);
			break;

			// preview for compass: radius first
		case EuclidianView.MODE_COMPASSES:
			previewDrawable = new DrawConic(view, mode, selectedPoints, selectedSegments);
			break;

			// preview for arcs and sectors
		case EuclidianView.MODE_SEMICIRCLE:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			previewDrawable = new DrawConicPart(view, mode, selectedPoints);
			break;										

		case EuclidianView.MODE_SHOW_HIDE_OBJECT:
			// select all hidden objects			
			Iterator it = kernel.getConstruction().getGeoSetConstructionOrder().iterator();
			while (it.hasNext()) {
				GeoElement geo = (GeoElement) it.next();
				// independent numbers should not be set visible
				// as this would produce a slider
				if (!geo.isSetEuclidianVisible() && 
						!(
								(geo.isNumberValue() || geo.isBooleanValue()) && geo.isIndependent())
				) 
				{
					app.addSelectedGeo(geo);
					geo.setEuclidianVisible(true);					
					geo.updateRepaint();										
				}
			}	
			break;

		case EuclidianView.MODE_COPY_VISUAL_STYLE:
			movedGeoElement = null; // this will be the active geo template
			break;

		case EuclidianView.MODE_MOVE_ROTATE:		
			rotationCenter = null; // this will be the active geo template
			break;

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:		
			recordObject = null; 
			break;

		default:
			previewDrawable = null;

		// macro mode?
		if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
			// get ID of macro
			int macroID = mode - EuclidianView.MACRO_MODE_ID_OFFSET;
			macro = kernel.getMacro(macroID);
			macroInput = macro.getInputTypes();
			this.mode = EuclidianView.MODE_MACRO;								
		}		
		break;
		}

		view.setPreview(previewDrawable);	
		toggleModeChangedKernel = false;
	}	



	protected void initShowMouseCoords() {
		view.showMouseCoords = (mode == EuclidianView.MODE_POINT);
	}

	void clearSelections() {

		clearSelection(selectedNumbers);
		clearSelection(selectedPoints);
		clearSelection(selectedLines);
		clearSelection(selectedSegments);
		clearSelection(selectedConics);
		clearSelection(selectedVectors);
		clearSelection(selectedPolygons);
		clearSelection(selectedGeos);
		clearSelection(selectedFunctions);		
		clearSelection(selectedCurves);
		clearSelection(selectedLists);

		app.clearSelectedGeos();

		// clear highlighting
		refreshHighlighting(null);		
	}

	final public void mouseClicked(MouseEvent e) {	
		ArrayList hits;
		//GeoElement geo;

		altDown=e.isAltDown();

		if (mode != EuclidianView.MODE_SELECTION_LISTENER)
			view.requestFocusInWindow();

		if (Application.isRightClick(e)) return;
		setMouseLocation(e);		


		// double-click on object selects MODE_MOVE and opens redefine dialog
		if (e.getClickCount() == 2) { 
			if (app.isApplet() || Application.isControlDown(e)
					|| mode == EuclidianView.MODE_SEGMENT) // it's annoying when you create several joined segments otherwise
				return;

			app.clearSelectedGeos();
			hits = view.getTopHits(mouseLoc);
			if (hits != null) {
				view.setMode(EuclidianView.MODE_MOVE);
				GeoElement geo0 = (GeoElement)hits.get(0);
				
				// double-click on slider -> open properties at slider tab
				if (geo0.isGeoNumeric() && geo0.isEuclidianVisible())
					app.getGuiManager().showPropertiesDialog(hits);
				else if (!geo0.isFixed() && !(geo0.isGeoBoolean() && geo0.isIndependent()) && !(geo0.isGeoImage() && geo0.isIndependent()))
					app.getGuiManager().showRedefineDialog((GeoElement)hits.get(0), true);
			}

		}

		switch (mode) {
		case EuclidianView.MODE_MOVE:								
		case EuclidianView.MODE_SELECTION_LISTENER:
			switch (e.getClickCount()) {
			case 1:			
				// handle selection click	
				handleSelectClick(view.getTopHits(mouseLoc), 
						Application.isControlDown(e));
				break;
				/*
			//	open properties dialog on double click
			case 2:
				if (app.isApplet())
					return;

				app.clearSelectedGeos();
				hits = view.getTopHits(mouseLoc);
				if (hits != null && mode == EuclidianView.MODE_MOVE) {
					GeoElement geo0 = (GeoElement)hits.get(0);
					if (!geo0.isFixed() && !(geo0.isGeoImage() && geo0.isIndependent()))
						app.getGuiManager().showRedefineDialog((GeoElement)hits.get(0));
				}
				break;*/
			}
			break;

		case EuclidianView.MODE_ZOOM_IN:
			view.zoom(mouseLoc.x, mouseLoc.y, EuclidianView.MODE_ZOOM_FACTOR, 15,  false);
			toggleModeChangedKernel = true;
			break;

		case EuclidianView.MODE_ZOOM_OUT:
			view.zoom(mouseLoc.x, mouseLoc.y, 1d/EuclidianView.MODE_ZOOM_FACTOR, 15, false);
			toggleModeChangedKernel = true;
			break;
		}

		// Alt click: copy definition to input field
		if (e.isAltDown() && app.showAlgebraInput()) {
			ArrayList geos = view.getTopHits(mouseLoc);
			if (geos != null && geos.size() > 0) {
				GeoElement geo = (GeoElement) geos.get(0);

				// F3 key: copy definition to input bar
				app.getGlobalKeyDispatcher().handleFunctionKeyForAlgebraInput(3, geo);

				moveMode = MOVE_NONE;	
				return;
			}					
		}
	}

	protected void handleSelectClick(ArrayList geos, boolean ctrlDown) {

		if (geos == null) {			
			app.clearSelectedGeos();
		} else {					
			if (ctrlDown) {				
				GeoElement geo = chooseGeo(geos, true);
				//boolean selected = geo.is
				app.toggleSelectedGeo( chooseGeo(geos, true) ); 
				//app.geoElementSelected(geo, true); // copy definiton to input bar
			} else {								
				if (!moveModeSelectionHandled) {					
					GeoElement geo = chooseGeo(geos, true);
					if (geo != null) {
						app.clearSelectedGeos(false);
						app.addSelectedGeo(geo);




					}
				}				
			}			
		}
	}

	public void mousePressed(MouseEvent e) {
		//GeoElement geo;
		
		ArrayList hits;
		setMouseLocation(e);
		transformCoords();			

		moveModeSelectionHandled = false;
		DRAGGING_OCCURED = false;			
		view.setSelectionRectangle(null);
		selectionStartPoint.setLocation(mouseLoc);	

		if (hitResetIcon() || view.hitAnimationButton(e)) {				
			// see mouseReleased
			return;
		}

		if (Application.isRightClick(e)) {			
			return;
		} 
		else if (
				app.isShiftDragZoomEnabled() && 
				(
						// MacOS: shift-cmd-drag is zoom
						(e.isShiftDown() && !Application.isControlDown(e)) // All Platforms: Shift key
						|| 
						e.isControlDown() && Application.WINDOWS // old Windows key: Ctrl key 
				)) 
		{
			// Michael Borcherds 2007-12-08 BEGIN
			// bugfix: couldn't select multiple objects with Ctrl
			hits = view.getHits(mouseLoc);
			if (hits != null) // bugfix 2008-02-19 removed this:&& ((GeoElement) hits.get(0)).isGeoPoint())
			{
				DONT_CLEAR_SELECTION=true;
			}
			//			 Michael Borcherds 2007-12-08 END
			TEMPORARY_MODE = true;
			oldMode = mode; // remember current mode	
			view.setMode(EuclidianView.MODE_TRANSLATEVIEW);				
		} 		

		switch (mode) {
		// create new point at mouse location
		// this point can be dragged: see mouseDragged() and mouseReleased()
		case EuclidianView.MODE_POINT:			
			hits = view.getHits(mouseLoc, true);
			createNewPoint(hits, true, true, true);
			break;

		case EuclidianView.MODE_SEGMENT:
		case EuclidianView.MODE_SEGMENT_FIXED:		
		case EuclidianView.MODE_JOIN:
		case EuclidianView.MODE_RAY:
		case EuclidianView.MODE_VECTOR:
		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
		case EuclidianView.MODE_CIRCLE_POINT_RADIUS:
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_SEMICIRCLE:
		case EuclidianView.MODE_CONIC_FIVE_POINTS:
		case EuclidianView.MODE_POLYGON:
		case EuclidianView.MODE_REGULAR_POLYGON:	
			hits = view.getHits(mouseLoc);
			createNewPoint(hits, true, true, true);
			break;

		case EuclidianView.MODE_PARALLEL:
		case EuclidianView.MODE_PARABOLA: // Michael Borcherds 2008-04-08
		case EuclidianView.MODE_ORTHOGONAL:
		case EuclidianView.MODE_LINE_BISECTOR:
		case EuclidianView.MODE_ANGULAR_BISECTOR:
		case EuclidianView.MODE_TANGENTS:		
		case EuclidianView.MODE_POLAR_DIAMETER:
			hits = view.getHits(mouseLoc);
			createNewPoint(hits, false, true, true);
			break;		

		case EuclidianView.MODE_COMPASSES:		// Michael Borcherds 2008-03-13	
			hits = view.getHits(mouseLoc);
			createNewPoint(hits, false, true, true);
			break;		

		case EuclidianView.MODE_ANGLE:
			hits = view.getTopHits(mouseLoc);
			// check if we got a polygon
			if (hits == null || !((GeoElement) hits.get(0)).isGeoPolygon()) {
				createNewPoint(hits, false, false, true);			
			}			
			break;

		case EuclidianView.MODE_ANGLE_FIXED:
		case EuclidianView.MODE_MIDPOINT:
			hits = view.getHits(mouseLoc);
			createNewPoint(hits, false, false, true);			
			break;

		case EuclidianView.MODE_MOVE_ROTATE:
			handleMousePressedForRotateMode();
			break;

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:
			handleMousePressedForRecordToSpreadsheetMode(e);
			break;

			// move an object
		case EuclidianView.MODE_MOVE:		
			handleMousePressedForMoveMode(e, false);			
			break;

			// move drawing pad or axis
		case EuclidianView.MODE_TRANSLATEVIEW:			
			// check if axis is hit
			hits = view.getHits(mouseLoc);
			if (hits != null && hits.size() == 1) {
				Object hit0 = hits.get(0);
				if (hit0 == kernel.getXAxis())
					moveMode = MOVE_X_AXIS;
				else if (hit0 == kernel.getYAxis())
					moveMode = MOVE_Y_AXIS;
				else
					moveMode = MOVE_VIEW;
			} else {						
				moveMode = MOVE_VIEW;
			}						

			startLoc = mouseLoc; 
			if (!TEMPORARY_MODE) {
				if (moveMode == MOVE_VIEW)
					view.setMoveCursor();
				else
					view.setDragCursor();
			}
			xZeroOld = view.xZero;
			yZeroOld = view.yZero;		
			xTemp = xRW;
			yTemp = yRW;
			view.showAxesRatio = (moveMode == MOVE_X_AXIS) || (moveMode == MOVE_Y_AXIS);
			//view.setDrawMode(EuclidianView.DRAW_MODE_DIRECT_DRAW);
			break;								

		default:
			moveMode = MOVE_NONE;			 
		}
	}

	protected void handleMousePressedForRotateMode() {	
		GeoElement geo;
		ArrayList hits;

		// we need the center of the rotation
		if (rotationCenter == null) {
			rotationCenter = (GeoPoint) chooseGeo(view.getHits(mouseLoc, GeoPoint.class, tempArrayList), true);
			app.addSelectedGeo(rotationCenter);
			moveMode = MOVE_NONE;
		}
		else {	
			hits = view.getHits(mouseLoc);
			// got rotation center again: deselect
			if (hits != null && hits.contains(rotationCenter)) {
				app.removeSelectedGeo(rotationCenter);
				rotationCenter = null;
				moveMode = MOVE_NONE;
				return;
			}

			moveModeSelectionHandled = true;

			// find and set rotGeoElement
			hits = view.getPointRotateableHits(hits, rotationCenter);
			if (hits != null && hits.contains(rotGeoElement))
				geo = rotGeoElement;
			else {
				geo = chooseGeo(hits, true);				
				app.addSelectedGeo(geo);				
			}			
			rotGeoElement = geo;						

			if (geo != null) {							
				doSingleHighlighting(rotGeoElement);						
				//rotGeoElement.setHighlighted(true);

				// init values needed for rotation
				rotStartGeo = rotGeoElement.copy();
				rotStartAngle = Math.atan2(yRW - rotationCenter.inhomY, 
						xRW - rotationCenter.inhomX);
				moveMode = MOVE_ROTATE;
			} else {
				moveMode = MOVE_NONE;
			}
		}			
	}

	protected void handleMousePressedForMoveMode(MouseEvent e, boolean drag) {

		//view.resetTraceRow(); // for trace/spreadsheet

		// fix for meta-click to work on Mac/Linux
		if (Application.isControlDown(e)) return;			

		// move label?
		GeoElement geo = view.getLabelHit(mouseLoc);
		if (geo != null) {
			moveMode = MOVE_LABEL;
			movedLabelGeoElement = geo;
			oldLoc.setLocation(geo.labelOffsetX, geo.labelOffsetY);
			startLoc = mouseLoc;
			view.setDragCursor();
			return;
		}

		// find and set movedGeoElement
		ArrayList moveableList;

		// if we just click (no drag) on eg an intersection, we want it selected
		// not a popup with just the lines in
		if (drag)
			moveableList = view.getMoveableHits(mouseLoc);
		else
			moveableList = view.getHits(mouseLoc);

		ArrayList hits = view.getTopHits(moveableList);	

		ArrayList selGeos = app.getSelectedGeos();
		// if object was chosen before, take it now!
		if (selGeos.size() == 1 && 
				hits != null && hits.contains(selGeos.get(0))) 
		{
			// object was chosen before: take it			
			geo = (GeoElement) selGeos.get(0);			
		} else {
			// choose out of hits			
			geo = chooseGeo(hits, false);
			
			if (!selGeos.contains(geo)) {
				app.clearSelectedGeos();
				app.addSelectedGeo(geo);
				//app.geoElementSelected(geo, false); // copy definiton to input bar
			}
		}				

		if (geo != null && !geo.isFixed()) {		
			moveModeSelectionHandled = true;														
		} else {
			// no geo clicked at
			moveMode = MOVE_NONE;	
			return;
		}				

		movedGeoElement = geo;
		//doSingleHighlighting(movedGeoElement);				

		/*
		// if object was chosen before, take it now!
		ArrayList selGeos = app.getSelectedGeos();
		if (selGeos.size() == 1 && hits != null && hits.contains(selGeos.get(0))) {
			// object was chosen before: take it
			geo = (GeoElement) selGeos.get(0);			
		} else {
			geo = chooseGeo(hits);			
		}		

		if (geo != null) {
			app.clearSelectedGeos(false);
			app.addSelectedGeo(geo);
			moveModeSelectionHandled = true;			
		}						

		movedGeoElement = geo;
		doSingleHighlighting(movedGeoElement);	
		 */	


		// multiple geos selected
		if (movedGeoElement != null && selGeos.size() > 1) {									
			moveMode = MOVE_MULTIPLE_OBJECTS;
			startPoint.setLocation(xRW, yRW);	
			startLoc = mouseLoc;
			view.setDragCursor();
			if (translationVec == null)
				translationVec = new GeoVector(kernel.getConstruction());
		}	

		// DEPENDENT object: changeable parents?
		// move free parent points (e.g. for segments)
		else if (!movedGeoElement.isMoveable()) {
			translateableGeos = null;

			// point with changeable coord parent numbers
			if (movedGeoElement.isGeoPoint() && 
					((GeoPoint) movedGeoElement).hasChangeableCoordParentNumbers()) {
				translateableGeos = new ArrayList();
				translateableGeos.add(movedGeoElement);
			}

			// STANDARD case: get free input points of dependent movedGeoElement
			else if (movedGeoElement.hasMoveableInputPoints()) {
				// allow only moving of the following object types
				if (movedGeoElement.isGeoLine() || 
						movedGeoElement.isGeoPolygon() ||					 
						movedGeoElement.isGeoConic() || 
						movedGeoElement.isGeoVector()) 
				{		
					translateableGeos = movedGeoElement.getFreeInputPoints();
				}
			}

			// init move dependent mode if we have something to move ;-)
			if (translateableGeos != null) {
				moveMode = MOVE_DEPENDENT;
				startPoint.setLocation(xRW, yRW);					
				view.setDragCursor();
				if (translationVec == null)
					translationVec = new GeoVector(kernel.getConstruction());
			} else {
				moveMode = MOVE_NONE;
			}				
		} 

		// free point
		else if (movedGeoElement.isGeoPoint()) {
			moveMode = MOVE_POINT;
			movedGeoPoint = (GeoPoint) movedGeoElement;
			view.showMouseCoords = !app.isApplet()
			&& !movedGeoPoint.hasPath();
			view.setDragCursor();
		} 			

		// free line
		else if (movedGeoElement.isGeoLine()) {
			moveMode = MOVE_LINE;
			movedGeoLine = (GeoLine) movedGeoElement;
			view.showMouseCoords = true;
			view.setDragCursor();
		} 

		// free vector
		else if (movedGeoElement.isGeoVector()) {
			movedGeoVector = (GeoVector) movedGeoElement;

			// change vector itself or move only startpoint?
			// if vector is dependent or
			// mouseLoc is closer to the startpoint than to the end
			// point
			// then move the startpoint of the vector
			if (movedGeoVector.hasAbsoluteLocation()) {
				GeoPoint sP = movedGeoVector.getStartPoint();
				double sx = 0;
				double sy = 0;
				if (sP != null) {
					sx = sP.inhomX;
					sy = sP.inhomY;
				}
				//	if |mouse - startpoint| < 1/2 * |vec| then move
				// startpoint
				if (2d * GeoVec2D.length(xRW - sx, yRW - sy) < GeoVec2D
						.length(movedGeoVector.x, movedGeoVector.y)) { // take
					// startPoint
					moveMode = MOVE_VECTOR_STARTPOINT;
					if (sP == null) {
						sP = new GeoPoint(kernel.getConstruction());
						sP.setCoords(xRW, xRW, 1.0);
						try {
							movedGeoVector.setStartPoint(sP);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				} else
					moveMode = MOVE_VECTOR;
			} else {
				moveMode = MOVE_VECTOR;
			}

			view.showMouseCoords = true;
			view.setDragCursor();
		} 

		// free text
		else if (movedGeoElement.isGeoText()) {
			moveMode = MOVE_TEXT;
			movedGeoText = (GeoText) movedGeoElement;
			view.showMouseCoords = false;
			view.setDragCursor();	

			if (movedGeoText.isAbsoluteScreenLocActive()) {
				oldLoc.setLocation(movedGeoText.getAbsoluteScreenLocX(),
						movedGeoText.getAbsoluteScreenLocY());
				startLoc = mouseLoc;
			}
			else if (movedGeoText.hasAbsoluteLocation()) {
				//	absolute location: change location
				GeoPoint loc = movedGeoText.getStartPoint();
				if (loc == null) {
					loc = new GeoPoint(kernel.getConstruction());
					loc.setCoords(0, 0, 1.0);
					try {
						movedGeoText.setStartPoint(loc);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					startPoint.setLocation(xRW, yRW);
				} else {
					startPoint.setLocation(xRW - loc.inhomX, yRW
							- loc.inhomY);
				}
			} else {
				// for relative locations label has to be moved
				oldLoc.setLocation(movedGeoText.labelOffsetX,
						movedGeoText.labelOffsetY);
				startLoc = mouseLoc;
			}							
		} 

		// free conic
		else if (movedGeoElement.isGeoConic()) {
			moveMode = MOVE_CONIC;
			movedGeoConic = (GeoConic) movedGeoElement;
			view.showMouseCoords = false;
			view.setDragCursor();

			startPoint.setLocation(xRW, yRW);
			if (tempConic == null) {
				tempConic = new GeoConic(kernel.getConstruction());
			}
			tempConic.set(movedGeoConic);
		} 
		else if (movedGeoElement.isGeoFunction()) {
			moveMode = MOVE_FUNCTION;
			movedGeoFunction = (GeoFunction) movedGeoElement;
			view.showMouseCoords = false;
			view.setDragCursor();

			startPoint.setLocation(xRW, yRW);
			if (tempFunction == null) {
				tempFunction = new GeoFunction(kernel.getConstruction());
			}
			tempFunction.set(movedGeoFunction);
		} 

		// free number
		else if (movedGeoElement.isGeoNumeric()) {															
			movedGeoNumeric = (GeoNumeric) movedGeoElement;
			moveMode = MOVE_NUMERIC;

			Drawable d = view.getDrawableFor(movedGeoNumeric);
			if (d instanceof DrawSlider) {
				// should we move the slider 
				// or the point on the slider, i.e. change the number
				DrawSlider ds = (DrawSlider) d;
				if (!ds.hitPoint(mouseLoc.x, mouseLoc.y) &&
						ds.hitSlider(mouseLoc.x, mouseLoc.y)) {
					moveMode = MOVE_SLIDER;
					if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
						oldLoc.setLocation(movedGeoNumeric.getAbsoluteScreenLocX(),
								movedGeoNumeric.getAbsoluteScreenLocY());
						startLoc = mouseLoc;
					} else {
						startPoint.setLocation(xRW - movedGeoNumeric.getRealWorldLocX(),
								yRW - movedGeoNumeric.getRealWorldLocY());
					}
				}	
				else {						
					startPoint.setLocation(movedGeoNumeric.getSliderX(), movedGeoNumeric.getSliderY());
				}
			} 						

			view.showMouseCoords = false;
			view.setDragCursor();					
		}  

		//  checkbox
		else if (movedGeoElement.isGeoBoolean()) {
			movedGeoBoolean = (GeoBoolean) movedGeoElement;
			
			// if fixed checkbox dragged, behave as if it's been clicked
			// important for electronic whiteboards
			if (movedGeoBoolean.isCheckboxFixed()) {
				movedGeoBoolean.setValue(!movedGeoBoolean.getBoolean());
				app.removeSelectedGeo(movedGeoBoolean); // make sure it doesn't get selected
				movedGeoBoolean.updateCascade();

			} 
			
			// move checkbox
			moveMode = MOVE_BOOLEAN;					
			startLoc = mouseLoc;
			oldLoc.x = movedGeoBoolean.getAbsoluteScreenLocX();
			oldLoc.y = movedGeoBoolean.getAbsoluteScreenLocY();

			view.showMouseCoords = false;
			view.setDragCursor();			
		}

		// image
		else if (movedGeoElement.isGeoImage()) {
			moveMode = MOVE_IMAGE;
			movedGeoImage = (GeoImage) movedGeoElement;
			view.showMouseCoords = false;
			view.setDragCursor();

			if (movedGeoImage.isAbsoluteScreenLocActive()) {
				oldLoc.setLocation(movedGeoImage.getAbsoluteScreenLocX(),
						movedGeoImage.getAbsoluteScreenLocY());
				startLoc = mouseLoc;
			} 
			else if (movedGeoImage.hasAbsoluteLocation()) {
				startPoint.setLocation(xRW, yRW);
				oldImage = new GeoImage(movedGeoImage);
			} 				
		}
		else {
			moveMode = MOVE_NONE;
		}

		view.repaint();												
	}

	final public void mouseDragged(MouseEvent e) {
		// check if input field is selection listener right now:
		// if yes, go back into move mode and start to handle drag
		if (mode == EuclidianView.MODE_SELECTION_LISTENER) {
			if (app.hasGuiManager() && app.getGuiManager().isInputFieldSelectionListener());
			app.setMoveMode();
			handleMousePressedForMoveMode(e, true);
			//view.requestFocusInWindow();			
		}

		if (!DRAGGING_OCCURED) {
			DRAGGING_OCCURED = true;			

			// Michael Borcherds 2007-10-07 allow right mouse button to drag points
			if (Application.isRightClick(e))
				if (view.getHits(mouseLoc, true)!=null) 
				{
					TEMPORARY_MODE = true;
					oldMode = mode; // remember current mode			
					view.setMode(EuclidianView.MODE_MOVE);
					handleMousePressedForMoveMode(e, true);	
					return;
				}
			if (!app.isRightClickEnabled()) return;
			//			 Michael Borcherds 2007-10-07


			if (mode == EuclidianView.MODE_MOVE_ROTATE) {
				app.clearSelectedGeos(false);
				app.addSelectedGeo(rotationCenter, false);						
			}
		}
		lastMouseLoc = mouseLoc;
		setMouseLocation(e);				
		transformCoords();

		// zoom rectangle (right drag) or selection rectangle (left drag)
		// Michael Borcherds 2007-10-07 allow dragging with right mouse button
		if (((Application.isRightClick(e)) || allowSelectionRectangle()) && !TEMPORARY_MODE) {
			//			 Michael Borcherds 2007-10-07 
			// set zoom rectangle's size
			// right-drag: zoom
			// Shift-right-drag: zoom without preserving aspect ratio
			updateSelectionRectangle((Application.isRightClick(e) && !e.isShiftDown())
					// MACOS:
					// Cmd-left-drag: zoom
					// Cmd-shift-left-drag: zoom without preserving aspect ratio
					|| (Application.MAC_OS && Application.isControlDown(e) && !e.isShiftDown() && !Application.isRightClick(e)));
			view.repaint();
			return;
		}		

		// update previewable
		if (view.previewDrawable != null) {
			view.previewDrawable.updateMousePos(mouseLoc.x, mouseLoc.y);
		}		

		/*
		 * Conintuity handling
		 * 
		 * If the mouse is moved wildly we take intermediate steps to
		 * get a more continous behaviour
		 */		 		
		if (kernel.isContinuous() && lastMouseLoc != null) {
			double dx = mouseLoc.x - lastMouseLoc.x;
			double dy = mouseLoc.y - lastMouseLoc.y;			
			double distsq = dx*dx + dy*dy;		
			if (distsq > MOUSE_DRAG_MAX_DIST_SQUARE) {										
				double factor = Math.sqrt(MOUSE_DRAG_MAX_DIST_SQUARE / distsq);				
				dx *= factor;
				dy *= factor;

				// number of continuity steps <= MAX_CONTINUITY_STEPS
				int steps = Math.min((int) (1.0 / factor), MAX_CONTINUITY_STEPS);
				int mx = mouseLoc.x;
				int my = mouseLoc.y;

				// Application.debug("BIG drag dist: " + Math.sqrt(distsq) + ", steps: " + steps  );
				for (int i=1; i <= steps; i++) {			
					mouseLoc.x = (int) Math.round(lastMouseLoc.x + i * dx);
					mouseLoc.y = (int) Math.round(lastMouseLoc.y + i * dy);
					calcRWcoords();

					handleMouseDragged(false);							
				}

				// set endpoint of mouse movement if we are not already there
				if (mouseLoc.x != mx || mouseLoc.y != my) {	
					mouseLoc.x = mx;
					mouseLoc.y = my;
					calcRWcoords();	
				}				
			} 
		}

		handleMouseDragged(true);								
	}	

	protected boolean allowSelectionRectangle() {
		switch (mode) {
		// move objects
		case EuclidianView.MODE_MOVE:
			return moveMode == MOVE_NONE;

			// move rotate objects
		case EuclidianView.MODE_MOVE_ROTATE:
			return selPoints() > 0; // need rotation center

			// object selection mode
		case EuclidianView.MODE_SELECTION_LISTENER:
			GeoElementSelectionListener sel = app.getCurrentSelectionListener();
			if (sel == null) return false;
			if (app.hasGuiManager()) {
				return !app.getGuiManager().isInputFieldSelectionListener();
			}
			else
				return sel != null;

			// transformations
		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
		case EuclidianView.MODE_DILATE_FROM_POINT:	
		case EuclidianView.MODE_MIRROR_AT_POINT:
		case EuclidianView.MODE_MIRROR_AT_LINE:
		case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
		case EuclidianView.MODE_ROTATE_BY_ANGLE:
		case EuclidianView.MODE_FITLINE:
			return true;

			// checkbox
		case EuclidianView.MODE_SHOW_HIDE_CHECKBOX:			
			return true;

		default:
			return false;
		}		
	}




	// square of maximum allowed pixel distance 
	// for continuous mouse movements
	protected static double MOUSE_DRAG_MAX_DIST_SQUARE = 36; 
	protected static int MAX_CONTINUITY_STEPS = 4; 

	protected void handleMouseDragged(boolean repaint) {
		// moveMode was set in mousePressed()
		switch (moveMode) {
		case MOVE_ROTATE:
			rotateObject(repaint);
			break;

		case MOVE_POINT:

			//view.incrementTraceRow(); // for spreadsheet/trace

			movePoint(repaint);
			break;

		case MOVE_LINE:
			moveLine(repaint);
			break;

		case MOVE_VECTOR:
			moveVector(repaint);
			break;

		case MOVE_VECTOR_STARTPOINT:
			moveVectorStartPoint(repaint);
			break;

		case MOVE_CONIC:
			moveConic(repaint);
			break;

		case MOVE_FUNCTION:
			moveFunction(repaint);
			break;

		case MOVE_LABEL:
			moveLabel();
			break;

		case MOVE_TEXT:
			moveText(repaint);
			break;

		case MOVE_IMAGE:
			moveImage(repaint);
			break;

		case MOVE_NUMERIC:
			//view.incrementTraceRow(); // for spreadsheet/trace

			moveNumeric(repaint);
			break;

		case MOVE_SLIDER:
			moveSlider(repaint);
			break;

		case MOVE_BOOLEAN:
			moveBoolean(repaint);
			break;

		case MOVE_DEPENDENT:
			moveDependent(repaint);
			break;

		case MOVE_MULTIPLE_OBJECTS:
			moveMultipleObjects(repaint);
			break;

		case MOVE_VIEW:
			if (repaint) {
				if (TEMPORARY_MODE) view.setMoveCursor();
				view.setCoordSystem(xZeroOld + mouseLoc.x - startLoc.x, yZeroOld
						+ mouseLoc.y - startLoc.y, view.xscale, view.yscale);
			}
			break;	

		case MOVE_X_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) view.setDragCursor();

				// take care when we get close to the origin
				if (Math.abs(mouseLoc.x - view.xZero) < 2) {
					mouseLoc.x = (int) Math.round(mouseLoc.x > view.xZero ?  view.xZero + 2 : view.xZero - 2);						
				}											
				double xscale = (mouseLoc.x - view.xZero) / xTemp;					
				view.setCoordSystem(view.xZero, view.yZero, xscale, view.yscale);
			}
			break;	

		case MOVE_Y_AXIS:
			if (repaint) {
				if (TEMPORARY_MODE) view.setDragCursor();
				// take care when we get close to the origin
				if (Math.abs(mouseLoc.y - view.yZero) < 2) {
					mouseLoc.y = (int) Math.round(mouseLoc.y > view.yZero ?  view.yZero + 2 : view.yZero - 2);						
				}											
				double yscale = (view.yZero - mouseLoc.y) / yTemp;					
				view.setCoordSystem(view.xZero, view.yZero, view.xscale, yscale);					
			}
			break;	

		default: // do nothing
		}
	}		

	protected void updateSelectionRectangle(boolean keepScreenRatio) {
		if (view.getSelectionRectangle() == null)
			view.setSelectionRectangle(new Rectangle());

		int dx = mouseLoc.x - selectionStartPoint.x;
		int dy = mouseLoc.y - selectionStartPoint.y;
		int dxabs = Math.abs(dx);
		int dyabs = Math.abs(dy);

		int width = dx;
		int height = dy;

		// the zoom rectangle should have the same aspect ratio as the view
		if (keepScreenRatio) {
			double ratio = (double) view.width / (double) view.height;
			if (dxabs >= dyabs * ratio) {		
				height = (int) (Math.round(dxabs / ratio));
				if (dy < 0)
					height = -height;
			} else {
				width = (int) Math.round(dyabs * ratio);
				if (dx < 0)
					width = -width;			
			}
		}

		Rectangle rect = view.getSelectionRectangle();
		if (height >= 0) {			
			if (width >= 0) {
				rect.setLocation(selectionStartPoint);
				rect.setSize(width, height);
			} else { // width < 0
				rect.setLocation(selectionStartPoint.x + width, selectionStartPoint.y);
				rect.setSize(-width, height);
			}
		} else { // height < 0
			if (width >= 0) {
				rect.setLocation(selectionStartPoint.x,
						selectionStartPoint.y + height);
				rect.setSize(width, -height);
			} else { // width < 0
				rect.setLocation(selectionStartPoint.x + width,
						selectionStartPoint.y + height);
				rect.setSize(-width, -height);
			}
		}
	}

	final public void mouseReleased(MouseEvent e) {	

		//if (mode != EuclidianView.MODE_RECORD_TO_SPREADSHEET) view.resetTraceRow(); // for trace/spreadsheet
		if (movedGeoPoint != null){

			// deselect point after drag, but not on click
			if (movedGeoPointDragged) movedGeoPoint.setSelected(false);

			if (mode != EuclidianView.MODE_RECORD_TO_SPREADSHEET) movedGeoPoint.resetTraceColumns();
		}
		if (movedGeoNumeric != null) {

			// deselect slider after drag, but not on click
			if (movedGeoNumericDragged) movedGeoNumeric.setSelected(false);

			if (mode != EuclidianView.MODE_RECORD_TO_SPREADSHEET) movedGeoNumeric.resetTraceColumns();
		}

		movedGeoPointDragged = false;
		movedGeoNumericDragged = false;

		view.requestFocusInWindow();
		setMouseLocation(e);

		altDown=e.isAltDown();

		transformCoords();
		ArrayList hits = null;
		GeoElement geo;

		if (hitResetIcon()) {				
			app.reset();
			return;
		}
		else if (view.hitAnimationButton(e)) {		
			if (kernel.isAnimationRunning())
				kernel.getAnimatonManager().stopAnimation();
			else
				kernel.getAnimatonManager().startAnimation();			
			view.repaint();
			app.setUnsaved();
			return;
		}

		// Michael Borcherds 2007-10-08 allow drag with right mouse button on Windows
		//      and command - drag on Mac ( 
		if ((Application.isRightClick(e) || Application.isControlDown(e)) && !TEMPORARY_MODE)
		{			
			if (!app.isRightClickEnabled()) return;
			if (processZoomRectangle()) return;			
			//			 Michael Borcherds 2007-10-08

			// make sure cmd-click selects multiple points (not open properties)
			if (Application.MAC_OS && Application.isControlDown(e) 
					|| !Application.isRightClick(e))
				return;

			// get selected GeoElements						
			// show popup menu after right click
			hits = view.getTopHits(mouseLoc);
			if (hits == null) {
				// no hits
				if (app.selectedGeosSize() == 1) {
					GeoElement selGeo = (GeoElement) app.getSelectedGeos().get(0);
					app.getGuiManager().showPopupMenu(selGeo, view, mouseLoc);
				}
				else if (app.selectedGeosSize() > 1) {
					// there are selected geos: show them
					app.getGuiManager().showPropertiesDialog(app.getSelectedGeos());
				}
				else {
					// there are no selected geos: show drawing pad popup menu
					app.getGuiManager().showDrawingPadPopup(view, mouseLoc);
				}
			} else {		
				// there are hits
				if (app.selectedGeosSize() > 0) {	
					// selected geos: add first hit to selection and show properties
					app.addSelectedGeo((GeoElement) hits.get(0));

					if (app.selectedGeosSize() == 1) {
						GeoElement selGeo = (GeoElement) app.getSelectedGeos().get(0);
						app.getGuiManager().showPopupMenu(selGeo, view, mouseLoc);
					}
					else  { // more than 1 selected					
						app.getGuiManager().showPropertiesDialog(app.getSelectedGeos());
					}
				}
				else {
					// no selected geos: choose geo and show popup menu
					geo = chooseGeo(hits, true);
					app.getGuiManager().showPopupMenu(geo, view, mouseLoc);						
				}																										
			}				
			return;
		}

		// handle moving
		boolean changedKernel = POINT_CREATED;		
		if (DRAGGING_OCCURED) {			

			//			// copy value into input bar
			//			if (mode == EuclidianView.MODE_MOVE && movedGeoElement != null) {
			//				app.geoElementSelected(movedGeoElement,false);
			//			}


			changedKernel = (moveMode != MOVE_NONE);			
			movedGeoElement = null;
			rotGeoElement = null;	

			// Michael Borcherds 2007-10-08 allow dragging with right mouse button
			if (!TEMPORARY_MODE)
				// Michael Borcherds 2007-10-08
				if (allowSelectionRectangle()) {
					processSelectionRectangle(e);	


					return;
				}
		} else {	
			// no hits: release mouse button creates a point
			// for the transformation tools
			// (note: this cannot be done in mousePressed because
			// we want to be able to select multiple objects using the selection rectangle)
			switch (mode) {
			case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
			case EuclidianView.MODE_DILATE_FROM_POINT:	
			case EuclidianView.MODE_MIRROR_AT_POINT:
			case EuclidianView.MODE_MIRROR_AT_LINE:
			case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			case EuclidianView.MODE_ROTATE_BY_ANGLE:
				hits = view.getHits(mouseLoc);
				if (hits == null) { 
					POINT_CREATED = createNewPoint(hits, false, false, true);					
				}
				changedKernel = POINT_CREATED;
				break;


			default:

				// change checkbox (boolean) state on mouse up only if there's been no drag
				hits = view.getTopHits(mouseLoc);
			if (hits != null) {
				GeoElement hit = (GeoElement)hits.get(0);
				if (hit != null && hit.isGeoBoolean()) {
					GeoBoolean bool = (GeoBoolean)(hits.get(0));
					if (!bool.isCheckboxFixed()) { // otherwise changed on mouse down
						bool.setValue(!bool.getBoolean());
						app.removeSelectedGeo(bool); // make sure doesn't get selected
						bool.updateCascade();
					}
				}
			}
			}
		}

		// remember helper point, see createNewPoint()
		if (changedKernel)
			app.storeUndoInfo();
		
		
		// make sure that when alt is pressed for creating a segment or line
		// it works if the endpoint is on a path
		if (useLineEndPoint && lineEndPoint != null) {
			mouseLoc.x = view.toScreenCoordX(lineEndPoint.x);
			mouseLoc.y = view.toScreenCoordY(lineEndPoint.y);
			useLineEndPoint = false;	
		}

		// now handle current mode
		hits = view.getHits(mouseLoc);

		// Michael Borcherds 2007-12-08 BEGIN moved up a few lines (bugfix: Tools eg Line Segment weren't working with grid on)
		// grid capturing on: newly created point should be taken
		if (hits == null && POINT_CREATED) {			
			hits = new ArrayList();
			hits.add(movedGeoPoint);				
		}
		POINT_CREATED = false;		
		//		 Michael Borcherds 2007-12-08 END	




		if (TEMPORARY_MODE) {
			//			Michael Borcherds 2007-10-13 BEGIN
			view.setMode(oldMode);
			TEMPORARY_MODE = false;
			// Michael Borcherds 2007-12-08 BEGIN bugfix: couldn't select multiple points with Ctrl
			if (DONT_CLEAR_SELECTION==false)
				clearSelections();	
			DONT_CLEAR_SELECTION=false;
			//			 Michael Borcherds 2007-12-08 END
			//mode = oldMode;
			//			Michael Borcherds 2007-10-13 END
		} 
		//		 Michael Borcherds 2007-10-12 bugfix: ctrl-click on a point does the original mode's command at end of drag if a point was clicked on
		//  also needed for right-drag
		else
		{
			if (mode != EuclidianView.MODE_RECORD_TO_SPREADSHEET) changedKernel = processMode(hits, e);
			if (changedKernel)
				app.storeUndoInfo();
		}
		//Michael Borcherds 2007-10-12



		//		Michael Borcherds 2007-10-12
		//      moved up a few lines
		//		changedKernel = processMode(hits, e);
		//		if (changedKernel)
		//			app.storeUndoInfo();
		//		Michael Borcherds 2007-10-12

		if (hits != null)
			view.setDefaultCursor();		
		else
			view.setHitCursor();

		refreshHighlighting(null);

		// reinit vars
		//view.setDrawMode(EuclidianView.DRAW_MODE_BACKGROUND_IMAGE);
		moveMode = MOVE_NONE;
		initShowMouseCoords();	
		view.showAxesRatio = false;
		kernel.notifyRepaint();					
	}

	protected boolean hitResetIcon() {
		return app.showResetIcon() &&
		(mouseLoc.y < 18 && mouseLoc.x > view.width - 18);
	}

	// return if we really did zoom
	protected boolean processZoomRectangle() {
		Rectangle rect = view.getSelectionRectangle();
		if (rect == null) 
			return false;

		if (rect.width < 30 || rect.height < 30
				|| !app.isShiftDragZoomEnabled() // Michael Borcherds 2007-12-11		
		) {
			view.setSelectionRectangle(null);
			view.repaint();
			return false;
		}

		view.resetMode();
		// zoom zoomRectangle to EuclidianView's size
		//double factor = (double) view.width / (double) rect.width;
		//Point p = rect.getLocation();
		view.setSelectionRectangle(null);
		//view.setAnimatedCoordSystem((view.xZero - p.x) * factor,
		//		(view.yZero - p.y) * factor, view.xscale * factor, 15, true);

		// zoom without (necessarily) preserving the aspect ratio
		view.setAnimatedRealWorldCoordSystem(
				view.toRealWorldCoordX(rect.getMinX()),
				view.toRealWorldCoordX(rect.getMaxX()),
				view.toRealWorldCoordY(rect.getMaxY()),
				view.toRealWorldCoordY(rect.getMinY()),
				15 ,true);
		return true;
	}

	// select all geos in selection rectangle 
	protected void processSelectionRectangle(MouseEvent e) {		
		clearSelections();
		ArrayList hits = view.getHits(view.getSelectionRectangle());		

		switch (mode) {
		case EuclidianView.MODE_SELECTION_LISTENER:
			// tell properties dialog
			if (hits.size() > 0 &&
					app.hasGuiManager() &&
					app.getGuiManager().isPropertiesDialogSelectionListener()) 
			{
				GeoElement geo = (GeoElement) hits.get(0);
				app.geoElementSelected(geo, false);
				for (int i=1; i < hits.size(); i++) {
					app.geoElementSelected((GeoElement) hits.get(i), true);
				}
			} 
			break;

		case EuclidianView.MODE_MIRROR_AT_POINT:	
		case EuclidianView.MODE_MIRROR_AT_LINE:
		case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			processSelectionRectangleForTransformations(hits, Mirrorable.class);									
			break;

		case EuclidianView.MODE_ROTATE_BY_ANGLE:
			processSelectionRectangleForTransformations(hits, PointRotateable.class);									
			break;		

		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
			processSelectionRectangleForTransformations(hits, Translateable.class);									
			break;	

		case EuclidianView.MODE_DILATE_FROM_POINT:
			processSelectionRectangleForTransformations(hits, Dilateable.class);									
			break;	

		case EuclidianView.MODE_FITLINE:
			processSelectionRectangleForTransformations(hits, GeoPoint.class);									
			processMode(hits, e);
			view.setSelectionRectangle(null);	


			break;	

		default:
			// STANDARD CASE
			app.setSelectedGeos(hits);

		// if alt pressed, create list of objects as string and copy to input bar
			if (hits.size() > 0 && e.isAltDown() && app.hasGuiManager() && app.showAlgebraInput()) {
	
				JTextComponent textComponent = app.getGuiManager().getAlgebraInputTextField();				
	
				StringBuffer sb = new StringBuffer();
				sb.append(" {");
				for (int i = 0 ; i < hits.size() ; i++ ) {
					sb.append(((GeoElement)hits.get(i)).getLabel());
					if (i < hits.size() - 1 ) sb.append(", ");
				}
				sb.append("} ");
				//Application.debug(sb+"");
				textComponent.replaceSelection(sb.toString());
			}
			break;
		}

		kernel.notifyRepaint();
	}

	protected void processSelectionRectangleForTransformations(ArrayList hits, Class transformationInterface) {
		for (int i=0; i < hits.size(); i++) {
			GeoElement geo = (GeoElement) hits.get(i);
			if (!(transformationInterface.isInstance(geo) || geo.isGeoPolygon())) {
				hits.remove(i);
			}
		}	
		removeParentPoints(hits);				
		selectedGeos.addAll(hits);
		app.setSelectedGeos(hits);
	}

	final public void mouseMoved(MouseEvent e) {		
		setMouseLocation(e);
		boolean repaintNeeded;

		// reset icon
		if (hitResetIcon()) {
			view.setToolTipText(app.getPlain("resetConstruction"));
			view.setHitCursor();
			return;
		} 

		// animation button
		boolean hitAnimationButton = view.hitAnimationButton(e);
		repaintNeeded = view.setAnimationButtonsHighlighted(hitAnimationButton);
		if (hitAnimationButton) {
			if (kernel.isAnimationPaused())
				view.setToolTipText(app.getPlain("Play"));
			else 
				view.setToolTipText(app.getPlain("Pause"));		
			view.setHitCursor();
			view.repaint();
			return;
		}			

		// standard handling
		ArrayList hits = null;
		boolean noHighlighting = false;		
		altDown=e.isAltDown();		

		// label hit in move mode: block all other hits
		if (mode == EuclidianView.MODE_MOVE) {
			GeoElement geo = view.getLabelHit(mouseLoc);
			if (geo != null) {				

				noHighlighting = true;
				tempArrayList.clear();
				tempArrayList.add(geo);
				hits = tempArrayList;				
			}
		}
		else if (mode == EuclidianView.MODE_POINT) {
			// include polygons in hits
			hits = view.getHits(mouseLoc, true);
		}

		if (hits == null)
			hits = view.getHits(mouseLoc);
		if (hits == null) {
			view.setToolTipText(null);
			view.setDefaultCursor();	
		}			
		else
			view.setHitCursor();

		//	manage highlighting
		repaintNeeded = noHighlighting ?  refreshHighlighting(null) : refreshHighlighting(hits) 
				|| repaintNeeded;

		// set tool tip text
		// the tooltips are only shown if algebra view is visible
		if (app.showAlgebraView()) {
			hits = view.getTopHits(hits);
			if (hits != null) {
				String text = GeoElement.getToolTipDescriptionHTML(hits,
						true, true);				
				view.setToolTipText(text);
			} else
				view.setToolTipText(null);
		}

		// update previewable
		if (view.previewDrawable != null) {
			view.previewDrawable.updateMousePos(mouseLoc.x, mouseLoc.y);
			repaintNeeded = true;
		}

		// show Mouse coordinates
		if (view.showMouseCoords) {
			transformCoords();
			repaintNeeded = true;
		}		

		if (repaintNeeded) {
			kernel.notifyRepaint();
		}
	}


	protected void doSingleHighlighting(GeoElement geo) {
		if (geo == null) return;

		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
		}		

		highlightedGeos.add(geo);
		geo.setHighlighted(true); 
		kernel.notifyRepaint();					
	}

	// mode specific highlighting of selectable objects
	// returns wheter repaint is necessary
	final boolean refreshHighlighting(ArrayList hits) {
		boolean repaintNeeded = false;

		//	clear old highlighting
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(false);
			repaintNeeded = true;
		}

		// find new objects to highlight
		highlightedGeos.clear();	
		selectionPreview = true; // only preview selection, see also
		// mouseReleased()
		processMode(hits, null); // build highlightedGeos List
		selectionPreview = false; // reactivate selection in mouseReleased()

		// set highlighted objects
		if (highlightedGeos.size() > 0) {
			setHighlightedGeos(true); 
			repaintNeeded = true;
		}		
		return repaintNeeded;
	}

	//	set highlighted state of all highlighted geos without repainting
	protected final void setHighlightedGeos(boolean highlight) {
		GeoElement geo;
		Iterator it = highlightedGeos.iterator();
		while (it.hasNext()) {
			geo = (GeoElement) it.next();
			geo.setHighlighted(highlight);
		}
	}

	// process mode and return whether kernel was changed
	final boolean processMode(ArrayList hits, MouseEvent e) {
		boolean changedKernel = false;
		switch (mode) {
		case EuclidianView.MODE_MOVE:
			// move() is for highlighting and selecting
			if (selectionPreview) {			
				move(view.getTopHits(hits));				
			} else {
				if (DRAGGING_OCCURED && app.selectedGeosSize() == 1)
					app.clearSelectedGeos();

			}
			break;			

		case EuclidianView.MODE_MOVE_ROTATE:
			// moveRotate() is a dummy function for highlighting only
			if (selectionPreview) {
				moveRotate(view.getTopHits(hits));
			}
			break;

		case EuclidianView.MODE_RECORD_TO_SPREADSHEET:
			//if (selectionPreview) 
		{
			changedKernel = record(view.getTopHits(hits), e);
		}
		break;

		case EuclidianView.MODE_POINT:
			// point() is dummy function for highlighting only
			if (selectionPreview) {
				point(view.getHitsForNewPointMode(hits));
			}
			break;

			// copy geo to algebra input
		case EuclidianView.MODE_SELECTION_LISTENER:
			boolean addToSelection = e != null && (Application.isControlDown(e));
			geoElementSelected(view.getTopHits(hits), addToSelection);
			break;

			// new line through two points
		case EuclidianView.MODE_JOIN:
			changedKernel = join(hits);
			break;

			// new segment through two points
		case EuclidianView.MODE_SEGMENT:
			changedKernel = segment(hits);
			break;

			// segment for point and number
		case EuclidianView.MODE_SEGMENT_FIXED:
			changedKernel = segmentFixed(hits);
			break;

			//	angle for two points and number
		case EuclidianView.MODE_ANGLE_FIXED:
			changedKernel = angleFixed(hits);
			break;

		case EuclidianView.MODE_MIDPOINT:
			changedKernel = midpoint(hits);
			break;

			// new ray through two points or point and vector
		case EuclidianView.MODE_RAY:
			changedKernel = ray(hits);
			break;

			// new polygon through points
		case EuclidianView.MODE_POLYGON:
			changedKernel = polygon(hits);
			break;

			// new vector between two points
		case EuclidianView.MODE_VECTOR:
			changedKernel = vector(hits);
			break;

			// intersect two objects
		case EuclidianView.MODE_INTERSECT:
			changedKernel = intersect(hits);
			break;

			// new line through point with direction of vector or line
		case EuclidianView.MODE_PARALLEL:
			changedKernel = parallel(hits);
			break;

			// Michael Borcherds 2008-04-08
		case EuclidianView.MODE_PARABOLA:
			changedKernel = parabola(hits);
			break;

			// new line through point orthogonal to vector or line
		case EuclidianView.MODE_ORTHOGONAL:
			changedKernel = orthogonal(hits);
			break;

			// new line bisector
		case EuclidianView.MODE_LINE_BISECTOR:
			changedKernel = lineBisector(hits);
			break;

			// new angular bisector
		case EuclidianView.MODE_ANGULAR_BISECTOR:
			changedKernel = angularBisector(hits);
			break;

			// new circle (2 points)
		case EuclidianView.MODE_CIRCLE_TWO_POINTS:
			// new semicircle (2 points)
		case EuclidianView.MODE_SEMICIRCLE:
			changedKernel = circle2(hits, mode);
			break;

		case EuclidianView.MODE_LOCUS:
			changedKernel = locus(hits);
			break;

			// new circle (3 points)
		case EuclidianView.MODE_CIRCLE_THREE_POINTS:
		case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
		case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
		case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
			changedKernel = threePoints(hits, mode);
			break;

			// new conic (5 points)
		case EuclidianView.MODE_CONIC_FIVE_POINTS:
			changedKernel = conic5(hits);
			break;

			// relation query
		case EuclidianView.MODE_RELATION:
			relation(view.getTopHits(hits));			
			break;

			// new tangents
		case EuclidianView.MODE_TANGENTS:
			changedKernel = tangents(view.getTopHits(hits));
			break;

		case EuclidianView.MODE_POLAR_DIAMETER:
			changedKernel = polarLine(view.getTopHits(hits));
			break;

			// delete selected object
		case EuclidianView.MODE_DELETE:
			changedKernel = delete(view.getTopHits(hits));
			break;

		case EuclidianView.MODE_SHOW_HIDE_OBJECT:
			if (showHideObject(view.getTopHits(hits)))
				toggleModeChangedKernel = true;
			break;

		case EuclidianView.MODE_SHOW_HIDE_LABEL:
			if (showHideLabel(view.getTopHits(hits)))
				toggleModeChangedKernel = true;
			break;

		case EuclidianView.MODE_COPY_VISUAL_STYLE:
			if (copyVisualStyle(view.getTopHits(hits)))
				toggleModeChangedKernel = true;
			break;

			//  new text or image
		case EuclidianView.MODE_TEXT:
		case EuclidianView.MODE_IMAGE:
			changedKernel = textImage(view.getOtherHits(hits, GeoImage.class, tempArrayList), mode, altDown); //e.isAltDown());
			break;

			// new slider
		case EuclidianView.MODE_SLIDER:
			changedKernel = slider();
			break;			

		case EuclidianView.MODE_MIRROR_AT_POINT:
			changedKernel = mirrorAtPoint(view.getTopHits(hits));
			break;

		case EuclidianView.MODE_MIRROR_AT_LINE:
			changedKernel = mirrorAtLine(view.getTopHits(hits));
			break;

		case EuclidianView.MODE_MIRROR_AT_CIRCLE: // Michael Borcherds 2008-03-23
			changedKernel = mirrorAtCircle(view.getTopHits(hits));
			break;

		case EuclidianView.MODE_TRANSLATE_BY_VECTOR:
			changedKernel = translateByVector(view.getTopHits(hits));
			break;

		case EuclidianView.MODE_ROTATE_BY_ANGLE:
			changedKernel = rotateByAngle(view.getTopHits(hits));
			break;

		case EuclidianView.MODE_DILATE_FROM_POINT:
			changedKernel = dilateFromPoint(view.getTopHits(hits));
			break;

		case EuclidianView.MODE_FITLINE:
			changedKernel = fitLine(hits);
			break;

		case EuclidianView.MODE_CIRCLE_POINT_RADIUS:
			changedKernel = circlePointRadius(hits);
			break;				

		case EuclidianView.MODE_ANGLE:
			changedKernel = angle(view.getTopHits(hits));
			break;

		case EuclidianView.MODE_VECTOR_FROM_POINT:
			changedKernel = vectorFromPoint(hits);
			break;

		case EuclidianView.MODE_DISTANCE:
			changedKernel = distance(hits, e);
			break;	

		case EuclidianView.MODE_MACRO:			
			changedKernel = macro(hits);
			break;

		case EuclidianView.MODE_AREA:
			changedKernel = area(hits, e);
			break;	

		case EuclidianView.MODE_SLOPE:
			changedKernel = slope(hits);
			break;

		case EuclidianView.MODE_REGULAR_POLYGON:
			changedKernel = regularPolygon(hits);
			break;

		case EuclidianView.MODE_SHOW_HIDE_CHECKBOX:
			changedKernel = showCheckBox(hits);
			break;

			// Michael Borcherds 2008-03-13	
		case EuclidianView.MODE_COMPASSES:
			changedKernel = compasses(hits);
			break;

		default:
			// do nothing
		}

		// update preview
		if (view.previewDrawable != null) {
			view.previewDrawable.updatePreview();
			if (mouseLoc != null)
				view.previewDrawable.updateMousePos(mouseLoc.x, mouseLoc.y);			
			view.repaint();
		}

		return changedKernel;
	}

	final public void mouseEntered(MouseEvent e) {
		initToolTipManager();
		initShowMouseCoords();
	}

	final public void mouseExited(MouseEvent e) {
		refreshHighlighting(null);
		resetToolTipManager();
		view.setAnimationButtonsHighlighted(false);
		view.showMouseCoords = false;
		mouseLoc = null;		
		view.repaint();
	}

	/*
	public void focusGained(FocusEvent e) {				
		initToolTipManager();
	}

	public void focusLost(FocusEvent e) {
		resetToolTipManager();
	}*/

	protected void initToolTipManager() {
		// set tooltip manager
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY / 2);
		ttm.setEnabled(true);
	}

	protected void resetToolTipManager() {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(DEFAULT_INITIAL_DELAY);
	}

	/* ****************************************************** */

	final protected void rotateObject(boolean repaint) {
		double angle = Math.atan2(yRW - rotationCenter.inhomY, 
				xRW - rotationCenter.inhomX)
				- rotStartAngle;

		tempNum.set(angle);
		rotGeoElement.set(rotStartGeo);	
		((PointRotateable) rotGeoElement).rotate(tempNum, rotationCenter);

		if (repaint)
			rotGeoElement.updateRepaint();
		else
			rotGeoElement.updateCascade();
	}

	final protected void moveLabel() {
		movedLabelGeoElement.setLabelOffset(oldLoc.x + mouseLoc.x
				- startLoc.x, oldLoc.y + mouseLoc.y - startLoc.y);
		// no update cascade needed
		movedLabelGeoElement.update();  
		kernel.notifyRepaint();
	}

	final protected void movePoint(boolean repaint) {
		movedGeoPoint.setCoords(xRW, yRW, 1.0);
		movedGeoPoint.updateCascade();	
		movedGeoPointDragged = true;

		if (repaint)
			kernel.notifyRepaint();
	}

	final protected void moveLine(boolean repaint) {
		// make parallel geoLine through (xRW, yRW)
		movedGeoLine.setCoords(movedGeoLine.x, movedGeoLine.y, 
				-(movedGeoLine.x * xRW + movedGeoLine.y * yRW));		
		if (repaint)
			movedGeoLine.updateRepaint();
		else
			movedGeoLine.updateCascade();	
	}

	final protected void moveVector(boolean repaint) {
		GeoPoint P = movedGeoVector.getStartPoint();
		if (P == null) {
			movedGeoVector.setCoords(xRW, yRW, 0.0);
		} else {
			movedGeoVector.setCoords(xRW - P.inhomX, yRW - P.inhomY, 0.0);
		}

		if (repaint)
			movedGeoVector.updateRepaint();
		else
			movedGeoVector.updateCascade();	
	}

	final protected void moveVectorStartPoint(boolean repaint) {
		GeoPoint P = movedGeoVector.getStartPoint();
		if (P == null) return;
		
		P.setCoords(xRW, yRW, 1.0);

		if (repaint)
			movedGeoVector.updateRepaint();
		else
			movedGeoVector.updateCascade();	
	}

	final protected void moveText(boolean repaint) {
		if (movedGeoText.isAbsoluteScreenLocActive()) {
			movedGeoText.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
					oldLoc.y + mouseLoc.y-startLoc.y);			
		} else {
			if (movedGeoText.hasAbsoluteLocation()) {
				//	absolute location: change location
				GeoPoint loc = movedGeoText.getStartPoint();				
				loc.setCoords(xRW - startPoint.x, yRW - startPoint.y, 1.0);
			} else {
				// relative location: move label (change label offset)
				movedGeoText.setLabelOffset(oldLoc.x + mouseLoc.x
						- startLoc.x, oldLoc.y + mouseLoc.y - startLoc.y);
			}
		}				

		if (repaint)
			movedGeoText.updateRepaint();
		else
			movedGeoText.updateCascade();	
	}

	final protected void moveImage(boolean repaint) {	
		if (movedGeoImage.isAbsoluteScreenLocActive()) {
			movedGeoImage.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
					oldLoc.y + mouseLoc.y-startLoc.y);			

			if (repaint)
				movedGeoImage.updateRepaint();
			else
				movedGeoImage.updateCascade();
		} else {
			if (movedGeoImage.hasAbsoluteLocation()) {
				//	absolute location: translate all defined corners
				double vx = xRW - startPoint.x;
				double vy = yRW - startPoint.y;
				movedGeoImage.set(oldImage);
				for (int i=0; i < 3; i++) {
					GeoPoint corner = movedGeoImage.getCorner(i);
					if (corner != null) {
						corner.setCoords(corner.inhomX + vx, corner.inhomY + vy, 1.0);
					}
				}

				if (repaint)
					movedGeoImage.updateRepaint();
				else
					movedGeoImage.updateCascade();
			} 	
		}
	}

	final protected void moveConic(boolean repaint) {
		movedGeoConic.set(tempConic);
		movedGeoConic.translate(xRW - startPoint.x, yRW - startPoint.y);		

		if (repaint)
			movedGeoConic.updateRepaint();
		else
			movedGeoConic.updateCascade();
	}

	final protected void moveFunction(boolean repaint) {	
		movedGeoFunction.set(tempFunction);
		movedGeoFunction.translate(xRW - startPoint.x, yRW - startPoint.y);		

		if (repaint)
			movedGeoFunction.updateRepaint();
		else
			movedGeoFunction.updateCascade();
	}

	final protected void moveBoolean(boolean repaint) {
		movedGeoBoolean.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
				oldLoc.y + mouseLoc.y-startLoc.y);

		if (repaint)
			movedGeoBoolean.updateRepaint();
		else
			movedGeoBoolean.updateCascade();
	}

	final protected void moveNumeric(boolean repaint) {
		double min = movedGeoNumeric.getIntervalMin();
		double max = movedGeoNumeric.getIntervalMax();
		double param;
		if (movedGeoNumeric.isSliderHorizontal()) {
			if (movedGeoNumeric.isAbsoluteScreenLocActive()) {				
				param = mouseLoc.x - startPoint.x;
			} else {
				param = xRW - startPoint.x;
			}
		}			
		else {
			if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
				param = startPoint.y - mouseLoc.y ;
			} else {
				param = yRW - startPoint.y;
			}
		}							
		param = param * (max - min) / movedGeoNumeric.getSliderWidth();					

		// round to animation step scale				
		param = Kernel.roundToScale(param, movedGeoNumeric.animationIncrement);
		double val = min + param;	

		if (movedGeoNumeric.animationIncrement > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			val = kernel.checkDecimalFraction(val, 1 / movedGeoNumeric.animationIncrement);
		}

		if (movedGeoNumeric.isGeoAngle()) {
			if (val < 0) 
				val = 0;
			else if (val > Kernel.PI_2)
				val = Kernel.PI_2;
			
			val = kernel.checkDecimalFraction(val * Kernel.CONST_180_PI) / Kernel.CONST_180_PI;

		}

		// do not set value unless it really changed!
		if (movedGeoNumeric.getValue() == val)
			return;

		movedGeoNumeric.setValue(val);		
		movedGeoNumericDragged = true;

		//movedGeoNumeric.setAnimating(false); // stop animation if slider dragged

		//if (repaint)
		movedGeoNumeric.updateRepaint();
		//else
		//	movedGeoNumeric.updateCascade();
	}

	final protected void moveSlider(boolean repaint) {
		if (movedGeoNumeric.isAbsoluteScreenLocActive()) {
			movedGeoNumeric.setAbsoluteScreenLoc( oldLoc.x + mouseLoc.x-startLoc.x, 
					oldLoc.y + mouseLoc.y-startLoc.y);
		} else {
			movedGeoNumeric.setSliderLocation(xRW - startPoint.x, yRW - startPoint.y);
		}

		// don't cascade, only position of the slider has changed
		movedGeoNumeric.update();

		if (repaint)
			kernel.notifyRepaint();			
	}

	final protected void moveDependent(boolean repaint) {
		translationVec.setCoords(xRW - startPoint.x, yRW - startPoint.y, 0.0);
		startPoint.setLocation(xRW, yRW);

		// we don't specify screen coords for translation as all objects are Translateables
		GeoElement.moveObjects(translateableGeos, translationVec, startPoint);		
		if (repaint)
			kernel.notifyRepaint();						
	}

	protected void moveMultipleObjects(boolean repaint) {		
		translationVec.setCoords(xRW - startPoint.x, yRW - startPoint.y, 0.0);		
		startPoint.setLocation(xRW, yRW);
		startLoc = mouseLoc;

		// move all selected geos
		GeoElement.moveObjects(app.getSelectedGeos(), translationVec, startPoint);									

		if (repaint)
			kernel.notifyRepaint();					
	}		




	/**
	 * COORD TRANSFORM SCREEN -> REAL WORLD
	 * 
	 * real world coords -> screen coords 
	 *     ( xscale 0 xZero ) 
	 * T = ( 0 -yscale yZero ) 
	 *     ( 0 0 1 )
	 * 
	 * screen coords -> real world coords 
	 *          ( 1/xscale 0 -xZero/xscale ) 
	 * T^(-1) = ( 0 -1/yscale yZero/yscale ) 
	 *          ( 0 0 1 )
	 */

	/*
	protected void transformCoords() {
		transformCoords(false);
	} */

	final protected void transformCoords() {
		// calc real world coords
		calcRWcoords();

		// if alt pressed, make sure slope is a multiple of 15 degrees
		if ((mode == EuclidianView.MODE_JOIN || mode == EuclidianView.MODE_SEGMENT
				|| mode == EuclidianView.MODE_RAY || mode == EuclidianView.MODE_VECTOR)
				&& useLineEndPoint && lineEndPoint != null) {
			xRW = lineEndPoint.x;
			yRW = lineEndPoint.y;
			return;
		}
		
		
		
		if (mode == EuclidianView.MODE_MOVE && 
				moveMode == MOVE_NUMERIC) return; // Michael Borcherds 2008-03-24 bugfix: don't want grid on

		//	point capturing to grid
		double pointCapturingPercentage = 1;
		switch (view.getPointCapturingMode()) {	
		case EuclidianView.POINT_CAPTURING_AUTOMATIC:				
			if (!view.isGridOrAxesShown())break;

		case EuclidianView.POINT_CAPTURING_ON:
			pointCapturingPercentage = 0.125;

		case EuclidianView.POINT_CAPTURING_ON_GRID:

			switch (view.getGridType()) {
			case EuclidianView.GRID_ISOMETRIC:

				// isometric Michael Borcherds 2008-04-28
				// iso grid is effectively two rectangular grids overlayed (offset)
				// so first we decide which one we're on (oddOrEvenRow)
				// then compress the grid by a scale factor of root3 horizontally to make it square.

				double root3=Math.sqrt(3.0);
				double isoGrid=view.gridDistances[0];
				int oddOrEvenRow = (int)Math.round(2.0*Math.abs(yRW - Kernel.roundToScale(yRW, isoGrid))/isoGrid);

				//Application.debug(oddOrEvenRow);

				if (oddOrEvenRow == 0)
				{
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW/root3, isoGrid);
					double y = Kernel.roundToScale(yRW, isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - xRW/root3);
					double b = Math.abs(y - yRW);
					if (a < isoGrid * pointCapturingPercentage
							&& b < isoGrid *  pointCapturingPercentage) {
						xRW = x*root3;
						yRW = y;
					}

				}
				else
				{
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW/root3- view.gridDistances[0]/2, isoGrid);
					double y = Kernel.roundToScale(yRW- isoGrid/2, isoGrid);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - (xRW/root3- isoGrid/2));
					double b = Math.abs(y - (yRW-isoGrid/2));
					if (a < isoGrid * pointCapturingPercentage
							&& b < isoGrid *  pointCapturingPercentage) {
						xRW = (x+ isoGrid/2)*root3;
						yRW = y+ isoGrid/2;
					}

				}
				break;

			case EuclidianView.GRID_CARTESIAN:

				// X = (x, y) ... next grid point
				double x = Kernel.roundToScale(xRW, view.gridDistances[0]);
				double y = Kernel.roundToScale(yRW, view.gridDistances[1]);
				// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
				double a = Math.abs(x - xRW);
				double b = Math.abs(y - yRW);
				if (a < view.gridDistances[0] * pointCapturingPercentage
						&& b < view.gridDistances[1] *  pointCapturingPercentage) {
					xRW = x;
					yRW = y;
				}
				break;
			}

		default:
		}
	}

	/*
	final protected void transformCoords(boolean usePointCapturing) {
		// calc real world coords
		calcRWcoords();

		if (usePointCapturing) {	
			double pointCapturingPercentage = 1;
			switch (view.getPointCapturingMode()) {		
				case EuclidianView.POINT_CAPTURING_AUTOMATIC:
					if (!view.isGridOrAxesShown())break;

				case EuclidianView.POINT_CAPTURING_ON:
					pointCapturingPercentage = 0.125;

				case EuclidianView.POINT_CAPTURING_ON_GRID:								
					// X = (x, y) ... next grid point
					double x = Kernel.roundToScale(xRW, view.gridDistances[0]);
					double y = Kernel.roundToScale(yRW, view.gridDistances[1]);
					// if |X - XRW| < gridInterval * pointCapturingPercentage  then take the grid point
					double a = Math.abs(x - xRW);
					double b = Math.abs(y - yRW);
					if (a < view.gridDistances[0] * pointCapturingPercentage
						&& b < view.gridDistances[1] *  pointCapturingPercentage) {
						xRW = x;
						yRW = y;
						mouseLoc.x = view.toScreenCoordX(xRW);
						mouseLoc.y = view.toScreenCoordY(yRW);
					}

				default:
					// point capturing off
			}		
		}
	}
	 */

	protected void calcRWcoords() {
		xRW = (mouseLoc.x - view.xZero) * view.invXscale;
		yRW = (view.yZero - mouseLoc.y) * view.invYscale;
	}

	final protected void setMouseLocation(MouseEvent e) {
		mouseLoc = e.getPoint();
		
		altDown = e.isAltDown();

		if (mouseLoc.x < 0)
			mouseLoc.x = 0;
		else if (mouseLoc.x > view.width)
			mouseLoc.x = view.width;
		if (mouseLoc.y < 0)
			mouseLoc.y = 0;
		else if (mouseLoc.y > view.height)
			mouseLoc.y = view.height;
	}

	/***************************************************************************
	 * mode implementations
	 * 
	 * the following methods return true if a factory method of the kernel was
	 * called
	 **************************************************************************/

	private boolean allowPointCreation() {
		return mode == EuclidianView.MODE_POINT || app.isOnTheFlyPointCreationActive(); 
	}
	
	
	// create new point at current position if hits is null
	// or on path
	// or intersection point
	// returns wether new point was created or not
	final protected boolean createNewPoint(ArrayList hits,
			boolean onPathPossible, boolean intersectPossible, boolean doSingleHighlighting) {

		if (!allowPointCreation()) 
			return false;
		
		// only keep polygon in hits if one side of polygon is in hits too
		if (hits != null)
			hits = view.getHitsForNewPointMode(hits);

		Path path = null;		
		boolean createPoint = !view.containsGeoPoint(hits);
		GeoPoint point = null;

		//	try to get an intersection point
		if (createPoint && intersectPossible) {
			point = getSingleIntersectionPoint(hits);
			if (point != null) {
				// we don't use an undefined or infinite
				// intersection point
				if (!point.showInEuclidianView()) {
					point.remove();
				} else
					createPoint = false;
			}
		}

		//	check if point lies on path and if we are allowed to place a point
		// on a path
		if (createPoint) {
			ArrayList pathHits = view.getHits(hits, Path.class, tempArrayList);
			if (pathHits != null) {
				if (onPathPossible) {
					path = (Path) chooseGeo(pathHits, true);
					createPoint = path != null;
				} else {
					createPoint = false;
				}
			}
		}

		if (createPoint) {
			transformCoords(); // use point capturing if on
			if (path == null) {
				point = kernel.Point(null, xRW, yRW);
				view.showMouseCoords = true;
			} else {
				point = kernel.Point(null, path, xRW, yRW);
			}
		}

		if (point != null) {
			movedGeoPoint = point;
			movedGeoElement = movedGeoPoint;
			moveMode = MOVE_POINT;
			view.setDragCursor();
			if (doSingleHighlighting)
				doSingleHighlighting(movedGeoPoint);
			POINT_CREATED = true;
			return true;
		} else {
			moveMode = MOVE_NONE;
			POINT_CREATED = false;
			return false;
		}
	}

	// get two points and create line through them
	final protected boolean join(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Line(null, points[0], points[1]);
			return true;
		}
		return false;
	}
	
	private void recordSingleObjectToSpreadSheet(GeoElement geo) {
		int i = 1;
		while (kernel.lookupLabel("A"+i) != null) i++;
		
    	kernel.getApplication().getGuiManager().setScrollToShow(true);
		if (geo.isGeoPoint()) {
		
			// find first empty pair of cells in columns A, B
			while (kernel.lookupLabel("A"+i) != null || kernel.lookupLabel("B"+i) != null) i++;
			
			GeoPoint p = (GeoPoint)geo;
			
			GeoNumeric num = new GeoNumeric(kernel.getConstruction(), "A"+i, p.inhomX);
			num.setAuxiliaryObject(true);
			num = new GeoNumeric(kernel.getConstruction(), "B"+i, p.inhomY);
			num.setAuxiliaryObject(true);				
		} else if (geo.isGeoVector()) {
			// find first empty pair of cells in columns A, B
			while (kernel.lookupLabel("A"+i) != null || kernel.lookupLabel("B"+i) != null) i++;
			
			GeoVector v = (GeoVector)geo;
			
			double [] coords = new double[2];
			v.getInhomCoords(coords);
			
			GeoNumeric num = new GeoNumeric(kernel.getConstruction(), "A"+i, coords[0]);
			num.setAuxiliaryObject(true);				
			num = new GeoNumeric(kernel.getConstruction(), "B"+i, coords[1]);
			num.setAuxiliaryObject(true);				
			
		} else if (geo.isGeoNumeric()) {
			
			GeoNumeric num = new GeoNumeric(kernel.getConstruction(), "A"+i, ((NumberValue)geo).getDouble());
			num.setAuxiliaryObject(true);
		}
    	kernel.getApplication().getGuiManager().setScrollToShow(false);
		
	}

	protected void handleMousePressedForRecordToSpreadsheetMode(MouseEvent e) {	
		GeoElement geo;
		ArrayList hits;

		ArrayList pointHits = view.getHits(mouseLoc, GeoPoint.class, tempArrayList);
		ArrayList vectorHits = view.getHits(mouseLoc, GeoVector.class, tempArrayList2);
		ArrayList numberHits = view.getHits(mouseLoc, GeoNumeric.class, tempArrayList3);
		//Application.debug(pointHits.size()+"");
		// we need the object to record
		if (recordObject == null) {
			
			// alt-click on object: special mode, just put it straight in the spreadsheet in column A (and B for Points)		
			if (e.isAltDown()) {
				
				if (pointHits != null)
					recordSingleObjectToSpreadSheet((GeoPoint)pointHits.get(0));
				else if (vectorHits != null)
					recordSingleObjectToSpreadSheet((GeoVector)vectorHits.get(0));
				else if (pointHits != null)
					recordSingleObjectToSpreadSheet((GeoNumeric)numberHits.get(0));
				
				return;
				
			}
			
			
			
			
			
			
			if (pointHits != null) {
				recordObject = (GeoPoint)pointHits.get(0);
				//app.addSelectedGeo(recordObject);
				recordObject.setSelected(true);
				resetSpreadsheetRecording();
			} else if (vectorHits != null) {
				recordObject = (GeoVector)vectorHits.get(0);
				//app.addSelectedGeo(recordObject);
				recordObject.setSelected(true);
				resetSpreadsheetRecording();
			}
		}
		else {	// recordObject != null
			hits = view.getPointVectorNumericHits(mouseLoc);
			// got recordObject again: deselect
			if (hits != null && hits.contains(recordObject) &&
					// if you drag a point at the end of a vector, we don't want to deselect the vector:
					(!recordObject.isGeoVector() || (recordObject.isGeoVector() && noPointsIn(hits)))		
			) {
				//app.removeSelectedGeo(recordObject);
				recordObject.setSelected(false);
				recordObject = null;
				moveMode = MOVE_NONE;
				return;
			}

			//moveModeSelectionHandled = true;

			if (movedGeoPoint != null) movedGeoPoint.setSelected(false);
			if (movedGeoNumeric != null) movedGeoNumeric.setSelected(false);

			//hits = view.getHits(hits, GeoPoint.class, tempArrayList);
			if (pointHits != null && pointHits.contains(movedGeoPoint))
			{
				movedGeoPoint.setSelected(true);
				moveMode = MOVE_POINT;
			}
			else if (pointHits != null) {
				movedGeoPoint = (GeoPoint)pointHits.get(0);
				movedGeoPoint.setSelected(true);
				moveMode = MOVE_POINT;
			}
			else if (numberHits != null && numberHits.contains(movedGeoNumeric))
			{
				movedGeoNumeric.setSelected(true);
				moveMode = MOVE_NUMERIC;
				startPoint.setLocation(movedGeoNumeric.getSliderX(), movedGeoNumeric.getSliderY());
			}
			else if (numberHits != null) {
				movedGeoNumeric = (GeoNumeric)numberHits.get(0);
				movedGeoNumeric.setSelected(true);
				moveMode = MOVE_NUMERIC;
				startPoint.setLocation(movedGeoNumeric.getSliderX(), movedGeoNumeric.getSliderY());
			}
			else {
				moveMode = MOVE_NONE;
			}

		}					
	}

	private boolean noPointsIn(ArrayList hits)
	{
		for (int i = 0 ; i < hits.size(); i++) {
			if ( ((GeoElement)(hits.get(i))).isGeoPoint()) return false;
		}
		return true;
	}

	final protected boolean record(ArrayList hits, MouseEvent e) {
		if (hits == null)
			return false;

		// check how many interesting hits we have
		if (!selectionPreview && hits.size() > 2 - selGeos()) {
			ArrayList goodHits = new ArrayList();
			//goodHits.add(selectedGeos);
			view.getHits(hits, GeoPoint.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			view.getHits(hits, GeoNumeric.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			view.getHits(hits, GeoVector.class, tempArrayList);
			goodHits.addAll(tempArrayList);

			if (goodHits.size() > 2 - selGeos()) {
				//  choose one geo, and select only this one
				GeoElement geo = chooseGeo(goodHits, true);
				hits.clear();
				hits.add(geo);				
			} else {
				hits = goodHits;
			}
		}			

		addSelectedPoint(hits, 1, true);
		addSelectedNumeric(hits, 1, true);
		addSelectedVector(hits, 1, true);	

		/*
		if (recordObject != null && selPoints() == 1 && selPoints() == 1 && points[0] == recordObject)
		{
			recordObject.setSelected(false);
			recordObject = null;
			return true;
		}*/

		if (recordObject == null) {
			if (selPoints() == 1) {
				GeoPoint[] points = getSelectedPoints();
				
				if (e.isAltDown()) {
					recordSingleObjectToSpreadSheet(points[0]);
				}
				else
				{
					recordObject = points[0];
					resetSpreadsheetRecording();
				}
			}
			else if (selNumbers() == 1) {
				GeoNumeric[] nums = getSelectedNumbers();
				if (e.isAltDown()) {
					recordSingleObjectToSpreadSheet(nums[0]);
				}
				else {
					recordObject = nums[0];
					resetSpreadsheetRecording();
				}
			}
			else if (selVectors() == 1) {
				GeoVector[] vecs = getSelectedVectors();
				if (e.isAltDown()) {
					recordSingleObjectToSpreadSheet(vecs[0]);
				}
				else {
					recordObject = vecs[0];
					resetSpreadsheetRecording();
				}
			}
			if (recordObject != null) recordObject.setSelected(true);
			//return true;
		} else { // recordObject != null
			if (selPoints() == 1)
			{
				GeoPoint[] points = getSelectedPoints();
				if (points[0] == recordObject) {
					recordObject.setSelected(false);
					recordObject = null;
					resetSpreadsheetRecording();
				}
			}
			else if (selNumbers() == 1) {
				GeoNumeric[] nums = getSelectedNumbers();
				if (recordObject == nums[0]) {
					recordObject.setSelected(false);
					recordObject = null;
					resetSpreadsheetRecording();
				}
			}
			else if (selVectors() == 1) {
				GeoVector[] vecs = getSelectedVectors();
				if (recordObject == vecs[0]) {
					recordObject.setSelected(false);
					recordObject = null;
					resetSpreadsheetRecording();
				}
			}
			if (recordObject != null) recordObject.setSelected(true);
			//return true;
		}


		if (selGeos() > 1)
			return false;

		return false;
	}

	private void resetSpreadsheetRecording() {
		moveMode = MOVE_NONE;
		if (recordObject != null) {
			recordObject.resetTraceColumns();
			recordObject.updateRepaint(); // force repaint to put first point in spreadsheet
		}
		movedGeoPoint = null;
		movedGeoNumeric = null;
		//view.resetTraceRow();		
	}

	//	get two points and create line through them
	final protected boolean segment(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Segment(null, points[0], points[1]);
			return true;
		}
		return false;
	}

	// get two points and create vector between them
	final protected boolean vector(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Vector(null, points[0], points[1]);
			return true;
		}
		return false;
	}

	//	get two points and create ray with them
	final protected boolean ray(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Ray(null, points[0], points[1]);
			return true;
		}

		return false;
	}

	//	get at least 3 points and create polygon with them
	final protected boolean polygon(ArrayList hits) {
		if (hits == null)
			return false;

		// if the first point is clicked again, we are finished
		if (selPoints() > 2) {
			// check if first point was clicked again
			boolean finished = !selectionPreview
			&& hits.contains(selectedPoints.get(0));
			if (finished) {
				// build polygon
				kernel.Polygon(null, getSelectedPoints());
				return true;
			}
		}

		// points needed
		addSelectedPoint(hits, GeoPolygon.POLYGON_MAX_POINTS, false);
		return false;
	}

	// get two objects (lines or conics) and create intersection point
	final protected boolean intersect(ArrayList hits) {
		if (hits == null)
			return false;		

		// when two objects are selected at once then only one single
		// intersection point should be created
		boolean singlePointWanted = selGeos() == 0;

		// check how many interesting hits we have
		if (!selectionPreview && hits.size() > 2 - selGeos()) {
			ArrayList goodHits = new ArrayList();
			//goodHits.add(selectedGeos);
			view.getHits(hits, GeoLine.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			view.getHits(hits, GeoConic.class, tempArrayList);
			goodHits.addAll(tempArrayList);
			view.getHits(hits, GeoFunction.class, tempArrayList);
			goodHits.addAll(tempArrayList);

			if (goodHits.size() > 2 - selGeos()) {
				//  choose one geo, and select only this one
				GeoElement geo = chooseGeo(goodHits, true);
				hits.clear();
				hits.add(geo);				
			} else {
				hits = goodHits;
			}
		}			

		// get lines, conics and functions
		addSelectedLine(hits, 2, true);
		addSelectedConic(hits, 2, true);
		addSelectedFunction(hits, 2, true);				

		singlePointWanted = singlePointWanted && selGeos() == 2;

		if (selGeos() > 2)
			return false;

		// two lines
		if (selLines() == 2) {
			GeoLine[] lines = getSelectedLines();
			kernel.IntersectLines(null, lines[0], lines[1]);
			return true;
		}
		// two conics
		else if (selConics() == 2) {
			GeoConic[] conics = getSelectedConics();
			if (singlePointWanted)
				kernel.IntersectConicsSingle(null, conics[0], conics[1], xRW,
						yRW);
			else
				kernel.IntersectConics(null, conics[0], conics[1]);
			return true;
		} else if (selFunctions() == 2) {
			GeoFunction[] fun = getSelectedFunctions();
			boolean polynomials = fun[0].isPolynomialFunction(false)
			&& fun[1].isPolynomialFunction(false);
			if (!polynomials) {
				GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
				startPoint.setCoords(xRW, yRW, 1.0);
				kernel.IntersectFunctions(null, fun[0], fun[1], startPoint);
			} else {
				// polynomials
				if (singlePointWanted) {
					kernel.IntersectPolynomialsSingle(null, fun[0], fun[1],
							xRW, yRW);
				} else {
					kernel.IntersectPolynomials(null, fun[0], fun[1]);
				}
			}
		}
		// one line and one conic
		else if (selLines() == 1 && selConics() == 1) {
			GeoConic[] conic = getSelectedConics();
			GeoLine[] line = getSelectedLines();
			if (singlePointWanted)
				kernel.IntersectLineConicSingle(null, line[0], conic[0], xRW,
						yRW);
			else
				kernel.IntersectLineConic(null, line[0], conic[0]);

			return true;
		}
		// line and function
		else if (selLines() == 1 && selFunctions() == 1) {
			GeoLine[] line = getSelectedLines();
			GeoFunction[] fun = getSelectedFunctions();
			if (fun[0].isPolynomialFunction(false)) {
				if (singlePointWanted)
					kernel.IntersectPolynomialLineSingle(null, fun[0], line[0],
							xRW, yRW);
				else
					kernel.IntersectPolynomialLine(null, fun[0], line[0]);
			} else {
				GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
				startPoint.setCoords(xRW, yRW, 1.0);
				kernel.IntersectFunctionLine(null, fun[0], line[0], startPoint);
			}
			return true;
		}
		return false;
	}

	// tries to get a single intersection point for the given hits
	// i.e. hits has to include two intersectable objects.
	final protected GeoPoint getSingleIntersectionPoint(ArrayList hits) {
		if (hits == null || hits.size() != 2)
			return null;

		GeoElement a = (GeoElement) hits.get(0);
		GeoElement b = (GeoElement) hits.get(1);

		// first hit is a line
		if (a.isGeoLine()) {
			if (b.isGeoLine())
				if (!((GeoLine) a).linDep((GeoLine) b))
					return kernel.IntersectLines(null, (GeoLine) a, (GeoLine) b);
				else 
					return null;
			else if (b.isGeoConic())
				return kernel.IntersectLineConicSingle(null, (GeoLine) a,
						(GeoConic) b, xRW, yRW);
			else if (b.isGeoFunctionable()) {
				// line and function
				GeoFunction f = ((GeoFunctionable) b).getGeoFunction();
				if (f.isPolynomialFunction(false))
					return kernel.IntersectPolynomialLineSingle(null, f,
							(GeoLine) a, xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, f, (GeoLine) a,
							startPoint);
				}
			} else
				return null;
		}
		//	first hit is a conic
		else if (a.isGeoConic()) {
			if (b.isGeoLine())
				return kernel.IntersectLineConicSingle(null, (GeoLine) b,
						(GeoConic) a, xRW, yRW);
			else if (b.isGeoConic())
				return kernel.IntersectConicsSingle(null, (GeoConic) a,
						(GeoConic) b, xRW, yRW);
			else
				return null;
		}
		// first hit is a function
		else if (a.isGeoFunctionable()) {
			GeoFunction aFun = (GeoFunction) a;
			if (b.isGeoFunctionable()) {
				GeoFunction bFun = ((GeoFunctionable) b).getGeoFunction();
				if (aFun.isPolynomialFunction(false) && bFun.isPolynomialFunction(false))
					return kernel.IntersectPolynomialsSingle(null, aFun, bFun,
							xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctions(null, aFun, bFun,
							startPoint);
				}
			} else if (b.isGeoLine()) {
				// line and function
				GeoFunction f = (GeoFunction) a;
				if (f.isPolynomialFunction(false))
					return kernel.IntersectPolynomialLineSingle(null, f,
							(GeoLine) b, xRW, yRW);
				else {
					GeoPoint startPoint = new GeoPoint(kernel.getConstruction());
					startPoint.setCoords(xRW, yRW, 1.0);
					return kernel.IntersectFunctionLine(null, f, (GeoLine) b,
							startPoint);
				}
			} else
				return null;
		} else
			return null;
	}

	// get point and line or vector;
	// create line through point parallel to line or vector
	final protected boolean parallel(ArrayList hits) {
		if (hits == null)
			return false;

		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}

		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPoint[] points = getSelectedPoints();
				GeoVector[] vectors = getSelectedVectors();
				// create new line
				kernel.Line(null, points[0], vectors[0]);
				return true;
			} else if (selLines() == 1) {
				// fetch selected point and vector
				GeoPoint[] points = getSelectedPoints();
				GeoLine[] lines = getSelectedLines();
				// create new line
				kernel.Line(null, points[0], lines[0]);
				return true;
			}
		}
		return false;
	}

	// get point and line 
	// create parabola (focus and directrix)
	// Michael Borcherds 2008-04-08
	final protected boolean parabola(ArrayList hits) {
		if (hits == null)
			return false;

		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			addSelectedLine(hits, 1, false);
		}

		if (selPoints() == 1) {
			if (selLines() == 1) {
				// fetch selected point and line
				GeoPoint[] points = getSelectedPoints();
				GeoLine[] lines = getSelectedLines();
				// create new parabola
				kernel.Parabola(null, points[0], lines[0]);
				return true;
			}
		}
		return false;
	}

	// get point and line or vector;
	// create line through point orthogonal to line or vector
	final protected boolean orthogonal(ArrayList hits) {
		if (hits == null)
			return false;

		boolean hitPoint = (addSelectedPoint(hits, 1, false) != 0);
		if (!hitPoint) {
			if (selLines() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selVectors() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}

		if (selPoints() == 1) {
			if (selVectors() == 1) {
				// fetch selected point and vector
				GeoPoint[] points = getSelectedPoints();
				GeoVector[] vectors = getSelectedVectors();
				// create new line
				kernel.OrthogonalLine(null, points[0], vectors[0]);
				return true;
			} else if (selLines() == 1) {
				// fetch selected point and vector
				GeoPoint[] points = getSelectedPoints();
				GeoLine[] lines = getSelectedLines();
				// create new line
				kernel.OrthogonalLine(null, points[0], lines[0]);
				return true;
			}
		}
		return false;
	}

	// get two points, line segment or conic
	// and create midpoint/center for them/it
	final protected boolean midpoint(ArrayList hits) {
		if (hits == null)
			return false;

		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		//if (selSegments() == 0)
		//	hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		if (!hitPoint && selPoints() == 0) {
			addSelectedSegment(hits, 1, false); // segment needed
			if (selSegments() == 0)
				addSelectedConic(hits, 1, false); // conic needed
		}

		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Midpoint(null, points[0], points[1]);
			return true;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegment[] segments = getSelectedSegments();
			kernel.Midpoint(null, segments[0]);
			return true;
		} else if (selConics() == 1) {
			// fetch the selected segment
			GeoConic[] conics = getSelectedConics();
			kernel.Center(null, conics[0]);
			return true;
		}
		return false;
	}

	// get two points and create line bisector for them
	// or get line segment and create line bisector for it
	final protected boolean lineBisector(ArrayList hits) {
		if (hits == null)
			return false;
		boolean hitPoint = false;

		if (selSegments() == 0)
			hitPoint = (addSelectedPoint(hits, 2, false) != 0);

		if (!hitPoint && selPoints() == 0)
			addSelectedSegment(hits, 1, false); // segment needed

		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.LineBisector(null, points[0], points[1]);
			return true;
		} else if (selSegments() == 1) {
			// fetch the selected segment
			GeoSegment[] segments = getSelectedSegments();
			kernel.LineBisector(null, segments[0]);
			return true;
		}
		return false;
	}

	// get three points and create angular bisector for them
	// or bisector for two lines
	final protected boolean angularBisector(ArrayList hits) {
		if (hits == null)
			return false;
		boolean hitPoint = false;

		if (selLines() == 0) {
			hitPoint = (addSelectedPoint(hits, 3, false) != 0);
		}
		if (!hitPoint && selPoints() == 0) {
			addSelectedLine(hits, 2, false);
		}

		if (selPoints() == 3) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.AngularBisector(null, points[0], points[1], points[2]);
			return true;
		} else if (selLines() == 2) {
			// fetch the two lines
			GeoLine[] lines = getSelectedLines();
			kernel.AngularBisector(null, lines[0], lines[1]);
			return true;
		}
		return false;
	}

	// get 3 points
	final protected boolean threePoints(ArrayList hits, int mode) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 3, false);
		if (selPoints() == 3) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			switch (mode) {
			case EuclidianView.MODE_CIRCLE_THREE_POINTS:
				kernel.Circle(null, points[0], points[1], points[2]);
				break;

			case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
				kernel.Ellipse(null, points[0], points[1], points[2]);
				break;

			case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
				kernel.Hyperbola(null, points[0], points[1], points[2]);
				break;

			case EuclidianView.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS:
				kernel.CircumcircleArc(null, points[0], points[1], points[2]);
				break;

			case EuclidianView.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS:
				kernel.CircumcircleSector(null, points[0], points[1], points[2]);
				break;

			case EuclidianView.MODE_CIRCLE_ARC_THREE_POINTS:
				kernel.CircleArc(null, points[0], points[1], points[2]);
				break;

			case EuclidianView.MODE_CIRCLE_SECTOR_THREE_POINTS:
				kernel.CircleSector(null, points[0], points[1], points[2]);
				break;												

			default:
				return false;
			}
			return true;
		}
		return false;
	}

	// get 2 lines, 2 vectors or 3 points
	final protected boolean angle(ArrayList hits) {
		if (hits == null)
			return false;				

		int count = 0;
		if (selPoints() == 0) {
			if (selVectors() == 0)
				count = addSelectedLine(hits, 2, false);
			if (selLines() == 0) 
				count = addSelectedVector(hits, 2, false);			
		}		
		if (count == 0)
			count = addSelectedPoint(hits, 3, false);	

		// try polygon too
		boolean polyFound = false;
		if (count == 0)	{		
			polyFound = 1 == addSelectedGeo(view.getHits(hits, GeoPolygon.class, tempArrayList), 
					1, false);
		}

		GeoAngle angle = null;
		GeoAngle [] angles = null;
		if (selPoints() == 3) {
			GeoPoint[] points = getSelectedPoints();
			angle = kernel.Angle(null, points[0], points[1], points[2]);					
		} else if (selVectors() == 2) {
			GeoVector[] vecs = getSelectedVectors();
			angle = kernel.Angle(null, vecs[0], vecs[1]);				
		} else if (selLines() == 2) {
			GeoLine[] lines = getSelectedLines();
			angle = createLineAngle(lines);			
		} else if (polyFound && selGeos() == 1) {
			angles = kernel.Angles(null,(GeoPolygon) getSelectedGeos()[0]);	
		}

		if (angle != null) {
			// commented in V3.0:
			// angle.setAllowReflexAngle(false);
			// 	make sure that we show angle value
			if (angle.isLabelVisible()) 
				angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			else 
				angle.setLabelMode(GeoElement.LABEL_VALUE);
			angle.setLabelVisible(true);
			angle.updateRepaint();
			return true;
		} 
		else if (angles != null) {
			for (int i=0; i < angles.length; i++) {
				//	make sure that we show angle value
				if (angles[i].isLabelVisible()) 
					angles[i].setLabelMode(GeoElement.LABEL_NAME_VALUE);
				else 
					angles[i].setLabelMode(GeoElement.LABEL_VALUE);
				angles[i].setLabelVisible(true);
				angles[i].updateRepaint();
			}
			return true;
		} else
			return false;
	}

	// build angle between two lines
	protected GeoAngle createLineAngle(GeoLine [] lines) {
		GeoAngle angle = null;

		// did we get two segments?
		if (lines[0] instanceof GeoSegment && 
				lines[1] instanceof GeoSegment) {
			// check if the segments have one point in common
			GeoSegment a = (GeoSegment) lines[0];
			GeoSegment b = (GeoSegment) lines[1];
			// get endpoints
			GeoPoint a1 = a.getStartPoint();
			GeoPoint a2 = a.getEndPoint();
			GeoPoint b1 = b.getStartPoint();
			GeoPoint b2 = b.getEndPoint();

			if (a1 == b1) {
				angle = kernel.Angle(null, a2, a1, b2);
			} else if (a1 == b2) {
				angle = kernel.Angle(null, a2, a1, b1);
			} else if (a2 == b1) {
				angle = kernel.Angle(null, a1, a2, b2);
			} else if (a2 == b2) {
				angle = kernel.Angle(null, a1, a2, b1);
			}			
		}

		if (angle == null)
			angle = kernel.Angle(null, lines[0], lines[1]);

		return angle;
	}

	// get 2 points
	final protected boolean circle2(ArrayList hits, int mode) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			if (mode == EuclidianView.MODE_SEMICIRCLE)
				kernel.Semicircle(null, points[0], points[1]);
			else
				kernel.Circle(null, points[0], points[1]);
			return true;
		}
		return false;
	}

	// get 2 points for locus
	// first point 
	final protected boolean locus(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 2, false);
		if (selPoints() == 2) {
			// fetch the two selected points
			GeoPoint[] points = getSelectedPoints();
			GeoLocus locus;
			if (points[0].getPath() == null) {
				locus = kernel.Locus(null, points[0], points[1]);
			} else {
				locus = kernel.Locus(null, points[1], points[0]);
			}				
			return locus != null;
		}
		return false;
	}

	// get 5 points
	final protected boolean conic5(ArrayList hits) {
		if (hits == null)
			return false;

		// points needed
		addSelectedPoint(hits, 5, false);
		if (selPoints() == 5) {
			// fetch the three selected points
			GeoPoint[] points = getSelectedPoints();
			kernel.Conic(null, points);
			return true;
		}
		return false;
	}

	// get 2 GeoElements
	final protected boolean relation(ArrayList hits) {
		if (hits == null)
			return false;

		addSelectedGeo(hits, 2, false);
		if (selGeos() == 2) {
			// fetch the three selected points
			GeoElement[] geos = getSelectedGeos();
			app.showRelation(geos[0], geos[1]);
			return true;
		}
		return false;
	}

	// get 2 points, 2 lines or 1 point and 1 line
	final protected boolean distance(ArrayList hits, MouseEvent e) {
		if (hits == null)
			return false;

		int count = addSelectedPoint(hits, 2, false);
		if (count == 0) {
			addSelectedLine(hits, 2, false);
		}
		if (count == 0) {
			addSelectedConic(hits, 2, false);
		}
		if (count == 0) {
			addSelectedPolygon(hits, 2, false);
		}
		if (count == 0) {
			addSelectedSegment(hits, 2, false);
		}			

		// TWO POINTS
		if (selPoints() == 2) {			
			// length
			GeoPoint[] points = getSelectedPoints();
			GeoNumeric length = kernel.Distance(null, points[0], points[1]);								

			// set startpoint of text to midpoint of two points
			GeoPoint midPoint = kernel.Midpoint(points[0], points[1]);
			createDistanceText(points[0], points[1], midPoint, length);			
		} 

		// SEGMENT
		else if (selSegments() == 1) {
			// length
			GeoSegment[] segments = getSelectedSegments();

			// length			
			if (segments[0].isLabelVisible()) 
				segments[0].setLabelMode(GeoElement.LABEL_NAME_VALUE);
			else 
				segments[0].setLabelMode(GeoElement.LABEL_VALUE);
			segments[0].setLabelVisible(true);
			segments[0].updateRepaint();
			return true;
		}

		// TWO LINES
		else if (selLines() == 2) {			
			GeoLine[] lines = getSelectedLines();
			kernel.Distance(null, lines[0], lines[1]);
			return true;
		}

		// POINT AND LINE
		else if (selPoints() == 1 && selLines() == 1) {	
			GeoPoint[] points = getSelectedPoints();
			GeoLine[] lines = getSelectedLines();
			GeoNumeric length = kernel.Distance(null, points[0], lines[0]);						

			// set startpoint of text to midpoint between point and line
			GeoPoint midPoint = kernel.Midpoint(points[0], kernel.ProjectedPoint(points[0], lines[0]));
			createDistanceText(points[0],lines[0], midPoint, length);		
		}

		// circumference of CONIC
		else if (selConics() == 1) {			
			GeoConic conic = getSelectedConics()[0];
			if (conic.isGeoConicPart()) {
				// length of arc
				GeoConicPart conicPart = (GeoConicPart) conic;
				if (conicPart.getConicPartType() == GeoConicPart.CONIC_PART_ARC) {
					// arc length
					if (conic.isLabelVisible()) 
						conic.setLabelMode(GeoElement.LABEL_NAME_VALUE);
					else 
						conic.setLabelMode(GeoElement.LABEL_VALUE);
					conic.updateRepaint();
					return true;
				}				
			} 

			// standard case: conic
			GeoNumeric circumFerence = kernel.Circumference(null, conic);

			// text			
			GeoText text = createDynamicText(app.getCommand("Circumference"), circumFerence, e.getPoint());			
			if (conic.isLabelSet()) {
				circumFerence.setLabel(removeUnderscores(app.getCommand("Circumference").toLowerCase(Locale.US) + conic.getLabel()));							
				text.setLabel(removeUnderscores(app.getPlain("Text") + conic.getLabel()));				
			}			
			return true;
		}

		// perimeter of CONIC
		else if (selPolygons() == 1) {			
			GeoPolygon [] poly = getSelectedPolygons();
			GeoNumeric perimeter = kernel.Perimeter(null, poly[0]);

			// text			
			GeoText text = createDynamicText(descriptionPoints(app.getCommand("Perimeter"), poly[0]), 
					perimeter, e.getPoint());

			if (poly[0].isLabelSet()) {
				perimeter.setLabel(removeUnderscores(app.getCommand("Perimeter").toLowerCase(Locale.US) + poly[0].getLabel()));							
				text.setLabel(removeUnderscores(app.getPlain("Text") + poly[0].getLabel()));				
			} 
			return true;
		}

		return false;
	}	

	/**
	 * Creates a text that shows the distance length between geoA and geoB at the given startpoint.
	 */
	protected GeoText createDistanceText(GeoElement geoA, GeoElement geoB, 
			GeoPoint startPoint, GeoNumeric length) {
		// create text that shows length
		try {				
			String strText = "";
			boolean useLabels = geoA.isLabelSet() && geoB.isLabelSet();
			if (useLabels) {		
				length.setLabel(removeUnderscores(app.getCommand("Distance").toLowerCase(Locale.US) + geoA.getLabel() + geoB.getLabel()));
				//strText = "\"\\overline{\" + Name["+ geoA.getLabel() 
				//	+ "] + Name["+ geoB.getLabel() + "] + \"} \\, = \\, \" + "
				//	+ length.getLabel();	

				//DistanceAB="\\overline{" + %0 + %1 + "} \\, = \\, " + %2
				// or
				//DistanceAB=%0+%1+" \\, = \\, "+%2
				strText = app.getPlain("DistanceAB.LaTeX","Name["+ geoA.getLabel()+"]","Name["+ geoB.getLabel()+"]",length.getLabel());			
				//Application.debug(strText);
				geoA.setLabelVisible(true);				
				geoB.setLabelVisible(true);
				geoA.updateRepaint();
				geoB.updateRepaint();
			}
			else {
				length.setLabel(removeUnderscores(app.getCommand("Distance").toLowerCase(Locale.US)));					
				strText = "\"\"" + length.getLabel();
			}

			// create dynamic text
			GeoText text = kernel.getAlgebraProcessor().evaluateToText(strText, true);
			if (useLabels) {
				text.setLabel(removeUnderscores(app.getPlain("Text") + geoA.getLabel() + geoB.getLabel()));	
				text.setLaTeX(useLabels, true);
			}			

			text.setStartPoint(startPoint);
			text.updateRepaint();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * Creates a text that shows a number value of geo at the current mouse position.
	 */
	protected GeoText createDynamicText(String descText, GeoElement value, Point loc) {
		// create text that shows length
		try {
			// create dynamic text
			String dynText = "\"" + descText + " = \" + " + value.getLabel();

			GeoText text = kernel.getAlgebraProcessor().evaluateToText(dynText, true);									
			text.setAbsoluteScreenLocActive(true);
			text.setAbsoluteScreenLoc(loc.x, loc.y);			
			text.updateRepaint();
			return text;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}

	protected String removeUnderscores(String label) {	
		// remove all indices
		return label.replaceAll("_", "");			
	}

	protected boolean area(ArrayList hits,  MouseEvent e) {
		if (hits == null)
			return false;

		int count = addSelectedPolygon(hits, 1, false);
		if (count == 0) {
			addSelectedConic(hits, 2, false);
		}				

		// area of CONIC
		if (selConics() == 1) {			
			GeoConic conic = getSelectedConics()[0];			

			//  check if arc
			if (conic.isGeoConicPart()) {				
				GeoConicPart conicPart = (GeoConicPart) conic;
				if (conicPart.getConicPartType() == GeoConicPart.CONIC_PART_ARC) {
					clearSelections();
					return false;
				}				
			} 

			// standard case: conic
			GeoNumeric area = kernel.Area(null, conic);

			// text			
			GeoText text = createDynamicText(app.getCommand("Area"), area, e.getPoint());			
			if (conic.isLabelSet()) {					
				area.setLabel(removeUnderscores(app.getCommand("Area").toLowerCase(Locale.US) + conic.getLabel()));							
				text.setLabel(removeUnderscores(app.getPlain("Text") + conic.getLabel()));				
			}			
			return true;
		}

		// area of polygon
		else if (selPolygons() == 1) {			
			GeoPolygon [] poly = getSelectedPolygons();						

			// dynamic text with polygon's area
			GeoText text = createDynamicText(descriptionPoints(app.getCommand("Area"), poly[0]), poly[0], e.getPoint());			
			if (poly[0].isLabelSet()) {					
				text.setLabel(removeUnderscores(app.getPlain("Text") + poly[0].getLabel()));				
			} 
			return true;
		}

		return false;
	}



	protected String descriptionPoints(String prefix, GeoPolygon poly) {
		// build description text including point labels	
		String descText = prefix;

		// use points for polygon with static points (i.e. no list of points)
		GeoPoint [] points = null;
		if (poly.getParentAlgorithm() instanceof AlgoPolygon) {
			points = ((AlgoPolygon) poly.getParentAlgorithm()).getPoints();
		}	

		if (points != null) {
			descText = descText + " \"";
			boolean allLabelsSet = true;
			for (int i=0; i < points.length; i++) {
				if (points[i].isLabelSet()) 
					descText = descText + " + Name[" + points[i].getLabel() + "]";
				else {
					allLabelsSet = false;
					i = points.length;
				}
			}

			if (allLabelsSet) {
				descText = descText + " + \"";
				for (int i=0; i < points.length; i++) {
					points[i].setLabelVisible(true);
					points[i].updateRepaint();
				}
			} else
				descText = app.getCommand("Area");
		}
		return descText;
	}

	protected boolean slope(ArrayList hits) {
		if (hits == null)
			return false;

		addSelectedLine(hits, 1, false);			

		if (selLines() == 1) {			
			GeoLine line = getSelectedLines()[0];						

			String strLocale = app.getLocale().toString();
			GeoNumeric slope;
			if (strLocale.equals("de_AT")) {
				slope = kernel.Slope("k", line);
			} else {
				slope = kernel.Slope("m", line);
			}			

			// show value
			if (slope.isLabelVisible()) {
				slope.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			} else {
				slope.setLabelMode(GeoElement.LABEL_VALUE);
			}
			slope.setLabelVisible(true);
			slope.updateRepaint();
			return true;
		}
		return false;
	}

	protected boolean regularPolygon(ArrayList hits) {
		if (hits == null)
			return false;

		// need two points
		addSelectedPoint(hits, 2, false);

		// we got the rotation center point
		if (selPoints() == 2) {					
			NumberValue num = app.getGuiManager().showNumberInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
					app.getPlain("Points"), "4");													

			if (num == null) {
				view.resetMode();
				return false;
			}

			GeoPoint [] points = getSelectedPoints();
			kernel.RegularPolygon(null, points[0], points[1], num);			
			return true;
		}
		return false;
	}

	protected boolean showCheckBox(ArrayList hits) {
		if (selectionPreview)
			return false;

		app.getGuiManager().showBooleanCheckboxCreationDialog(mouseLoc, null);
		return false;
	}

	// get (point or line) and (conic or function or curve)
	final protected boolean tangents(ArrayList hits) {
		if (hits == null)
			return false;

		boolean found=false;
		found = addSelectedConic(hits, 1, false) != 0;
		if (!found)
			found = addSelectedFunction(hits, 1, false) != 0;
		if (!found)
			found = addSelectedCurve(hits, 1, false) != 0;		

		if (!found) {
			if (selLines() == 0) {
				addSelectedPoint(hits, 1, false);
			}
			if (selPoints() == 0) {
				addSelectedLine(hits, 1, false);
			}
		}

		if (selConics() == 1) {
			if (selPoints() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				kernel.Tangent(null, points[0], conics[0]);
				return true;
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				kernel.Tangent(null, lines[0], conics[0]);
				return true;
			}
		} 
		else if (selFunctions() == 1) {
			if (selPoints() == 1) {
				GeoFunction[] functions = getSelectedFunctions();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				kernel.Tangent(null, points[0], functions[0]);
				return true;
			}
		}
		else if (selCurves() == 1) {
			if (selPoints() == 1) {
				GeoCurveCartesian[] curves = getSelectedCurves();
				GeoPoint [] points = getSelectedPoints();
				// create new tangents
				kernel.Tangent(null, points[0], curves[0]);
				return true;
			}
		}
		return false;
	}

	// get (point or line or vector) and conic
	final protected boolean polarLine(ArrayList hits) {
		if (hits == null)
			return false;
		boolean hitConic = false;

		hitConic = (addSelectedConic(hits, 1, false) != 0);

		if (!hitConic ) {
			if (selVectors() == 0) {
				addSelectedVector(hits, 1, false);
			}
			if (selLines() == 0) {
				addSelectedPoint(hits, 1, false);
			}
			if (selPoints() == 0) {
				addSelectedLine(hits, 1, false);
			}			
		}

		if (selConics() == 1) {
			if (selPoints() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoPoint[] points = getSelectedPoints();
				// create new tangents
				kernel.PolarLine(null, points[0], conics[0]);
				return true;
			} else if (selLines() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoLine[] lines = getSelectedLines();
				// create new line
				kernel.DiameterLine(null, lines[0], conics[0]);
				return true;
			}  else if (selVectors() == 1) {
				GeoConic[] conics = getSelectedConics();
				GeoVector[] vecs = getSelectedVectors();
				// create new line
				kernel.DiameterLine(null, vecs[0], conics[0]);
				return true;
			}
		}
		return false;
	}

	final protected boolean delete(ArrayList hits) {
		if (hits == null)
			return false;

		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			// delete this object
			GeoElement[] geos = getSelectedGeos();
			geos[0].removeOrSetUndefinedIfHasFixedDescendent();
			return true;
		}
		return false;
	}

	final protected boolean showHideObject(ArrayList hits) {
		if (hits == null)
			return false;

		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}

		GeoElement geo = chooseGeo(hits, true);
		if (geo != null) {
			// hide axis
			if (geo instanceof GeoAxis)	{
				switch (((GeoAxis) geo).getType()) {
				case GeoAxis.X_AXIS:
					view.showAxes(false, view.getShowYaxis());
					break;

				case GeoAxis.Y_AXIS:
					view.showAxes(view.getShowXaxis(), false);
					break;
				}				
				app.updateMenubar();
			} else {
				app.toggleSelectedGeo(geo);
			}
			return true;
		}						
		return false;
	}

	final protected boolean showHideLabel(ArrayList hits) {
		if (hits == null)
			return false;

		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}

		GeoElement geo = chooseGeo(view.getOtherHits(hits, GeoAxis.class, tempArrayList), true);
		if (geo != null) {			
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.updateRepaint();
			return true;
		}						
		return false;
	}

	final protected boolean copyVisualStyle(ArrayList hits) {
		if (hits == null)
			return false;

		if (selectionPreview) {
			addSelectedGeo(hits, 1000, false);
			return false;
		}

		GeoElement geo = chooseGeo(view.getOtherHits(hits, GeoAxis.class, tempArrayList), true);
		if (geo == null) return false;

		// movedGeoElement is the active geo
		if (movedGeoElement == null) {
			movedGeoElement = geo;
			app.addSelectedGeo(geo);
		} else {
			if (geo == movedGeoElement) {
				// deselect
				app.removeSelectedGeo(geo);
				movedGeoElement = null;
				if (toggleModeChangedKernel)
					app.storeUndoInfo();
				toggleModeChangedKernel = false;
			} else {
				// standard case: copy visual properties
				geo.setVisualStyle(movedGeoElement);
				geo.updateRepaint();
				return true;
			}
		}					
		return false;
	}


	// get mirrorables and point
	final protected boolean mirrorAtPoint(ArrayList hits) {	
		if (hits == null)
			return false;

		// try to get one mirrorable	
		int count = 0;
		if (selGeos() == 0) {
			ArrayList mirAbles = view.getHits(hits, Mirrorable.class, tempArrayList);		
			count = addSelectedGeo(mirAbles, 1, false);	
		}

		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}

		// point = mirror
		if (count == 0) {
			count = addSelectedPoint(hits, 1, false);
		}					

		// we got the mirror point
		if (selPoints() == 1) {							
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				kernel.Mirror(null,  polys[0], points[0]);
				return true;
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoPoint point = getSelectedPoints()[0];						
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != point) {
						if (geos[i] instanceof Mirrorable)
							kernel.Mirror(null,  (Mirrorable) geos[i], point);
						else if (geos[i].isGeoPolygon()) {
							kernel.Mirror(null, (GeoPolygon) geos[i], point);
						}
					}
				}		
				return true;
			}						
		}
		return false;
	}

	/**
	 * Removes parent points of segments, rays, polygons, etc. from selGeos
	 * that are not necessary for transformations of these objects.
	 */
	protected void removeParentPoints(ArrayList selGeos) {
		tempArrayList.clear();	
		tempArrayList.addAll(selGeos);

		// remove parent points
		for (int i=0; i < selGeos.size(); i++) {
			GeoElement geo = (GeoElement) selGeos.get(i);

			switch (geo.getGeoClassType()) {			
			case GeoElement.GEO_CLASS_SEGMENT:
			case GeoElement.GEO_CLASS_RAY:
				// remove start and end point of segment
				GeoLine line = (GeoLine) geo;						
				tempArrayList.remove(line.getStartPoint());
				tempArrayList.remove(line.getEndPoint());									
				break;

			case GeoElement.GEO_CLASS_CONICPART:
				GeoConicPart cp = (GeoConicPart) geo;
				ArrayList ip = cp.getParentAlgorithm().getInputPoints();
				tempArrayList.removeAll(ip);
				break;

			case GeoElement.GEO_CLASS_POLYGON:
				// remove points and segments of poly
				GeoPolygon poly = (GeoPolygon) geo;
				GeoPoint [] points = poly.getPoints();
				for (int k=0; k < points.length; k++) {
					tempArrayList.remove(points[k]);
				}
				GeoSegment [] segs = poly.getSegments();
				for (int k=0; k < segs.length; k++) {
					tempArrayList.remove(segs[k]);
				}
				break;					
			}				
		}		

		selGeos.clear();
		selGeos.addAll(tempArrayList);						
	}

	// get mirrorable and line
	final protected boolean mirrorAtLine(ArrayList hits) {
		if (hits == null)
			return false;

		// mirrorable	
		int count = 0;
		if (selGeos() == 0) {
			ArrayList mirAbles = view.getHits(hits, Mirrorable.class, tempArrayList);
			count =addSelectedGeo(mirAbles, 1, false);
		}

		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}

		// line = mirror
		if (count == 0) {
			addSelectedLine(hits, 1, false);
		}					

		// we got the mirror point
		if (selLines() == 1) {	
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoLine[] lines = getSelectedLines();	
				kernel.Mirror(null,  polys[0], lines[0]);
				return true;
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoLine line = getSelectedLines()[0];						
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != line) {
						if (geos[i] instanceof Mirrorable)
							kernel.Mirror(null,  (Mirrorable) geos[i], line);
						else if (geos[i].isGeoPolygon()) {
							kernel.Mirror(null, (GeoPolygon) geos[i], line);
						}
					}
				}		
				return true;
			}	
		}				
		return false;
	}


	// Michael Borcherds 2008-03-23
	final protected boolean mirrorAtCircle(ArrayList hits) {
		if (hits == null)
			return false;


		// remove conics that aren't circles
		for (int i=0 ; i<hits.size(); i++)
		{
			GeoElement geo = (GeoElement) hits.get(i);
			if (geo.isGeoConic())
				if (!((GeoConic)geo).isCircle()) hits.remove(i);
		}

		addSelectedConic(hits, 1, false);

		addSelectedPoint(hits, 1, false);

		if (selConics() == 1 && selPoints() == 1) {
			GeoConic[] conics = getSelectedConics();
			GeoPoint[] points = getSelectedPoints();
			//if (((GeoConic)conics[0]).getTypeString()!="Circle") return false;
			if (!((GeoConic)conics[0]).isCircle()) return false;
			kernel.Mirror(null, points[0], conics[0]);
			return true;

		} 
		return false;
	}

	// get translateable and vector
	final protected boolean translateByVector(ArrayList hits) {
		if (hits == null)
			return false;

		// translateable
		int count = 0;
		if (selGeos() == 0) {
			ArrayList transAbles = view.getHits(hits, Translateable.class, tempArrayList);
			count = addSelectedGeo(transAbles, 1, false);
		}

		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}	

		// translation vector
		if (count == 0) {
			addSelectedVector(hits, 1, false);
		}				

		// we got the mirror point
		if (selVectors() == 1) {		
			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoVector[] vecs = getSelectedVectors();	
				kernel.Translate(null,  polys[0], vecs[0]);
				return true;
			}
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoVector vec = getSelectedVectors()[0];						
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != vec) {
						if (geos[i] instanceof Translateable)
							kernel.Translate(null,  (Translateable) geos[i], vec);
						else if (geos[i].isGeoPolygon()) {
							kernel.Translate(null, (GeoPolygon) geos[i], vec);
						}
					}
				}		
				return true;
			}				
		}
		return false;
	}

	// get rotateable object, point and angle
	final protected boolean rotateByAngle(ArrayList hits) {
		if (hits == null)
			return false;

		// translateable
		int count = 0;
		if (selGeos() == 0) {
			ArrayList rotAbles = view.getHits(hits, PointRotateable.class, tempArrayList);
			count = addSelectedGeo(rotAbles, 1, false);
		}

		// polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}

		// rotation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false);
		}

		// we got the rotation center point
		if (selPoints() == 1 && selGeos() > 0) {					
			Object [] ob = app.getGuiManager().showAngleInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
					app.getPlain("Angle"), defaultRotateAngle);
			NumberValue num = (NumberValue) ob[0];											
			geogebra.gui.AngleInputDialog dialog = (geogebra.gui.AngleInputDialog) ob[1];
			String angleText = dialog.getText();

			// keep angle entered if it ends with 'degrees'
			if (angleText.endsWith("\u00b0") && dialog.success==true) defaultRotateAngle = angleText;
			else defaultRotateAngle = "45"+"\u00b0";

			if (num == null) {
				view.resetMode();
				return false;
			}

			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				kernel.Rotate(null,  polys[0], num, points[0]);
			} else {	
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoPoint point = getSelectedPoints()[0];						
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != point) {
						if (geos[i] instanceof PointRotateable)
							kernel.Rotate(null,  (PointRotateable) geos[i], num, point);
						else if (geos[i].isGeoPolygon()) {
							kernel.Rotate(null, (GeoPolygon) geos[i], num, point);
						}
					}
				}						
			}
			return true;
		}

		return false;
	}		

	// get dilateable object, point and number
	final protected boolean dilateFromPoint(ArrayList hits) {
		if (hits == null)
			return false;

		// dilateable
		int count = 0;
		if (selGeos() == 0) {
			ArrayList dilAbles = view.getHits(hits, Dilateable.class, tempArrayList);
			count =	addSelectedGeo(dilAbles, 1, false);
		}

		//		 polygon
		if (count == 0) {					
			count = addSelectedPolygon(hits, 1, false);
		}

		// dilation center
		if (count == 0) {
			addSelectedPoint(hits, 1, false);
		}

		// we got the mirror point
		if (selPoints() == 1) {		
			NumberValue num = app.getGuiManager().showNumberInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
					app.getPlain("Numeric"), null);			
			if (num == null) {
				view.resetMode();
				return false;
			}

			if (selPolygons() == 1) {
				GeoPolygon[] polys = getSelectedPolygons();
				GeoPoint[] points = getSelectedPoints();
				kernel.Dilate(null,  polys[0], num, points[0]);
				return true;
			} 
			else if (selGeos() > 0) {					
				// mirror all selected geos
				GeoElement [] geos = getSelectedGeos();
				GeoPoint point = getSelectedPoints()[0];					
				for (int i=0; i < geos.length; i++) {				
					if (geos[i] != point) {
						if (geos[i] instanceof Dilateable)
							kernel.Dilate(null,  (Dilateable) geos[i], num, point);
						else if (geos[i].isGeoPolygon()) {
							kernel.Dilate(null, (GeoPolygon) geos[i], num, point);
						}
					}
				}		
				return true;
			}		
		}
		return false;
	}


	// get point and number
	final protected boolean segmentFixed(ArrayList hits) {
		if (hits == null)
			return false;

		// dilation center
		addSelectedPoint(hits, 1, false);

		// we got the point
		if (selPoints() == 1) {
			// get length of segment
			NumberValue num = app.getGuiManager().showNumberInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
					app.getPlain("Length"), null);		

			if (num == null) {
				view.resetMode();
				return false;
			}

			GeoPoint[] points = getSelectedPoints();		
			kernel.Segment(null, points[0], num);
			return true;
		}
		return false;
	}	



	final protected boolean fitLine(ArrayList hits) {

		GeoList list;

		addSelectedList(hits,1,false);

		if (selLists() > 0)
		{
			list = getSelectedLists()[0];
			if (list != null) {
				kernel.FitLineY(null, list);
				return true;   
			}
		}
		else
		{
			addSelectedPoint(hits, 999, true);

			if (selPoints() > 1) 
			{					
				GeoPoint[] points = getSelectedPoints();
				list = geogebra.kernel.commands.CommandProcessor.wrapInList(kernel,points, points.length, GeoElement.GEO_CLASS_POINT);
				if (list != null) {
					kernel.FitLineY(null, list);
					return true;             	     	 
				} 
			}
		}
		return false;
	}





	// Michael Borcherds 2008-03-14	
	// Markus 2008-07-30: added support for two identical input points (center *2 and point on edge)
	final protected boolean compasses(ArrayList hits) {
		if (hits == null)
			return false;

		// we already have two points that define the radius
		if (selPoints() == 2) {			
			GeoPoint [] points = new GeoPoint[2];
			points[0] = (GeoPoint) selectedPoints.get(0);
			points[1] = (GeoPoint) selectedPoints.get(1);

			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, GeoPoint.class);

			if (centerPoint != null) {
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return false;
				}
				else {
					// three points: center, distance between two points		
					kernel.Circle(null, centerPoint, points[0], points[1],true);
					clearSelections();
					return true;
				}
			}
		} 

		// we already have a segment that defines the radius
		else if (selSegments() == 1) {
			GeoSegment segment =  (GeoSegment) selectedSegments.get(0);

			// check for centerPoint
			GeoPoint centerPoint = (GeoPoint) chooseGeo(hits, GeoPoint.class);

			if (centerPoint != null) {	
				if (selectionPreview) {
					// highlight the center point
					tempArrayList.clear();
					tempArrayList.add(centerPoint);
					addToHighlightedList(selectedPoints, tempArrayList, 3);
					return false;
				}
				else {
					// center point and segment
					kernel.Circle(null, centerPoint, segment);
					clearSelections();
					return true;
				}
			}
		}


		// don't have radius yet: need two points or segment		
		boolean hitPoint = (addSelectedPoint(hits, 2, false) != 0);
		if (!hitPoint && selPoints() != 2 ) {
			addSelectedSegment(hits, 1, false);
		}

		return false;
	}	

	// get two points and number
	final protected boolean angleFixed(ArrayList hits) {
		if (hits == null)
			return false;

		// dilation center
		int count = addSelectedPoint(hits, 2, false);

		if (count == 0) {
			addSelectedSegment(hits, 1, false);
		}				

		// we got the points		
		if (selPoints() == 2 || selSegments() == 1) {
			// get angle			
			Object [] ob = app.getGuiManager().showAngleInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
					app.getPlain("Angle"), "45\u00b0");
			NumberValue num = (NumberValue) ob[0];
			geogebra.gui.AngleInputDialog aDialog = (geogebra.gui.AngleInputDialog) ob[1]; 			

			if (num == null) {
				view.resetMode();
				return false;
			}

			GeoAngle angle = null;
			boolean posOrientation = aDialog.isCounterClockWise();
			if (selPoints() == 2) {
				GeoPoint[] points = getSelectedPoints();		
				angle = (GeoAngle) kernel.Angle(null, points[0], points[1], num, posOrientation)[0];			
			} else {
				GeoSegment[] segment = getSelectedSegments();		
				angle = (GeoAngle) kernel.Angle(null, segment[0].getEndPoint(), segment[0].getStartPoint(), num, posOrientation)[0];
			}			

			// make sure that we show angle value
			if (angle.isLabelVisible()) 
				angle.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			else 
				angle.setLabelMode(GeoElement.LABEL_VALUE);
			angle.setLabelVisible(true);		
			angle.updateRepaint();					
			return true;
		}
		return false;
	}	

	// get center point and number
	final protected boolean circlePointRadius(ArrayList hits) {
		if (hits == null)
			return false;

		addSelectedPoint(hits, 1, false);		

		// we got the center point
		if (selPoints() == 1) {	
			NumberValue num = app.getGuiManager().showNumberInputDialog(app.getMenu(EuclidianView.getModeText(mode)),
					app.getPlain("Radius"), null);

			if (num == null) {
				view.resetMode();
				return false;
			}

			GeoPoint[] points = getSelectedPoints();	

			kernel.Circle(null, points[0], num);
			return true;
		}
		return false;
	}	

	// get point and vector
	final protected boolean vectorFromPoint(ArrayList hits) {
		if (hits == null)
			return false;

		// point	
		int count = addSelectedPoint(hits, 1, false);

		// vector
		if (count == 0) {
			addSelectedVector(hits, 1, false);
		}

		if (selPoints() == 1 && selVectors() == 1) {			
			GeoVector[] vecs = getSelectedVectors();			
			GeoPoint[] points = getSelectedPoints();
			GeoPoint endPoint = (GeoPoint) kernel.Translate(null, points[0], vecs[0])[0];
			kernel.Vector(null, points[0], endPoint);
			return true;
		}
		return false;
	}

	/**
	 * Handles selected objects for a macro
	 * @param hits
	 * @return
	 */
	final protected boolean macro(ArrayList hits) {		
		// try to get next needed type of macroInput
		int index = selGeos();

		// standard case: try to get one object of needed input type
		boolean objectFound = 1 == 
			handleAddSelected(hits, macroInput.length, false, selectedGeos, macroInput[index]);			

		/*
		// POLYGON instead of points special case:
		// if no object was found maybe we need points
		// in this case let's try to use a polygon's points 
		int neededPoints = 0;				
		if (!objectFound) {
			// how many points do we need?						
			for (int k = index; k < macroInput.length; k++) {
				if (macroInput[k] == GeoPoint.class) 
					++neededPoints;					
				else 
					break;				
			}

			// several points needed: look for polygons with this number of points
			if (neededPoints > 2) {				
				if (macroPolySearchList == null)
					macroPolySearchList = new ArrayList();
				// get polygons with needed number of points
				view.getPolygons(hits, neededPoints, macroPolySearchList);

				if (selectionPreview) {
					addToHighlightedList(selectedGeos, macroPolySearchList , macroInput.length);
					return false;
				}

				// now we only have polygons with the right number of points: choose one 
				GeoPolygon poly = (GeoPolygon) chooseGeo(macroPolySearchList);
				if (poly != null) {					
					// success: let's take the points from the polygon
					GeoPoint [] points = poly.getPoints();					
					for (int k=0; k < neededPoints; k++) {
						selectedGeos.add(points[k]);
						app.toggleSelectedGeo(points[k]);
					}										
					index = index + neededPoints - 1;	
					objectFound = true;
				}
			}									
		}		
		 */

		// we're done if in selection preview
		if (selectionPreview) 
			return false; 	


		// only one point needed: try to create it
		if (!objectFound && macroInput[index] == GeoPoint.class) {
			if (createNewPoint(hits, true, true, false)) {				
				// take movedGeoPoint which is the newly created point								
				selectedGeos.add(movedGeoPoint);
				app.addSelectedGeo(movedGeoPoint);
				objectFound = true;
				POINT_CREATED = false;
			}
		}

		// object found in handleAddSelected()
		if (objectFound) { 
			// look ahead if we need a number or an angle next			
			while (++index < macroInput.length) {				
				// maybe we need a number
				if (macroInput[index] == GeoNumeric.class) {									
					NumberValue num = app.getGuiManager().showNumberInputDialog(macro.getToolOrCommandName(),
							app.getPlain("Numeric" ), null);									
					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					} else {
						// great, we got our number
						selectedGeos.add(num);
					}
				}	

				// maybe we need an angle
				else if (macroInput[index] == GeoAngle.class) {									
					Object [] ob = app.getGuiManager().showAngleInputDialog(macro.getToolOrCommandName(),
							app.getPlain("Angle"), "45\u00b0");
					NumberValue num = (NumberValue) ob[0];						

					if (num == null) {
						// no success: reset mode
						view.resetMode();
						return false;
					} else {
						// great, we got our angle
						selectedGeos.add(num);
					}
				}	

				else // other type needed, so leave loop 
					break;				
			}			
		}

		//Application.debug("index: " + index + ", needed type: " + macroInput[index]);

		// do we have everything we need?
		if (selGeos() == macroInput.length) {						
			kernel.useMacro(null, macro, getSelectedGeos())	;		
			return true;
		} 		
		return false;
	}

	final protected boolean geoElementSelected(ArrayList hits, boolean addToSelection) {
		if (hits == null)
			return false;

		addSelectedGeo(hits, 1, false);
		if (selGeos() == 1) {
			GeoElement[] geos = getSelectedGeos();			
			app.geoElementSelected(geos[0], addToSelection);
		}
		return false;
	}

	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final protected boolean move(ArrayList hits) {		
		addSelectedGeo(view.getMoveableHits(hits), 1, false);		
		return false;
	}

	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final protected boolean moveRotate(ArrayList hits) {				
		addSelectedGeo(view.getPointRotateableHits(hits, rotationCenter), 1, false);
		return false;
	}

	// dummy function for highlighting:
	// used only in preview mode, see mouseMoved() and selectionPreview
	final protected boolean point(ArrayList hits) {
		addSelectedGeo(view.getHits(hits, Path.class, tempArrayList), 1, false);
		return false;
	}

	final protected boolean textImage(ArrayList hits, int mode, boolean altDown) {
		GeoPoint loc = null; // location

		if (hits == null) {
			if (selectionPreview)
				return false;
			else {
				// create new Point
				loc = new GeoPoint(kernel.getConstruction());			
				loc.setCoords(xRW, yRW, 1.0);	
			}
		} else {
			// points needed
			addSelectedPoint(hits, 1, false);
			if (selPoints() == 1) {
				// fetch the selected point
				GeoPoint[] points = getSelectedPoints();
				loc = points[0];
			}
		}		

		// got location
		if (loc != null) {
			switch (mode) {
			case EuclidianView.MODE_TEXT:				
				app.getGuiManager().showTextCreationDialog(loc);
				break;

			case EuclidianView.MODE_IMAGE:	
				app.getGuiManager().loadImage(loc, altDown);
				break;
			}			
			return true;
		}

		return false;
	}


	// new slider
	final protected boolean slider() {		
		return !selectionPreview && mouseLoc != null && app.getGuiManager().showSliderCreationDialog(mouseLoc.x, mouseLoc.y);
	}		

	/***************************************************************************
	 * helper functions for selection sets
	 **************************************************************************/

	/*
	final protected boolean isSelected(GeoElement geo) {
		return selectedGeos.contains(geo);
	}*/

	//	final protected GeoElement getFirstSelectedInstance(Class myclass) {
	//		Iterator it = selectedGeos.iterator();		
	//		while (it.hasNext()) {
	//			GeoElement geo = (GeoElement) it.next();
	//			if (myclass.isInstance(geo))
	//				return geo;
	//		}
	//		return null;
	//	}

	final protected GeoElement[] getSelectedGeos() {
		GeoElement[] ret = new GeoElement[selectedGeos.size()];
		int i = 0;
		Iterator it = selectedGeos.iterator();
		while (it.hasNext()) {
			ret[i] = (GeoElement) it.next();
			i++;
		}
		clearSelection(selectedGeos);
		return ret;
	}	

	final protected GeoPoint[] getSelectedPoints() {				
		GeoPoint[] ret = new GeoPoint[selectedPoints.size()];
		for (int i = 0; i < selectedPoints.size(); i++) {		
			ret[i] = (GeoPoint) selectedPoints.get(i);
		}
		clearSelection(selectedPoints);
		return ret;
	}

	final protected GeoNumeric[] getSelectedNumbers() {				
		GeoNumeric[] ret = new GeoNumeric[selectedNumbers.size()];
		for (int i = 0; i < selectedNumbers.size(); i++) {		
			ret[i] = (GeoNumeric) selectedNumbers.get(i);
		}
		clearSelection(selectedNumbers);
		return ret;
	}

	final protected GeoList[] getSelectedLists() {				
		GeoList[] ret = new GeoList[selectedLists.size()];
		for (int i = 0; i < selectedLists.size(); i++) {		
			ret[i] = (GeoList) selectedLists.get(i);
		}
		clearSelection(selectedLists);
		return ret;
	}

	final protected GeoPolygon[] getSelectedPolygons() {				
		GeoPolygon[] ret = new GeoPolygon[selectedPolygons.size()];
		for (int i = 0; i < selectedPolygons.size(); i++) {		
			ret[i] = (GeoPolygon) selectedPolygons.get(i);
		}
		clearSelection(selectedPolygons);
		return ret;
	}

	final protected GeoLine[] getSelectedLines() {
		GeoLine[] lines = new GeoLine[selectedLines.size()];
		int i = 0;
		Iterator it = selectedLines.iterator();
		while (it.hasNext()) {
			lines[i] = (GeoLine) it.next();
			i++;
		}
		clearSelection(selectedLines);
		return lines;
	}

	final protected GeoSegment[] getSelectedSegments() {
		GeoSegment[] segments = new GeoSegment[selectedSegments.size()];
		int i = 0;
		Iterator it = selectedSegments.iterator();
		while (it.hasNext()) {
			segments[i] = (GeoSegment) it.next();
			i++;
		}
		clearSelection(selectedSegments);
		return segments;
	}

	final protected GeoVector[] getSelectedVectors() {
		GeoVector[] vectors = new GeoVector[selectedVectors.size()];
		int i = 0;
		Iterator it = selectedVectors.iterator();
		while (it.hasNext()) {
			vectors[i] = (GeoVector) it.next();
			i++;
		}
		clearSelection(selectedVectors);
		return vectors;
	}

	final protected GeoConic[] getSelectedConics() {
		GeoConic[] conics = new GeoConic[selectedConics.size()];
		int i = 0;
		Iterator it = selectedConics.iterator();
		while (it.hasNext()) {
			conics[i] = (GeoConic) it.next();
			i++;
		}
		clearSelection(selectedConics);
		return conics;
	}

	final protected GeoFunction[] getSelectedFunctions() {
		GeoFunction[] functions = new GeoFunction[selectedFunctions.size()];
		int i = 0;
		Iterator it = selectedFunctions.iterator();
		while (it.hasNext()) {
			functions[i] = (GeoFunction) it.next();
			i++;
		}
		clearSelection(selectedFunctions);
		return functions;
	}


	final protected GeoCurveCartesian [] getSelectedCurves() {
		GeoCurveCartesian [] curves = new GeoCurveCartesian[selectedCurves.size()];
		int i = 0;
		Iterator it = selectedCurves.iterator();
		while (it.hasNext()) {
			curves[i] = (GeoCurveCartesian) it.next();
			i++;
		}
		clearSelection(selectedCurves);
		return curves;
	}	

	final protected void clearSelection(ArrayList selectionList) {
		// unselect
		selectionList.clear();
		selectedGeos.clear();
		app.clearSelectedGeos();	
		view.repaint();
	}

	final protected int addSelectedGeo(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedGeos, GeoElement.class);
	}		

	protected int handleAddSelected(ArrayList hits, int max, boolean addMore, ArrayList list, Class geoClass) {		
		if (selectionPreview)
			return addToHighlightedList(list, view.getHits(hits, geoClass, handleAddSelectedArrayList) , max);
		else
			return addToSelectionList(list, view.getHits(hits, geoClass, handleAddSelectedArrayList), max, addMore);
	}
	protected ArrayList handleAddSelectedArrayList = new ArrayList();

	final protected int addSelectedPoint(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPoints, GeoPoint.class);
	}

	final protected int addSelectedNumeric(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedNumbers, GeoNumeric.class);
	}

	final protected int addSelectedLine(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedLines, GeoLine.class);
	}

	final protected int addSelectedSegment(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedSegments, GeoSegment.class);
	}

	final protected int addSelectedVector(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedVectors, GeoVector.class);
	}

	final protected int addSelectedConic(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedConics, GeoConic.class);
	}

	final protected int addSelectedFunction(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedFunctions, GeoFunction.class);
	}

	final protected int addSelectedCurve(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedCurves, GeoCurveCartesian.class);
	}

	final protected int addSelectedPolygon(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedPolygons, GeoPolygon.class);
	}

	final protected int addSelectedList(ArrayList hits, int max,
			boolean addMoreThanOneAllowed) {
		return handleAddSelected(hits, max, addMoreThanOneAllowed, selectedLists, GeoList.class);
	}

	final int selGeos() {
		return selectedGeos.size();
	}

	final int selPoints() {
		return selectedPoints.size();
	}

	final int selNumbers() {
		return selectedNumbers.size();
	}

	final int selLists() {
		return selectedLists.size();
	}

	final int selPolygons() {
		return selectedPolygons.size();
	}

	final int selLines() {
		return selectedLines.size();
	}

	final int selSegments() {
		return selectedSegments.size();
	}

	final int selVectors() {
		return selectedVectors.size();
	}

	final int selConics() {
		return selectedConics.size();
	}

	final int selFunctions() {
		return selectedFunctions.size();
	}

	final int selCurves() {
		return selectedCurves.size();
	}

	// selectionList may only contain max objects
	// a choose dialog will be shown if not all objects can be added
	// @param addMoreThanOneAllowed: it's possible to add several objects
	// without choosing
	final protected int addToSelectionList(ArrayList selectionList,
			ArrayList geos, int max, boolean addMoreThanOneAllowed) {
		if (geos == null)
			return 0;
		//GeoElement geo;

		// ONLY ONE ELEMENT
		if (geos.size() == 1)
			return addToSelectionList(selectionList, (GeoElement) geos.get(0), max);

		//	SEVERAL ELEMENTS
		// here nothing should be removed
		//  too many objects -> choose one
		if (!addMoreThanOneAllowed || geos.size() + selectionList.size() > max)
			return addToSelectionList(selectionList, chooseGeo(geos, true), max);

		// already selected objects -> choose one
		boolean contained = false;
		for (int i = 0; i < geos.size(); i++) {
			if (selectionList.contains(geos.get(i)))
				contained = true;
		}
		if (contained)
			return addToSelectionList(selectionList, chooseGeo(geos, true), max);

		// add all objects to list
		int count = 0;
		for (int i = 0; i < geos.size(); i++) {
			count += addToSelectionList(selectionList, (GeoElement) geos.get(i), max);
		}
		return count;
	}

	//	selectionList may only contain max objects
	// an already selected objects is deselected
	final protected int addToSelectionList(ArrayList selectionList,
			GeoElement geo, int max) {
		if (geo == null)
			return 0;

		int ret = 0;
		if (selectionList.contains(geo)) { // remove from selection
			selectionList.remove(geo);
			if (selectionList != selectedGeos)
				selectedGeos.remove(geo);
			ret =  -1;
		} else { // new element: add to selection
			if (selectionList.size() < max) {
				selectionList.add(geo);
				if (selectionList != selectedGeos)
					selectedGeos.add(geo);
				ret = 1;
			} 
		}
		if (ret != 0) app.toggleSelectedGeo(geo);
		return ret;
	}

	// selectionList may only contain max objects
	final protected int addToHighlightedList(ArrayList selectionList,
			ArrayList geos, int max) {
		if (geos == null)
			return 0;

		Object geo;
		int ret = 0;
		for (int i = 0; i < geos.size(); i++) {
			geo = geos.get(i);
			if (selectionList.contains(geo)) {
				ret = (ret == 1) ? 1 : -1;
			} else {
				if (selectionList.size() < max) {
					highlightedGeos.add(geo); // add hit
					ret = 1;
				}
			}
		}
		return ret;
	}

	/**
	 * Shows dialog to choose one object out of hits[] that is an instance of
	   specified class (note: subclasses are included)
	 * 
	 */
	private GeoElement chooseGeo(ArrayList hits, Class geoclass) {
		return chooseGeo(view.getHits(hits, geoclass, tempArrayList), true);
	}

	final protected GeoElement chooseGeo(ArrayList geos, boolean includeFixed) {
		if (geos == null)
			return null;

		GeoElement ret = null;
		GeoElement retFree = null;
		GeoElement retPath = null;
		GeoElement retIndex = null;

		switch (geos.size()) {
		case 0:
			ret =  null;
			break;

		case 1:
			ret =  (GeoElement) geos.get(0);
			break;

		default:	

			int maxLayer = -1;

		int layerCount = 0;

		// work out max layer, and
		// count no of objects in max layer
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = (GeoElement)(geos.get(i));
			int layer = geo.getLayer();

			if (layer > maxLayer && (includeFixed || !geo.isFixed())) {
				maxLayer = layer;
				layerCount = 1;
				ret = geo;
			}
			else if (layer == maxLayer)
			{
				layerCount ++;
			}

		}

		//Application.debug("maxLayer"+maxLayer);
		//Application.debug("layerCount"+layerCount);

		// only one object in top layer, return it.
		if (layerCount == 1) return ret;


		int pointCount = 0;
		int freePointCount = 0;
		int pointOnPathCount = 0;
		int maxIndex = -1;

		// count no of points in top layer
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = (GeoElement)(geos.get(i));
			if (geo.isGeoPoint() && geo.getLayer() == maxLayer
					&& (includeFixed || !geo.isFixed())) {
				pointCount ++;
				ret = geo;

				// find point with the highest construction index
				int index = geo.getConstructionIndex();
				if (index > maxIndex) {
					maxIndex = index;
					retIndex = geo;
				}

				// find point-on-path with the highest construction index
				if (((GeoPoint)geo).isPointOnPath()) {
					pointOnPathCount ++;
					if (retPath == null) {
						retPath = geo;
					}
					else
					{
						if (geo.getConstructionIndex() > retPath.getConstructionIndex()) retPath = geo;
					}
				}

				// find free point with the highest construction index
				if (((GeoPoint)geo).isIndependent()) {
					freePointCount ++;
					if (retFree == null) {
						retFree = geo;
					}
					else
					{
						if (geo.getConstructionIndex() > retFree.getConstructionIndex()) retFree = geo;						
					}
				}
			}
		}
		//Application.debug("pointOnPathCount"+pointOnPathCount);
		//Application.debug("freePointCount"+freePointCount);
		//Application.debug("pointCount"+pointCount);

		// return point-on-path with highest index
		if (pointOnPathCount > 0) return retPath;

		// return free-point with highest index
		if (freePointCount > 0) return retFree;

		// only one point in top layer, return it
		if (pointCount == 1) return ret;

		// just return the most recently created point
		if (pointCount > 1) return retIndex;

		/*
			try {
				throw new Exception("choose");
			} catch (Exception e) {
				e.printStackTrace();

			}
		 */
		
		
		// remove fixed objects (if there are some not fixed)
		if (!includeFixed && geos.size() > 1) {
			
			boolean allFixed = true;
			for (int i = 0 ; i < geos.size() ; i++)
				if (!((GeoElement)geos.get(i)).isFixed())
					allFixed = false;
			
			if (!allFixed)
			for (int i = geos.size() - 1 ; i >= 0 ; i--) {
				GeoElement geo = (GeoElement)geos.get(i);
				if (geo.isFixed()) geos.remove(i);		
			}
			
			if (geos.size() == 1)
				return (GeoElement)geos.get(0);
		}


		// no points selected, multiple objects selected
		// popup a menu to choose from
		ToolTipManager ttm = ToolTipManager.sharedInstance();		
		ttm.setEnabled(false);		
		ListDialog dialog = new ListDialog(view, geos, null);
		if (app.areChooserPopupsEnabled()) ret = dialog.showDialog(view, mouseLoc);			
		ttm.setEnabled(true);				
		}
		return ret;	

	}

	public void componentResized(ComponentEvent e) {
		// tell the view that it was resized
		view.updateSize();
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	/**
	 * Zooms in or out using mouse wheel
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		// don't allow mouse wheel zooming for applets if mode is not zoom mode
		boolean allowMouseWheel = 
			!app.isApplet() ||
			mode == EuclidianView.MODE_ZOOM_IN ||
			mode == EuclidianView.MODE_ZOOM_OUT ||
			(app.isShiftDragZoomEnabled() && 
					(e.isControlDown() || e.isMetaDown() || e.isShiftDown()));			
		if (!allowMouseWheel)
			return;

		setMouseLocation(e);

		//double px = view.width / 2d;
		//double py = view.height / 2d;
		double px = mouseLoc.x;
		double py = mouseLoc.y;
		double dx = view.xZero - px;
		double dy = view.yZero - py;


		double xFactor = 1;
		if (e.isAltDown())
			xFactor = 1.5;


		double factor = (e.getWheelRotation() > 0) ?
				EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor:
					1d / (EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR * xFactor);


		// make zooming a little bit smoother by having some steps

		view.setAnimatedCoordSystem(
				px + dx * factor,
				py + dy * factor,
				view.xscale * factor, 4, false);
		//view.yscale * factor);
		app.setUnsaved();
	}

	public void zoomInOut(KeyEvent event) {
		boolean allowZoom = 
			!app.isApplet() ||
			mode == EuclidianView.MODE_ZOOM_IN ||
			mode == EuclidianView.MODE_ZOOM_OUT ||
			(app.isShiftDragZoomEnabled());			
		if (!allowZoom)
			return;

		double px = mouseLoc.x;
		double py = mouseLoc.y;
		double dx = view.xZero - px;
		double dy = view.yZero - py;
		
		double factor = event.getKeyCode() == KeyEvent.VK_MINUS ? 1d / EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR
				: EuclidianView.MOUSE_WHEEL_ZOOM_FACTOR;

		// accelerated zoom
		if (event.isAltDown())
			factor *= event.getKeyCode() == KeyEvent.VK_MINUS ? 2d/3d: 1.5;

		// make zooming a little bit smoother by having some steps
		view.setAnimatedCoordSystem(
				px + dx * factor,
				py + dy * factor,
				view.xscale * factor, 4, false);
		//view.yscale * factor);
		app.setUnsaved();


	}
	
	/*
	 * when drawing a line, this is used when alt is down
	 * to set the angle to be a multiple of 15 degrees
	 */
	public void setLineEndPoint(Point2D.Double point) {
		lineEndPoint = point;
		useLineEndPoint = true;
	}

}

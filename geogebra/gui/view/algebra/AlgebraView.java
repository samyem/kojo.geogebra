/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgebraView.java
 *
 * Created on 27. September 2001, 11:30
 */

package geogebra.gui.view.algebra;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * AlgebraView with tree for free and dependent objects.
 * 
 * @author  Markus
 * @version 
 */
public class AlgebraView extends JTree implements View {	
		
	private static final long serialVersionUID = 1L;
	


	private Application app; // parent appame
	private Kernel kernel;
	private DefaultTreeModel model;
	
	private MyRenderer renderer;
	private MyDefaultTreeCellEditor editor;
	private JTextField editTF;
	
	// store all pairs of GeoElement -> node in the Tree
	private HashMap nodeTable = new HashMap(100);

	// nodes
	private DefaultMutableTreeNode root, depNode, indNode, auxiliaryNode;	
	private TreePath tpInd, tpDep, tpAux; // tree paths for main nodes

	private GeoElement selectedGeoElement;
	private DefaultMutableTreeNode selectedNode;
	
	//	closing cross
	private static BasicStroke crossStroke = new BasicStroke(1.5f); 
	private static int crossBorder = 4;
	private static int crossOffset = crossBorder + 6;
	private boolean highlightCross;

	/** Creates new AlgebraView */
	public AlgebraView(AlgebraController algCtrl) {		
		app = algCtrl.getApplication();
		kernel = algCtrl.getKernel();
		algCtrl.setView(this);					

		// tree's selection model	
		/*
		TreeSelectionModel tsm = new DefaultTreeSelectionModel();
		tsm.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		setSelectionModel(tsm);		
		tsm.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {				
				selectionChanged();	
			}				 
		});*/

		// cell renderer (tooltips) and editor
		ToolTipManager.sharedInstance().registerComponent(this);

		// EDITOR	   
		setEditable(true);
		initTreeCellRendererEditor();

		// add listener
		addMouseListener(algCtrl);
		addMouseMotionListener(algCtrl);		

		// build default tree structure
		root = new DefaultMutableTreeNode();
		depNode = new DefaultMutableTreeNode(); // dependent objects
		indNode = new DefaultMutableTreeNode();
		auxiliaryNode = new DefaultMutableTreeNode();
				
		
		// independent objects                  
		root.add(indNode);
		root.add(depNode);
		root.add(auxiliaryNode);

		// create model from root node
		model = new DefaultTreeModel(root);
		// this.treeModel = model;        
		setModel(model);
		setLargeModel(true);
		setLabels();

		// tree's options             
		setRootVisible(false);
		// show lines from parent to children
		putClientProperty("JTree.lineStyle", "Angled");
		setInvokesStopCellEditing(true);
		setScrollsOnExpand(true);	
		setRowHeight(-1); // to enable flexible height of cells
		
		tpInd = new TreePath(indNode.getPath());
		tpDep = new TreePath(depNode.getPath());
		tpAux =new TreePath(auxiliaryNode.getPath()); 	
		
		// needed in applets
		model.removeNodeFromParent(auxiliaryNode);
		
		attachView();						
	}
	
	boolean attached = false;

	public void attachView() {
		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);	
		attached = true;
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
		attached = false;
		//kernel.notifyRemoveAll(this);		
	}
	
	public void updateFonts() {
		Font font = app.getPlainFont();
		setFont(font);
		editor.setFont(font);
		renderer.setFont(font);
		editTF.setFont(font);
	}    

	private void initTreeCellRendererEditor() {
		renderer = new MyRenderer(app);		
		editTF = new JTextField();
		editor = new MyDefaultTreeCellEditor(this, renderer, 
									new MyCellEditor(editTF, app));
		
		editor.addCellEditorListener(editor); // self-listening
		setCellRenderer(renderer);
		setCellEditor(editor);
	}

	public void clearSelection() {
		super.clearSelection();
		selectedGeoElement = null;
	}

	public GeoElement getSelectedGeoElement() {
		return selectedGeoElement;
	}
	
	public boolean showAuxiliaryObjects() {
		return auxiliaryNode.getParent() != null;
	}
	
	public void setShowAuxiliaryObjects(boolean flag) {
		if (flag == showAuxiliaryObjects()) return;
		cancelEditing();
		
		if (flag) {
			clearView();
			//	add to root
			model.insertNodeInto(auxiliaryNode, root, root.getChildCount());		
			kernel.notifyAddAll(this);
		} else {
			model.removeNodeFromParent(auxiliaryNode);			
		}					
	}
	
	final public void paint(Graphics g) { 	 
		if (!kernel.isNotifyRepaintActive()) 
			return;
				
		super.paint(g);
				
		// draw a cross in the upper right corner 
		// to close the algebra view
		// Lalit Pant - don't draw closing cross on Kojo, because context menu gets out of sync
		// the real solution to this problem (if it shows up for other UI elements might be to 
		// tweak Application.updateMenubar. See there for more details
//		if (!app.isApplet())
//			drawClosingCross((Graphics2D) g);
	}		
	
	private void drawClosingCross(Graphics2D g2) {
		int width = getWidth();			
		g2.setStroke(crossStroke);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (highlightCross) {
			g2.setColor(Color.red);
		} else {
			g2.setColor(Color.gray);
		}
		g2.drawLine(width-crossOffset, crossBorder, width-crossBorder, crossOffset);
		g2.drawLine(width-crossOffset, crossOffset, width-crossBorder, crossBorder);
		
		if (highlightCross) {
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
								RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			// "close" 
			String strClose = app.getMenu("Close");
			TextLayout layout = new TextLayout(strClose, app.getSmallFont(), g2.getFontRenderContext());
			g2.setColor(Color.gray);
			
			int stringX = (int) (width - crossOffset - crossOffset - layout.getAdvance());
			
			g2.drawString(strClose, stringX, layout.getAscent()+2);
		}
	}
	
	boolean hitClosingCross(int x, int y) {
		return !app.isApplet() && 
				(y <= crossOffset) && 
		  		(x >= getWidth() - crossOffset);		
	}
	
	void setClosingCrossHighlighted(boolean flag) {
		if (flag == highlightCross) return;		
		highlightCross = flag;		
		repaint();
	}
	
	/* *
	 * selection mangament
	 *
	private GeoElement [] selectedGeos; 
	 
	
	public GeoElement [] getAllSelectedGeoElements() {
		return selectedGeos;
	}						
	
	private void selectionChanged() {
		TreePath[] paths = getSelectionPaths();			
		if (paths == null) { // no paths selected			
			app.clearSelectedGeos();
			return;
		} 
				
		// get all GeoElements out of selection		
		for (int i=0; i < paths.length; i++) {
			Object ob = paths[i].getLastPathComponent();
			Object userOb  = ((DefaultMutableTreeNode) ob).getUserObject();
			if (userOb instanceof GeoElement) {			
				app.addSelectedGeo((GeoElement) userOb, false);
			}
		}		
		kernel.notifyRepaint();
	}*/
	
	/*
	public void select(GeoElement geo, boolean flag) {
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) nodeTable.get(geo);
		if (node != null) {
			TreePath tp = new TreePath(node.getPath());			
			if (flag) 
				addSelectionPath(tp);
			else 
				removeSelectionPath(tp);
		}
	}	*/

	public static GeoElement getGeoElementForLocation(JTree tree, int x, int y) {
		TreePath tp = tree.getPathForLocation(x, y);
		return getGeoElementForPath(tp);
	}
		
	public static GeoElement getGeoElementForPath(TreePath tp) {
		if (tp == null)
			return null;

		Object ob;
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) tp.getLastPathComponent();
		if (node != null
			&& (ob = node.getUserObject()) instanceof GeoElement)
			return (GeoElement) ob;
		else
			return null;
	}
	
	public void setToolTipText(String text) {
		renderer.setToolTipText(text);
	}

	/**
	 * Open Editor textfield for geo.
	 */
	public void startEditing(GeoElement geo, boolean shiftDown) {
		if (geo == null) return;

		if (!shiftDown || !geo.isPointOnPath()) {
			if (!geo.isIndependent()
			|| !attached )		// needed for F2 when Algebra View closed
			 {
				if (geo.isRedefineable()) { 
					app.getGuiManager().showRedefineDialog(geo, true);
				}
				return;
			}
			
			if (!geo.isChangeable()) {
				if (geo.isFixed()) {
					//app.showMessage(app.getError("AssignmentToFixed"));
				} 
				else if (geo.isRedefineable()) { 
					app.getGuiManager().showRedefineDialog(geo, true);
				}
				return;
			}
		}
	
		
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) nodeTable.get(geo);

		if (node != null) {
			cancelEditing();
			// select and show node
			TreePath tp = new TreePath(node.getPath());
			setSelectionPath(tp); // select
			expandPath(tp);
			makeVisible(tp);
			scrollPathToVisible(tp);						
			startEditingAtPath(tp); // open editing text field
		}
	}

	/**
	 * resets all fix labels of the View. This method is called
	 * by the application if the language setting is changed.
	 */
	public void setLabels() {
		// tree node labels        		
		setNodeLabel(indNode, app.getPlain("FreeObjects"));
		setNodeLabel(depNode, app.getPlain("DependentObjects"));
		setNodeLabel(auxiliaryNode, app.getPlain("AuxiliaryObjects"));		
	}

	/** update everything up the tree */
	private void setNodeLabel(DefaultMutableTreeNode node, String label) {
		node.setUserObject(label);
		if (model != null) model.nodeChanged(node);
	}

	/*
	public String getToolTipText(MouseEvent evt) {				
		GeoElement geo = getGeoElementForLocation(evt.getX(), evt.getY());
	   	if (geo == null) return null;
	   	return geo.getLongDescriptionHTML(true, true);    	   	
	}*/

	/**
	 * adds a new node to the tree
	 */
	public void add(GeoElement geo) {	
		if (isEditing())
			cancelEditing();	
				
		if (geo.isLabelSet() && geo.isSetAlgebraVisible()) {			
			DefaultMutableTreeNode parent, node;
			node = new DefaultMutableTreeNode(geo);
			if (geo.isAuxiliaryObject()) {
				parent = auxiliaryNode;
			}				
			else if (geo.isIndependent()) {			
				parent = indNode;				
			} 
			else {				
				parent = depNode;
			}

			// add node to model (alphabetically ordered)
			int pos = getInsertPosition(parent, geo);			
			model.insertNodeInto(node, parent, pos);				
			nodeTable.put(geo, node);
			
			// show new node			
			if (parent == indNode)
				expandPath(tpInd);
			else if (parent == depNode)
				expandPath(tpDep);
			else
				expandPath(tpAux);
								
			//TreePath tp = new TreePath(node.getPath());			
			//this.scrollPathToVisible(tp);											
		}
	}

	
	/**
	 * Gets the insert position for newGeo to insert it in alphabetical
	 * order in parent node. Note: all children of parent must have instances of GeoElement 
	 * as user objects.
	 */
	final public static int getInsertPosition(DefaultMutableTreeNode parent, GeoElement newGeo) { 							
		// label of inserted geo
		String newLabel = newGeo.getLabel();
		
		// standard case: binary search		
		int left = 0;
		int right = parent.getChildCount();	
		if (right == 0) return right;
		
		// bigger then last?
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastChild();	
		String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();	
		if (newLabel.compareTo(nodeLabel) > 0) 
			return right;				
		
		// binary search
		while (right > left) {							
			int middle = (left + right) / 2;
			node = (DefaultMutableTreeNode) parent.getChildAt(middle);
			nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
			
			if (newLabel.compareTo(nodeLabel) < 0) {
				right = middle;
			} else {
				left = middle + 1;
			}
		}													
		
		// insert at correct position
		return right;				
	}
	
	/**
	 * Performs a binary search for geo among the children of parent. All children of parent
	 * have to be instances of GeoElement sorted alphabetically by their names.
	 * @return -1 when not found
	 */
	final public static int binarySearchGeo(DefaultMutableTreeNode parent, String geoLabel) { 				
		int left = 0;
		int right = parent.getChildCount()-1;
		if (right == -1) return -1;
	
		// binary search for geo's label
		while (left <= right) {							
			int middle = (left + right) / 2;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(middle);
			String nodeLabel = ((GeoElement) node.getUserObject()).getLabel();
			
			int compare = geoLabel.compareTo(nodeLabel);
			if (compare < 0)
				right = middle -1;
		    else if (compare > 0)
		    	left = middle + 1;	
		    else
		    	return middle;
		}												  
		
		return -1;				
	}		
	
	/**
	 * Performs a linear search for geo among the children of parent.
	 * @return -1 when not found
	 */
	final public static int linearSearchGeo(DefaultMutableTreeNode parent, String geoLabel) { 												
		int childCount = parent.getChildCount();	
		for (int i = 0; i < childCount; i++) {			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
			GeoElement g = (GeoElement) node.getUserObject();
			if (geoLabel.equals(g.getLabel()))
				return i;
		}
		return -1;
	}

	/**
	 * removes a node from the tree
	 */
	public void remove(GeoElement geo) {
		if (isEditing())
			cancelEditing();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodeTable.get(geo);
		if (node != null) removeFromModel(node);		
	}
	
	public void clearView() {
		nodeTable.clear();		
		indNode.removeAllChildren();
		depNode.removeAllChildren();
		auxiliaryNode.removeAllChildren();
		model.reload();
	}
	
	final public void repaintView() {
		repaint();
	}

	/**
	 * renames an element and sorts list 
	 */
	public void rename(GeoElement geo) {
		remove(geo);
		add(geo);
	}
	
	public void reset() {
	  if (isEditing())
		cancelEditing();
	  repaint();
	}

	private void removeFromModel(DefaultMutableTreeNode node) {
		// remove node from model
		model.removeNodeFromParent(node);
		nodeTable.remove(node.getUserObject());
		//updateNodeLabel(parent);                   
	}

	/**
	   * updates node of GeoElement geo (needed for highlighting)
	   * @see EuclidianView.setHighlighted()
	   */
	final public void update(GeoElement geo) {	
		if (isEditing())
			cancelEditing();
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) nodeTable.get(geo);						
		if (node != null) {
			model.nodeChanged(node);		
		} 	
	}
	
	final public void updateAuxiliaryObject(GeoElement geo) {
		remove(geo);
		add(geo);
	}		



	/**
	 * inner class MyEditor handles editing of tree nodes
	 *
	 * Created on 28. September 2001, 12:36
	 */
	private class MyDefaultTreeCellEditor
		extends DefaultTreeCellEditor
		implements CellEditorListener {

		public MyDefaultTreeCellEditor(AlgebraView tree, DefaultTreeCellRenderer renderer,
								DefaultCellEditor editor) {
			super(tree, renderer, editor);						
		}

		/*
		 * CellEditorListener implementation 
		*/
		public void editingCanceled(ChangeEvent event) {
		}

		public void editingStopped(ChangeEvent event) {
			// get the entered String
			String newValue = getCellEditorValue().toString();
			
			// the userObject was changed to this String
			// reset it to the old userObject, which we stored
			// in selectedGeoElement (see valueChanged())        
			// only nodes with a GeoElement as userObject can be edited!		
			selectedNode.setUserObject(selectedGeoElement);
			
			// change this GeoElement in the Kernel   
			
			// allow shift-double-click on a PointonPath in Algebra View to 
			// change without redefine
			boolean redefine = !selectedGeoElement.isPointOnPath();
			
			GeoElement geo = kernel.getAlgebraProcessor().changeGeoElement(selectedGeoElement, newValue, redefine);			
			if (geo != null) {				
				selectedGeoElement = geo;
				selectedNode.setUserObject(selectedGeoElement);
			}			
			model.nodeChanged(selectedNode); // refresh display        
		}

		/*
		 * OVERWRITE SOME METHODS TO ONLY ALLOW EDITING OF GeoElements
		 */

		public boolean isCellEditable(EventObject event) {
			
			if (event == null) return true;
			
			return false;		
		}
		//
		// TreeSelectionListener
		//

		/**
		 * Resets lastPath.
		 */
		public void valueChanged(TreeSelectionEvent e) {
			if (tree != null) {
				if (tree.getSelectionCount() == 1)
					lastPath = tree.getSelectionPath();
				else
					lastPath = null;
				/***** ADDED by Markus Hohenwarter ***********/
				storeSelection(lastPath);
				/********************************************/
			}
			if (timer != null) {
				timer.stop();
			}
		}

		/** stores currently selected GeoElement and node.
		 *  selectedNode, selectedGeoElement are private members of AlgebraView
		 */
		private void storeSelection(TreePath tp) {
			if (tp == null)
				return;

			Object ob;
			selectedNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
			if (selectedNode != null
				&& (ob = selectedNode.getUserObject()) instanceof GeoElement) {
				selectedGeoElement = (GeoElement) ob;
			} else {
				selectedGeoElement = null;
			}
		}

	}  // MyEditor

	public String getContentXML() {		
		return "";
	}

	public String getGuiXML() {		
		return "";
	}
	
	
	

} // AlgebraView

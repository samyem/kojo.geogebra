package geogebra.gui.menubar;

import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.DefaultGuiManager;
import geogebra.gui.ToolCreationDialog;
import geogebra.gui.ToolManagerDialog;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public abstract class MenubarImpl extends JMenuBar implements Menubar {

	private static final long serialVersionUID = 1736020764918189176L;

	// Actions
	protected AbstractAction refreshAction, recomputeAllViews,
			drawingPadToClipboardAction, deleteAll, newWindowAction,
			propertiesAction, constProtocolAction, drawingPadPropAction,
			toolbarConfigAction, showAlgebraViewAction, showAlgebraInputAction,
			showSpreadsheetAction,     // Michael Borcherds 2008-01-14
			showCmdListAction, horizontalSplitAction,
			showAuxiliaryObjectsAction, showConsProtNavigationAction,
			showConsProtNavigationPlayAction,
			showConsProtNavigationOpenProtAction, loadAction, saveAction,
			saveAsAction, //printProtocolAction, 
			printEuclidianViewAction,
			exitAction, exitAllAction, helpAction,
			// updateAction,
			infoAction, exportGraphicAction, exportWorksheet,
			exportPSTricksAction,exportPgfAction,
			showCreateToolsAction, showManageToolsAction,
			savePreferencesAction, clearPreferencesAction,
			selectAllAction, deleteAction, websiteAction, forumAction, wikiAction,
			selectCurrentLayerAction, // Michael Borcherds 2008-03-03
			selectAllDescendantsAction, selectAllAncestorsAction;
			
	protected JCheckBoxMenuItem cbShowAxes, cbShowGrid, cbShowAlgebraView,
	        cbShowSpreadsheet,     // Michael Borcherds 2008-01-14
			cbShowAuxiliaryObjects, cbHorizontalSplit,
			cbShowConsProtNavigation, cbShowConsProtNavigationPlay,
			cbShowConsProtNavigationOpenProt, cbShowAlgebraInput,
			cbShowCmdList;

	protected JMenu menuAngleUnit, menuPointCapturing, menuDecimalPlaces,
			menuContinuity, menuPointStyle, menuBooleanSize, menuRightAngleStyle,
			menuCoordStyle, menuLabeling, menuWindow, menuFile, menuTools,
			menuAlgebraStyle; //G.Sturr 2009-10-18
	
	//private JMenuItem miCloseAll;

	protected Application app;

	protected Kernel kernel;
	
	private JMenu viewMenu, optionsMenu, editMenu;

	public MenubarImpl() {

	}
	
	public JMenu getViewMenu() {
		return viewMenu;
	}
	
	public JMenu getOptionsMenu() {
		return optionsMenu;
	}
	
	public JMenu getEditMenu() {
		return editMenu;
	}
	
	public Action getSelectAllAction() {
		return selectAllAction;
	}
	
	public void updateMenubar() {	
		EuclidianView ev = app.getEuclidianView();
		cbShowAxes.setSelected(ev.getShowXaxis() && ev.getShowYaxis());
	    cbShowGrid.setSelected(ev.getShowGrid());
	    
	    cbShowAlgebraView.setSelected(app.showAlgebraView());
	    cbShowSpreadsheet.setSelected(app.showSpreadsheetView());     // Michael Borcherds 2008-01-14
        cbShowAlgebraInput.setSelected(app.showAlgebraInput());
        cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());

		boolean showAlgebraView = app.showAlgebraView();
		boolean showSpreadsheetView = app.showSpreadsheetView();
		cbShowAlgebraView.setSelected(showAlgebraView);
		cbShowAuxiliaryObjects.setVisible(showAlgebraView);
		cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());

		cbHorizontalSplit.setVisible(showAlgebraView || showSpreadsheetView);
		cbHorizontalSplit.setSelected(app.isHorizontalSplit());
		
		cbShowAlgebraInput.setSelected(app.showAlgebraInput());
		cbShowCmdList.setSelected(app.showCmdList());
		cbShowCmdList.setVisible(app.showAlgebraInput());		
			
		
		cbShowConsProtNavigation.setSelected(app.showConsProtNavigation());		
		
		DefaultGuiManager guiMan = (DefaultGuiManager) app.getGuiManager();
		if (guiMan != null) {
			cbShowConsProtNavigationPlay
					.setSelected(guiMan.isConsProtNavigationPlayButtonVisible());
			cbShowConsProtNavigationOpenProt
					.setSelected(guiMan.isConsProtNavigationProtButtonVisible());
		}
			
		cbShowConsProtNavigationPlay.setVisible(app.showConsProtNavigation());
		cbShowConsProtNavigationOpenProt.setVisible(app.showConsProtNavigation());	

		// Michael Borcherds 2008-03-03 BEGIN put these back in
        updateMenuContinuity();
        updateMenuPointCapturing();
        updateMenuAngleUnit();
        updateMenuDecimalPlaces();
        updateMenuPointStyle();
        updateMenuBooleanSize(); // Michael Borcherds 2008-05-12
        updateMenuRightAngleStyle();
        updateMenuCoordStyle();	
        updateMenuLabeling();        
        updateMenuViewDescription(); //G.Sturr 2009-10-18
        updateActions();
        updateSelection();
		// Michael Borcherds 2008-03-03 END
                
	}
	


	public void updateMenuFile() {
		if (menuFile == null) return;
		
		menuFile.removeAll();
		
		JMenu menu = menuFile;
		JMenuItem mi;
		
		if (app.isStandaloneApplication() && app.hasFullPermissions()) {
			// "New" in application: new window
			mi = new JMenuItem(newWindowAction);	
			setMenuShortCutAccelerator(mi, 'N');
			menu.add(mi);
		}			
		
		// "New": reset 
		mi = menu.add(deleteAll);	
		
		if (app.hasFullPermissions()) {
			mi = menu.add(loadAction);
			setMenuShortCutAccelerator(mi, 'O'); // open
			menu.addSeparator();
			mi = menu.add(saveAction);
			setMenuShortCutAccelerator(mi, 'S');
			mi = menu.add(saveAsAction);	
			// now assigned to spreadsheet: setMenuShortCutShiftAccelerator(mi, 'S');
			
			// export
			menu.addSeparator();
			JMenu submenu = new JMenu(app.getMenu("Export"));
			submenu.setIcon(app.getImageIcon("text-html.png"));
			menu.add(submenu);
			mi = submenu.add(exportWorksheet);
			setMenuShortCutShiftAccelerator(mi, 'W');
		
			submenu.addSeparator();
			//submenu.add(htmlCPAction);
			mi = submenu.add(exportGraphicAction);
			setMenuShortCutShiftAccelerator(mi, 'P');
			
			mi = submenu.add(drawingPadToClipboardAction);
			// ctrl-shift-c is also handled in MyKeyListener
			setMenuShortCutShiftAccelerator(mi, 'C');
			
			submenu.addSeparator();
			mi = submenu.add(exportPSTricksAction);
			setMenuShortCutShiftAccelerator(mi, 'T');
			
			// Added by Loïc Le Coq
			mi = submenu.add(exportPgfAction);
			//End
		
		
			menu.addSeparator();
		
	//		submenu = new JMenu(app.getMenu("PrintPreview"));
	//		submenu.setIcon(app.getImageIcon("document-print-preview.png"));
	//		submenu.add(printEuclidianViewAction);
	//	//	submenu.add(printProtocolAction);
	//		menu.add(submenu);
			mi = menu.add(printEuclidianViewAction);
			mi.setText(app.getMenu("PrintPreview") + " ...");
			mi.setIcon(app.getImageIcon("document-print-preview.png"));
			setMenuShortCutAccelerator(mi, 'P');
		}
				
		// STOP here when not a standalone application
		if (!app.isStandaloneApplication()) return;
		
		if (app.hasFullPermissions()) {
			// LAST FILES list
			int size = Application.getFileListSize();
			if (size > 0) {
				menu.addSeparator();						
				for (int i = 0; i < 4; i++) {
					File file = Application.getFromFileList(i);
					if (file != null) {										
						mi = new JMenuItem(file.getName());
						mi.setIcon(app.getImageIcon("geogebra.gif"));					
						ActionListener al = new LoadFileListener(app, file);
						mi.addActionListener(al);
						menu.add(mi);
					}
				}						
			}
		}
		
		// close
		menu.addSeparator();
		mi = menu.add(exitAction);
		if (Application.MAC_OS) {
			setMenuShortCutAccelerator(mi, 'W');
		} else {
			// Alt + F4
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK);
			mi.setAccelerator(ks);
		}

		// close all		
		if (GeoGebraFrame.getInstanceCount() > 1) {								
			menu.add(exitAllAction);
		}
	}

	public void initMenubar() {
		initActions();
		
		JMenu menu, submenu;
		JMenuItem mi;
		int pos;
		removeAll();

		
		// File
		menuFile = new JMenu(app.getMenu("File"));
		updateMenuFile();	
		add(menuFile);
		
		// Edit
		menu = editMenu();
		editMenu = editMenu();
		
		add(menu);

		// View
		menu = viewMenu(true);
		viewMenu = viewMenu(false);
		
		add(menu);

		// Options
		menu = optionsMenu(true);
		optionsMenu = optionsMenu(false);

		add(menu);

		
		if (app.isStandaloneApplication() && Application.hasFullPermissions()) 
		{
			// Preferences
			menu.addSeparator();
			//submenu = new JMenu(app.getMenu("Settings"));			
			menu.add(savePreferencesAction);
			menu.add(clearPreferencesAction);
			//menu.add(submenu);
		}
		
		
		// tools menu		
		menuTools = new JMenu(app.getMenu("Tools"));
		add(menuTools);
		menuTools.add(showCreateToolsAction);
		menuTools.add(showManageToolsAction);
		menuTools.addSeparator();
		menuTools.add(toolbarConfigAction);

		if (app.isStandaloneApplication() && app.hasFullPermissions()) 
		{
			// windows menu
			menuWindow = new JMenu(app.getMenu("Window"));
			updateMenuWindow();
			add(menuWindow);
						
			if (app.getPluginManager() != null) {			
				javax.swing.JMenu pim=app.getPluginManager().getPluginMenu();
				if(pim!=null){ add(pim);}     // H-P Ulven 2008-04-17
			}
		}

		// help
		menu = new JMenu(app.getMenu("Help"));
		menu.add(helpAction);
		menu.addSeparator();

		menu.add(websiteAction);
		menu.add(forumAction);
		menu.add(wikiAction);

		menu.addSeparator();

		menu.add(infoAction);
		add(menu);
		
		updateMenubar();
	}

	private JMenu editMenu() {
		JMenu menu;
		JMenuItem mi;
		menu = new JMenu(app.getMenu("Edit"));
		if (app.isUndoActive()) {
			mi = menu.add(app.getGuiManager().getUndoAction());
//			setMenuShortCutAccelerator(mi, 'Z');
			mi = menu.add(app.getGuiManager().getRedoAction());
//			if (Application.MAC_OS)
//				// Command-Shift-Z
//				setMenuShortCutShiftAccelerator(mi, 'Z');
//			else
//				// Ctrl-Y
//				setMenuShortCutAccelerator(mi, 'Y');
			menu.addSeparator();
		}
		
		//mi = menu.add(DataFromClipboardAction);
		//setMenuShortCutAccelerator(mi, 'V');
				
		if (app.letDelete()) {
			mi = menu.add(deleteAction);
		
			if (Application.MAC_OS) {
				mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0));							
			} else {				
				mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
			}	
		}
		menu.addSeparator();
		
		mi = menu.add(selectAllAction);
		setMenuShortCutAccelerator(mi, 'A');
		
		mi = menu.add(selectCurrentLayerAction);
//		setMenuShortCutAccelerator(mi, 'L');
		
		mi = menu.add(selectAllDescendantsAction);
//		setMenuShortCutShiftAccelerator(mi, 'J'); // changed from 'Q' - doesn't work on MacOS
		
		mi = menu.add(selectAllAncestorsAction);
//		setMenuShortCutAccelerator(mi, 'J'); // changed from 'Q' - doesn't work on MacOS
		
		menu.addSeparator();
		
		if (app.hasFullPermissions()) {
			// Michael Borcherds 2008-03-03 added to Edit menu
			mi = menu.add(drawingPadToClipboardAction);
//			setMenuShortCutShiftAccelerator(mi, 'C');
			menu.addSeparator();
		}

		mi = menu.add(propertiesAction);
//		setMenuShortCutAccelerator(mi, 'E');
		return menu;
	}

	private JMenu optionsMenu(boolean inMenuBar) {
		JMenu menu;
		JMenu submenu;
		int pos;
		menu = new JMenu(app.getMenu("Options"));
		
		//G.Sturr 2009-10-18
		// Algebra description: show value or definition of objects
		menuAlgebraStyle = new JMenu(app.getPlain("Algebra"));
		menuAlgebraStyle.setIcon(app.getEmptyIcon());
		String[] strDescription = { app.getPlain("Value"), 
				app.getPlain("Definition"), 
				app.getPlain("Command") };
		String[] strDescriptionAC = { "0", "1", "2" };
		ActionListener descAL = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int desc = Integer.parseInt(ae.getActionCommand());
				kernel.setAlgebraStyle(desc);
				kernel.updateConstruction();
			}
		};
		addRadioButtonMenuItems(menuAlgebraStyle, descAL, strDescription, strDescriptionAC,0);
		menu.add(menuAlgebraStyle);
		updateMenuViewDescription();
		//END G.Sturr
		
		menu.addSeparator();

		// point capturing
		menuPointCapturing = new JMenu(app.getMenu("PointCapturing"));
		menuPointCapturing.setIcon(app.getImageIcon("magnet.gif"));
		String[] strPointCapturing = { 
				"Labeling.automatic",
				"on",
				app.getMenu("on") + " (" + app.getMenu("Grid") + ")", 
				"off" };
		String[] strPointCapturingAC = { 
				"3 PointCapturing" ,
				"1 PointCapturing",
				"2 PointCapturing", 
				"0 PointCapturing" };
		addRadioButtonMenuItems(menuPointCapturing, (ActionListener) this, strPointCapturing,
				strPointCapturingAC, 0);
		menu.add(menuPointCapturing);
		updateMenuPointCapturing();

		// Angle unit
		menuAngleUnit = new JMenu(app.getMenu("AngleUnit"));
		menuAngleUnit.setIcon(app.getImageIcon("mode_angle_16.gif"));
		String[] strAngleUnit = { "Degree", "Radiant" };
		addRadioButtonMenuItems(menuAngleUnit, (ActionListener) this, strAngleUnit, strAngleUnit,
				0);
		menu.add(menuAngleUnit);
		updateMenuAngleUnit();

		// decimal places
		menuDecimalPlaces = new JMenu(app.getMenu("Rounding"));
		menuDecimalPlaces.setIcon(app.getEmptyIcon());
		/*int max_dec = 15;
		String[] strDecimalSpaces = new String[max_dec + 1];
		String[] strDecimalSpacesAC = new String[max_dec + 1];
		for (int i=0; i <= max_dec; i++){ 
			strDecimalSpaces[i] = Integer.toString(i);
			strDecimalSpacesAC[i] = i + " decimals";
		}*/
		String[] strDecimalSpaces = app.getRoundingMenu();

		addRadioButtonMenuItems(menuDecimalPlaces, (ActionListener) this, strDecimalSpaces,
				Application.strDecimalSpacesAC, 0);
		menu.add(menuDecimalPlaces);
		updateMenuDecimalPlaces();

		// continuity
		menuContinuity = new JMenu(app.getMenu("Continuity"));
		menuContinuity.setIcon(app.getEmptyIcon());
		String[] strContinuity = { "on", "off" };
		String[] strContinuityAC = { "true Continuity", "false Continuity" };
		addRadioButtonMenuItems(menuContinuity, (ActionListener) this, strContinuity,
				strContinuityAC, 0);
		menu.add(menuContinuity);
		updateMenuContinuity();

		menu.addSeparator();
		
		// point style
		menuPointStyle = new JMenu(app.getMenu("PointStyle"));
		menuPointStyle.setIcon(app.getImageIcon("mode_point_16.gif"));
		// dot, circle, cross, plus
		String[] strPointStyle = { "\u25cf", "\u25cb", "\u2716", "\u271a", "\u25c6", "\u25b2", "\u25bc" };
		String[] strPointStyleAC = { "0", "2", "1", "3", "4", "6", "7" };
		ActionListener psal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int style = Integer.parseInt(ae.getActionCommand());
				app.getEuclidianView().setPointStyle(style);
			}
		};
		addRadioButtonMenuItems(menuPointStyle, psal, strPointStyle,
				strPointStyleAC, 0);
		menu.add(menuPointStyle);
		updateMenuPointStyle();
		
		// coordinate style
		menuCoordStyle = new JMenu(app.getPlain("Coordinates"));
		menuCoordStyle.setIcon(app.getEmptyIcon());
		// dot, circle, cross
		String[] strCoordStyle = { "A = (x, y)", "A(x | y)", "A: (x, y)"};
		String[] strCoordStyleAC = { "0", "1", "2"};
		ActionListener csal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int style = Integer.parseInt(ae.getActionCommand());
				kernel.setCoordStyle(style);
				kernel.updateConstruction();
			}
		};
		addRadioButtonMenuItems(menuCoordStyle, csal, strCoordStyle,
				strCoordStyleAC, 0);
		menu.add(menuCoordStyle);
		updateMenuCoordStyle();

		// checkboxsize
		// Michael Borcherds 2008-05-12
		menuBooleanSize = new JMenu(app.getMenu("CheckboxSize"));
		menuBooleanSize.setIcon(app.getImageIcon("checkbox16.gif"));
		// dot, circle, cross
		String[] strBooleanSize = { app.getMenu("CheckboxSize.Regular"), app.getMenu("CheckboxSize.Large") };
		String[] strBooleanSizeAC = { "13", "26" };
		ActionListener bsal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int size = Integer.parseInt(ae.getActionCommand());
				app.getEuclidianView().setBooleanSize(size);
			}
		};
		addRadioButtonMenuItems(menuBooleanSize, bsal, strBooleanSize,
				strBooleanSizeAC, 0);
		menu.add(menuBooleanSize);
		updateMenuBooleanSize();

		// added by Lo�c BEGIN
		// right angle style

		menuRightAngleStyle = new JMenu(app.getMenu("RightAngleStyle"));
		menuRightAngleStyle.setIcon(app.getImageIcon("right_angle.gif"));
		// dot, none, square
	//	String[] strAngleStyle = { app.getPlain("off"), "\u25a1", "\u2219", app.getPlainFont().canDisplay('\u2335') ? "\u2335" : "|_" };
		
		String[] strAngleStyle = { app.getPlain("off"), "\u25a1", "\u2219", "\u2335" };
		
		
		String[] strAngleStyleAC = {
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_NONE),
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_SQUARE),
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_DOT),
				String.valueOf(EuclidianView.RIGHT_ANGLE_STYLE_L) };
		ActionListener asal = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int style = Integer.parseInt(ae.getActionCommand());
				app.getEuclidianView().setRightAngleStyle(style);
			}
		};
		addRadioButtonMenuItems(menuRightAngleStyle, asal, strAngleStyle,
				strAngleStyleAC, 0);
		menu.add(menuRightAngleStyle);
		updateMenuRightAngleStyle();
		// END
		
		// Labeling
		menuLabeling = new JMenu(app.getMenu("Labeling"));
		menuLabeling.setIcon(app.getImageIcon("mode_showhidelabel_16.gif"));
		String[] lstr = { "Labeling.automatic", "Labeling.on", "Labeling.off", "Labeling.pointsOnly"  };		
		String[] lastr = { "0_labeling", "1_labeling", "2_labeling", "3_labeling"  };
		addRadioButtonMenuItems(menuLabeling, (ActionListener) this, lstr, lastr, 0);
		menu.add(menuLabeling);		

		/*
		// Graphics quality
		submenu = new JMenu(app.getMenu("GraphicsQuality"));
		String[] gqfi = { "LowQuality", "HighQuality" };
		if (app.getEuclidianView().getAntialiasing())
			pos = 1;
		else
			pos = 0;
		addRadioButtonMenuItems(submenu, this, gqfi, gqfi, pos);
		menu.add(submenu);
		*/
		
		menu.addSeparator();

		// Font size
		submenu = new JMenu(app.getMenu("FontSize"));
		submenu.setIcon(app.getImageIcon("font.png"));
		String[] fsfi = { "12 pt", "14 pt", "16 pt", "18 pt", "20 pt", "24 pt",
				"28 pt", "32 pt" };

		// find current pos
		String strFS = app.getFontSize() + " pt";
		pos = 0;
		for (int i = 0; i < fsfi.length; i++) {
			if (strFS.equals(fsfi[i])) {
				pos = i;
				break;
			}
		}

		addRadioButtonMenuItems(submenu, (ActionListener) this, fsfi, fsfi, pos);
		menu.add(submenu);

		/*
		 * // FontName menuFontName = new JMenu(getMenu("PointCapturing"));
		 * String[] strFontName = { "Sans Serif", "Serif" }; String[]
		 * strFontNameAC = { "SansSerif", "Serif" };
		 * addRadioButtonMenuItems(menuFontName, al, strFontName, strFontNameAC,
		 * 0); menu.add(menuFontName); updateMenuFontName();
		 */

		//menu.addSeparator();

		if (inMenuBar) {
			// Language
			if (app.propertiesFilesPresent()) {
				LanguageActionListener langListener = new LanguageActionListener();
				submenu = new JMenu(app.getMenu("Language"));
				submenu.setIcon(app.getImageIcon("globe.png"));
				addLanguageMenuItems(submenu, langListener);
				menu.add(submenu);
			}

			menu.addSeparator();

			// drawing pad properteis
			menu.add(drawingPadPropAction);
		}
		return menu;
	}

	private JMenu viewMenu(boolean inMenuBar) {
		JMenu menu;
		JMenuItem mi;
		menu = new JMenu(app.getMenu("View"));
		if (inMenuBar) {
			cbShowAxes = new JCheckBoxMenuItem(app.getGuiManager()
					.getShowAxesAction());
			cbShowAxes.setSelected(app.getEuclidianView().getShowXaxis()
					&& app.getEuclidianView().getShowYaxis());
			menu.add(cbShowAxes);
			
			cbShowGrid = new JCheckBoxMenuItem(app.getGuiManager()
					.getShowGridAction());
			cbShowGrid.setSelected(app.getEuclidianView().getShowGrid());
			menu.add(cbShowGrid);
			menu.addSeparator();
		}
		cbShowAlgebraView = new JCheckBoxMenuItem(showAlgebraViewAction);		
		cbShowAlgebraView.setIcon(app.getEmptyIcon());
		cbShowAlgebraView.setSelected(app.showAlgebraView());
		// setMenuShortCutShiftAccelerator(cbShowAlgebraView, 'A');
		menu.add(cbShowAlgebraView);

	    // Michael Borcherds 2008-01-14
		cbShowSpreadsheet = new JCheckBoxMenuItem(showSpreadsheetAction);		
		cbShowSpreadsheet.setIcon(app.getEmptyIcon());
		cbShowSpreadsheet.setSelected(app.showSpreadsheetView());	
		// setMenuShortCutShiftAccelerator(cbShowSpreadsheet, 'S');
		menu.add(cbShowSpreadsheet);		
		
		cbShowAuxiliaryObjects = new JCheckBoxMenuItem(
				showAuxiliaryObjectsAction);
		cbShowAuxiliaryObjects.setIcon(app.getEmptyIcon());
		cbShowAuxiliaryObjects.setSelected(app.showAuxiliaryObjects());
		menu.add(cbShowAuxiliaryObjects);

		cbHorizontalSplit = new JCheckBoxMenuItem(horizontalSplitAction);				
		cbHorizontalSplit.setIcon(app.getEmptyIcon());
		menu.add(cbHorizontalSplit);

		menu.addSeparator();

		// show/hide cmdlist, algebra input
		cbShowAlgebraInput = new JCheckBoxMenuItem(showAlgebraInputAction);		
		menu.add(cbShowAlgebraInput);

		cbShowCmdList = new JCheckBoxMenuItem(showCmdListAction);		
		menu.add(cbShowCmdList);
		menu.addSeparator();

		// Construction Protocol
		cbShowConsProtNavigation = new JCheckBoxMenuItem(
				showConsProtNavigationAction);
		cbShowConsProtNavigationPlay = new JCheckBoxMenuItem(
				showConsProtNavigationPlayAction);
		cbShowConsProtNavigationOpenProt = new JCheckBoxMenuItem(
				showConsProtNavigationOpenProtAction);	
		menu.add(constProtocolAction);
		menu.add(cbShowConsProtNavigation);
		menu.add(cbShowConsProtNavigationPlay);
		menu.add(cbShowConsProtNavigationOpenProt);		
		
		menu.addSeparator();
		mi = menu.add(refreshAction);
		// setMenuShortCutAccelerator(mi, 'F');
		
		mi = menu.add(recomputeAllViews);
		// setMenuShortCutAccelerator(mi, 'R');
		//KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_R, 
		//		Application.MAC_OS ? InputEvent.META_MASK : 0 );
		//mi.setAccelerator(ks);
		return menu;
	}

	protected void initActions() {				
		showAlgebraViewAction = new AbstractAction(app.getPlain("AlgebraWindow")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.setShowAlgebraView(!app.showAlgebraView());
				app.updateCenterPanel(true);
				app.setDefaultCursor();
			}
		};

	    // Michael Borcherds 2008-01-14
		showSpreadsheetAction = new AbstractAction(app.getPlain("Spreadsheet")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.setShowSpreadsheetView(!app.showSpreadsheetView());
				app.updateCenterPanel(true);
				app.setDefaultCursor();
			}
		};

		showAlgebraInputAction = new AbstractAction(app.getMenu("InputField"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.setShowAlgebraInput(!app.showAlgebraInput());
				app.updateContentPane();
				app.setDefaultCursor();
			}
		};

		showCmdListAction = new AbstractAction(app.getMenu("CmdList"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.setShowCmdList(!app.showCmdList());
				if (app.getGuiManager().getAlgebraInput() != null)
					SwingUtilities.updateComponentTreeUI(app.getGuiManager().getAlgebraInput());
				app.setDefaultCursor();
			}
		};

		horizontalSplitAction = new AbstractAction(app.getPlain("HorizontalSplit")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.setHorizontalSplit(!app.isHorizontalSplit());
				app.updateCenterPanel(true);
				app.setDefaultCursor();
			}
		};

		showAuxiliaryObjectsAction = new AbstractAction(
				app.getPlain("AuxiliaryObjects")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.setShowAuxiliaryObjects(!app.showAuxiliaryObjects());
				app.setUnsaved();	
				app.setDefaultCursor();
			}
		};

		showConsProtNavigationAction = new AbstractAction(
				app.getPlain("ConstructionProtocolNavigation"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.setShowConstructionProtocolNavigation(!app.showConsProtNavigation());
				app.setUnsaved();	
				app.updateCenterPanel(true);
				app.setDefaultCursor();
			}
		};

		showConsProtNavigationPlayAction = new AbstractAction(
				app.getPlain("PlayButton"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				ConstructionProtocolNavigation cpn =
					(ConstructionProtocolNavigation) app.getGuiManager().getConstructionProtocolNavigation();
				cpn.setPlayButtonVisible(!cpn.isPlayButtonVisible());
				//cpn.initGUI();
				SwingUtilities.updateComponentTreeUI(cpn);
				app.setUnsaved();
				app.setDefaultCursor();
			}
		};

		showConsProtNavigationOpenProtAction = new AbstractAction(
				app.getPlain("ConstructionProtocolButton"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				ConstructionProtocolNavigation cpn =
					(ConstructionProtocolNavigation) app.getGuiManager().getConstructionProtocolNavigation();
				cpn.setConsProtButtonVisible(!cpn.isConsProtButtonVisible());
				//cpn.initGUI();
				SwingUtilities.updateComponentTreeUI(cpn);
				app.setUnsaved();
				app.setDefaultCursor();
			}
		};

		deleteAll = new AbstractAction(app.getMenu("New"),
				app.getEmptyIcon()) {				
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();		
				app.fileNew();
				app.setDefaultCursor();
			}
		};

		newWindowAction = new AbstractAction(app.getMenu("NewWindow"),
				app.getImageIcon("document-new.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
		    		public void run() {   
		    			app.setWaitCursor();
		    			GeoGebraFrame.createNewWindow(null);		    		
		    			app.setDefaultCursor();
		    		}
		    	};
		    	runner.start();				
			}
		};

		propertiesAction = new AbstractAction(app.getPlain("Properties") + " ...",
				app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showPropertiesDialog();
			}
		};

		constProtocolAction = new AbstractAction(
				app.getPlain("ConstructionProtocol") + " ...",
				app.getImageIcon("table.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				
				try {
					Thread runner = new Thread() {
						public void run() {
							app.getGuiManager().showConstructionProtocol();
						}
					};
					runner.start();
				}
				
				catch (java.lang.NoClassDefFoundError ee) {
				app.setDefaultCursor();
				app.showErrorDialog(app.getError("ExportJarMissing"));
				ee.printStackTrace();
				}
				finally {
					app.setDefaultCursor();
				}
			}
		};

		drawingPadPropAction = new AbstractAction(app.getPlain("DrawingPad")
				+ " ...", app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.getGuiManager().showDrawingPadPropertiesDialog();
				app.setDefaultCursor();
			}
		};

		toolbarConfigAction = new AbstractAction(app.getMenu("Toolbar.Customize")
				+ " ...", app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.getGuiManager().showToolbarConfigDialog();
				app.setDefaultCursor();
			}
		};

		saveAction = new AbstractAction(app.getMenu("Save"),
				app.getImageIcon("document-save.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {	
				app.setWaitCursor();
				app.getGuiManager().save();
				app.setDefaultCursor();
			}
		};

		saveAsAction = new AbstractAction(app.getMenu("SaveAs") + " ...",
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.getGuiManager().saveAs();
				app.setDefaultCursor();
			}
		};

		/*
		printProtocolAction = new AbstractAction(
				app.getPlain("ConstructionProtocol") + " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						ConstructionProtocol constProtocol = app.getConstructionProtocol();
						if (constProtocol == null) {
							constProtocol = new ConstructionProtocol(app);
						}
						constProtocol.initProtocol();
						
						try {
							new PrintPreview(app, constProtocol, PageFormat.PORTRAIT);
                    	} catch (Exception e) {
                    		Application.debug("Print preview not available");
                    	}
					}
				};
				runner.start();
			}
		};
*/
		
		printEuclidianViewAction = new AbstractAction(app.getPlain("DrawingPad")
				+ " ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				showPrintPreview(app);
			}
		};

		exitAction = new AbstractAction(app.getMenu("Close"), app.getImageIcon("exit.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.exit();
				app.setDefaultCursor();
			}
		};

		exitAllAction = new AbstractAction(app.getMenu("CloseAll"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.exitAll();
				app.setDefaultCursor();
			}
		};

		loadAction = new AbstractAction(app.getMenu("Load") + " ...",
				app.getImageIcon("document-open.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {				
				app.getGuiManager().openFile();
			}
		};

		

		refreshAction = new AbstractAction(app.getMenu("Refresh"),new ImageIcon(app.getRefreshViewImage())) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {				
				app.setWaitCursor();
				app.refreshViews();
				app.setDefaultCursor();
			}
		};

		recomputeAllViews = new AbstractAction(app.getMenu("RecomputeAllViews"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				app.getKernel().updateConstruction();
				app.setDefaultCursor();
			}
		};

		drawingPadToClipboardAction = new AbstractAction(
				app.getMenu("DrawingPadToClipboard"),
				app.getImageIcon("edit-copy.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.setWaitCursor();
				app.copyGraphicsViewToClipboard();	
				app.setDefaultCursor();
			}
		};

		/*
		// Michael Borcherds 2008-04-09
		DataFromClipboardAction = new AbstractAction(
				app.getMenu("PasteDataFromClipboard"),
				app.getImageIcon("edit-copy.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
            	          	 				
				Thread runner = new Thread() {
					public void run() {		
						app.setWaitCursor();
						
						String str=app.getStringFromClipboard();
						String lf = "\n"; // doesn't work: System.getProperty("line.separator");
						
						// split input at linefeed
						String strs[] = str.split(lf);
						
						// Remove Whitespace
						strs[0]=strs[0].trim();
						while (strs[0].indexOf("  ")>-1) strs[0]=strs[0].replaceAll("  ", " ");
						
						String sep = null, value;
						
						// check if data is comma tab, space, semicolon or comma (CSV) separated
						if      (strs[0].split("\t").length > 1) sep="\t"; // tab separated
						else if (strs[0].split(" ").length > 1) sep=" "; // space separated
						else if (strs[0].split(";").length > 1) sep=";"; // semicolon separated
						else if (strs[0].split(",").length > 1) sep=","; // comma separated
						
						//Application.debug("sep"+sep);
						
						String list = "{";
						
						if (sep != null) for (int row=0 ; row<strs.length ; row++)
						{
							//Application.debug("XX"+strs[i]+"XX");
							
							// Remove Whitespace
							strs[row]=strs[row].trim();
							while (strs[row].indexOf("  ")>-1) strs[row]=strs[row].replaceAll("  ", " ");
							
							
							if (!strs[row].equals(""))
							{
								String tempStr[] = strs[row].split(sep);
								
								String coords="(";
								for (int col=0 ; col < tempStr.length ; col++)
								{
									value = tempStr[col].trim();
									if (value.equals("")) value = "0";		
									
									value = value.replace(',', '.'); // decimal comma -> decimal point
									
									try { Double.parseDouble(value); } // check if input is a valid number
									catch (Exception e) { value = "0"; } // set to zero
									
									String command = GeoElement.getSpreadsheetCellName(col,row) + "=" + value;
									kernel.getAlgebraProcessor().processAlgebraCommand(command, false);
									coords += GeoElement.getSpreadsheetCellName(col,row);
									if (col == tempStr.length-1) coords +=")"; else coords +=",";
									//Application.debug("XX"+list+"XX");
								}
								if (tempStr.length == 2) // 2D coords
								{
									//kernel.getAlgebraProcessor().processAlgebraCommand(coords, false);
									list = list + coords + ",";
								}
									
							}
						}
						
						if (list.lastIndexOf(",") == list.length()-1) // list contains at least one pair of coords
						{
							list = list.substring(0,list.length()-1); 	// remove last comma
							list = list + "}"; 							// close list
							
							kernel.getAlgebraProcessor().processAlgebraCommand(list, false);							
						}
				
					app.setDefaultCursor();
					}
				};
				runner.start();						    			    								
			}
		};*/

		helpAction = new AbstractAction(app.getMenu("Help"),
				app.getImageIcon("help.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						app.setWaitCursor();
						app.getGuiManager().openHelp(null);
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};

		/*
		 * updateAction = new AbstractAction(getMenu("Update"), getEmptyIcon()) {
		 * private static final long serialVersionUID = 1L; public void
		 * actionPerformed(ActionEvent e) { Thread runner = new Thread() {
		 * public void run() { updateGeoGebra(); } }; runner.start(); } };
		 */

		exportGraphicAction = new AbstractAction(app.getPlain("DrawingPadAsPicture")
				+ " ("	+ Application.FILE_EXT_PNG + ", " + Application.FILE_EXT_EPS + ") ...", 
				app.getImageIcon("image-x-generic.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				
				
				try {
					Thread runner = new Thread() {
						public void run() {
							app.setWaitCursor();
							try {							
						    	app.clearSelectedGeos();
						    	
						    	// use reflection for					    	
					  		    JDialog d = new geogebra.export.GraphicExportDialog(app);   		
					  		    //Class casViewClass = Class.forName("geogebra.export.GraphicExportDialog");
					  		    //Object[] args = new Object[] { app };
					  		    //Class [] types = new Class[] {Application.class};
					  	        //Constructor constructor = casViewClass.getDeclaredConstructor(types);   	        
					  	        //JDialog d =  (JDialog) constructor.newInstance(args);  					    
						      
						        d.setVisible(true);
						       
							} catch (Exception e) {
								Application.debug("GraphicExportDialog not available");
							}
							app.setDefaultCursor();
						}
					};
					runner.start();
				}
				
				catch (java.lang.NoClassDefFoundError ee) {
					app.setDefaultCursor();
				app.showErrorDialog(app.getError("ExportJarMissing"));
				ee.printStackTrace();
				}
					
			}
		};
		
		
		exportPSTricksAction = new AbstractAction(app.getPlain("DrawingPadAsPSTricks") + " ...", 
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {				
				try {		
					app.setWaitCursor();
					
		  	        new geogebra.export.pstricks.GeoGebraToPstricks(app);	
		  	        app.setDefaultCursor();
				} catch (Exception ex) {
					 app.setDefaultCursor();
					Application.debug("GeoGebraToPstricks not available");
				}	
				catch (java.lang.NoClassDefFoundError ee) {
					 app.setDefaultCursor();
					app.showErrorDialog(app.getError("ExportJarMissing"));
					ee.printStackTrace();
				}
			}
		};
		// Added By Loïc Le Coq
		exportPgfAction = new AbstractAction(app.getPlain("DrawingPagAsPGF") + " ...", 
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {				
				try {	
					app.setWaitCursor();
					
		  	        new geogebra.export.pstricks.GeoGebraToPgf(app);	
		  	      app.setDefaultCursor();
				} catch (Exception ex) {
					 app.setDefaultCursor();
					Application.debug("GeoGebraToPGF not available");
				}	
				catch (java.lang.NoClassDefFoundError ee) {
					 app.setDefaultCursor();
					app.showErrorDialog(app.getError("ExportJarMissing"));
					ee.printStackTrace();
				}
			}
		};			
		
		//End
		
		exportWorksheet = new AbstractAction(app.getPlain("DynamicWorksheetAsWebpage") + " ("
				+ Application.FILE_EXT_HTML + ") ...",
				app.getImageIcon("text-html.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				
				
				try {
				
					Thread runner = new Thread() {
						public void run() {
							app.setWaitCursor();
							try {
								app.clearSelectedGeos();
								geogebra.export.WorksheetExportDialog d = new geogebra.export.WorksheetExportDialog(app); 		
															
								d.setVisible(true);
							} catch (Exception e) {
								Application.debug("WorksheetExportDialog not available");
								e.printStackTrace();
							}
							app.setDefaultCursor();
						}
					};
					runner.start();
				}
				
				catch (java.lang.NoClassDefFoundError ee) {
					 app.setDefaultCursor();
				app.showErrorDialog(app.getError("ExportJarMissing"));
				ee.printStackTrace();
				}
			}
		};

		showCreateToolsAction = new AbstractAction(app.getMenu("Tool.CreateNew")
				+ " ...", app.getImageIcon("tool.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				 app.setWaitCursor();
				ToolCreationDialog tcd = new ToolCreationDialog(
						app);
				tcd.setVisible(true);
				app.setDefaultCursor();
			}
		};

		showManageToolsAction = new AbstractAction(app.getMenu("Tool.Manage")
				+ " ...", app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				ToolManagerDialog tmd = new ToolManagerDialog(app);
				tmd.setVisible(true);
				app.setDefaultCursor();
			}
		};

		infoAction = new AbstractAction(app.getMenu("About") + " / "
				+ app.getMenu("License"), app.getImageIcon("info.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				showAboutDialog(app);
				app.setDefaultCursor();
			}
		};			
		
		savePreferencesAction = new AbstractAction(app.getMenu("Settings.Save"),
				app.getImageIcon("document-save.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				GeoGebraPreferences.getPref().saveXMLPreferences(app);
				app.setDefaultCursor();
			}
		};
		
		clearPreferencesAction = new AbstractAction(app.getMenu("Settings.ResetDefault"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();
				GeoGebraPreferences.getPref().clearPreferences();
				app.setDefaultCursor();
			}
		};
		
		selectAllAction = new AbstractAction(app.getMenu("SelectAll"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {	
				app.setWaitCursor();
				app.selectAll(-1); // Michael Borcherds 2008-03-03 pass "-1" to select all
				app.setDefaultCursor();
			}
		};			
		
		selectCurrentLayerAction = new AbstractAction(app.getMenu("SelectCurrentLayer"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.setWaitCursor();
				int layer = getSelectedLayer();
				if (layer !=-1) app.selectAll(layer); // select all objects in layer
				app.setDefaultCursor();
			}
		};			
		
		selectAllAncestorsAction = new AbstractAction(app.getMenu("SelectAncestors"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.setWaitCursor();
				
				app.selectAllPredecessors();
				app.setDefaultCursor();
			}
		};			
		
		selectAllDescendantsAction = new AbstractAction(app.getMenu("SelectDescendants"),
				app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.setWaitCursor();
				app.selectAllDescendants();
				app.setDefaultCursor();
			}
		};			
		
		deleteAction = new AbstractAction(app.getPlain("Delete"), 
				app.getImageIcon("delete_small.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {	
				app.setWaitCursor();
				app.deleteSelectedObjects();
				app.setDefaultCursor();
			}
		};		
		
		websiteAction  = new AbstractAction("www.geogebra.org", 
							new ImageIcon(app.getInternalImage("geogebra.gif"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {			
				app.setWaitCursor();
				app.getGuiManager().showURLinBrowser(GeoGebra.GEOGEBRA_WEBSITE);
				app.setDefaultCursor();
			}
		};		
		
		forumAction  = new AbstractAction("GeoGebra Forum", 
				new ImageIcon(app.getInternalImage("users.png"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {	
				app.setWaitCursor();
				app.getGuiManager().showURLinBrowser(GeoGebra.GEOGEBRA_WEBSITE + "forum/");
				app.setDefaultCursor();
			}
		};	
		
		wikiAction  = new AbstractAction("GeoGebraWiki", 
				new ImageIcon(app.getInternalImage("wiki.png"))) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {	
				app.setWaitCursor();
				String url = GeoGebra.GEOGEBRA_WEBSITE;
				if (app.getLocale().getLanguage().equals("de"))
					url += "de/wiki/";
				else
					url += "en/wiki/";
					
				app.getGuiManager().showURLinBrowser(url);
				app.setDefaultCursor();
			}
		};	
				
		updateActions();
	}
	/* Michael Borcherds 2008-03-03
	 * return -1 if nothing selected
	 * return -2 if objects from more than one layer selected
	 * return layer number if objects from exactly one layer are selected
	 */	
	protected int getSelectedLayer() { 
		Object [] geos = app.getSelectedGeos().toArray();
		if (geos.length == 0) return -1; // return -1 if nothing selected
		
		int layer = ((GeoElement)geos[0]).getLayer();
		
		for (int i=1; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (geo.getLayer() != layer) return -2; // return -2 if more than one layer selected
		}
		return layer;
	}
	
	public static void showPrintPreview(final Application  app) {
		
		
		Thread runner = new Thread() {
			public void run() {			
				app.setWaitCursor();
				
				try {
					// use reflection for
		  		    // new geogebra.export.PrintPreview(app, app.getEuclidianView(), PageFormat.LANDSCAPE);		
		  		    //Class classObject = Class.forName("geogebra.export.PrintPreview");
		  		    //Object[] args = new Object[] { app , app.getEuclidianView(), new Integer(PageFormat.LANDSCAPE)};
		  		    //Class [] types = new Class[] {Application.class, Printable.class, int.class};
		  	        //Constructor constructor = classObject.getDeclaredConstructor(types);   	        
		  	        //constructor.newInstance(args); 				
		  		    new geogebra.export.PrintPreview(app, app.getEuclidianView(), PageFormat.LANDSCAPE);							
            	} catch (Exception e) {
            		Application.debug("Print preview not available");            		
            	}	
            	
            	app.setDefaultCursor();
			}
		};
		runner.start();	
	}
	
	public static void showAboutDialog(final Application app) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html><b>");
		sb.append(app.getPlain("ApplicationName"));
		sb.append(" ");
		sb.append(GeoGebra.VERSION_STRING);
		sb.append("</b>  (");
		sb.append("Java "+ System.getProperty("java.version") + ", " +(app.getHeapSize()/1024/1024)+"MB"); 
		sb.append(")<br>");	
		sb.append(GeoGebra.BUILD_DATE);
	
		// load license
		String text = app.loadTextFile(Application.LICENSE_FILE);
		
		JTextArea textArea = new JTextArea(21, 45);
		JScrollPane scrollPane = new JScrollPane(textArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textArea.setEditable(false);
		textArea.setText(text);
		textArea.setCaretPosition(0);

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(new JLabel(sb.toString()), BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.SOUTH);

		JOptionPane infoPane = new JOptionPane(panel,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);

		final JDialog dialog = infoPane.createDialog(app.getMainComponent(),
				app.getMenu("About") + " / " + app.getMenu("License"));

//		final ActionListener listener = new ActionListener() {
//			public final void actionPerformed(final ActionEvent e) {
//				JOptionPane.showMessageDialog(dialog, null,
//						"GeoZebra forever", JOptionPane.DEFAULT_OPTION,
//						app.getImageIcon("zebra.gif"));
//			}
//		};
//
//		final KeyStroke keyStroke = KeyStroke.getKeyStroke(
//				KeyEvent.VK_Z, 0, true);
//		dialog.getRootPane().registerKeyboardAction(listener,
//				keyStroke,
//				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		dialog.setVisible(true);
	}
	
	public void updateSelection() {		
		// Michael Borcherds 2008-03-03 BEGIN
		//boolean haveSelection = !app.getSelectedGeos().isEmpty();
		//deleteAction.setEnabled(haveSelection);
		int layer = getSelectedLayer();
		deleteAction.setEnabled(layer != -1); // -1 means nothing selected, -2 means different layers selected
		selectCurrentLayerAction.setEnabled(getSelectedLayer() >= 0); // exactly one layer selected
		// Michael Borcherds 2008-03-03 END
		boolean haveSelection = !app.getSelectedGeos().isEmpty();
		selectAllDescendantsAction.setEnabled(haveSelection);
		selectAllAncestorsAction.setEnabled(haveSelection);
	}

	public void updateActions() {	
		if (propertiesAction != null)
			propertiesAction.setEnabled(!kernel.isEmpty());			
		
		if (selectAllAction != null)
			selectAllAction.setEnabled(!kernel.isEmpty());
		
		updateMenuDecimalPlaces();
	}

	protected void setMenuShortCutAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		mi.setAccelerator(ks);
	}
	
	protected void setMenuShortCutShiftAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, 
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() + Event.SHIFT_MASK);
		mi.setAccelerator(ks);
	}
	
	
	public void updateMenuWindow() {	
		if (menuWindow == null) return;
		
		menuWindow.removeAll();
		JMenuItem mit = menuWindow.add(newWindowAction);
		setMenuShortCutAccelerator(mit, 'N');

		ArrayList ggbInstances = GeoGebraFrame.getInstances();
		int size = ggbInstances.size();
		if (size == 1)
			return;

		menuWindow.addSeparator();
		StringBuffer sb = new StringBuffer();
		ButtonGroup bg = new ButtonGroup();
		JRadioButtonMenuItem mi;
		for (int i = 0; i < size; i++) {
			GeoGebraFrame ggb = (GeoGebraFrame) ggbInstances.get(i);
			Application application = ggb.getApplication();

			sb.setLength(0);
			sb.append(i + 1);
			if (application != null) // Michael Borcherds 2008-03-03 bugfix
			if (application.getCurrentFile() != null) {
				sb.append(" ");
				sb.append(application.getCurrentFile().getName());
			}

			mi = new JRadioButtonMenuItem(sb.toString());
			if (application == this.app)
				mi.setSelected(true);
			ActionListener al = new RequestFocusListener(ggb);
			mi.addActionListener(al);
			bg.add(mi);
			menuWindow.add(mi);
		}
	}

	protected void updateMenuAngleUnit() {
		int pos;
		if (kernel.getAngleUnit() == Kernel.ANGLE_DEGREE)
			pos = 0;
		else
			pos = 1;
		if (menuAngleUnit != null) {
		((JRadioButtonMenuItem) menuAngleUnit.getMenuComponent(pos))
				.setSelected(true);
		}
	}

	protected void updateMenuPointStyle() {
		if (menuPointStyle == null) return;
		
		int pos = app.getEuclidianView().getPointStyle();
		if (pos == 1) pos=2; else if (pos == 2) pos=1; // bugfix swap 2 and 1 Michael Borcherds 2008-03-13
		if (pos > 5) pos--; // needed as hollow diamond not in menu (yet)
		((JRadioButtonMenuItem) menuPointStyle.getMenuComponent(pos))
				.setSelected(true);
	}

	protected void updateMenuBooleanSize() {
		if (menuBooleanSize == null) return;
		
		int size = app.getEuclidianView().getBooleanSize();
		int pos = (size == 13) ? 0 : 1; // only 13 and 26 allowed
		((JRadioButtonMenuItem) menuBooleanSize.getMenuComponent(pos))
				.setSelected(true);
	}

	// added by Lo�c BEGIN
	protected void updateMenuRightAngleStyle() {
		if (menuRightAngleStyle == null) return;
		
		int pos = app.getEuclidianView().getRightAngleStyle();
		((JRadioButtonMenuItem) menuRightAngleStyle.getMenuComponent(pos))
				.setSelected(true);
	}

	// END

	protected void updateMenuCoordStyle() {
		if (menuCoordStyle == null) return;
		
		int pos = kernel.getCoordStyle();
		((JRadioButtonMenuItem) menuCoordStyle.getMenuComponent(pos))
				.setSelected(true);
	}
	
	protected void updateMenuLabeling() {
		if (menuLabeling == null) return;
		
		int pos = app.getLabelingStyle();
		((JRadioButtonMenuItem) menuLabeling.getMenuComponent(pos))
				.setSelected(true);
	}
	
	protected void updateMenuPointCapturing() {
		if (menuPointCapturing == null) return;
		
		String pos = Integer.toString(app.getEuclidianView().getPointCapturingMode());
		for (int i = 0; i < 4; i++) {
			JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menuPointCapturing
					.getMenuComponent(i);
			String ac = mi.getActionCommand();
			if (ac.substring(0, 1).equals(pos)) {
				mi.setSelected(true);
				break;
			}
		}
	}

	public void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null) return;
		int pos = -1;
		
		if (kernel.useSignificantFigures) {
			int figures = kernel.getPrintFigures();
			if (figures > 0 && figures < Application.figuresLookup.length)
				pos = Application.figuresLookup[figures];
		}
		else
		{
			int decimals = kernel.getPrintDecimals();
			
			if (decimals > 0 && decimals < Application.decimalsLookup.length)
				pos = Application.decimalsLookup[decimals];
			
		}

		try {
			((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
		}

	}

	protected void updateMenuContinuity() {
		int pos = kernel.isContinuous() ? 0 : 1;
		try {
			((JRadioButtonMenuItem) menuContinuity.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
		}
	}

	//G.Sturr 2009-10-18 
	private void updateMenuViewDescription() {
		if (menuAlgebraStyle != null) {
			((JRadioButtonMenuItem) menuAlgebraStyle.getMenuComponent(kernel.getAlgebraStyle()))
					.setSelected(true);
		}
	}
	//END G.Sturr
	
	
	protected void addLanguageMenuItems(JMenu menu, ActionListener al) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;
		String ggbLangCode;

		JMenu submenu1 = new JMenu("A - E");
		JMenu submenu2 = new JMenu("F - L");
		JMenu submenu3 = new JMenu("M - Z");
		menu.add(submenu1);
		menu.add(submenu2);
		menu.add(submenu3);
		
		for (int i = 0; i < Application.supportedLocales.size(); i++) {						
			Locale loc = (Locale)  Application.supportedLocales.get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();

			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			String text = (String)  Application.specialLanguageNames.get(ggbLangCode);
			if (text == null)
				text = loc.getDisplayLanguage(Locale.ENGLISH);
			mi = new JRadioButtonMenuItem(text);

			if (loc == app.getLocale())
				mi.setSelected(true);
			mi.setActionCommand(ggbLangCode);
			mi.addActionListener(al);
			bg.add(mi);
			
			char ch = text.charAt(0);
			if (ch <= 'E')
				submenu1.add(mi);
			else if (ch <= 'L')
				submenu2.add(mi);
			else
				submenu3.add(mi);
		}
	}

	protected void addRadioButtonMenuItems(JMenu menu, ActionListener al,
			String[] items, String[] actionCommands, int selectedPos) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				menu.addSeparator();
			}
			else
			{
				String text = app.getMenu(items[i]);
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(app.getFontCanDisplay(text));
				if (i == selectedPos)
					mi.setSelected(true);
				mi.setActionCommand(actionCommands[i]);
				mi.addActionListener(al);
				bg.add(mi);
				menu.add(mi);
			}
		}
	}
	
	 // handle language changes
    protected class LanguageActionListener implements ActionListener {
    	
    	public LanguageActionListener() {}
    	
        public void actionPerformed(ActionEvent e) {
        	app.setLanguage(Application.getLocale(e.getActionCommand())); 
        	
        	// needed in case eg e^x changes to exp(x) with font change
        	app.getKernel().updateConstruction();

        	if (app.hasFullPermissions()) {
        		GeoGebraPreferences.getPref().saveDefaultLocale(app.getLocale());
        	}
        }
    }
    
    
  
}

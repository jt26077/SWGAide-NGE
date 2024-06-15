package swg.gui.resources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AbstractDocument;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import com.jidesoft.swing.StyledLabel;
import com.jidesoft.swing.StyledLabelBuilder;

import swg.SWGAide;
import swg.SWGConstants;
import swg.crafting.SWGValues;
import swg.crafting.SWGWeights;
import swg.crafting.Stat;
import swg.crafting.resources.SWGKnownResource;
import swg.crafting.resources.SWGMutableResource;
import swg.crafting.resources.SWGResourceClass;
import swg.crafting.resources.SWGResourceFilter;
import swg.crafting.resources.SWGResourceStats;
import swg.crafting.resources.SWGWeightComparator;
import swg.crafting.resources.types.SWGCreatureResources;
import swg.gui.SWGFrame;
import swg.gui.common.SWGDecoratedTableCellRenderer.DecoratedTableModel;
import swg.gui.common.SWGDecoratedTableCellRenderer.TableCellDecorations;
import swg.gui.common.SWGDocNumberFilter;
import swg.gui.common.SWGGuiUtils;
import swg.gui.common.SWGHelp;
import swg.gui.common.SWGJTable;
import swg.gui.common.SWGResourceStatRenderer;
import swg.gui.common.SWGTableCellEditor;
import swg.gui.schematics.SWGSchemController;
import swg.gui.schematics.SWGSchemResViewer;
import swg.gui.schematics.SWGSchematicTab;
import swg.model.SWGCGalaxy;
import swg.model.SWGCharacter;
import swg.model.SWGNotes;
import swg.model.SWGStation;
import swg.swgcraft.SWGResourceManager;
import swg.swgcraft.SWGResourceTuple;
import swg.swgcraft.SWGSoapListResResponse;
import swg.tools.ZCSV;
import swg.tools.ZHtml;
import swg.tools.ZNumber;
import swg.tools.ZReader;
import swg.tools.ZString;

/**
 * The GUI component for individual resource inventories. The main section of
 * this component is the table which displays the inventory entries for the
 * current galaxy. Bottom-most is a set of filtering features.
 * <p>
 * A couple of notes on some of the columns at the main table. "Assignee" may be
 * the name of an in-game character but it may also be defined by the user,
 * unique per galaxy though. Resource name for resources which come from foreign
 * galaxies are prefixed with the galaxy name in parentheses, e.g. (Bria)Foobar.
 * The "Rate" column is used only when values are entered at the filter input
 * fields. "Notes" is whatever text, including multi-line comments.
 * 
 * @author <a href="mailto:simongronlund@gmail.com">Simon Gronlund</a> aka
 *         Chimaera.Zimoon
 */
@SuppressWarnings("serial")
public final class SWGInventoryTab extends JPanel {

    // TODO: add plain text filtering for notes column

    /**
     * The GUI combo box for selecting an assignee; notice that some methods in
     * this class use an in-game character for assignee, these methods are
     * called from locations where only in-game characters are known, such as
     * the harvesting table.
     */
    private JComboBox<String> assigneeCombo;

    /**
     * The bottom-most panel for this GUI component.
     */
    private Box bottomPanel;
    
    /**
     * The top most panel containing primary control buttons.
     */
    private static JMenuBar cPanel;

    /**
     * A resource filter which can be a plain filter or a weighted filter. If
     * there is nothing entered at the stats filter input fields this object is
     * {@code null}.
     */
    private SWGValues filter;

    /**
     * An array with text fields for stats. The elements are ordered by
     * SWGAide's model, while the GUI presents them in in-game order.
     */
    private final JTextField[] filterFields =
            new JTextField[Stat.COUNT];

    /**
     * The frame for SWGAide.
     */
    private final SWGFrame frame;

    /**
     * The URL for the help page for this panel.
     */
    private final URL helpPage;

    /**
     * A counter for displaying some information for the user while importing
     * from file, to make the time fly :)
     */
    private int importCounter;

    /**
     * The number of lines to import, used together with {@link #importCounter}.
     */
    private int importLines;

    /**
     * A flag which denotes if the GUI is ready created. This field is {@code
     * false} until the GUI is ready and is used to block action events.
     */
    private boolean isGuiCreated = false;

    /**
     * A flag which denotes if GUI events should be ignored until later. This
     * flag is {@code true} if there is a process importing guards, or if the
     * GUI is resetting.
     */
    private boolean isWorking = false;

    /**
     * Helper variable that denotes which galaxy is the most recently seen. If
     * the user switches to another galaxy this member is different than
     * {@link SWGResourceTab#galaxy()}.
     */
    private SWGCGalaxy recentGalaxy;

    /**
     * The resource class which is selected by the user to filter the view from
     * and which is the base for coloring the stats.
     */
    private SWGResourceClass resourceClass;

    /**
     * The GUI component which is used to filter on resource class.
     */
    private JComboBox<String> resourceClassCombo;

    /**
     * The GUI component which contains this object.
     */
    private final SWGResourceTab resourceTab;

    /**
     * The main GUI table for the inventory.
     */
    private SWGJTable table;

    /**
     * The table model for the main GUI table.
     */
    private TableModel tableModel;

    /**
     * A flag that denotes if this panel may update the schematic-resource
     * viewer continuously. Default is {@code false}.
     */
    private boolean updateViewer;

    /**
     * The most recently selected list of wrappers, or {@code null} if is reset.
     */
    private volatile List<SWGInventoryWrapper> wrappers;
    
    /**
     * Label for the total value
     */
	private StyledLabel totVal;

    /**
     * A flag to use throughout this panel to account for JTL resource
     * capping adjustments
     */
	private boolean useJTLcap;

    /**
     * Creates an instance of this GUI component.
     * 
     * @param resourceTab the GUI component which contains this instance
     */
    SWGInventoryTab(SWGResourceTab resourceTab) {
        this.resourceTab = resourceTab;
        this.frame = SWGAide.frame();

        useJTLcap = ((Boolean) SWGFrame.getPrefsKeeper().get(
                "optionUseJTLcaps", Boolean.FALSE)).booleanValue();
        helpPage = SWGAide.class.getResource(
                "docs/help_resources_inventory_en.html");

        // create GUI content lazily, see focusGained()

        resourceTab.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                focusGained(true);
            }
        });
    }

    /**
     * Called when the user selects to copy all of the current inventory to
     * another galaxy.
     */
    private void actionCopyAll() {
        List<SWGCGalaxy> gl = SWGAide.frame().getMainTab().galaxies();
        gl.remove(recentGalaxy);

        if (gl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No other galaxy exists",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SWGCGalaxy g = (SWGCGalaxy) JOptionPane.showInputDialog(this,
                "Select a galaxy to copy to", "Select galaxy",
                JOptionPane.PLAIN_MESSAGE, null, gl.toArray(), gl.get(0));
        if (g == null) return;

        List<SWGInventoryWrapper> wsl, wtl; // source and target lists
        wsl = SWGResController.inventory(recentGalaxy);
        wtl = new ArrayList<SWGInventoryWrapper>(wsl.size());
        for (SWGInventoryWrapper ws : wsl)
            wtl.add(ws.copy(recentGalaxy.getName()));

        SWGResController.inventoryAdd(wtl, g);
    }

    /**
     * Called when the user selects to remove all visible entries. In
     * particular, if the view is filtered only the filtered view is removed.
     */
    private void actionDeleteEntries() {
        if (!isWorking
                && JOptionPane.OK_OPTION == showConfirmWarning(
                        "Delete inventory entries.\nOnly the current view is "
                                + "deleted. Continue?", "Confirm")) {

            List<SWGInventoryWrapper> wList = wrappersFiltered();
            isWorking = true;
            for (SWGInventoryWrapper wr : wList)
                SWGResController.inventoryRemove(wr, recentGalaxy);

            isWorking = false;
            totVal.setText(" ");
            actionResetBottomPanel();
        }
    }

    /**
     * Called when the user selects to delete the selected entry from the
     * inventory.
     * 
     * @param row the row for the selected entry, already converted to model
     */
    private void actionDeleteEntry(int row) {
        if (isWorking)
            return;

        SWGInventoryWrapper wr = wrappersFiltered().get(row);

        if (JOptionPane.OK_OPTION == showConfirmQuestion(String.format(
                "Delete the entry for \"%s\"?", wr.getResource().getName()),
                "Confirm deletion")) {
            SWGResController.inventoryRemove(wr, recentGalaxy);
            updateDisplay();
            actionTotalValue();
        }
    }

    /**
     * Called when the user selects to merge the specified duplicate entries to
     * the specified assignee. This method removes duplicates from their current
     * owners and adds them to the specified assignee, if the assignee already
     * owns an entry its amount is updated. Finally this method triggers an
     * update of the GUI.
     * 
     * @param ass an inventory assignee
     * @param dl a list of duplicates
     */
    private void actionDuplicatesMerge(String ass, List<SWGInventoryWrapper> dl) {
        isWorking = true;
        // two turns
        // first remove non-targets and rename them, but leave target
        for (SWGInventoryWrapper w : dl) {
            if (!w.getAssignee().equals(ass)) {
                SWGResController.inventoryRemove(w, recentGalaxy);
                w.equalAddSub = "+";
            }
        }
        // second turn, add all non-targets to target
        for (SWGInventoryWrapper w : dl) {
            if (!w.getAssignee().equals(ass)) {
                w.setAssignee(ass);
                SWGResController.inventoryAdd(w, recentGalaxy);
            }
            w.equalAddSub = null;
        }

        isWorking = false;
        updateDisplay();
    }

    /**
     * Called when the user selects to write the current display to notes file.
     * This method invokes helper methods which presents the user with existing
     * notes files for the account of the specified character and writes the
     * currently displayed inventory to the selected file.
     * 
     * @param toon the currently selected character
     */
    private void actionExportNotes(SWGCharacter toon) {
        SWGNotes notes = notesFile(toon);
        notesWrite(notes);
    }

    /**
     * Called when the user wants to export an inventory to file.
     * <p>
     * <b>Notice:</b> Only the current, possibly filtered, display is exported.
     */
    private void actionExportToFile() {
        if (isWorking)
            return;

        List<SWGInventoryWrapper> invList = wrappersFiltered();
        if (invList.isEmpty())
            return;

        JFileChooser fc = SWGFrame.getFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        File recentFile =
                (File) SWGFrame.getPrefsKeeper().get(
                        "resourceInventoryImportFile");
        if (recentFile != null && recentFile.exists()) {
            fc.setCurrentDirectory(recentFile.getParentFile());
            fc.setSelectedFile(recentFile);
        }

        if (JFileChooser.APPROVE_OPTION == fc.showSaveDialog(this)) {
            File file = fc.getSelectedFile();

            String msg =
                    String.format("Overwrite file?%n\"%s\"", file.getName());
            if (file.exists()
                    && JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(
                            this, msg, "File exists",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE))
                return;

            SWGFrame.getPrefsKeeper().add("resourceInventoryImportFile", file);

            fileWrite(invList, file);
        }
    }

    /**
     * Called when the user selects the option to filter the inventory at a
     * popup dialog. If this instance is not yet ready made this method invokes
     * {@link #makeGUI()} which is synchronized, then it filters the display
     * based on the specified arguments.
     * 
     * @param rc a resource class constant, or {@code null}
     * @param wg experimental weights, or {@code null}
     * @param kr a resource to selected, or {@code null}
     */
    void actionFilterInventory(final SWGResourceClass rc,
            final SWGWeights wg, final SWGKnownResource kr) {
        if (!isGuiCreated)
            makeGUI();
        recentGalaxy = SWGFrame.getSelectedGalaxy();
        filterValues(wg);
        rcSelect(rc, kr);
    }

    /**
     * Called when the user selects the option to create a guard for a selected
     * inventory entry denoted by the specified argument. This method displays
     * the common create/edit guard dialog and presets values based on the
     * selected inventory wrapper. The argument must be valid.
     * 
     * @param row a selected row
     */
    private void actionGuardCreate(int row) {
        SWGInventoryWrapper wr = wrappersFiltered().get(row);
        SWGKnownResource kr = wr.getResource();

        SWGGuard g = new SWGGuard("Plain",
                kr.rc(), kr.stats(), 0, true, true);
        g.notes("Based on " + kr.getName() + ", " + kr.rc().rcName()
                + "\n" + wr.getNotes());

        SWGResController.guardsDialog().showAndBegin(
                null, g, recentGalaxy);
    }

    /**
     * Called when the user selects to import inventory from file. This
     * implementation figures out which file format the file has by parsing the
     * file header. If there is no file header or if it cannot be parsed the
     * user is informed and nothing is done. This implementation identifies the
     * parsed resources in this order, each entry by itself:
     * <ol>
     * <li>if ID is valid the resource data from swgaide.com is used, parsed
     * resource data is discarded, just assignee specific data is used</li>
     * <li>else if name/galaxy is known at swgaide.com, see previous point</li>
     * <li>else the parsed data is used as is</li>
     * </ol>
     * <p>
     * With the update made to resources in the autumn of 2009 an enhanced file
     * format is added. Also see the previous paragraphs. This is the documented
     * syntax (unbroken lines):
     * <p>
     * <tt>[assignee],galaxy|?,resourceName,ID,resource 
     * class,classToken,amount[,ER,CR,CD,DR,FL,HR,MA,PE,OQ,SR,UT[,notes]]</tt>
     * <dl>
     * <dt>assignee</dt>
     * <dd>(optional) any string without comma signs (the delimiting comma sign
     * is mandatory) &mdash; if there is no string the current user is assumed</dd>
     * <dt>galaxy</dt>
     * <dd>the galaxy name &mdash; if the ID is invalid and name/galaxy is
     * unknown at SWGCraft this method assumes nothing but informs the user</dd>
     * <dt>resourceName</dt>
     * <dd>a valid resource name</dd>
     * <dt>resourceClass / classToken</dt>
     * <dd>one of them must be valid</dd>
     * <dt>amount</dt>
     * <dd>the number of units owned of the resource</dd>
     * <dt>stats</dt>
     * <dd>(optional, none or all) the stats for the resource in the order read
     * in the file header</dd>
     * <dt>notes</dt>
     * <dd>(optional) any text, if notes are present they must be preceded by a
     * comma sign</dd>
     * </dl>
     * <b>Notice:</b> Any order of the columns is supported, in fact, just a
     * valid ID would suffice for the most meager entry. The method which reads
     * the file has no notion of "optional" columns, they are just present or
     * not.
     */
    private void actionImportFile() {
        if (isWorking)
            return;

        JFileChooser fc = SWGFrame.getFileChooser();
        File recentFile = (File)
                SWGFrame.getPrefsKeeper().get("resourceInventoryImportFile");
        if (recentFile != null && recentFile.exists()) {
            fc.setCurrentDirectory(recentFile.getParentFile());
            fc.setSelectedFile(recentFile);
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(this)) {
            File file = fc.getSelectedFile();
            if (!file.exists()) {
                JOptionPane.showMessageDialog(this, "No such file:\n"
                        + file.getAbsolutePath(), "File error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            SWGFrame.getPrefsKeeper().add("resourceInventoryImportFile", file);
            fileRead(file);
        }
    }

    /**
     * Called when the user selects to import from notes file. The specified
     * character determines which station to find a notes files for. The notes
     * file the user finally selects must have the following format:
     * 
     * <pre>[(galaxyName)]resourceName,amount[,optional notes]</pre>
     * <dl>
     * <dt>galaxyName</dt>
     * <dd>optional but if it is present it must be embraced by parentheses</dd>
     * <dt>resourceName</dt>
     * <dd>if it is not recognized it will be looked up interactively</dd>
     * <dt>amount</dt>
     * <dd>the number of units of the resource</dd>
     * <dt>optional notes</dt>
     * <dd>optional misc notes</dd>
     * </dl>
     * <p>
     * Example: <TT>(Ahazi)Hansolo,123456789,for Smugglers Delight</TT>
     * 
     * @param toon the currently selected character, not {@code null}
     */
    private void actionImportNotes(SWGCharacter toon) {
        if (isWorking || toon == null)
            return;

        SWGNotes notes = notesFile(toon);
        if (notes != null)
            notesRead(notes);
    }

    /**
     * Called when the user wants to move all visible resources from the
     * specified assignee to another assignee. This method displays another
     * dialog at which the user selects a target assignee; then this method
     * removes each element from the specified assignee and adds it to the
     * selected target or updates its amount.
     * 
     * @param from the from-assignee
     */
    private void actionMove(String from) {
        String[] al = assigneesAndCharacters().toArray(new String[0]);
        String to = (String) JOptionPane.showInputDialog(this, String.format(
                "Move visible resources for \"%s\" to...\n" +
                        "Select wanted target assignee", from),
                "Select target", JOptionPane.QUESTION_MESSAGE,
                null, al, al[0]);
        if (to != null && !to.equals(from)) {
            List<SWGInventoryWrapper> wl = wrappersFiltered();
            for (SWGInventoryWrapper w : wl) {
                if (from.equals(w.getAssignee())) {
                    SWGResController.inventoryRemove(w, recentGalaxy);
                    w.setAssignee(to);
                    w.equalAddSub = "+";
                    SWGResController.inventoryAdd(w, recentGalaxy);
                    w.equalAddSub = null;
                }
            }
            updateDisplay();
        }
    }

    /**
     * Called when the user wants to reset the bottom panel, and also from some
     * action methods. This method clears any selection at any of the components
     * at the bottom panel and triggers the GUI to update itself.
     */
    private void actionResetBottomPanel() {
        if (isWorking || !isGuiCreated)
            return;

        resetBottomPanel();
        updateDisplay();
    }

    /**
     * Called when the user clicked the main table with the right-hand mouse
     * button. This method invokes to {@code showTablePopup(int, MouseEvent)}.
     * 
     * @param e the event which triggered the call
     */
    private void actionTableMouse(MouseEvent e) {
        if (!isWorking && e.getButton() == MouseEvent.BUTTON3) {
            int row = table.rowAtPoint(e.getPoint());
            if (row >= 0) {
                table.getSelectionModel().setSelectionInterval(row, row);
                row = table.convertRowIndexToModel(row);
            }
            tablePopup(row, e);
        }
    }
    
    /**
     * Called when cpu data has changed and sums the value column.
     */
    private void actionTotalValue () {
    	int sum = 0;
        int pcol=5;

        TableModel t = (TableModel) table.getModel();
        for (int i = 0; i < t.getRowCount(); i++) {
        	Object val = t.getValueAt(i, pcol);
        	if(val != null) {
        		sum = sum + Integer.parseInt(val.toString());
        	}
        }
        if (sum >0) {
        	totVal.setText(ZNumber.asText(sum));
        	StyledLabelBuilder.setStyledText(totVal, "{" + ZNumber.asText(sum) + ":bi}");
        	totVal.setVisible(true);
        } else {
        	totVal.setText(" ");
        }
        totVal.revalidate();
        totVal.repaint(300L);
    }

    /**
     * Called when the user selects an entry at the main table. Selection is
     * either a left-click or by keyboard navigation. If possible this method
     * updates the resource-schematic viewer.
     */
    private void actionTableSelect() {
        if (updateViewer) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                row = table.convertRowIndexToModel(row);
                updateViewer = SWGSchemResViewer.updateDisplay(
                        wrappersFiltered().get(row).getResource(), this, frame);
            }
        }
    }

    /**
     * Returns an assignee. This is the assignee selected at the GUI component.
     * If no assignee is selected this method returns the character currently
     * selected at the main panel of SWGAide.
     * 
     * @return an assignee
     */
    String assignee() {
        String assignee = (String) assigneeCombo.getSelectedItem();
        if (!assignee.equals("All"))
            return assignee;

        return SWGFrame.getSelectedCharacter().getName();
    }

    /**
     * Helper method which return a list of assignees for the current galaxy. If
     * no assignee exists at the current galaxy {@code null} is returned. The
     * returned list can freely be modified.
     * 
     * @return a list of assignees, or {@code null}
     */
    private List<String> assignees() {
        Map<String, List<SWGInventoryWrapper>> assignees =
                SWGResController.inventoryAssignees(
                        SWGResourceTab.galaxy(), false);

        return assignees != null
                ? new ArrayList<String>(assignees.keySet())
                : null;
    }

    /**
     * Helper method which returns a list of character names and assignees for
     * the current galaxy. The elements are the first names of all characters
     * and all assignees which have inventories at the current galaxy. The list
     * is alphabetically sorted.
     * 
     * @return a list of character names and assignees
     */
    List<String> assigneesAndCharacters() {
        List<String> assignees =
                SWGGuiUtils.characterNames(SWGResourceTab.galaxy());

        List<String> ags = assignees();
        if (ags != null)
            for (String s : ags)
            if (!assignees.contains(s))
                assignees.add(s);

        Comparator<String> comp = new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        };
        Collections.sort(assignees, comp);
        return assignees;
    }

    /**
     * Helper method that creates and returns a comparator for inventory
     * wrappers that sort on resource names.
     * 
     * @return a comparator
     */
    private Comparator<SWGInventoryWrapper> comparatorResourceNames() {
        return new Comparator<SWGInventoryWrapper>() {
            @Override
            public int compare(SWGInventoryWrapper o1, SWGInventoryWrapper o2) {
                return o1.getResource().getName().compareTo(
                        o2.getResource().getName());
            }
        };
    }

    /**
     * Helper method which returns the GUI dialog for adding/editing resources
     * to the inventory.
     * 
     * @return the GUI dialog
     */
    SWGInventoryDialog dialog() {
        return new SWGInventoryDialog(this);
    }

    /**
     * Helper method that returns a list of duplicate resource elements, or an
     * empty list. This method This method iterates over all inventory wrappers
     * for the current galaxy and returns a list all duplicates. If none is
     * found the empty list is returned.
     * 
     * @return a list of duplicates, may be empty
     */
    private List<SWGInventoryWrapper> duplicates() {
        List<SWGInventoryWrapper> wl =
                SWGResController.inventory(recentGalaxy);
        Collections.sort(wl, comparatorResourceNames());

        boolean first = true;
        List<SWGInventoryWrapper> dl = new ArrayList<SWGInventoryWrapper>();
        for (int i = 1; i < wl.size(); ++i) {
            SWGInventoryWrapper w0 = wl.get(i - 1);
            SWGInventoryWrapper w1 = wl.get(i);

            if (w0.getResource() == w1.getResource()) {
                if (first) dl.add(w0);
                dl.add(w1);
                first = false;
            } else
                first = true;
        }
        return dl;
    }

    private List<SWGInventoryWrapper> fileImportParse(CSVParser parser, String curAss, List<String> lAss,
			SWGCGalaxy curGxy) throws Throwable {
    	importCounter = 0;
        List<SWGInventoryWrapper> ret = new ArrayList<SWGInventoryWrapper>();
    	
    	SWGCGalaxy gxy;
    	for (CSVRecord record : parser) {
    		++importCounter;
            frame.putToStatbar(null,null);
            
    		// first see if we even have a proper galaxy and if it's correct one before proceeding.
    		String g = record.get("galaxy");
    		if (SWGCGalaxy.isNameValid(g)) {
    			gxy = SWGCGalaxy.fromName(g);
    		} else if ((g = SWGCGalaxy.properName(g)) != null) {
    			gxy = SWGCGalaxy.fromName(g);
    		} else {
    			showError("No Galaxy\n" +
    					"Review documentation on file syntax, press F1\n" +
    					"Aborting", "No Galaxy");
    			return null;
    		}
    		if(!gxy.equals(curGxy)) {
    			showError("Incorrect Galaxy\n" +
    					"You are trying to import resources from\n" +
    					gxy.getName() + " into " + curGxy.getName() + "\n" +
    					"Please select a character that lives in the galaxy you are trying to import.\n" +
    					"Aborting", "Incorrect Galaxy");
    			return null;
    		}
    		// next lets check if the assignee is valid and exists
    		String a = record.get("assignee").trim();
    		String ass = curAss;
    		if(lAss != null) {
    			for (int i = 0; i < lAss.size(); i++) {
    				String aa = lAss.get(i);
    				if(!a.isEmpty() && a.equalsIgnoreCase(aa) ) {
    					ass = a;
    				}
    			}
    		}
    		
    		// get resource class
            String cln = null; // class name
            String clt = null; // class token
            
            if (record.get("resourceClass") != null && !record.get("resourceClass").trim().isEmpty())
                cln = record.get("resourceClass").trim();
            if (record.get("classToken") != null && !record.get("classToken").trim().isEmpty())
                clt = record.get("classToken").trim(); // class token
            if (cln == null && clt == null) {
            	showError("No valid resource class\n" +
                        "Review documentation on file syntax, press F1\n" +
                        "Aborting", "Invalid resource class");
            	return null;
            }

            SWGResourceClass cls = null;
            SWGKnownResource res = null;
            if (cln != null && SWGResourceClass.rcID(cln) >= 0)
                cls = SWGResourceClass.rc(cln);
            if (cls == null && clt != null && SWGResourceClass.rcID(clt) >= 0)
                cls = SWGResourceClass.rc(clt);

            if (cls == null) {
            	showError("No valid resource class\n" +
                        "Review documentation on file syntax, press F1\n" +
                        "Aborting", "Invalid resource class");
            	return null;
            }
            String name = null;
            if (record.get("resourceName") != null && !record.get("resourceName").trim().isEmpty()) {
                name = ZString.tac(record.get("resourceName"));
                if (name.length() <3 ) {
                	showError("Invalid name detected: " + name + "\n" +
                            "Review documentation on file syntax, press F1\n" +
                            "Aborting", "Invalid Name");
                	return null;
                }
            }

            if (cls.isSpaceOrRecycled()) {
                res = cls.spaceOrRecycled();
                res.galaxy(gxy);
            } else {
            	// lookup by name
            	if(res==null && name != null && name.length() >= 3) {
            		frame.putToStatbar(String.format(
            				"%s(%s) - Lookup %s @ %s",
            				Integer.toString(importCounter),
            				Integer.toString(importLines),
            				name, gxy.getName()),null);
            		res = SWGResourceManager.getInstance(name, gxy);
            	}
            	//}
            }
            // if res is still null then we will assume it's a local resource
            if (res == null) {
                SWGMutableResource mr = new SWGMutableResource(name, cls);
                mr.galaxy(gxy);
                SWGResourceStats stats = new SWGResourceStats();
                for (Stat s : Stat.values()) {
                	stats.set(s, ZNumber.intVal(record.get(s.getName().toUpperCase())));
                }
                mr.stats(stats, true);
                res = SWGResourceManager.getInstance(mr);
            }
            
            //for sanity. if res is still null show error and exit loop.
            if (res == null) {
            	showError("Invalid Resource detected\n" +
                        "Review documentation on file syntax, press F1\n" +
                        "Aborting", "Invalid Resource");
            	return null;
            }
            // finally, we have a resource

            // Create an inventory wrapper from the info
            SWGInventoryWrapper wr = new SWGInventoryWrapper(res, ass);
            // get and set the amount
            long amt = Long.parseLong(record.get("amount"));
            if (amt >= 0) wr.setAmount(amt);
            // get and set CPU
            if(record.isMapped("CPU")) {
            	double cpu = Double.parseDouble(record.get("CPU"));
            	if(cpu > 0) {
            		wr.setCPU(cpu);
            	}
            }
            // get and set notes
            if (!record.get("notes").trim().isEmpty()) {
            	String notes = record.get("notes");
            	wr.setNotes(ZHtml.regainEOL(notes));
            }
            
            ret.add(wr);
    	}
		return ret;
	}

	/**
     * Helper method which imports inventory wrappers from the specified file,
     * this is the entry point. See {@link #actionImportFile()} for details on
     * the this implementation. The execution is performed on a background
     * worker.
     * 
     * @param file the file to read from
     */
    private void fileRead(final File file) {
    	try {
    		
    		ZReader tr = ZReader.newTextReaderExc(file);
    		StringBuilder cvList = new StringBuilder();
            String line;
            boolean header = false;
            boolean newheader = false;
            int cnt = 0;
            while ((line = tr.lineExc(false)) != null) {
                if (!line.isEmpty()) {
                    if ((line.startsWith("#") || line.startsWith("\"#"))) {
                        if (line.indexOf("resourceName") > 0
                                || line.indexOf("ID") > 0
                                || line.indexOf("resourceClass") > 0
                                || line.indexOf("classToken") > 0) {
                        	header = true;
                        	if(line.indexOf("CPU") > 0) {
                        		newheader = true;
                        	}
                        }
                    } else if (isJNF(line) > 0) {
                    	showError("No valid header line. JnF not supported.\n" +
                                "Review documentation on file syntax, press F1\n" +
                                "Aborting", "Invalid header");
                    	return;
                    } else {
                    	cvList.append(line).append("\n");
                    	++cnt;
                    }
                }
            }
            tr.close();
            importLines = cnt;
            if(!header) {
            	showError("No valid header line\n" +
                        "Review documentation on file syntax, press F1\n" +
                        "Aborting", "Invalid header");
            	return;
            }
            
            BufferedReader buffer = new BufferedReader(new StringReader(cvList.toString()));
            CSVParser parser;
            if(newheader) {
            	parser = CSVParser.parse(buffer, CSVFormat.EXCEL.withHeader("assignee", "galaxy", "resourceName", "ID", "resourceClass", "classToken", "amount", "CPU", "ER", "CR",
            			"CD", "DR", "FL", "HR", "MA", "PE", "OQ", "SR", "UT", "notes"));
            } else {
            	parser = CSVParser.parse(buffer, CSVFormat.EXCEL.withHeader("assignee", "galaxy", "resourceName", "ID", "resourceClass", "classToken", "amount", "ER", "CR",
            			"CD", "DR", "FL", "HR", "MA", "PE", "OQ", "SR", "UT", "notes"));
            }
			final String curAss = assignee();
			final List<String> lAss = assignees();
	        final SWGCGalaxy curGxy = recentGalaxy;
	        
	        // run the parser in a thread so GUI can be freed up
	        Runnable parse = () -> {
	        	try {
	        		List<SWGInventoryWrapper> wl = fileImportParse(parser, curAss, lAss, curGxy);
	        		if(wl !=null) {
	        			SWGResController.inventoryAdd(wl, curGxy);
	        		}
	        		importDone();
	        	} catch (Throwable e) {
	        		SWGAide.printError("SWGInventoryTab:csv import error: ", e);
	        	}
	        };
	        // start the thread
	        new Thread(parse).start();
		} catch (Exception e) {
			SWGAide.printError("SWGInventoryTab:csv import error: ", e);
		}

    	/*final int[] order = fileReadHeader(file);
        if (order == null)
            return; // there is an error, already communicated

        // run the remaining code in background process since it may involve
        // communication with SWGCraft

        importBegin();

        // snapshot of fields in the case the user flips to another server
        // while processing and communicating with swgaide.com
        final String assignee = assignee();
        final SWGCGalaxy currentGxy = recentGalaxy;
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            
            public void run() {
                List<SWGInventoryWrapper> wl = fileReadWorker(
                        file, order, assignee, currentGxy, isJNF);
                SWGResController.inventoryAdd(wl, currentGxy);
                importDone();
                executor.shutdown();
            }

            @Override
            public String toString() {
                return "SWGInventoryTab:fileRead";
            }
        });*/
    }

    /**
     * Helper method which exports the content of the specified wrappers to the
     * the selected file.
     * 
     * @param wl the list of wrappers to export
     * @param file the file to write to
     */
    private void fileWrite(List<SWGInventoryWrapper> wl, File file) {
        try {
        	CSVPrinter printer = new CSVPrinter(new FileWriter(file), CSVFormat.EXCEL.withQuote(null));
        	printer.printRecord("# SWGAide :: Inventory for All @ " + SWGResourceTab.galaxy().getName() + ", file format \"CSV\" (Comma Separated Values)");
        	printer.printRecord("# Galaxy/server name is always written to make transfers possible");
        	printer.printRecord("# Commas are IMPORTANT. Stats and notes are optional.");
        	printer.printRecord("# assignee", "galaxy", "resourceName", "ID", "resourceClass", "classToken", "amount", "CPU", "ER", "CR",
        			"CD", "DR", "FL", "HR", "MA", "PE", "OQ", "SR", "UT", "notes");
        	
        	for (int i = 0; i < wl.size(); i++) {
        		SWGInventoryWrapper w = wl.get(i);
        		int[] stats = w.getResource().stats().values();
        		printer.printRecord(w.getAssignee(), w.getResource().galaxy().getName(), w.getResource().getName(),
        				w.getResource().id(), w.getResource().rc().rcName(), w.getResource().rc().rcToken(),
        				w.getAmount(), w.getCPU(), stats[0], stats[1], stats[2], stats[3], stats[4], stats[5], stats[6], stats[7], stats[8],
        				stats[9], stats[10], ZHtml.replaceEOL(w.getNotes()) );
        	}
        	printer.close();

        } catch (Throwable e) {
            if (SWGConstants.DEV_DEBUG) e.printStackTrace();
            SWGAide.printDebug("invy", 1, "SWGInventoryTab:fileWrite:",
                    e.getMessage());
        }
    }

    /**
     * Helper method which resets all stats filter input fields.
     */
    private void filterReset() {
        for (JTextField tf : filterFields)
            tf.setText(null);
    }

    /**
     * Helper method which returns an array with the integer values from the
     * stats filter input fields. If there is no value at the input fields the
     * returned array contains zeroes.
     * 
     * @return an array of stat filter values
     */
    private int[] filterValues() {
        int[] stats = new int[Stat.COUNT];
        for (int i = 0; i < stats.length; ++i)
            stats[i] = ZNumber.intVal(filterFields[i].getText());

        return stats;
    }

    /**
     * Helper method which updates the filter fields with the specified values.
     * 
     * @param weights the weights
     */
    private void filterValues(SWGWeights weights) {
        isWorking = true;
        if (weights != null) {
            for (Stat s : Stat.values())
                filterFields[s.i].setText(
                        ZNumber.asText(weights.value(s), false, false));
        } else
            filterReset();
        isWorking = false;
    }

    /**
     * This method is called for two different action events, in both cases it
     * is determined if this component has focus and which action to select:
     * <ul>
     * <li>if focus is changed for {@link #resourceTab} the argument is {@code
     * true} or {@code false}; this is when the user selects the tab named
     * "Resources", or any one of its siblings</li>
     * <li>if focus is changed for this component the argument is always {@code
     * true}; this is when the user selects the tab for {@code this} component,
     * or any of its siblings</li>
     * </ul>
     * <p>
     * If this component gains focus this implementation updates the GUI.
     * 
     * @param focused {@code true} if there is any focus change for this
     *        component or its siblings, or if {@link #resourceTab} gained
     *        focus; {@code false} otherwise
     */
    void focusGained(boolean focused) {
        if (focused && isWorking)
            return;

        if (focused && resourceTab.getSelectedComponent() == this) {
            synchronized (this) {
                if (isWorking)
                    return;
                isWorking = true;
                if (!isGuiCreated)
                    makeGUI(); // lazy creation
                isWorking = false;
            }
            if (isGalaxyChanged()) {
                resetAssigneeCombo();
                updateDisplay();
            } else {
                this.repaint();
            }
            resizeBottomPanel();
            SWGHelp.push(helpPage);
            updateStatBar();
            actionTotalValue();
            if(SWGFrame.verified != null && SWGFrame.verified == true) {
            	cPanel.setVisible(true);
            } else {
            	cPanel.setVisible(false);
            	JOptionPane pane = new JOptionPane("<html>If you have not already, please create and verify account at swgaide.com and <br>input your account credentials in Options -> SwgAide, then click verify button.<br>Once verified, inventory functions should be unlocked.<br>This is due to some inventory functions using the website API.</html>",JOptionPane.PLAIN_MESSAGE);
                JDialog d = pane.createDialog(null, "Account is not Verified");
                d.setBackground(Color.WHITE);
                d.pack();
                d.setModalityType(ModalityType.MODELESS);
                d.setVisible(true);
            }
        } else {
            SWGHelp.remove(helpPage);
            wrappers = null;
        }
    }

    /**
     * Helper method for the importing methods. This method initiates the GUI,
     * such as status bars, prior to the import. This method also toggles
     * {@link #isWorking} to {@code true}.
     */
    private void importBegin() {
        frame.setCursor(Cursor
                .getPredefinedCursor(Cursor.WAIT_CURSOR));
        frame.progressBar.setIndeterminate(true);
        frame.putToLogbar_2(frame.progressBar);
    }

    /**
     * Helper method for the importing methods. This method resets the GUI, such
     * as status bars, when the import is finished. This method also toggles
     * {@link #isWorking} to {@code false} and triggers the main table to
     * update.
     */
    private void importDone() {
        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                frame.putToStatbar("Done",null);
                frame.putToLogbar_2(null);
                frame.progressBar.setIndeterminate(false);

                frame.setCursor(
                        Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                actionTotalValue();
                updateDisplay();
            }

            @Override
            public String toString() {
                return "SWGInventoryTab:importDone";
            }
        });
    }

    /**
     * Helper method which determines if the user has switched galaxy from a
     * previous visit to this GUI element. This method returns {@code true} if
     * the galaxy is changed.
     * 
     * @return {@code true} if the galaxy is different
     */
    private boolean isGalaxyChanged() {
        if (recentGalaxy == SWGResourceTab.galaxy())
            return false;

        recentGalaxy = SWGResourceTab.galaxy();
        wrappers = null;
        return true;
    }

    /**
     * Determines if the specified string may be exported from the Excel-based
     * J-n-F SWGTool and returns the number of elements per line, or -1. This
     * method is not 100% bullet proof. The output from J-n-F is CSV without
     * file header on the format, void stat values are 0:
     * 
     * <pre>
     * class;name;filter;ER;CR;CD;DR;FL;HR;MA;PE;OQ;SR;UT;amount;generic;CPU;[misc]
     *   0     1     2    3  4  5  6  7  8  9 10 11 12 13   14      15    16 17
     * </pre>
     * <p>
     * This method returns the number of elements if the specified string
     * resembles this layout, or -1. In particular it is determined that the
     * first two elements are a resource class followed by a string without
     * white spaces, that it follows 13(15) integers (filter to amount plus
     * optionally generic and CPU), and that all values for resource stats are 0
     * or that they match the specified resource class.
     * <p>
     * This method supports quote-embraced elements.
     * 
     * @param line a string to match
     * @return the number of elements per line, or -1
     */
    private int isJNF(String line) {
        String[] sp = ZCSV.parse(line, ';', true);
        if (sp.length < 15) return -1; // minimum: rc, name, flt, stats, amt

        String rcn = sp[0];
        int rcid;
        if ((rcid = SWGResourceClass.rcID(rcn)) < 0
                || !ZString.isAlpha(sp[1]))
            return -1;

        int[] is = new int[Stat.COUNT];
        final int offset = 3; // the first slot for a stat
        // scan from filter and up
        for (int i = 2; i < sp.length; ++i) {
            // len is ? and slot 17 undefined, always check all integers anyway
            if (i > 16) break;
            String e = sp[i];
            if (!ZNumber.isInteger(e, true))
                return -1;
            if (i >= offset && i < 11 + offset)
                is[i - offset] = ZNumber.intVal(e);
        }

        if (SWGValues.sum(is) > 0) {
            // don't bother make the array correct, just check
            SWGResourceClass rc = SWGResourceClass.rc(rcid);
            for (Stat s : Stat.gameOrder()) {
                if ((is[s.i] > 0 && !rc.has(s))
                        || (is[s.i] <= 0 && rc.has(s)))
                    return -1;
            }
        } // else we don't know but we at least have the supposed integers

        return sp.length;
    }

    /**
     * Helper method which returns a resource which is interactively selected by
     * the user, or {@code null} if none was found or if the user cancels. This
     * implementation recursively obtains suggestions from swgaide.com, each
     * turn truncating the suggested resource name to widen the scope. This
     * continues until the suggested resource name is shorter than 3 letters
     * (which is the known minimum length), or until the user selects a resource
     * or cancels.
     * 
     * @param rn the name of the resource to find, or its first letters
     * @param tn the resource name of the resource or the truncated name
     * @param galaxy the galaxy where the resource spawned
     * @param resNCList a list of pairs: names with their resource classes, see
     *        {@link #lookupResources(String, SWGCGalaxy)}
     * @return a resource selected by the user, or {@code null}
     * @throws IOException if there is an I/O error with swgaide.com
     */
    private SWGKnownResource lookupInteractively(String rn, String tn,
            SWGCGalaxy galaxy, List<String> resNCList) throws IOException {

        if (resNCList.size() == 1 && resNCList.get(0).startsWith("ERROR")) {
            SWGAide.printDebug("invy", 1, "SWGInventoryTab:" +
                    "lookupInteractively: " + resNCList.get(0));
            return null;
        }
        if (resNCList.size() > 0) {
            String[] strings = resNCList.toArray(new String[0]);
            String msg = ZString.fs("\"%s\" is unknown at swgaide.com%n" +
                    "but similar resources are found.%n%n" +
                    "Select a resource or Cancel", rn);
            String resp = (String) JOptionPane.showInputDialog(this, msg,
                    "Select resource", JOptionPane.OK_CANCEL_OPTION, null,
                    strings, strings[0]);

            if (resp == null) return null; // canceled

            return SWGResourceManager.getInstance(resp.split(" : ")[0], galaxy);
        }
        // else size <= 0
        // try a shorter name, might be spelling error
        int len = tn.length();
        len = (len > 5
                ? (len - 3)
                : (len - 1));
        if (len < 3) return null; // no resource has shorter name

        // try recursively
        String sn = tn.substring(0, len);
        return lookupInteractively(rn, sn, galaxy, lookupResources(sn, galaxy));
    }

    /**
     * Helper method which return a list of strings which denote resources known
     * at swgaide.com for the specified string and galaxy. Each element is a
     * line on the form {@code "name : resource class"} and each resource name
     * begins with the specified string.
     * <dl>
     * The list has the following sizes
     * <dt>size 0</dt>
     * <dd>no resource which begins with {@code string} is found</dd>
     * <dt>size 1</dt>
     * <dd>one resource which begins with {@code string} is found</dd>
     * <dt>size > 1</dt>
     * <dd>several resources which begin with {@code string} are found</dd>
     * <dt>size 1 but "ERROR"</dt>
     * <dd>if the string begins with "ERROR" the rest of the line contains an
     * error message</dd>
     * </dl>
     * <p>
     * <b>Notice:</b> Also if the specified resource name is correctly spelled
     * the returned list may contain more than one entry, whereof all begins
     * with the specified name.
     * 
     * @param string the first letters of a resource name, or its full name
     * @param galaxy the galaxy for the resource
     * @return a list of the resources which are known at swgaide.com, or one
     *         string which begins with "ERROR"
     * @throws NullPointerException if an argument is {@code null}
     */
    List<String> lookupResources(String string, SWGCGalaxy galaxy) {
        SWGSoapListResResponse resp =
                SWGResourceManager.getResources(string, galaxy);

        ArrayList<String> retList = new ArrayList<String>();
        if (resp.getFaultMessage() != null)
            retList.add("ERROR: " + resp.getFaultMessage());
        else
            for (SWGResourceTuple rt : resp.getTuples())
                retList.add(rt.name + " : " + rt.type.rcName());

        return retList;
    }

    /**
     * Creates and returns the GUI component for displaying and selecting
     * assignees, a combo box.
     * 
     * @return a GUI component
     */
    private Component makeAssigneeComboBox() {
        assigneeCombo = new JComboBox<String>();
        assigneeCombo.setToolTipText("Select an assignee to filter the view");
        resetAssigneeCombo();

        assigneeCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                updateDisplay();
            }
        });
        return assigneeCombo;
    }

    /**
     * Creates and returns the GUI component for the bottom area of this tab.
     * This component is populated with the assignee combo box, a clear-button,
     * the resource-class combo box, and the stats filter input fields.
     * 
     * @return a GUI component
     */
    private Component makeBottomPanel() {
        bottomPanel = Box.createHorizontalBox();

        bottomPanel.add(makeAssigneeComboBox());
        bottomPanel.add(makeClearButton());
        bottomPanel.add(makeResourceClassCombo());
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 0))); // size later
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 0))); // size later
        bottomPanel.add(makeTotalValue()); // size later
        makeStatFilterFields(bottomPanel);

        return bottomPanel;
    }
    
    private Component makeTotalValue () {
    	Box tbox = Box.createHorizontalBox();
    	tbox.setAlignmentX(Component.RIGHT_ALIGNMENT);
    	totVal = new StyledLabel(" ");
    	totVal.setToolTipText("Total value of all resources in this view");
    	totVal.setAlignmentX(SwingConstants.RIGHT);
    	tbox.add(totVal);
    	
    	return tbox;
    }

    /**
     * Creates and returns a GUI button which clears possible user input at the
     * bottom panel of the GUI.
     * 
     * @return a GUI button
     */
    private Component makeClearButton() {
        JButton clearButton = new JButton("Clear");
        clearButton.setToolTipText("Clear any selection and the filter fields");
        clearButton.setMnemonic('C');

        clearButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
            	actionTotalValue();
                actionResetBottomPanel();
            }
        });
        return clearButton;
    }
    
    private Component makeButtonPanel () {
    	final SWGCharacter toon = SWGFrame.getSelectedCharacter();
    	cPanel = new JMenuBar();
    	JButton add = new JButton("Add");
    	add.setToolTipText("Create a new entry for this galaxy");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e1) {
                dialog().showAndBegin(null);
            }
        });
    	JButton nimport = new JButton("Notes Import");
    	nimport.setToolTipText("Import / Update inventory from in-game notes file");
        nimport.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e1) {
                actionImportNotes(toon);
            }
        });
        nimport.setEnabled(toon != null);
    	JButton nexport = new JButton("Notes Export");
    	nexport.setToolTipText("Export current display to in-game notes file");
        nexport.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e1) {
            	if(wrappersFiltered().size() > 0) {
            		actionExportNotes(toon);
            	} else {
            		JOptionPane.showMessageDialog(frame, "It appears your inventory is empty. Nothing to export.",
                            "Notes Export", JOptionPane.PLAIN_MESSAGE);
            	}
            }
        });
        nexport.setEnabled(toon != null && toon.galaxy().exists());
    	JButton csvbackup = new JButton("CSV Backup");
    	csvbackup.setToolTipText("Backup the current view to CSV file");
        csvbackup.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e1) {
            	if(wrappersFiltered().size() > 0) {
            		actionExportToFile();
            	} else {
            		JOptionPane.showMessageDialog(frame, "It appears your inventory is empty. Nothing to backup.",
                            "CSV Backup", JOptionPane.PLAIN_MESSAGE);
            	}
            }
        });
    	JButton csvrestore = new JButton("CSV Restore");
    	csvrestore.setToolTipText("Import inventory from CSV file");
        csvrestore.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e1) {
                actionImportFile();
            }
        });
    	JButton gcopy = new JButton("Galaxy Copy");
    	gcopy.setToolTipText("Copy this inventory to another galaxy");
        gcopy.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e1) {
            	if(SWGResController.inventory(SWGFrame.getSelectedGalaxy()).size() > 0) {
            		actionCopyAll();
            	} else {
            		JOptionPane.showMessageDialog(frame, "It appears your inventory is empty. Nothing to copy.",
                            "Galaxy Copy", JOptionPane.PLAIN_MESSAGE);
            	}
            }
        });
    	JButton dall = new JButton("Delete All");
    	dall.setForeground(Color.RED);
    	dall.setToolTipText("Delete all entries of the current view");
        dall.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e1) {
            	if(wrappersFiltered().size() > 0) {
            		actionDeleteEntries();
            	} else {
            		JOptionPane.showMessageDialog(frame, "It appears your inventory is empty. Nothing to delete.",
                            "Delete All", JOptionPane.PLAIN_MESSAGE);
            	}
            }
        });
    	
    	int pad = 5;
    	cPanel.setBorder(BorderFactory.createCompoundBorder(cPanel.getBorder(),BorderFactory.createEmptyBorder(pad, pad, pad, pad)));
    	cPanel.add(add);
    	cPanel.add(Box.createHorizontalStrut(pad));
    	cPanel.add(nimport);
    	cPanel.add(Box.createHorizontalStrut(pad));
    	cPanel.add(nexport);
    	cPanel.add(Box.createHorizontalStrut(pad));
    	cPanel.add(csvbackup);
    	cPanel.add(Box.createHorizontalStrut(pad));
    	cPanel.add(csvrestore);
    	cPanel.add(Box.createHorizontalStrut(pad));
    	cPanel.add(gcopy);
    	cPanel.add(Box.createHorizontalGlue());
    	cPanel.add(dall);
		return cPanel;
    }
    /**
     * Helper method which dispatches the creation of the main table and the
     * bottom panel to their make-methods respectively. Once this method is
     * finished {code isGuiCreated} is set to {@code true}.
     */
    private synchronized void makeGUI() {
        if (isGuiCreated) return;
        
        this.addComponentListener(new ComponentAdapter() 
        {  
        	public void componentResized(ComponentEvent evt) {
        		resizeBottomPanel();
        	}
        });
        
        setLayout(new BorderLayout());
        add(makeButtonPanel(), BorderLayout.NORTH);
        add(makeMainTable(), BorderLayout.CENTER);
        add(makeBottomPanel(), BorderLayout.PAGE_END);
        isGuiCreated = true;
    }

    /**
     * Creates and returns the main table for inventories.
     * 
     * @return a GUI component
     */
    private Component makeMainTable() {
        tableModel = new TableModel();
        table = new SWGJTable(tableModel);

        table.setDefaultRenderer(Integer.class,
                new SWGResourceStatRenderer(tableModel));

        SWGTableCellEditor te = new SWGTableCellEditor(SwingConstants.TRAILING);
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(3).setCellEditor(te);
        tcm.getColumn(4).setCellEditor(te);
        tcm.getColumn(18).setCellEditor(te);

        SWGGuiUtils.setRowHeight(table);
        int w;
        w = SWGGuiUtils.fontWidth(table, "100,000,000", table.getFont()) + 5; // amount
        SWGGuiUtils.tableColumnFixWidth(table, 3, w);
        w = SWGGuiUtils.fontWidth(table, "CPU0", table.getFont()) + 5; // cpu
        SWGGuiUtils.tableSetColumnWidths(table, 4, 4, w, 5);
        // do not set width for the notes column
        w = SWGGuiUtils.fontWidth(table, "1 000", table.getFont()) + 5;
        SWGGuiUtils.tableSetColumnWidths(table, 6, 6 + 10, w, 5);
        w = SWGGuiUtils.fontWidth(table, "999.99", table.getFont()) + 5;
        SWGGuiUtils.tableColumnSetWidth(table, 17, w, w, w + 10);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);

        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    
                    public void valueChanged(ListSelectionEvent e) {
                        if (!e.getValueIsAdjusting())
                            actionTableSelect();
                    }
                });
        table.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                actionTableMouse(e);
            }
        });
        
        // just for the column-width thing
        table.getTableHeader().addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseReleased(MouseEvent e)
        	{
        		/* On mouse release, check if column width has changed */
        		if(table.getColumnWidthChanged()) {
        			resizeBottomPanel();
        			table.setColumnWidthChanged(false);
        		}
        	}
        });
        
        table.getColumnModel().addColumnModelListener(
                new TableColumnModelListener() {

                    public void columnAdded(TableColumnModelEvent e) { /* pass */
                    }

                    
                    public void columnMarginChanged(ChangeEvent e) {
                    	if(!table.getColumnWidthChanged()) {
                            /* the condition  below will NOT be true if
                               the column width is being changed by code. */
                            if(table.getTableHeader().getResizingColumn() != null) {
                                // User must have dragged column and changed width
                                table.setColumnWidthChanged(true);
                            }
                        }
                    }

                    public void columnMoved(TableColumnModelEvent e) { /* pass */
                    }

                    public void columnRemoved(TableColumnModelEvent e) { /* pass */
                    }

                    public void columnSelectionChanged(ListSelectionEvent e) { /**/
                    }
                });

        JScrollPane jsp =
                new JScrollPane(table,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // also listen to mouse clicks at an empty table
        jsp.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
                actionTableMouse(e);
            }
        });
        return jsp;
    }

    /**
     * Creates and returns the GUI component for filtering on resource class,
     * that is a combo box.
     * 
     * @return a GUI component
     */
    private Component makeResourceClassCombo() {
        Vector<String> resCls = new Vector<String>(SWGResourceClass.maxID());
        resCls.add("All");
        resCls.addAll(SWGResourceClass.rcNames());

        resourceClassCombo = new JComboBox<String>(resCls);
        resourceClassCombo.setToolTipText(
                "Select a resource class to filter the view");

        resourceClassCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDisplay();
            }
        });
        return resourceClassCombo;
    }

    /**
     * Creates and returns a GUI component which is a stats filter input field
     * with the specified tool tip text. A document listener is added to the
     * component which dispatches to {@link #updateDisplay()}.
     * 
     * @param toolTipText the text for the tool tip
     * @return the GUI component
     * @see SWGDocNumberFilter
     */
    private JTextField makeStatFilterField(String toolTipText) {

        final JTextField fld = new JTextField();
        fld.setBorder(BorderFactory.createLoweredBevelBorder());
        fld.setHorizontalAlignment(SwingConstants.RIGHT);
        fld.setToolTipText(toolTipText);

        ((AbstractDocument) fld.getDocument())
                .setDocumentFilter(new SWGDocNumberFilter());

        fld.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                updateDisplay();
            }

            public void insertUpdate(DocumentEvent e) {
                updateDisplay();
            }

            public void removeUpdate(DocumentEvent e) {
                updateDisplay();
            }
        });

        return fld;
    }

    /**
     * Creates the stats filter input fields adds them to the specified
     * component. This implementation adds the objects to the GUI in in-game
     * order, however, it adds the new objects to {@link #filterFields} in the
     * order used by the model.
     * 
     * @param panel the component which to add the new components to
     */
    private void makeStatFilterFields(Container panel) {
        for (Stat s : Stat.gameOrder()) {
            JTextField tf = makeStatFilterField(String.format(
                    "%s - %s", s.name(), s.getDescription()));
            filterFields[s.i] = tf;
            panel.add(tf);
        }
    }

    /**
     * Helper method which returns a file object for the specified character, or
     * {@code null}. This implementation presents the user with the notes files
     * for the station of the character and returns the selected file. If there
     * are no predefined notes file or if the user aborts this method returns
     * {@code null}.
     * 
     * @param toon a character
     * @return a file object, or {@code null}
     */
    private SWGNotes notesFile(SWGCharacter toon) {
        // find notes files to choose from
        SWGStation station = toon.galaxy().station();
        String[] notesNames = SWGNotes.notesNames(station, true);
        Arrays.sort(notesNames);

        if (notesNames.length <= 0) {
            showError("No notes files for this galaxy", "No notes");
            return null;
        }

        // concatenate with galaxy-name, imported per galaxy not station
        String pkey =
                "resourceInventoryNotes" + recentGalaxy.getName();
        String note = (String) SWGFrame.getPrefsKeeper().get(pkey);

        String msg = String.format("Select a notes file for \"%s\"",
                toon.getName());
        note = (String) JOptionPane.showInputDialog(this, msg, "Select file",
                JOptionPane.QUESTION_MESSAGE, null, notesNames, note);

        if (note == null)
            return null;

        SWGFrame.getPrefsKeeper().add(pkey, note);

        return station.notes(note);
    }

    /**
     * Helper method which parses an in-game notes file and adds inventory
     * entries for the current assignee at the current galaxy. This method
     * executes on a worker thread.
     * 
     * @param notes the file to parse from
     */
    private void notesRead(final SWGNotes notes) {

        importBegin();

        final String a = assignee();

        // snapshot of fields in case the user flips galaxy while parsing
        final SWGCGalaxy g = recentGalaxy;

        // run the parser in a thread so GUI can be freed up
        Runnable parse = () -> {
        	try {
        		List<SWGInventoryWrapper> lst = notesReadWorker(notes, a, g);
                SWGResController.inventoryAdd(lst, g);
                for (SWGInventoryWrapper w : lst)
                    w.equalAddSub = null; // nullify wrapper for future use

                importDone();
        	} catch (Throwable e) {
        		SWGAide.printError("SWGInventoryTab:notes import error: ", e);
        	}
        };
        // start the thread
        new Thread(parse).start();
    }

    /**
     * Helper method which carries out the actual workload and returns a list of
     * wrappers. If there is an error an empty list is returned.
     * 
     * @param notes the file to parse from
     * @param ass the assignee for the inventory entries
     * @param gxy the galaxy for the assignee
     * @return a list of wrappers, or an empty list if there is an error
     */
    private List<SWGInventoryWrapper> notesReadWorker(
            SWGNotes notes, String ass, SWGCGalaxy gxy) {

        // each line appended to errorMessages must end in "\n"
        ZString err = new ZString();

        try {
            List<SWGInventoryWrapper> wl = new ArrayList<SWGInventoryWrapper>();
            List<String> tl = notes.lines();
            List<String> nl = new ArrayList<String>();
            for (int i = 0; i < tl.size(); i++) {
            	String l = tl.get(i);
            	if (ZReader.isComment(l)) {
            		continue;
            	} else {
            		nl.add(l);
            	}
            }
            importLines = nl.size();
            frame.putToStatbar(null,null);
            for (String line : nl) {
            	++importCounter;
                line = line.trim();
                SWGInventoryWrapper w = notesReadWrapper(line, gxy, err);
                if (w == null)
                    return Collections.emptyList(); // abort
                if (w != SWGInventoryWrapper.DUMMY)
                    wl.add(w); // error but continue
            }

            if (!err.isEmpty()) showErrors(err);

            return wl;
        } catch (Exception e) {
            if (SWGConstants.DEV_DEBUG) e.printStackTrace();
            showError(String.format("Error reading notes file:%n%s%n%s",
                    e.getCause(), e.getMessage()), "Notes file error");
            return Collections.emptyList();
        }
    }

    /**
     * Helper method which parses the specified line and returns a wrapper for
     * the entry. If there is an error but the user selects to continue
     * {@link SWGInventoryWrapper#DUMMY} is returned, if the user selects to
     * abort {@code null} is returned. The specified file must be on this strict
     * syntax, there is no support for variations:
     * 
     * <pre>[(galaxyName)]resourceName,amount[,optional notes]</pre>
     * 
     * @param line the line to parse
     * @param assignee the assignee for the entry
     * @param galaxy the galaxy to add entries to, this is the current galaxy
     * @param err an error string to add error messages to
     * @return a parsed wrapper, if there is an error either {@code null} or
     *         {@link SWGInventoryWrapper#DUMMY} determined by if the user
     *         selects to abort or if parsing can continue respectively
     */
    private SWGInventoryWrapper notesReadWrapper(String line, SWGCGalaxy galaxy, ZString err) {
    	
    	final String curAss = assignee();
		final List<String> lAss = assignees();

        // line = [(galaxyName)]resourceName,amount[,whatever]
        StringTokenizer tok = new StringTokenizer(line);
        try {
            SWGCGalaxy gxy; // resource origin
            String name = null;
            String assignee;
            String token = tok.nextToken(",;:").trim();
            if (token.startsWith("(")) { // begins with (galaxyName)
                int end = token.indexOf(')');
                String rg = token.substring(1, end).trim();
                String rgg = SWGCGalaxy.properName(rg);
                if (rgg == null) {
                    err.app("Invalid galaxy name (");
                    err.app(rg).app(") in line: ").appnl(line);

                    return SWGInventoryWrapper.DUMMY;
                }

                gxy = SWGCGalaxy.fromName(rgg);
                assignee = token.substring(end + 1);
            } else {
                gxy = galaxy;
                assignee = token;
            }
            assignee = ZString.tac(assignee);

            long amount = 0L;
            double cpu = 0;
            String eas = null;
            String notesString = null;
            if (tok.hasMoreTokens()) {
            	name = tok.nextToken(",;:");
            	name = ZString.tac(name);
            }
            if (tok.hasMoreTokens()) {
                String amt = tok.nextToken(" ,;:");
                if (amt.startsWith("=")
                        || amt.startsWith("+") || amt.startsWith("-")) {
                    eas = amt.substring(0, 1);
                    amt = amt.substring(1).trim(); // otherwise old style
                }

                amount = ZNumber.longExc(amt);
                amount = amount >= 0
                        ? amount
                        : 0;

                
                if (tok.hasMoreTokens()) {
                	String cp = tok.nextToken(" ,;:");
                	cp = cp.trim();
                	if(!cp.isEmpty()) {
                		cpu = Double.parseDouble(cp);
                	}
                }
                if (tok.hasMoreTokens()) {
                    notesString = tok.nextToken("\n\r");
                    notesString = notesShaveString(notesString);
                }
            }
            
            SWGKnownResource res = null;
            // check for whitespace in name
            if (name.indexOf(' ') > 0) {
            	SWGResourceClass cls = null;
            	cls = SWGResourceClass.rc(name);
            	res = cls.spaceOrRecycled();
                res.galaxy(gxy);
            } else {
            	res = SWGResourceManager.getInstance(name, gxy);
            }
            if (res == null)
                res = lookupInteractively(name, name, gxy,
                        lookupResources(name, gxy));

            if (res != null) {
            	frame.putToStatbar(String.format(
                        "%s(%s) - Importing %s",
                        Integer.toString(importCounter),
                        Integer.toString(importLines), name),null);
            	// get the assignee
            	String ass = curAss;
        		if(lAss != null) {
        			for (int i = 0; i < lAss.size(); i++) {
        				String aa = lAss.get(i);
        				if(!assignee.isEmpty() && assignee.equalsIgnoreCase(aa) ) {
        					ass = assignee;
        				}
        			}
        		}
                SWGInventoryWrapper wr = new SWGInventoryWrapper(res, ass);
                wr.setAmount(amount);
                wr.setCPU(cpu);
                wr.equalAddSub = eas;
                wr.setNotes(notesString);
                return wr;
            }
        } catch (Exception e) {
            if (SWGConstants.DEV_DEBUG) e.printStackTrace();

            String ms1 = String.format(": %s%n%s:%s", line,
                    e.getClass().getSimpleName(), e.getMessage());
            SWGAide.printDebug("invy", 1, "SWGInventoryTab:" +
                    "notesReadWrapper" + ms1);

            if (JOptionPane.OK_OPTION == showConfirmWarning(ZString.fs(
                    "Error parsing notes file%s%n%nAbort?", ms1), "Error"))
                return null; // cancel
        }
        err.app("Failed for: ").appnl(line);
        return SWGInventoryWrapper.DUMMY;
    }

    /**
     * Helper method which shaves leading punctuation signs off of the specified
     * string and returns the result.
     * 
     * @param string the string shave
     * @return the shaved string
     * @throws NullPointerException if the argument is {@code null}
     */
    private String notesShaveString(String string) {
        String a = string.trim();
        String b = null;
        while (!a.equals(b)) {
            b = a;
            char c = a.charAt(0);
            if (c == ',' || c == ';' || c == '.' || c == ':')
                a = a.substring(1);
        }
        return b;
    }

    /**
     * Helper method which appends the specified inventory wrapper's name,
     * amount, and notes.
     * 
     * @param w the wrapper
     * @param z a string
     */
    private void notesWrite(SWGInventoryWrapper w, ZString z) {
        if (!w.getResource().rc().isSpaceOrRecycled()
                && w.getResource().galaxy().equals(recentGalaxy) == false) {
            SWGCGalaxy g = w.getResource().galaxy();
            z.app("(").app(g != null
                    ? g.getName()
                    : "?").app(")");
        }
        z.app(w.getAssignee()).app(", ");
        z.app(w.getResource().getName()).app(", ");
        z.app(Long.toString(w.getAmount())).app(", ");
        z.app(Double.toString(w.getCPU())).app(", ");

        String n = w.getNotes().split("\n\r", 1)[0]; // just first line if many
        z.appnl(n);
    }

    /**
     * Helper method which writes the list of inventory resources that are
     * currently displayed to the specified notes file. If the argument is
     * {@code null} or if no resources are displayed this method does nothing.
     * If there is an error a message is written to SWGAide's error log
     * 
     * @param notes the notes
     * @param toon the currently selected character
     */
    private void notesWrite(SWGNotes notes) {
        if (notes == null) return;

        List<SWGInventoryWrapper> wl = wrappersFiltered();
        if (wl.size() <= 0) return;

        notes.backup();

        Collections.sort(wl, comparatorResourceNames());

        try {
            ZString z = new ZString();

            notesWriteHeader(z);
            for (SWGInventoryWrapper w : wl)
                notesWrite(w, z);

            notes.setText(z.nl().toString());
        } catch (Throwable e) {
            SWGAide.printError("SWGInventoryTab:notesWrite", e);
        }
    }

    /**
     * Helper method which appends the header for a notes file. This method is
     * invoked when the user selects to write inventory entries to a notes file.
     * 
     * @param z a string
     * @param toon the currently selected character
     */
    private void notesWriteHeader(ZString z) {
    	String ass = (String) assigneeCombo.getSelectedItem();
        z.app("# SWGAide :: Inventory for ").app(ass).app(" @ ");
        z.app(recentGalaxy.getName()).appnl(" at the notes file format:");
        z.appnl("# To change the amount you can optionally prepend + - or = to the amount value which will");
        z.appnl("# add, subtract, or set the amount, press F1 for details").nl();
        z.appnl("# assignee, resourcename, amount, cpu, notes").nl();
    }

    /**
     * Selects the specified resource class at the GUI combo box which as a side
     * effect filters the display. If the specified resource class is {@code
     * null} this method clears the current selection and exits, the second
     * argument is ignored. If the specified resource is not {@code null} and
     * exists in inventory it is selected.
     * 
     * @param rc a resource class, or {@code null}
     * @param kr a resource to select, or {@code null}
     */
    private void rcSelect(SWGResourceClass rc, SWGKnownResource kr) {
        wrappers = null;
        if (rc == null) {
            resourceClassCombo.setSelectedIndex(-1);
        } else {
            resourceClassCombo.setSelectedItem(rc.rcName());

            if (kr != null) {
                List<SWGInventoryWrapper> fl = wrappersFiltered();
                for (int i = 0; i < fl.size(); ++i) {
                    if (fl.get(i).getResource() == kr) {
                        int j = table.convertRowIndexToView(i);
                        table.setRowSelectionInterval(j, j);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Helper method which resets the GUI component for assignees and removes
     * any selection.
     */
    private void resetAssigneeCombo() {
        isWorking = true;
        assigneeCombo.removeAllItems();
        assigneeCombo.addItem("All");

        List<String> as = assigneesAndCharacters();
        for (String a : as)
            assigneeCombo.addItem(a);

        isWorking = false;
        assigneeCombo.setSelectedIndex(0);
    }

    /**
     * Helper method that resets the bottom panel. This method clears any user
     * selection at any of the GUI elements at the bottom panel. No action is
     * directly triggered by this method and indirect actions should be blocked
     * by {@link #isWorking} that is {@code true} while this method operates.
     */
    private void resetBottomPanel() {
        isWorking = true;
        assigneeCombo.setSelectedIndex(0);
        resourceClassCombo.setSelectedIndex(0);
        table.getRowSorter().setSortKeys(null);
        filterReset();
        table.clearSelection();
        isWorking = false;
    }

    /**
     * Resizes all GUI components at the bottom panel relative the corresponding
     * column widths of the main table. The job executes on a worker thread.
     */
    private void resizeBottomPanel() {
        if (isWorking || !isGuiCreated)
            return;

        SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                resizeComponent(assigneeCombo, 0, 20);
                resizeComponent(bottomPanel.getComponent(1), 1, -20);

                // res-class, rigid strut, and all stat input fields
                for (int i = 2; i < bottomPanel.getComponentCount(); ++i)
                    resizeComponent(bottomPanel.getComponent(i), i, 0);

                bottomPanel.revalidate();
                bottomPanel.repaint(300L);
            }

            @Override
            public String toString() {
                return "SWGInventoryTab:resizeBottomPanel";
            }
        });
    }

    /**
     * Helper method which aligns the width of the component with the width of
     * the specified column of the main table, adjusting the width with {@code
     * modifier} which can be negative, zero, or positive.
     * 
     * @param comp the component to resize
     * @param column the corresponding table column
     * @param modifier the modifier with; negative, 0, or positive
     */
    private void resizeComponent(Component comp, int column, int modifier) {
        TableColumn c = table.getColumnModel().getColumn(column);
        Dimension d = new Dimension(c.getWidth() + modifier, comp.getHeight());
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
    }

    /**
     * Helper method which displays a GUI OK/Cancel "question" dialog with the
     * specified arguments and returns {@link JOptionPane#OK_OPTION} or any of
     * its siblings.
     * 
     * @param message the message to display
     * @param title the title for the dialog
     * @return {@link JOptionPane#OK_OPTION} or any of its siblings
     */
    private int showConfirmQuestion(String message, String title) {
        return JOptionPane.showConfirmDialog(frame, message, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Helper method which displays a GUI OK/Cancel "warning" dialog with the
     * specified arguments and returns {@link JOptionPane#OK_OPTION} or any of
     * its siblings.
     * 
     * @param message the message to display
     * @param title the title for the dialog
     * @return {@link JOptionPane#OK_OPTION} or any of its siblings
     */
    private int showConfirmWarning(String message, String title) {
        return JOptionPane.showConfirmDialog(frame, message, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Helper method which displays a GUI "error" message dialog with the
     * specified arguments. The dialog does not return any response.
     * 
     * @param message the main message to display
     * @param title the title of the dialog
     */
    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Helper method which displays a GUI dialog with a larger text field which
     * reads the text from the {@code errorMessages}. This method is only called
     * from methods which collects non-fatal error messages which are displayed
     * for the user when the process is finished.
     * <p>
     * <b>Notice:</b> Each message in the specified argument must be delimited
     * with a character '\n'.
     * 
     * @param err a container with the text to display
     */
    private void showErrors(ZString err) {
        err.pre("Summary: error and information messages.\n" +
                "Perhaps also see log file.\n\n", false);

        JDialog dialog = new JDialog(frame, false);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Point loc = frame.getLocation();
        loc.translate(20, 20);
        dialog.setLocation(loc);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setText(err.toString());
        textArea.setCaretPosition(0);

        dialog.add(new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Helper method which displays a popup menu regarding inventory entries,
     * importing and exporting, etc.
     * 
     * @param row the row which is selected, already converted to the model, or
     *        -1 if no row is selected
     * @param e the event which triggered the call
     */
    private void tablePopup(final int row, MouseEvent e) {
        JPopupMenu ppp = new JPopupMenu();

        final SWGInventoryWrapper wr = row >= 0
                ? wrappersFiltered().get(row)
                : null;
        SWGKnownResource kr = wr != null
                ? wr.getResource()
                : null;
        SWGResourceClass rc = kr != null
                ? kr.rc()
                : null;

        if(SWGFrame.verified != null && SWGFrame.verified == true) {
	        ppp.add(SWGSchemResViewer.displayMenu(kr, this, frame));
	        updateViewer = true; // by chance, it is reset if...
	        SWGSchematicTab schemTab = SWGFrame.getSchematicTab(frame);
	        SWGSchemController sc = new SWGSchemController(schemTab);
	        ppp.add(sc.resClassUse(rc));
	
	        ppp.addSeparator();
	        if(kr != null && kr.id()>0) {
		        JMenuItem goWeb = new JMenuItem("View Resource on Web Site");
		        goWeb.setToolTipText("View this resource on the SWGAide website");
		        goWeb.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                ZHtml.browser("https://swgaide.com/resources/view.php?rid=" + kr.id() );
		            }
		        });
		        
		        ppp.add(goWeb);
		        ppp.addSeparator();
	        }
	
	        JMenuItem edit = new JMenuItem("Edit...");
	        edit.setToolTipText("Edit the selected entry");
	        edit.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e1) {
	                dialog().showAndBegin(wr);
	            }
	        });
	        edit.setEnabled(row >= 0);
	        ppp.add(edit);
	
	        JMenu mgm = new JMenu("Manage");
	        mgm.setEnabled(wrappersFiltered().size() > 0);
	        ppp.add(mgm);
	
	        final List<SWGInventoryWrapper> dl = duplicates();
	        JMenuItem dupl = new JMenuItem("Find duplicates");
	        dupl.setToolTipText("Filter to find duplicate resource entries");
	        dupl.addActionListener(new ActionListener() {
	            
	            @Override
	            public void actionPerformed(ActionEvent e1) {
	                resetBottomPanel();
	                wrappers = dl;
	                tableModel.fireTableDataChanged();
	            }
	        });
	        dupl.setEnabled(!dl.isEmpty());
	        mgm.add(dupl);
	
	        final String s = wr == null
	                ? "All"
	                : wr.getAssignee();
	        JMenuItem merg = new JMenuItem(String.format("Merge to \"%s\"", s));
	        merg.setToolTipText(String.format(
	                "Merge all duplicate resource entries to \"%s\"", s));
	        merg.addActionListener(new ActionListener() {
	            
	            @Override
	            public void actionPerformed(ActionEvent e1) {
	                actionDuplicatesMerge(s, dl);
	            }
	        });
	        merg.setEnabled(!dl.isEmpty());
	        mgm.add(merg);
	
	        JMenuItem move = new JMenuItem(String.format("Move from \"%s\"...", s));
	        move.setToolTipText(String.format(
	                "Move visible resource entries from \"%s\" to...", s));
	        move.addActionListener(new ActionListener() {
	            
	            @Override
	            public void actionPerformed(ActionEvent e1) {
	                actionMove(s);
	            }
	        });
	        mgm.add(move);
	
	        ppp.addSeparator();
	
	        if (rc != null && rc.isSub(SWGCreatureResources.class))
	            ppp.add(SWGResController.creatureHarvMenu(
	                    (SWGCreatureResources) rc, null));
	
	        JMenuItem create = new JMenuItem("Create guard...");
	        create.setToolTipText("Create a guard based in the selected entry");
	        create.addActionListener(new ActionListener() {
	            
	            public void actionPerformed(ActionEvent e1) {
	                actionGuardCreate(row);
	            }
	        });
	        create.setEnabled(row >= 0);
	        ppp.add(create);

	        ppp.addSeparator();

	        JMenuItem del = new JMenuItem("Delete entry");
	        del.setToolTipText("Delete the selected entry");
	        del.addActionListener(new ActionListener() {
	            
	            public void actionPerformed(ActionEvent e1) {
	                actionDeleteEntry(row);
	            }
	        });
	        del.setEnabled(row >= 0);
	        ppp.add(del);
        } else {
        	JLabel warn = new JLabel("<html>If you have not already, please create and verify account at swgaide.com and <br>input your account credentials in Options -> SwgAide, then click verify button.<br>Once verified, inventory functions should be unlocked.<br>This is due to some inventory functions using the website API.</html>");
        	warn.setOpaque(true);
        	warn.setBackground(Color.WHITE);
        	warn.setBorder(new EmptyBorder(10, 10, 10, 10));
        	ppp.add(warn);
        	
        }

        ppp.show(table, e.getX(), e.getY());
    }

    /**
     * Helper method which resets the main table of inventory entries. This
     * method is called when the underlying data model is changed.
     */
    void updateDisplay() {
        if (isWorking || !isGuiCreated)
            return;

        // keep selection if possible
        int row = wrappers == null
                ? -1
                : table.getSelectedRow();
        SWGInventoryWrapper selected = row < 0
                ? null
                : wrappers.get(table.convertRowIndexToModel(row));
        isWorking = true;
        wrappers = null;

        String select = (String) resourceClassCombo.getSelectedItem();
        resourceClass = (select == null || select.equals("All"))
                ? null
                : SWGResourceClass.rc(select);

        int[] flt = filterValues();
        if (SWGValues.sum(flt) > 0) {
            if (SWGWeights.isValid(flt))
                filter = new SWGWeights(flt);
            else
                filter = new SWGResourceFilter(flt);
        } else
            filter = null;

        isWorking = false;

        if (tableModel != null)
            tableModel.fireTableDataChanged();

        if (selected != null) {
            // retain the selection, if possible
            List<SWGInventoryWrapper> wl = wrappersFiltered();
            for (int i = 0; i < wl.size(); ++i) {
                if (selected == wl.get(i)) {
                    int j = table.convertRowIndexToView(i);
                    table.getSelectionModel().setSelectionInterval(j, j);
                    break;
                }
            }
        }
    }

    /**
     * A help method to set the internal flag and redraw the UI
     *
     * @param useJTLcap
     */
    void updateJTLcap(boolean useJTLcap) {
    	this.useJTLcap = useJTLcap;
    	updateDisplay();
    }
    
    /**
     * Helper method which updates SWGAide's status bar. This method executes on
     * a worker thread.
     */
    private void updateStatBar() {
        if (isWorking || !isGuiCreated || !isVisible())
            return;

        String msg = wrappersFiltered().size() + " resources";
        frame.putToLogbar_2(msg);
    }

    /**
     * Helper method which returns a list of wrappers filtered on the selected
     * assignee. If no particular assignee is selected a list of all wrappers
     * for the current galaxy is returned. If nothing else
     * {@link Collections#EMPTY_LIST} is returned.
     * <p>
     * <b>Notice:</b> The returned list may be the collection which is stored by
     * SWGAide and must not be modified rashly.
     * 
     * @return a list of wrappers, or {@link Collections#EMPTY_LIST}
     */
    private List<SWGInventoryWrapper> wrappersByAssignee() {
        String ass = (String) assigneeCombo.getSelectedItem();

        if (ass.equals("All"))
            return SWGResController.inventory(recentGalaxy);

        List<SWGInventoryWrapper> ws = SWGResController.inventory(
                    ass, recentGalaxy, false);

        if (ws != null)
            return ws;
        return Collections.emptyList();
    }

    /**
     * Helper method which returns a list of wrappers according to the stats
     * filter input fields. If the filter values form a weighed filter the
     * returned list is just sorted. Otherwise, if at least one value is entered
     * the list is filtered on a plain filter. If there are no entered values
     * the argument is returned.
     * <p>
     * It is assumed that {@link #filter} is updated before this method is
     * called.
     * 
     * @param wl the list of wrappers to filter
     * @return a list of wrappers
     */
    private List<SWGInventoryWrapper> wrappersByFilter(
            List<SWGInventoryWrapper> wl) {

        if (filter == null)
            return wl;

        if (SWGWeights.class.isAssignableFrom(filter.getClass()))
            return wrappersSortedByWeight(wl);

        return wrappersByResFilter(wl, (SWGResourceFilter) filter);
    }

    /**
     * Helper method which returns a list of wrappers filtered by
     * {@link #filter}.
     * <p>
     * It is assumed that {@link #filter} is validated to be of type
     * {@link SWGResourceFilter} before this method is called.
     * 
     * @param wl the list of wrappers to filter
     * @param flt the filter to sift through
     * @return a list of wrappers
     */
    private List<SWGInventoryWrapper> wrappersByResFilter(
            List<SWGInventoryWrapper> wl, SWGResourceFilter flt) {

        List<SWGInventoryWrapper> wrs =
                new ArrayList<SWGInventoryWrapper>(wl.size());

        for (SWGInventoryWrapper w : wl)
            if (flt.isBetter(w.getResource(), true))
                wrs.add(w);

        return wrs;
    }

    /**
     * Helper method which returns a list of wrappers filtered on the selected
     * resource class. If no resource class is selected the argument itself is
     * returned.
     * <p>
     * It is assumed that {@link #resourceClass} is updated before this method
     * is called.
     * 
     * @param wl the list of wrappers to filter
     * @return a list of wrappers
     */
    private List<SWGInventoryWrapper> wrappersByResourceClass(
            List<SWGInventoryWrapper> wl) {

        if (resourceClass == null)
            return wl;

        List<SWGInventoryWrapper> wrs =
                new ArrayList<SWGInventoryWrapper>(wl.size());
        Class<? extends SWGResourceClass> crc = resourceClass.getClass();
        for (SWGInventoryWrapper wr : wl)
            if (wr.getResource().rc().isSub(crc))
                wrs.add(wr);

        return wrs;
    }

    /**
     * Helper method which returns a list of inventory wrappers which is
     * filtered by the active filters. The list is what is displayed at the main
     * table. If there is nothing to return or if there is a work in progress an
     * empty list is returned.
     * 
     * @return a list of wrappers for the current view
     */
    private List<SWGInventoryWrapper> wrappersFiltered() {
        if (isWorking || !isGuiCreated) {
            if (wrappers == null) return Collections.emptyList();
            return wrappers;
        }

        if (wrappers == null) {
            // protect the original
            List<SWGInventoryWrapper> wrs = new ArrayList<SWGInventoryWrapper>(
                    wrappersByAssignee());
            wrs = wrappersByResourceClass(wrs);
            wrs = wrappersByFilter(wrs);
            wrs = wrappersSort(wrs);
            wrappers = wrs;
            updateStatBar();
            if (table.isEditing()) table.editingCanceled(null);
        }
        return wrappers;
    }

    /**
     * Helper method which sorts the list of wrappers. If {@link #filter} is a
     * weighed filter <i>and </i> a resource class is specified the list is
     * sorted by the weighed rates of the resources, otherwise the list is
     * sorted by assignees, resource classes, and resource names.
     * 
     * @param wl a list of wrappers
     * @return the sorted list
     */
    private List<SWGInventoryWrapper> wrappersSort(List<SWGInventoryWrapper> wl) {
        Comparator<SWGInventoryWrapper> comp;
        if (filter != null && filter instanceof SWGWeights
                && resourceClass != null)
            comp = new Comparator<SWGInventoryWrapper>() {
                
                final SWGWeightComparator cmp = new SWGWeightComparator(
                        (SWGWeights) filter, resourceClass, true, useJTLcap);

                @Override
                public int compare(
                        SWGInventoryWrapper o1, SWGInventoryWrapper o2) {
                    return cmp.compare(o1.getResource(), o2.getResource());
                }
            };
        else
            comp = new Comparator<SWGInventoryWrapper>() {
                @Override
                public int compare(
                        SWGInventoryWrapper o1, SWGInventoryWrapper o2) {
                    int c = o1.getAssignee().
                            compareToIgnoreCase(o2.getAssignee());
                    return c == 0
                            ? o1.getResource().compareTo(o2.getResource())
                            : c;
                }
            };

        Collections.sort(wl, comp);
        return wl;
    }

    /**
     * Helper method which returns the argument sorted on rating by
     * {@link #filter}.
     * <p>
     * It is assumed that {@link #filter} is a validated to be of type
     * {@link SWGWeights} before this method is called.
     * 
     * @param wl the list of wrappers to filter
     * @return a list of wrappers
     */
    private List<SWGInventoryWrapper> wrappersSortedByWeight(
            List<SWGInventoryWrapper> wl) {

        Comparator<SWGInventoryWrapper> comp =
                new Comparator<SWGInventoryWrapper>() {

                    
                    SWGResourceClass cls = resourceClass;

                    
                    SWGWeights wgt = (SWGWeights) filter;

                    public int compare(SWGInventoryWrapper w1,
                            SWGInventoryWrapper w2) {
                        double d1 = wgt.rate(w1.getResource(), cls, true, false);
                        double d2 = wgt.rate(w2.getResource(), cls, true, false);
                        return Double.compare(d1, d2);
                    }
                };
        Collections.sort(wl, comp);
        return wl;
    }
    
    public static void verified () {
    	if(cPanel != null) {
    		cPanel.setVisible(true);
    	}
    }

    /**
     * The model for the main table of inventory entries.
     * 
     * @author <a href="mailto:simongronlund@gmail.com">Simon Gronlund</a> aka
     *         Chimaera.Zimoon
     */
    final class TableModel extends AbstractTableModel
        implements DecoratedTableModel {

        /**
         * Table column header titles
         */
        // remember isCellEditable if changes here
        private final String[] columnNames = { "Assignee", "Name", "Class",
                "Amount", "CPU", "Value", "ER", "CR", "CD", "DR", "FL", "HR", "MA", "PE", "OQ",
                "SR", "UT", "Rate", "Notes" };

        /**
         * A convenience constant array of stats in game order.
         */
        private final Stat[] gOrder = Stat.gameOrder();

        /**
         * The base string format for galaxy-name and resource name. This format
         * is used for foreign galaxies, on the syntax "(Galaxy)Name".
         */
        private final String gxyfmt = "(%s)%s";

        
        @Override
        public TableCellDecorations getCellDecor(int row, int column,
                Object value) {

            if (value == null || column <= 5 || column >= 18) return null;

            SWGInventoryWrapper wrapper = wrappersFiltered().get(row);
            SWGKnownResource kr = wrapper.getResource();

            if (column <= 16) { // stats capRes.max(j));
                Stat s = gOrder[column - 6];
                int val = kr.stats().value(s);
                int cap = resourceClass != null
                        ? resourceClass.max(s)
                        : kr.rc().max(s);
                return new TableCellDecorations(
                        SWGResourceStatRenderer.getStatBackGround(val, cap),
                        SWGResourceStatRenderer.getStatForeground(val, cap),
                        null, (Object[]) null);
            }

            // column == 17
            double d = ((Double) value).doubleValue();
            return new TableCellDecorations(
                        SWGResourceStatRenderer.getStatBackGround(d),
                        SWGResourceStatRenderer.getStatForeground(d),
                        null, (Object[]) null);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
        	if (columnIndex == 3) return Integer.class;
        	if (columnIndex == 4) return Double.class;
            if (columnIndex >= 5 && columnIndex <= 17) return Integer.class;
            return String.class;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        
        public int getRowCount() {
            if (!isGuiCreated) return 0;
            return wrappersFiltered().size();
        }

        
        public Object getValueAt(int row, int col) {
            int sz = wrappersFiltered().size();
            if (sz <= row || (isWorking && SWGConstants.DEV_DEBUG)) {
                // XXX: remove this debugging whenever (added april 2011)
                IllegalStateException e = new IllegalStateException(
                        "ERROR: size:" + sz + " row:" + row + " " + isWorking +
                                " " + Thread.currentThread());
                SWGAide.printError("SWGInventoryTab:Model:getValueAt", e);
                throw e;
            }
            SWGInventoryWrapper wrapper = wrappersFiltered().get(row);
            SWGKnownResource kr = wrapper.getResource();

            switch (col) {
            case 0:
                return wrapper.getAssignee();
            case 1: {
                SWGCGalaxy g = kr.galaxy();
                return g.equals(recentGalaxy) == false && g != null
                        ? String.format(gxyfmt, g.getName(), kr.getName())
                        : kr.getName();
            }
            case 2:
                return kr.rc().rcName();
            case 3:
                return Long.valueOf(wrapper.getAmount());
            case 4:
            	double cp = Double.valueOf(wrapper.getCPU());
            	if (cp>0) {
            		return cp;
            	} else return null;
            case 5:
            	long amt = Long.valueOf(wrapper.getAmount());
            	double cpu = Double.valueOf(wrapper.getCPU());
            	long result;
            	if (cpu > 0) {
            		result = (long) (amt * cpu);
            	} else return null;
				return result;
            case 6: // fall through
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16: {
                Stat s = gOrder[col - 6];
                int val = kr.stats().value(s);
                return Integer.valueOf(val);
            }
            case 17: { // rate
                if (filter == null) return null;

                SWGWeights weights;
                if (filter.getClass() == SWGWeights.class) {
                    weights = (SWGWeights) filter;
                } else {
                    int[] dummy = new int[Stat.COUNT];
                    for (Stat s : Stat.values()) {
                        dummy[s.i] = filter.value(s);
                    }
                    weights = new SWGWeights(dummy, false);
                }
                if (!weights.isValid())
                    weights.adjust();

                SWGResourceClass cap = resourceClass != null
                        ? resourceClass
                        : kr.rc();

                double rating = weights.rate(kr, cap, true, useJTLcap);
                return Double.valueOf(rating);
            }
            case 18:
                return wrapper.getNotes();
			default:
				return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // remember to fix setValueAt if anything changes here
            if (columnIndex == 3 || columnIndex == 4 || columnIndex == 18) return true;
            return false;
        }

        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            SWGInventoryWrapper wr = wrappersFiltered().get(rowIndex);

            if (columnIndex == 3) {
                try {
                    // XXX: make the cell editor handle error input, it that is
                    // how to do it
                    wr.setAmount(ZNumber.longExc((String) value));
                } catch (Exception e) {
                    Toolkit.getDefaultToolkit().beep();
                }
            } else if (columnIndex == 4) {
            	double cpu = 0.0;
            	if(!value.toString().trim().isEmpty()) {
            		cpu = Double.parseDouble(value.toString());
            	}
            	wr.setCPU(cpu);
            	fireTableCellUpdated(rowIndex, columnIndex+1);
            	actionTotalValue();
            } else if (columnIndex == 18) wr.setNotes((String) value);

            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}

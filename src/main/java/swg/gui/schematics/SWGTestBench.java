package swg.gui.schematics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import swg.SWGAide;
import swg.crafting.SWGWeights;
import swg.crafting.Stat;
import swg.crafting.resources.SWGKnownResource;
import swg.crafting.resources.SWGResourceClass;
import swg.crafting.resources.SWGResourceClassTree;
import swg.crafting.resources.types.SWGMineral;
import swg.crafting.schematics.SWGResourceSlot;
import swg.crafting.schematics.SWGSchematic;
import swg.crafting.schematics.SWGSchematicsManager;
import swg.gui.SWGFrame;
import swg.gui.common.SWGGuiUtils;
import swg.gui.common.SWGJDialog;
import swg.tools.SimplePrefsKeeper;
import swg.tools.SpringUtilities;
import swg.tools.ZNumber;
import swg.tools.ZString;

/**
 * This type is a panel for testing and find combinations of resources for a
 * specified schematic and the combination's "Weighed Average Result" (WAR)
 * &ndash; see <i>Beginners's Guide for Traders</i>, chapter "Power Crafting"
 * for details. This type works as in game, it is enough to meet a stat only
 * once without penalty.
 * <p>
 * This type is operated from The Laboratory: for a specified schematic the user
 * selects resources for the resource slots. For each resource that is added or
 * withdrawn a panel computes and displays the current WAR; empty slots and
 * unfilled stats reduce WAR* and the panel is tinted to alert the user.
 * <p>
 * <b>Usage:</b>
 * <ul>
 * <li>double click a schematic at The Laboratory to open or update this dialog
 * with another schematic, slots are empty</li>
 * <li>double click a resource at The Laboratory to open or update this dialog
 * with another schematic and fill a slot with the resource</li>
 * <li>left click a resource slot at the dialog to select or deselect it</li>
 * <li>right click a slot to empty the it</li>
 * <ul>
 * <li>if a slot is selected and if the resource matches the required resource
 * class it fills that slot and replaces any previous resource; but if its
 * resource class does not match nothing happens</li>
 * <li>if no slot is selected the resource fills <i>the first empty </i> slot
 * that accepts it; resource slots are iterated in the same order they appear in
 * the XML file that is downloaded from swgaide.com</li>
 * </ul>
 * </li> </ul>
 * <p>
 * Currently this type considers only resources and displays their WAR; future
 * versions may display in measurements such as BER, Hit Points, etc., and the
 * possibly consider additional values from components such as tissues or weapon
 * cores.
 * <p>
 * This type may reduce WAR differently than in game, this is yet to be
 * determined and implemented correctly. Currently it simply does not add value
 * weighed from the number of units.
 * 
 * @author <a href="mailto:simongronlund@gmail.com">Simon Gronlund</a> aka
 *         Chimaera.Zimoon
 */
@SuppressWarnings("serial")
final class SWGTestBench extends SWGJDialog {

    /**
     * A constant color for a BAD value relative its cap.
     */
    private static final String BAD = SWGGuiUtils.toHexString(UIManager.getColor("SWG.benchBAD"));

    /**
     * A constant color for a FAIR value relative its cap.
     */
    private static final String FAIR = SWGGuiUtils.toHexString(UIManager.getColor("SWG.benchFAIR"));
    
    /**
     * A constant color for a FAIR value relative its cap.
     */
    private static final String GREAT = SWGGuiUtils.toHexString(UIManager.getColor("SWG.benchGREAT"));

    /**
     * Dimension of fillers for unused slots at GUI.
     */
    private static final Dimension LABEL_DIM = new Dimension(Math.round(76 * SWGGuiUtils.fontMultiplier()), Math.round(96 * SWGGuiUtils.fontMultiplier()));

    /**
     * A constant reference to an instance of this type. The first time a client
     * invokes {@link #update(SWGSchematic, SWGKnownResource)} this variable is
     * set.
     */
    private static SWGTestBench THIS;

    /**
     * A panel for labels displaying experimentation wrappers.
     */
    private final JPanel exps;
    
    /**
     * A panel for displaying ingredients
     */
    private final JPanel ingredients;

    /**
     * A constant string of four &nbsp; for HTML padding.
     */
    private final String NBSP = "&nbsp;&nbsp;&nbsp;&nbsp;";

    /**
     * A list of resource slots. This list must be cleared when the user selects
     * a new schematic and before this test-bench is updated with new slots.
     */
    private final List<RSlot> slots;

    /**
     * A GUI panel for resource slots.
     */
    private final JPanel slotsGrid;

    /**
     * A wrapper for the current schematic that provides merged experimental
     * properties conveyed by a set of {@link SWGExperimentWrapper}.
     */
    private SWGSchematicWrapper wrap;

    /**
     * A list of experiment wrappers for {@link #wrap}; each element is unique
     * in regards of weights. Notice that its resource class has not meaning
     * when it is used in this type.
     */
    private List<SWGExperimentWrapper> wraps;

    /**
     * A flag to use throughout this dialog to account for JTL resource
     * capping adjustments
     */
    private static boolean useJTLcap;

    /**
     * Creates an instance of this dialog. Further interaction via {@code
     * setSchem(SWGSchematic)}.
     */
    private SWGTestBench() {
        super(null, false, null);

        slots = new ArrayList<RSlot>();
        float m = SWGGuiUtils.fontMultiplier();
        int w = Math.round(420 * m);
        int h = Math.round(96 * m);
        Dimension pd = new Dimension(w, h);
        getContentPane().setLayout(new BorderLayout());

        // add content        
        slotsGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        getContentPane().add(slotsGrid, BorderLayout.NORTH);
        slotsGrid.setPreferredSize(pd);
        
        exps = new JPanel(new BorderLayout());
        h = Math.round(305 * m);
        
        JScrollPane jsp = new JScrollPane(exps, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setPreferredSize(new Dimension(w, h));
        getContentPane().add(jsp, BorderLayout.CENTER);
        
        ingredients = new JPanel(new BorderLayout());
        h = Math.round(190 * m);
        ingredients.setPreferredSize(new Dimension(w, h));
        getContentPane().add(ingredients, BorderLayout.SOUTH);
        Border current = ingredients.getBorder();
        Border empty = new EmptyBorder(2, 2, 5, 5);
        if (current == null) {
            ingredients.setBorder(empty);
        } else {
            ingredients.setBorder(new CompoundBorder(empty, current));
        }
        
        // set max size to be screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setMaximumSize(new Dimension(screen.width-20, screen.height-50));
        setMinimumSize(new Dimension(w, Math.round(500 * m)));

        setLocation(true);
        registerHelp(SWGAide.class.getResource(
                "docs/help_schematics_test_bench_en.html"));
    }

    /**
     * Called when the user left-clicks a slot. This method iterates over all
     * slots to ensure they are deselected, and it selects the specified
     * argument, leaving only one selected slot.
     * 
     * @param slot a selected slot
     */
    private void actionSlotSelected(RSlot slot) {
        for (RSlot rs : slots)
            rs.select(slot.equals(rs) && rs.selected());
    }

    /**
     * A helper method to adjust for JTL resource capping rules
     *
     * @param value value of the stat being calculated
     * @param rc the resource class of the object being evaluated
     * @param s the stat of the resource being evaluated
     * @return the adjusted value
     */
    private double adjustForResourceCap(double value, SWGResourceClass rc, Stat s) {
    	if (useJTLcap && rc.isAffectedByJTLcap())
    		rc = SWGMineral.getInstance();
    	
    	return Math.min(value / rc.max(s) * 1000, 1000);
    }
    
    @Override
    protected void close() {
        SimplePrefsKeeper pk = SWGFrame.getPrefsKeeper();
        pk.add("schemTestBenchLocation", getLocation());
    }

    /**
     * Helper method which creates and returns an empty label with minimum size
     * {@link #LABEL_DIM}. The boolean arguments determines if the label should
     * have a preferred size.
     * 
     * @return a label
     */
    private JLabel makeLabel() {
        JLabel l = new JLabel("") {
            
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.width = THIS.getSize().width - 15;
                return d;
            }
        };
        l.setHorizontalAlignment(SwingConstants.LEFT);
        l.setVerticalAlignment(SwingConstants.TOP);
        l.setMinimumSize(LABEL_DIM);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(2, 3, 2, 0)));
        l.setOpaque(true);
        l.setBackground(UIManager.getColor("TextArea.background"));
        return l;
    }

    /**
     * Helper method which resets labels for experimentation wrappers. This
     * method empties {@link #exps} and adds a label for each wrapper, finally
     * it ensures that the list contains at least one label; this method is not
     * invoked if the schematic is {@code null}.
     */
    private void resetExps() {
        wraps = new ArrayList<SWGExperimentWrapper>();
        // find wrappers that have unique weights
        if (wrap != null) {
            for (SWGExperimentWrapper ew : wrap.experiments()) {
                SWGExperimentWrapper exist = null;
                for (SWGExperimentWrapper w : wraps)
                    if (w.weights().equals(ew.weights())) {
                        exist = w;
                        break;
                    }

                if (exist == null) wraps.add(ew);
            }
        }

        exps.setLayout(new SpringLayout());
        exps.removeAll();

        for (int i = 0; i < wraps.size(); ++i) {
            JLabel l = makeLabel();
            l.setFont(SWGGuiUtils.fontPlain());
            exps.add(l);
        }
        if (exps.getComponentCount() < 1) {
            JLabel l = makeLabel();
            l.setText(wrap == null
                    ? "Select a schematic"
                    : "LQ schematic");
            exps.add(l);
        }

        SpringUtilities.makeCompactGrid(exps,
                exps.getComponentCount(), 1, 2, 5, 5, 5);
        exps.revalidate();
        exps.repaint(200);
    }

    
    /**
     * Helper method which rebuilds ingredient list with
     * whatever is currently in the resource slots.
     */
    private void resetIngredients() {
    	//
    	String rez = "";
    	ingredients.setLayout(new BorderLayout());
        ingredients.removeAll();
        int lines = slots.size() + 6;
        SWGGuiUtils.setDim(ingredients, lines, ingredients.getWidth(), 170, false);
        String header = "<h4>Ingredients:</h4>";
        UIManager.getColor("TextArea.background");
        String colors = "<html><div align=\"center\" style=\"font-weight:bold; font-size:95%; text-align:center;margin-left:auto;margin-right:auto;\">&uarr;&uarr;&nbsp;Color Codes: <font color='"+ BAD + "'>\"Bad\" " +
        		"</font>&nbsp;<font color='"+ FAIR + "'>\"Good\" " +
        		"</font>&nbsp;<font color='" + GREAT + "'>\"Great\"</font>&nbsp;&uarr;&uarr;</div>";
        for (Component c : slotsGrid.getComponents()) {
            if (c instanceof JLabel) {
               String jl = ((JLabel)c).getToolTipText();
            	   if (jl != null && !jl.isEmpty()) {
            		   jl = jl.replace("[", "<strong>");
                       jl = jl.replace("]", "</strong>");
            		   rez = rez + "<h4 style=\"font-weight:normal; font-size:97%; margin:0;\">" + jl + "</h4>";
            	   }
            }
        }
        int tc = 0;
        int tot = 0;
        for (int i = 0; i < slots.size(); i++) {
        	RSlot sl = slots.get(i);
        	tot += sl.getUnits();
        	if(sl.getCPU()>0 && sl.filled()) tc += sl.getUnits() * sl.getCPU();
        }
        String pc;
        if(tc>0) {
        	pc = ZNumber.asText(tc);
        } else {
        	pc = "N/A";
        }
        String totals = "<br>Total units: <strong>" + ZNumber.asText(tot) + "</strong> :: Potential Cost: <strong>" + pc + "</strong>";
        String text = colors + header + "<div style=\"font-size:97%;\">" + rez + totals + "</div>";
        
        JEditorPane iL = new JEditorPane();
        HTMLEditorKit kit = new HTMLEditorKit();
        iL.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        Style style = styleSheet.getStyle("body");
        StyleConstants.setFontSize(style, SWGGuiUtils.fontPlain().getSize());
        StyleConstants.setFontFamily(style, SWGGuiUtils.fontPlain().getFamily());
        iL.setText("<html>" + text + "</html>");
        iL.setEditable(false);
        iL.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(2, 3, 2, 0)));
        iL.setOpaque(true);
        iL.setBackground(UIManager.getColor("TextArea.background"));
        iL.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu p = new JPopupMenu();
                    Action copy = new DefaultEditorKit.CopyAction();
                    copy.putValue(Action.NAME, "Copy text");
                    copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
                    if (iL.getSelectedText() != null) { // See if they selected something 
                    	p.add( copy );
                        //p.addSeparator();
                        // Do work with String s
                    }
                    p.show(iL, e.getX(), e.getY());
                }
            }
        });
        
        ingredients.add(iL);
        ingredients.revalidate();
        ingredients.repaint(300L);
        
    }
    /**
     * Helper method which resets and updates the slots for the current
     * schematic. This method empties {@link #slots} and refills it with the
     * number of resource slots for the current schematic; this method is not
     * invoked if the schematic is {@code null}.
     */
    private void resetSlots() {
        slots.clear();
        if (wrap != null) {
            for (SWGResourceSlot s : wrap.schem().getResourceSlots()) {
            	slots.add(new RSlot(s.getResourceClass(), s.getUnits()));
            }
        }
        slotsGrid.setLayout(null);
        slotsGrid.removeAll();
        slotsGrid.setLayout(new SpringLayout());

        for (RSlot rs : slots) {
        	slotsGrid.add(rs);
        }

        int rows = 2;
        int defcols = 5;
        int cols = Math.max(  ((slots.size() ) ), defcols);
        while (slotsGrid.getComponentCount() < cols * rows
                || slotsGrid.getComponentCount() % cols > 0) {
            JLabel l = new JLabel("");
            l.setMinimumSize(LABEL_DIM);
            l.setPreferredSize(LABEL_DIM);
            slotsGrid.add(l);
        }

        rows = Math.max(((slots.size() + cols - 1) / cols), rows);
        SpringUtilities.makeCompactGrid(slotsGrid, rows, cols, 2, 2, 3, 3);
        int diff = cols - defcols;
    	int nh = Math.min( ((diff * 20) + 155), 230);
    	float m = SWGGuiUtils.fontMultiplier();
    	nh = Math.round(nh * m);
    	ingredients.setPreferredSize(new Dimension(400, nh));
        int nwidth = cols * Math.round(79 * m);
        Dimension sp = slotsGrid.getPreferredSize();
        slotsGrid.setPreferredSize(new Dimension(nwidth, sp.height));
        slotsGrid.revalidate();
        slotsGrid.repaint(200);
    }

    /**
     * Helper method which sets the location and size of this dialog and
     * possibly adjusts the location so that it is on-screen. If the argument is
     * {@code true} this method obtains values from SWGAide's preference keeper.
     * Otherwise it obtains the current size and location and adjusts only if
     * necessary.
     * 
     * @param init {@code true} if invoked from the constructor
     */
    private void setLocation(boolean init) {
    	// set a preferred dimension for the dialogue window.
    	float m = SWGGuiUtils.fontMultiplier();
    	int w = Math.round(453 * m);
    	int h = Math.round(630 * m);
    	Dimension pd = new Dimension(w, h);
    	Dimension mx = getMaximumSize();
    	Dimension cs = getSize();
    	if (cs.width > mx.width) {
    		w = mx.width;
    	} else if (cs.width > pd.width) {
    		w = cs.width;
    	} else {
    		w = pd.width;
    	}
    	if (cs.height > mx.height) {
    		h = mx.height;
    	} else if (cs.height > pd.height) {
    		h = cs.height;
    	} else {
    		h = pd.height;
    	}
    	setSize(w, h);
    	Point p;
        if (init) {
            SimplePrefsKeeper pk = SWGFrame.getPrefsKeeper();
            p = (Point) pk.get("schemTestBenchLocation",
                    new Point(SWGAide.frame().getWidth(), 0));
        } else {
            p = getLocation();
        }
        Point pp = SWGGuiUtils.ensureOnScreen(p, getSize());
        if (!p.equals(pp)) {
            setLocation(pp);
        } else {
        	setLocation(p);
        }
    }

    /**
     * Helper method which sets the specified resource to a suitable slot. This
     * method does one of...
     * <ul>
     * <li>if the argument is {@code null} this method does nothing</li>
     * <li>if a slot is selected...
     * <ul>
     * <li>if the resource matches required class it fills the slot and possibly
     * replaces a previous resource</li>
     * <li>else this method does nothing</li>
     * </ul>
     * </li>
     * <li>if no slot is selected...
     * <ul>
     * <li>the resource fills the first empty slot that accepts it</li>
     * <li>if no not-filled slot accepts the resource the invocation is void</li>
     * </ul>
     * </li>
     * </ul>
     * This method does not invoke any method to update this dialog.
     * 
     * @param kr a resource, or {@code null}
     */
    private void setResource(SWGKnownResource kr, double cpu) {
        if (kr == null) return;

        RSlot slot = null;
        for (RSlot s : slots)
            if (s.selected()) slot = s;

        boolean fill = false;
        if (slot == null) {
            for (RSlot s : slots)
                if (!s.filled() && kr.rc().isSub(s.rc)) {
                    slot = s;
                    fill = true;
                    break;
                }
        } else if (kr.rc().isSub(slot.rc))
            fill = true;

        if (slot != null && fill)
            slot.fill(kr, cpu);
    }

    /**
     * Helper method for {@link #update(SWGSchematic, SWGKnownResource)}.
     * 
     * @param s a schematic, or {@code null}
     * @param kr a resource, or {@code null}
     */
    private void updat(SWGSchematic s, SWGKnownResource kr, double cpu) {
        if (s != null) {
            if (wrap == null || !s.equals(wrap.schem())) {
                // use schemexp-wrapper and unified exp-group/lines
                // because selecting a schematic always first selects the
                // default wrapper it should always be initialized with
                // exp-groups/lines
                wrap = SWGSchemController.wrapperDefault(s);
                setTitle("SWGAide-Unity - Test Bench: " + s.getName());

                resetSlots();
                resetExps();
                resetIngredients();
            }

            setResource(kr, cpu);
        } else {
            setTitle("Test Bench");
            wrap = null;
            resetSlots();
            resetExps();
            resetIngredients();
        }
        updateEWAR();
        resetIngredients();

        pack();
        setVisible(true);
        int h = getContentPane().getHeight() + getInsets().top + getInsets().bottom;
        setSize(getWidth(), h);
        setLocation(false);
    }

    /**
     * Helper method which updates the labels for experimentation wrapper. This
     * method computes WAR for each wrapper determined by the resource slots
     * with their weights relative number of units and possible resources.
     */
    private void updateEWAR() {
        // wraps is empty if wrap == null
        for (int i = 0; i < wraps.size(); ++i) {
            SWGExperimentWrapper ew = wraps.get(i);
            JLabel grp = (JLabel) exps.getComponent(i);
            grp.setText(updateExp(ew));
        }
    }

    /**
     * Helper method which returns a string for the specified experimental
     * wrapper. The string reads details and WAR for the wrapper.
     * 
     * @param ew an experimental wrapper
     * @return a string object
     */
    private String updateExp(SWGExperimentWrapper ew) {
        // wraps is empty if wrap == null hence no invocation
        ZString z = new ZString();
        z.app("<html><b>").app(ew.getName());
        if (!SWGSchematicsManager.isQuality(wrap.schem()))
            z.app(" &mdash; LQ schematic").app("</b>");
        else
            updateExpWar(ew, z.app("</b>"));

        return z.app("</html>").toString();
    }

    /**
     * Helper method which appends a suitable color to the string object. This
     * method adds the HTML string {@code <font color="COLOR">} where color is
     * determined by the value.
     * 
     * @param d a value in the range [0 1000.0]
     * @param z a string object
     */
    private void updateExpColor(double d, ZString z) {
        z.app("<font color=");
        if (d < SWGGuiUtils.statLimits[1] * 1000)
            z.app(BAD);
        else if (d < SWGGuiUtils.statLimits[2] * 1000)
            z.app(FAIR);
        else
            z.app(GREAT);
        z.app(">");
    }

    /**
     * Helper method which updates the string object for the specified wrapper.
     * This method invoke helper methods to compute the weighed WAR per resource
     * stat, which is added each line by itelf; for each resource slot it adds
     * colored information about the weighed and adjusted intermediate value to
     * the line; finally, a colored line as added for the stats and for the
     * total "WAR Adjusted for Caps".
     * 
     * @param ew an experimental wrapper
     * @param z a string object
     */
    private void updateExpWar(SWGExperimentWrapper ew, ZString z) {
        // *** preparations *** //
        int stats = 0; // number of used stats
        double[] wd = new double[Stat.COUNT];
        SWGWeights wi = ew.weights();
        for (Stat s : Stat.values())
            if (wi.value(s) > 0) {
                wd[s.i] = wi.value(s);
                ++stats;
            }

        // adjust weights for 100.00%
        double d = 1.0 / wi.sum();
        for (int i = 0; i < wd.length; ++i)
            if (wd[i] > 0) wd[i] *= d;

        // *** compute *** //
        // iterate over stats

        z.app(NBSP).app("<table border=0  cellspacing=1 cellpadding=0>");
        double[] statsWAR = new double[stats];
        stats = 0;
        for (Stat s : Stat.values())
            if (wd[s.i] > 0) {
                statsWAR[stats] = updateExpWarStat(s, wi.value(s), wd[s.i], z);
                ++stats;
            }
        z.app("</table>");

        // add final line for wrapper
        double war = 0;
        z.app("<b>Result</b>&nbsp;=&nbsp;");
        for (int i = 0; i < statsWAR.length;) {
            double sd = statsWAR[i];
            war += sd;
            z.app(ZNumber.asText(sd, 1, 2));

            ++i;
            if (i < statsWAR.length) z.app(" + ");
        }
        z.app("&nbsp;&nbsp;=&nbsp;&nbsp;<b>");
        updateExpColor(war, z);
        z.app(ZNumber.asText(war, 1, 4)).app("</b>");
    }

    /**
     * Helper method which computes and returns "WAR Adjusted for Caps" for the
     * specified stat in the range {@code [0.0 1000]}. This method updates the
     * string object with information, colored intermediate values for the
     * resource slots that were used, and the WAR for the stat.
     * <p>
     * Notice that the invoker must supply an adjusted weight so that the sum of
     * its weights is 100.0; in SWG integer weights can read 33 and 66 which
     * sums up to 99 which is inaccurate.
     * 
     * @param s a stat
     * @param wi a weight
     * @param wd an adjusted weight, ranging [0.0 100.0]
     * @param z a string object
     * @return intermediate WAR adjusted for caps
     */
    private double updateExpWarStat(Stat s, int wi, double wd, ZString z) {
        z.app("<tr><td>&nbsp;&nbsp;</td><td>");
        z.app(s.name()).app("</td><td align=\"right\">&nbsp;");
        z.app(wi).app("%:&nbsp;</td>");

        double dividend = 0;
        double divisor = 0;

        int frac = slots.size() <= 4
                ? 2
                : slots.size() > 7
                        ? 0
                        : 1;

        for (Iterator<RSlot> iter = slots.iterator(); iter.hasNext();) {
            RSlot rs = iter.next();
            if (rs.filled()) {
                SWGKnownResource kr = rs.kr();
                double v = kr.stats().value(s);
                if (v > 0) {
                    double adj4cap = adjustForResourceCap(v, rs.rc, s);
                    dividend += rs.units * adj4cap;
                    divisor += rs.units;
                }
            } else {
                if (rs.rc.has(s)) divisor += rs.units;
            }
        }
        for (Iterator<RSlot> iter = slots.iterator(); iter.hasNext();) {
            RSlot rs = iter.next();
            z.app("<td align=\"right\"><b>");
            if (rs.filled()) {
                SWGKnownResource kr = rs.kr();
                double v = kr.stats().value(s);
                if (v > 0) {
                    double adj4cap = adjustForResourceCap(v, rs.rc, s);
                    double wg = rs.units / divisor;
                    upStatText(adj4cap * wd * wg, adj4cap, z, frac);
                }
            } else {
                upStatText(0.0, 0.0, z, frac);
            }
            z.app("</b></td>");
            if (iter.hasNext()) z.app("<td>+&nbsp;</td>");
        }

        if (divisor <= 0) {
            z.app("= <font color=\"red\"><b>No values</b></font></tr>");
            return 0.0;
        }

        // else
        double ret = dividend / divisor;

        z.app("=&nbsp;<b>");
        upStatText(ret * wd, ret, z, frac);
        z.app("</b></td></tr>");

        return ret * wd;
    }

    /**
     * Helper method which updates the specified string object for the argumnts.
     * The {@code adj4caps} determines which color to tint the value with, using
     * the Good and Great limits that the user has setup at the Resources panel.
     * 
     * @param v the weighed resource value
     * @param adj4cap v adjusted for caps
     * @param z a string object
     * @param frac number of decimals
     */
    private void upStatText(double v, double adj4cap, ZString z, int frac) {
        updateExpColor(adj4cap, z);
        z.app(ZNumber.asText(v, 1, frac)).app("</font> ");
    }

    /**
     * Updates this dialog for the specified arguments. If the schematic does
     * not equal a previous schematic this method resets and updates itself.
     * otherwise this method updates a resource slot determined by the rules
     * outlined in the class comment. If the resource is {@code null} that
     * argument is ignored and possibly this method does nothing. If the
     * schematic is {@code null} this method does nothing.
     * <p>
     * If this is the first invokation for the session this method also creates
     * an instance of this dialog and sets internal states.
     * <p>
     * Misc: because this dialog is only used from {@link SWGLaboratoryTab} only
     * resources that suits the schematic can be supplied as an argument.
     * Furthermore, this method must only be invoked on the AWT event thread,
     * hence it is not synchronized.
     * 
     * @param s a schematic, or {@code null}
     * @param kr a resource, or {@code null}
     * @param useJTL a flag to help determine if the experimentation values should
     *              adjust for JTL resource rules
     */
    static void update(SWGSchematic s, SWGKnownResource kr, Double cpu, boolean useJTL) {
        try {
            if (THIS == null) THIS = new SWGTestBench();
            useJTLcap = useJTL;
            THIS.updat(s, kr, cpu);
        } catch (Throwable e) {
            SWGAide.printError("SWGTestBench:update", e);
        }
    }

    /**
     * This type displays a resource slot and provides some logic that relates
     * to selecting, replacing, and removing resources from the slot. The
     * members of this type model the statistics of a resource slot at the
     * crafting tool or at a schematic at the datapad.
     * <p>
     * This type is used in a local scope, thus final members are scope visible.
     * 
     * @author <a href="mailto:simongronlund@gmail.com">Simon Gronlund</a> aka
     *         Chimaera.Zimoon
     */
    private final class RSlot extends JLabel {

        /**
         * The middle component in the compund border.
         */
        private final Border be =
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

        /**
         * A border for a not-filled resource slot.
         */
        private final Border bfn = new LineBorder(Color.RED, 2);

        /**
         * A border for a filled resource slot.
         */
        private final Border bfy = new LineBorder(new Color(0, 191, 0), 2);

        /**
         * A border for a not-selected resource slot.
         */
        private final Border bsn =
                BorderFactory.createEmptyBorder(2, 2, 2, 2);

        /**
         * A border for a selected resource slot.
         */
        private final Border bsy = new LineBorder(Color.BLUE, 2);

        /**
         * The resource class this slot calls for.
         */
        final SWGResourceClass rc;

        /**
         * A resource that fills this slot, or {@code null} if it is empty.
         */
        private SWGKnownResource res;
        
        private double cpu;

        /**
         * {@code true} if this slot is selected. Only one border at a time can
         * be selected, the client must ensure that.
         */
        private boolean selected;

        /**
         * The size of this slot.
         */
        final int units;

        /**
         * Creates an instance of this type for the specified members. The new
         * instance is set in not-filled and not-selected mode.
         * <p>
         * <b>Notice</b>: When the user selects a new schematic and before this
         * constructor is invoked for new slots that list must be emptied.
         * 
         * @param rc a resource class
         * @param units number of units
         */
        
        RSlot(SWGResourceClass rc, int units) {
            super(ZNumber.asText(units, true, true),
                    SWGResourceClassTree.icon(rc), SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setHorizontalTextPosition(SwingConstants.CENTER);
            
            //SWGAide.printDebug("tbench", 1, rc.rcName());

            this.rc = rc;
            this.units = units;
            
            // scaling the icons up with the fonts.
            int iw = SWGResourceClassTree.icon(rc).getIconWidth();
            int ih = SWGResourceClassTree.icon(rc).getIconHeight();
            /* TODO add a way to compensate if a user has used test bench already during session
             * then changes font size and uses the bench again without restarting aide.
             * Since some icons would have already been resized they will no longer be 64x64 and not hit
             * Could just let it re-scale all the time but that would be expensive.
             */
            // only scale them once per session. Original image size is 64x64 so....
            if (iw == 64) {
	            float m = SWGGuiUtils.fontMultiplier();
	            iw = Math.round(iw * m);
	            ih = Math.round(ih * m);
	            Image image = SWGResourceClassTree.icon(rc).getImage(); // transform it 
	            Image newimg = image.getScaledInstance(iw, ih,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
	            SWGResourceClassTree.icon(rc).setImage(newimg);  // transform it back
            }

            fill(null, 0);
            select(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        selected = !selected; // toggle before call
                        actionSlotSelected(RSlot.this);
                    } else if (e.getButton() == MouseEvent.BUTTON3) {
                        fill(null,0);
                        updateEWAR();
                        resetIngredients();
                    }
                }
            });
        }

        /**
         * Helper method which sets an appropriate border determined by
         * {@link #filled()} and {@link #selected}.
         */
        private void border() {
            Border outer = BorderFactory.createCompoundBorder(
                    (selected
                            ? bsy
                            : bsn), be);
            Border b = BorderFactory.createCompoundBorder(outer,
                    (filled()
                            ? bfy
                            : bfn));
            setBorder(b);
        }

        /**
         * Fills this instance with the specified resource, or empties it.
         * 
         * @param kr a resource, or {@code null} to empty this slot
         */
        void fill(SWGKnownResource kr, double cost) {
            res = kr;
            cpu = cost;
            border();
            ZString z = ZString.fz("%s units of %s",
                    ZNumber.asText(units, true, true), rc.rcName());
            if (filled()) z.app("  [").app(kr().getName()).app(']');
            setToolTipText(z.toString());
        }

        /**
         * Returns {@code true} if this slot is filled.
         * 
         * @return {@code true} if filled
         */
        boolean filled() {
            return kr() != null;
        }
        
        int getUnits() {
        	return units;
        }
        
        double getCPU() {
        	return cpu;
        }

        /**
         * Returns the resource that fills this slot, or {@code null}.
         * 
         * @return a resource, or {@code null}
         */
        SWGKnownResource kr() {
            return res;
        }

        /**
         * Sets the select state of this instance. Only one slot at a time can
         * be selected, the client must ensure that.
         * 
         * @param s {@code true} to select this resource slot
         */
        void select(boolean s) {
            selected = s;
            border();
            repaint(200L);
        }

        /**
         * Returns {@code true} if this slot is selected.
         * 
         * @return {@code true} if selected
         */
        boolean selected() {
            return selected;
        }

        @Override
        public String toString() {
            ZString z = new ZString("RSlot[").app(units).app(' ');
            return z.app(rc.rcName()).app(']').toString();
        }
    }
}

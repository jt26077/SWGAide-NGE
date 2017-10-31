package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Extrusive Ore"
 *
 * <b>WARNING:</b>
 * This class is generated by SWGResourceClassGenerator.
 * Do not manually modify this class as your changes are
 * erased when the classes are re-generated.
 *
 * @author Steven M. Doyle <shadow@triwizard.net>
 * @author <a href="mailto:simongronlund@gmail.com">Simon Gronlund</a>
 * aka Chimaera.Zimoon
 */
@SuppressWarnings("all")
public class SWGExtrusiveOre extends SWGIgneousOre {

  private static final long serialVersionUID = 1233504L;

  private static final int[] minStats = {0, 200, 300, 0, 0, 400, 1, 1, 0, 400, 400};
  private static final int[] maxStats = {0, 1000, 1000, 0, 0, 1000, 600, 1000, 0, 1000, 1000};

  private static final SWGExtrusiveOre INSTANCE = new SWGExtrusiveOre();

  SWGExtrusiveOre() { super(); }

  public static SWGExtrusiveOre getInstance() { return INSTANCE; }

  public int expectedStats() { return 7; }
  public int sortIndex() { return 699; }
  public int rcID() { return 155; }
  public String rcName() { return "Extrusive Ore";}
  public String rcToken() { return "ext";}
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

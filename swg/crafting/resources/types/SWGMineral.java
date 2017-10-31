package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Mineral"
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
public class SWGMineral extends SWGInorganic {

  private static final long serialVersionUID = 71175L;

  private static final int[] minStats = {1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1};
  private static final int[] maxStats = {1000, 1000, 1000, 1000, 0, 1000, 1000, 1000, 1000, 1000, 1000};

  private static final SWGMineral INSTANCE = new SWGMineral();

  SWGMineral() { super(); }

  public static SWGMineral getInstance() { return INSTANCE; }

  public int expectedStats() { return 10; }
  public int sortIndex() { return 613; }
  public int rcID() { return 8; }
  public String rcName() { return "Mineral";}
  public String rcToken() { return "min";}
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Flowers"
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
public class SWGFlowers extends SWGFruit {

  private static final long serialVersionUID = 3233975L;

  private static final int[] minStats = {0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0};
  private static final int[] maxStats = {0, 0, 1000, 0, 1000, 0, 0, 1000, 700, 0, 0};

  private static final SWGFlowers INSTANCE = new SWGFlowers();

  SWGFlowers() { super(); }

  public static SWGFlowers getInstance() { return INSTANCE; }

  public int expectedStats() { return 4; }
  public int sortIndex() { return 513; }
  public int rcID() { return 408; }
  public String rcName() { return "Flowers";}
  public String rcToken() { return "flr";}
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

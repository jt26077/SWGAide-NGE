package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Dathomirian Leathery Hide"
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
public final class SWGDathomirianLeatheryHide extends SWGLeatheryHide {

  private static final long serialVersionUID = 1557691L;

  private static final int[] minStats = {0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1};
  private static final int[] maxStats = {0, 0, 1000, 0, 0, 0, 1000, 1000, 0, 1000, 1000};

  private static final SWGDathomirianLeatheryHide INSTANCE = new SWGDathomirianLeatheryHide();

  SWGDathomirianLeatheryHide() { super(); }

  public static SWGDathomirianLeatheryHide getInstance() { return INSTANCE; }

  public int expectedStats() { return 5; }
  public int sortIndex() { return 295; }
  public int rcID() { return 196; }
  public String rcName() { return "Dathomirian Leathery Hide";}
  public String rcToken() { return "dalhd";}
  public boolean isHarvested() { return true; }
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

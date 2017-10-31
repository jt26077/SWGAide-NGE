package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Conductive Borcarbitic Copper"
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
public final class SWGConductiveBorcarbiticCopper extends SWGCopper {

  private static final long serialVersionUID = 2364205L;

  private static final int[] minStats = {1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1};
  private static final int[] maxStats = {1000, 1000, 1000, 0, 0, 1000, 1000, 1000, 0, 1000, 1000};

  private static final SWGConductiveBorcarbiticCopper INSTANCE = new SWGConductiveBorcarbiticCopper();

  SWGConductiveBorcarbiticCopper() { super(); }

  public static SWGConductiveBorcarbiticCopper getInstance() { return INSTANCE; }

  public int expectedStats() { return 8; }
  public int sortIndex() { return 693; }
  public int rcID() { return 298; }
  public String rcName() { return "Conductive Borcarbitic Copper";}
  public String rcToken() { return "cbcpr";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public boolean  isJTL() { return true; }

  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

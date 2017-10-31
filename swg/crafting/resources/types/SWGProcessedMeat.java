package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Processed Meat"
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
public final class SWGProcessedMeat extends SWGMeat {

  private static final long serialVersionUID = 6380961L;

  private static final int[] minStats = {0, 0, 200, 0, 200, 0, 0, 200, 200, 0, 0};
  private static final int[] maxStats = {0, 0, 200, 0, 200, 0, 0, 200, 200, 0, 0};

  private static final SWGProcessedMeat INSTANCE = new SWGProcessedMeat();

  SWGProcessedMeat() { super(); }

  public static SWGProcessedMeat getInstance() { return INSTANCE; }

  public int expectedStats() { return 4; }
  public int sortIndex() { return 76; }
  public int rcID() { return 806; }
  public String rcName() { return "Processed Meat";}
  public String rcToken() { return "pmet";}
  public boolean isSpaceOrRecycled()  { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

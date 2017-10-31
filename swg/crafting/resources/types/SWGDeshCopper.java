package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Desh Copper"
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
public final class SWGDeshCopper extends SWGCopper {

  private static final long serialVersionUID = 2372112L;

  private static final int[] minStats = {500, 1, 1, 0, 0, 200, 500, 1, 0, 300, 300};
  private static final int[] maxStats = {572, 116, 102, 0, 0, 265, 572, 1000, 0, 372, 372};

  private static final SWGDeshCopper INSTANCE = new SWGDeshCopper();

  SWGDeshCopper() { super(); }

  public static SWGDeshCopper getInstance() { return INSTANCE; }

  public int expectedStats() { return 8; }
  public int sortIndex() { return 684; }
  public int rcID() { return 299; }
  public String rcName() { return "Desh Copper";}
  public String rcToken() { return "decpr";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

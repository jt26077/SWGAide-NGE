package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Mustafarian Extrusive Ore"
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
public final class SWGMustafarianExtrusiveOre extends SWGExtrusiveOre {

  private static final long serialVersionUID = 4870724L;

  private static final int[] minStats = {0, 896, 909, 0, 0, 922, 550, 500, 0, 922, 922};
  private static final int[] maxStats = {0, 1000, 1000, 0, 0, 1000, 600, 1000, 0, 1000, 1000};

  private static final SWGMustafarianExtrusiveOre INSTANCE = new SWGMustafarianExtrusiveOre();

  SWGMustafarianExtrusiveOre() { super(); }

  public static SWGMustafarianExtrusiveOre getInstance() { return INSTANCE; }

  public int expectedStats() { return 7; }
  public int sortIndex() { return 710; }
  public int rcID() { return 615; }
  public String rcName() { return "Mustafarian Extrusive Ore";}
  public String rcToken() { return "muext";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

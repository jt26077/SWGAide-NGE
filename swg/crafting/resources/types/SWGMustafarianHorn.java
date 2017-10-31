package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Mustafarian Horn"
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
public final class SWGMustafarianHorn extends SWGHorn {

  private static final long serialVersionUID = 5392586L;

  private static final int[] minStats = {0, 0, 200, 0, 0, 0, 1, 1, 0, 300, 1};
  private static final int[] maxStats = {0, 0, 1000, 0, 0, 0, 500, 1000, 0, 700, 500};

  private static final SWGMustafarianHorn INSTANCE = new SWGMustafarianHorn();

  SWGMustafarianHorn() { super(); }

  public static SWGMustafarianHorn getInstance() { return INSTANCE; }

  public int expectedStats() { return 5; }
  public int sortIndex() { return 263; }
  public int rcID() { return 681; }
  public String rcName() { return "Mustafarian Horn";}
  public String rcToken() { return "muhrn";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

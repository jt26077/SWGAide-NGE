package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Kelsh Copper"
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
public final class SWGKelshCopper extends SWGCopper {

  private static final long serialVersionUID = 2387926L;

  private static final int[] minStats = {761, 418, 366, 0, 0, 435, 761, 1, 0, 561, 561};
  private static final int[] maxStats = {850, 560, 490, 0, 0, 515, 850, 1000, 0, 650, 650};

  private static final SWGKelshCopper INSTANCE = new SWGKelshCopper();

  SWGKelshCopper() { super(); }

  public static SWGKelshCopper getInstance() { return INSTANCE; }

  public int expectedStats() { return 8; }
  public int sortIndex() { return 689; }
  public int rcID() { return 301; }
  public String rcName() { return "Kelsh Copper";}
  public String rcToken() { return "kecpr";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

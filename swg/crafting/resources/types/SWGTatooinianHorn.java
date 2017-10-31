package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Tatooinian Horn"
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
public final class SWGTatooinianHorn extends SWGHorn {

  private static final long serialVersionUID = 5416307L;

  private static final int[] minStats = {0, 0, 200, 0, 0, 0, 1, 1, 0, 300, 1};
  private static final int[] maxStats = {0, 0, 1000, 0, 0, 0, 500, 1000, 0, 700, 500};

  private static final SWGTatooinianHorn INSTANCE = new SWGTatooinianHorn();

  SWGTatooinianHorn() { super(); }

  public static SWGTatooinianHorn getInstance() { return INSTANCE; }

  public int expectedStats() { return 5; }
  public int sortIndex() { return 260; }
  public int rcID() { return 684; }
  public String rcName() { return "Tatooinian Horn";}
  public String rcToken() { return "tahrn";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

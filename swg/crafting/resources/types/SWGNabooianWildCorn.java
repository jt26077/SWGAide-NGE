package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Nabooian Wild Corn"
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
public final class SWGNabooianWildCorn extends SWGWildCorn {

  private static final long serialVersionUID = 3415836L;

  private static final int[] minStats = {0, 0, 1, 0, 300, 0, 0, 1, 1, 0, 0};
  private static final int[] maxStats = {0, 0, 1000, 0, 1000, 0, 0, 1000, 700, 0, 0};

  private static final SWGNabooianWildCorn INSTANCE = new SWGNabooianWildCorn();

  SWGNabooianWildCorn() { super(); }

  public static SWGNabooianWildCorn getInstance() { return INSTANCE; }

  public int expectedStats() { return 4; }
  public int sortIndex() { return 342; }
  public int rcID() { return 431; }
  public String rcName() { return "Nabooian Wild Corn";}
  public String rcToken() { return "nawcn";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

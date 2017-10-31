package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Yavinian Wild Rice"
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
public final class SWGYavinianWildRice extends SWGWildRice {

  private static final long serialVersionUID = 3763744L;

  private static final int[] minStats = {0, 0, 1, 0, 300, 0, 0, 1, 1, 0, 0};
  private static final int[] maxStats = {0, 0, 1000, 0, 1000, 0, 0, 1000, 700, 0, 0};

  private static final SWGYavinianWildRice INSTANCE = new SWGYavinianWildRice();

  SWGYavinianWildRice() { super(); }

  public static SWGYavinianWildRice getInstance() { return INSTANCE; }

  public int expectedStats() { return 4; }
  public int sortIndex() { return 373; }
  public int rcID() { return 475; }
  public String rcName() { return "Yavinian Wild Rice";}
  public String rcToken() { return "yawrc";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

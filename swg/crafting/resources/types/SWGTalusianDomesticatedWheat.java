package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Talusian Domesticated Wheat"
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
public final class SWGTalusianDomesticatedWheat extends SWGDomesticatedWheat {

  private static final long serialVersionUID = 3827000L;

  private static final int[] minStats = {0, 0, 1, 0, 1, 0, 0, 1, 300, 0, 0};
  private static final int[] maxStats = {0, 0, 1000, 0, 700, 0, 0, 1000, 1000, 0, 0};

  private static final SWGTalusianDomesticatedWheat INSTANCE = new SWGTalusianDomesticatedWheat();

  SWGTalusianDomesticatedWheat() { super(); }

  public static SWGTalusianDomesticatedWheat getInstance() { return INSTANCE; }

  public int expectedStats() { return 4; }
  public int sortIndex() { return 412; }
  public int rcID() { return 483; }
  public String rcName() { return "Talusian Domesticated Wheat";}
  public String rcToken() { return "tudwt";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

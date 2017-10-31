package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Endorian Domesticated Wheat"
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
public final class SWGEndorianDomesticatedWheat extends SWGDomesticatedWheat {

  private static final long serialVersionUID = 3795372L;

  private static final int[] minStats = {0, 0, 1, 0, 1, 0, 0, 1, 300, 0, 0};
  private static final int[] maxStats = {0, 0, 1000, 0, 700, 0, 0, 1000, 1000, 0, 0};

  private static final SWGEndorianDomesticatedWheat INSTANCE = new SWGEndorianDomesticatedWheat();

  SWGEndorianDomesticatedWheat() { super(); }

  public static SWGEndorianDomesticatedWheat getInstance() { return INSTANCE; }

  public int expectedStats() { return 4; }
  public int sortIndex() { return 408; }
  public int rcID() { return 479; }
  public String rcName() { return "Endorian Domesticated Wheat";}
  public String rcToken() { return "endwt";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

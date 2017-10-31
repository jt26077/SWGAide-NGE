package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Unknown Liquid Petrochem Fuel"
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
public final class SWGUnknownLiquidPetrochemFuel extends SWGLiquidPetrochemFuel {

  private static final long serialVersionUID = 537688L;

  private static final int[] minStats = {0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0};
  private static final int[] maxStats = {0, 0, 1000, 0, 0, 0, 0, 1000, 1000, 0, 0};

  private static final SWGUnknownLiquidPetrochemFuel INSTANCE = new SWGUnknownLiquidPetrochemFuel();

  SWGUnknownLiquidPetrochemFuel() { super(); }

  public static SWGUnknownLiquidPetrochemFuel getInstance() { return INSTANCE; }

  public int expectedStats() { return 3; }
  public int sortIndex() { return 572; }
  public int rcID() { return 67; }
  public String rcName() { return "Unknown Liquid Petrochem Fuel";}
  public String rcToken() { return "upf";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}
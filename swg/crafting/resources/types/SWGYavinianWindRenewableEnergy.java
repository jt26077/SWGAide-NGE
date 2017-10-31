package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Yavinian Wind Renewable Energy"
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
public final class SWGYavinianWindRenewableEnergy extends SWGWindEnergy {

  private static final long serialVersionUID = 925131L;

  private static final int[] minStats = {0, 0, 0, 0, 0, 0, 0, 500, 1, 0, 0};
  private static final int[] maxStats = {0, 0, 0, 0, 0, 0, 0, 500, 400, 0, 0};

  private static final SWGYavinianWindRenewableEnergy INSTANCE = new SWGYavinianWindRenewableEnergy();

  SWGYavinianWindRenewableEnergy() { super(); }

  public static SWGYavinianWindRenewableEnergy getInstance() { return INSTANCE; }

  public int expectedStats() { return 2; }
  public int sortIndex() { return 28; }
  public int rcID() { return 116; }
  public String rcName() { return "Yavinian Wind Renewable Energy";}
  public String rcToken() { return "yaweg";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

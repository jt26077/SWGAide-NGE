package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Weak Geothermal Renewable Energy"
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
public final class SWGWeakGeothermalRenewableEnergy extends SWGGeothermalEnergy {

  private static final long serialVersionUID = 6475845L;

  private static final int[] minStats = {0, 0, 0, 0, 0, 0, 0, 200, 200, 0, 0};
  private static final int[] maxStats = {0, 0, 0, 0, 0, 0, 0, 200, 200, 0, 0};

  private static final SWGWeakGeothermalRenewableEnergy INSTANCE = new SWGWeakGeothermalRenewableEnergy();

  SWGWeakGeothermalRenewableEnergy() { super(); }

  public static SWGWeakGeothermalRenewableEnergy getInstance() { return INSTANCE; }

  public int expectedStats() { return 2; }
  public int sortIndex() { return 32; }
  public int rcID() { return 818; }
  public String rcName() { return "Weak Geothermal Renewable Energy";}
  public String rcToken() { return "wgeg";}
  public boolean isSpaceOrRecycled()  { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

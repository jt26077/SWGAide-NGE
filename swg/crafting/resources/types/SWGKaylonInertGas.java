package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Kaylon Inert Gas"
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
public final class SWGKaylonInertGas extends SWGKnownInertGas {

  private static final long serialVersionUID = 1020015L;

  private static final int[] minStats = {0, 0, 413, 0, 0, 0, 0, 1, 0, 0, 0};
  private static final int[] maxStats = {0, 0, 511, 0, 0, 0, 0, 1000, 0, 0, 0};

  private static final SWGKaylonInertGas INSTANCE = new SWGKaylonInertGas();

  SWGKaylonInertGas() { super(); }

  public static SWGKaylonInertGas getInstance() { return INSTANCE; }

  public int expectedStats() { return 2; }
  public int sortIndex() { return 793; }
  public int rcID() { return 128; }
  public String rcName() { return "Kaylon Inert Gas";}
  public String rcToken() { return "kakrt";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

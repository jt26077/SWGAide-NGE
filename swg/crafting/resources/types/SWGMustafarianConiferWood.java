package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Mustafarian Conifer Wood"
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
public final class SWGMustafarianConiferWood extends SWGSoftWood {

  private static final long serialVersionUID = 5961890L;

  private static final int[] minStats = {0, 0, 1, 0, 0, 0, 600, 1, 0, 100, 1};
  private static final int[] maxStats = {0, 0, 600, 0, 0, 0, 1000, 1000, 0, 400, 300};

  private static final SWGMustafarianConiferWood INSTANCE = new SWGMustafarianConiferWood();

  SWGMustafarianConiferWood() { super(); }

  public static SWGMustafarianConiferWood getInstance() { return INSTANCE; }

  public int expectedStats() { return 5; }
  public int sortIndex() { return 554; }
  public int rcID() { return 753; }
  public String rcName() { return "Mustafarian Conifer Wood";}
  public String rcToken() { return "mucwd";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Mustafarian Iron"
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
public final class SWGMustafarianIron extends SWGIron {

  private static final long serialVersionUID = 4910259L;

  private static final int[] minStats = {450, 750, 886, 0, 0, 919, 550, 500, 0, 903, 903};
  private static final int[] maxStats = {500, 800, 1000, 0, 0, 1000, 600, 1000, 0, 1000, 1000};

  private static final SWGMustafarianIron INSTANCE = new SWGMustafarianIron();

  SWGMustafarianIron() { super(); }

  public static SWGMustafarianIron getInstance() { return INSTANCE; }

  public int expectedStats() { return 8; }
  public int sortIndex() { return 668; }
  public int rcID() { return 620; }
  public String rcName() { return "Mustafarian Iron";}
  public String rcToken() { return "muirn";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

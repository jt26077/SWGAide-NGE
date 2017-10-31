package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Amorphous Gemstone"
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
public class SWGAmorphousGemstone extends SWGGemstone {

  private static final long serialVersionUID = 577223L;

  private static final int[] minStats = {0, 1, 1, 1, 0, 600, 1, 1, 0, 1, 1};
  private static final int[] maxStats = {0, 1000, 1000, 800, 0, 1000, 600, 1000, 0, 1000, 1000};

  private static final SWGAmorphousGemstone INSTANCE = new SWGAmorphousGemstone();

  SWGAmorphousGemstone() { super(); }

  public static SWGAmorphousGemstone getInstance() { return INSTANCE; }

  public int expectedStats() { return 8; }
  public int sortIndex() { return 746; }
  public int rcID() { return 72; }
  public String rcName() { return "Amorphous Gemstone";}
  public String rcToken() { return "amo";}
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

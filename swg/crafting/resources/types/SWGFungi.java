package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Fungi"
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
public class SWGFungi extends SWGVegetables {

  private static final long serialVersionUID = 3257696L;

  private static final int[] minStats = {0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0};
  private static final int[] maxStats = {0, 0, 1000, 0, 1000, 0, 0, 1000, 1000, 0, 0};

  private static final SWGFungi INSTANCE = new SWGFungi();

  SWGFungi() { super(); }

  public static SWGFungi getInstance() { return INSTANCE; }

  public int expectedStats() { return 4; }
  public int sortIndex() { return 472; }
  public int rcID() { return 411; }
  public String rcName() { return "Fungi";}
  public String rcToken() { return "fgs";}
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}
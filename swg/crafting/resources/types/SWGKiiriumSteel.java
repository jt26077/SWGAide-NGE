package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Kiirium Steel"
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
public final class SWGKiiriumSteel extends SWGSteel {

  private static final long serialVersionUID = 2253507L;

  private static final int[] minStats = {46, 57, 535, 0, 0, 628, 29, 1, 0, 535, 442};
  private static final int[] maxStats = {150, 185, 615, 0, 0, 692, 93, 1000, 0, 615, 538};

  private static final SWGKiiriumSteel INSTANCE = new SWGKiiriumSteel();

  SWGKiiriumSteel() { super(); }

  public static SWGKiiriumSteel getInstance() { return INSTANCE; }

  public int expectedStats() { return 8; }
  public int sortIndex() { return 646; }
  public int rcID() { return 284; }
  public String rcName() { return "Kiirium Steel";}
  public String rcToken() { return "kistl";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

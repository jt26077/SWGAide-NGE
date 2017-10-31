package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Class 7 Solid Petro Fuel"
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
public final class SWGClass7SolidPetroFuel extends SWGKnownSolidPetrochemFuel {

  private static final long serialVersionUID = 5052585L;

  private static final int[] minStats = {0, 0, 652, 0, 0, 0, 0, 1, 870, 0, 0};
  private static final int[] maxStats = {0, 0, 800, 0, 0, 0, 0, 1000, 1000, 0, 0};

  private static final SWGClass7SolidPetroFuel INSTANCE = new SWGClass7SolidPetroFuel();

  SWGClass7SolidPetroFuel() { super(); }

  public static SWGClass7SolidPetroFuel getInstance() { return INSTANCE; }

  public int expectedStats() { return 3; }
  public int sortIndex() { return 624; }
  public int rcID() { return 638; }
  public String rcName() { return "Class 7 Solid Petro Fuel";}
  public String rcToken() { return "c7ksp";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Class 3 Solid Petro Fuel"
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
public final class SWGClass3SolidPetroFuel extends SWGKnownSolidPetrochemFuel {

  private static final long serialVersionUID = 1360016L;

  private static final int[] minStats = {0, 0, 195, 0, 0, 0, 0, 1, 470, 0, 0};
  private static final int[] maxStats = {0, 0, 378, 0, 0, 0, 0, 1000, 630, 0, 0};

  private static final SWGClass3SolidPetroFuel INSTANCE = new SWGClass3SolidPetroFuel();

  SWGClass3SolidPetroFuel() { super(); }

  public static SWGClass3SolidPetroFuel getInstance() { return INSTANCE; }

  public int expectedStats() { return 3; }
  public int sortIndex() { return 620; }
  public int rcID() { return 171; }
  public String rcName() { return "Class 3 Solid Petro Fuel";}
  public String rcToken() { return "c3ksp";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}
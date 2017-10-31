package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Class 4 Solid Petro Fuel"
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
public final class SWGClass4SolidPetroFuel extends SWGKnownSolidPetrochemFuel {

  private static final long serialVersionUID = 5036771L;

  private static final int[] minStats = {0, 0, 309, 0, 0, 0, 0, 1, 570, 0, 0};
  private static final int[] maxStats = {0, 0, 492, 0, 0, 0, 0, 1000, 730, 0, 0};

  private static final SWGClass4SolidPetroFuel INSTANCE = new SWGClass4SolidPetroFuel();

  SWGClass4SolidPetroFuel() { super(); }

  public static SWGClass4SolidPetroFuel getInstance() { return INSTANCE; }

  public int expectedStats() { return 3; }
  public int sortIndex() { return 621; }
  public int rcID() { return 636; }
  public String rcName() { return "Class 4 Solid Petro Fuel";}
  public String rcToken() { return "c4ksp";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

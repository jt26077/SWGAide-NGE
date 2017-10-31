package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Class 1 Radioactive"
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
public final class SWGClass1Radioactive extends SWGKnownRadioactive {

  private static final long serialVersionUID = 1320481L;

  private static final int[] minStats = {0, 0, 400, 0, 0, 0, 0, 1, 500, 0, 0};
  private static final int[] maxStats = {0, 0, 474, 0, 0, 0, 0, 1000, 593, 0, 0};

  private static final SWGClass1Radioactive INSTANCE = new SWGClass1Radioactive();

  SWGClass1Radioactive() { super(); }

  public static SWGClass1Radioactive getInstance() { return INSTANCE; }

  public int expectedStats() { return 3; }
  public int sortIndex() { return 630; }
  public int rcID() { return 166; }
  public String rcName() { return "Class 1 Radioactive";}
  public String rcToken() { return "c1krd";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

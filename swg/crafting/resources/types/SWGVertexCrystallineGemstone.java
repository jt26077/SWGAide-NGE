package swg.crafting.resources.types;

import swg.crafting.Stat;

/*
 * Represents a resource class of type "Vertex Crystalline Gemstone"
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
public final class SWGVertexCrystallineGemstone extends SWGCrystallineGemstone {

  private static final long serialVersionUID = 1225597L;

  private static final int[] minStats = {0, 838, 838, 919, 0, 951, 335, 1, 0, 886, 886};
  private static final int[] maxStats = {0, 1000, 1000, 1000, 0, 1000, 400, 1000, 0, 1000, 1000};

  private static final SWGVertexCrystallineGemstone INSTANCE = new SWGVertexCrystallineGemstone();

  SWGVertexCrystallineGemstone() { super(); }

  public static SWGVertexCrystallineGemstone getInstance() { return INSTANCE; }

  public int expectedStats() { return 8; }
  public int sortIndex() { return 767; }
  public int rcID() { return 154; }
  public String rcName() { return "Vertex Crystalline Gemstone";}
  public String rcToken() { return "vecry";}
  public boolean isSpawnable() { return true; }
  public boolean has(Stat s) { return minStats[s.i] > 0; }
  public int max(Stat s) { return maxStats[s.i]; }
  public int min(Stat s) { return minStats[s.i]; }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }
}

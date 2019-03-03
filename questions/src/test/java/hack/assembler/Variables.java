package hack.assembler;

import java.util.LinkedHashMap;

public enum Variables {
  Vars;

  private static final int offset = 16;
  private final LinkedHashMap<String, Integer> vars = new LinkedHashMap<>();

  public int indexOfVariable(String var) {
    return vars.computeIfAbsent(var, s -> offset + vars.size());
  }

  public void clearState() {
    vars.clear();
  }
}

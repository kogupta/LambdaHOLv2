package hack.assembler;

import java.util.LinkedHashMap;

public enum Labels {
  Instance;

  private final LinkedHashMap<String, Integer> labels = new LinkedHashMap<>();

  public void putIfAbsent(String label, int n) {
    labels.putIfAbsent(label, n);
  }

  public int rowIndex(String s) {
    if (!labels.containsKey(s)) {
      String err = String.format("label [%s] not found!\nExisting labels: %s", s, labels.keySet());
      throw new AssertionError(err);
    }

    return labels.get(s);
  }

  public boolean isLabel(String s) {
    return labels.containsKey(s);
  }

  public static String maybeLabel(String line) {
    char firstChar = line.charAt(0);
    char lastChar = line.charAt(line.length() - 1);
    if (firstChar == '(' && lastChar == ')') {
      return line.substring(1, line.length() - 1);
    } else return null;
  }

  public void clearState() {
    labels.clear();
  }
}

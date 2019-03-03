package hack.assembler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public enum CInstrParser implements Parser {
  Instance;

  // binary syntax: 111 a c1 c2 c3 c4 c5 c6 d1 d2 d3 j1 j2 j3
  // dest = comp ; jump
  private static final Map<String, String> dests = Map.of(
      "", "000",
      "M", "001",
      "D", "010",
      "MD", "011",
      "A", "100",
      "AM", "101",
      "AD", "110",
      "AMD", "111"
  );

  private static final Map<String, String> jumps = Map.of(
      "", "000",
      "JGT", "001",
      "JEQ", "010",
      "JGE", "011",
      "JLT", "100",
      "JNE", "101",
      "JLE", "110",
      "JMP", "111"
  );

  private static Map<String, String> createComps() {
    Map<String, String> m = new LinkedHashMap<>();
    m.put("0", "0101010");
    m.put("1", "0111111");
    m.put("-1", "0111010");
    m.put("D", "0001100");
    m.put("A", "0110000");
    m.put("M", "1110000");
    m.put("!D", "0001101");
    m.put("!A", "0110001");
    m.put("!M", "1110001");
    m.put("-D", "0001111");
    m.put("-A", "0110011");
    m.put("-M", "1110011");
    m.put("D+1", "0011111");
    m.put("A+1", "0110111");
    m.put("M+1", "1110111");
    m.put("D-1", "0001110");
    m.put("A-1", "0110010");
    m.put("M-1", "1110010");
    m.put("D-A", "0010011");
    m.put("D-M", "1010011");
    m.put("A-D", "0000111");
    m.put("M-D", "1000111");
    m.put("D+A", "0000010");
    m.put("A+D", "0000010");
    m.put("D+M", "1000010");
    m.put("M+D", "1000010");
    m.put("D&A", "0000000");
    m.put("A&D", "0000000");
    m.put("D&M", "1000000");
    m.put("M&D", "1000000");
    m.put("D|A", "0010101");
    m.put("A|D", "0010101");
    m.put("D|M", "1010101");
    return m;
  }

  private static final Map<String, String> comps = createComps();

  @Override
  public String toBinaryFormat(String instruction) {
    String dest = extractDest(instruction);
    String comp = extractComp(instruction);
    String jmp = extractJump(instruction);
    return "111" + comps.get(comp) + dests.get(dest) + jumps.get(jmp);
  }

  private static String extractDest(String s) {
    int idxOfEq = s.indexOf('=');
    if (idxOfEq != -1) {
      return s.substring(0, idxOfEq);
    }
    return "";
  }

  private static String extractComp(String s) {
    int idxOfEq = s.indexOf('=') + 1;
    int idxSemiColon = s.indexOf(';', idxOfEq);
    return idxSemiColon == -1 ? s.substring(idxOfEq) : s.substring(idxOfEq, idxSemiColon);
  }

  private static String extractJump(String s) {
    int idx = s.indexOf(';');
    if (idx != -1) {
      return s.substring(idx + 1);
    }
    return "";
  }

  public static void main(String[] args) {
    String[] xs = {"D=M", "0;JMP", "D=D-M;JGT"};
    String[] ds = {"D", "", "D"};
    String[] cs = {"M", "0", "D-M"};
    String[] js = {"", "JMP", "JGT"};
    for (int i = 0; i < xs.length; i++) {
      String x = xs[i];

      test(x, ds[i], extractDest(x));
      test(x, cs[i], extractComp(x));
      test(x, js[i], extractJump(x));
    }
  }

  private static void test(String x, String expected, String got) {
    if (!expected.equals(got)) {
      String err = x + " => expected:" + expected + ", got:" + got;
      throw new AssertionError(err);
    }
  }
}

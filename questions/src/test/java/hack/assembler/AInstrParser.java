package hack.assembler;

import com.google.common.base.Strings;

public enum AInstrParser implements Parser {
  Instance;

  @Override
  public String toBinaryFormat(String instruction) {
    int n = Integer.parseInt(instruction.substring(1));
    String s = Integer.toBinaryString(n);
    return Strings.padStart(s, 16, '0');
  }
}

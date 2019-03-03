package hack.assembler;

public interface Parser {
  String toBinaryFormat(String instruction);

  static String parse(String instruction) {
    return isCInstruction(instruction) ?
        CInstrParser.Instance.toBinaryFormat(instruction) :
        AInstrParser.Instance.toBinaryFormat(instruction);
  }

  static boolean isCInstruction(String line) {
    return line.charAt(0) != '@';
  }

  static boolean isAInstruction(String line) {
    return line.charAt(0) == '@';
  }
}

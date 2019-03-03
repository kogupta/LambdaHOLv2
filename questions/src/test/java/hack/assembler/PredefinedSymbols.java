package hack.assembler;

import com.google.common.collect.Streams;

import java.util.Optional;

import static java.util.Arrays.stream;

public final class PredefinedSymbols {
  private static final int count = 23;
//          "R0" =>     0x0000,
//          "R1" =>     0x0001,
//          "R2" =>     0x0002,
//          "R3" =>     0x0003,
//          "R4" =>     0x0004,
//          "R5" =>     0x0005,
//          "R6" =>     0x0006,
//          "R7" =>     0x0007,
//          "R8" =>     0x0008,
//          "R9" =>     0x0009,
//          "R10" =>    0x000a,
//          "R11" =>    0x000b,
//          "R12" =>    0x000c,
//          "R13" =>    0x000d,
//          "R14" =>    0x000e,
//          "R15" =>    0x000f,
//          "SP" =>     0x0000,
//          "LCL" =>    0x0001,
//          "ARG" =>    0x0002,
//          "THIS" =>   0x0003,
//          "THAT" =>   0x0004,
//          "SCREEN" => 0x4000,
//          "KBD" =>    0x6000

  private static final String[] predefinedSymbols;
  private static final int[] values;

  static {
    predefinedSymbols = new String[count];
    values = new int[count];
    int idx = 0;

    predefinedSymbols[idx] = "R0";  values[idx++] = 0x0000;
    predefinedSymbols[idx] = "R1";  values[idx++] = 0x0001;
    predefinedSymbols[idx] = "R2";  values[idx++] = 0x0002;
    predefinedSymbols[idx] = "R3";  values[idx++] = 0x0003;
    predefinedSymbols[idx] = "R4";  values[idx++] = 0x0004;
    predefinedSymbols[idx] = "R5";  values[idx++] = 0x0005;
    predefinedSymbols[idx] = "R6";  values[idx++] = 0x0006;
    predefinedSymbols[idx] = "R7";  values[idx++] = 0x0007;
    predefinedSymbols[idx] = "R8";  values[idx++] = 0x0008;
    predefinedSymbols[idx] = "R9";  values[idx++] = 0x0009;
    predefinedSymbols[idx] = "R10";  values[idx++] = 0x000a;
    predefinedSymbols[idx] = "R11";  values[idx++] = 0x000b;
    predefinedSymbols[idx] = "R12";  values[idx++] = 0x000c;
    predefinedSymbols[idx] = "R13";  values[idx++] = 0x000d;
    predefinedSymbols[idx] = "R14";  values[idx++] = 0x000e;
    predefinedSymbols[idx] = "R15";  values[idx++] = 0x000f;
    predefinedSymbols[idx] = "SP";  values[idx++] = 0x0000;
    predefinedSymbols[idx] = "LCL";  values[idx++] = 0x0001;
    predefinedSymbols[idx] = "ARG";  values[idx++] = 0x0002;
    predefinedSymbols[idx] = "THIS";  values[idx++] = 0x0003;
    predefinedSymbols[idx] = "THAT";  values[idx++] = 0x0004;
    predefinedSymbols[idx] = "SCREEN";  values[idx++] = 0x4000;
    predefinedSymbols[idx] = "KBD";  values[idx++] = 0x6000;
  }

  public static int valueOf(String symbol) {
    for (int i = 0; i < count; i++) {
      String key = predefinedSymbols[i];
      if (key.equals(symbol)) return values[i];
    }

    throw new IllegalArgumentException("Invalid symbol: " + symbol);
  }

  public static boolean isPredefinedSymbol(String s) {
    for (String symbol : predefinedSymbols)
      if (symbol.equals(s)) return true;

    return false;
  }

  public static Optional<Integer> intValueIfPredefined(String s) {
    for (int i = 0; i < count; i++) {
      String key = predefinedSymbols[i];
      if (key.equals(s)) return Optional.of(values[i]);
    }

    return Optional.empty();
  }

  public static void main(String[] args) {
    Streams.zip(stream(predefinedSymbols),
        stream(values).boxed(),
        (s, n) -> s + " => " + n)
        .forEach(System.out::println);
  }
}

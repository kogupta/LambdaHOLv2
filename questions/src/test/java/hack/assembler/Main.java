package hack.assembler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hack.assembler.Labels.Instance;
import static hack.assembler.PredefinedSymbols.isPredefinedSymbol;
import static hack.assembler.Variables.*;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.stream.Collectors.toList;

@SuppressWarnings({"Convert2MethodRef", "ForLoopReplaceableByForEach"})
public final class Main {
  public static void main(String[] args) throws IOException {
//    assert args.length == 1;
    String[] files = {
        "/home/kohgupta/depot/nand2tetris/projects/06/max/Max.asm",
        "/home/kohgupta/depot/nand2tetris/projects/06/max/MaxL.asm",
        "/home/kohgupta/depot/nand2tetris/projects/06/rect/Rect.asm",
        "/home/kohgupta/depot/nand2tetris/projects/06/rect/RectL.asm",
        "/home/kohgupta/depot/nand2tetris/projects/06/pong/Pong.asm",
        "/home/kohgupta/depot/nand2tetris/projects/06/pong/PongL.asm",
        "/home/kohgupta/depot/nand2tetris/projects/06/add/Add.asm"
    };

    for (String file : files) {
      System.out.println("file: " + file);
      Path path = Paths.get(file);
      List<String> lines = readStrippedAsm(path);
      System.out.println("-- comments/whitespace removed [max 20 lines] --");
      printAsm(lines);

      List<String> firstPass = buildFirstPass(lines);
      System.out.println("-- first pass --");
      printAsm(firstPass);

      List<String> xs = firstPass.stream()
          .map(line -> resolveSymbolIfAny(line))
          .map(Parser::parse)
          .collect(toList());
      System.out.println("-- second pass --");
      printAsm(xs);

      Path target = Paths.get(file.substring(0, file.length() - 4) + ".hack");
      System.out.println("Writing to file: " + target);
      Files.write(target, xs, US_ASCII, CREATE, WRITE);

      Instance.clearState();
      Vars.clearState();
    }
  }

  private static String resolveSymbolIfAny(String line) {
    if (line.charAt(0) != '@') return line;

    String rest = line.substring(1);
    if (isNotNumeric(rest)) {
      // non numeric -> symbol [loop-label | variable]
      // predef vars already resolved
      if (Instance.isLabel(rest)) {
        return "@" + Instance.rowIndex(rest);
      } else {
        // variable
        int n = Vars.indexOfVariable(rest);
        return "@" + n;
      }
    }

    return line;
  }

  private static void printAsm(List<String> asm) {
    IntStream.range(0, asm.size())
        .limit(20)
        .mapToObj(i -> i + "\t\t" + asm.get(i))
        .forEach(System.out::println);
  }

  private static List<String> buildFirstPass(List<String> lines) {
    int row = 0;
    List<String> firstPass = new ArrayList<>(lines.size());
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      char startsWith = line.charAt(0);
      switch (startsWith) {
        case '@':
          String rest = line.substring(1);
          if (isNotNumeric(rest) && isPredefinedSymbol(rest)) {
            int n = PredefinedSymbols.valueOf(rest);
            firstPass.add("@" + n);
          } else {
            firstPass.add(line);
          }
          row++;
          break;
        case '(':
          String loop = line.substring(1, line.length() - 1);
          Instance.putIfAbsent(loop, row);
          break;
        default:
          firstPass.add(line);
          row++;
      }
    }

    return firstPass;
  }

  private static boolean isNotNumeric(String s) {
    for (char c : s.toCharArray()) {
      if (c < '0' || c > '9') return true;
    }

    return false;
  }

  private static List<String> readStrippedAsm(Path file) throws IOException {
    try (Stream<String> stream = Files.lines(file, US_ASCII)) {
      return stream
          .map(line -> trimRow(line))
          .filter(line -> line.length() > 0)
          .collect(toList());
    }
  }

  private static String trimRow(String line) {
    if (line.isBlank() || startsWithComment(line)) return "";

    String s = line.stripLeading();
    if (startsWithComment(s)) return "";
    int commentStart = s.indexOf('/');
    return commentStart == -1 ?
        s.stripTrailing() :
        s.substring(0, commentStart).stripTrailing();
  }

  private static boolean startsWithComment(String line) {
    return line.charAt(0) == '/' && line.charAt(1) == '/';
  }
}

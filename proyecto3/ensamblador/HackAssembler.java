import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HackAssembler {

    public static void main(String[] args) throws IOException {
        // Salida estandar en UTF-8 para que la tilde de "Línea" se vea bien en Git Bash.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));

        if (args.length != 1 || !args[0].endsWith(".asm")) {
            System.out.println("Uso: ./ensamblador.sh Prog.asm");
            System.exit(1);
        }

        Path input = Paths.get(args[0]);
        String fileName = input.getFileName().toString();
        Path output = input.resolveSibling(fileName.substring(0, fileName.length() - 4) + ".hack");

        try {
            assemble(input, output);
        } catch (HackException e) {
            Files.deleteIfExists(output); // Si hay un error no debe quedar archivo de salida.
            System.out.println("ERROR *** Línea " + e.getLine() + ": " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("ERROR *** No se pudo leer o escribir el archivo: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Archivo generado: " + output);
    }

    public static void assemble(Path input, Path output) throws IOException, HackException {
        SymbolTable symbols = new SymbolTable();

        // Primera pasada: valida cada linea en orden y las etiquetas apuntan a posiciones de la ROM.
        Parser parser = new Parser(input);
        int romAddress = 0;
        while (parser.hasMoreLines()) {
            parser.advance();
            if (parser.instructionType() == Parser.InstructionType.L_INSTRUCTION) {
                symbols.addEntry(parser.symbol(), romAddress);
            } else {
                romAddress++;
            }
        }

        // Segunda pasada: traduce instrucciones y asigna RAM a variables.
        parser = new Parser(input);
        int nextVariableAddress = 16;
        List<String> binaryLines = new ArrayList<>();

        while (parser.hasMoreLines()) {
            parser.advance();
            switch (parser.instructionType()) {
                case L_INSTRUCTION:
                    break; // Una etiqueta no genera instrucciones binarias.

                case A_INSTRUCTION:
                    String symbol = parser.symbol();
                    int address;
                    if (symbol.matches("[0-9]+")) {
                        address = Integer.parseInt(symbol);
                    } else {
                        if (!symbols.contains(symbol)) {
                            symbols.addEntry(symbol, nextVariableAddress++);
                        }
                        address = symbols.getAddress(symbol);
                    }
                    if (address < 0 || address > 32767) {
                        throw new HackException(parser.lineNumber(), "dirección fuera de rango: " + address);
                    }
                    binaryLines.add(to16Bits(address));
                    break;

                case C_INSTRUCTION:
                    String binary = "111"
                            + Code.compToBinary(parser.comp())
                            + Code.destToBinary(parser.dest())
                            + Code.jumpToBinary(parser.jump());
                    binaryLines.add(binary);
                    break;
            }
        }

        Files.write(output, binaryLines, StandardCharsets.UTF_8);
    }

    private static String to16Bits(int value) {
        String binary = Integer.toBinaryString(value);
        return "0000000000000000".substring(binary.length()) + binary;
    }
}

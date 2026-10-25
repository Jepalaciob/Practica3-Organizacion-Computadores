import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HackDisassembler {

    public static void main(String[] args) throws IOException {
        if (args.length != 1 || !args[0].endsWith(".hack")) {
            System.err.println("Uso: java HackDisassembler Prog.hack");
            return;
        }

        Path input = Paths.get(args[0]);
        String fileName = input.getFileName().toString();
        Path output = input.resolveSibling(fileName.substring(0, fileName.length() - 5) + ".asm");

        disassemble(input, output);
        System.out.println("Archivo generado: " + output);
    }

    public static void disassemble(Path input, Path output) throws IOException {
        // Una sola pasada: los simbolos se pierden al ensamblar, asi que
        // las instrucciones A se escriben con su direccion numerica.
        List<String> asmLines = new ArrayList<>();

        for (String line : Files.readAllLines(input, StandardCharsets.UTF_8)) {
            String instruction = line.replaceAll("\\s+", "");
            if (instruction.isEmpty()) {
                continue;
            }
            if (!instruction.matches("[01]{16}")) {
                throw new IllegalArgumentException("Instruccion invalida: " + instruction);
            }

            if (instruction.charAt(0) == '0') {
                asmLines.add("@" + Integer.parseInt(instruction, 2));
            } else {
                if (!instruction.startsWith("111")) {
                    throw new IllegalArgumentException("Instruccion invalida: " + instruction);
                }
                // Formato: 111a cccc ccdd djjj
                String comp = Code.binaryToComp(instruction.substring(3, 10));
                if (comp == null) {
                    throw new IllegalArgumentException("Instruccion invalida: " + instruction);
                }
                String dest = Code.binaryToDest(instruction.substring(10, 13));
                String jump = Code.binaryToJump(instruction.substring(13, 16));

                String asm = (dest.isEmpty() ? "" : dest + "=")
                        + comp
                        + (jump.isEmpty() ? "" : ";" + jump);
                asmLines.add(asm);
            }
        }

        Files.write(output, asmLines, StandardCharsets.UTF_8);
    }
}

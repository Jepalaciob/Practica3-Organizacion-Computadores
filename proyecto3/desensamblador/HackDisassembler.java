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

public class HackDisassembler {

    public static void main(String[] args) throws IOException {
        // Salida estandar en UTF-8 para que la tilde de "Línea" se vea bien en Git Bash.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));

        if (args.length != 1 || !args[0].endsWith(".hack")) {
            System.out.println("Uso: ./desensamblador.sh Prog.hack");
            System.exit(1);
        }

        // Prog.hack -> ProgDis.asm, para no pisar el Prog.asm original.
        Path input = Paths.get(args[0]);
        String fileName = input.getFileName().toString();
        Path output = input.resolveSibling(fileName.substring(0, fileName.length() - 5) + "Dis.asm");

        try {
            disassemble(input, output);
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

    public static void disassemble(Path input, Path output) throws IOException, HackException {
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

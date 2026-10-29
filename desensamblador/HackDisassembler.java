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
        // Se lee sin recortar: los espacios al final y las lineas vacias en medio son errores.
        // ISO-8859-1 acepta cualquier byte, asi un caracter raro se reporta como error y no como excepcion.
        List<String> lines = Files.readAllLines(input, StandardCharsets.ISO_8859_1);

        // El salto de linea final es opcional: solo se ignoran las lineas vacias del final.
        int n = lines.size();
        while (n > 0 && lines.get(n - 1).isEmpty()) {
            n--;
        }

        // Primera pasada: valida y traduce cada instruccion. Los simbolos se pierden al
        // ensamblar, asi que las instrucciones A quedan en decimal.
        String[] asm = new String[n];
        for (int i = 0; i < n; i++) {
            asm[i] = translate(lines.get(i), i + 1);
        }

        // Segunda pasada (regla 9.1): si la instruccion i es C con salto y la i-1 es @v con
        // v <= n, el salto va a la direccion v y esa @v se escribe con una etiqueta.
        boolean[] usesLabel = new boolean[n];
        boolean[] isTarget = new boolean[n + 1];
        for (int i = 1; i < n; i++) {
            String current = lines.get(i);
            String previous = lines.get(i - 1);
            boolean jumps = current.charAt(0) == '1' && !current.endsWith("000");
            if (jumps && previous.charAt(0) == '0') {
                int target = Integer.parseInt(previous, 2);
                if (target <= n) {
                    usesLabel[i - 1] = true;
                    isTarget[target] = true;
                }
            }
        }

        // Una sola etiqueta por destino, nombradas L0, L1, ... en orden de direccion.
        String[] labels = new String[n + 1];
        int count = 0;
        for (int address = 0; address <= n; address++) {
            if (isTarget[address]) {
                labels[address] = "L" + count++;
            }
        }

        List<String> asmLines = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (labels[i] != null) {
                asmLines.add("(" + labels[i] + ")");
            }
            asmLines.add(usesLabel[i] ? "@" + labels[Integer.parseInt(lines.get(i), 2)] : asm[i]);
        }
        if (labels[n] != null) {
            asmLines.add("(" + labels[n] + ")"); // Salto a v = n: la etiqueta va al final del archivo.
        }

        Files.write(output, asmLines, StandardCharsets.UTF_8);
    }

    private static String translate(String instruction, int line) throws HackException {
        if (instruction.isEmpty()) {
            throw new HackException(line, "línea vacía");
        }
        if (instruction.length() != 16) {
            throw new HackException(line, "la instrucción tiene " + instruction.length() + " caracteres y debe tener 16");
        }
        for (char bit : instruction.toCharArray()) {
            if (bit != '0' && bit != '1') {
                throw new HackException(line, "carácter inválido '" + bit + "', solo se permiten 0 y 1");
            }
        }

        if (instruction.charAt(0) == '0') {
            return "@" + Integer.parseInt(instruction, 2);
        }

        // Formato: 111a cccc ccdd djjj
        if (!instruction.startsWith("111")) {
            throw new HackException(line, "instrucción C con bits 14 y 13 en '" + instruction.substring(1, 3) + "', deben ser 11");
        }
        String comp = Code.binaryToComp(instruction.substring(3, 10));
        if (comp == null) {
            throw new HackException(line, "cómputo inválido '" + instruction.substring(3, 10) + "'");
        }
        // Los 8 valores posibles de dest y de jump existen en las tablas.
        String dest = Code.binaryToDest(instruction.substring(10, 13));
        String jump = Code.binaryToJump(instruction.substring(13, 16));

        return (dest.isEmpty() ? "" : dest + "=")
                + comp
                + (jump.isEmpty() ? "" : ";" + jump);
    }
}

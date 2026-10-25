import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public enum InstructionType {
        A_INSTRUCTION,
        C_INSTRUCTION,
        L_INSTRUCTION
    }

    private final List<String> instructions = new ArrayList<>();
    private final List<Integer> lineNumbers = new ArrayList<>();
    private int position = 0;
    private String current;
    private int currentLine;

    public Parser(Path input) throws IOException {
        // ISO-8859-1 acepta cualquier byte, asi una tilde en un comentario no detiene la lectura.
        List<String> lines = Files.readAllLines(input, StandardCharsets.ISO_8859_1);
        for (int i = 0; i < lines.size(); i++) {

            String instruction = lines.get(i).split("//", 2)[0].replaceAll("\\s+", "");
            if (!instruction.isEmpty()) {
                instructions.add(instruction);
                lineNumbers.add(i + 1); // Numero de linea real, contando comentarios y lineas vacias.
            }

        }
    }

    public boolean hasMoreLines() {
        return position < instructions.size();
    }

    public void advance() throws HackException {
        current = instructions.get(position);
        currentLine = lineNumbers.get(position);
        position++;

        if (instructionType() == InstructionType.C_INSTRUCTION) {
            validateC();
        }
    }

    public int lineNumber() {
        return currentLine;
    }

    public InstructionType instructionType() {
        if (current.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        }
        if (current.startsWith("(")) {
            return InstructionType.L_INSTRUCTION;
        }
        return InstructionType.C_INSTRUCTION;
    }

    public String symbol() {
        if (instructionType() == InstructionType.A_INSTRUCTION) {
            return current.substring(1);
        }
        if (instructionType() == InstructionType.L_INSTRUCTION) {
            return current.substring(1, current.length() - 1);
        }
        throw new IllegalStateException("La instruccion no tiene simbolo: " + current);
    }

    public String dest() {
        int equal = current.indexOf('=');
        return equal == -1 ? "" : current.substring(0, equal);
    }

    public String comp() {
        int equal = current.indexOf('=');
        int semicolon = current.indexOf(';');
        int start = equal == -1 ? 0 : equal + 1;
        int end = semicolon == -1 ? current.length() : semicolon;
        return current.substring(start, end);
    }

    public String jump() {
        int semicolon = current.indexOf(';');
        return semicolon == -1 ? "" : current.substring(semicolon + 1);
    }

    // dest=comp;jump: dest= y ;jump son opcionales, pero si aparecen no pueden estar vacios.
    private void validateC() throws HackException {
        int equals = count('=');
        int semicolons = count(';');

        if (equals > 1) {
            throw error("la instrucción tiene más de un '='");
        }
        if (semicolons > 1) {
            throw error("la instrucción tiene más de un ';'");
        }
        if (equals == 1 && semicolons == 1 && current.indexOf(';') < current.indexOf('=')) {
            throw error("el ';' aparece antes del '='");
        }
        if (equals == 0 && semicolons == 0 && Code.compToBinary(current) == null) {
            throw error("instrucción no reconocida '" + current + "'");
        }

        if (equals == 1) {
            if (dest().isEmpty()) {
                throw error("destino vacío");
            }
            if (Code.destToBinary(dest()) == null) {
                throw error("destino inválido '" + dest() + "'");
            }
        }

        if (comp().isEmpty()) {
            throw error("cómputo vacío");
        }
        if (Code.compToBinary(comp()) == null) {
            throw error("cómputo inválido '" + comp() + "'");
        }

        if (semicolons == 1) {
            if (jump().isEmpty()) {
                throw error("salto vacío");
            }
            if (Code.jumpToBinary(jump()) == null) {
                throw error("salto inválido '" + jump() + "'");
            }
        }
    }

    private int count(char character) {
        int total = 0;
        for (int i = 0; i < current.length(); i++) {
            if (current.charAt(i) == character) {
                total++;
            }
        }
        return total;
    }

    private HackException error(String message) {
        return new HackException(currentLine, message);
    }
}

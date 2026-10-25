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
    private int position = 0;
    private String current;

    public Parser(Path input) throws IOException {
        for (String line : Files.readAllLines(input, StandardCharsets.UTF_8)) {

            String instruction = line.split("//", 2)[0].replaceAll("\\s+", "");
            if (!instruction.isEmpty()) {
                instructions.add(instruction);
            }

        }
    }

    public boolean hasMoreLines() {
        return position < instructions.size();
    }

    public void advance() {
        current = instructions.get(position++);
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
}


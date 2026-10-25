import java.util.HashMap;
import java.util.Map;

public final class Code {

    private static final Map<String, String> COMP;
    private static final Map<String, String> DEST;
    private static final Map<String, String> JUMP;
    private static final Map<String, String> BINARY_TO_COMP;
    private static final Map<String, String> BINARY_TO_DEST;
    private static final Map<String, String> BINARY_TO_JUMP;

    static {
        Map<String, String> comp = new HashMap<>();
        comp.put("0", "0101010");
        comp.put("1", "0111111");
        comp.put("-1", "0111010");
        comp.put("D", "0001100");
        comp.put("A", "0110000");
        comp.put("M", "1110000");
        comp.put("!D", "0001101");
        comp.put("!A", "0110001");
        comp.put("!M", "1110001");
        comp.put("-D", "0001111");
        comp.put("-A", "0110011");
        comp.put("-M", "1110011");
        comp.put("D+1", "0011111");
        comp.put("A+1", "0110111");
        comp.put("M+1", "1110111");
        comp.put("D-1", "0001110");
        comp.put("A-1", "0110010");
        comp.put("M-1", "1110010");
        comp.put("D+A", "0000010");
        comp.put("D+M", "1000010");
        comp.put("D-A", "0010011");
        comp.put("D-M", "1010011");
        comp.put("A-D", "0000111");
        comp.put("M-D", "1000111");
        comp.put("D&A", "0000000");
        comp.put("D&M", "1000000");
        comp.put("D|A", "0010101");
        comp.put("D|M", "1010101");
        COMP = comp;

        // Solo los siete destinos del enunciado: DM, ADM, MA, etc. son errores.
        Map<String, String> dest = new HashMap<>();
        dest.put("", "000");
        dest.put("M", "001");
        dest.put("D", "010");
        dest.put("MD", "011");
        dest.put("A", "100");
        dest.put("AM", "101");
        dest.put("AD", "110");
        dest.put("AMD", "111");
        DEST = dest;

        Map<String, String> jump = new HashMap<>();
        jump.put("", "000");
        jump.put("JGT", "001");
        jump.put("JEQ", "010");
        jump.put("JGE", "011");
        jump.put("JLT", "100");
        jump.put("JNE", "101");
        jump.put("JLE", "110");
        jump.put("JMP", "111");
        JUMP = jump;

        // Tablas inversas para traducir de binario a Assembler.
        BINARY_TO_COMP = invert(COMP);
        BINARY_TO_DEST = invert(DEST);
        BINARY_TO_JUMP = invert(JUMP);
    }

    private static Map<String, String> invert(Map<String, String> table) {
        Map<String, String> inverse = new HashMap<>();
        for (Map.Entry<String, String> entry : table.entrySet()) {
            inverse.put(entry.getValue(), entry.getKey());
        }
        return inverse;
    }

    private Code() {
    }

    public static String compToBinary(String comp) {
        String binary = COMP.get(comp);
        if (binary == null) {
            throw new IllegalArgumentException("Computo invalido: " + comp);
        }
        return binary;
    }

    public static String destToBinary(String dest) {
        String binary = DEST.get(dest);
        if (binary == null) {
            throw new IllegalArgumentException("Destino invalido: " + dest);
        }
        return binary;
    }

    public static String jumpToBinary(String jump) {
        String binary = JUMP.get(jump);
        if (binary == null) {
            throw new IllegalArgumentException("Salto invalido: " + jump);
        }
        return binary;
    }

    public static String binaryToComp(String bits) {
        String comp = BINARY_TO_COMP.get(bits);
        if (comp == null) {
            throw new IllegalArgumentException("Computo invalido: " + bits);
        }
        return comp;
    }

    public static String binaryToDest(String bits) {
        String dest = BINARY_TO_DEST.get(bits);
        if (dest == null) {
            throw new IllegalArgumentException("Destino invalido: " + bits);
        }
        return dest;
    }

    public static String binaryToJump(String bits) {
        String jump = BINARY_TO_JUMP.get(bits);
        if (jump == null) {
            throw new IllegalArgumentException("Salto invalido: " + bits);
        }
        return jump;
    }

}

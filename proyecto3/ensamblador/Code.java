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

    // Cada traduccion devuelve null si el mnemonico o el codigo no esta en la tabla;
    // quien la llama decide el mensaje de error con su numero de linea.

    public static String compToBinary(String comp) {
        return COMP.get(comp);
    }

    public static String destToBinary(String dest) {
        return DEST.get(dest);
    }

    public static String jumpToBinary(String jump) {
        return JUMP.get(jump);
    }

    public static String binaryToComp(String bits) {
        return BINARY_TO_COMP.get(bits);
    }

    public static String binaryToDest(String bits) {
        return BINARY_TO_DEST.get(bits);
    }

    public static String binaryToJump(String bits) {
        return BINARY_TO_JUMP.get(bits);
    }

}

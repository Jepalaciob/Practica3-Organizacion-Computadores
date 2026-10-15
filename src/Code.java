import java.util.HashMap;
import java.util.Map;

public final class Code {

    private static final Map<String, String> COMP;
    private static final Map<String, String> JUMP;

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
        int bits = 0;
        for (char register : dest.toCharArray()) {
            switch (register) {
                case 'A': bits |= 4; break;
                case 'D': bits |= 2; break;
                case 'M': bits |= 1; break;
                default: throw new IllegalArgumentException("Destino invalido: " + dest);
            }
        }
        String binary = Integer.toBinaryString(bits);
        return "000".substring(binary.length()) + binary;
    }

    public static String jumpToBinary(String jump) {
        String binary = JUMP.get(jump);
        if (binary == null) {
            throw new IllegalArgumentException("Salto invalido: " + jump);
        }
        return binary;
    }

}

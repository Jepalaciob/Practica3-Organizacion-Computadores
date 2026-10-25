import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private final Map<String, Integer> symbols = new HashMap<>();

    public SymbolTable() {
        for (int i = 0; i <= 15; i++) {
            symbols.put("R" + i, i);
        }

        symbols.put("SP", 0);
        symbols.put("LCL", 1);
        symbols.put("ARG", 2);
        symbols.put("THIS", 3);
        symbols.put("THAT", 4);
        symbols.put("SCREEN", 16384);
        symbols.put("KBD", 24576);
    }

    public void addEntry(String symbol, int address) {
        symbols.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return symbols.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        Integer address = symbols.get(symbol);
        if (address == null) {
            throw new IllegalArgumentException("Simbolo desconocido: " + symbol);
        }
        return address;
    }
}

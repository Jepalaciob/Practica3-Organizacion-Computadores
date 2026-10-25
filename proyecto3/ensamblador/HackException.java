public class HackException extends Exception {

    // Linea del archivo de entrada, contando desde 1 e incluyendo comentarios y lineas vacias.
    private final int line;

    public HackException(int line, String message) {
        super(message);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}

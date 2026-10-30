// Intercambia R0 y R1 y guarda una copia del valor inicial de R0.
@R0
D=M
@temporal
M=D
@R1
D=M
@R0
M=D
@temporal
D=M
@R1
M=D
@copia
M=D

// Comprueba las direcciones de los simbolos predefinidos.
@SCREEN
D=A
@KBD
D=A
@SP
@LCL
@ARG
@THIS
@THAT
@32767
D=A

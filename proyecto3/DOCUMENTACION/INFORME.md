# Informe técnico – Ensamblador y desensamblador Hack

## Integrantes y aportes

Según el historial del repositorio, los aportes incluyen:

- **Thomas Alejandro Serna Saldarriaga:** estructura inicial, analizador de
  instrucciones, tablas del ensamblador, tabla de símbolos y ensamblador de dos
  pasadas; organización posterior, pruebas propias y README.
- **Juan Esteban Palacio Betancur:** traducción inversa, tablas para el
  desensamblador, validación de instrucciones y errores, generación de etiquetas
  para los saltos, scripts de ejecución y ajustes de la estructura.

## Descripción de la solución

La solución está escrita en Java y utiliza la biblioteca estándar. El
ensamblador recibe un archivo `Prog.asm` y produce `Prog.hack` en la misma
carpeta. El desensamblador recibe `Prog.hack` y produce `ProgDis.asm`. Las
instrucciones de compilación y ejecución están en el
[README del proyecto](../README.md).

Los módulos principales son:

| Módulo | Función |
| --- | --- |
| `HackAssembler` | Coordina las dos pasadas y genera instrucciones binarias. |
| `Parser` | Lee las líneas de ensamblador, identifica los tipos de instrucción y valida su sintaxis. |
| `SymbolTable` | Almacena símbolos predefinidos, etiquetas y variables. |
| `Code` | Contiene las tablas de `comp`, `dest` y `jump`, y sus traducciones inversas. |
| `HackException` | Almacena el mensaje y el número de línea de un error de entrada. |
| `HackDisassembler` | Valida y traduce las instrucciones binarias y genera etiquetas. |

### Pasadas del ensamblador

1. **Primera pasada:** `Parser` elimina comentarios, espacios y tabulaciones, conserva la
   posición original de cada instrucción y valida la sintaxis. Por cada
   instrucción A o C, el ensamblador incrementa la dirección de ROM. Cuando
   encuentra `(ETIQUETA)`, registra la dirección de la siguiente instrucción;
   la etiqueta no ocupa una posición de ROM. También rechaza etiquetas
   duplicadas y nombres predefinidos.
2. **Segunda pasada:** vuelve a leer las instrucciones. Las A numéricas se
   convierten a 16 bits; los símbolos se resuelven en la tabla y las variables
   nuevas reciben una dirección de RAM. Las instrucciones C se traducen con
   las tablas de `Code`. El archivo de salida se escribe después de procesar
   todas las instrucciones.

La primera pasada permite usar una etiqueta antes de su declaración. La
segunda encuentra todas las etiquetas ya registradas y puede distinguirlas de
las variables que se asignan en ese recorrido.

## Tabla de símbolos

`SymbolTable` usa un `HashMap<String, Integer>` para asociar cada nombre con
su dirección. Al iniciar carga `R0` a `R15`, `SP`, `LCL`, `ARG`, `THIS`,
`THAT`, `SCREEN` y `KBD`. También conserva un `HashSet` con los nombres
predefinidos para impedir que se declaren como etiquetas.

Una etiqueta toma la dirección de ROM de la siguiente instrucción. Una
variable que no aparece como símbolo predefinido ni como etiqueta recibe la
siguiente dirección libre de RAM, comenzando en 16. Cada nueva variable avanza
el contador y las apariciones posteriores reutilizan la dirección asignada. Por
ejemplo, si `@temporal` aparece primero y `@copia` después, sus direcciones son
16 y 17 respectivamente.

El campo de una instrucción A representa direcciones de 15 bits, entre 0 y
32767. La implementación valida ese rango para las constantes numéricas.

## Generación de etiquetas

El desensamblador primero valida y traduce cada línea binaria. Después examina
las parejas de instrucciones consecutivas. Si una instrucción C tiene un salto
y la anterior es una instrucción A con valor `v` menor o igual al número `n` de
instrucciones, marca esa A como referencia a una etiqueta y marca `v` como
destino. La etiqueta se declara antes de la instrucción de dirección `v`, o al
final del archivo si `v = n`.

Para cada dirección marcada se genera un solo nombre `L0`, `L1`, etc., en orden
de dirección. Las demás instrucciones A conservan su valor decimal, incluso
cuando ese valor coincide con el de un destino de salto.

Por ejemplo, `DestinoCompartido.hack` tiene dos saltos hacia la dirección 0 y
otra instrucción A con el mismo valor que no precede a un salto:

```text
Dirección  Binario           Ensamblador generado
           —                 (L0)
0          0000000000000000  @L0
1          1110101010000111  0;JMP
2          0000000000000000  @L0
3          1110001100000101  D;JNE
4          0000000000000000  @0
5          1111110000010000  D=M
```

La misma etiqueta `L0` representa los dos saltos. `@0` en la dirección 4 se
mantiene decimal porque la instrucción siguiente no es un salto. Al ensamblar
la salida se recupera la secuencia binaria original.

## Manejo de errores

El ensamblador conserva el número de línea original al leer el archivo, de modo
que cuenta desde 1 e incluye comentarios y líneas vacías. El desensamblador usa
la posición de cada línea binaria, también desde 1. En una entrada inválida,
ambos detienen el procesamiento en el primer error en orden de línea y muestran
en la salida estándar `ERROR *** Línea N: <mensaje>`. Terminan con código
distinto de cero y no dejan el archivo de salida correspondiente.

Las categorías de error se detectan así:

| Traductor | Categoría | Comprobación |
| --- | --- | --- |
| Ensamblador | Instrucción A | Valor presente, constante decimal entre 0 y 32767 o símbolo válido que no empiece por dígito. |
| Ensamblador | Etiquetas | Paréntesis y nombre válidos; el ensamblador comprueba que no se repitan ni sean predefinidas. |
| Ensamblador | Destino | Nombre no vacío cuando aparece `=` y perteneciente a la tabla exacta de destinos. |
| Ensamblador | Cómputo | Parte obligatoria y perteneciente a la tabla de cómputos. |
| Ensamblador | Salto | Nombre no vacío cuando aparece `;` y perteneciente a la tabla de saltos. |
| Ensamblador | Estructura | Orden y cantidad de `=` y `;`, y reconocimiento de la instrucción. |
| Desensamblador | Longitud | Cada línea de instrucción debe tener exactamente 16 caracteres. |
| Desensamblador | Caracteres | Cada carácter de una instrucción debe ser `0` o `1`. |
| Desensamblador | Bits 14–13 | Una instrucción C debe empezar por `111`. |
| Desensamblador | Cómputo | Los siete bits de `comp` deben estar en la tabla inversa. |

## Pruebas

La carpeta [`pruebas/`](../pruebas/) contiene tres programas válidos propios
para cada traductor y casos de error para las categorías anteriores. El
[catálogo de pruebas](../pruebas/README.md) identifica cada entrada y su
resultado esperado e incluye ejemplos de ejecución y comparación. En las
pruebas realizadas, las salidas coincidieron con los resultados esperados; para
los errores se verificaron el mensaje, el código de salida y la ausencia del
archivo generado.

### Comparación con las herramientas oficiales

Se usaron los programas de `projects/6` y el ensamblador de la
[suite oficial de Nand2Tetris](https://www.nand2tetris.org/software), versión
2.7. Para cada programa se comparó el resultado de nuestro ensamblador con el
del oficial. Además, se desensambló el binario y se volvió a ensamblar el
resultado con la herramienta oficial. En ambos casos se compararon las
secuencias de instrucciones, normalizando LF, CRLF y el salto final opcional.

| Programa | Instrucciones | Ensamblado coincide | Ida y vuelta con ensamblador oficial | Tiempo propio |
| --- | ---: | --- | --- | ---: |
| `Add` | 6 | Sí | Sí | 0,248 s |
| `Max` | 16 | Sí | Sí | 0,376 s |
| `MaxL` | 16 | Sí | Sí | 0,264 s |
| `Rect` | 25 | Sí | Sí | 0,305 s |
| `RectL` | 25 | Sí | Sí | 0,295 s |
| `Pong` | 27 483 | Sí | Sí | 0,845 s |
| `PongL` | 27 483 | Sí | Sí | 0,701 s |

Las mediciones corresponden a una ejecución por programa en Windows con JDK
26 y compilación mediante `--release 11`. Incluyen el arranque de Java y sirven
como referencia de esta ejecución, no como una evaluación estadística de
rendimiento. La comparación verifica la traducción; no es una prueba
interactiva del juego Pong. Estos resultados cubren las entradas probadas y no
garantizan el comportamiento de todos los programas posibles.

## Análisis del desensamblado

El ensamblador conserva en el binario los bits de las instrucciones, pero no
los comentarios, espacios, nombres de etiquetas, nombres de variables ni las
declaraciones de etiquetas. Por ejemplo, `@R0` y `@0` producen los mismos
bits. El código de máquina contiene el valor de una instrucción A, pero no la
información que permita saber si ese valor se escribió como constante,
variable o símbolo predefinido.

Por eso el desensamblado no es único: distintos textos de ensamblador pueden
generar el mismo programa binario y pueden usarse nombres diferentes para una
misma etiqueta. El objetivo es reconstruir un programa válido que vuelva a
producir las instrucciones originales, no recuperar exactamente el texto de
entrada.

La regla 9.1 solo etiqueta un salto cuando la instrucción inmediatamente
anterior es una A con valor `v <= n`. No identifica mediante esa regla los
saltos cuya dirección fue cargada o calculada antes, cuyo destino depende del
estado durante la ejecución o cuyo valor inmediato supera `n`. Tampoco se
puede aplicar a un salto situado en la primera instrucción, pues no tiene una
instrucción anterior. En esos casos se conserva la traducción de las
instrucciones sin atribuirles una etiqueta por la regla indicada.

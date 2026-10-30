# Pruebas – Ensamblador y desensamblador Hack

Esta carpeta contiene programas propios válidos y casos de error para ambos
traductores. Los archivos `*.esperado.hack` y `*Dis.esperado.asm` contienen
las salidas esperadas; los archivos `*.error.txt` contienen el mensaje
esperado para cada entrada inválida.

## Ejecución

Desde la carpeta `proyecto3/` en Git Bash:

```bash
./compilar.sh
./pruebas/verificar.sh
```

El script ejecuta las pruebas en una carpeta temporal, compara los resultados
ignorando diferencias de fin de línea y comprueba la ida y vuelta de los
archivos del desensamblador. Para los errores verifica el mensaje, el código
de salida y que no se genere un archivo de salida.

También es posible ejecutar un caso válido y compararlo manualmente:

```bash
./ensamblador.sh pruebas/ensamblador/Doble.asm
diff --strip-trailing-cr pruebas/ensamblador/Doble.hack pruebas/ensamblador/Doble.esperado.hack
./desensamblador.sh pruebas/desensamblador/SaltoFinal.hack
diff --strip-trailing-cr pruebas/desensamblador/SaltoFinalDis.asm pruebas/desensamblador/SaltoFinalDis.esperado.asm
./ensamblador.sh pruebas/desensamblador/SaltoFinalDis.asm
diff --strip-trailing-cr pruebas/desensamblador/SaltoFinalDis.hack pruebas/desensamblador/SaltoFinal.hack
```

Si las comparaciones coinciden, `diff` no muestra diferencias. Estos comandos
manuales generan archivos junto a las entradas. El script de pruebas evita
modificar las carpetas de casos.

## Ensamblador: programas válidos

| Entrada | Descripción | Salida esperada |
| --- | --- | --- |
| `ensamblador/Doble.asm` | Calcula `RAM[1] = 2 * RAM[0]` con direcciones numéricas. | [`Doble.esperado.hack`](ensamblador/Doble.esperado.hack) |
| `ensamblador/CuentaRegresiva.asm` | Cuenta desde 3 usando una variable, etiquetas con referencias adelantadas, espacios y tabulaciones; usa CRLF. | [`CuentaRegresiva.esperado.hack`](ensamblador/CuentaRegresiva.esperado.hack) |
| `ensamblador/Intercambio.asm` | Intercambia `R0` y `R1`, usa dos variables y símbolos predefinidos; incluye `@32767`. | [`Intercambio.esperado.hack`](ensamblador/Intercambio.esperado.hack) |

## Desensamblador: programas válidos

| Entrada | Descripción | Salida esperada |
| --- | --- | --- |
| `desensamblador/SaltoFinal.hack` | Salta a la dirección posterior a la última instrucción; incluye destinos `MD` y `AMD`. | [`SaltoFinalDis.esperado.asm`](desensamblador/SaltoFinalDis.esperado.asm) |
| `desensamblador/DestinoCompartido.hack` | Dos saltos comparten destino y otra instrucción A con ese valor permanece en decimal; usa CRLF. | [`DestinoCompartidoDis.esperado.asm`](desensamblador/DestinoCompartidoDis.esperado.asm) |
| `desensamblador/SaltoExterno.hack` | El destino `32767` queda en decimal por estar fuera del programa; incluye `AM`, `AD` y no tiene salto de línea final. | [`SaltoExternoDis.esperado.asm`](desensamblador/SaltoExternoDis.esperado.asm) |

Las etiquetas generadas pueden tener nombres distintos en otra implementación;
los archivos esperados muestran los nombres usados por esta solución. Al volver
a ensamblar cada salida debe obtenerse la misma secuencia de instrucciones de
la entrada `.hack`.

## Ensamblador: casos de error

Cada entrada está en `errores/ensamblador/`. Debe mostrarse el mensaje
indicado, terminar con código distinto de cero y no quedar archivo `.hack` de
salida. Los números incluyen las líneas de comentarios y las líneas vacías.

| Entrada | Categoría | Resultado esperado |
| --- | --- | --- |
| `A_FueraDeRango.asm` | Instrucción A | Línea 4: constante `32768` fuera de rango. [`Mensaje`](errores/ensamblador/A_FueraDeRango.error.txt) |
| `ControlInvalido.asm` | Instrucción A | Línea 4: símbolo que empieza por dígito y contiene un carácter de control. [`Mensaje`](errores/ensamblador/ControlInvalido.error.txt) |
| `EtiquetaRepetida.asm` | Etiqueta | Línea 5: etiqueta `BUCLE` declarada dos veces. [`Mensaje`](errores/ensamblador/EtiquetaRepetida.error.txt) |
| `DestinoInvalido.asm` | Destino | Línea 4: destino `DM` inválido. [`Mensaje`](errores/ensamblador/DestinoInvalido.error.txt) |
| `ComputoInvalido.asm` | Cómputo | Línea 4: cómputo `A+D` inválido. [`Mensaje`](errores/ensamblador/ComputoInvalido.error.txt) |
| `SaltoInvalido.asm` | Salto | Línea 4: salto `jmp` inválido. [`Mensaje`](errores/ensamblador/SaltoInvalido.error.txt) |
| `EstructuraInvalida.asm` | Estructura | Línea 4: más de un `=`. [`Mensaje`](errores/ensamblador/EstructuraInvalida.error.txt) |

Varios de estos casos contienen además un error posterior, para comprobar que
se informa el primero en orden de línea.

## Desensamblador: casos de error

Cada entrada está en `errores/desensamblador/`. Debe mostrarse el mensaje
indicado, terminar con código distinto de cero y no quedar archivo `Dis.asm` de
salida.

| Entrada | Categoría | Resultado esperado |
| --- | --- | --- |
| `LongitudInvalida.hack` | Longitud | Línea 2: instrucción de 15 caracteres. [`Mensaje`](errores/desensamblador/LongitudInvalida.error.txt) |
| `RetornoAislado.hack` | Longitud | Línea 1: dos instrucciones separadas por un retorno de carro aislado forman una línea de 33 caracteres. [`Mensaje`](errores/desensamblador/RetornoAislado.error.txt) |
| `CaracterInvalido.hack` | Caracteres | Línea 2: carácter `2` inválido. [`Mensaje`](errores/desensamblador/CaracterInvalido.error.txt) |
| `PrefijoInvalido.hack` | Bits 14–13 | Línea 2: prefijo de instrucción C inválido. [`Mensaje`](errores/desensamblador/PrefijoInvalido.error.txt) |
| `ComputoInvalido.hack` | Cómputo | Línea 2: código de cómputo inválido. [`Mensaje`](errores/desensamblador/ComputoInvalido.error.txt) |
| `LineaVacia.hack` | Longitud | Línea 2: línea vacía en medio del archivo. [`Mensaje`](errores/desensamblador/LineaVacia.error.txt) |
| `LineaVaciaFinal.hack` | Longitud | Línea 2: línea vacía adicional al final del archivo. [`Mensaje`](errores/desensamblador/LineaVaciaFinal.error.txt) |
| `SoloLineasVacias.hack` | Longitud | Línea 1: archivo formado por líneas vacías con finales CRLF. [`Mensaje`](errores/desensamblador/SoloLineasVacias.error.txt) |
| `EspacioFinal.hack` | Longitud | Línea 2: instrucción con un espacio adicional. [`Mensaje`](errores/desensamblador/EspacioFinal.error.txt) |

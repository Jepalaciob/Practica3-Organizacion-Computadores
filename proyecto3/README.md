# Práctica 3 – Ensamblador y desensamblador Hack

## Estudiantes

- Juan Esteban Palacio Betancur
- Thomas Alejandro Serna Saldarriaga

## Descripción

La práctica implementa en Java un ensamblador y un desensamblador para el
lenguaje Hack de Nand2Tetris. El ensamblador traduce instrucciones A y C,
resuelve símbolos predefinidos, etiquetas y variables, y detecta errores de
entrada. El desensamblador traduce instrucciones binarias a ensamblador y
genera etiquetas para los destinos de salto identificados.

## Compilación y ejecución

### Dependencias

- Windows con [Git for Windows](https://git-scm.com/install/windows), que incluye Git Bash.
- JDK 11 o superior; el script compila con `javac --release 11`. Puede instalarse un JDK desde [Eclipse Temurin](https://adoptium.net/temurin/releases/).
- No se requieren bibliotecas externas.

Durante la instalación, agregue Git y el directorio `bin` del JDK al `PATH`.
Abra Git Bash y compruebe que los comandos siguientes muestren las versiones
instaladas:

```bash
git --version
java -version
javac -version
```

Desde la raíz del repositorio, ejecute:

```bash
cd proyecto3
./compilar.sh
./ensamblador.sh pruebas/ensamblador/Doble.asm
./desensamblador.sh pruebas/desensamblador/SaltoFinal.hack
```

`compilar.sh` guarda las clases compiladas en `out/`. El ensamblador recibe un
archivo `.asm` y crea un `.hack` con el mismo nombre en la carpeta de entrada.
El desensamblador recibe un archivo `.hack` y crea un archivo con el sufijo
`Dis.asm` en esa misma carpeta. Los archivos de salida existentes con esos
nombres se sobrescriben al traducir correctamente.

Las rutas pueden ser relativas o absolutas. Si contienen espacios, deben ir
entre comillas:

```bash
./ensamblador.sh "ruta con espacios/Programa.asm"
./desensamblador.sh "ruta con espacios/Programa.hack"
```

Ante un error en el contenido, el programa muestra en la salida estándar
`ERROR *** Línea N: <mensaje>` y termina con código distinto de cero. La línea
se cuenta desde 1 e incluye líneas vacías y comentarios. No queda archivo de
salida para esa ejecución; si ya existía un archivo con ese nombre, se elimina.
Cuando la traducción termina correctamente, el código de salida es cero.

Los casos propios, su resultado esperado y las instrucciones de comprobación
están en [`pruebas/README.md`](pruebas/README.md).

## Estructura del código

```text
proyecto3/
├── compilar.sh
├── ensamblador.sh
├── desensamblador.sh
├── README.md
├── ensamblador/
├── desensamblador/
├── pruebas/
└── DOCUMENTACION/
```

- `ensamblador/HackAssembler.java`: realiza las dos pasadas, resuelve símbolos y escribe el binario.
- `ensamblador/Parser.java`: lee las instrucciones y valida su sintaxis.
- `ensamblador/SymbolTable.java`: almacena símbolos predefinidos, etiquetas y variables.
- `ensamblador/Code.java`: contiene las tablas de traducción usadas por ambos programas.
- `ensamblador/HackException.java`: representa los errores con su número de línea.
- `desensamblador/HackDisassembler.java`: valida y traduce el binario y genera las etiquetas.

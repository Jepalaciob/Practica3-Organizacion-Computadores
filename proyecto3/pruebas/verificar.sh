#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BASE="$(cd "${TMPDIR:-/tmp}" && pwd)"
TEMP="$(mktemp -d "$BASE/pruebas-hack.XXXXXX")"
[[ "$TEMP" == "$BASE"/pruebas-hack.* ]] || exit 1
trap 'rm -rf -- "$TEMP"' EXIT
cp -R "$DIR/pruebas/." "$TEMP/"

comparar() {
    diff -u <(awk '{ sub(/\r$/, ""); print }' "$1") <(awk '{ sub(/\r$/, ""); print }' "$2")
}

total=0
for esperado in "$TEMP/ensamblador/"*.esperado.hack; do
    entrada="${esperado%.esperado.hack}.asm"
    "$DIR/ensamblador.sh" "$entrada" > "$TEMP/salida.txt" 2> "$TEMP/error.txt"
    test ! -s "$TEMP/error.txt"
    comparar "$esperado" "${entrada%.asm}.hack"
    echo "OK ensamblador: $(basename "$entrada")"
    total=$((total + 1))
done

for esperado in "$TEMP/desensamblador/"*Dis.esperado.asm; do
    entrada="${esperado%Dis.esperado.asm}.hack"
    "$DIR/desensamblador.sh" "$entrada" > "$TEMP/salida.txt" 2> "$TEMP/error.txt"
    test ! -s "$TEMP/error.txt"
    salida="${entrada%.hack}Dis.asm"
    comparar "$esperado" "$salida"
    "$DIR/ensamblador.sh" "$salida" > "$TEMP/salida.txt" 2> "$TEMP/error.txt"
    test ! -s "$TEMP/error.txt"
    comparar "$entrada" "${salida%.asm}.hack"
    echo "OK desensamblador e ida y vuelta: $(basename "$entrada")"
    total=$((total + 1))
done

for entrada in "$TEMP/errores/ensamblador/"*.asm "$TEMP/errores/desensamblador/"*.hack; do
    case "$entrada" in
        *.asm) programa=ensamblador; salida="${entrada%.asm}.hack" ;;
        *.hack) programa=desensamblador; salida="${entrada%.hack}Dis.asm" ;;
    esac
    if "$DIR/$programa.sh" "$entrada" > "$TEMP/salida.txt" 2> "$TEMP/error.txt"; then
        echo "FALLO: se acepto $(basename "$entrada")" >&2
        exit 1
    fi
    test ! -s "$TEMP/error.txt"
    comparar "${entrada%.*}.error.txt" "$TEMP/salida.txt"
    test ! -e "$salida"
    echo "OK error de $programa: $(basename "$entrada")"
    total=$((total + 1))
done

echo "$total pruebas correctas."

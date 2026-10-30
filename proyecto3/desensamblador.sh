#!/usr/bin/env bash
# Uso: ./desensamblador.sh ruta/Prog.hack  ->  genera ruta/ProgDis.asm
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
java -cp "$DIR/out" HackDisassembler "$@"

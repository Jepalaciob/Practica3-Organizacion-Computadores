#!/usr/bin/env bash
# Uso: ./ensamblador.sh ruta/Prog.asm  ->  genera ruta/Prog.hack
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
java -cp "$DIR/out" HackAssembler "$@"

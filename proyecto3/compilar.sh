#!/usr/bin/env bash
# Compila el ensamblador y el desensamblador en la carpeta out/.
cd "$(dirname "${BASH_SOURCE[0]}")" || exit 1
javac -encoding UTF-8 --release 11 -d out ensamblador/*.java desensamblador/*.java

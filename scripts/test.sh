#!/usr/bin/env bash
set -e

echo "=== Running MacroMod Test Suite ==="
./gradlew test --info
echo "✓ All tests executed successfully!"

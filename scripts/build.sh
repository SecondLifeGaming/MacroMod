#!/usr/bin/env bash
set -e

echo "=== Building MacroMod Ecosystem Artifacts ==="
mkdir -p release

VERSION=$(grep "^mod_version" gradle.properties | cut -d'=' -f2 | tr -d '[:space:]')

if [ -d "src" ]; then
    echo "1. Building Free Tier Client Mod..."
    ./gradlew clean jar -Ppremium=false
    cp "build/libs/macromod-${VERSION}-free.jar" "release/macromod-${VERSION}-free.jar" 2>/dev/null || true

    echo "2. Building Premium Tier Client Mod..."
    ./gradlew clean jar -Ppremium=true
    cp "build/libs/macromod-${VERSION}-premium.jar" "release/macromod-${VERSION}-premium.jar" 2>/dev/null || true
fi

if [ -d "macromod-server" ]; then
    echo "3. Building Universal Companion Server Plugin..."
    ./gradlew :macromod-server:build
    find macromod-server/common/build/libs/ -name "common-*.jar" -exec cp {} "release/macromod-server-${VERSION}.jar" \;
fi

echo ""
echo "=== Build Summary (release/ folder) ==="
ls -lh release/


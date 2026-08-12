#!/usr/bin/env bash
set -e

NEW_VER="$1"
if [ -z "$NEW_VER" ]; then
    echo "Usage: $0 <new_version>"
    exit 1
fi

OLD_VER=$(grep "^mod_version" gradle.properties | cut -d'=' -f2 | tr -d '[:space:]')
echo "Bumping MacroMod version from ${OLD_VER} to ${NEW_VER}..."

sed -i "s/mod_version=.*/mod_version=${NEW_VER}/" gradle.properties
sed -i "s/mod_version = .*/mod_version = ${NEW_VER}/" gradle.properties
if [ -f "src/main/resources/fabric.mod.json" ]; then
    sed -i "s/\"version\": \".*\"/\"version\": \"${NEW_VER}\"/" src/main/resources/fabric.mod.json
fi
if [ -f "src/main/java/com/github/westkevin12/macromod/MacroModClient.java" ]; then
    sed -i "s/\.orElse(\".*\")/\.orElse(\"${NEW_VER}\")/" src/main/java/com/github/westkevin12/macromod/MacroModClient.java
fi

if [ -f "macromod-server/build.gradle" ]; then
    sed -i "s/version = '.*'/version = '${NEW_VER}'/" macromod-server/build.gradle
fi
if [ -f "macromod-server/common/src/main/resources/paper-plugin.yml" ]; then
    sed -i "s/version: .*/version: ${NEW_VER}/" macromod-server/common/src/main/resources/paper-plugin.yml
fi
if [ -f "macromod-server/common/src/main/resources/plugin.yml" ]; then
    sed -i "s/version: .*/version: ${NEW_VER}/" macromod-server/common/src/main/resources/plugin.yml
fi
if [ -f "macromod-server/common/src/main/resources/bungee.yml" ]; then
    sed -i "s/version: .*/version: ${NEW_VER}/" macromod-server/common/src/main/resources/bungee.yml
fi
if [ -f "macromod-server/common/src/main/resources/velocity-plugin.json" ]; then
    sed -i "s/\"version\": \".*\"/\"version\": \"${NEW_VER}\"/" macromod-server/common/src/main/resources/velocity-plugin.json
fi
if [ -f "macromod-server/common/src/main/java/com/github/westkevin12/macromod/server/MacroModVelocityPlugin.java" ]; then
    sed -i "s/version = \".*\"/version = \"${NEW_VER}\"/" macromod-server/common/src/main/java/com/github/westkevin12/macromod/server/MacroModVelocityPlugin.java
fi


echo "✓ Updated version to ${NEW_VER} across all project configuration files."
echo ""
echo "⚠️ REMINDER: Please manually update release-notes.md and release-notes.bbcode with changelog details for v${NEW_VER}!"

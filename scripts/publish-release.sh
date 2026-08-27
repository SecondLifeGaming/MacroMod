#!/usr/bin/env bash
set -e

# -----------------------------------------------------------------------------
# MacroMod GitHub Release Publisher Script
# Automatically builds and publishes Free Client Mod and Universal Server Plugin
# JARs to GitHub Releases using the GitHub CLI (gh).
# -----------------------------------------------------------------------------

# 1. Verify GitHub CLI is installed
if ! command -v gh &> /dev/null; then
    echo "❌ Error: GitHub CLI ('gh') is not installed or not in PATH."
    echo "Please install gh CLI or follow https://cli.github.com/"
    exit 1
fi

# 2. Extract current version from gradle.properties
VERSION=$(grep "^mod_version" gradle.properties | cut -d'=' -f2 | tr -d '[:space:]')
TAG="v${VERSION}"

echo "=== Preparing GitHub Release for MacroMod ${TAG} ==="

# 3. Execute build script to ensure fresh release binaries exist
echo "Building release artifacts..."
./scripts/build.sh

FREE_JAR="release/macromod-${VERSION}-free.jar"
SERVER_JAR="release/macromod-server-${VERSION}.jar"

# 4. Verify output JAR files exist
MISSING=0
if [ ! -f "$FREE_JAR" ]; then
    echo "❌ Missing release asset: $FREE_JAR"
    MISSING=1
fi

if [ ! -f "$SERVER_JAR" ]; then
    echo "❌ Missing release asset: $SERVER_JAR"
    MISSING=1
fi

if [ "$MISSING" -ne 0 ]; then
    echo "❌ Build failed to generate required release assets."
    exit 1
fi

echo "Found release assets:"
echo "  - Client Mod (Free): $FREE_JAR"
echo "  - Server Companion: $SERVER_JAR"
echo ""

# 5. Check if release-notes.md exists
NOTES_ARG=()
if [ -f "release-notes.md" ]; then
    NOTES_ARG=(--notes-file "release-notes.md")
else
    NOTES_ARG=(--notes "MacroMod ${TAG} Release")
fi

# 6. Ensure annotated git tag exists locally and is pushed to remote
if ! git rev-parse "$TAG" >/dev/null 2>&1; then
    echo "🏷️ Creating local GPG-signed tag ${TAG}..."
    git tag -s "$TAG" -m "MacroMod ${TAG} Release"
fi

if ! git ls-remote --tags origin | grep -q "refs/tags/${TAG}"; then
    echo "📤 Pushing tag ${TAG} to remote..."
    git push origin "$TAG"
fi

# 7. Check if release already exists on GitHub
if gh release view "$TAG" &>/dev/null; then
    echo "⚠️ Release $TAG already exists on GitHub. Uploading / overwriting assets..."
    gh release upload "$TAG" "$FREE_JAR" "$SERVER_JAR" --clobber
else
    echo "🚀 Creating new GitHub Release $TAG..."
    gh release create "$TAG" \
        "$FREE_JAR" \
        "$SERVER_JAR" \
        --title "MacroMod ${TAG}" \
        "${NOTES_ARG[@]}"
fi

echo ""
echo "🎉 Release successfully published to GitHub!"
echo "View release at: https://github.com/SecondLifeGaming/MacroMod/releases/tag/${TAG}"

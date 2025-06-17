#!/bin/bash

set -e

echo "Starting fast build..."

./gradlew build \
  -x test \
  -x ktlintMainSourceSetCheck \
  -x ktlintKotlinScriptCheck \
  -x ktlintTestSourceSetCheck \
  --no-daemon \
  --parallel \
  --build-cache

if [ $? -eq 0 ]; then
    echo "📦 JAR location: build/libs/"
else
    echo "❌ Build failed!"
    exit 1
fi

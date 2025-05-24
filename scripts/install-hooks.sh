#!/bin/bash
echo "pre-commit hook 설치 중..."
cp .githooks/pre-push .git/hooks/pre-push
chmod +x .git/hooks/pre-push
echo "✅ 설치 완료!"

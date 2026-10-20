@echo off
title VitePress 开发模式（快速预览）

echo ==============================================
echo   VitePress 开发模式
echo   轻量快速，改完自动刷新
echo ==============================================
echo.

cd /d "%~dp0.."

echo 启动开发服务器...
echo.
call pnpm docs:dev

pause
@echo off
chcp 65001 >nul
title 语雀笔记打包
cd /d "%~dp0"

echo ========================================
echo  正在执行默认打包，请稍候...
echo ========================================
echo.

REM 优先用 python，找不到则退回 py 启动器
set "PY=python"
where python >nul 2>nul
if errorlevel 1 set "PY=py -3"

%PY% pack_yuque_notes.py

echo.
echo ========================================
echo  打包结束，按任意键关闭窗口。
echo ========================================
pause >nul

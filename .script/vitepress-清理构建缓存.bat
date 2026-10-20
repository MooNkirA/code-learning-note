@echo off
title VitePress 清理脚本

echo ==============================================
echo   VitePress 清理脚本
echo   清理构建产物与缓存
echo ==============================================
echo.

cd /d "%~dp0.."

echo [1/2] 清理构建产物 .vitepress/dist ...
if exist ".vitepress\dist" (
    rmdir /s /q ".vitepress\dist"
    echo       已删除 .vitepress\dist
) else (
    echo       .vitepress\dist 不存在，跳过
)

echo.
echo [2/2] 清理构建缓存 .vitepress/cache ...
if exist ".vitepress\cache" (
    rmdir /s /q ".vitepress\cache"
    echo       已删除 .vitepress\cache
) else (
    echo       .vitepress\cache 不存在，跳过
)

echo.
echo ==============================================
echo   清理完成
echo ==============================================
echo.
pause
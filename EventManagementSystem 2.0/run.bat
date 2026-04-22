@echo off
echo ========================================
echo   Event Management System
echo ========================================
echo.
echo Starting application...
echo.

cd /d "B:\EventManagementSystem 2.0\EventManagementSystem 2.0"

echo Compiling Java files...
javac -cp "lib\mysql-connector-j-9.6.0.jar" src\*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Compilation successful!
echo.
echo Running application...
echo.

java -cp "src;lib\mysql-connector-j-9.6.0.jar" Main

pause
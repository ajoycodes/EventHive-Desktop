@echo off
REM EventHive Desktop - Run Script for Windows
REM This script ensures the application runs with proper JavaFX configuration

echo Starting EventHive Desktop Application...
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Using Maven to run the application...
    call mvn clean javafx:run
) else (
    echo Maven not found. Please run from IntelliJ with VM options:
    echo --add-modules javafx.controls,javafx.fxml
    echo.
    echo Or install Maven and run: mvn clean javafx:run
)

pause


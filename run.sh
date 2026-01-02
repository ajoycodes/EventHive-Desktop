#!/bin/bash

# EventHive Desktop - Run Script
# This script ensures the application runs with proper JavaFX configuration

echo "Starting EventHive Desktop Application..."
echo ""

# Check if Maven is available
if command -v mvn &> /dev/null; then
    echo "Using Maven to run the application..."
    mvn clean javafx:run
else
    echo "Maven not found. Please run from IntelliJ with VM options:"
    echo "--add-modules javafx.controls,javafx.fxml"
    echo ""
    echo "Or install Maven and run: mvn clean javafx:run"
fi


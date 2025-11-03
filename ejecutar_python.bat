@echo off
echo ===============================================================
echo     EJECUTAR ANALISIS PYTHON
echo ===============================================================
echo.
echo Este script ejecutara todos los analisis Python
echo.

if not exist "datos_analisis\resumen_partidas.csv" (
    echo ERROR: No se encuentran los archivos CSV
    echo Primero ejecuta: ejecutar_5000_partidas.bat
    pause
    exit /b 1
)

echo Archivos CSV encontrados. Iniciando analisis...
echo.

echo [1/4] Analisis Visual (genera 7 graficos)...
python analizar_datos.py
if errorlevel 1 goto ERROR

echo.
echo [2/4] Analisis Estadistico (tests formales)...
python analisis_estadistico.py
if errorlevel 1 goto ERROR

echo.
echo [3/4] Predictor de Enfrentamientos...
python predictor_enfrentamientos.py
if errorlevel 1 goto ERROR

echo.
echo [4/4] Machine Learning...
python ml_predictor.py
if errorlevel 1 goto ERROR

echo.
echo ===============================================================
echo ANALISIS COMPLETADO!
echo ===============================================================
echo.
echo Revisa los archivos generados en: datos_analisis\
echo   - 10+ graficos PNG
echo   - Reportes de analisis
echo.
pause
exit /b 0

:ERROR
echo.
echo ERROR: El script Python fallo
echo.
echo Verifica:
echo   1. Python instalado: python --version
echo   2. Dependencias: pip install -r requirements.txt
echo.
pause
exit /b 1

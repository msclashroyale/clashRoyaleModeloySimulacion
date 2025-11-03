@echo off
echo ===============================================================
echo     EJECUTAR 5000 PARTIDAS - ANALISIS COMPLETO
echo ===============================================================
echo.
echo Este script ejecutara 5000 partidas para analisis robusto
echo Tiempo estimado: 30-45 minutos
echo.
pause
echo.

echo Paso 1: Compilando proyecto...
call mvn clean compile
if errorlevel 1 (
    echo.
    echo ERROR: La compilacion fallo
    echo Verifica que Maven este instalado: mvn --version
    pause
    exit /b 1
)

echo.
echo Paso 2: Ejecutando 5000 partidas...
echo (Se mostrara progreso cada 100 partidas)
echo.
java -cp target/classes analisis.EjemploAnalisisExtendido
if errorlevel 1 (
    echo.
    echo ERROR: La ejecucion fallo
    pause
    exit /b 1
)

echo.
echo ===============================================================
echo SIMULACION COMPLETADA!
echo ===============================================================
echo.
echo Archivos CSV generados en: datos_analisis\
echo.
echo Ahora ejecuta: ejecutar_python.bat
echo O manualmente:
echo   python analizar_datos.py
echo   python analisis_estadistico.py
echo   python predictor_enfrentamientos.py
echo   python ml_predictor.py
echo.
pause

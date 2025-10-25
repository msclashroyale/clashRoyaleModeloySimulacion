@echo off
cls

echo ============================================================
echo     Sistema de Analisis - Clash Royale Simulation
echo ============================================================
echo.

:menu
echo.
echo Selecciona una opcion:
echo.
echo   [1] Compilar proyecto
echo   [2] Generar datos (ejecutar 100 partidas)
echo   [3] Analisis completo con graficos (Python)
echo   [4] Analisis rapido en consola (Python)
echo   [5] Analisis estadistico avanzado (Python)
echo   [6] Limpiar datos anteriores
echo   [7] Ver archivos generados
echo   [0] Salir
echo.
set /p opcion="Opcion: "

if "%opcion%"=="1" goto compilar
if "%opcion%"=="2" goto generar
if "%opcion%"=="3" goto analizar_completo
if "%opcion%"=="4" goto analizar_rapido
if "%opcion%"=="5" goto analizar_estadistico
if "%opcion%"=="6" goto limpiar
if "%opcion%"=="7" goto ver_archivos
if "%opcion%"=="0" goto salir
echo.
echo Opcion no valida. Intenta de nuevo.
goto menu

:compilar
echo.
echo ============================================================
echo Compilando proyecto...
echo ============================================================
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] La compilacion fallo. Revisa los errores arriba.
    echo.
) else (
    echo.
    echo [OK] Compilacion exitosa.
    echo.
)
pause
goto menu

:generar
echo.
echo ============================================================
echo Generando datos (esto puede tomar unos minutos)...
echo ============================================================
echo.

if not exist "target\classes\analisis\EjemploAnalisis.class" (
    echo [ERROR] El proyecto no esta compilado.
    echo Ejecuta primero la opcion [1] para compilar.
    echo.
    pause
    goto menu
)

java -cp target/classes analisis.EjemploAnalisis
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Ocurrio un error al generar datos.
    echo.
) else (
    echo.
    echo [OK] Datos generados exitosamente.
    echo.
)
pause
goto menu

:analizar_completo
echo.
echo ============================================================
echo Ejecutando analisis completo con visualizaciones...
echo ============================================================
echo.

if not exist "datos_analisis\resumen_partidas.csv" (
    echo [ERROR] No se encontraron los archivos de datos.
    echo Ejecuta primero la opcion [2] para generar datos.
    echo.
    pause
    goto menu
)

python analizar_datos.py
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Ocurrio un error en el analisis.
    echo Verifica que tengas Python y las dependencias instaladas:
    echo    pip install -r requirements.txt
    echo.
)
pause
goto menu

:analizar_rapido
echo.
echo ============================================================
echo Ejecutando analisis rapido...
echo ============================================================
echo.

if not exist "datos_analisis\resumen_partidas.csv" (
    echo [ERROR] No se encontraron los archivos de datos.
    echo Ejecuta primero la opcion [2] para generar datos.
    echo.
    pause
    goto menu
)

python analisis_rapido.py
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Ocurrio un error en el analisis.
    echo.
)
pause
goto menu

:analizar_estadistico
echo.
echo ============================================================
echo Ejecutando analisis estadistico avanzado...
echo ============================================================
echo.

if not exist "datos_analisis\resumen_partidas.csv" (
    echo [ERROR] No se encontraron los archivos de datos.
    echo Ejecuta primero la opcion [2] para generar datos.
    echo.
    pause
    goto menu
)

python analisis_estadistico.py
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Ocurrio un error en el analisis.
    echo.
)
pause
goto menu

:limpiar
echo.
echo ============================================================
echo Limpiando datos anteriores...
echo ============================================================
echo.
if exist datos_analisis\*.csv (
    del /Q datos_analisis\*.csv
    echo OK - Archivos CSV eliminados
)
if exist datos_analisis\*.png (
    del /Q datos_analisis\*.png
    echo OK - Archivos PNG eliminados
)
if exist datos_analisis\*.txt (
    del /Q datos_analisis\*.txt
    echo OK - Archivos TXT eliminados
)
echo.
echo Limpieza completada
echo.
pause
goto menu

:ver_archivos
echo.
echo ============================================================
echo Archivos en datos_analisis/
echo ============================================================
echo.
if exist datos_analisis (
    dir /B datos_analisis
) else (
    echo No se encontro la carpeta datos_analisis
)
echo.
pause
goto menu

:salir
echo.
echo Hasta luego!
echo.
timeout /t 2 > nul
exit

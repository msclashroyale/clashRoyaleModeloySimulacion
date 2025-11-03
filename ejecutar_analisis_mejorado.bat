@echo off
chcp 65001 > nul
title Sistema de Análisis - Clash Royale Simulation

:MENU
cls
echo ╔═════════════════════════════════════════════════════════════╗
echo ║      SISTEMA DE ANÁLISIS - CLASH ROYALE SIMULATION         ║
echo ╚═════════════════════════════════════════════════════════════╝
echo.
echo Selecciona una opción:
echo.
echo   [1] Análisis RÁPIDO    (100 partidas   - ~1 minuto)
echo   [2] Análisis COMPLETO  (5000 partidas  - ~30-45 minutos) ⭐ RECOMENDADO
echo   [3] Análisis CUSTOM    (tú eliges el número)
echo.
echo   [4] Solo ejecutar scripts Python (requiere CSV existentes)
echo   [5] Ver estadísticas de archivos actuales
echo.
echo   [0] Salir
echo.
set /p opcion="Tu opción: "

if "%opcion%"=="1" goto RAPIDO
if "%opcion%"=="2" goto COMPLETO
if "%opcion%"=="3" goto CUSTOM
if "%opcion%"=="4" goto PYTHON
if "%opcion%"=="5" goto STATS
if "%opcion%"=="0" goto FIN
goto MENU

:RAPIDO
cls
echo ╔═════════════════════════════════════════════════════════════╗
echo ║              ANÁLISIS RÁPIDO - 100 PARTIDAS                ║
echo ╚═════════════════════════════════════════════════════════════╝
echo.
echo ⚠️  ATENCIÓN: 100 partidas son insuficientes para análisis robusto
echo    Para resultados confiables, usa la opción 2 (5000 partidas)
echo.
pause
echo.
echo Compilando proyecto...
call mvn clean compile
if errorlevel 1 goto ERROR_COMPILE
echo.
echo Ejecutando 100 partidas...
java -cp target/classes analisis.EjemploAnalisis
if errorlevel 1 goto ERROR_EXEC
goto PREGUNTAR_PYTHON

:COMPLETO
cls
echo ╔═════════════════════════════════════════════════════════════╗
echo ║            ANÁLISIS COMPLETO - 5000 PARTIDAS ⭐             ║
echo ╚═════════════════════════════════════════════════════════════╝
echo.
echo ✅ Esta es la opción RECOMENDADA para análisis robusto
echo.
echo Estimación de tiempo: 30-45 minutos
echo El script mostrará progreso cada 100 partidas
echo.
pause
echo.
echo Compilando proyecto...
call mvn clean compile
if errorlevel 1 goto ERROR_COMPILE
echo.
echo Ejecutando 5000 partidas...
echo (Puedes minimizar esta ventana mientras se ejecuta)
echo.
java -cp target/classes analisis.EjemploAnalisisExtendido
if errorlevel 1 goto ERROR_EXEC
goto PREGUNTAR_PYTHON

:CUSTOM
cls
echo ╔═════════════════════════════════════════════════════════════╗
echo ║              ANÁLISIS PERSONALIZADO                        ║
echo ╚═════════════════════════════════════════════════════════════╝
echo.
set /p num_partidas="¿Cuántas partidas deseas ejecutar? (recomendado: 5000): "
echo.
echo Ejecutarás %num_partidas% partidas
echo Tiempo estimado: aproximadamente %num_partidas%/10 minutos
echo.
pause
echo.
echo ⚠️  NOTA: Necesitarás modificar manualmente el archivo Java
echo    Abre: src\main\java\analisis\EjemploAnalisis.java
echo    Cambia: int numeroPartidas = 100;  
echo    Por:    int numeroPartidas = %num_partidas%;
echo.
echo ¿Ya modificaste el archivo? (S/N)
set /p modificado="Respuesta: "
if /i not "%modificado%"=="S" goto MENU
echo.
echo Compilando proyecto...
call mvn clean compile
if errorlevel 1 goto ERROR_COMPILE
echo.
echo Ejecutando %num_partidas% partidas...
java -cp target/classes analisis.EjemploAnalisis
if errorlevel 1 goto ERROR_EXEC
goto PREGUNTAR_PYTHON

:PREGUNTAR_PYTHON
echo.
echo ═════════════════════════════════════════════════════════════
echo ✅ Simulación completada con éxito!
echo ═════════════════════════════════════════════════════════════
echo.
echo ¿Deseas ejecutar los scripts de análisis Python ahora? (S/N)
set /p ejecutar_python="Respuesta: "
if /i "%ejecutar_python%"=="S" goto PYTHON
goto MENU

:PYTHON
cls
echo ╔═════════════════════════════════════════════════════════════╗
echo ║              EJECUTANDO ANÁLISIS PYTHON                    ║
echo ╚═════════════════════════════════════════════════════════════╝
echo.
echo Verificando archivos CSV...
if not exist "datos_analisis\resumen_partidas.csv" goto ERROR_CSV
echo ✓ CSV encontrados
echo.
echo ┌─────────────────────────────────────────────────────────────┐
echo │ 1/4 - Análisis Visual (genera 7 gráficos)                  │
echo └─────────────────────────────────────────────────────────────┘
python analizar_datos.py
if errorlevel 1 goto ERROR_PYTHON
echo.
echo ┌─────────────────────────────────────────────────────────────┐
echo │ 2/4 - Análisis Estadístico (tests formales)                │
echo └─────────────────────────────────────────────────────────────┘
python analisis_estadistico.py
if errorlevel 1 goto ERROR_PYTHON
echo.
echo ┌─────────────────────────────────────────────────────────────┐
echo │ 3/4 - Predictor de Enfrentamientos                         │
echo └─────────────────────────────────────────────────────────────┘
python predictor_enfrentamientos.py
if errorlevel 1 goto ERROR_PYTHON
echo.
echo ┌─────────────────────────────────────────────────────────────┐
echo │ 4/4 - Machine Learning (entrenamiento de modelos)          │
echo └─────────────────────────────────────────────────────────────┘
python ml_predictor.py
if errorlevel 1 goto ERROR_PYTHON
echo.
echo ═════════════════════════════════════════════════════════════
echo ✅ ¡ANÁLISIS COMPLETO!
echo ═════════════════════════════════════════════════════════════
echo.
echo Archivos generados en: datos_analisis\
echo   • 3 archivos CSV con datos
echo   • 10+ gráficos PNG
echo   • Reportes de análisis
echo.
pause
goto MENU

:STATS
cls
echo ╔═════════════════════════════════════════════════════════════╗
echo ║           ESTADÍSTICAS DE ARCHIVOS ACTUALES                ║
echo ╚═════════════════════════════════════════════════════════════╝
echo.
if not exist "datos_analisis" (
    echo ❌ La carpeta datos_analisis no existe
    echo    Ejecuta primero la opción 1 o 2 para generar datos
    echo.
    pause
    goto MENU
)

cd datos_analisis
echo Archivos CSV:
echo ─────────────────────────────────────────────────────────────
if exist "resumen_partidas.csv" (
    for %%A in (resumen_partidas.csv) do (
        echo   ✓ resumen_partidas.csv - %%~zA bytes
        
        REM Contar líneas (aproximado)
        for /f %%B in ('find /c /v "" ^< resumen_partidas.csv') do set lineas=%%B
        set /a partidas=lineas-1
        echo     Partidas: !partidas!
    )
) else (
    echo   ❌ resumen_partidas.csv - No encontrado
)

if exist "estadisticas_jugadores.csv" (
    for %%A in (estadisticas_jugadores.csv) do echo   ✓ estadisticas_jugadores.csv - %%~zA bytes
) else (
    echo   ❌ estadisticas_jugadores.csv - No encontrado
)

if exist "eventos_partidas.csv" (
    for %%A in (eventos_partidas.csv) do echo   ✓ eventos_partidas.csv - %%~zA bytes
) else (
    echo   ❌ eventos_partidas.csv - No encontrado
)

echo.
echo Gráficos generados:
echo ─────────────────────────────────────────────────────────────
set graficos=0
for %%F in (*.png) do (
    echo   ✓ %%F
    set /a graficos+=1
)
if %graficos%==0 echo   ❌ Ningún gráfico encontrado (ejecuta análisis Python)
echo.
echo Total de gráficos: %graficos%
echo.
cd ..
pause
goto MENU

:ERROR_COMPILE
echo.
echo ═════════════════════════════════════════════════════════════
echo ❌ ERROR DE COMPILACIÓN
echo ═════════════════════════════════════════════════════════════
echo.
echo Posibles causas:
echo   • Maven no está instalado o no está en el PATH
echo   • Errores en el código Java
echo   • Permisos insuficientes
echo.
echo Soluciones:
echo   1. Verifica que Maven esté instalado: mvn --version
echo   2. Revisa los errores mostrados arriba
echo   3. Intenta ejecutar: mvn clean install
echo.
pause
goto MENU

:ERROR_EXEC
echo.
echo ═════════════════════════════════════════════════════════════
echo ❌ ERROR DE EJECUCIÓN
echo ═════════════════════════════════════════════════════════════
echo.
echo Posibles causas:
echo   • La compilación no fue exitosa
echo   • Errores en tiempo de ejecución
echo   • Memoria insuficiente
echo.
echo Soluciones:
echo   1. Revisa los errores mostrados arriba
echo   2. Intenta compilar de nuevo: mvn clean compile
echo   3. Si es problema de memoria, reduce el número de partidas
echo.
pause
goto MENU

:ERROR_PYTHON
echo.
echo ═════════════════════════════════════════════════════════════
echo ❌ ERROR EN SCRIPT PYTHON
echo ═════════════════════════════════════════════════════════════
echo.
echo Posibles causas:
echo   • Python no está instalado o no está en el PATH
echo   • Faltan dependencias (pandas, matplotlib, etc.)
echo   • Los archivos CSV no fueron generados correctamente
echo.
echo Soluciones:
echo   1. Verifica Python: python --version
echo   2. Instala dependencias: pip install -r requirements.txt
echo   3. Verifica que existan los CSV en datos_analisis\
echo.
pause
goto MENU

:ERROR_CSV
echo.
echo ═════════════════════════════════════════════════════════════
echo ❌ ARCHIVOS CSV NO ENCONTRADOS
echo ═════════════════════════════════════════════════════════════
echo.
echo Los archivos CSV no existen en datos_analisis\
echo.
echo Debes ejecutar primero la opción 1 o 2 para generar los datos.
echo.
pause
goto MENU

:FIN
cls
echo.
echo ╔═════════════════════════════════════════════════════════════╗
echo ║                    ¡HASTA LUEGO!                           ║
echo ╚═════════════════════════════════════════════════════════════╝
echo.
echo 📊 Recuerda:
echo    • Para análisis robusto: usa 5000 partidas (opción 2)
echo    • Revisa los gráficos en: datos_analisis\
echo    • Lee el documento: ANALISIS_Y_RECOMENDACIONES.md
echo.
timeout /t 3
exit

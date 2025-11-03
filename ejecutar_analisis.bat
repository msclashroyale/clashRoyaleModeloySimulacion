@echo off
chcp 65001 > nul
title Sistema de Analisis - Clash Royale Simulation

:MENU
cls
echo ===============================================================
echo      SISTEMA DE ANALISIS - CLASH ROYALE SIMULATION
echo ===============================================================
echo.
echo Selecciona una opcion:
echo.
echo   [1] Analisis RAPIDO    (100 partidas   - ~1 minuto)
echo   [2] Analisis COMPLETO  (5000 partidas  - ~30-45 minutos) *** RECOMENDADO ***
echo   [3] Analisis CUSTOM    (tu eliges el numero)
echo.
echo   [4] Solo ejecutar scripts Python (requiere CSV existentes)
echo   [5] Ver estadisticas de archivos actuales
echo.
echo   [0] Salir
echo.
set /p opcion="Tu opcion: "

if "%opcion%"=="1" goto RAPIDO
if "%opcion%"=="2" goto COMPLETO
if "%opcion%"=="3" goto CUSTOM
if "%opcion%"=="4" goto PYTHON
if "%opcion%"=="5" goto STATS
if "%opcion%"=="0" goto FIN
goto MENU

:RAPIDO
cls
echo ===============================================================
echo              ANALISIS RAPIDO - 100 PARTIDAS
echo ===============================================================
echo.
echo ATENCION: 100 partidas son insuficientes para analisis robusto
echo Para resultados confiables, usa la opcion 2 (5000 partidas)
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
echo ===============================================================
echo            ANALISIS COMPLETO - 5000 PARTIDAS
echo ===============================================================
echo.
echo Esta es la opcion RECOMENDADA para analisis robusto
echo.
echo Estimacion de tiempo: 30-45 minutos
echo El script mostrara progreso cada 100 partidas
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
echo ===============================================================
echo              ANALISIS PERSONALIZADO
echo ===============================================================
echo.
set /p num_partidas="Cuantas partidas deseas ejecutar? (recomendado: 5000): "
echo.
echo Ejecutaras %num_partidas% partidas
echo Tiempo estimado: aproximadamente %num_partidas%/10 minutos
echo.
pause
echo.
echo NOTA: Necesitaras modificar manualmente el archivo Java
echo    Abre: src\main\java\analisis\EjemploAnalisis.java
echo    Cambia: int numeroPartidas = 100;  
echo    Por:    int numeroPartidas = %num_partidas%;
echo.
echo Ya modificaste el archivo? (S/N)
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
echo ===============================================================
echo SIMULACION COMPLETADA CON EXITO!
echo ===============================================================
echo.
echo Deseas ejecutar los scripts de analisis Python ahora? (S/N)
set /p ejecutar_python="Respuesta: "
if /i "%ejecutar_python%"=="S" goto PYTHON
goto MENU

:PYTHON
cls
echo ===============================================================
echo              EJECUTANDO ANALISIS PYTHON
echo ===============================================================
echo.
echo Verificando archivos CSV...
if not exist "datos_analisis\resumen_partidas.csv" goto ERROR_CSV
echo CSV encontrados
echo.
echo ---------------------------------------------------------------
echo 1/4 - Analisis Visual (genera 7 graficos)
echo ---------------------------------------------------------------
python analizar_datos.py
if errorlevel 1 goto ERROR_PYTHON
echo.
echo ---------------------------------------------------------------
echo 2/4 - Analisis Estadistico (tests formales)
echo ---------------------------------------------------------------
python analisis_estadistico.py
if errorlevel 1 goto ERROR_PYTHON
echo.
echo ---------------------------------------------------------------
echo 3/4 - Predictor de Enfrentamientos
echo ---------------------------------------------------------------
python predictor_enfrentamientos.py
if errorlevel 1 goto ERROR_PYTHON
echo.
echo ---------------------------------------------------------------
echo 4/4 - Machine Learning (entrenamiento de modelos)
echo ---------------------------------------------------------------
python ml_predictor.py
if errorlevel 1 goto ERROR_PYTHON
echo.
echo ===============================================================
echo ANALISIS COMPLETO!
echo ===============================================================
echo.
echo Archivos generados en: datos_analisis\
echo   - 3 archivos CSV con datos
echo   - 10+ graficos PNG
echo   - Reportes de analisis
echo.
pause
goto MENU

:STATS
cls
echo ===============================================================
echo           ESTADISTICAS DE ARCHIVOS ACTUALES
echo ===============================================================
echo.
if not exist "datos_analisis" (
    echo La carpeta datos_analisis no existe
    echo Ejecuta primero la opcion 1 o 2 para generar datos
    echo.
    pause
    goto MENU
)

cd datos_analisis
echo Archivos CSV:
echo ---------------------------------------------------------------
if exist "resumen_partidas.csv" (
    for %%A in (resumen_partidas.csv) do (
        echo   resumen_partidas.csv - %%~zA bytes
    )
) else (
    echo   resumen_partidas.csv - No encontrado
)

if exist "estadisticas_jugadores.csv" (
    for %%A in (estadisticas_jugadores.csv) do echo   estadisticas_jugadores.csv - %%~zA bytes
) else (
    echo   estadisticas_jugadores.csv - No encontrado
)

if exist "eventos_partidas.csv" (
    for %%A in (eventos_partidas.csv) do echo   eventos_partidas.csv - %%~zA bytes
) else (
    echo   eventos_partidas.csv - No encontrado
)

echo.
echo Graficos generados:
echo ---------------------------------------------------------------
dir /b *.png 2>nul
if errorlevel 1 echo   Ningun grafico encontrado (ejecuta analisis Python)
echo.
cd ..
pause
goto MENU

:ERROR_COMPILE
echo.
echo ===============================================================
echo ERROR DE COMPILACION
echo ===============================================================
echo.
echo Posibles causas:
echo   - Maven no esta instalado o no esta en el PATH
echo   - Errores en el codigo Java
echo   - Permisos insuficientes
echo.
echo Soluciones:
echo   1. Verifica que Maven este instalado: mvn --version
echo   2. Revisa los errores mostrados arriba
echo   3. Intenta ejecutar: mvn clean install
echo.
pause
goto MENU

:ERROR_EXEC
echo.
echo ===============================================================
echo ERROR DE EJECUCION
echo ===============================================================
echo.
echo Posibles causas:
echo   - La compilacion no fue exitosa
echo   - Errores en tiempo de ejecucion
echo   - Memoria insuficiente
echo.
echo Soluciones:
echo   1. Revisa los errores mostrados arriba
echo   2. Intenta compilar de nuevo: mvn clean compile
echo   3. Si es problema de memoria, reduce el numero de partidas
echo.
pause
goto MENU

:ERROR_PYTHON
echo.
echo ===============================================================
echo ERROR EN SCRIPT PYTHON
echo ===============================================================
echo.
echo Posibles causas:
echo   - Python no esta instalado o no esta en el PATH
echo   - Faltan dependencias (pandas, matplotlib, etc.)
echo   - Los archivos CSV no fueron generados correctamente
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
echo ===============================================================
echo ARCHIVOS CSV NO ENCONTRADOS
echo ===============================================================
echo.
echo Los archivos CSV no existen en datos_analisis\
echo.
echo Debes ejecutar primero la opcion 1 o 2 para generar los datos.
echo.
pause
goto MENU

:FIN
cls
echo.
echo ===============================================================
echo                    HASTA LUEGO!
echo ===============================================================
echo.
echo Recuerda:
echo    - Para analisis robusto: usa 5000 partidas (opcion 2)
echo    - Revisa los graficos en: datos_analisis\
echo    - Lee el documento: ANALISIS_Y_RECOMENDACIONES.md
echo.
timeout /t 3
exit

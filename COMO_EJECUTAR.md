# COMO EJECUTAR EL ANALISIS - GUIA RAPIDA

## OPCION 1: Comandos Directos (MAS SIMPLE)

### Paso 1: Ejecutar 5000 partidas
```bash
ejecutar_5000_partidas.bat
```
Tiempo: 30-45 minutos

### Paso 2: Ejecutar analisis Python
```bash
ejecutar_python.bat
```
Tiempo: 5-10 minutos

---

## OPCION 2: Menu Interactivo

```bash
ejecutar_analisis.bat
```

Luego selecciona: [2] Analisis COMPLETO (5000 partidas)

---

## OPCION 3: Comandos Manuales

### Compilar y ejecutar Java:
```bash
mvn clean compile
java -cp target/classes analisis.EjemploAnalisisExtendido
```

### Ejecutar Python:
```bash
python analizar_datos.py
python analisis_estadistico.py
python predictor_enfrentamientos.py
python ml_predictor.py
```

---

## ARCHIVOS CREADOS

- `ejecutar_5000_partidas.bat` - Ejecuta simulacion (RECOMENDADO)
- `ejecutar_python.bat` - Ejecuta analisis Python
- `ejecutar_analisis.bat` - Menu interactivo completo

---

## SI HAY ERRORES

### Error al compilar:
```bash
mvn clean install
```

### Error con Python:
```bash
pip install -r requirements.txt
```

### Error "comando no reconocido":
Usa PowerShell o CMD como administrador

---

## ORDEN RECOMENDADO

1. Lee: `REVISION_COMPLETA.md`
2. Ejecuta: `ejecutar_5000_partidas.bat`
3. Ejecuta: `ejecutar_python.bat`
4. Revisa: `datos_analisis/` (graficos y CSV)
5. Lee: `INFORMACION_UTIL_JUGADORES.md`
6. Actualiza tu informe con las conclusiones

---

**IMPORTANTE:** Usa 5000 partidas, no 100. 
Es la diferencia entre resultados confiables y no confiables.

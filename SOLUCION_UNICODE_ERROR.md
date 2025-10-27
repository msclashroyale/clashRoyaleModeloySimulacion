# 🐛 Solución: Error UnicodeDecodeError

## ❌ Error Completo

```
UnicodeDecodeError: 'utf-8' codec can't decode byte 0xe1 in position 474: invalid continuation byte
```

## 🔍 ¿Qué Significa Este Error?

Este error ocurre cuando Python intenta leer un archivo CSV que tiene **caracteres especiales** (tildes, ñ, etc.) pero la codificación no es correcta.

---

## ✅ Solución (Ya Implementada)

**El proyecto ya está arreglado.** Solo necesitas:

### 1. Actualizar el código

```bash
git pull origin feature/sistema-analisis-datos
```

### 2. Recompilar y regenerar los datos

```bash
# Recompilar
mvn clean compile

# Generar datos nuevamente (esto es IMPORTANTE)
java -cp target/classes analisis.EjemploAnalisis
```

### 3. Ejecutar el análisis

```bash
python analizar_datos.py
```

**Debería funcionar ahora.** ✅

---

## 🔧 ¿Qué se Arregló?

### En Java (ExportadorCSV.java):
Ahora los CSVs se generan con codificación UTF-8 explícita:

```java
// ANTES (podía causar problemas)
new PrintWriter(new FileWriter(rutaArchivo))

// DESPUÉS (correcto)
new PrintWriter(new OutputStreamWriter(
    new FileOutputStream(rutaArchivo), 
    StandardCharsets.UTF_8))
```

### En Python (todos los scripts):
Ahora los CSVs se leen con codificación UTF-8 explícita:

```python
# ANTES
pd.read_csv("archivo.csv")

# DESPUÉS
pd.read_csv("archivo.csv", encoding='utf-8')
```

---

## 🎯 Si el Error Persiste

### Opción 1: Eliminar CSVs viejos

```bash
# Eliminar datos antiguos
cd datos_analisis
del *.csv

# O usar el menú
ejecutar_analisis.bat
# Opción [7] Limpiar datos anteriores
```

### Opción 2: Regenerar desde cero

```bash
# 1. Limpiar completamente
mvn clean

# 2. Compilar
mvn compile

# 3. Generar datos nuevos
java -cp target/classes analisis.EjemploAnalisis
```

### Opción 3: Verificar encoding manualmente (Python)

```python
import pandas as pd

# Probar diferentes encodings
encodings = ['utf-8', 'latin-1', 'cp1252', 'iso-8859-1']

for enc in encodings:
    try:
        df = pd.read_csv('datos_analisis/resumen_partidas.csv', encoding=enc)
        print(f"✓ Funcionó con: {enc}")
        break
    except:
        print(f"✗ No funcionó con: {enc}")
```

---

## 📋 Verificar que Funciona

Ejecuta esto para confirmar:

```bash
python -c "import pandas as pd; df = pd.read_csv('datos_analisis/resumen_partidas.csv', encoding='utf-8'); print('OK')"
```

Debería imprimir: `OK`

---

## 🤔 ¿Por Qué Pasaba Esto?

1. **Java sin encoding explícito**: Usaba la codificación predeterminada del sistema (Windows-1252 en Windows)
2. **Python esperaba UTF-8**: Por defecto pandas espera UTF-8
3. **Caracteres especiales**: Tildes (á, é, í), ñ, etc. se codifican diferente en Windows-1252 vs UTF-8
4. **Resultado**: Conflicto al leer los archivos

---

## 🎓 Nota sobre JDK 17 vs 21

La versión del JDK (17.0.1 en tu caso) **NO causa este error**. El error es puramente de codificación de archivos.

**JDK 17 funciona perfectamente** para este proyecto. No necesitas actualizar a JDK 21.

---

## ✅ Checklist de Solución

- [ ] `git pull` para obtener los cambios
- [ ] `mvn clean compile` para recompilar
- [ ] Eliminar CSVs viejos de `datos_analisis/`
- [ ] `java -cp target/classes analisis.EjemploAnalisis` para regenerar datos
- [ ] `python analizar_datos.py` para verificar que funciona

---

## 📞 Si Aún No Funciona

1. Copia el mensaje de error completo
2. Ejecuta: `python --version`
3. Ejecuta: `java -version`
4. Comparte en el grupo con esa información

---

_Este error está solucionado en la rama `feature/sistema-analisis-datos`_

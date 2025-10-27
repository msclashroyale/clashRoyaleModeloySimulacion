# 📋 Instrucciones de Setup - Sistema de Análisis de Datos

## 🆕 Cambios en esta actualización

Se ha agregado un **sistema completo de análisis de datos y Machine Learning** para el simulador. Incluye:

- ✅ Recolección automática de datos de partidas
- ✅ Exportación a CSV
- ✅ Análisis estadístico avanzado
- ✅ Visualizaciones con gráficos
- ✅ Reportes automáticos
- ✅ **NUEVO: Predictor con Machine Learning (scikit-learn)**

---

## 🔧 Setup Inicial (Solo la primera vez)

### 1. Actualizar tu repositorio local

```bash
# Asegúrate de estar en la rama correcta
git checkout analisis

# Descarga los cambios
git pull origin analisis
```

### 2. Instalar dependencias Python (OPCIONAL)

**⚠️ Solo si quieres usar el análisis con gráficos o Machine Learning:**

```bash
pip install -r requirements.txt
```

**Dependencias que instala:**
- pandas (análisis de datos)
- matplotlib (gráficos)
- seaborn (visualizaciones)
- numpy (cálculos)
- scipy (estadística)
- **scikit-learn (Machine Learning)** ⭐ NUEVO

### 3. Compilar el proyecto

```bash
mvn clean compile
```

**⚠️ Si te da error "mvn no se reconoce":** Maven no está instalado. Lee **`INSTALAR_MAVEN.md`** para instalarlo.

---

## 🎮 Uso del Sistema

### Opción A: Menú Interactivo (Recomendado)

**Windows:**
```cmd
ejecutar_analisis.bat
```
```windows powershell
.\ejecutar_analisis.bat
```

Luego selecciona:
- `[1]` Compilar proyecto
- `[2]` Generar datos (100 partidas)
- `[3]` Análisis completo con gráficos
- `[4]` Análisis rápido en consola
- `[5]` Análisis estadístico avanzado

### Opción B: Comandos Manuales

```bash
# 1. Generar datos
java -cp target/classes analisis.EjemploAnalisis

# 2. Analizar con Python (opcional)
python analizar_datos.py

# 3. Análisis estadístico (opcional)
python analisis_estadistico.py

# 4. Machine Learning (opcional) ⭐ NUEVO
python ml_predictor.py
```

---

## 🤖 Machine Learning - NUEVO

### ¿Qué hace el predictor?

Entrena modelos de Machine Learning para **predecir el ganador** de una partida basándose en las métricas del juego.

**Modelos que entrena:**
1. Random Forest
2. Gradient Boosting
3. Logistic Regression
4. Decision Tree

### Cómo usar

```bash
# 1. Primero genera los datos
java -cp target/classes analisis.EjemploAnalisis

# 2. Instala scikit-learn (si no lo tienes)
pip install scikit-learn

# 3. Entrena el modelo
python ml_predictor.py
```

### Resultados que genera

1. **Comparación de modelos** - ¿Cuál predice mejor?
2. **Matriz de confusión** - Visualización de aciertos/errores
3. **Importancia de features** - ¿Qué métricas son más importantes?
4. **Predicciones de ejemplo** - Prueba el modelo entrenado
5. **Métricas de rendimiento** - Accuracy, precision, recall, F1-score

### Features que usa el modelo

El predictor usa **19 características** diferentes:

**Estrategias:**
- Estrategia del Jugador 1
- Estrategia del Jugador 2

**Niveles:**
- Nivel de cada jugador
- Diferencia de niveles

**Recursos:**
- Cartas jugadas
- Elixir gastado
- Eficiencia (daño por elixir)

**Combate:**
- Daño causado
- Tropas invocadas
- Ataques realizados

**Ratios calculados:**
- Ratio de cartas (J1/J2)
- Ratio de daño (J1/J2)
- Eficiencia comparativa

### Ejemplo de salida

```
======================================================================
MEJOR MODELO: Gradient Boosting
Accuracy: 1.0000 (100%!)
======================================================================

Top 10 Features más importantes:
  1. danio_j1: 0.2543
  2. danio_j2: 0.2189
  3. estrategia_j1: 0.1234
  4. elixir_j1: 0.0987
  ...
```

### Archivos generados

- `matriz_confusion.png` - Matriz de confusión del mejor modelo
- `importancia_features.png` - Gráfico de importancia de features
- `comparacion_modelos.png` - Comparación de todos los modelos

---

## 📁 Archivos Nuevos

```
├── src/main/java/analisis/          # Sistema Java de análisis
│   ├── RegistroPartida.java
│   ├── EstadisticasPartidaJugador.java
│   ├── EventoPartida.java
│   ├── RecolectorDatos.java
│   ├── ExportadorCSV.java
│   ├── GestorAnalisis.java
│   └── EjemploAnalisis.java
│
├── analizar_datos.py                # Script Python (análisis completo)
├── analisis_rapido.py               # Script Python (análisis rápido)
├── analisis_estadistico.py          # Script Python (tests estadísticos)
├── ml_predictor.py                  # Script Python (Machine Learning) ⭐ NUEVO
│
├── requirements.txt                 # Dependencias Python
├── ejecutar_analisis.bat            # Menú Windows
│
├── README_ANALISIS.md               # Documentación detallada
├── GUIA_ANALISIS.md                 # Guía de uso
├── RESUMEN_SISTEMA.md               # Resumen ejecutivo
└── ESTRUCTURA_VISUAL.txt            # Diagrama del sistema
```

---

## 🔥 Si NO Quieres Usar el Sistema de Análisis

**No hay problema!** El juego funciona exactamente igual que antes.

Los archivos de análisis son **opcionales** y no afectan el funcionamiento del juego:

```bash
# Solo compila y ejecuta como siempre
mvn clean compile
java -cp target/classes Main
```

El sistema de análisis es completamente **independiente** y solo se ejecuta si tú lo llamas explícitamente.

---

## 📊 Casos de Uso del ML

### 1. Para el Proyecto Académico
- Validar que el modelo de simulación es realista
- Identificar qué factores determinan la victoria
- Incluir métricas de ML en el informe
- Demostrar análisis cuantitativo avanzado

### 2. Para Mejorar el Juego
- Identificar estrategias dominantes
- Detectar necesidades de balanceo
- Predecir resultados de cambios en parámetros
- Optimizar el diseño de estrategias IA

### 3. Para Investigación
- Analizar qué features son más predictivas
- Comparar diferentes algoritmos de ML
- Estudiar patrones en los datos
- Generar insights sobre el comportamiento del juego

---

## 🎓 Para el Informe Final

### Sección de Machine Learning

Puedes incluir:

**1. Metodología**
- Descripción de las features usadas
- Algoritmos probados
- Métricas de evaluación

**2. Resultados**
- Accuracy de cada modelo
- Matriz de confusión
- Importancia de features
- Cross-validation scores

**3. Análisis**
- ¿Qué features son más importantes?
- ¿Qué modelo funciona mejor?
- ¿Qué nos dice sobre el balance del juego?

**4. Conclusiones**
- ¿El juego es predecible?
- ¿Hay estrategias dominantes?
- ¿El modelo confirma la lógica del juego?

---

## ❓ Preguntas Frecuentes

### ¿Necesito instalar Python?

**No**, solo si quieres usar los análisis con gráficos o ML. El juego funciona sin Python.

### ¿Qué versión de Python necesito?

Python 3.8 o superior. Verifica con:
```bash
python --version
```

### ¿Cuántos datos necesito para ML?

Mínimo 50 partidas, ideal 100+. Mientras más datos, mejor el modelo.

```bash
# Puedes cambiar el número de partidas en EjemploAnalisis.java
int numeroPartidas = 100; // Cambia este valor
```

### ¿El ML requiere mucho tiempo?

No, entrenar con 100 partidas toma menos de 10 segundos.

### ¿Puedo usar otros algoritmos de ML?

Sí! El código usa scikit-learn, que tiene muchos más algoritmos:
- Support Vector Machines (SVM)
- K-Nearest Neighbors (KNN)
- Neural Networks
- Ensemble methods
- etc.

### ¿Dónde se guardan los datos?

En la carpeta `datos_analisis/` (se crea automáticamente)

### ¿Puedo usar el juego sin tocar nada de análisis?

**Sí**, todo funciona como antes. El análisis es opcional.

### ¿El análisis afecta el rendimiento del juego?

**No**, el análisis solo se ejecuta cuando tú lo llamas manualmente.

---

## 🐛 Solución de Problemas

### Error "mvn no se reconoce como comando"

**Maven no está instalado.** Consulta **`INSTALAR_MAVEN.md`** para una guía completa de instalación.

**Solución rápida:**
1. Descarga Maven: https://maven.apache.org/download.cgi
2. Extrae a `C:\Program Files\Apache\maven`
3. Agrega al PATH: `%MAVEN_HOME%\bin`
4. Reinicia la terminal

**Alternativa:** Usa IntelliJ IDEA que incluye Maven integrado.

### Error "UnicodeDecodeError: 'utf-8' codec can't decode..."

**Problema de codificación.** Lee **`SOLUCION_UNICODE_ERROR.md`** para la solución completa.

**Solución rápida:**
1. `git pull` para obtener la última versión
2. Elimina los CSVs viejos: `datos_analisis/*.csv`
3. Regenera los datos: `java -cp target/classes analisis.EjemploAnalisis`
4. Ejecuta de nuevo el análisis

### Error al compilar

```bash
# Limpia y recompila
mvn clean
mvn compile
```

### Error "ModuleNotFoundError" en Python

```bash
# Instala las dependencias
pip install -r requirements.txt
```

### Error "No module named 'sklearn'"

```bash
# Instala scikit-learn específicamente
pip install scikit-learn
```

### El .bat no funciona en PowerShell

```powershell
# En PowerShell, usa:
.\ejecutar_analisis.bat

# O cambia a CMD:
cmd
```

### No se generan los CSVs

Verifica que ejecutaste:
```bash
java -cp target/classes analisis.EjemploAnalisis
```

### El ML da error de clases

El script ahora detecta automáticamente las clases presentes (con o sin empates).

---

## 🚀 Flujo de Trabajo Completo

### Para análisis básico:
```bash
mvn clean compile
java -cp target/classes analisis.EjemploAnalisis
python analizar_datos.py
```

### Para análisis avanzado con ML:
```bash
# 1. Compilar
mvn clean compile

# 2. Generar datos (mientras más, mejor para ML)
java -cp target/classes analisis.EjemploAnalisis

# 3. Análisis estadístico
python analisis_estadistico.py

# 4. Machine Learning
python ml_predictor.py

# 5. Revisar resultados en datos_analisis/
```

---

## 📚 Documentación Completa

Para más detalles, consulta:

- **README_ANALISIS.md** - Documentación técnica
- **GUIA_ANALISIS.md** - Tutorial completo
- **RESUMEN_SISTEMA.md** - Resumen y casos de uso

---

## ✅ Checklist Rápido

### Setup Inicial
- [ ] `git pull origin feature/sistema-analisis-datos`
- [ ] `mvn clean compile`
- [ ] (Opcional) `pip install -r requirements.txt`
- [ ] El juego funciona: `java -cp target/classes Main`
- [ ] ✨ Todo listo!

### Para usar ML
- [ ] Generar datos: `java -cp target/classes analisis.EjemploAnalisis`
- [ ] Instalar sklearn: `pip install scikit-learn`
- [ ] Entrenar modelo: `python ml_predictor.py`
- [ ] Revisar gráficos en `datos_analisis/`

---

## 🎯 Resumen

**Para usar el juego (sin cambios):**
```bash
mvn clean compile
java -cp target/classes Main
```

**Para usar análisis de datos:**
```bash
pip install pandas matplotlib seaborn
ejecutar_analisis.bat
```

**Para usar Machine Learning:**
```bash
pip install -r requirements.txt
java -cp target/classes analisis.EjemploAnalisis
python ml_predictor.py
```

**¡Eso es todo!** 🚀

---

## 🎓 Valor Académico

Este sistema completo te permite:

✅ **Recolección de datos** - Metodología científica  
✅ **Análisis estadístico** - Tests de hipótesis formales  
✅ **Visualización** - Comunicación efectiva de resultados  
✅ **Machine Learning** - Técnicas avanzadas de análisis  
✅ **Validación** - Verificar que el modelo funciona correctamente  
✅ **Optimización** - Identificar mejoras basadas en datos  

Todo esto es **ideal para un proyecto universitario** de Modelos y Simulación.

---

_Si tienes problemas, revisa la documentación completa o pregunta en el grupo._

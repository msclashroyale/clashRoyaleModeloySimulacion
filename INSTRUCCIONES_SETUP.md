# 📋 Instrucciones de Setup - Sistema de Análisis de Datos

## 🆕 Cambios en esta actualización

Se ha agregado un **sistema completo de análisis de datos** para el simulador. Incluye:

- ✅ Recolección automática de datos de partidas
- ✅ Exportación a CSV
- ✅ Análisis estadístico avanzado
- ✅ Visualizaciones con gráficos
- ✅ Reportes automáticos

---

## 🔧 Setup Inicial (Solo la primera vez)

### 1. Actualizar tu repositorio local

```bash
# Asegúrate de estar en la rama dev
git checkout dev

# Descarga los cambios
git pull origin dev
```

### 2. Instalar dependencias Python (OPCIONAL)

**⚠️ Solo si quieres usar el análisis con gráficos:**

```bash
pip install -r requirements.txt
```

**Dependencias que instala:**
- pandas (análisis de datos)
- matplotlib (gráficos)
- seaborn (visualizaciones)
- numpy (cálculos)
- scipy (estadística)

### 3. Compilar el proyecto

```bash
mvn clean compile
```

---

## 🎮 Uso del Sistema

### Opción A: Menú Interactivo (Recomendado)

**Windows:**
```cmd
ejecutar_analisis.bat
```

Luego selecciona:
- `[1]` Compilar proyecto
- `[2]` Generar datos (100 partidas)
- `[3]` Análisis completo con gráficos
- `[4]` Análisis rápido en consola
- `[5]` Análisis estadístico avanzado

### Opción B: Comandos Manuales

```bash
# Generar datos
java -cp target/classes analisis.EjemploAnalisis

# Analizar con Python (opcional)
python analizar_datos.py
```

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

## ❓ Preguntas Frecuentes

### ¿Necesito instalar Python?

**No**, solo si quieres usar los análisis con gráficos. El juego funciona sin Python.

### ¿Qué versión de Python necesito?

Python 3.8 o superior. Verifica con:
```bash
python --version
```

### ¿Dónde se guardan los datos?

En la carpeta `datos_analisis/` (se crea automáticamente)

### ¿Puedo usar el juego sin tocar nada de análisis?

**Sí**, todo funciona como antes. El análisis es opcional.

### ¿El análisis afecta el rendimiento del juego?

**No**, el análisis solo se ejecuta cuando tú lo llamas manualmente.

---

## 🐛 Solución de Problemas

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

---

## 📚 Documentación Completa

Para más detalles, consulta:

- **README_ANALISIS.md** - Documentación técnica
- **GUIA_ANALISIS.md** - Tutorial completo
- **RESUMEN_SISTEMA.md** - Resumen y casos de uso

---

## ✅ Checklist Rápido

- [ ] `git pull origin dev`
- [ ] `mvn clean compile`
- [ ] (Opcional) `pip install -r requirements.txt`
- [ ] El juego funciona: `java -cp target/classes Main`
- [ ] ✨ Todo listo!

---

## 🎯 Resumen

**Para usar el juego (sin cambios):**
```bash
mvn clean compile
java -cp target/classes Main
```

**Para usar el análisis de datos:**
```bash
pip install -r requirements.txt
ejecutar_analisis.bat
```

**¡Eso es todo!** 🚀

---

_Si tienes problemas, revisa la documentación completa o pregunta en el grupo._

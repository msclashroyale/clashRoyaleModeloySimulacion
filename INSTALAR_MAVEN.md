# 🔧 Guía de Instalación de Maven

## ❌ Error: "mvn no se reconoce como comando"

Este error significa que **Maven no está instalado** o no está configurado en el PATH de Windows.

---

## ✅ Solución Rápida

### Opción 1: Instalar Maven con Chocolatey (MÁS FÁCIL)

Si tienes Chocolatey instalado:

```powershell
# Ejecutar PowerShell como Administrador
choco install maven
```

Luego cierra y abre una nueva terminal.

---

### Opción 2: Instalación Manual de Maven

#### Paso 1: Descargar Maven

1. Ve a: https://maven.apache.org/download.cgi
2. Descarga el archivo **apache-maven-X.X.X-bin.zip** (la versión más reciente)

#### Paso 2: Extraer Maven

1. Extrae el ZIP a una carpeta, por ejemplo:
   ```
   C:\Program Files\Apache\maven
   ```

#### Paso 3: Configurar Variables de Entorno

**Windows 10/11:**

1. Presiona `Win + X` → Selecciona "Sistema"
2. Click en "Configuración avanzada del sistema"
3. Click en "Variables de entorno"
4. En "Variables del sistema", click en "Nuevo":
   - **Nombre:** `MAVEN_HOME`
   - **Valor:** `C:\Program Files\Apache\maven` (tu ruta de Maven)
5. Busca la variable `Path` en "Variables del sistema"
6. Click en "Editar" → "Nuevo"
7. Agrega: `%MAVEN_HOME%\bin`
8. Click "Aceptar" en todas las ventanas

#### Paso 4: Verificar Instalación

**Cierra y abre una nueva terminal** (PowerShell o CMD), luego ejecuta:

```bash
mvn -version
```

Deberías ver algo como:
```
Apache Maven 3.9.5
Maven home: C:\Program Files\Apache\maven
Java version: 21.0.1, vendor: Oracle Corporation
```

---

### Opción 3: Usar IntelliJ IDEA sin Maven en terminal

Si no quieres instalar Maven manualmente, puedes usar IntelliJ IDEA:

1. Abre el proyecto en IntelliJ IDEA
2. IntelliJ incluye Maven integrado
3. Click derecho en `pom.xml` → "Maven" → "Reload project"
4. Para compilar: "Build" → "Build Project" (Ctrl+F9)
5. Para ejecutar: Click derecho en `Main.java` → "Run"

---

## 🔍 Verificar si Maven está instalado

Ejecuta en una terminal nueva:

```bash
mvn -version
```

### Si funciona:
```
✓ Maven está instalado correctamente
```

### Si NO funciona:
```
❌ Sigue los pasos de instalación arriba
```

---

## ☕ Verificar Java también

Maven requiere Java JDK. Verifica que esté instalado:

```bash
java -version
javac -version
```

Deberías ver algo como:
```
java version "21.0.1"
javac 21.0.1
```

### Si Java no está instalado:

1. Descarga Java JDK 21: https://www.oracle.com/java/technologies/downloads/
2. Instala el JDK
3. Configura `JAVA_HOME` en variables de entorno:
   - **Nombre:** `JAVA_HOME`
   - **Valor:** `C:\Program Files\Java\jdk-21` (tu ruta de Java)
4. Agrega al `Path`: `%JAVA_HOME%\bin`

---

## 🚀 Comandos Básicos de Maven

Una vez instalado Maven:

```bash
# Limpiar el proyecto
mvn clean

# Compilar
mvn compile

# Compilar y empaquetar
mvn package

# Ejecutar tests
mvn test

# Limpiar y compilar
mvn clean compile
```

---

## 🐛 Problemas Comunes

### 1. "JAVA_HOME is not set"

```bash
# Configura JAVA_HOME manualmente (temporal)
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%
```

O configúralo permanentemente en Variables de Entorno (ver arriba).

### 2. "mvn: command not found" después de instalar

**Solución:** Cierra TODAS las terminales y ábrelas de nuevo. Windows necesita reiniciar para cargar las nuevas variables.

### 3. Maven se instaló pero no funciona

Verifica que la ruta esté correcta:

```bash
# En PowerShell
echo $env:PATH

# En CMD
echo %PATH%
```

Deberías ver la ruta de Maven en el PATH.

---

## 🎯 Alternativa: Compilar sin Maven

Si realmente no quieres instalar Maven, puedes compilar manualmente con `javac`:

```bash
# Crear carpeta para clases compiladas
mkdir -p target/classes

# Compilar todos los archivos .java
javac -d target/classes -sourcepath src/main/java src/main/java/**/*.java

# Ejecutar
java -cp target/classes Main
```

**Nota:** Esta opción es más complicada porque debes manejar las dependencias manualmente.

---

## 📋 Checklist de Instalación

- [ ] Maven descargado y extraído
- [ ] `MAVEN_HOME` configurado en variables de entorno
- [ ] `%MAVEN_HOME%\bin` agregado al `Path`
- [ ] Terminal cerrada y reabierta
- [ ] `mvn -version` funciona correctamente
- [ ] Java JDK instalado (`java -version` funciona)
- [ ] `JAVA_HOME` configurado

---

## ✅ Una Vez Instalado Maven

Ya puedes usar el proyecto:

```bash
# 1. Compilar
mvn clean compile

# 2. Ejecutar el juego
java -cp target/classes Main

# 3. Ejecutar análisis
java -cp target/classes analisis.EjemploAnalisis
```

---

## 🆘 Si Nada Funciona

**Opción 1: Usar IntelliJ IDEA**
- Descarga IntelliJ IDEA Community (gratis)
- Abre el proyecto
- IntelliJ maneja Maven automáticamente

**Opción 2: Usar Eclipse**
- Descarga Eclipse IDE for Java
- Importa el proyecto como "Existing Maven Project"
- Eclipse maneja Maven automáticamente

**Opción 3: Pedir el .jar compilado**
- Alguien que ya lo compiló puede compartir el archivo `.jar`
- Ejecutar con: `java -jar ClashRoyale.jar`

---

## 📞 Contacto

Si después de seguir esta guía sigues con problemas:
1. Copia el mensaje de error completo
2. Envía `mvn -version` y `java -version`
3. Comparte en el grupo

---

_Esta guía cubre la instalación de Maven en Windows. Para Linux/Mac, el proceso es diferente._

# DOCUMENTO INTEGRADO: ENCARGO, CASO DE ESTUDIO Y PAUTA DE EVALUACIÓN

**Asignatura:** Desarrollo de Aplicaciones Móviles (`DSY1105`)  
**Proyecto:** MPAZ PRO 2.º — Aplicación móvil de apoyo didáctico para Segundo Básico  
**Institución / Cliente:** Escuela Escritora Marcela Paz (San Bernardo) / Duoc UC  
**Coordinador CITT:** Leonel Garay  
**Tiempo Asignado:** 5 semanas (Asignado en semana 6, entrega en semana 11)  
**Ponderación:** 8% de la nota final (Evaluación Parcial 2 — Encargo en parejas)  

---

> ⚠️ **REGLA DE PRIORIDAD INSTITUCIONAL:**  
> En caso de existir cualquier discrepancia técnica o de alcance entre el formato de caso y la Pauta de Evaluación oficial, **prevalecen de forma absoluta las Instrucciones Específicas y la Pauta de Evaluación (Texto 1)**. Por ejemplo, aunque el caso mencione no requerir hardware específico, para cumplir con el indicador **IE 2.4.1** es **obligatorio integrar de forma funcional y segura al menos dos recursos nativos del dispositivo** (ej. reproductor de audio/Text-To-Speech, motor de vibración háptico, cámara para perfil/evidencia o gestión de almacenamiento).

---

## 1. SITUACIÓN EVALUATIVA Y DESCRIPCIÓN GENERAL

La **Evaluación Parcial 2** consiste en el desarrollo de una interfaz funcional de una aplicación móvil que represente la solución al caso **"MPAZ PRO 2.º"**, basado en las necesidades operativas de la Escuela Escritora Marcela Paz.

El proyecto forma parte de un proceso continuo que servirá de base para la **Evaluación Final Transversal (EFT)**. Constituye un avance intermedio para evidenciar el progreso e integrar retroalimentación. Debe integrar:
* Diseño visual adaptado y usabilidad.
* Formularios validados con retroalimentación en tiempo real.
* Navegación funcional entre vistas.
* Lógica de control y gestión de estado desacoplada (MVVM).
* Almacenamiento y persistencia local de datos.
* Acceso seguro a recursos nativos del dispositivo.
* Animaciones funcionales.
* Control de versiones (GitHub) y planificación colaborativa (Trello).

---

## 2. CASO DE ESTUDIO: "MPAZ PRO 2.º"

### 2.1 Contexto de la Organización
La **Escuela Escritora Marcela Paz** es un colegio público, gratuito e inclusivo ubicado en San Bernardo, que atiende a 270 estudiantes de Pre-Kínder a 8.º Básico. En **Segundo Básico**, los estudiantes fortalecen la lectura, comprensión, escritura, matemática, exploración natural y social. Al ser lectores iniciales, transitan hacia una mayor autonomía, pero requieren instrucciones breves, apoyos visuales, audios explicativos y ejemplos guiados.

### 2.2 Situación Actual y Problemática
Actualmente, los contenidos y ejercicios de las cuatro asignaturas principales se trabajan mediante guías impresas creadas por los profesores:
* **Alta carga operativa:** Los docentes deben redactar, imprimir, repartir y corregir manualmente todo el material.
* **Fragmentación de datos:** Los resultados quedan dispersos en papel, impidiendo el registro automático de avances o la generación de un historial de progreso por Objetivo de Aprendizaje (OA).
* **Ritmos desiguales:** Existen niveles de autonomía lectora y ritmos de aprendizaje muy variados dentro del aula.

### 2.3 Mejora Esperada
Desarrollar un MVP funcional de la aplicación **"MPAZ PRO 2.º"** que organice unidades didácticas con soporte multimedia (texto + audio + apoyo gráfico). La herramienta digitalizará y sistematizará la entrega de actividades, la aplicación de evaluaciones y la generación de informes pedagógicos reservados para el equipo docente, reduciendo el uso de papel y facilitando intervenciones pedagógicas oportunas.

---

## 3. ROLES DE USUARIO Y FUNCIONALIDADES

### 3.1 Jefatura UTP (Administrador Pedagógico)
* **Gestión Curricular:** Administrar las 4 asignaturas del nivel (*Lenguaje y Comunicación*, *Matemática*, *Ciencias Naturales*, e *Historia, Geografía y Ciencias Sociales*).
* **Gestión de Contenidos:** Crear, editar, aprobar, publicar o deshabilitar unidades, Objetivos de Aprendizaje (OA), explicaciones (escritas, audios e imágenes), actividades, desafíos y pruebas finales.
* **Validación Legítima:** Validar la autoría y licenciamiento de contenidos (propios, de dominio público o Mineduc).
* **Trazabilidad:** Revisar la coherencia entre preguntas y OA, además de consultar el historial de modificaciones y aprobaciones.

### 3.2 Docente de Segundo Básico
* **Selección Pedagógica:** Consultar unidades autorizadas por UTP y seleccionar los OA a trabajar en cada curso.
* **Configuración:** Asignar contenidos/desafíos, configurar rango de fechas de disponibilidad y establecer el número máximo de intentos permitidos para la prueba final.
* **Monitoreo:** Seguimiento en tiempo real del avance, revisión de respuestas en la prueba final y consulta del rendimiento agrupado por OA.
* **Retroalimentación:** Descargar, validar y registrar observaciones pedagógicas sobre el informe del estudiante.

### 3.3 Estudiante de Segundo Básico
* **Acceso Personalizado:** Ingresar a su perfil de prueba y acceder únicamente a las asignaturas y OA asignados.
* **Experiencia Didáctica:** Leer textos breves con tipografía grande, visualizar imágenes de apoyo, escuchar audios de instrucciones/explicaciones y observar ejemplos guiados.
* **Resolución de Actividades:** Resolver desafíos progresivos (asociación, secuencias, completación, selección) con confirmación de avance.
* **Evaluación:** Rendir la prueba final según el límite de intentos configurado y revisar respuestas correctas/incorrectas en el solucionario. *(Nota: El estudiante NO tiene acceso a informes pedagógicos ni métricas consolidadas).*

---

## 4. ESTRUCTURA DE INFORMACIÓN Y DATOS REQUERIDOS

* **Información Curricular:** Nivel (2° Básico), Asignatura, Unidad, OA (código y descripción), Eje curricular, Texto de referencia, Estado (*Borrador*, *En revisión*, *Autorizado*, *Publicado*, *Deshabilitado*), Fechas y UTP responsable.
* **Información del Contenido Didáctico:** Título, Explicación escrita, Archivo de audio, Imágenes de apoyo, Ejemplos guiados, Instrucciones, Orden de secuencia, Responsable/Origen del material.
* **Información de Actividades y Desafíos:** Título, Tipo de desafío (asociación, secuencias, completación, selección), Preguntas/Ejercicios, Alternativas, Respuesta correcta, Explicación, Multimedia adjunta, Dificultad, Orden y Estado.
* **Información de la Prueba Final:** ID de prueba, OA evaluado, Preguntas/Alternativas, Solucionario, Criterios de puntuación, Límite de intentos, Fechas de habilitación/rendición y Estado.
* **Información del Estudiante y Progreso:** ID y Curso ficticios, Docente asignado, OA habilitados, Historial de contenidos/actividades completadas, Intentos usados, Respuestas seleccionadas, Marcas de tiempo (`YYYY-MM-DD HH:MM`) y Porcentaje de avance.
* **Información del Informe Pedagógico:** Datos del alumno/curso ficticio, Asignatura/Unidad/OA, Puntaje global y por OA, Desglose de respuestas, Diagnóstico de fortalezas/áreas de refuerzo, Fecha de emisión/validación y Observación docente.

---

## 5. REQUISITOS TÉCNICOS Y ARQUITECTURA

* **Plataforma Objetivo:** Android Studio (SO Android en smartphones o tabletas).
* **Lenguaje & UI:** Kotlin con Jetpack Compose o XML Views (Material Design 3).
* **Arquitectura:** Patrón **MVVM** (Model-View-ViewModel) obligatoria para asegurar la separación de responsabilidades y el desacoplamiento entre la lógica de negocio y la interfaz.
* **Persistencia Local:** **Room**, SQLite o DataStore para el almacenamiento y consulta local del progreso, respuestas, perfiles ficticios y contenidos.
* **Diseño UI/UX:** Interfaz adaptada a lectores iniciales (alta legibilidad, botones e íconos grandes, frases cortas, baja carga cognitiva por pantalla).
* **Inclusividad Multimedia:** Reproducción de audios explicativos/instruccionales de apoyo (sin requerir uso de micrófono).
* **Recursos Nativos (Mínimo 2):** Integración funcional de al menos 2 recursos del dispositivo (ej. `MediaPlayer`/`TextToSpeech`, motor de vibración/háptico para aciertos, gestión de almacenamiento nativo, o cámara para foto de perfil/evidencia).
* **Formularios Validados:** Validaciones visuales por campo en tiempo real, con mensajes de error claros e íconos explicativos.
* **Animaciones Funcionales:** Implementación de animaciones para transiciones entre pantallas y retroalimentación visual inmediata durante la interacción del usuario.

---

## 6. REGLAS ESPECÍFICAS DE ENTREGA Y FORMATO

### 6.1 Repositorio GitHub
* Debe ser un **repositorio público** con acceso otorgado al docente y a ambos miembros del equipo.
* Debe contener la estructura completa del proyecto (`app/`, `build.gradle`, etc.).
* Archivo **`README.md`** obligatorio con:
  * Descripción clara del proyecto.
  * Nombres completos de los integrantes.
  * Funcionalidades e indicadores implementados.
  * Instrucciones paso a paso para compilar y ejecutar la app.
* Commits **frecuentes, equitativos y bien descritos** utilizando lenguaje técnico (se penalizarán commits con textos genéricos o vacíos).
* 🚨 **Penalización Automática por Modificaciones Post-Entrega:** Si se detectan cambios o commits en el repositorio (por mínimos que sean) tras la fecha/hora límite de entrega y antes de la evaluación, **se asignará automáticamente la nota 1.0 (un uno)** tanto en la nota grupal como individual.

### 6.2 Planificación Colaborativa
* Tablero disponible en **Trello** (o herramienta equivalente) con la distribución de tareas, responsables e historial de trabajo visible.

### 6.3 Mecanismo de Entrega en Plataforma AVA
1. **Entrega Grupal:** Un solo integrante sube a AVA el archivo comprimido del código fuente (idéntico al del repositorio). *La falta de esta entrega en AVA dentro del plazo implica nota 1.0 automática a nivel grupal e individual.*
2. **Activación Individual:** Cada integrante debe ingresar al enlace de entrega individual en AVA e ingresar: **Nombre + Apellido + Número de equipo asignado + Nombre de la aplicación/contexto**. *De no activarlo dentro del plazo, el estudiante pierde el derecho a la defensa y obtiene nota 1.0 individual automática.*

---

## 7. INDICADORES DE LOGRO (IL) Y PAUTA DE EVALUACIÓN (RÚBRICA)

### 7.1 Indicadores de Logro Asociados
* **IL 2.1:** Diseña interfaces móviles estructuradas, aplicando principios de usabilidad, jerarquía visual y adaptabilidad, integrando formularios validados, retroalimentación visual y navegación coherente entre componentes.
* **IL 2.2:** Implementa funcionalidades visuales mediante estructuras de programación, lógica de control y mecanismos de gestión de estado, asegurando coherencia entre la interacción y la respuesta visual.
* **IL 2.3:** Integra almacenamiento local y organiza la aplicación móvil usando patrones arquitectónicos y herramientas colaborativas para el desarrollo continuo.
* **IL 2.4:** Implementa funciones de acceso a recursos nativos del dispositivo móvil, garantizando su correcto funcionamiento e integración segura.

---

### 7.2 Escala de Desempeño
* **Muy Buen Desempeño (100%):** Demuestra un desempeño destacado, evidenciando el logro de todos los aspectos evaluados en el indicador.
* **Buen Desempeño (80%):** Demuestra un alto desempeño del indicador, presentando pequeñas omisiones, dificultades y/o errores.
* **Desempeño Aceptable (60%):** Demuestra un desempeño competente, evidenciando los elementos básicos, pero con omisiones o errores visibles.
* **Desempeño Incipiente (30%):** Presenta importantes omisiones, dificultades o errores que no permiten evidenciar los elementos básicos.
* **Desempeño No Logrado (0%):** Ausencia o incorrecto desempeño.

---

### 7.3 Tabla Desglosada de la Rúbrica

| Indicador de Evaluación | Criterio para Muy Buen Desempeño (100%) | Criterio para Desempeño Aceptable (60%) | Criterio para Desempeño Incipiente (30%) | Desempeño No Logrado (0%) | Pond. |
| :--- | :--- | :--- | :--- | :--- | :---: |
| **IE 2.1.1** *(Interfaz y Navegación)* | Diseña una interfaz estructurada y jerárquica, con elementos bien distribuidos, navegación fluida y coherente entre vistas, y aplicación evidente de principios de usabilidad. | Diseña una interfaz funcional, aunque presenta inconsistencias visuales menores o navegación con leves errores. | N/A | La interfaz no fue entregada o presenta una estructura básica con errores de estructura o desorden visual. | **15%** |
| **IE 2.1.2** *(Formularios y Validaciones)* | Integra todos los formularios necesarios para cumplir con los requerimientos, e incluye validaciones visuales completas por campo, retroalimentación clara y uso correcto de íconos. | Integra todos los formularios necesarios, pero presenta validaciones parciales o poco claras, o retroalimentación visual limitada. | Integra menos del 80% pero más del 60% de los formularios requeridos con validaciones completas, retroalimentación e íconos. | Integra menos del 60% de los formularios necesarios para cumplir con los requerimientos de la aplicación. | **15%** |
| **IE 2.2.1** *(Lógica y Gestión de Estado)* | Gestiona la lógica de validación centralizada en componentes lógicos desacoplados (MVVM), y la interfaz responde correctamente a cambios de estado. | Gestiona la lógica de validación la cual está parcialmente centralizada o presenta acoplamientos, con respuestas no completamente consistentes. | N/A | No se evidencia gestión lógica clara de validaciones ni coherencia en la respuesta visual ante cambios de estado. | **10%** |
| **IE 2.2.2** *(Animaciones Visuales)* | Integra animaciones visuales funcionales que mejoran la UX, aportan fluidez en transiciones y entregan retroalimentación clara durante la interacción. | Integra animaciones funcionales en algunos elementos clave, aunque podrían optimizarse en fluidez o coherencia con el diseño general. | Integra animaciones, pero son mínimas o su uso no aporta significativamente a la experiencia del usuario ni a la fluidez visual. | No se integran animaciones visuales o las existentes son irrelevantes, con fallas de implementación o sin relación. | **10%** |
| **IE 2.3.1** *(Modularidad y Persistencia Local)* | Estructura el proyecto aplicando principios de modularidad clara, con separación adecuada de responsabilidades, integración de persistencia local y organización mantenible. | Estructura el proyecto de manera funcional, pero tiene problemas de organización, escasa modularidad o integración de persistencia con acoplamientos. | Organiza el proyecto de manera básica y funcional, pero con limitaciones estructurales o persistencia poco integrada. | El proyecto no presenta una estructura modular clara ni persistencia local implementada de forma efectiva. | **15%** |
| **IE 2.3.2** *(Trabajo Colaborativo)* | Utiliza herramientas como GitHub y Trello, con commits distribuidos, issues resueltos y tareas colaborativas visibles en la planificación. | Utiliza herramientas colaborativas, pero limitado a uno de los integrantes o con registros poco consistentes de la participación. | Utiliza herramientas de manera básica o puntual, sin seguimiento constante ni planificación, y con participación poco distribuida. | No se entrega evidencia del uso de herramientas de colaboración o el repositorio está vacío, duplicado o mal gestionado. | **20%** |
| **IE 2.4.1** *(Recursos Nativos)* | Accede correctamente a **dos o más recursos del dispositivo**, integrándolos de forma segura, funcional y con coherencia en el flujo de la interfaz. | Accede a menos de dos recursos del dispositivo, además presenta deficiencias en permisos, seguridad o coherencia visual en uno de ellos. | N/A | No se accede a recursos nativos o la funcionalidad entregada no opera correctamente. | **15%** |
| **TOTAL** | | | | | **100%** |

---

## 8. RESTRICCIONES Y CONDICIONES ÉTICAS

* **Alcance Académico:** Corresponde a un Producto Mínimo Viable (MVP) para evaluación universitaria; no es software comercial en producción.
* **Protección de Datos:** Queda estrictamente prohibido utilizar datos reales, RUTs, nombres de alumnos o credenciales institucionales. Todos los registros de la base de datos local deben ser **100% ficticios y anonimizados**.
* **Derechos de Autor:** No se permite escanear o copiar fragmentos de libros protegidos sin licencia. Usar recursos de dominio público, propios o con licencia educativa del Mineduc.
* **Rol Pedagógico:** La aplicación actúa como un asistente didáctico; bajo ninguna circunstancia reemplaza la evaluación ni la labor pedagógica del profesor.
* **Independencia del Sistema:** La app funciona con persistencia local propia de pruebas y no requiere conexión con sistemas externos (Libro de Clases, APIs institucionales ni motores de IA generativa).
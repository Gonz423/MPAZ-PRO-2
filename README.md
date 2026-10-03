# MPAZ PRO 2

Aplicacion movil de apoyo didactico para Segundo Basico basada en las necesidades operativas de la Escuela Escritora Marcela Paz (San Bernardo) y Duoc UC.

## Informacion del Proyecto

- Asignatura: Desarrollo de Aplicaciones Moviles (DSY1105)
- Evaluacion: Evaluacion Parcial 2
- Plataforma: Android (Kotlin)
- Arquitectura: MVVM (Model - View - ViewModel)
- Persistencia Local: Room Database

## Integrantes

- Integrante 1: Por definir
- Integrante 2: Por definir

## Descripcion General

MPAZ PRO 2 es una aplicacion movil orientada a estudiantes de Segundo Basico en etapa de lectura inicial. El sistema digitaliza la distribucion de material didactico, ejercicios interactivos, pruebas finales y generacion de reportes pedagogicos para docentes, reduciendo la carga operativa y el uso de papel en el aula.

## Roles de Usuario

1. Jefatura UTP (Administrador Pedagogico):
   - Gestion de asignaturas (Lenguaje, Matematica, Ciencias Naturales, Historia).
   - Administracion de unidades, Objetivos de Aprendizaje (OA) y contenidos multimedia.
   - Validacion de licencias y trazabilidad de cambios.

2. Docente de Segundo Basico:
   - Habilitacion de OA por curso.
   - Configuracion de periodos de evaluacion y limite de intentos.
   - Monitoreo en tiempo real del progreso de los alumnos.
   - Consulta de informes pedagogicos y registro de observaciones.

3. Estudiante de Segundo Basico:
   - Interfaz con alta legibilidad, frases breves y apoyos visuales.
   - Resolucion de desafios didacticos con soporte de audio.
   - Rendicion de prueba final con acceso a solucionario.

## Funcionalidades e Indicadores Implementados

- Diseno visual adaptado para lectores iniciales con navegacion estructurada (IE 2.1.1).
- Formularios con validacion en tiempo real, mensajes de error e iconos descriptivos (IE 2.1.2).
- Gestion logica desacoplada bajo patron MVVM y gestion reactiva de estado (IE 2.2.1).
- Animaciones funcionales y transiciones fluidas de interfaz (IE 2.2.2).
- Persistencia local con base de datos Room para perfiles, avance e historial (IE 2.3.1).
- Planificacion colaborativa y control de versiones distribuido (IE 2.3.2).
- Integracion de recursos nativos del dispositivo (IE 2.4.1).

## Requisitos de Ejecucion

- Android Studio Ladybug o superior.
- JDK 17 o superior.
- Dispositivo fisico o emulador con Android 8.0 (API 26) o superior.

## Instrucciones de Compilacion y Ejecucion

1. Clonar el repositorio:
   git clone https://github.com/Gonz423/MPAZ-PRO-2.git

2. Abrir el proyecto en Android Studio.

3. Sincronizar las dependencias con Gradle (Sync Project with Gradle Files).

4. Seleccionar un dispositivo o emulador disponible.

5. Ejecutar la aplicacion presionando Run (Shift + F10).

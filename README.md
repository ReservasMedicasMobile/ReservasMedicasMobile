
# ReservasMedicasMobile 
![ic_logo-playstore](https://github.com/user-attachments/assets/39eb885f-6366-4a77-86f5-f375b3e65823)  

## Descripción del Proyecto
 
La Aplicación de Reservas Médicas Mobile es una extensión del sistema de reserva médica previamente desarrollado en plataforma web. Esta aplicación móvil está diseñada para facilitar a los usuarios/pacientes la gestión de sus turnos médicos de manera más accesible a través de dispositivos móviles. 
El objetivo es ofrecer una experiencia fluida y conveniente para los pacientes al momento de reservar citas, consultar su historial, todo desde sus smartphones.
El backend de esta aplicación seguirá siendo el mismo, desarrollado en Django y utilizando Python, mientras que el frontend será implementado en Android Studio (versión Koala, API Lollipop 5.1) con Java para mejorar la experiencia de los usuarios móviles.

## Tabla de Contenidos
- [Objetivo](#objetivo)
- [Requisitos del Sistema](#requisitos-del-sistema)
- [Instalación](#instalación)
- [Uso](#uso)
- [Características Principales](#características-principales)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Contribuir](#contribuir)
- [Licencia](#licencia)
- [Funcionalidades Futuras](#funcionalidades-futuras)
- [Contacto](#contacto)
- [Repositorios](#repositorios)
- [Documentacion](#documentación)
- [Integrantes](#integrantes)


## Objetivo
El objetivo principal de este proyecto es permitir a los usuarios realizar las siguientes acciones de manera sencilla y rápida desde sus dispositivos móviles:
* Reservar turnos médicos con diferentes especialidades y profesionales.
* Programar estudios médicos específicos seleccionando fechas y horarios disponibles.
* Consultar el historial de turnos y estudios realizados por el paciente.
* Acceso para el administrador, con todas las credenciales para gestionar el sistema.
* Acceso para el usuario común, enfocado en la gestión de sus turnos.

## Tecnologías Utilizadas
* **Lenguaje de programación:** Java
* **IDE de desarrollo:** Android Studio Version Koala
* **API (Minimun SDK) 22:** Lollipop 5.1
* **Backend:** Django (con APIs REST para la sincronización de datos), Python y Mysql.
* **Peticiones http:** Volley
* **Autenticación:** JWT para la autenticación de usuarios

## Requisitos del Sistema
* **Android Studio:** Versión  Koala (API 22 Lollipop 5.1)
* **Backend:** Servidor con Django y Python
* **Conexión a Internet:** Para interactuar con la API del backend

## Instalación
1. Abre una terminal (en una carpeta de tu computadora) y Clona el repositorio del proyecto:
   ```bash
   git clone https://github.com/ReservasMedicasMobile/ReservasMedicasMobile.git
2. Abrir el proyecto en Android Studio.
   - Inicia Android Studio.
   - Selecciona "Abrir un Proyecto Existente" y navega hasta la carpeta donde clonaste el repositorio.

3. **Configurar el Entorno**
   - Asegúrate de tener la versión mínima del SDK de Android (API 22: Lollipop 5.1) instalada.
   - Sincroniza las dependencias del archivo `build.gradle`: Abre el menú de Android Studio -> File -> Sync Project with Gradle Files.

4. Ejecutar el proyecto en un emulador o dispositivo físico con la versión mínima de Android Lollipop 5.1 (como se va a explicar en el siguiente apartado "Uso")
   
## Uso
1. **Ejecutar la Aplicación**
   - Conecta un dispositivo Android a tu computadora o inicia un emulador.
   - Haz clic en el botón "Ejecutar" (el ícono del triángulo verde) en Android Studio.
   - Selecciona el dispositivo o emulador donde deseas ejecutar la aplicación.

2. **Iniciar Sesión o Registrarse**
   - Una vez que la aplicación se inicie, podrás registrarte si eres un nuevo usuario o iniciar sesión con tus credenciales existentes.

3. **Explorar las Funcionalidades**
   - Navega por la aplicación para familiarizarte con las diferentes funcionalidades, como la gestión de citas médicas, visualización de servicios y más.

### Notas:
- Asegúrate de que tu dispositivo o emulador tenga acceso a Internet para la funcionalidad completa de la aplicación.
- Revisa la documentación adicional si encuentras problemas durante la instalación o ejecución.
  

## Características Principales
* **Reservas de turnos:** Los usuarios pueden buscar y reservar turnos con los médicos o especialidades de su preferencia.
* **Historial de turnos y estudios:** Los usuarios pueden acceder a un registro detallado de sus citas pasadas y próximas.
* **Notificaciones:** Notificaciones sobre confirmaciones de citas, recordatorios y alertas de pagos pendientes.
* **Perfil del paciente:** Los usuarios pueden gestionar y actualizar su información personal dentro de la aplicación.

## Contribuir 
Las contribuciones son bienvenidas. Si deseas contribuir al proyecto, sigue estos pasos:

1. Haz un fork del repositorio.
2. Crea una nueva rama
   ```bash
   git checkout -b feature/nueva-caracteristica
4. Realiza tus cambios y haz commit
   ```bash
   git commit -m 'Agregada nueva característica'
6. Haz push a la rama
   ```bash
   git push origin feature/nueva-caracteristica
8. Abre un Pull Request.

## Licencia
Este proyecto está licenciado bajo los términos de la [Licencia MIT](https://opensource.org/licenses/MIT).


## Funcionalidades Futuras
* Integración con sistemas de notificaciones push para alertar a los usuarios sobre citas y pagos.
* Mejoras en la experiencia de usuario (UI/UX) en la app.

## Contacto  
Para más información o soporte, puedes contactar al equipo de desarrollo a través de nuestro correo: soporte@reservamedica.com.

## Repositorios
- [Organizacion en github](https://github.com/ReservasMedicasMobile)
- [App Mobile](https://github.com/ReservasMedicasMobile/ReservasMedicasMobile)
- [Api Django](https://github.com/ReservasMedicasMobile/Backend_api_django)
- [Proyecto Reservas Médicas Web](https://github.com/ReservasMedicasMobile/ReservasMedicasWeb)


## Documentación
[Wiki del proyecto - Reservas Medicas Mobile](https://github.com/ReservasMedicasMobile/ReservasMedicasMobile/wiki)
## Integrantes:

| Integrante                  | Rol            | Github  |
|-----------------------------|----------------|------------|
| Arias Jon Jonathan   | Scrum Master - Developer    | [jonjonathanarias](https://github.com/jonjonathanarias)   |
| Antich Mónica                | Developer       | [MonicaAntich](https://github.com/MonicaAntich)   |
| Aramayo Jesica          | Developer       | [Jesica-A](https://github.com/Jesica-A)   |
| Cardozo Axel Cardozo     | Developer       | [ExpertHacker444](https://github.com/ExpertHacker444)    |
| Lucero Luis Alejo             | Developer       | [alejo11lucero](https://github.com/Alejo11Lucero)    |
| Maldonado Florencia     | Developer       | [MaldonF](https://github.com/MaldonF)   |
| Santiago Leandro        | Developer       | [leandros1793](https://github.com/leandros1793)   |
| Segall Daniel Alejandro             | Developer       | [pupaycleo](https://github.com/pupaycleo)   |
| Sorrentino Matias      | Developer       | [matiassorrentino](https://github.com/matiassorrentino)   |





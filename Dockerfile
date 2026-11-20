# Imagen de la aplicacion SGP Ay Chabela.
# Se construye en dos etapas: primero se compilan las clases con JDK 8 y
# despues se copian a una imagen de Tomcat 9 lista para ejecutar.

# ---------- Etapa 1: compilar ----------
FROM eclipse-temurin:8-jdk AS compilacion

WORKDIR /proyecto

# La API de Servlets solo hace falta para compilar; Tomcat ya la trae.
ADD https://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar /libs/servlet-api.jar

COPY src/ src/
COPY web/WEB-INF/lib/ web/WEB-INF/lib/

RUN mkdir -p salida/classes \
 && find src/java -name "*.java" > fuentes.txt \
 && javac -encoding UTF-8 -nowarn \
      -cp "/libs/servlet-api.jar:web/WEB-INF/lib/mysql-connector-j-9.7.0.jar" \
      -d salida/classes @fuentes.txt

# ---------- Etapa 2: imagen final ----------
FROM tomcat:9.0

# Se reemplaza la aplicacion de ejemplo de Tomcat por la nuestra.
RUN rm -rf /usr/local/tomcat/webapps/ROOT

COPY web/ /usr/local/tomcat/webapps/ROOT/
COPY --from=compilacion /proyecto/salida/classes/ /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/

# Los datos de conexion se pasan como variables de entorno al desplegar:
# DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD.
EXPOSE 8080

CMD ["catalina.sh", "run"]

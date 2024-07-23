# Utiliser l'image de base OpenJDK
FROM openjdk:21-jdk-slim

# Copier le JAR généré dans le conteneur
COPY target/kata-tondeuse-0.0.1-SNAPSHOT.jar /app/kata-tondeuse.jar

# Définir le répertoire de travail
WORKDIR /app

# Exposer le port sur lequel l'application sera disponible
EXPOSE 8080

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "kata-tondeuse.jar"]
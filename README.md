# Kata Tondeuse

## Description

Le projet **Kata Tondeuse** est une application Spring Boot qui utilise Spring Batch pour traiter des instructions de tondeuse à gazon à partir d'un fichier d'entrée. Le traitement des instructions est réalisé par un job Spring Batch, et les résultats sont sauvegardés dans un fichier de sortie.

## Prérequis

Avant de commencer, assurez-vous d'avoir les outils suivants installés :

- **Java 21**
- **Git** : pour cloner le dépôt
- **Maven** : pour compiler le projet
- **Docker** : pour construire et exécuter les conteneurs
- **Kubernetes** : pour déployer l'application (optionnel)

## Cloner le Projet depuis le dépôt GitHub

   ```bash
   git clone https://github.com/arnoldkouakep/kata-tondeuse.git
   ```
  
## Compilation du projet

Avec Maven :

   ```bash
   cd kata-tondeuse
   mvn clean package
   ```
   
## Docker

1. Créer l'image Docker :

  ```bash
  docker build -t kata-tondeuse-app .
   ```

2. Exécuter l'Application avec Docker :

  ```bash
   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev kata-tondeuse-app
   ```

2. Supprimer le container Docker :

  ```bash
   docker ps -a
   docker rm <container_id_or_name>
   docker rmi kata-tondeuse-app
   ```
   
# Introduction

## Présentation générale du projet

Ce projet de preuve de concept (POC) explore l'intégration de SpringIA avec un modèle de langage local, Ollama Mistal, en utilisant la technique de RAG (Retrieval-Augmented Generation). L'objectif est de démontrer comment enrichir les réponses d'un modèle de langage en utilisant des informations extraites de documents PDF.

## Définition des concepts clés

**SpringIA :** Framework utilisé pour développer des applications intelligentes et évolutives.  
**LLM (Large Language Model) :** Modèles de langage de grande taille capables de générer du texte de manière cohérente.  
**Ollama Mistal :** Un modèle de langage local performant utilisé dans ce POC.  
**RAG (Retrieval-Augmented Generation) :** Technique qui combine la récupération d'informations (retrieval) avec la génération de texte (generation) pour améliorer la pertinence et l'exactitude des réponses.

# Partie I : Mise en place de l'environnement

## Choix des technologies et outils

Pour ce POC, nous avons choisi SpringIA en raison de sa robustesse et de sa flexibilité dans la construction d'applications complexes.  
Ollama Mistal a été sélectionné comme modèle de langage local pour ses capacités de génération de texte avancées.  
Les PDF sont utilisés comme source d'information en raison de leur format structuré et riche en contenu.

## Installation et configuration

### Installation de SpringIA

Créer un projet Spring depuis le site officiel: https://start.spring.io/

- project : Maven
- Spring Boot : 3.2.8
- Packaging : War
- Java: 21

Configurer le projet en utilisant les dépendances nécessaires :

- Spring Web
- Ollama AI
- spring-ai-pdf-document-reader
- thymeleaf

### Configuration du LLM local avec Ollama Mistal

Télécharger le modèle Ollama Mistal : https://ollama.com/library/llama3  
Ouvrer un cmd et télécharger mistral : `ollama run mistral`

# Partie II : Fonctionnement de la RAG

## Architecture de la solution

L'architecture se compose de plusieurs composants clés :  
**SpringIA :** Gestion de l'application et orchestration des composants.  
**Ollama Mistal :** Modèle de langage pour la génération de texte.  
**Moteur de recherche PDF :** Extraction et indexation des informations provenant des PDF.  
**ChatClient :** Interface utilisateur permettant l'interaction en temps réel avec le système.  
**Vector Store :** Système de stockage et de recherche des documents en utilisant des vecteurs.

## Procédé de récupération d'informations

### Extraction des informations des PDF

Utilisation de `PagePdfDocumentReader` et des `chunks` pour extraire le texte et la création des `vectorStore`.

## Recherche dans les PDF

Utilisation d'algorithmes de recherche vectorielle pour trouver les informations pertinentes dans les PDF.

### Principe

Le Vector Store permet de convertir les requêtes et les documents en vecteurs et de mesurer la similarité pour retourner les résultats les plus pertinents avec `vectorStore.similaritySearch`.

### Enrichissement des réponses

Combinaison des informations récupérées avec les capacités de génération de texte du LLM pour fournir des réponses complètes et précises.

## Intégration avec Ollama Mistal

Le processus d'intégration suit les étapes suivantes :

- Requête utilisateur envoyée via le `ChatClient`.
- Recherche d'informations pertinentes dans les PDF en utilisant le Vector Store.
- Envoi des informations extraites au modèle Ollama Mistal.
- Génération de la réponse enrichie et retour au ChatClient pour affichage à l'utilisateur.

# Partie III : Démonstration et résultats

## Cas d'utilisation

Recherche d'informations spécifiques dans les PDF.  
Génération de résumés et d'analyses basées sur les documents.

## Scénarios de test

Pour les tests, un faux CV en format PDF a été créé.

Les tests ont été conduits en utilisant l'interface graphique ainsi que des appels API pour vérifier l'extraction et la génération des réponses.

## Exemples de requêtes et réponses

### Utilisation de l'interface graphique

**Lien de test :** http://localhost:8080/  
Via cette interface, les utilisateurs peuvent poser des questions et obtenir des réponses enrichies directement depuis le chatClient.

### Utilisation des données du PDF avec des appels API

**Lien de test :** [http://localhost:8080/ask?question=donne moi le titre dans le document](http://localhost:8080/ask?question=donne%20moi%20le%20titre%20dans%20le%20document)  
**Requête :** "Donne-moi le titre dans le document"  
**Réponse attendue :** CV of Jean dupon  
**Réponse obtenue :** The title in the provided content is not explicitly stated. However, based on the context, it appears to be a professional profile or resume of an individual named Jean Dupon, who is a Consultant Informatique with 3 years of experience in Java development.

### Utilisation du contexte tiré d'un texte avec des appels API

**Lien de test :** [http://localhost:8080/ask1?question=who is alex](http://localhost:8080/ask1?question=who%20is%20alex)  
**Requête :** "Who is Alex?"  
**Réponse attendue :** Alex is a programmer who works for hhachich Programming.  
**Réponse obtenue :** Alex is a programmer who works for hhachich Programming.

## Analyse des performances

### Précision des réponses

La précision a été mesurée en comparant les réponses générées avec les informations contenues dans le faux CV PDF. Les réponses obtenues étaient pertinentes, démontrant l'efficacité du système RAG.

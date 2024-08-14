package fr.hhachich.SpringIA.RGA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RagController est un contrôleur Spring MVC qui gère les interactions de l'utilisateur via une interface web.
 * Il permet de poser des questions à un service d'IA et d'afficher les réponses.
 */
@Controller
public class RagController {
    private final RagRestController ragRestController;

    /**
     * Constructeur qui injecte une instance de RagRestController.
     *
     * @param ragRestController Instance de RagRestController utilisée pour interagir avec le service d'IA.
     */
    @Autowired
    public RagController(RagRestController ragRestController) {
        this.ragRestController = ragRestController;
    }

    /**
     * Gère les requêtes GET pour la racine de l'application ("/").
     *
     * @return Le nom de la vue "index" pour afficher la page d'accueil.
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Récupère la liste des fichiers JSON présents dans le répertoire "src/main/resources/vectorestore".
     *
     * @return Une liste de noms de fichiers JSON.
     * @throws Exception En cas de problème d'accès aux fichiers.
     */
    private List<String> getJsonFiles() throws Exception {
        // Chemin de départ pour la recherche des fichiers
        Path startPath = Paths.get("src/main/resources/vectorestore");
        // Liste pour stocker les noms des fichiers JSON
        List<String> jsonResources = new ArrayList<>();
        // Parcourt récursivement les fichiers à partir du chemin de départ
        try (Stream<Path> paths = Files.walk(startPath)) {
            jsonResources = paths
                    .filter(Files::isRegularFile) // Filtre pour ne garder que les fichiers réguliers
                    .filter(path -> path.toString().endsWith(".json"))// Filtre pour ne garder que les fichiers JSON
                    .map(path -> path.getFileName().toString()) // Récupère le nom de chaque fichier
                    .collect(Collectors.toList()); // Collecte les noms dans une liste
        }
        // Retourne la liste des fichiers JSON
        return jsonResources;
    }

    /**
     * Gère les requêtes GET pour "/askQuestion".
     * <p>
     * Affiche une vue permettant de poser une question à l'IA.
     *
     * @param model Le modèle utilisé pour ajouter des attributs à la vue.
     * @return Le nom de la vue "chatRag".
     * @throws Exception En cas de problème d'accès aux fichiers JSON.
     */
    @GetMapping("/askQuestion")
    public String ask(Model model) throws Exception {
        // Récupère la liste des fichiers JSON
        List<String> documentNames = getJsonFiles();
        // Ajoute la liste au modèle pour l'afficher dans la vue
        model.addAttribute("documentNames", documentNames);
        // Retourne le nom de la vue
        return "chatRag";
    }

    /**
     * Gère les requêtes POST pour "/askQuestion".
     * <p>
     * Envoie la question posée par l'utilisateur au service d'IA et affiche la réponse dans la vue.
     *
     * @param question La question posée par l'utilisateur.
     * @param model    Le modèle utilisé pour ajouter des attributs à la vue.
     * @return Le nom de la vue "chatRag" avec la réponse de l'IA.
     * @throws Exception En cas de problème d'accès aux fichiers JSON ou lors de l'interaction avec l'IA.
     */
    @PostMapping("/askQuestion")
    public String ask(@RequestParam("question") String question, Model model) throws Exception {
        // Envoie la question à l'IA et récupère la réponse
        String response = ragRestController.ask(question);
        // Récupère à nouveau la liste des fichiers JSON
        List<String> documentNames = getJsonFiles();
        // Ajoute la liste au modèle
        model.addAttribute("documentNames", documentNames);
        // Ajoute la réponse de l'IA au modèle
        model.addAttribute("response", response);
        // Retourne le nom de la vue avec la réponse affichée
        return "chatRag";
    }
}
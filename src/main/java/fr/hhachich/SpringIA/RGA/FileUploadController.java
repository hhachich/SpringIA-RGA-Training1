package fr.hhachich.SpringIA.RGA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * FileUploadController est un contrôleur Spring MVC qui gère le téléversement de fichiers PDF via une interface web.
 * Les fichiers téléchargés sont stockés dans un répertoire spécifique et peuvent être utilisés pour d'autres traitements.
 */
@Controller
public class FileUploadController {
    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    // Injection de la classe DataLoader pour gérer les données après le téléversement
    @Autowired
    private DataLoader dataLoader;
    // Répertoire où les fichiers PDF seront stockés
    private static final String UPLOAD_DIR = "src/main/resources/pdfs/";

    /**
     * Gère les requêtes GET pour "/upload".
     * Affiche une page pour permettre à l'utilisateur de téléverser un fichier PDF.
     *
     * @return Le nom de la vue "uploadPdf".
     */
    @GetMapping("/upload")
    public String index() {
        return "uploadPdf";
    }

    /**
     * Gère les requêtes POST pour "/upload".
     * Traite le fichier PDF téléversé par l'utilisateur, le sauvegarde sur le serveur et déclenche des traitements supplémentaires via DataLoader.
     *
     * @param pdfFile Le fichier PDF téléversé par l'utilisateur.
     * @param model   Le modèle utilisé pour ajouter des attributs à la vue.
     * @return Le nom de la vue "uploadPdf" avec un message de succès ou d'erreur.
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("pdfFile") MultipartFile pdfFile, Model model) {
        // Vérifie si un fichier a été sélectionné
        if (pdfFile.isEmpty()) {
            model.addAttribute("message", "Please select a PDF file to upload.");
            return "uploadPdf";
        }

        try {
            // Crée le répertoire de destination si nécessaire
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                // Crée le répertoire s'il n'existe pas
                directory.mkdirs();
            }

            // Sauvegarde le fichier téléversé sur le serveur
            String fileName = pdfFile.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);

            // Copie le fichier vers le répertoire de destination
            Files.copy(pdfFile.getInputStream(), path);

            // Log et message de succès
            log.info("File uploaded successfully: " + fileName);
            model.addAttribute("message", "File uploaded successfully: " + fileName);

            // Déclenche le chargement des données après le téléversement
            dataLoader.simpleVectorStore();
        } catch (Exception e) {
            log.error("Error saving file", e);
            model.addAttribute("message", "Failed to upload file: " + e.getMessage());
        }

        return "uploadPdf";
    }
}

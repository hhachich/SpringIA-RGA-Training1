package fr.hhachich.SpringIA.RGA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * DataLoader est une classe de composant Spring qui gère le chargement de fichiers PDF,
 * la génération de vecteurs d'embedding à partir de leur contenu,
 * et la sauvegarde ou le chargement de ces vecteurs dans un SimpleVectorStore.
 */
@Component
public class DataLoader {

    private static Logger log = LoggerFactory.getLogger(DataLoader.class);
    // Modèle d'embedding utilisé pour générer les vecteurs à partir du contenu des documents
    @Autowired
    private EmbeddingModel embeddingModel;

    /**
     * Crée un bean SimpleVectorStore qui charge ou génère des vecteurs d'embedding à partir de fichiers PDF.
     *
     * @return SimpleVectorStore chargé avec les vecteurs d'embedding des documents.
     * @throws Exception si une erreur survient lors du traitement des fichiers PDF.
     */
   @Bean
    public SimpleVectorStore simpleVectorStore() throws Exception {

        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingModel);
        try{
            // Récupère la liste des fichiers PDF à traiter
            List<Resource> pdfFiles = getPdfFiles();

            for (Resource pdfFile : pdfFiles) {
                String fileName = pdfFile.getFilename();
                String vectorStoreName = fileName.replace(".pdf", ".json");
                String path= Path.of("src","main","resources","vectorestore").toFile().getAbsolutePath()+"/"+vectorStoreName;
                File fileStore=new File(path);

                try {
                    // Si un fichier de vecteur existe déjà, il est chargé dans le store
                    if (fileStore.exists()) {
                        log.info("Vector store exist=>" + path);
                        simpleVectorStore.load(fileStore);
                    }
                    // Sinon, le contenu du PDF est lu, découpé en chunks, et des vecteurs sont générés
                    else {
                        PagePdfDocumentReader documentReader = new PagePdfDocumentReader(pdfFile);
                        List<Document> documents = documentReader.get();
                        TextSplitter textSplitter = new TokenTextSplitter();
                        List<Document> chunks = textSplitter.split(documents);
                        // Les vecteurs sont ajoutés au store et sauvegardés
                        simpleVectorStore.add(chunks);
                        simpleVectorStore.save(fileStore);
                    }
                }catch (Exception e) {
                    log.error("Error during vector store creation or loading", e);
                    throw new RuntimeException(e);
                }
            }
        }catch(Exception e) {
            log.error("Error during load PDF", e);
            throw new RuntimeException(e);
        }


        return simpleVectorStore;
    }

    /**
     * Récupère la liste des fichiers PDF présents dans le répertoire de ressources spécifié.
     *
     * @return Une liste de ressources représentant les fichiers PDF.
     * @throws Exception si une erreur survient lors de l'accès aux fichiers.
     */
    private List<Resource> getPdfFiles() throws Exception {
        Path startPath = Paths.get("src/main/resources/pdfs");
        List<Resource> pdfResources = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(startPath)) {
            pdfResources = paths
                    .filter(Files::isRegularFile) // Filtre pour ne garder que les fichiers réguliers
                    .filter(path -> path.toString().endsWith(".pdf"))// Filtre pour ne garder que les fichiers PDF
                    .map(FileSystemResource::new) // Convertit chaque chemin en ressource
                    .collect(Collectors.toList());
        }

        return pdfResources;
    }

}

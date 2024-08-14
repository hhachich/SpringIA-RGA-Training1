package fr.hhachich.SpringIA.RGA;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
/**
 * RagRestController est un contrôleur REST Spring Boot qui gère les interactions avec un service de chat IA.
 * Il fournit plusieurs endpoints pour poser des questions à l'IA et recevoir des réponses basées soit sur un contenu prédéfini,
 * soit sur des informations contextuelles récupérées à partir d'un vector store.
 */
@RestController
public class RagRestController {
    // Déclaration des objets nécessaires pour les interactions avec l'IA
    private ChatClient chatClient;
    // Injection de la ressource de modèle de prompt
    @Value("classpath:prompts/prompt.st")
    private Resource promptResource;
    private VectorStore vectorStore;
    /**
     * Constructeur de RagRestController.
     * Initialise le ChatClient et le VectorStore pour générer des réponses IA.
     *
     * @param chatClientBuilder Le builder pour créer une instance de ChatClient.
     * @param vectorStore       Le VectorStore utilisé pour rechercher des documents similaires à la requête.
     */
    public RagRestController(ChatClient.Builder chatClientBuilder,VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore =vectorStore;
    }
    /**
     * Gère les requêtes GET vers le endpoint /ask.
     * Utilise la question fournie pour rechercher des documents similaires dans le vector store, crée un prompt
     * basé sur les résultats de la recherche et la question, et retourne la réponse de l'IA au format Markdown.
     *
     * @param question La question posée par l'utilisateur.
     * @return La réponse de l'IA au format Markdown.
     */
    @GetMapping(value = "/ask",produces = MediaType.TEXT_MARKDOWN_VALUE)
    public String ask(String question){
        // Chargement du modèle de prompt à partir de la ressource
        PromptTemplate promptTemplate=new PromptTemplate(promptResource);
        // Recherche de documents similaires dans le vector store en fonction de la question posée
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.query(question).withTopK(4)
        );
        // Extraction du contenu des documents sous forme de liste de chaînes
        List<String> context = documents.stream().map(d -> d.getContent()).toList();
        // Création du prompt en utilisant le modèle de prompt et le contexte extrait
        Prompt prompt = promptTemplate.create(Map.of("context", context, "question", question));
        // Appel à l'API de chat et retour du contenu généré par l'IA
        return chatClient.prompt(prompt).call().content();
    }
    /**
     * Gère les requêtes GET vers le endpoint /ask1.
     * Utilise un contenu fixe pour générer une réponse basée sur la question posée.
     *
     * @param question La question posée par l'utilisateur.
     * @return La réponse de l'IA au format Markdown.
     */
    @GetMapping(value = "/ask1",produces = MediaType.TEXT_MARKDOWN_VALUE)
    public String ask1(String question){
        // Définition manuelle du message de prompt avec une instruction pour l'IA
        String message= """
                <INST>You are an AI assistant that can answer your questions. Use the content provided. If you don't know the answer, don't make suggestions just say "I don't know".</INST>
                content: {content}
                question: {input}
                                
                """;
        // Création du modèle de prompt avec le message défini
        PromptTemplate promptTemplate=new PromptTemplate(message);
        // Définition d'un contenu fixe utilisé dans le contexte du prompt
        String myContent = """
            Alex is a programmer working for hhachich Programming.
            
            Alex is under paid.
            
            Bob is a programmer working for Acme Programming.
            
            Bob is paid more than Alex.
            
            hhachich Programming is a consulting company that employs programmers.
            
            """;
        // Création du prompt en remplaçant les variables "content" et "input" dans le modèle de prompt
        Prompt prompt = promptTemplate.create(Map.of("content", myContent, "input", question));
        // Appel à l'API de chat et retour du contenu généré par l'IA
        return chatClient.prompt(prompt).call().content();
    }
    /**
     * Gère les requêtes GET vers le endpoint /chat.
     * Retourne une liste de réponses générées par l'IA en fonction de la question posée.
     *
     * @param question La question posée par l'utilisateur.
     * @return Une liste d'objets Generation représentant les réponses de l'IA.
     */
    @GetMapping("/chat")
    public List<Generation> chat(String question){
        // Utilisation de la question directement comme message de prompt
        String message=question;
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt=promptTemplate.create();
        // Appel à l'API de chat pour obtenir les réponses générées par l'IA
        ChatClient.ChatClientRequest.CallPromptResponseSpec responseSpec=chatClient.prompt(prompt).call();
        return responseSpec.chatResponse().getResults();
    }

}

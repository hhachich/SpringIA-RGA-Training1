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

@RestController
public class RagRestController {
    private ChatClient chatClient;
    @Value("classpath:prompts/prompt.st")
    private Resource promptResource;
    private VectorStore vectorStore;
    public RagRestController(ChatClient.Builder chatClientBuilder,VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore =vectorStore;
    }

    @GetMapping(value = "/ask",produces = MediaType.TEXT_MARKDOWN_VALUE)
    public String ask(String question){
        PromptTemplate promptTemplate=new PromptTemplate(promptResource);
        //List<Document> documents = vectorStore.similaritySearch(question);
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.query(question).withTopK(4)
        );

        List<String> context = documents.stream().map(d -> d.getContent()).toList();

        Prompt prompt = promptTemplate.create(Map.of("context", context, "question", question));
        return chatClient.prompt(prompt).call().content();
    }
    @GetMapping(value = "/ask1",produces = MediaType.TEXT_MARKDOWN_VALUE)
    public String ask1(String question){
        String message= """
                <INST>You are an AI assistant that can answer your questions. Use the content provided. If you don't know the answer, don't make suggestions just say "I don't know".</INST>
                content: {content}
                question: {input}
                                
                """;
        PromptTemplate promptTemplate=new PromptTemplate(message);


        String myContent = """
            Alex is a programmer working for hhachich Programming.
            
            Alex is under paid.
            
            Bob is a programmer working for Acme Programming.
            
            Bob is paid more than Alex.
            
            hhachich Programming is a consulting company that employs programmers.
            
            """;

        Prompt prompt = promptTemplate.create(Map.of("content", myContent, "input", question));
        return chatClient.prompt(prompt).call().content();
    }
    @GetMapping("/chat")
    public List<Generation> chat(String question){
        String message=question;
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt=promptTemplate.create();
        ChatClient.ChatClientRequest.CallPromptResponseSpec responseSpec=chatClient.prompt(prompt).call();
        return responseSpec.chatResponse().getResults();
    }

}

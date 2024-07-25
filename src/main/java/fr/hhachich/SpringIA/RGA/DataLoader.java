package fr.hhachich.SpringIA.RGA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Component
public class DataLoader {
    @Value("classpath:/pdf/Fake-CV.pdf")
    private Resource pdfFile;
    @Value("vector1.json")
    private String vectorStoreName;
    private static Logger log = LoggerFactory.getLogger(DataLoader.class);
    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel){
        SimpleVectorStore simpleVectorStore=new SimpleVectorStore(embeddingModel);
        String path= Path.of("src","main","resources","vectorestore").toFile().getAbsolutePath()+"/"+vectorStoreName;
        File fileStore=new File(path);
        try {
            if (fileStore.exists()) {
                log.info("Vector store exist=>" + path);
                simpleVectorStore.load(fileStore);
            } else {
                PagePdfDocumentReader documentReader = new PagePdfDocumentReader(pdfFile);
                List<Document> documents = documentReader.get();
                TextSplitter textSplitter = new TokenTextSplitter();
                List<Document> chunks = textSplitter.split(documents);
                simpleVectorStore.add(chunks);
                simpleVectorStore.save(fileStore);
            }
        }catch (Exception e) {
                log.error("Error during vector store creation or loading", e);
                throw new RuntimeException(e);
            }
        return simpleVectorStore;
    }

}

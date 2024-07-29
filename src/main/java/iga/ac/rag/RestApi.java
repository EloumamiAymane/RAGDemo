package iga.ac.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
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
public class RestApi {
    private ChatClient chatClient;
    @Value("classpath:/prompts/prompt.st")
    private Resource promptResource;
    private VectorStore vectorStore;


    RestApi(ChatClient.Builder builder, VectorStore vectorStore){
        this.chatClient=builder.build();
        this.vectorStore = vectorStore;
    }
    @GetMapping(path = "/ask",produces = MediaType.TEXT_PLAIN_VALUE)
    public String ask(String question){
        PromptTemplate promptTemplate=new PromptTemplate(promptResource);
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.query(question));
        List<String> list = documents.stream().map(d -> d.getContent()).toList();
        Prompt prompt = promptTemplate.create(Map.of("context", list, "question", question));
        return chatClient.prompt(prompt).call().content();


    }
}

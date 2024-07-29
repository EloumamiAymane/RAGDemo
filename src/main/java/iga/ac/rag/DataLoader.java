package iga.ac.rag;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
@Component
public class DataLoader {
    @Value("classpath:/pdfs/CDI.pdf")
    private Resource pdfFile;
    @Value("iga-vs4.json")
    private String  vectorStoreName;
    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel){
    SimpleVectorStore simpleVectorStore=new SimpleVectorStore(embeddingModel);
    String path= Path.of("src","main","resources","vectorsStore").toFile().getAbsolutePath()+"/"+vectorStoreName;
    File file= new File(path);
    if(file.exists()){
        System.out.println("exists !");
        simpleVectorStore.load(file);
    }else{
          PagePdfDocumentReader pagePdfDocumentReader=new PagePdfDocumentReader(pdfFile);
          List<Document> documents = pagePdfDocumentReader.get();
          TextSplitter textSplitter= new TokenTextSplitter();
          List<Document>chunks=textSplitter.split(documents);
          simpleVectorStore.add(chunks);
          simpleVectorStore.save(file);
        }
      return  simpleVectorStore;
    }

    }


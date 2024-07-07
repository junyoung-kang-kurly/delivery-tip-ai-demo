package com.example.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Service
public class DeliveryTipService {

    @Autowired
    VectorStore vectorStore;

    @Autowired
    ChatClient chatClient;

    @Autowired
    JdbcClient jdbcClient;

    @Value("classpath:/prompts/prompt_template.st")
    private Resource template;

    /*
    요약해줘.
    몇번게이트가 가까워?
    공동현관 비번은?
    공비?
    4번게이트 위치 알려줘
    고층 가려면 어떻게해?
     */
    public String query(String query) {

        List<Document> results = vectorStore.similaritySearch(SearchRequest.query(query).withTopK(10));
        results.forEach(System.out::println);

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("question", query);
        promptParameters.put("documents", findSimilarDocuments(query));

        return chatClient.prompt(promptTemplate.create(promptParameters))
            .call()
            .content();
    }


    private List<Document> findSimilarDocuments(String message) {
        return vectorStore.similaritySearch(SearchRequest.query(message).withTopK(10));
    }

}

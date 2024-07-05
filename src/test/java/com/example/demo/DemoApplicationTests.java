package com.example.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    VectorStore vectorStore;

    @Autowired
    ChatClient chatClient;

    @Autowired
    JdbcClient jdbcClient;

    @Value("classpath:/prompts/prompt_template.st")
    private Resource template;

    private final String 건물번호 = "1154510100102330005015498";

    record DeliveryType(
        String buildingNumber,
        String tip
    ) {

    }

    List<DeliveryType> create() {
        return List.of(
            new DeliveryType(건물번호, "A B C 동 호수 확인요망"),
            new DeliveryType(건물번호, "11층에서 고층용으로 연결됨"),
            new DeliveryType(건물번호, "공비4238"),
            new DeliveryType(건물번호, "방화문 안에"),
            new DeliveryType(건물번호, "방화문 안에"),

            new DeliveryType(건물번호, "3번 게이트(a19~31, 55~62)"),
            new DeliveryType(건물번호, "2번 게이트(a1~18,63~76) (b1~4,40~43)"),
            new DeliveryType(건물번호, "1번 게이트"),
            new DeliveryType(건물번호, "팁 믿으면 안됨. 호수 마다 게이트 다름"),
            new DeliveryType(건물번호, "2번게이트가 가까움"),
            new DeliveryType(건물번호, "4번 게이트 (A32~54)"),
            new DeliveryType(건물번호, "1번 게이트(B5~39)")
        );
    }

    @Test
    void contextLoads() {
        createIfEmpty();

        List<Document> results = vectorStore.similaritySearch(SearchRequest.query(건물번호).withTopK(100));
        results.forEach(System.out::println);

        PromptTemplate promptTemplate = new PromptTemplate(template);

        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("buildingNumber", 건물번호);
        promptParameters.put("documents", findSimilarDocuments("buildingNumber:" + 건물번호));

        String content = chatClient.prompt(promptTemplate.create(promptParameters))
            .call()
            .content();


        System.out.println(content);
    }

    private void createIfEmpty() {
        Integer count = jdbcClient.sql("select count(*) from vector_store").query(Integer.class).single();
        if (count != 0) {
            return;
        }
        List<Document> docs = create().stream().map(
            deliveryType -> new Document(
                deliveryType.toString(),
                Map.of("buildingNumber", deliveryType.buildingNumber(), "tip", deliveryType.tip())
            )
        ).toList();
        vectorStore.accept(docs);
    }

    private List<Document> findSimilarDocuments(String message) {
        return vectorStore.similaritySearch(SearchRequest.query(message).withTopK(10));
    }


}

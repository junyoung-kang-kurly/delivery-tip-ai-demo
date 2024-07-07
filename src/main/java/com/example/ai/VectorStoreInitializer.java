package com.example.ai;

import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class VectorStoreInitializer implements ApplicationRunner {

    @Autowired
    VectorStore vectorStore;

    @Autowired
    ChatClient chatClient;

    @Autowired
    JdbcClient jdbcClient;

    private final String 건물번호1 = "1154510100102330005015498";


    @Override
    public void run(ApplicationArguments args) throws Exception {
        createIfEmpty();
    }

    private void createIfEmpty() {
        Integer count = jdbcClient.sql("select count(*) from vector_store").query(Integer.class).single();
        if (count != 0) {
            return;
        }
        List<Document> docs = create().stream().map(
            deliveryType -> new Document(
                deliveryType.toString(),
                Map.of(
                    "팁유형", deliveryType.tipType(),
                    "건물번호", deliveryType.buildingNumber(),
                    "건물팁", deliveryType.tip()
                )
            )
        ).toList();
        vectorStore.accept(docs);
    }


    List<DeliveryTip> create() {
        return List.of(
            new DeliveryTip("건물팁", 건물번호1, "A B C 동 호수 확인요망"),
            new DeliveryTip("건물팁", 건물번호1, "11층에서 고층용으로 연결됨"),
            new DeliveryTip("건물팁", 건물번호1, "공비4238"),
            new DeliveryTip("건물팁", 건물번호1, "방화문이 따로 있음"),
            new DeliveryTip("건물팁", 건물번호1, "3번 게이트(a19~31, 55~62)"),
            new DeliveryTip("건물팁", 건물번호1, "2번 게이트(a1~18,63~76) (b1~4,40~43)"),
            new DeliveryTip("건물팁", 건물번호1, "1번 게이트"),
            new DeliveryTip("건물팁", 건물번호1, "팁 믿으면 안됨. 호수 마다 게이트 다름"),
            new DeliveryTip("건물팁", 건물번호1, "2번게이트가 가까움"),
            new DeliveryTip("건물팁", 건물번호1, "4번 게이트 (A32~54)"),
            new DeliveryTip("건물팁", 건물번호1, "1번 게이트(B5~39)")
        );
    }
}

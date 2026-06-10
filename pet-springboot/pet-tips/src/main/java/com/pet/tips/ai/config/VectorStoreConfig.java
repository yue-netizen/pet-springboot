package com.pet.tips.ai.config;

import com.pet.tips.ai.vectorstore.InMemoryVectorStore;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:}")
    private String baseUrl;

    @Value("${tips-ai.embedding-model:text-embedding-v3}")
    private String embeddingModelName;

    @Bean
    public EmbeddingModel embeddingModel() {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();
        return new OpenAiEmbeddingModel(openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .model(embeddingModelName)
                        .build());
    }

    @Bean
    public InMemoryVectorStore inMemoryVectorStore(EmbeddingModel embeddingModel) {
        return new InMemoryVectorStore(embeddingModel);
    }
}

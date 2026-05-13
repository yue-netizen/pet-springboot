package com.pet.tips.ai.vectorstore;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryVectorStore {

    private final EmbeddingModel embeddingModel;
    private final List<StoredDocument> store = Collections.synchronizedList(new ArrayList<>());
    private final Path persistPath;

    public InMemoryVectorStore(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
        this.persistPath = Path.of(System.getProperty("user.dir"), "data", "pet-tips-vector-store.json");
    }

    @PostConstruct
    public void init() {
        loadFromFile();
        log.info("向量库初始化完成, 当前文档数: {}", store.size());
    }

    private void loadFromFile() {
        if (!Files.exists(persistPath)) {
            log.info("向量存储文件不存在，将使用空库: {}", persistPath);
            return;
        }
        try {
            String json = Files.readString(persistPath, StandardCharsets.UTF_8);
            JSONArray array = JSON.parseArray(json);
            if (array == null || array.isEmpty()) {
                log.warn("向量存储文件内容为空");
                return;
            }
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String text = obj.getString("text");
                Map<String, Object> metadata = new LinkedHashMap<>();
                if (obj.containsKey("metadata")) {
                    metadata.putAll(obj.getJSONObject("metadata"));
                }
                JSONArray embArr = obj.getJSONArray("embedding");
                float[] embedding = new float[embArr.size()];
                for (int j = 0; j < embArr.size(); j++) {
                    embedding[j] = embArr.getFloatValue(j);
                }
                Document doc = new Document(text, metadata);
                store.add(new StoredDocument(doc, embedding));
            }
            log.info("从文件加载 {} 条向量数据", store.size());
        } catch (Exception e) {
            log.error("加载向量存储文件失败: {}", e.getMessage(), e);
        }
    }

    public void add(Document document) {
        float[] embedding = embeddingModel.embed(document.getText());
        store.add(new StoredDocument(document, embedding));
        persist();
        log.info("向量库新增1条文档, 当前总量: {}", store.size());
    }

    public void add(List<Document> documents) {
        if (documents == null || documents.isEmpty()) return;

        for (Document doc : documents) {
            float[] embedding = embeddingModel.embed(doc.getText());
            store.add(new StoredDocument(doc, embedding));
        }

        persist();
        log.info("向量库新增 {} 条文档, 当前总量: {}", documents.size(), store.size());
    }

    public void removeByIndex(int index) {
        if (index >= 0 && index < store.size()) {
            StoredDocument removed = store.remove(index);
            persist();
            log.info("向量库删除第{}条文档: {}", index,
                    removed.document.getMetadata().getOrDefault("title", "未知"));
        }
    }

    public void clear() {
        int size = store.size();
        store.clear();
        persist();
        log.info("向量库已清空, 共删除{}条文档", size);
    }

    public void rebuild(List<Document> documents) {
        store.clear();
        add(documents);
        log.info("向量库已重建, 共{}条文档", store.size());
    }

    public List<Document> similaritySearch(String query, int topK, double minScore) {
        if (store.isEmpty()) {
            return Collections.emptyList();
        }

        float[] queryEmbedding = embeddingModel.embed(query);

        PriorityQueue<ScoredDocument> topKQueue = new PriorityQueue<>(
                topK, Comparator.comparingDouble(ScoredDocument::score));

        for (StoredDocument doc : store) {
            double score = cosineSimilarity(queryEmbedding, doc.embedding);
            if (score >= minScore) {
                topKQueue.offer(new ScoredDocument(score, doc.document));
                if (topKQueue.size() > topK) {
                    topKQueue.poll();
                }
            }
        }

        List<ScoredDocument> result = new ArrayList<>(topKQueue);
        result.sort((a, b) -> Double.compare(b.score, a.score));
        log.info("向量检索完成, 查询='{}', 命中{}条", query, result.size());

        return result.stream().map(sd -> sd.document).collect(Collectors.toList());
    }

    public SearchResult testSearch(String query, int topK, double minScore) {
        if (store.isEmpty()) {
            return new SearchResult(0, query, Collections.emptyList());
        }

        float[] queryEmbedding = embeddingModel.embed(query);

        List<SearchHit> hits = new ArrayList<>();
        for (int i = 0; i < store.size(); i++) {
            StoredDocument doc = store.get(i);
            double score = cosineSimilarity(queryEmbedding, doc.embedding);
            if (score >= minScore) {
                Map<String, Object> meta = doc.document.getMetadata();
                hits.add(new SearchHit(
                        i,
                        meta.getOrDefault("id", "-").toString(),
                        meta.getOrDefault("title", "-").toString(),
                        meta.getOrDefault("category", "-").toString(),
                        score,
                        doc.document.getText().length() > 150 ? doc.document.getText().substring(0, 150) + "..." : doc.document.getText()
                ));
            }
        }

        hits.sort((a, b) -> Double.compare(b.score, a.score));
        if (hits.size() > topK) {
            hits = hits.subList(0, topK);
        }

        return new SearchResult(hits.size(), query, hits);
    }

    public VectorStoreInfo getInfo() {
        List<Map<String, Object>> docs = new ArrayList<>();
        for (int i = 0; i < store.size(); i++) {
            StoredDocument sd = store.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("index", i);
            item.put("id", sd.document.getMetadata().getOrDefault("id", "-"));
            item.put("title", sd.document.getMetadata().getOrDefault("title", "-"));
            item.put("category", sd.document.getMetadata().getOrDefault("category", "-"));
            String text = sd.document.getText();
            item.put("textLength", text.length());
            item.put("textPreview", text.length() > 100 ? text.substring(0, 100) + "..." : text);
            item.put("embeddingDim", sd.embedding.length);
            docs.add(item);
        }
        return new VectorStoreInfo(store.size(), docs);
    }

    public int size() {
        return store.size();
    }

    public boolean isEmpty() {
        return store.isEmpty();
    }

    private synchronized void persist() {
        try {
            Files.createDirectories(persistPath.getParent());

            JSONArray array = new JSONArray();
            for (StoredDocument sd : store) {
                JSONObject obj = new JSONObject();
                obj.put("text", sd.document.getText());
                obj.put("metadata", sd.document.getMetadata());
                JSONArray embArr = new JSONArray();
                for (float v : sd.embedding) {
                    embArr.add(v);
                }
                obj.put("embedding", embArr);
                array.add(obj);
            }

            Files.writeString(persistPath, array.toJSONString(), StandardCharsets.UTF_8);
            log.debug("向量数据已持久化到文件: {}, 共{}条", persistPath, store.size());
        } catch (Exception e) {
            log.error("向量数据持久化失败: {}", e.getMessage(), e);
        }
    }

    private double cosineSimilarity(float[] v1, float[] v2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        double denominator = Math.sqrt(norm1) * Math.sqrt(norm2);
        return denominator > 0 ? dotProduct / denominator : 0.0;
    }

    public record VectorStoreInfo(int totalDocuments, List<Map<String, Object>> documents) {}
    public record SearchResult(int totalHits, String query, List<SearchHit> hits) {}
    public record SearchHit(int index, String id, String title, String category, double score, String contentPreview) {}
    private record StoredDocument(Document document, float[] embedding) {}
    private record ScoredDocument(double score, Document document) {}
}

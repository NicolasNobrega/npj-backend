package com.uniprojecao.fabrica.gprojuridico.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.uniprojecao.fabrica.gprojuridico.dto.QueryFilter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.google.cloud.firestore.Query.Direction.DESCENDING;
import static com.uniprojecao.fabrica.gprojuridico.services.utils.Utils.filter;
import static com.uniprojecao.fabrica.gprojuridico.services.utils.Utils.sleep;

@Repository
@Primary
public class BaseRepository {

    @Autowired
    public Firestore firestore;

    public void save(String collectionName, Object data) {
        try {
            firestore.collection(collectionName).add(data).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String collectionName, String CustomId, Object data) {
        try {
            firestore.collection(collectionName).document(CustomId).set(data).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    List<Object> findAll(String collectionName, @Nullable String[] fields, @Nullable Class<?> type, @Nonnull Integer limit, @Nullable QueryFilter queryFilter) {
        List<Object> list = new ArrayList<>();

        try {
            var result = getDocSnapshots(collectionName, fields, limit, queryFilter);
            for (QueryDocumentSnapshot document : result) {
                if (type != null) list.add(document.toObject(type));
                else list.add(document);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    DocumentSnapshot findLast(String collectionName) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(collectionName).orderBy("instante", DESCENDING).limit(1).get();
            var list = future.get().getDocuments();
            return (!list.isEmpty()) ? list.get(0) : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Object findById(String collectionName, @Nullable Class<?> type, String id) {
        try {
            DocumentReference document = firestore.collection(collectionName).document(id);
            DocumentSnapshot snapshot = document.get().get();
            if (!snapshot.exists()) return null;
            if (type != null) return snapshot.toObject(type);
            return snapshot;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String collectionName, String id, Map<String, Object> data) {
        firestore.collection(collectionName).document(id).update(convertMap(data));
    }

    public void delete(String collectionName, String id) {
        firestore.collection(collectionName).document(id).delete();
    }

    public void deleteAll(String collectionName, String[] list, Integer limit, @Nullable QueryFilter queryFilter) {
        var result = getDocSnapshots(collectionName, list, limit, queryFilter);
        for (QueryDocumentSnapshot document : result) {
            document.getReference().delete();
            sleep(200); // Eventualmente será necessário considerar usar programação reativa no sistema, pois a deleção assíncrona deve garantir a deleção em massa por completo.
        }
    }

    List<QueryDocumentSnapshot> getDocSnapshots(String collectionName, @Nullable String[] list, @Nonnull Integer limit, @Nullable QueryFilter queryFilter) {
        if (list == null) list = new String[]{"id"};

        ApiFuture<QuerySnapshot> future;

        future = (queryFilter != null) ?
                firestore.collection(collectionName).where(filter(queryFilter)).select(list).limit(limit).get() :
                firestore.collection(collectionName).select(list).limit(limit).get();

        try {
            return future.get().getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> convertMap(Map<String, Object> inputMap) {
        Map<String, Object> outputMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            processEntry(entry.getKey(), entry.getValue(), outputMap, "");
        }
        return outputMap;
    }

    private static void processEntry(String key, Object value, Map<String, Object> outputMap, String parentKey) {
        // Se o valor é um Map, deve-se percorrer o método recursivamente para concatenar as chaves aninhadas
        if (value instanceof Map) {
            Map<String, Object> subMap = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : subMap.entrySet()) {
                processEntry(entry.getKey(), entry.getValue(), outputMap, parentKey + key + ".");
            }
        } else {
            outputMap.put(parentKey + key, value); // Ex. de entrada percorrida recursivamente a ser adicionada: {"ficha.parteContraria.nome", "Mauro Silva"}
        }
    }
}

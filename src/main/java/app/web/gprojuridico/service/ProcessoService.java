package app.web.gprojuridico.service;

import app.web.gprojuridico.model.Processo;
import app.web.gprojuridico.repository.BaseRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.web.gprojuridico.service.utils.AtendimentoUtils.convertSnapshotToCorrespondingAtendimentoDTO;
import static app.web.gprojuridico.service.utils.Utils.convertUsingReflection;

@Service
public class ProcessoService {

    @Autowired
    BaseRepository repository;

    CollectionReference collection = FirestoreClient.getFirestore().collection("processos");

    public Map<String, Object> insert(Processo data) {
        DocumentReference result = repository.save(collection, data);

        ApiFuture<DocumentSnapshot> future = result.get();
        Processo o;

        try {
            DocumentSnapshot snapshot = future.get();
            o = snapshot.toObject(Processo.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String documentId = result.getId();
        System.out.println("\nProcesso adicionado. ID: " + documentId);

        try {
            return Map.of(
                    "object", convertUsingReflection(o),
                    "id", documentId
            );
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object> findAll(String limit) {
        List<QueryDocumentSnapshot> result = repository.findAll(collection, Integer.parseInt(limit));
        List<Object> list = new ArrayList<>();

        for (QueryDocumentSnapshot document : result) {
            list.add(document.toObject(Processo.class));
        }

        return list;
    }

    public Object findById(String id) {
        DocumentSnapshot snapshot = repository.findById(collection, id);
        return snapshot.toObject(Processo.class);
    }

    public Boolean update(String id, Map<String, Object> data) {
        return repository.update(collection, id, data);
    }

    public Boolean delete(String id) {
        return repository.delete(collection, id);
    }

    public Boolean deleteAll(String limit) {
        return repository.deleteAll(collection, Integer.parseInt(limit));
    }
}
package app.web.gprojuridico.service;

import app.web.gprojuridico.exception.EmailAlreadyExistsException;
import app.web.gprojuridico.exception.NameAlreadyExistsException;
import app.web.gprojuridico.model.user.AuthenticationDTO;
import app.web.gprojuridico.model.user.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
public class UserService implements UserDetailsService {
    private static final String COLLECTION_NAME = "acesso";
    
    @Autowired
    Firestore firestore;

    public User create(User user) throws ExecutionException, InterruptedException {

        // Verificar se username e login sao unicos
        Query emailQuery = firestore.collection(COLLECTION_NAME).whereEqualTo("login", user.getLogin());
        QuerySnapshot emailQuerySnapshot = emailQuery.get().get();

        Query nameQuery = firestore.collection(COLLECTION_NAME).whereEqualTo("username", user.getUsername());
        QuerySnapshot nameQuerySnapshot = nameQuery.get().get();

        if (!emailQuerySnapshot.isEmpty()) {
            // Email ja cadastrado
            throw new EmailAlreadyExistsException("Email já cadastrado");

        } else if (!nameQuerySnapshot.isEmpty()) {
            // Nome ja cadastrado
            throw new NameAlreadyExistsException("Nome já cadastrado");

        } else {
            // Pegar e incrementar id
            Query lastUserQuery = firestore.collection(COLLECTION_NAME).orderBy("id", Query.Direction.DESCENDING).limit(1);
            QuerySnapshot lastUserQuerySnapshot = lastUserQuery.get().get();

            int lastUserId = 0;

            if (!lastUserQuerySnapshot.isEmpty()) {
                lastUserId = Objects.requireNonNull(lastUserQuerySnapshot.getDocuments().get(0).getLong("id")).intValue();
            }

            int newUserId = lastUserId + 1;
            user.setId(newUserId);

            String pw_hash = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
            user.setPassword(pw_hash);

            System.out.println(pw_hash);
            System.out.println("Senha criada:" + user.getPassword());

            DocumentReference documentReference = firestore.collection(COLLECTION_NAME).document();
            ApiFuture<WriteResult> collectionApiFuture = documentReference.set(user);

            return user; // Retorna o usuário criado
        }
    }

    public void deleteUserById(String documentId) {
        
        CollectionReference usersCollection = firestore.collection(COLLECTION_NAME);

        DocumentReference documentReference = usersCollection.document(documentId);

        ApiFuture<WriteResult> writeResult = documentReference.delete();

        try {
            writeResult.get();
            System.out.println("Usuário com o ID " + documentId + " deletado com sucesso.");
        } catch (NotFoundException e) {
            throw new RuntimeException("Usuário não encontrado: ", e);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Erroao deletar o usuário: " + e.getMessage());
        }
    }

    public User findUserByEmail(String login) throws ExecutionException, InterruptedException {
        
        CollectionReference usersCollection = firestore.collection(COLLECTION_NAME);

        User foundUser = null;

        // Query Firestore to find a user with matching login and password
        List<QueryDocumentSnapshot> matchingUsers;
        try {
            matchingUsers = usersCollection
                    .whereEqualTo("login", login)
                    .get().get().getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Erro ao consultar o banco firebase: " + e.getMessage(), e);
        }

        if (!matchingUsers.isEmpty()) {
            // Assuming login and password combination is unique, return the first matching user
            System.out.println("Usuário encontrado no Fb: " +matchingUsers.get(0));

            foundUser = matchingUsers.get(0).toObject(User.class);
            foundUser.setDocumentId(matchingUsers.get(0).getId());

            // Retrieve perfil data based on perfil_id
            System.out.println("Usuário encontrado objeto: " + foundUser);
        }

        if (foundUser == null) {
            throw new RuntimeException("Nenhum usuário encontrado com a password e e-mail fornecidos.");
        }

        return foundUser;
    }

    public User findUserByEmailAndPassword(AuthenticationDTO user) throws ExecutionException, InterruptedException {
        
        CollectionReference usersCollection = firestore.collection(COLLECTION_NAME);

        User foundUser = null;

        // Extract login and password from the user object
        String login = user.login();
        String password = user.password();

        // Query Firestore to find a user with matching login and password
        List<QueryDocumentSnapshot> matchingUsers;
        try {
            matchingUsers = usersCollection
                    .whereEqualTo("login", login)
                    .get().get().getDocuments();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Erro ao consultar o banco firebase: " + e.getMessage(), e);
        }

        if (!matchingUsers.isEmpty()) {
            // Assuming login and password combination is unique, return the first matching user
            System.out.println(matchingUsers.get(0));

            foundUser = matchingUsers.get(0).toObject(User.class);
            foundUser.setDocumentId(matchingUsers.get(0).getId());

            // Retrieve perfil data based on perfil_id
            System.out.println(foundUser);
        }

        if (foundUser == null) {
            throw new RuntimeException("Nenhum usuário encontrado com a password e e-mail fornecidos.");
        }

        BCrypt.Result passwordCheck = BCrypt.verifyer().verify(password.toCharArray(), foundUser.getPassword());
        if (passwordCheck.verified) {
            return foundUser;
        } else {
            throw new RuntimeException("A Senha do usuário incorreta");
        }
    }

    public ArrayList toggleUserStatus(String documentId) {

        try {
            DocumentReference userDocument = firestore.collection(COLLECTION_NAME).document(documentId);
            ApiFuture<DocumentSnapshot> documentSnapshot = userDocument.get();

            String currentStatus = documentSnapshot.get().getString("accountNonLocked");
            String newStatus = "Ativo".equals(currentStatus) ? "Inativo" : "Ativo";

            ApiFuture<WriteResult> updateResult = userDocument.update("accountNonLocked", newStatus);
            updateResult.get();

            // Include additional data if needed, e.g., updated accountNonLocked
            return new ArrayList<>();
        } catch (NotFoundException e) {
            throw new RuntimeException("Error usuário não encontrado: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error enquanto atualiza o status do usuário: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        CollectionReference usersCollection = firestore.collection(COLLECTION_NAME);

        try {
            ApiFuture<QuerySnapshot> query = usersCollection.get();
            QuerySnapshot querySnapshot = query.get();
            List<User> userList = new ArrayList<>();

            for (QueryDocumentSnapshot userDocument : querySnapshot) {
                // Convert Firestore document to your User object
                User user = userDocument.toObject(User.class);
                user.setDocumentId(userDocument.getId());

                // Check if the user is already in the list
                if (!userList.contains(user)) {
                    userList.add(user);
                }
            }

            return userList;
        } catch (InterruptedException | ExecutionException e) {
            // Handle any exceptions
            throw new RuntimeException("Erro ao retornar usuarios do fb: " + e.getMessage(), e);
        }
    }

    public List<User> getUserByName(String nome) {
        
        CollectionReference usersCollection = firestore.collection(COLLECTION_NAME);

        try {
            // Create a query with the filter on the "username" field
            Query query = usersCollection.whereEqualTo("username", nome);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            List<User> userList = new ArrayList<>();

            for (QueryDocumentSnapshot document : querySnapshot.get().getDocuments()) {
                // Convert Firestore document to your User object
                User user = document.toObject(User.class);
                user.setDocumentId(document.getId());
                userList.add(user);
            }

            return userList;
        } catch (InterruptedException | ExecutionException e) {
            // Handle any exceptions
            throw new RuntimeException("Erro ao retornar usuarios do fb: " + e.getMessage(), e);
        }
    }

    public User getUserById(String userId) {
        
        DocumentReference userDocRef = firestore.collection(COLLECTION_NAME).document(userId);

        try {
            DocumentSnapshot userSnapshot = userDocRef.get().get();

            User user = userSnapshot.toObject(User.class);
            assert user != null;
            user.setDocumentId(userSnapshot.getId());
            return user;

        } catch (NotFoundException e) {
            throw new RuntimeException("Usuário não encontrado: ", e);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Erro ao buscar usuário: ", e);
        }
    }

    public User updateUser(User updatedUser) {
        DocumentReference userDocRef = firestore.collection(COLLECTION_NAME).document(updatedUser.getDocumentId());

        try {
            DocumentSnapshot userSnapshot = userDocRef.get().get();
            if (userSnapshot.exists()) {
                userDocRef.update(
                        "username", updatedUser.getUsername(),
                        "login", updatedUser.getLogin(),
                        "semestre", updatedUser.getSemestre(),
                        "password", updatedUser.getPassword(),
                        "perfil", updatedUser.getPerfil(),
                        "accountNonLocked", updatedUser.isAccountNonLocked()
                ).get();

                // Recupere o usuário atualizado para retornar ao chamador
                DocumentSnapshot updatedUserSnapshot = userDocRef.get().get();
                User user = updatedUserSnapshot.toObject(User.class);
                assert user != null;
                user.setDocumentId(updatedUserSnapshot.getId());

                return user;
            } else {
                throw new RuntimeException("Usuário não encontrado para atualização. ID: " + updatedUser.getDocumentId());
            }
        } catch (NotFoundException e) {
            throw new RuntimeException("Usuário não encontrado: ", e);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Erro ao atualizar usuário: ", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return findUserByEmail(username);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

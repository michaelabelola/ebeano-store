package com.michael.ebeano.services;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.michael.ebeano.models.User;
import com.michael.ebeano.models.UserDoc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceImpl implements UserService {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    final CollectionReference col = FirebaseFirestore.getInstance().collection("users");

    public Optional<User> getById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> listAll() {
        return new ArrayList<>(users.values());
    }

    public void delete(String id) {
        col.document(id).delete();
        users.remove(id);
    }

    public List<User> findByField(String fieldName, Object value) {
        return new ArrayList<>();
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> email != null && email.equals(u.email()))
                .findFirst();
    }

    public User register(String email, String passwordHash, String firstName, String lastName, String phone) {
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        User user = new User(id, email, passwordHash, firstName, lastName, phone, false, now, now);
        users.put(id, user);
        UserDoc d = new UserDoc();
        d.id = id;
        d.email = email;
        d.passwordHash = passwordHash;
        d.firstName = firstName;
        d.lastName = lastName;
        d.phone = phone;
        d.emailVerified = false;
        d.createdAt = Timestamp.now();
        d.updatedAt = d.createdAt;
        col.document(id).set(d);
        return user;
    }

    public Optional<User> verifyEmail(String userId) {
        User u = users.get(userId);
        if (u == null) return Optional.empty();
        User updated = new User(u.id(), u.email(), u.passwordHash(), u.firstName(), u.lastName(), u.phone(), true, u.createdAt(), LocalDateTime.now());
        users.put(userId, updated);
        col.document(userId).update("emailVerified", true, "updatedAt", Timestamp.now());
        return Optional.of(updated);
    }

    public Optional<User> updateNames(String userId, String firstName, String lastName) {
        User u = users.get(userId);
        if (u == null) return Optional.empty();
        User updated = new User(u.id(), u.email(), u.passwordHash(), firstName, lastName, u.phone(), u.emailVerified(), u.createdAt(), LocalDateTime.now());
        users.put(userId, updated);
        col.document(userId).update("firstName", firstName, "lastName", lastName, "updatedAt", Timestamp.now());
        return Optional.of(updated);
    }
}

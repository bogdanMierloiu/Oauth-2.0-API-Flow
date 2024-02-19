package ro.bogdanmierloiu.Oauth2APIFlow.controller;

import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

public interface CrudController<K, L> {

    ResponseEntity<Set<L>> getAll();

    ResponseEntity<L> getByUuid(UUID uuid);

    ResponseEntity<L> save(K k);

    ResponseEntity<L> update(UUID objectToUpdateUuid, K k);

    ResponseEntity<Void> delete(UUID uuid);
}

package ro.bogdanMierloiu.Oauth2APIFlow.controller;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CrudController<K, L> {

    ResponseEntity<List<L>> getAll();

    ResponseEntity<L> getByUuid(UUID uuid);

    ResponseEntity<L> save(K k);

    ResponseEntity<L> update(UUID objectToUpdateUuid, K k);

    ResponseEntity<Void> delete(UUID uuid);
}

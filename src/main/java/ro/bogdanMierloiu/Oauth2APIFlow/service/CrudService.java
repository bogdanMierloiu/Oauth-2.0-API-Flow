package ro.bogdanMierloiu.Oauth2APIFlow.service;

import java.util.List;
import java.util.UUID;

public interface CrudService<K, L> {

    L save(K k);

    L update(UUID uuid, K k);

    L getByUuid(UUID uuid);

    List<L> getAll();

    void delete(UUID uuid);
}

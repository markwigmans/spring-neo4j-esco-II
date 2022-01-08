package com.btb.sne.service;

import com.btb.sne.model.neo.N_BaseEntity;
import com.btb.sne.model.neo.N_EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class N_EntityService {

    private final N_EntityRepository repository;

    public N_BaseEntity save(N_BaseEntity entity) {
        return repository.save(entity);
    }

    public void save(List<N_BaseEntity> entities) {
        repository.saveAll(entities);
    }

    public Optional<N_BaseEntity> get(String uri) {
        return repository.findById(uri);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}

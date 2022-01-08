package com.btb.sne.service;

import com.btb.sne.model.jpa.J_Entity;
import com.btb.sne.model.jpa.J_EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class J_EntityService {

    private final J_EntityRepository repository;

    public J_Entity save(J_Entity entity) {
        return repository.save(entity);
    }

    public void save(List<J_Entity> entities) {
        repository.saveAll(entities);
    }

    public Optional<J_Entity> get(String uri) {
        return repository.findById(uri);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}

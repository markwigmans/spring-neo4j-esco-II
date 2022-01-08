package com.btb.sne.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {

    final private J_EntityService jpaEntityService;
    final private N_EntityService neoEntityService;

    public void deleteAll() {
        jpaEntityService.deleteAll();
        neoEntityService.deleteAll();
    }
}

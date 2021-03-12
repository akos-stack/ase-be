package com.bloxico.ase.userservice.service.constant.impl;

import com.bloxico.ase.userservice.entity.user.Permission;
import com.bloxico.ase.userservice.repository.user.PermissionRepository;
import com.bloxico.ase.userservice.service.constant.IConstantService;
import com.bloxico.ase.userservice.util.EnumConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.bloxico.ase.userservice.util.AseMapper.MAPPER;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ConstantServiceImpl implements IConstantService {

    private final PermissionRepository permissionRepository;

    @Autowired
    public ConstantServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Map<String, Object> constantsAsMap() {
        log.info("ConstantServiceImpl.constantsAsMap - start");
        var constantsMap = EnumConstants.asMap();
        var permissions = permissionRepository
                .findAll()
                .stream()
                .sorted(comparing(Permission::getName))
                .map(MAPPER::toDto)
                .collect(toList());
        constantsMap.putAll(Map.of("PERMISSION", permissions));
        log.info("ConstantServiceImpl.constantsAsMap - end");
        return constantsMap;
    }

}

package com.bloxico.ase.testutil;

import com.bloxico.ase.userservice.dto.entity.config.ConfigDto;
import com.bloxico.ase.userservice.entity.config.Config.Type;
import org.springframework.stereotype.Component;

import static com.bloxico.ase.testutil.Util.genUUID;
import static com.bloxico.ase.testutil.Util.randEnumConst;

@Component
public class UtilConfig {

    public ConfigDto genConfigDto() {
        return genConfigDto(randEnumConst(Type.class), genUUID());
    }

    public ConfigDto genConfigDto(Type type, String value) {
        var configDto = new ConfigDto();
        configDto.setType(type);
        configDto.setValue(value);
        return configDto;
    }

}

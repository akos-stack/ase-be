package com.bloxico.userservice.web.model.registration;

import com.bloxico.userservice.dto.entities.RegionDto;
import lombok.Data;

import java.util.List;

@Data
public class RegistrationDataResponse {
    List<RegionDto> regions;
}

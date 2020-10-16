package com.bloxico.userservice.web.model.userprofile;

import com.bloxico.userservice.dto.entities.RegionDto;
import com.bloxico.userservice.dto.entities.UserProfileDto;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileDataResponse {

    private UserProfileDto userProfile;
    private List<RegionDto> regions;

}

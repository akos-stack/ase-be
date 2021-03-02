package com.bloxico.ase.userservice.web.model.evaluation;

import io.swagger.annotations.ApiParam;
import lombok.*;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true, access = PRIVATE)
public class SearchCountryEvaluationDetailsForManagementRequest implements ISearchCountryEvaluationDetailsRequest {

    @NotNull
    @ApiParam(name = "search", required = true)
    String search;

    @ApiParam(name = "regions")
    Set<String> regions;

    @JsonIgnore
    @Override
    public boolean includeCountriesWithoutEvaluationDetails() {
        return true;
    }

}

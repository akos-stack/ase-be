package com.bloxico.ase.userservice.service.evaluation;

import com.bloxico.ase.userservice.dto.entity.evaluation.CountryEvaluationDetailsDto;
import com.bloxico.ase.userservice.proj.CountryEvaluationDetailsCountedProj;
import com.bloxico.ase.userservice.web.model.evaluation.SearchCountryEvaluationDetailsRequest;
import org.springframework.data.domain.Page;

public interface IEvaluationService {

    Page<CountryEvaluationDetailsCountedProj> findAll(SearchCountryEvaluationDetailsRequest request);

    CountryEvaluationDetailsDto saveCountryEvaluationDetails(CountryEvaluationDetailsDto dto, long principalId);

    CountryEvaluationDetailsDto updateCountryEvaluationDetails(CountryEvaluationDetailsDto dto, long principalId);

}

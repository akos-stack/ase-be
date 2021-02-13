package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.Country;
import com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj;
import com.bloxico.ase.userservice.web.model.address.SearchCountriesRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

    Optional<Country> findByNameIgnoreCase(String name);

    @Query(
            value =
                    "select " +
                            "new com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj " +
                            "(c.id, c.name, c.region.name, " +
                            "c.countryEvaluationDetails.pricePerEvaluation, c.countryEvaluationDetails.availabilityPercentage, " +
                            "count(e.id) as total_of_evaluators) " +
                            "from Evaluator e " +
                            "right join e.userProfile.location.country c " +
                            "where (c.region.name in :regions or :regions is null) " +
                            "and (lower(c.name) like lower(concat('%', :search, '%')) " +
                            "or lower(c.region.name) like lower(concat('%', :search, '%'))) " +
                            "group by c.id, c.region.name, c.countryEvaluationDetails.pricePerEvaluation, " +
                            "c.countryEvaluationDetails.availabilityPercentage",
            countQuery =
                    "select " +
                            "count(c.id) " +
                            "from Country c " +
                            "where (c.region.name in :regions or :regions is null) " +
                            "and (lower(c.name) like lower(concat('%', :search, '%')) " +
                            "or lower(c.region.name) like lower(concat('%', :search, '%')))")
    Page<CountryTotalOfEvaluatorsProj> findAllIncludeEvaluatorsCount(String search, List<String> regions, Pageable pageable);

    int countByRegionId(int id);

    default Page<CountryTotalOfEvaluatorsProj> findAll(SearchCountriesRequest request) {
        return findAllIncludeEvaluatorsCount(request.getSearch(), request.getRegions(), getPageable(request));
    }

    private Pageable getPageable(SearchCountriesRequest request) {
        if (request.getSort().equals("availability_percentage")) {
            request.setSort("c.countryEvaluationDetails.availabilityPercentage");
        }

        if (request.getSort().equals("price_per_evaluation")) {
            request.setSort("c.countryEvaluationDetails.pricePerEvaluation");
        }

        if (request.getSort().equals("region")) {
            request.setSort("c.region.name");
        }

        if (request.getSort().equals("name")) {
            request.setSort("c.name");
        }

        if (request.getSort().equals("id")) {
            request.setSort("c.id");
        }

        var sort = request.getOrder().equals("asc") ?
                Sort.by(request.getSort()).ascending()
                : Sort.by(request.getSort()).descending();

        return request.isPaginated() ?
                PageRequest.of(request.getPage(), request.getSize(), sort)
                : PageRequest.of(0, Integer.MAX_VALUE, sort);
    }

}

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
            "SELECT new com.bloxico.ase.userservice.projection.CountryTotalOfEvaluatorsProj( " +
            "c.id, c.name, r.name, ced.pricePerEvaluation, ced.availabilityPercentage, COUNT(e.id) AS total_of_evaluators) " +
            "FROM Evaluator e " +
            "RIGHT JOIN e.userProfile.location.country c " +
            "RIGHT JOIN c.region r " +
            "RIGHT JOIN c.countryEvaluationDetails ced " +
            "WHERE (r.name IN :regions OR :regions IS NULL) " +
            "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "GROUP BY c.id, r.id, ced.id",
            countQuery =
            "SELECT COUNT(c.id) FROM Country c " +
            "RIGHT JOIN c.region r " +
            "WHERE (r.name IN :regions OR :regions IS NULL) " +
            "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CountryTotalOfEvaluatorsProj> findAllIncludeEvaluatorsCount(String search, List<String> regions, Pageable pageable);

    int countByRegionId(int id);

    default Page<CountryTotalOfEvaluatorsProj> findAll(SearchCountriesRequest request) {
        return findAllIncludeEvaluatorsCount(request.getSearch(), request.getRegions(), getPageable(request));
    }

    private Pageable getPageable(SearchCountriesRequest request) {
        var sortProperty = request.getSort();

        if (sortProperty.equals("availability_percentage")) {
            sortProperty = "ced.availabilityPercentage";
        }

        if (sortProperty.equals("price_per_evaluation")) {
            sortProperty = "ced.pricePerEvaluation";
        }

        if (sortProperty.equals("region")) {
            sortProperty = "r.name";
        }

        if (sortProperty.equals("name")) {
            sortProperty = "c.name";
        }

        if (sortProperty.equals("id")) {
            sortProperty = "c.id";
        }

        var sort = request.getOrder().equals("asc") ?
                Sort.by(sortProperty).ascending() :
                Sort.by(sortProperty).descending();

        return request.isPaginated() ?
                PageRequest.of(request.getPage(), request.getSize(), sort) :
                PageRequest.of(0, Integer.MAX_VALUE, sort);
    }

}

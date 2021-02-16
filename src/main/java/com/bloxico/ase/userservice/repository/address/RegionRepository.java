package com.bloxico.ase.userservice.repository.address;

import com.bloxico.ase.userservice.entity.address.Region;
import com.bloxico.ase.userservice.projection.RegionDetailsProj;
import com.bloxico.ase.userservice.web.model.address.SearchRegionsRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Integer> {

    Optional<Region> findByNameIgnoreCase(String name);

    @Query(
            value =
            "SELECT new com.bloxico.ase.userservice.projection.RegionDetailsProj( " +
            "r.id, r.name, COUNT(c.id) AS number_of_countries, COUNT(e.id) AS number_of_evaluators) " +
            "FROM Evaluator e " +
            "RIGHT JOIN e.userProfile.location.country c " +
            "RIGHT JOIN c.region r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "GROUP BY r.id",
            countQuery =
            "SELECT COUNT(r.id) FROM Region r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<RegionDetailsProj> findAllIncludeCountriesAndEvaluatorsCount(String search, Pageable pageable);

    default Page<RegionDetailsProj> findAll(SearchRegionsRequest request) {
        return findAllIncludeCountriesAndEvaluatorsCount(request.getSearch(), getPageable(request));
    }

    private Pageable getPageable(SearchRegionsRequest request) {
        var sortProperty = request.getSort();

        if (sortProperty.equals("name")) {
            sortProperty = "r.name";
        }

        if (sortProperty.equals("id")) {
            sortProperty = "r.id";
        }

        var sort = request.getOrder().equals("asc") ?
                Sort.by(sortProperty).ascending() :
                Sort.by(sortProperty).descending();

        return request.isPaginated() ?
                PageRequest.of(request.getPage(), request.getSize(), sort) :
                PageRequest.of(0, Integer.MAX_VALUE, sort);
    }

}

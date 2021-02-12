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
                    "select " +
                            "new com.bloxico.ase.userservice.projection.RegionDetailsProj " +
                            "(r.id, r.name, count(c.id) as number_of_countries, count(e.id) as number_of_evaluators) " +
                            "from Evaluator e " +
                            "right join e.userProfile.location.country c " +
                            "right join c.region r " +
                            "where lower(r.name) like lower(concat('%', :search, '%')) " +
                            "group by r.id",
            countQuery =
                    "select " +
                            "count(r.id) " +
                            "from Region r " +
                            "where lower(r.name) like lower(concat('%', :search, '%'))")
    Page<RegionDetailsProj> findAllIncludeCountriesAndEvaluatorsCount(String search, Pageable pageable);

    default Page<RegionDetailsProj> findAll(SearchRegionsRequest request) {
        return findAllIncludeCountriesAndEvaluatorsCount(request.getSearch(), getPageable(request));
    }

    private Pageable getPageable(SearchRegionsRequest request) {
        if (request.getSort().equals("name")) {
            request.setSort("r.name");
        }

        if (request.getSort().equals("id")) {
            request.setSort("r.id");
        }

        var sort = request.getOrder().equals("asc") ?
                Sort.by(request.getSort()).ascending()
                : Sort.by(request.getSort()).descending();

        return request.isPaginated() ?
                PageRequest.of(request.getPage(), request.getSize(), sort)
                : PageRequest.of(0, Integer.MAX_VALUE, sort);
    }

}

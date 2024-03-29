package com.icebear.stay.repository;


import com.icebear.stay.model.Location;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends ElasticsearchRepository<Location, Long>, CustomLocationRepository {
// implementation automatically generated by our framework, including ElasticsearchRepository and CustomLocationRepository
}

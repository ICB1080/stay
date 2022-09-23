package com.icebear.stay.repository;

import com.icebear.stay.model.Location;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class CustomLocationRepositoryImpl implements CustomLocationRepository {
    private final String DEFAULT_DISTANCE = "50";
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public CustomLocationRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    // search stays' id whose distance from (lat, lon) <= DEFAULT_DISTANCE
    public List<Long> searchByDistance(double lat, double lon, String distance) {
        if (distance == null || distance.isEmpty()) {
            distance = DEFAULT_DISTANCE;
        }
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withFilter(new GeoDistanceQueryBuilder("geoPoint").point(lat, lon).distance(distance, DistanceUnit.KILOMETERS));

        // find all locations
        SearchHits<Location> searchResult = elasticsearchOperations.search(queryBuilder.build(), Location.class);
        List<Long> locationIDs = new ArrayList<>();
        for (SearchHit<Location> hit : searchResult.getSearchHits()) {
            // get IDs of these locations
            locationIDs.add(hit.getContent().getId());
        }
        return locationIDs;
    }
}
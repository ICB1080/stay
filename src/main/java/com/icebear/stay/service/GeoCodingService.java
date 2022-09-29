package com.icebear.stay.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.icebear.stay.exception.GeoCodingException;
import com.icebear.stay.exception.InvalidStayAddressException;
import com.icebear.stay.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeoCodingService {
    private GeoApiContext context;

    @Autowired
    public GeoCodingService(GeoApiContext context) {
        this.context = context;
    }

    public GeocodingResult getLatLng(String address) throws GeoCodingException {
        try {
            GeocodingResult result = GeocodingApi.geocode(context, address).await()[0];
            if (result.partialMatch){
                throw new InvalidStayAddressException("The address is invalid");
            }
            return result;
        }catch(IOException | ApiException | InterruptedException e){
            e.printStackTrace();
            throw new GeoCodingException("Failed to encode this address");
        }
    }


    // convert address to (lat, lon) and return a Location object
//    public Location getLocation(Long id, String address) throws GeoCodingException {
//        try {
//            GeocodingResult result = GeocodingApi.geocode(context, address).await()[0];
//            if (result.partialMatch) {
//                throw new InvalidStayAddressException("Failed to find stay address");
//            }
//            return new Location(id,
//                    new GeoPoint(result.geometry.location.lat, result.geometry.location.lng));
//        } catch (IOException | ApiException | InterruptedException e) {
//            e.printStackTrace();
//            throw new GeoCodingException("Failed to encode stay address");
//        }
//    }
}

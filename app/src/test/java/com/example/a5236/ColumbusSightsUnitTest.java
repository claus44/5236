package com.example.a5236;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ColumbusSightsUnitTest {

    //Both latitude and longitude are different between the coordinates
    @Test
    public void landmarkFragment_distance_latitudeAndLongitude_test() {
        LandmarkFragment testFragment = LandmarkFragment.newInstance();
        String coordinatesOne = "-83.015969,39.999121";
        double latCoordinatesTwo = 37.4219983;
        double longCoordinatesTwo = -122.084;

        double distance = testFragment.distance(coordinatesOne, latCoordinatesTwo, longCoordinatesTwo);

        /*
         * assert that the method is within 5 feet of the true distance
         * Expected values calculated using https://keisan.casio.com/exec/system/1224587128 and
         * then converting with the same precision as the method to feet
         */
        assertEquals(11072713.38, distance, 5);
    }

    //Only the latitude is different between the coordinates
    @Test
    public void landmarkFragment_distance_latitude_test() {
        LandmarkFragment testFragment = LandmarkFragment.newInstance();
        String coordinatesOne = "-83.015969,39.999121";
        double latCoordinatesTwo = 37.4219983;
        double longCoordinatesTwo = -83.015969;

        double distance = testFragment.distance(coordinatesOne, latCoordinatesTwo, longCoordinatesTwo);

        //assert that the method is within 5 feet of the true distance
        assertEquals(940213.10, distance, 5);
    }

    //Only the longitude is different between the coordinates
    @Test
    public void landmarkFragment_distance_longitude_test() {
        LandmarkFragment testFragment = LandmarkFragment.newInstance();
        String coordinatesOne = "-122.084,37.4219983";
        double latCoordinatesTwo = 37.4219983;
        double longCoordinatesTwo = -83.015969;

        double distance = testFragment.distance(coordinatesOne, latCoordinatesTwo, longCoordinatesTwo);

        //assert that the method is within 5 feet of the true distance
        assertEquals(11236411.19, distance, 5);
    }
}
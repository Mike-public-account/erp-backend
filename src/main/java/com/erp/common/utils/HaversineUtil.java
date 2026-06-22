package com.erp.common.utils;

/**
 * Haversine公式 计算两点球面距离km
 */
public class HaversineUtil {
    // 地球半径 km
    private static final double R = 6371.0;

    public static double calcDistanceKm(double lat1, double lng1, double lat2, double lng2) {
        double latRad1 = Math.toRadians(lat1);
        double lngRad1 = Math.toRadians(lng1);
        double latRad2 = Math.toRadians(lat2);
        double lngRad2 = Math.toRadians(lng2);

        double dLat = latRad2 - latRad1;
        double dLng = lngRad2 - lngRad1;

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(latRad1) * Math.cos(latRad2)
                * Math.pow(Math.sin(dLng / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
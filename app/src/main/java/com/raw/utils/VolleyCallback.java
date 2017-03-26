package com.raw.utils;

import java.util.List;

/**
 * Created by KorPai on 21/3/2560.
 */

public interface VolleyCallback {

    void onSuccessResponse(String result, List<Double> Lat, List<Double> Long,List<String> place,List<String> id_place);
}

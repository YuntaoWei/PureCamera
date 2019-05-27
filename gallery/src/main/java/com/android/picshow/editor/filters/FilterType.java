package com.android.picshow.editor.filters;

public class FilterType {

    public static final int BeitaOfWhiteLOG = 1313;

    public static final int FILTER4GRAY = 1314;

    public static final int FILTER4MOSATIC = 1315;

    public static final int FILTER4NOSTALGIC = 1316;

    public static final int FILTER4LOMO = 1317;

    public static final int FILTER4COMICS = 1318;

    public static final int FILTER4BlackWhite = 1319;

    public static final int FILTER4NEGATIVE = 1320;

    public static final int FILTER4BROWN = 1321;

    public static final int FILTER4SKETCH_PENCIL = 1322;

    public static final int FILTER4COLORSKETCH = 1324;

    public static final int FILTER4OVEREXPOSURE = 1323;

    public static final int FILTER4WHITELOG = 1325;

    public static final int FILTER4CONTRAST_LIGHT = 1327;

    public static final int FILTER4SOFTNESS = 1328;

    public static final int FILTER4NiHong = 1329;

    public static final int FILTER4SKETCH = 1330;

    public static final int FILTER4CARVING = 1331;

    public static final int FILTER4RELIEF = 1332;

    public static final int FILTER4RUIHUA = 1333;

    private static final int[] FILTERS = {
            FilterType.FILTER4BROWN,
            FilterType.FILTER4NOSTALGIC,
            FilterType.FILTER4COMICS,
            FilterType.FILTER4GRAY,
            FilterType.FILTER4LOMO,
            FilterType.FILTER4MOSATIC,
            FilterType.FILTER4SKETCH_PENCIL,
            FilterType.FILTER4NiHong
    };

    public static int[] getAllAvailableFilters() {
        return FILTERS;
    }

}

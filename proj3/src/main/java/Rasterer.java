import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    private static double rootUpperLat, rootLowerLat, rootLeftLon, rootRightLon;
    private double upperLat, lowerLat, leftLon, rightLon;
    private static double rootLonDPP, maxDepth;

    public Rasterer() {
        // YOUR CODE HERE
        rootUpperLat = MapServer.ROOT_ULLAT;
        rootLowerLat = MapServer.ROOT_LRLAT;
        rootLeftLon = MapServer.ROOT_ULLON;
        rootRightLon = MapServer.ROOT_LRLON;

        rootLonDPP = (rootRightLon - rootLeftLon) / MapServer.TILE_SIZE;
        maxDepth = 7;
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     *     <li>The tiles collected must cover the most longitudinal distance per pixel
     *     (LonDPP) possible, while still covering less than or equal to the amount of
     *     longitudinal distance per pixel in the query box for the user viewport size. </li>
     *     <li>Contains all tiles that intersect the query bounding box that fulfill the
     *     above condition.</li>
     *     <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     * forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        /*System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
                           + "your browser.");*/

        initialize(params, results);

        boolean querySuccess = basicCheck();
        if (!querySuccess) {
            return results;
        } else {
            results.put("query_success", true);
        }

        computeDepth(params, results);
        amend(results);
        computeGridAndSoOn(results);

        // System.out.println(results);
        return results;
    }

    private void initialize(Map<String, Double> params, Map<String, Object> results) {
        upperLat = params.get("ullat");
        lowerLat = params.get("lrlat");
        leftLon = params.get("ullon");
        rightLon = params.get("lrlon");

        String[] strs = {"render_grid", "raster_ul_lon", "raster_ul_lat",
            "raster_lr_lon", "raster_lr_lat", "depth"};
        for (String str : strs) {
            results.put(str, null);
        }
        results.put("query_success", false);
    }

    private boolean basicCheck() {
        return (upperLat > lowerLat) && (leftLon < rightLon)
                && (lowerLat < rootUpperLat)
                && (upperLat > rootLowerLat)
                && (rightLon > rootLeftLon)
                && (leftLon < rootRightLon);
    }

    private void amend(Map<String, Object> results) {
        if (upperLat > rootUpperLat) {
            upperLat = rootUpperLat;
        }
        if (lowerLat < rootLowerLat) {
            lowerLat = rootLowerLat;
        }
        if (leftLon < rootLeftLon) {
            leftLon = rootLeftLon;
        }
        if (rightLon > rootRightLon) {
            rightLon = rootRightLon;
        }
    }

    private void computeDepth(Map<String, Double> params, Map<String, Object> results) {
        double goalLonDPP = (rightLon - leftLon) / params.get("w");
        double lonDPP = rootLonDPP;
        int depth = 0;
        for (; lonDPP > goalLonDPP && depth < maxDepth; depth++) {
            lonDPP /= 2;
        }

        results.put("depth", depth);
    }

    private void computeGridAndSoOn(Map<String, Object> results) {
        int depth = (int) results.get("depth");

        /* compute d: denominator */
        double rootWidth = rootRightLon - rootLeftLon, rootHeight = rootUpperLat - rootLowerLat;

        /* compute n: numerator */
        double leftWidth = leftLon - rootLeftLon, rightWidth = rightLon - rootLeftLon,
                upperHeight = rootUpperLat - upperLat, lowerHeight = rootUpperLat - lowerLat;

        /* compute ratio of n to d, and multiply by the length of the edge in tiles,
           then convert it to integer. */
        double total = Math.pow(2, depth); // the length of the edge in tiles
        int left = (int) Math.floor((leftWidth / rootWidth) * total),
                right = (int) Math.ceil((rightWidth / rootWidth) * total) - 1, // index minus 1
                upper = (int) Math.floor((upperHeight / rootHeight) * total),
                lower = (int) Math.ceil((lowerHeight / rootHeight) * total) - 1;

        /* compute lon and lat related to tiles */
        results.put("raster_ul_lon", (double) left / total * rootWidth + rootLeftLon);
        results.put("raster_ul_lat", rootUpperLat - (double) upper / total * rootHeight);
        results.put("raster_lr_lon", (double) (right + 1) / total * rootWidth + rootLeftLon);
        results.put("raster_lr_lat", rootUpperLat - (double) (lower + 1) / total * rootHeight);

        /* compute grid */
        String[][] grid = new String[lower - upper + 1][right - left + 1];
        String prefix = "d" + depth + "_x",
                middle = "_y",
                suffix = ".png";
        for (int i = upper; i <= lower; i++) {
            for (int j = left; j <= right; j++) {
                grid[i - upper][j - left] = prefix + j + middle + i + suffix;
            }
        }

        results.put("render_grid", grid);
    }
}

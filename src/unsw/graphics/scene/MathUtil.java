package unsw.graphics.scene;

/**
 * A collection of useful math methods 
 *
 * @author malcolmr
 */
public class MathUtil {

    /**
     * Normalise an angle to the range [-180, 180)
     * 
     * @param angle 
     * @return
     */
    public static float normaliseAngle(float angle) {
        return ((angle + 180f) % 360f + 360f) % 360f - 180f;
    }

    /**
     * Clamp a value to the given range
     * 
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Calculate the cross product between two vectors u and v.
     * Source: COMP3421 lecture example code
     *
     * @param u input u vector
     * @param v input v vector
     * @return Cross product result
     */
    public static float [] crossProduct(float u[], float v[]){
        float crossProduct[] = new float[3];
        crossProduct[0] = u[1]*v[2] - u[2]*v[1];
        crossProduct[1] = u[2]*v[0] - u[0]*v[2];
        crossProduct[2] = u[0]*v[1] - u[1]*v[0];

        return crossProduct;
    }

    /**
     * Find the unit vector of given vector by dividing by the magnitude.
     * Source: http://gamedev.stackexchange.com/a/76082
     *
     * @param u input vector to be converted
     * @return input vector as a unit vector
     */
    public static float [] getUnitVector(float[] u) {
        float unitVector[] = new float[4];

        float magnitude = (float) Math.sqrt(u[0] * u[0] + u[1] * u[1] + u[2] * u[2]);
        unitVector[0] = u[0] / magnitude;
        unitVector[1] = u[1] / magnitude;
        unitVector[2] = u[2] / magnitude;
        unitVector[3] = 1;

        return unitVector;
    }

    /**
     * A 3D scale matrix that scales both axes by the same factor
     *
     * @param scale scale factor
     * @return 4x4 array of doubles representing 3D scale matrix
     */
    public static float[][] scaleMatrix(float scale) {
        float[][] m = {
                {scale, 0, 0, 0},
                { 0, scale, 0, 0},
                { 0, 0, scale, 0},
                { 0, 0, 0, 1}
        };

        return m;
    }

    /**
     * Multiply a vector by a matrix (for 3D)
     *
     * @param m A 4x4 matrix
     * @param v A 4x1 vector
     * @return resulting multiplied vector
     */
    public static float[] multiply(float[][] m, float[] v) {

        float[] u = new float[4];

        for (int i = 0; i < 4; i++) {
            u[i] = 0;
            for (int j = 0; j < 4; j++) {
                u[i] += m[i][j] * v[j];
            }
        }

        return u;
    }
}

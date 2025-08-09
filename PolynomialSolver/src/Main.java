import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import java.math.BigInteger;

public class Main {

    public static void main(String[] args) throws Exception {
        // Path to JSON file
        String jsonPath = "C:\\Users\\lokes\\OneDrive\\Documents\\Java\\PolynomialSolver\\data\\test_case.json"; // adjust path if needed
        String jsonString = new String(Files.readAllBytes(Paths.get(jsonPath)));

        // Parse JSON
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject keys = jsonObject.getAsJsonObject("keys");
        int n = keys.get("n").getAsInt();
        int k = keys.get("k").getAsInt();

        // Store decoded roots
        double[][] roots = new double[k][k + 1];
        int count = 0;

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (entry.getKey().equals("keys")) continue;
            int x = Integer.parseInt(entry.getKey());
            JsonObject valObj = entry.getValue().getAsJsonObject();
            int base = Integer.parseInt(valObj.get("base").getAsString());
            String value = valObj.get("value").getAsString();

            // Decode y
           BigInteger bigY = new BigInteger(value, base);
double y = bigY.doubleValue();

            // Fill augmented matrix row
            if (count < k) {
                for (int j = 0; j < k; j++) {
                    roots[count][j] = Math.pow(x, k - j - 1);
                }
                roots[count][k] = y;
                count++;
            }
        }

        // Solve using Gaussian elimination
        double[] coefficients = gaussianElimination(roots);

        // Print constant term c
        System.out.println("Constant c = " + coefficients[coefficients.length - 1]);
    }

    // Gaussian elimination solver
    public static double[] gaussianElimination(double[][] matrix) {
        int n = matrix.length;

        for (int i = 0; i < n; i++) {
            // Pivot
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(matrix[j][i]) > Math.abs(matrix[max][i])) {
                    max = j;
                }
            }
            double[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;

            // Normalize pivot row
            double pivot = matrix[i][i];
            for (int j = i; j <= n; j++) {
                matrix[i][j] /= pivot;
            }

            // Eliminate below
            for (int j = i + 1; j < n; j++) {
                double factor = matrix[j][i];
                for (int k = i; k <= n; k++) {
                    matrix[j][k] -= factor * matrix[i][k];
                }
            }
        }

        // Back substitution
        double[] solution = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            solution[i] = matrix[i][n];
            for (int j = i + 1; j < n; j++) {
                solution[i] -= matrix[i][j] * solution[j];
            }
        }

        return solution;
    }
}

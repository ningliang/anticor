package util;

public class MatrixUtils {
	public static double[][] transpose(double[][] matrix) {
		double[][] retval = new double[matrix[0].length][matrix.length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				retval[j][i] = matrix[i][j];
			}
		}
		return retval;
	}
	
	public static double[][] product(double[][] first_matrix, double[][] second_matrix) {
		if (first_matrix[0].length != second_matrix.length) {
			return null;
		} else {
			int num_rows = first_matrix.length;
			int num_cols = second_matrix[0].length;
			int intermed = second_matrix.length;
			double[][] retval = new double[num_rows][num_cols];
			for (int i = 0; i < num_rows; i++) {
				for (int j = 0; j < num_cols; j++) {
					double sum = 0.0;
					for (int k = 0; k < intermed; k++) {
						sum += first_matrix[i][k] * second_matrix[k][j];
					}
					retval[i][j] = sum;
				}
			}
			return retval;
		}
	}
	
	public static double[] columnMeans(double[][] matrix) {
		int dim = matrix[0].length;
		double[] retval = new double[dim];
		for (int j = 0; j < dim; j++) {
			double sum = 0.0;
			for (int i = 0; i < matrix.length; i++) {
				sum += matrix[i][j];
			}
			retval[j] = sum / (double)matrix.length;
		}
		return retval;
	}
	
	public static double[] columnStdDevs(double[][] matrix, double[] means) {
		int dim = matrix[0].length;
		double[] retval = new double[dim];
		for (int j = 0; j < dim; j++) {
			double sum = 0.0;
			for (int i = 0; i < matrix.length; i++) {
				double diff = matrix[i][j] - means[i];
				sum += diff * diff;
			}
			sum /= (double)matrix.length;
			retval[j] = Math.sqrt(sum / (double) matrix.length);
		}
		return retval;		
	}
	
	public static double innerProduct(double[] first, double[] second) {
		double sum = 0.0;
		int dim = first.length;
		for (int i = 0; i < dim; i++) {
			sum += first[i] * second[i];
		}
		return sum;
	}
	
	public static double[] hadamardProduct(double[] first, double[] second) {
		double[] result = new double[first.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = first[i] * second[i];
		}
		return result;
	}
	
	public static void divideAll(double[] arr, double factor) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] /= factor;
		}
	}
	
	public static boolean equals(double[][] first, double[][] second) {
		int num_rows = first.length;
		int num_cols = first[0].length;
		for (int i = 0; i < num_rows; i++) {
			for (int j = 0; j < num_cols; j++) {
				if (first[i][j] != second[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
}

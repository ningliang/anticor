package anticor;

import util.MatrixUtils;

public class AntiCor {
	private DataSource data_source;
	
	public AntiCor(DataSource data_source) {
		this.data_source = data_source;
	}
	
	public DataSource getDataSource() {
		return data_source;
	}
	
	public double[] nextPortfolio(int date_index, int window_size, double[] portfolio) {
		int start_index = date_index - (2 * window_size) + 1;
		if (start_index < 0) {
			return portfolio;
		}

		// Fill the two windows with log relative prices
		// System.out.println("Filling windows");
		double[][] first_window = new double[window_size][];
		double[][] second_window = new double[window_size][];
		
		for (int i = start_index; i < start_index + window_size; i++) {
			int index = i - start_index;
			first_window[index] = data_source.getLogRelativePricesForDateIndex(i);
		}
		for (int i = start_index + window_size; i < date_index + 1; i++) {
			int index = i - (start_index + window_size);
			second_window[index] = data_source.getLogRelativePricesForDateIndex(i);
		}
		
		// Calculate the means and standard deviations of the columns
		// System.out.println("Calculating means and std_devs");		
		double[] first_means = MatrixUtils.columnMeans(first_window);
		double[] second_means = MatrixUtils.columnMeans(second_window);
		double[] first_std_devs = MatrixUtils.columnStdDevs(first_window, first_means);
		double[] second_std_devs = MatrixUtils.columnStdDevs(second_window, second_means);
		
		// Calculate correlation matrix
		// System.out.println("Calculating corr_matrix");
		double[][] corr_matr = calculateCorrelations(first_window, first_means, first_std_devs, second_window, second_means, second_std_devs);
		
		// Calculate claims and transfer terms
		// System.out.println("Calculating claims");
		double[][] claims = calculateClaims(corr_matr, second_means);
		
		// Calculate new portfolio
		// System.out.println("Calculating portfolio");
		return calculateNewPortfolio(claims, portfolio);
	}

	private double[][] calculateCorrelations(double[][] first_window, double[] first_means, double[] first_std_devs, double[][] second_window, double[] second_means, double[] second_std_devs) {
		double[][] first_normed = calculateNormedMatrix(first_window, first_means);
		double[][] second_normed = calculateNormedMatrix(second_window, second_means);
		double[][] product = MatrixUtils.product(MatrixUtils.transpose(first_normed), second_normed);
		for (int i = 0; i < product.length; i++) {
			for (int j = 0; j < product[i].length; j++) {
				product[i][j] /= 1.0 / ((double) first_window.length - 1);
				if (first_std_devs[i] > 0 && second_std_devs[j] > 0) {
					product[i][j] /= (first_std_devs[i] * second_std_devs[j]);
				} else {
					product[i][j] = 0.0;
				}
			}
		}
		return product;
	}
	
	private double[][] calculateNormedMatrix(double[][] window, double[] means) {
		double[][] retval = new double[window.length][means.length];
		for (int i = 0; i < window.length; i++) {
			for (int j = 0; j < means.length; j++) {
				retval[i][j] = window[i][j] - means[j];
			}
		}
		return retval;
	}
	
	private double[][] calculateClaims(double[][] corr_matr, double[] previous_means) {
		int dim = corr_matr.length;
		double[][] claims = new double[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				double sum = 0.0;
				if (previous_means[i] >= previous_means[j] && corr_matr[i][j] > 0) {
					sum += corr_matr[i][j];
					if (corr_matr[i][i] < 0) {
						sum -= corr_matr[i][i];
					}
					if (corr_matr[j][j] < 0) {
						sum -= corr_matr[j][j];
					}
				}
				claims[i][j] = sum;
			}
		}
		
		return claims;
	}
	
	private double[] calculateNewPortfolio(double[][] claims, double[] portfolio) {
		int dim = portfolio.length;
		double[] new_portfolio = new double[dim];
		double[][] transfer = new double[dim][dim];
		
		// Calculate claim sums per row
		// System.out.println("Calculate claim sums");
		double[] claim_sums = new double[claims.length];
		for (int i = 0; i < claims.length; i++) {
			double claim_sum = 0.0;
			for (int j = 0; j < claims[i].length; j++) {
				claim_sum += claims[i][j];
			}
			claim_sums[i] = claim_sum;
		}
		
		// Calculate the transfer matrix
		// System.out.println("Calculate transfer matrix");
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				// TODO is this correct?
				if (claim_sums[i] > 0) {
					transfer[i][j] = portfolio[i] * claims[i][j] / claim_sums[i];
				} else {
					transfer[i][j] = 0.0;
				}
			}
		}
		
		// Calculate the new values
		// System.out.println("Calculate new portfolio");
		for (int i = 0; i < dim; i++) {
			double transfer_sum = 0.0;
			for (int j = 0; j < dim; j++) {
				if (j != i) {
					transfer_sum += transfer[j][i] - transfer[i][j];
				}
			}
			new_portfolio[i] = portfolio[i] + transfer_sum;
		}
		
		return new_portfolio;
	}
		
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println("<directory> <window_size>");
			System.exit(1);
		}
		
		String dir = args[0];
		int window_size = Integer.parseInt(args[1]);
		
		System.out.println("Initializing data source");
		DataSource data_source = new DataSource(dir);
		int num_symbols = data_source.getSymbolCount();
		
		System.out.println("Set initial portfolio");
		double[] current_portfolio = new double[num_symbols];
		for (int i = 0; i < num_symbols; i++) {
			current_portfolio[i] = 1.0 / (double)num_symbols;
		}
		
		System.out.println("Calculating portfolios");
		AntiCor anti_cor = new AntiCor(data_source);
		double total_gain = 1.0;
		for (int i = 0; i < data_source.getDateCount(); i++) {
			// We have the portfolio from end of day the previous day (normalized)
			// Find the portfolio we should rebalance to for *today*
			current_portfolio = anti_cor.nextPortfolio(i, window_size, current_portfolio);
			
			// Find the gain and effect from the market, and normalize for plugging into tomorrow
			double gain = MatrixUtils.innerProduct(current_portfolio, data_source.getRelativePricesForDateIndex(i));
			current_portfolio = MatrixUtils.hadamardProduct(current_portfolio, data_source.getRelativePricesForDateIndex(i));
			total_gain *= gain;
			MatrixUtils.divideAll(current_portfolio, gain);	
			
			System.out.println(i + " " + data_source.getDateForIndex(i) + ":\t" + gain + "\t" + total_gain);
		}
	}
}

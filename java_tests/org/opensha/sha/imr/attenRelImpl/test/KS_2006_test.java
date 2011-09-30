package org.opensha.sha.imr.attenRelImpl.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensha.commons.param.event.ParameterChangeWarningEvent;
import org.opensha.commons.param.event.ParameterChangeWarningListener;
import org.opensha.sha.imr.attenRelImpl.KS_2006_AttenRel;

/**
 * Class providing methods for testing {@link KS_2006_AttenRel}. Tables
 * created from Excel spreadsheet provided by Jonathan P. Stewart.
 */
public class KS_2006_test implements ParameterChangeWarningListener {

	/** Kempton & Stewart (2006) GMPE (attenuation relationship) */
	private KS_2006_AttenRel KS2006AttenRel = null;

	/**
	 * Table for median ground motion validation. M5 to M8 at R=0 to 50km. Vs30=300m/s
	 */
	private static final String MEDIAN_M5_M8_R0_50_TABLE_VS300 = "kemptonstewart2006_M5-M8-R0-50_vs300.out";

	/**
	 * Table for median ground motion validation. M5 to M8 at R=0 to 50km. Vs30=600m/s
	 */
	private static final String MEDIAN_M5_M8_R0_50_TABLE_VS600 = "kemptonstewart2006_M5-M8-R0-50_vs600.out";

	/** Header for median tables. */
	private static String[] TABLE_HEADER_MEDIAN = new String[1];

	/** Number of columns in test tables for median ground motion value. */
	private static final int TABLE_NUM_COL_MEDIAN = 4;

	/** Number of rows in first test table. */
	private static final int TABLE_NUM_ROWS = 6;

	/** Median ground motion validation table. M5 to M8 at R=0 to 50km. Vs30=300m/s */
	private static double[][] medianTable_vs300 = null;

	/** Median ground motion validation table. M5 to M8 at R=0 to 50km. Vs30=600m/s */
	private static double[][] medianTable_vs600 = null;

	private static final double TOLERANCE = 1e-3;

	/**
	 * Set up attenuation relationship object, and tables for tests.
	 *
	 * @throws Exception
	 */
	@Before
	public final void setUp() throws Exception {
		KS2006AttenRel = new KS_2006_AttenRel(this);
		KS2006AttenRel.setParamDefaults();

		medianTable_vs300 = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_M5_M8_R0_50_TABLE_VS300).toURI()),
				medianTable_vs300, TABLE_HEADER_MEDIAN);

		medianTable_vs600 = new double[TABLE_NUM_ROWS][TABLE_NUM_COL_MEDIAN];
		AttenRelTestHelper.readNumericTableWithHeader(new File(ClassLoader
				.getSystemResource(MEDIAN_M5_M8_R0_50_TABLE_VS600).toURI()),
				medianTable_vs600, TABLE_HEADER_MEDIAN);

	}

	/**
	 * Clean up.
	 */
	@After
	public final void tearDown() {
		KS2006AttenRel = null;
		medianTable_vs300 = null;
		medianTable_vs600 = null;
	}

	@Test
	public void medianTable_vs300() {
		double vs30 = 300.0;
		validateMedian(vs30, medianTable_vs300);
	}

	@Test
	public void medianTable_vs600() {
		double vs30 = 600.0;
		validateMedian(vs30, medianTable_vs600);
	}

	private void validateMedian(double vs30, double[][] table) {
		// check for relative significant duration
		double[] rrups={0,3,10,20,30,50};
		for (int j = 0; j < table.length; j++) {
			double rrup = rrups[j];
			double expectedMedian_M5 = table[j][0];
			double computedMedian_M5 = Math.exp(KS2006AttenRel.getMean(5.0,rrup, vs30));
			double expectedMedian_M6 = table[j][1];
			double computedMedian_M6 = Math.exp(KS2006AttenRel.getMean(6.0,rrup, vs30));
			double expectedMedian_M7 = table[j][2];
			double computedMedian_M7 = Math.exp(KS2006AttenRel.getMean(7.0,rrup, vs30));
			double expectedMedian_M8 = table[j][3];
			double computedMedian_M8 = Math.exp(KS2006AttenRel.getMean(8.0,rrup, vs30));
			assertEquals(expectedMedian_M5, computedMedian_M5, TOLERANCE);
			assertEquals(expectedMedian_M6, computedMedian_M6, TOLERANCE);
			assertEquals(expectedMedian_M7, computedMedian_M7, TOLERANCE);
			assertEquals(expectedMedian_M8, computedMedian_M8, TOLERANCE);
		}
	}

	@Override
	public void parameterChangeWarning(ParameterChangeWarningEvent event) {
	}
}

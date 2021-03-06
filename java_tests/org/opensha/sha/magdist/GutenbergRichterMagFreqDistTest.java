package org.opensha.sha.magdist;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opensha.sha.magdist.GutenbergRichterMagFreqDist;


public class GutenbergRichterMagFreqDistTest {

    @Test
    public void checkIncrementMagUpper() {
    	double TCR = 123;
    	double bValue = 2;
    	double min = 10.3;
    	double max = 15.7;
    	double delta = 0.6;
    	int numValues = 10;
    	double TMR, a;

    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(bValue, TCR, min, max, numValues);
    	assertEquals(max, mfd.getMagUpper(), 1e-5);
    	assertEquals(min, mfd.getMagLower(), 1e-5);
    	assertEquals(numValues, mfd.getNum());
    	assertEquals(delta, mfd.getDelta(), 1e-6);
    	assertEquals(TCR, mfd.getTotCumRate(), 1e-5);
    	TMR = mfd.getTotalMomentRate();
    	a = mfd.get_aValue();

    	mfd.incrementMagUpper(2.3);
    	
    	assertEquals(18.1, mfd.getMagUpper(), 1e-5);
    	assertEquals(min, mfd.getMagLower(), 1e-5);
    	assertEquals(numValues + 4, mfd.getNum());
    	assertEquals(delta, mfd.getDelta(), 1e-6);
    	
    	// b and TMR should not change
    	assertEquals(2, mfd.get_bValue(), 1e-5);
    	assertEquals(TMR, mfd.getTotalMomentRate(), 1e15);

    	// bringing mMax to the same value of 20
    	mfd.incrementMagUpper(-2.65);
    	
    	assertEquals(max, mfd.getMagUpper(), 1e-5);
    	assertEquals(min, mfd.getMagLower(), 1e-5);
    	assertEquals(numValues, mfd.getNum());
    	assertEquals(delta, mfd.getDelta(), 1e-6);
    	assertEquals(2, mfd.get_bValue(), 1e-5);
    	assertEquals(TMR, mfd.getTotalMomentRate(), 1e15);
    	assertEquals(TCR, mfd.getTotCumRate(), 1e-5);
    	assertEquals(a, mfd.get_aValue(), 0);
    }
    
    @Test
    public void checkSetMagUpper() {
    	double TCR = 3;
    	double b = 4;
    	double min = 2 + 0.0125;
    	double max = 4 - 0.0125;
    	int numValues = 80;
    	double TMR, a;
    	double delta = 0.025;

    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(b, TCR, min, max, numValues);
    	assertEquals(delta, mfd.getDelta(), 1e-6);
    	assertEquals(numValues, mfd.getNum());
    	TMR = mfd.getTotalMomentRate();
    	a = mfd.get_aValue();

    	mfd.setMagUpper(3.7);
    	assertEquals(min, mfd.getMagLower(), 0);
    	assertEquals(delta, mfd.getDelta(), 1e-6);
    	assertEquals(3.6875, mfd.getMagUpper(), 0);
    	assertEquals(68, mfd.getNum());

    	// checks that rates in the mfd object are consistent with the
    	// original data
    	for (int i = 0; i < mfd.getNum(); i++) {
    		double computedOccRate = mfd.getY(i);
    		double expectedOccRate = Math.pow(10, a - b * (mfd.getX(i) - delta / 2))
    								 - Math.pow(10, a - b * (mfd.getX(i) + delta / 2));
    		assertEquals(expectedOccRate, computedOccRate, 1e-5);
    	}

    	// return to the same value
    	mfd.setMagUpper(4);
    	assertEquals(min, mfd.getMagLower(), 0);
    	assertEquals(max, mfd.getMagUpper(), 0);
    	assertEquals(b, mfd.get_bValue(), 1e-5);
    	assertEquals(TMR, mfd.getTotalMomentRate(), 1e15);
    	assertEquals(a, mfd.get_aValue(), 1e-2);
    	assertEquals(TCR, mfd.getTotCumRate(), 1e-1);
    	assertEquals(delta, mfd.getDelta(), 1e-6);
    }
    
    @Test
    public void checkGetAValue() {
    	double a = 14.4;
    	double b = 4.5;
    	double min = 3;
    	double max = 6;
    	int numValues = 250;

    	double delta = (max - min + 1) / numValues;
    	assertEquals(delta, 0.016, 1e-3);
    	
    	double TCR = Math.pow(10, a - b * (min - delta/2)) - Math.pow(10, a - b * (max + delta/2));
    	// TCR = 10 ^ (a - b * (min - delta/2)) - 10 ^ (a - b * (max + delta/2))
    	// 8.63 = 10 ^ (14.4 - (4.5 * (3 - 8e-3))) - 10 ^ (14.47 - (4.5 * (6 + 8e-3)))    	
    	assertEquals(TCR, 8.63, 1e-2);
    	
    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(b, TCR, min, max, numValues);
    	assertEquals(14.4, mfd.get_aValue(), 1e-2);
    }
    
    @Test
    public void checkSetAB() {
    	double TCR = 12;
    	double bValue = 7.3;
    	double min = 5;
    	double max = 7;
    	int numValues = 100;

    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(bValue, TCR, min, max, numValues);
    	mfd.setAB(22, 15);
    	
    	assertEquals(22, mfd.get_aValue(), 1e-2);
    	assertEquals(15, mfd.get_bValue(), 0);
    	
    	// mMax, mMin, delta and num should stay the same
    	assertEquals(5, mfd.getMagLower(), 0);
    	assertEquals(7, mfd.getMagUpper(), 0);
    	assertEquals(0.02, mfd.getDelta(), 1e-3);
    	assertEquals(numValues, mfd.getNum());
    }

    @Test
    public void checkIncrementB() {
    	double TCR = 5;
    	double bValue = 2.2;
    	double min = 1;
    	double max = 4;
    	int numValues = 4;

    	GutenbergRichterMagFreqDist mfd = new GutenbergRichterMagFreqDist(bValue, TCR, min, max, numValues);
    	double oldTMR = mfd.getTotalMomentRate();
    	mfd.incrementB(-0.4);

    	assertEquals(2.2 - 0.4, mfd.get_bValue(), 0);
    	
    	// mMax, mMin, delta and TMR should stay the same
    	assertEquals(1, mfd.getMagLower(), 0);
    	assertEquals(4, mfd.getMagUpper(), 0);
    	assertEquals(1, mfd.getDelta(), 0);
    	assertEquals(oldTMR, mfd.getTotalMomentRate(), 1e8);
    }
}

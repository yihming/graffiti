package org.trajectory.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.trajectory.util.GPSPoint;

public class GPSPointTest {
	private GPSPoint p1;
	private GPSPoint p2;

	@Test
	public void testGeoDistance() {
		p1.setLatitude(39.979666);
		p1.setLongitude(116.337844);
		
		p2.setLatitude(39.980605);
		p2.setLongitude(116.33795);
		
		assert(p1.geoDistance(p2) == p2.geoDistance(p1));
	}

}

package test;
import java.nio.IntBuffer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;


public class CloseTo extends BaseMatcher<Number> implements Matcher<Number> {
	
	private final Number _val;
	private final double _absErr;
	private final boolean _pct;
	
	public CloseTo(Number value) {
		this(value,0.001,true);
	}
	
	public CloseTo(Number value, Number absErr, boolean isPercent) {
		_val = value;
		_absErr = Math.abs(absErr.doubleValue());
		_pct = isPercent;
	}
	
	public static CloseTo closeToPercent(Number value, double percentError) {
		return new CloseTo(value, value.doubleValue() * percentError, true);
	}
	
	public static CloseTo closeTo(Number value, Number error) {
		return new CloseTo(value, error, false);
	}
	
	public static CloseTo closeToMeanPercent(IntBuffer values, double percentError) {
		double value = sum(values) * 1.0 / values.remaining();
		return new CloseTo(value, value * percentError, true);
	}
	
	public static CloseTo closeToMean(IntBuffer values, Number error) {
		double value = sum(values) * 1.0 / values.remaining();
		return new CloseTo(value, error, false);
	}
	
	public static long sum(IntBuffer values) {
		int pos = values.position();
		long sum = 0;
		try {
			while(values.hasRemaining())
				sum += values.get();
		} finally {
			values.position(pos);
		}
		return sum;
	}

	@Override
	public boolean matches(Object item) {
		if(item instanceof Number) {
			return Math.abs(_val.doubleValue() - ((Number)item).doubleValue()) < _absErr;
		}
		return false;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("Within ");
		if(_pct) {
			description.appendValue(_absErr * 100 / _val.doubleValue());
			description.appendText("% of ");
		} else {
			description.appendValue(_absErr);
			description.appendText(" of ");
		}
		description.appendValue(_val);
	}
}

package test;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;


public class CloseTo extends BaseMatcher<Number> implements Matcher<Number> {
	
	private final Number _val;
	private final double _absErr;
	private final boolean _pct;
	
	public CloseTo(Number value) {
		this(value,0.001);
	}
	
	public CloseTo(Number value, double percentError) {
		_val = value;
		_absErr = Math.abs(value.doubleValue() * percentError);
		_pct = true;
	}
	
	public CloseTo(Number value, Number absErr) {
		_val = value;
		_absErr = Math.abs(absErr.doubleValue());
		_pct = false;
	}
	
	public static CloseTo closeToPercent(Number value, double percentError) {
		return new CloseTo(value,percentError);
	}
	
	public static CloseTo closeTo(Number value, Number error) {
		return new CloseTo(value,error);
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

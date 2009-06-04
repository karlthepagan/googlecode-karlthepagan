package test;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class EmpiricalMemoryTest
{
  private static final Runtime _rt = Runtime.getRuntime();
  private Object[] _bucket;
  protected int _capacity = 300000;
  protected int _numberOfRuns = 10;
  protected boolean _filterSamples = true;

  private static void clear(Object[] bucket)
  {
  	int i = bucket.length;
  	while(i > 0) {
  		bucket[--i] = null;
  	}
  }

  public EmpiricalMemoryTest()
  {
    super();
  }

  public void setUp() throws Exception
  {
  	_bucket = new Object[_capacity];
  	
  	gc();
  }

  public void tearDown() throws Exception
  {
  	_bucket = null;
  }

  private void gc() throws InterruptedException
  {
  	System.gc();
  	Thread.yield();
  	System.gc();
  	Thread.sleep(10);
  	System.gc();
  }

  /**
   * Each sample divided by the volume, and the mean is returned. Samples less
   * than zero are discarded.
   */
  private float meanDivVolume(long[] samples, long volume)
  {
  	float value = 0;
  	int skipped = 0;
  	for(int i = 0; i < samples.length; i++) {
  		if(samples[i] >= 0) {
  			value += 1.0 * samples[i] / volume;
  		}
  	}
  	return value / (samples.length - skipped);
  }

  /**
   * removes all samples less than the 8-byte-aligned mean
   */
  private long[][] filterSamples(long[][] origsamples)
  {
  	// TODO consider defensive copy of samples, it gets mutated
  	
    long[] samples = Arrays.copyOf(origsamples[0], origsamples[0].length);
  	int count = 0;
  	int rejects = 0;
  	do {
  		count = 0;
  		rejects = 0;
  		float mean = meanDivVolume(samples,_capacity);
  		int ceil = 8 * (int)Math.round(mean / 8); 
  		int ceilRange = ceil * _capacity;
  		
  		for(int i = 0; i < samples.length; i++) {
  			if(samples[i] >= 0) {
  				if(samples[i] < ceilRange) {
  					samples[i] = -1;
  					rejects++;
  				} else {
  					count++;
  				}
  			}
  		}
  	} while(rejects > 0);
  	
  	if(count * 2 < origsamples.length) {
  	  return origsamples;
  	}
  	
  	long[][] out = new long[origsamples.length][];
  	for(int i = 0; i < origsamples.length; i++) {
  	  out[i] = new long[count];
  	}
  	
  	for(int i = 0; i < samples.length; i++) {
  		if(samples[i] >= 0) {
  			out[0][--count] = samples[i];
  			for(int j = 1; j < origsamples.length; j++) {
  			  out[j][count] = origsamples[j][i];
  			}
  		}
  	}
  	
  	return out;
  }

  public float testDelegateMean(Callable<Long> test, int count) throws Exception
  {
  	float value = 0;
  	long[] samples = new long[count];
    long[] nanos = new long[count];
    long t0 = 0;
  	test.call(); // warmup, reduce the number of rejects
  	clear(_bucket);
  	for(int i = 0; i < count; i++) {
  	  t0 = System.nanoTime();
  		samples[i] = test.call();
      nanos[i] = System.nanoTime() - t0;
  		clear(_bucket);
  		gc();
  	}
  	if(_filterSamples) {
    	long[][] filtered = filterSamples(new long[][]{samples,nanos});
    	samples = filtered[0];
    	nanos = filtered[1];
  	}
  	value = meanDivVolume(samples, _capacity);
  	System.out.println(formatLine(test.toString(), count, value, samples, nanos));
  	return value;
  }

  public float testMean(Callable<? extends Object> maker, int count) throws Exception
  {
  	float value = 0;
  	long[] samples = new long[count];
  	long[] nanos = new long[count];
  	long t0 = 0;
  	test(maker); // warmup
  	clear(_bucket);
  	for(int i = 0; i < count; i++) {
  	  t0 = System.nanoTime();
  		samples[i] = test(maker);
  		nanos[i] = System.nanoTime() - t0;
  		clear(_bucket);
  		gc();
  	}
  	if(_filterSamples) {
      long[][] filtered = filterSamples(new long[][]{samples,nanos});
      samples = filtered[0];
      nanos = filtered[1];
  	}
  	value = meanDivVolume(samples, _capacity);
  	System.out.println(formatLine(maker.toString(), count, value, samples, nanos));
  	return value;
  }
  
  public double sum(long[] samples) {
    double accum = 0;
    for(long l : samples) {
      accum += l;
    }
    return accum;
  }
  
  public double mean(long[] samples) {
    return sum(samples) / samples.length;
  }
  
  public static final long NANOS_PER_SECOND = TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS);
  
  public double nanosToEps(double mean, int volume) {
    return volume * NANOS_PER_SECOND / mean;
  }
  
  private StringBuilder formatLine(String seed, int count, float mean, long[] samples, long[] nanos)
  {
  	int approx = 8 * (int)Math.round(mean / 8); 
  	int volume = approx * _capacity;
  	StringBuilder sb = new StringBuilder(seed);
  	sb.append(" ");
  	sb.append(mean);
  	sb.append(" bytes ");
  	double eps = nanosToEps(mean(nanos),_capacity);
  	sb.append(eps);
  	sb.append(" EPS ");
  	sb.append(eps * mean);
  	sb.append(" bytes per second");
  	sb.append(" (mean ");
  	sb.append(samples.length);
  	sb.append(" of possible ");
  	sb.append(count);
  	sb.append(", GC overhead {");
  	for(int i = 0; i < samples.length; i++) {
  		if(i != 0) sb.append(", ");
  		long waste = samples[i] - volume;
  		sb.append(waste);
  		sb.append(" ");
  		sb.append(Math.round(waste * 100.0 / volume));
  		sb.append("%");
  	}
  	sb.append("})");
  	return sb;
  }

  public long test(Callable<? extends Object> maker) throws Exception
  {
  	int cap = _capacity;
  	long m0 = _rt.totalMemory() - _rt.freeMemory();
  	for(int i = 0; i < cap; i++) {
  		_bucket[i] = maker.call();
  	}
  	return _rt.totalMemory() - _rt.freeMemory() - m0;
  }

}
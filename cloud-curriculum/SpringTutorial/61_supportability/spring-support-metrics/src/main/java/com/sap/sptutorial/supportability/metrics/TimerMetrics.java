package com.sap.sptutorial.supportability.metrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.stereotype.Component;

@Component
public class TimerMetrics implements PublicMetrics {
	private final String TIMER = "timer";
	private final String MAXTIME = "max";
	private final String MINTIME = "min";
	private final String AVGTIME = "average";
	private final String COUNT = "count";
	private final Map<String, Metric<?>> dealMetrics;

	public TimerMetrics() {
		dealMetrics = new HashMap<>();
	}

	@Override
	public Collection<Metric<?>> metrics() {
		return dealMetrics.values();
	}

	public Timer start(String name) {
		return new Timer(name);
	}

	private void stop(String name, long time) {
		String maxname = String.format("%s.%s.%s", TIMER, name, MAXTIME);
		long maxtime = dealMetrics.containsKey(maxname)
				? Math.max(dealMetrics.get(maxname).getValue().longValue(), time) : time;
		dealMetrics.put(maxname, new Metric<Long>(maxname, maxtime));

		String minname = String.format("%s.%s.%s", TIMER, name, MINTIME);
		long mintime = dealMetrics.containsKey(minname)
				? Math.min(dealMetrics.get(minname).getValue().longValue(), time) : time;
		dealMetrics.put(minname, new Metric<Long>(minname, mintime));

		String cntname = String.format("%s.%s.%s", TIMER, name, COUNT);
		String avgname = String.format("%s.%s.%s", TIMER, name, AVGTIME);
		double avgtime = 0;
		if (dealMetrics.containsKey(avgname) && dealMetrics.containsKey(cntname)) {
			long count = dealMetrics.get(cntname).getValue().longValue();
			avgtime = ((dealMetrics.get(avgname).getValue().longValue() * count) + time) / (count + 1L);
			dealMetrics.put(cntname, dealMetrics.get(cntname).increment(1));
		} else {
			avgtime = time;
			dealMetrics.put(cntname, new Metric<Long>(cntname, 1L));
		}
		dealMetrics.put(avgname, new Metric<Double>(avgname, avgtime));
	}

	class Timer {
		private final String name;
		private final long start;

		public Timer(String name) {
			this.name = name;
			this.start = System.nanoTime();
		}

		public void stop() {
			TimerMetrics.this.stop(name, System.nanoTime() - start);
		}
	}

}

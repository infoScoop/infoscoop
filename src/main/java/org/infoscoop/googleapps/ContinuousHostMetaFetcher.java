package org.infoscoop.googleapps;

import java.util.Arrays;
import java.util.List;

import com.google.step2.discovery.HostMeta;
import com.google.step2.discovery.HostMetaException;
import com.google.step2.discovery.HostMetaFetcher;

public class ContinuousHostMetaFetcher implements HostMetaFetcher {
	private final List<HostMetaFetcher> fetchers;

	public ContinuousHostMetaFetcher(HostMetaFetcher... fetchers) {
		if (fetchers.length == 0) {
			throw new IllegalArgumentException("need to supply at least one "
					+ "HostMetaFetcher to ParallelHostMetaFetcher");
		}
		this.fetchers = Arrays.asList(fetchers);
	}

	@Override
	public HostMeta getHostMeta(String host) throws HostMetaException {
		for (HostMetaFetcher fetcher : fetchers) {
			try {
				HostMeta hostMeta = fetcher.getHostMeta(host);

				// return first success
				return hostMeta;
			} catch (Exception e) {
				// ignore error
			}
		}

		throw new HostMetaException("no fetcher found a host-meta for " + host);
	}

}
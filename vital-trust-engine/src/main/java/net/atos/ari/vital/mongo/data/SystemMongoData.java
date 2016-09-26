package net.atos.ari.vital.mongo.data;

public class SystemMongoData {
	String id;
	MetricMongoData metric[];
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public MetricMongoData[] getMetric() {
		return metric;
	}
	public void setMetric(MetricMongoData[] metric) {
		this.metric = metric;
	}
	
}

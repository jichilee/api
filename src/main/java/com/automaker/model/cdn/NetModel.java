package com.automaker.model.cdn;

/**
 * 由machineroom控制方向
 */
public class NetModel {

	private double bandwidth;
	private double requests;
	private long userid;
	private long urls;
	private String net;
//	private String arrow = "1";//进出标记。0出，1进
	
	public String toString(){
		return "bandwidth:" + bandwidth + ",requests:" + requests
				+ ",userid:" + userid + ",urls:" + urls
				+ ",net:" + net;
	}
	
	public NetModel copy(){
		NetModel nm = new NetModel();
		nm.setBandwidth(bandwidth)
			.setRequests(requests)
			.setUserid(userid)
			.setUrls(urls)
			.setNet(net);
		
		return nm;
	}
	/**
	 * 无方向
	 * {@link #copy}
	 * @return
	 */
	@Deprecated
	public NetModel invert(){
		NetModel nm = copy();
		return nm;
	}
	
	public double getBandwidth() {
		return bandwidth;
	}
	public NetModel setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
		return this;
	}
	public double getRequests() {
		return requests;
	}
	public NetModel setRequests(double requests) {
		this.requests = requests;
		return this;
	}
	public long getUrls() {
		return urls;
	}
	public NetModel setUrls(long urls) {
		this.urls = urls;
		return this;
	}
	public String getNet() {
		return net;
	}
	public NetModel setNet(String net) {
		this.net = net;
		return this;
	}
	public long getUserid() {
		return userid;
	}
	public NetModel setUserid(long userid) {
		this.userid = userid;
		return this;
	}
	
}

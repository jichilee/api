package com.automaker.model.cdn;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 机房的内外网。
 * 以cdnid为主线。
 * http://spark.apache.org/graphx/
 * http://www.csdn.net/article/2014-08-07/2821097
 * 
 * TODO
 * @author liqi7
 *
 */
public class MachineRoom {
	
	private long ptime;//key
	private String cdnid;//key
	public static enum ARROW {
		left, right;
	}
	//进出标记。0出，1进。翻转后，避免重复link。
	private ARROW arrow = ARROW.right;
	/**
	 * mr中 写死了外部集合为1000000
	 * short as u-
	 */
	public static final String _mr_startid_default = "1000000";
	private String startid;//source node id  //key
	
	/**
	 * route1指仅仅serverip aggr的结果。对serverip来说，就是合并值了。
	 * 不能再次merge。仅仅在merge时取在大值
	 */
	private long totalUserids;//route2聚合结果。单纯累加内外文件数，可能存在重复。 参考serverips
	private long totalUrls;//单纯累加内外文件数，可能存在重复。 参考serverips//route2聚合结果
	private long serverips;//serverips //route1聚合结果，整体值。不需要merge
	
	/**
	 * route2是指 userip和serverip aggr的结果。
	 * 需要merge时累加
	 */
	private double totalBandwidth;//route2聚合结果//流量不是带宽
	private long totalRequests;//route2聚合结果
	
	//sub net models。从es生成时是两个节点间只有一个netmode。在生成force-echart时，自身分裂，
	//此时汇集子节点的netmodel，生成list。
	private List<NetModel> netModelList;//route2
	
//	private List<String>
	
	private static final Logger logger = LoggerFactory.getLogger(MachineRoom.class);
	
	public String toString(){
		return "ptime:" + ptime + ",cdnid:" + cdnid + ",startid:" + startid
				+ ",serverips:" + serverips + ",totalBandwidth:" + totalBandwidth
				+ ",totalRequests:" + totalRequests + ",totalUserids:" + totalUserids
				+ ",totalUrls:" + totalUrls + ",netModelList:" + netModelList
				+ ",arrow:" + arrow;
	}
	
//	public List<Category> toCategory(){
//		List<Category> categorys = new ArrayList<Category>();
//		//根据net分
//		Category category = new Category();
//		category.setName(name);
//		
//		return categorys;
//	}
	
	
	
	/**
	 * totalBandwidth totalRequests 可以累加
	 * @param mr
	 * @return
	 */
	public MachineRoom merge(MachineRoom mr){
		setTotalBandwidth(totalBandwidth + mr.getTotalBandwidth())
			.setTotalRequests(totalRequests + mr.getTotalRequests())
			.setTotalUrls(maxLong(totalUrls, mr.getTotalUrls()))
			.setTotalUserids(maxLong(totalUserids, mr.getTotalUserids()));
		netModelList.addAll(mr.getNetModelList());
		logger.info("merge:{}", this);
		return this;
	}
	
	private long maxLong(long a, long b){
		return a >= b ? a : b;
	}
	public static String getCategory(String categoryTemplate){
//		return "[{'name':'外部集合'},{'name':'机房'}]";
		return categoryTemplate;
	}
	
	private String getNodeLabel(){
		return cdnid.replace(_mr_startid_default, "u-");
	}
	
	private String getName(){
		return  MessageFormat.format("时间:{0}__机房:{1}__服务器数:{2}__"
				+ "总带宽:{3}__总请求:{4}__总用户:{5}__总url:{6}__"
				+ "进出:{7}", 
				"" + ptime, cdnid.replace(_mr_startid_default, "u"), serverips, normalizBanwidth(), totalRequests, totalUserids, totalUrls, "0".equals(arrow) ? "出" : "进");
	}
//    symbol: 'image://http://www.damndigital.com/wp-content/uploads/2010/12/steve-jobs.jpg',
//    symbolSize: [60, 35],
//    draggable: true,
	public String getNode(String nodeTemplate){
		return nodeTemplate.replace("#category#", (_mr_startid_default.equals(cdnid.split("\\-")[0]) ? 0 : weightRang(getTotalRequests())) + "")
						.replace("#name#", getName())
						.replace("#value#", normalizBanwidth() + "")
						.replace("#label#", getNodeLabel())
						.replace("#color#", "#080808")
//						.replace("#color#", weightRangColor(weightRang(getTotalRequests())))
//						.replace("#symbol#", "image://http://www.damndigital.com/wp-content/uploads/2010/12/steve-jobs.jpg")
						.replace("#symbol#", "circle")
//						.replace("#symbolSize#", "[20, 15]")
						.replace("#symbolSize#", "4")
						.replace("#draggable#", "true")
						;
	}
//	public String getNode(String nodeTemplate){
//		return "{" +
//	        "'name':'" + getName() + "'," +
//				//5minute bandwidth by kb.
//	        "'value':" + normalizBanwidth() + "," +
//	        "'label':'" + getNodeLabel() + "'," +
//	        symbol: 'image://http://www.damndigital.com/wp-content/uploads/2010/12/steve-jobs.jpg',
//	        symbolSize: [60, 35],
//	        draggable: true,
////	        "'id':" + this.cdnid + "," +
//	        "'category':" + ("u-".equals(cdnid.split("-")[0]) ? 0 : weightRang(getTotalRequests())) +//针对反转生成的node
//	        "}";
//	}
	
	public double normalizBanwidth(){
		return this.totalBandwidth * 8 / 300 / 1000 * 1.1;
	}
	
	public String getLink(String linkTemplate, int source, int target){
		return linkTemplate
				.replace("#source#", source + "")
				.replace("#target#", target + "")
				.replace("#weight#", getTotalRequests() + "")
				.replace("#lineWidth#", weightRang(getTotalRequests()) + "")
				.replace("#text#", getTotalRequests() + "")
				.replace("#color#", weightRangColor(weightRang(getTotalRequests())));
	}
	/**
	 * 返回值时category的下标。并且对应节点颜色。0除外。
	 * TODO　config
	 * @param weight
	 * @return
	 */
	@Deprecated
	public static int weightRang(long weight){
		return weight < 100000 ? 1 : 
					weight < 200000 ? 2 : 
						weight < 500000 ? 3 : 4;
	}
	/**
	 * config
	 * @param weight
	 * @return
	 */
	@Deprecated
	public static String weightRangColor(long weight){
		return weight == 1 ? "#00FF00" : 
					weight == 2 ? "#228B22" : 
						weight == 3 ? "#A0522D" : "red";
	}
	/**
	 * a-b split as nodes:a,b. and if a.startid=1000000 a.category=1
	 * a-b a-c split as nodes:a,b,c. there are two a needs compaction.
	 * 
	 * 分裂时自身作为一个，新增一个。
	 * TODO merge the same name nodes.
	 * 
	 * BUG:
	 * 1、清除自身的startid，避免由自身发起链接。
	 * @return
	 */
	public List<MachineRoom> split(){
		List<MachineRoom> mrs = new ArrayList<MachineRoom>();
		mrs.add(invert());
		MachineRoom mr = copy();
//		mr.setStartid(null);//fix bug001。fix bug003
		mrs.add(mr);//right
		logger.info("split:{}", mrs);
		return mrs;
	}
	
	/**
	 * 对端的startid 无意义了。这里只是为了采集node。
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private MachineRoom invert(){
		MachineRoom mr = copy();
		mr.setCdnid(startid)
			.setStartid(cdnid)
			.setServerips(0)
			.setTotalUrls(0)
			.setTotalUrls(0)
			.setArrow(ARROW.left);//反转对象不生成link
		
		//invert netmode
		//check arrayoutofbounds
		NetModel nm = getNetModelList().get(0);
		List<NetModel> netModelList = new ArrayList<NetModel>();
		netModelList.add(nm.invert());
		
		mr.setNetModelList(netModelList);
		logger.info("invert:{}", mr);
		return mr;
	}
	
	public MachineRoom copy(){
		
		MachineRoom mr = new MachineRoom();
		mr.setPtime(ptime)
			.setCdnid(cdnid)
			.setStartid(startid)
			.setServerips(serverips)
			.setTotalBandwidth(totalBandwidth)
			.setTotalRequests(totalRequests)
			.setTotalUrls(totalUrls)
			.setTotalUserids(totalUserids)
			.setNetModelList(netModelList)
			.setArrow(arrow)
			;
		
		return mr;
	}
	
	public String getCdnid() {
		return cdnid;
	}
	public MachineRoom setCdnid(String cdnid) {
		this.cdnid = cdnid;
		return this;
	}
	public double getTotalBandwidth() {
		return totalBandwidth;
	}
	public MachineRoom setTotalBandwidth(double totalBandwidth) {
		this.totalBandwidth = totalBandwidth;
		return this;
	}
	public long getTotalRequests() {
		return totalRequests;
	}
	public MachineRoom setTotalRequests(long totalRequests) {
		this.totalRequests = totalRequests;
		return this;
	}
	public long getTotalUrls() {
		return totalUrls;
	}
	public MachineRoom setTotalUrls(long totalUrls) {
		this.totalUrls = totalUrls;
		return this;
	}
	public long getPtime() {
		return ptime;
	}
	public MachineRoom setPtime(long ptime) {
		this.ptime = ptime;
		return this;
	}
	public long getTotalUserids() {
		return totalUserids;
	}
	public MachineRoom setTotalUserids(long totalUserids) {
		this.totalUserids = totalUserids;
		return this;
	}
	public long getServerips() {
		return serverips;
	}
	public MachineRoom setServerips(long serverips) {
		this.serverips = serverips;
		return this;
	}
	public List<NetModel> getNetModelList() {
		return netModelList;
	}
	public MachineRoom setNetModelList(List<NetModel> netModelList) {
		this.netModelList = netModelList;
		return this;
	}

	public String getStartid() {
		return startid;
	}

	public MachineRoom setStartid(String startid) {
		this.startid = startid;
		return this;
	}
	public ARROW getArrow() {
		return arrow;
	}
	public MachineRoom setArrow(ARROW arrow) {
		this.arrow = arrow;
		return this;
	}
}

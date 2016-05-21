package com.automaker.model.cdn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automaker.model.cdn.MachineRoom.ARROW;

/**
 * http://echarts.baidu.com.cn/doc/example/force.html
 * @author liqi7
 *
 */
public class ForceEchart {
	
	private List<MachineRoom> MachineRooms;
	private List<String> cdnids = new ArrayList<String>();//生成force-echart时构造
	
	private static final Logger logger = LoggerFactory.getLogger(ForceEchart.class);
	
	/**
	 * compaction nodes
	 */
	private Map<String, MachineRoom> mrMerges = new HashMap<String, MachineRoom>();
	@Deprecated
	private Map<String, String> mrLinks = new HashMap<String, String>();
	
	private static String forceTemplate;
	private static String nodeTemplate;
	private static String linkTemplate;
	private static String categoryTemplate;
	
	public static void setForceTemplate(){
		String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		try {
			
			linkTemplate = FileUtils.readFileToString(new File(root + "/templates/link.js"));
			linkTemplate = linkTemplate.replaceAll("[\n|\r]", "");
			
			nodeTemplate = FileUtils.readFileToString(new File(root + "/templates/node.js"));
			nodeTemplate = nodeTemplate.replaceAll("[\n|\r]", "");
			
			categoryTemplate = FileUtils.readFileToString(new File(root + "/templates/category.js"));
			categoryTemplate = categoryTemplate.replaceAll("[\n|\r]", "");
			
			forceTemplate = FileUtils.readFileToString(new File(root + "/templates/force-curve.js"));
			forceTemplate = forceTemplate.replaceAll("[\n|\r]", "");
			
			forceTemplate = forceTemplate.replaceAll(" +", " ");
			linkTemplate = linkTemplate.replaceAll(" +", " ");
			nodeTemplate = nodeTemplate.replaceAll(" +", " ");
			categoryTemplate = categoryTemplate.replaceAll(" +", " ");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	/**
	 * a-b model split as two nodes:a,b; one link:a-b
	 * 
	 * @return
	 */
	public String toForce(){
		
		//debug load each time
		setForceTemplate();
		//
		String ret = forceTemplate.replace("#categories#", MachineRoom.getCategory(categoryTemplate));
		
		StringBuilder nodes = new StringBuilder("[");//nodes
		StringBuilder links = new StringBuilder("[");//nodes
		
		for(MachineRoom mr : MachineRooms){//mrMerges
			//BUG002
//			mrLinks.put(mr.getCdnid(), mr.getStartid() + "_" + mr.getTotalUrls());//arrow: cdnid,startid
			merge(mr.split());
		}
		logger.info("mrMerges:{}", mrMerges);
		for(String cdnid : mrMerges.keySet()){//nodes
			nodes.append(mrMerges.get(cdnid).getNode(nodeTemplate)).append(",");//node
			cdnids.add(cdnid);
		}
		logger.info("cdnids:{}", cdnids);
		for(MachineRoom mr : MachineRooms){//links
			if(mr.getArrow().equals(ARROW.left)) continue;
//			String link = linkTemplate
//					.replace("#source#", cdnids.indexOf(mr.getStartid()) + "")
//					.replace("#target#", cdnids.indexOf(mr.getCdnid()) + "")
//					.replace("#weight#", mr.getTotalRequests() + "")
//					.replace("#lineWidth#", weightRang(mr.getTotalRequests()) + "")
//					.replace("#text#", mr.getTotalRequests() + "")
//					.replace("#color#", weightRang(mr.getTotalRequests()) > 5 ? "red" : "#00FF00");
			
			links.append(mr.getLink(linkTemplate, cdnids.indexOf(mr.getStartid()), cdnids.indexOf(mr.getCdnid())))//使用全局值
					.append(",");
		}
		
		String n = nodes.toString();
		String l = links.toString();
		if(n.endsWith(",")){
			n = n.substring(0, n.length() -1);
			l = l.substring(0, l.length() -1);
		}
		n = n + "]";
		l = l + "]";
		ret = ret.replace("#nodes#", n);
		ret = ret.replace("#links#", l);
		
		return ret;
	}
	
	public static int weightRang(long weight){
		return weight < 1000 ? 1 : 
					weight < 10000 ? 2 : 
						weight < 100000 ? 4 : 
							weight < 1000000 ? 8 : 10;
	}
	/**
	 * 如果cdnid的key不存在，插入，否则merge。
	 * @param cdnid
	 * @param mr
	 */
	public void merge(List<MachineRoom> mrs){
		for(MachineRoom mr : mrs){
			if(mrMerges.containsKey(mr.getCdnid())){//merge
				mrMerges.put(mr.getCdnid(), mrMerges.get(mr.getCdnid()).merge(mr));
			} else {
				mrMerges.put(mr.getCdnid(), mr);
			}
		}
	}
	
	public int toInt(String i){
		return Integer.valueOf(i);
	}
	
	public double toDouble(String i){
		return Double.valueOf(i);
	}
	
	public List<MachineRoom> getMachineRooms() {
		return MachineRooms;
	}
	public void setMachineRooms(List<MachineRoom> machineRooms) {
		MachineRooms = machineRooms;
	}
//	public List<String> getCdnids() {
//		return cdnids;
//	}
//	public void setCdnids(List<String> cdnids) {
//		this.cdnids = cdnids;
//	}
	
}

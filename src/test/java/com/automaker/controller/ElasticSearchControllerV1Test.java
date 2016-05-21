package com.automaker.controller;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.automaker.model.cdn.ForceEchart;
import com.automaker.model.cdn.MachineRoom;
import com.automaker.model.cdn.NetModel;
import com.youku.bgmovie.radar.utils.DateUtils;
import com.youku.bgmovie.radar.utils.MapUtil;
/**
 * 添加startid的aggr
 * @author liqi7
 *
 */
public class ElasticSearchControllerV1Test {

	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchControllerV1Test.class);
    
	
	private Node node;
	private Client client;
	@Before
	public void setUp() {
		ForceEchart.setForceTemplate();
		node = nodeBuilder().client(true).node();
		client = node.client();
	}

	@Test
	public void Loop() throws ElasticsearchException, Exception{
		for(int i=0; i< 5;){
			Map<String, String> params = new HashMap<String, String>();
			params.put("start", "2015110310" + DateUtils.formatHour(i, 0));
			params.put("end", "2015110310" + DateUtils.formatHour(i, 5));
			logger.info("params:{}", params);
			logger.info("params:{}", params);
			ForceEchart mrs = testAggregation(params);
			String ret = mrs.toForce();
			logger.info("force:{}", ret);
			
			JSONObject r = new JSONObject();
			r.put("data", ret);
			ret = r.toString();
//			ret = "{data:[" + ret + "]}";
			JSONObject _ret = JSONObject.fromObject(ret);
			
			i += 5;
		}
	}
	/**
	 * http://www.ruizhishu.com/overt/article/view/117
	 * @return 
	 * @throws Exception 
	 * @throws ElasticsearchException 
	 */
	public ForceEchart testAggregation(Map<String, String> params) throws ElasticsearchException, Exception
	{
		SearchRequestBuilder srb = client.prepareSearch()
				.setIndices("cdnmodel")//cdnmodeltest cdnmodel
				.setTypes("phoenix")
				.setSearchType(SearchType.COUNT);
		
		RangeQueryBuilder ptimeRangeTermQueryBuilder = QueryBuilders.rangeQuery("ptime")
				.gte(MapUtil.getLong(params, "start", 201511021045L))
				.lt(MapUtil.getLong(params, "end", 201511021050L));
//		TermQueryBuilder urlTermQueryBuilder = QueryBuilders.termQuery("cdnid", "1113");
		
		QueryBuilder qb = QueryBuilders.boolQuery()
//				.must(urlTermQueryBuilder)
				.must(ptimeRangeTermQueryBuilder);
		
		srb.setQuery(qb);
		
		TermsBuilder ptimeTermsBuilder = AggregationBuilders.terms("ptimeAgg").field("ptime");
		TermsBuilder cdnidTermsBuilder = AggregationBuilders.terms("cdnidAgg").field("cdnid");
		TermsBuilder startidTermsBuilder = AggregationBuilders.terms("startidAgg").field("startid");
		TermsBuilder netTermsBuilder = AggregationBuilders.terms("netAgg").field("net");
		//嵌套
		startidTermsBuilder.subAggregation(netTermsBuilder);
		cdnidTermsBuilder.subAggregation(startidTermsBuilder);
		ptimeTermsBuilder.subAggregation(cdnidTermsBuilder);
		
		//distinct count server ip
		CardinalityBuilder serveripCardinalityBuilder = AggregationBuilders.cardinality("serveripSum").field("serverip");
		CardinalityBuilder urlCardinalityBuilder = AggregationBuilders.cardinality("urlSum").field("url");
		CardinalityBuilder useridCardinalityBuilder = AggregationBuilders.cardinality("useridSum").field("userid");
//		
		cdnidTermsBuilder.subAggregation(serveripCardinalityBuilder)
						.subAggregation(urlCardinalityBuilder)
						.subAggregation(useridCardinalityBuilder);
		
		/**
		 * http://www.nosqldb.cn/1394012037549.html
		 * 
		 * http://www.programcreek.com/java-api-examples/index.php?api=org.elasticsearch.search.aggregations.metrics.sum.Sum
		 */
		SumBuilder bandwidthSum = AggregationBuilders.sum("bandwidthSum").field("bandwidth");
		SumBuilder requestSum = AggregationBuilders.sum("requestSum").field("request");
		
		/**
		 * https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-cardinality-aggregation.html
		 */
//		CardinalityBuilder urlCardinalityBuilder = AggregationBuilders.cardinality("urlSum").field("url");
//		CardinalityBuilder useridCardinalityBuilder = AggregationBuilders.cardinality("useridSum").field("userid");
//		CardinalityBuilder serveripCardinalityBuilder = AggregationBuilders.cardinality("serveripSum").field("serverip");
		
		netTermsBuilder.subAggregation(bandwidthSum)
			.subAggregation(requestSum);
//			.subAggregation(urlCardinalityBuilder)
//			.subAggregation(useridCardinalityBuilder)
//			.subAggregation(serveripCardinalityBuilder);
		
		
		srb.addAggregation(ptimeTermsBuilder);
		
		return mapping(srb.execute().actionGet());
	}
	
	
	public ForceEchart mapping(SearchResponse sr) throws Exception{
		ForceEchart forceEchart = new ForceEchart();
		logger.info("--------> sr:{}", sr);
		
		Map<String, Aggregation> aggMap = sr.getAggregations().asMap();
		
		LongTerms ptimeTerms = (LongTerms) aggMap.get("ptimeAgg");
		
		List<MachineRoom> machineRooms = new ArrayList<MachineRoom>();
//		List<String> cdnids = new ArrayList<String>();
		
		Iterator<Bucket> ptimeBucketIt = ptimeTerms.getBuckets().iterator();
		while(ptimeBucketIt.hasNext())
		{//ptime
			
			Bucket ptimeBucket = ptimeBucketIt.next();
			StringTerms cdnidTerms = (StringTerms) ptimeBucket.getAggregations().asMap().get("cdnidAgg");
			List<Bucket> cdnidTermList = cdnidTerms.getBuckets();
			Iterator<Bucket> cdnidBucketIt = cdnidTermList.iterator();
			while(cdnidBucketIt.hasNext())
			{//cdnid
				
				Bucket cdnidBucket = cdnidBucketIt.next();
				Map<String, Aggregation> cdhidAggrMap = cdnidBucket.getAggregations().asMap();
				InternalCardinality serverTerms = (InternalCardinality) cdhidAggrMap.get("serveripSum");
				InternalCardinality urlTerms = (InternalCardinality) cdhidAggrMap.get("urlSum");
				InternalCardinality useridTerms = (InternalCardinality) cdhidAggrMap.get("useridSum");
				
//				if(!cdnids.contains(cdnidBucket.getKey())){
//					cdnids.add(cdnidBucket.getKey());
//				}
				
				StringTerms startidTerms = (StringTerms) cdhidAggrMap.get("startidAgg");
				List<Bucket> startidTermList = startidTerms.getBuckets();
				Iterator<Bucket> startidBucketIt = startidTermList.iterator();
				while(startidBucketIt.hasNext()){//startid
					Bucket startidBucket = startidBucketIt.next();
					
//					if(!cdnids.contains(startidBucket.getKey())){
//						cdnids.add(startidBucket.getKey() + cdnidBucket.getKey());
//					}
					
					Map<String, Aggregation> startidAggrMap = startidBucket.getAggregations().asMap();
					StringTerms netTerms = (StringTerms) startidAggrMap.get("netAgg");
					List<Bucket> netTermList = netTerms.getBuckets();
					Iterator<Bucket> netBucketIt = netTermList.iterator();
					
					MachineRoom machineRoom = new MachineRoom();
					machineRoom.setPtime(ptimeBucket.getKeyAsNumber().longValue())
							.setCdnid(cdnidBucket.getKey())
							.setServerips(serverTerms.getValue())
							.setTotalUrls(urlTerms.getValue())
							.setTotalUserids(useridTerms.getValue());
					
					double totalBandwidth = 0.;
					long totalRequests = 0l;
//					long totalUserids = 0l;
//					long totalUrls = 0l;
					List<NetModel> netModelList = new ArrayList<>();
					while(netBucketIt.hasNext()){//net
						
						NetModel netModel = new NetModel();
						Bucket netBucket = netBucketIt.next();
						Map<String, Aggregation> netSum = netBucket.getAggregations().asMap();
						netModel.setNet(netBucket.getKey())
//								.setArrow("1")//default 1
							    .setBandwidth(((Sum) netSum.get("bandwidthSum")).getValue())
							    .setRequests(((Sum) netSum.get("requestSum")).getValue());
						totalBandwidth += netModel.getBandwidth();
						totalRequests += netModel.getRequests();
//						totalUserids += netModel.getUserid();//内外网的客户数累加，可能存在重复
//						totalUrls += netModel.getUrls();//内外网的url累加，可能存在重复
						
//						logger.info(MessageFormat.format("ptime:{0}, cdnid:{1}, startid:{2}, net:{3}, bandwidthSum:{4}, urlSum:{5}, useridSum:{6}, requestSum:{7}", 
//								ptimeBucket.getKey(), cdnidBucket.getKey(), startidBucket.getKey(), netBucket.getKey(),
//								((Sum) netSum.get("bandwidthSum")).getValue(), 
//								((InternalCardinality) netSum.get("urlSum")).getValue(),
//								((InternalCardinality) netSum.get("useridSum")).getValue(),
//								((Sum) netSum.get("requestSum")).getValue()
//								));
						
						//add machineRoom
						netModelList.add(netModel);
					}
					
					machineRoom.setStartid((MachineRoom._mr_startid_default.equals(startidBucket.getKey()) ?
							startidBucket.getKey() + "-" + cdnidBucket.getKey() : startidBucket.getKey()))//扩展为组合key
						.setNetModelList(netModelList)
						.setTotalBandwidth(totalBandwidth)
						.setTotalRequests(totalRequests)
						;
					machineRooms.add(machineRoom);
				}//startid~
			}//cdnid~
		}//ptime~
//		forceEchart.setCdnids(cdnids);
		forceEchart.setMachineRooms(machineRooms);
		return forceEchart;
	}
	
	@After
	public void close() {
		node.close();
	}
}

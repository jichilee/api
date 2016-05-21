package com.automaker.controller;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
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

import com.automaker.model.cdn.MachineRoom;
import com.automaker.model.cdn.NetModel;
import com.youku.bgmovie.radar.utils.DateUtils;
import com.youku.bgmovie.radar.utils.MapUtil;

public class ElasticSearchControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchControllerTest.class);
    
	private Node node;
	private Client client;
	@Before
	public void setUp() {
		node = nodeBuilder().client(true).node();
		client = node.client();
	}

//	@Test
	public void Loop(){
		for(int i=0; i< 5;){
			Map<String, String> params = new HashMap<String, String>();
			params.put("start", "2015110310" + DateUtils.formatHour(i, 0));
			params.put("end", "2015110310" + DateUtils.formatHour(i, 5));
			logger.info("params:{}", params);
			
			List<MachineRoom> mrs = testAggregation(params);
			for(MachineRoom mr : mrs){
				logger.info("mr:{}", mr);
			}
			i += 5;
		}
	}
	/**
	 * http://www.ruizhishu.com/overt/article/view/117
	 * @return 
	 */
	public List<MachineRoom> testAggregation(Map<String, String> params)
	{
		SearchRequestBuilder srb = client.prepareSearch()
				.setIndices("cdnmodel")
				.setTypes("phoenix")
				.setSearchType(SearchType.COUNT);
		
		RangeQueryBuilder ptimeRangeTermQueryBuilder = QueryBuilders.rangeQuery("ptime")
				.gte(MapUtil.getLong(params, "start", 201511021045L))
				.lt(MapUtil.getLong(params, "end", 201511021050L));
		
		QueryBuilder qb = QueryBuilders.boolQuery()
				.must(ptimeRangeTermQueryBuilder);
		
		srb.setQuery(qb);
		
		TermsBuilder ptimeTermsBuilder = AggregationBuilders.terms("ptimeAgg").field("ptime");
		TermsBuilder cdnidTermsBuilder = AggregationBuilders.terms("cdnidAgg").field("cdnid");
		TermsBuilder netTermsBuilder = AggregationBuilders.terms("netAgg").field("net");
		//嵌套
		cdnidTermsBuilder.subAggregation(netTermsBuilder);
		ptimeTermsBuilder.subAggregation(cdnidTermsBuilder);
		
		//distinct count server ip
		CardinalityBuilder serveripCardinalityBuilder = AggregationBuilders.cardinality("serveripSum").field("serverip");
		cdnidTermsBuilder.subAggregation(serveripCardinalityBuilder);
		
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
		CardinalityBuilder urlCardinalityBuilder = AggregationBuilders.cardinality("urlSum").field("url");
		CardinalityBuilder useridCardinalityBuilder = AggregationBuilders.cardinality("useridSum").field("userid");
//		CardinalityBuilder serveripCardinalityBuilder = AggregationBuilders.cardinality("serveripSum").field("serverip");
		
		netTermsBuilder.subAggregation(bandwidthSum)
			.subAggregation(requestSum)
			.subAggregation(urlCardinalityBuilder)
			.subAggregation(useridCardinalityBuilder);
//			.subAggregation(serveripCardinalityBuilder);
		
		
		srb.addAggregation(ptimeTermsBuilder);
		
		return mapping(srb.execute().actionGet());
	}
	
	
	public List<MachineRoom> mapping(SearchResponse sr){
		
		logger.info("--------> sr:{}", sr);
		
		Map<String, Aggregation> aggMap = sr.getAggregations().asMap();
		
		LongTerms ptimeTerms = (LongTerms) aggMap.get("ptimeAgg");
		
		List<MachineRoom> machineRooms = new ArrayList<MachineRoom>();
		
		Iterator<Bucket> ptimeBucketIt = ptimeTerms.getBuckets().iterator();
		while(ptimeBucketIt.hasNext())
		{//ptime
			MachineRoom machineRoom = new MachineRoom();
			Bucket ptimeBucket = ptimeBucketIt.next();
			StringTerms cdnidTerms = (StringTerms) ptimeBucket.getAggregations().asMap().get("cdnidAgg");
			List<Bucket> cdnidTermList = cdnidTerms.getBuckets();
			Iterator<Bucket> cdnidBucketIt = cdnidTermList.iterator();
			machineRoom.setPtime(ptimeBucket.getKeyAsNumber().longValue());
			while(cdnidBucketIt.hasNext())
			{//cdnid
				Bucket cdnidBucket = cdnidBucketIt.next();
				Map<String, Aggregation> cdhidAggrMap = cdnidBucket.getAggregations().asMap();
				StringTerms netTerms = (StringTerms) cdhidAggrMap.get("netAgg");
				InternalCardinality serverTerms = (InternalCardinality) cdhidAggrMap.get("serveripSum");
				List<Bucket> netTermList = netTerms.getBuckets();
				Iterator<Bucket> netBucketIt = netTermList.iterator();
				machineRoom.setCdnid(cdnidBucket.getKey())
						   .setServerips(serverTerms.getValue());
				
				logger.info(MessageFormat.format("ptime:{0}, count:{1}, serverips:{2}", 
						ptimeBucket.getKey(), netTermList.size(), serverTerms.getValue()));
				double totalBandwidth = 0.;
				long totalRequests = 0l;
				long totalUserids = 0l;
				long totalUrls = 0l;
				
				List<NetModel> netModelList = new ArrayList<>();
				while(netBucketIt.hasNext()){//net
					NetModel netModel = new NetModel();
					Bucket netBucket = netBucketIt.next();
					Map<String, Aggregation> netSum = netBucket.getAggregations().asMap();
					netModel.setNet(netBucket.getKey())
						    .setBandwidth(((Sum) netSum.get("bandwidthSum")).getValue())
						    .setUrls(((InternalCardinality) netSum.get("urlSum")).getValue())
						    .setUserid(((InternalCardinality) netSum.get("useridSum")).getValue())
						    .setRequests(((Sum) netSum.get("requestSum")).getValue());
					totalBandwidth += netModel.getBandwidth();
					totalRequests += netModel.getRequests();
					totalUserids += netModel.getUserid();//内外网的客户数累加，可能存在重复
					totalUrls += netModel.getUrls();//内外网的url累加，可能存在重复
					
					logger.info(MessageFormat.format("ptime:{0}, cdnid:{1}, net:{2}, bandwidthSum:{3}, urlSum:{4}, useridSum:{5}, requestSum:{6}", 
							ptimeBucket.getKey(), cdnidBucket.getKey(), netBucket.getKey(),
							((Sum) netSum.get("bandwidthSum")).getValue(), 
							((InternalCardinality) netSum.get("urlSum")).getValue(),
							((InternalCardinality) netSum.get("useridSum")).getValue(),
							((Sum) netSum.get("requestSum")).getValue()
							));
					
					//add machineRoom
					netModelList.add(netModel);
				}
				machineRoom.setNetModelList(netModelList)
					.setTotalBandwidth(totalBandwidth)
					.setTotalRequests(totalRequests)
					.setTotalUrls(totalUrls)
					.setTotalUserids(totalUserids);
			}
			machineRooms.add(machineRoom);
		}
		return machineRooms;
	}
	
	/**
	 * 测试通过。能聚合出结果。保存为模板！
	 * 1、聚合ptime、url、isp的request和bandwidth；
	 * 2、匹配url=sd.mp4 和 userid=136098
	 */
//	@Test
	public void testAggregationExample()
	{
		SearchRequestBuilder srb = client.prepareSearch()
				.setIndices("cdn");
		srb.setTypes("phoenix");
		srb.setSearchType(SearchType.COUNT);
//		.setQuery();//查询--Query 
		
		TermQueryBuilder urlTermQueryBuilder = QueryBuilders.termQuery("url", "sd.mp4");
		TermQueryBuilder useridTermQueryBuilder = QueryBuilders.termQuery("userid", "136098");
		RangeQueryBuilder ptimeRangeTermQueryBuilder = QueryBuilders.rangeQuery("ptime")
				.gte(201511021050L)
				.lte(201511021050L);
		
//		TermFilterBuilder urlTermFilterBuilder = FilterBuilders.termFilter("url", "sd.mp4");
//		TermFilterBuilder useridTermFilterBuilder = FilterBuilders.termFilter("userid", "136098");
		
		/**
		 * bool query:
		 * https://groups.google.com/forum/#!topic/elasticsearch/G3dK4ZMwCr0
		 * 
		 * http://elasticsearch-users.115913.n3.nabble.com/Java-Query-API-td4020466.html
		 * 
		 */
		QueryBuilder qb = QueryBuilders.boolQuery()
				.must(urlTermQueryBuilder).must(useridTermQueryBuilder).must(ptimeRangeTermQueryBuilder);
		
		srb.setQuery(qb);
		
		TermsBuilder gradeTermsBuilder = AggregationBuilders.terms("ptimeAgg").field("ptime");
		TermsBuilder urlTermsBuilder = AggregationBuilders.terms("urlAgg").field("url");
		TermsBuilder ispTermsBuilder = AggregationBuilders.terms("ispAgg").field("isp");
		//嵌套
		urlTermsBuilder.subAggregation(ispTermsBuilder);
		gradeTermsBuilder.subAggregation(urlTermsBuilder);
		
		/**
		 * http://www.nosqldb.cn/1394012037549.html
		 * 
		 * http://www.programcreek.com/java-api-examples/index.php?api=org.elasticsearch.search.aggregations.metrics.sum.Sum
		 */
		SumBuilder bandwidthSum = AggregationBuilders.sum("bandwidthSum").field("bandwidth");
		SumBuilder requestSum = AggregationBuilders.sum("requestSum").field("request");
		ispTermsBuilder.subAggregation(bandwidthSum).subAggregation(requestSum);
		
		
		srb.addAggregation(gradeTermsBuilder);
		
		SearchResponse sr = srb.execute().actionGet();
		
		logger.info("--------> sr:{}", sr);
		
		Map<String, Aggregation> aggMap = sr.getAggregations().asMap();
		
		LongTerms gradeTerms = (LongTerms) aggMap.get("ptimeAgg");
		
		Iterator<Bucket> gradeBucketIt = gradeTerms.getBuckets().iterator();
		
		while(gradeBucketIt.hasNext())
		{
			Bucket gradeBucket = gradeBucketIt.next();
			System.out.println(gradeBucket.getKey() + "ptime" + gradeBucket.getDocCount() +"url");
			
			StringTerms classTerms = (StringTerms) gradeBucket.getAggregations().asMap().get("urlAgg");
			Iterator<Bucket> classBucketIt = classTerms.getBuckets().iterator();
			
			while(classBucketIt.hasNext())
			{
				Bucket classBucket = classBucketIt.next();
				System.out.println(gradeBucket.getKey() + "年级" +classBucket.getKey() + "班有" + classBucket.getDocCount() +"个学生。");
			}
			System.out.println();
		}
		
	}
	
	@After
	public void close() {
		node.close();
	}
}

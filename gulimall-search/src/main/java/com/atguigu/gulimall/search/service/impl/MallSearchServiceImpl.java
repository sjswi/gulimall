package com.atguigu.gulimall.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.*;
import co.elastic.clients.elasticsearch.ingest.SortProcessor;
import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuEsModelTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.feign.ProductFeignService;
import com.atguigu.gulimall.search.service.MallSearchService;

import com.atguigu.gulimall.search.vo.AttrResponseVo;
import com.atguigu.gulimall.search.vo.SearchParamVo;
import com.atguigu.gulimall.search.vo.SearchResponseVo;
import com.atguigu.gulimall.search.vo.SearchResult;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;


import javax.swing.text.Highlighter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: gulimall
 * @description: 商城检索服务的实现类
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-11 19:27
 **/
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private ElasticsearchClient client;
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResponseVo search(SearchParamVo param) {

        SearchRequest searchRequest = buildSearchRequest(param);
        SearchResponseVo responseVo=null;
        try {

            SearchResponse<SearchResult> search = client.search(searchRequest, SearchResult.class);
//            System.out.println(searchRequest.highlight().fields());
            responseVo = buildSearchResult(search, param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseVo;
    }

    private SearchResponseVo buildSearchResult(SearchResponse<SearchResult> search, SearchParamVo param) {

        /**
         *
         *
         * **/
        //1、返回所有查询到的商品
        SearchResponseVo responseVo = new SearchResponseVo();
        HitsMetadata<SearchResult> hits = search.hits();
        assert hits.total() != null;
        long value = hits.total().value();
        List<SkuEsModelTo> list = new ArrayList<>();
        if(hits.hits()!=null&&hits.hits().size()>0){
            for(Hit<SearchResult> hit:hits.hits()){
                SearchResult source = hit.source();
                if(source!=null) {
                    SkuEsModelTo esModelTo = new SkuEsModelTo();
                    BeanUtils.copyProperties(source, esModelTo);
                    esModelTo.setSkuId(Long.parseLong(source.getSkuId()));
                    esModelTo.setSpuId(Long.parseLong(source.getSpuId()));
                    esModelTo.setSkuPrice(BigDecimal.valueOf(Double.parseDouble(source.getSkuPrice())));

//                    高亮处理
                    if (!StringUtils.isNullOrEmpty(param.getKeyword())){
                        List<String> skuTitle = hit.highlight().get("skuTitle");
//                        System.out.println(skuTitle.toString());
                        if(skuTitle!=null) {
                            esModelTo.setSkuTitle(skuTitle.get(0));
                        }
                    }
                    list.add(esModelTo);
                }

            }
        }
        responseVo.setProducts(list);
//        从聚合中得到属性信息封装到AttrVo
        List<SearchResponseVo.AttrVo> attrVos = new ArrayList<>();
        Aggregate attr_agg = search.aggregations().get("attr_agg");
        NestedAggregate nestedAggregate = (NestedAggregate) attr_agg._get();
        LongTermsAggregate longTermsAggregate = (LongTermsAggregate) nestedAggregate.aggregations().get("attr_id_agg")._get();
        List<LongTermsBucket> buckets = longTermsAggregate.buckets().array();
        for(LongTermsBucket bucket:buckets){
            SearchResponseVo.AttrVo attrVo = new SearchResponseVo.AttrVo();
            attrVo.setAttrId(Long.parseLong(bucket.key()));
            Aggregate attr_name_agg = bucket.aggregations().get("attr_name_agg");
            StringTermsAggregate stringTermsAggregate = (StringTermsAggregate) attr_name_agg._get();
            String attr_name = stringTermsAggregate.buckets().array().get(0).key();
            attrVo.setAttrName(attr_name);
            Aggregate attr_value_agg = bucket.aggregations().get("attr_value_agg");
            StringTermsAggregate stringTermsAggregate1 = (StringTermsAggregate) attr_value_agg._get();
            List<StringTermsBucket> array = stringTermsAggregate1.buckets().array();
            List<String> collect = array.stream().map(stringTermsBucket -> {
                return stringTermsBucket.key();
            }).collect(Collectors.toList());
            attrVo.setAttrValue(collect);
            attrVos.add(attrVo);
        }
        responseVo.setAttrVos(attrVos);
//        商品涉及的品牌信息
        List<SearchResponseVo.BrandVo> brandVos = new ArrayList<>();
        LongTermsAggregate longTermsAggregate1 = (LongTermsAggregate) search.aggregations().get("brand_agg")._get();
        List<LongTermsBucket> array = longTermsAggregate1.buckets().array();
        for(LongTermsBucket bucket:array){
            SearchResponseVo.BrandVo brandVo = new SearchResponseVo.BrandVo();
            brandVo.setBrandId(Long.parseLong(bucket.key()));
            StringTermsAggregate stringTermsAggregate = (StringTermsAggregate) bucket.aggregations().get("brand_img_agg")._get();
            brandVo.setLogo(stringTermsAggregate.buckets().array().get(0).key());
            StringTermsAggregate stringTermsAggregate1 = (StringTermsAggregate) bucket.aggregations().get("brand_name_agg")._get();
            brandVo.setName(stringTermsAggregate1.buckets().array().get(0).key());
            brandVos.add(brandVo);
        }
        responseVo.setBrandVos(brandVos);
//        商品涉及的分类信息
        List<SearchResponseVo.CategoryVo> categoryVos = new ArrayList<>();
        LongTermsAggregate longTermsAggregate2 = (LongTermsAggregate) search.aggregations().get("catalog_agg")._get();
        List<LongTermsBucket> array1 = longTermsAggregate2.buckets().array();
        for(LongTermsBucket bucket:array1){
            SearchResponseVo.CategoryVo categoryVo = new SearchResponseVo.CategoryVo();
            categoryVo.setCatelogId(Long.parseLong(bucket.key()));

            StringTermsAggregate stringTermsAggregate = (StringTermsAggregate) bucket.aggregations().get("catalog_name_agg")._get();
            categoryVo.setCatelogName(stringTermsAggregate.buckets().array().get(0).key());

            categoryVos.add(categoryVo);
        }
        responseVo.setCategoryVos(categoryVos);

//        设置分页信息
        responseVo.setPageNum(Math.toIntExact(param.getPageNum()));
        responseVo.setTotal(value);
        int v = (int) value;
        int totalPage = (int) (v%EsConstant.PRODUCT_PAGESIZE==0?v/EsConstant.PRODUCT_PAGESIZE:(v+1)/EsConstant.PRODUCT_PAGESIZE);
        responseVo.setTotalPages(totalPage);
        List<Integer> pageNavs = new ArrayList<>();
        for(int i=1;i<=totalPage;i++){
            pageNavs.add(i);
        }
        responseVo.setPageNavs(pageNavs);
//        System.out.println(responseVo);
//        构建面包屑导航
        if (param.getAttrs() !=null && param.getAttrs().size()>0){
            List<SearchResponseVo.NavVo> collect = param.getAttrs().stream().map(attr -> {
                //1、分析每个attrs传递过来的参数值
                SearchResponseVo.NavVo navVo = new SearchResponseVo.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                responseVo.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0){
                    AttrResponseVo attrResponseVo = (AttrResponseVo) r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(attrResponseVo.getAttrName());
                }else{
                    navVo.setNavName(s[0]);
                }
                //2、取消了这个面包屑之后，跳转的地方，将请求的地址的url里面的当前条件置空
                //拿到所有的查询条件去掉当前
                String replace = replaceQueryString(param, attr,"attrs");
                navVo.setLink("http://search.gulimall.com/list.html?"+replace);

                return navVo;
            }).collect(Collectors.toList());
            responseVo.setNavs(collect);
        }
        if (param.getBrandIds() != null && param.getBrandIds().size() >0){
            List<SearchResponseVo.NavVo> navs = responseVo.getNavs();
            SearchResponseVo.NavVo navVo = new SearchResponseVo.NavVo();
            navVo.setNavName("品牌");
            //远程查询所有品牌
            R r = productFeignService.brandsInfo(param.getBrandIds());
            if (r.getCode() == 0){
                List<SearchResponseVo.BrandVo> brand = (List<SearchResponseVo.BrandVo>) r.getData("brand", new TypeReference<List<SearchResponseVo.BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for (SearchResponseVo.BrandVo brandVo : brand) {
                    buffer.append(brandVo.getName()+";");
                    replace = replaceQueryString(param, brandVo.getBrandId()+"","brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.gulimall.com/list.html?"+replace);
            }
            navs.add(navVo);
            responseVo.setNavs(navs);
            System.out.println(navs);
        }
        return responseVo;
    }

    private String replaceQueryString(SearchParamVo param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "%20");//浏览器和java对+号的差异化处理
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = param.get_queryString().replace("&"+key+"=" + encode, "");
        return replace;
    }
    /**
     * 构建检索请求
     *
     * @param param
     * @params: null
     * @return: SearchRequest
     * @author: yuxiaobing
     * @date: 2022/3/12
     */
    private SearchRequest buildSearchRequest(SearchParamVo param) {
//        DSL语句构造器
        SearchRequest.Builder builder = new SearchRequest.Builder();
//        指定索引
        builder.index(EsConstant.PRODUCT_INDEX);
//        1、构架bool-query-build
        BoolQuery.Builder bool = QueryBuilders.bool();
//        1.1 构建must条件，是否给出关键字
        if (!StringUtils.isNullOrEmpty(param.getKeyword())) {
            bool.must(s -> {
                return s.match(m -> {

                    return m.field("skuTitle").query(param.getKeyword());
                });
            });
        }
//        1.2 构建filter 按照(属性，价格，品牌，库存，分类)
        List<Query> queryLink = new ArrayList<>();
//      分类id
        if (param.getCatalog3Id() != null) {
            queryLink.add(new Query.Builder().term(s -> {
                return s.field("catalogId").value(param.getCatalog3Id().toString());
            }).build());
        }
        //品牌id
        if (param.getBrandIds() != null && param.getBrandIds().size() > 0) {
            queryLink.add(new Query.Builder().term(s -> {
                return s.field("brandId").value(param.getBrandIds().get(0));
            }).build());
        }
//        属性
        if(param.getAttrs()!=null&&param.getAttrs().size()>0){

            for (String attr:param.getAttrs()) {
                NestedQuery.Builder builder1 = new NestedQuery.Builder();

                BoolQuery.Builder builder2 = new BoolQuery.Builder();
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                builder2.must(sss -> {
                    return sss.term(t -> {
                        return t.field("attrs.attrId").value(attrId);
                    });
                }).must(sss -> {
                    return sss.terms(t->{
                        return t.field("attrs.attrValue").terms(t1->{
                            List<FieldValue> fieldValues = new ArrayList<>();
                            for(String i:attrValues){
                                fieldValues.add(FieldValue.of(i));
                            }
                            t1.value(fieldValues);
                            return t1;
                        });
                    });
                });
//                每一个属性都生成一个嵌入式查询
                builder1.path("attrs").query(new Query(builder2.build()));
                queryLink.add(new Query(builder1.build()));
            }

        }
//        库存
        if(param.getHasStock()!=null) {
            queryLink.add(new Query.Builder().term(s -> {
                return s.field("hasStock").value(param.getHasStock() == 1);
            }).build());
        }
//        价格区间

        if(!StringUtils.isNullOrEmpty(param.getSkuPrice())){
            RangeQuery.Builder rangeQuery = new RangeQuery.Builder().field("skuPrice");

            String[] s1 = param.getSkuPrice().split("_");
            if(s1.length==2){
                rangeQuery.gte(JsonData.of(s1[0])).lte(JsonData.of(s1[1]));
            }else if(s1.length==1){
                if(param.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(JsonData.of(s1[0]));
                }
                if(param.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(JsonData.of(s1[0]));
                }
            }
            queryLink.add(new Query(rangeQuery.build()));
        }
//        加入boolquery
        bool.filter(queryLink);
        builder.query(new Query(bool.build()));
        /**
         * 2
         * 排序
         * 高亮
         * 分页
         * **/
//        排序sortOption
        if(!StringUtils.isNullOrEmpty( param.getSort())){
            String[] s = param.getSort().split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc")?SortOrder.Asc:SortOrder.Desc;
            builder.sort(new SortOptions.Builder().field(v->{
                return v.field(s[0]).order(order);
            }).build());

        }
//        分页
        builder.from((int) ((param.getPageNum()-1)*EsConstant.PRODUCT_PAGESIZE));
        builder.size(Math.toIntExact(EsConstant.PRODUCT_PAGESIZE));

//        高亮

        if(!StringUtils.isNullOrEmpty(param.getKeyword())){
            Highlight.Builder builder1 = new Highlight.Builder();
            HighlightField.Builder builder2 = new HighlightField.Builder();
//            builder2.field("skuTitle");
            builder1.fragmentSize(80000);
            builder1.preTags("<b style='color:red'>");
            builder1.postTags("</b>");
            builder1.fields("skuTitle", builder2.build());

            builder.highlight(builder1.build());
        }

        /**
         * 聚合分析
         * 聚合的层次结构
         * agg->{brand_agg->{brandId,brandImg,brandId},catalog_agg->{catalogName,catalogId},attr_agg->{attrId->{attrName,attrValue}}}
         * **/
//        1、品牌聚合
        Map<String, Aggregation> map = new HashMap<>();
//        三个最外层聚合
        Aggregation.Builder brand_agg = new Aggregation.Builder();
        Aggregation.Builder catalog_agg = new Aggregation.Builder();
        Aggregation.Builder attr_agg = new Aggregation.Builder();




//      设置品牌聚合

        TermsAggregation.Builder builder1 = new TermsAggregation.Builder();
        TermsAggregation.Builder builder5 = new TermsAggregation.Builder();
        TermsAggregation.Builder builder6 = new TermsAggregation.Builder();
        builder5.field("brandName").size(1);
        builder6.field("brandImg").size(1);
        builder1.field("brandId").size(50);
        brand_agg.aggregations("brand_name_agg",new Aggregation(builder5.build()));
        brand_agg.aggregations("brand_img_agg",new Aggregation(builder6.build()));
//         分类聚合
        TermsAggregation.Builder builder7 = new TermsAggregation.Builder();
        TermsAggregation.Builder builder3 = new TermsAggregation.Builder();
        builder7.field("catalogName").size(1);
        builder3.field("catalogId").size(10);
        catalog_agg.aggregations("catalog_name_agg", new Aggregation(builder7.build()));
//                设置属性聚合
        NestedAggregation.Builder builder4 = new NestedAggregation.Builder();
        TermsAggregation.Builder builder9 = new TermsAggregation.Builder();
        TermsAggregation.Builder builder10 = new TermsAggregation.Builder();
        TermsAggregation.Builder builder11 = new TermsAggregation.Builder();
        Aggregation.Builder builder8 = new Aggregation.Builder();
        builder4.path("attrs");
        builder9.field("attrs.attrName").size(1);
        builder10.field("attrs.attrId").size(1);
        builder11.field("attrs.attrValue").size(1);
        builder8.aggregations("attr_value_agg", new Aggregation(builder11.build()));
        builder8.aggregations("attr_name_agg", new Aggregation(builder9.build()));
        attr_agg.aggregations("attr_id_agg", builder8.terms(builder10.build()).build());
//        将最外层加入request
        map.put("brand_agg", brand_agg.terms(builder1.build()).build());
        map.put("catalog_agg", catalog_agg.terms(builder3.build()).build());
        map.put("attr_agg", attr_agg.nested(builder4.build()).build());
        builder.aggregations(map);
////        System.out.println("构建的DSL："+s);
        SearchRequest searchRequest = builder.build();
//        System.out.println(searchRequest.aggregations().toString());
////        System.out.println(searchRequest.defaultOperator().toString());
//        System.out.println(searchRequest.preference());
//        System.out.println(searchRequest.q());
////        System.out.println(searchRequest.source().toString());
////        System.out.println(searchRequest.aggregations().toString());
////        System.out.println(searchRequest.aggregations().toString());
////        System.out.println(searchRequest.aggregations().toString());



        return searchRequest;
    }
}

package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModelTo;
import com.atguigu.common.validator.group.AddGroup;
import com.atguigu.common.validator.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: gulimall
 * @description: 检索信息返回的传输对象
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-11 19:39
 **/
@Data
public class SearchResponseVo {
    private List<SkuEsModelTo> products;

    private Integer pageNum;

    private Long total;

    private Integer totalPages;

    private List<BrandVo> brandVos;
    private List<AttrVo> attrVos;
    private List<CategoryVo> categoryVos;
    private List<Integer> pageNavs;
    private Boolean hasStock;
//    面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();
//    返回给页面的全部信息
    @Data
    public static class BrandVo{

        private Long brandId;
        /**
         * 品牌名
         */

        private String name;

        private String logo;
    }
    @Data
    public static class AttrVo{

        private Long attrId;

        private String attrName;
        private List<String> attrValue;
    }
    @Data
    public static class CategoryVo{

        private Long catelogId;

        private String catelogName;
//        private List<String> catelogValue;
    }

    @Data
    public static class NavVo {
        private String navName;
        private String navValue;
        private String link;
    }
}

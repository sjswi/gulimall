package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;

import com.atguigu.gulimall.search.vo.SearchParamVo;
import com.atguigu.gulimall.search.vo.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: gulimall
 * @description: 检索页面
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-11 18:53
 **/
@Controller
public class SearchController {
    @Autowired
    MallSearchService mallSearchService;

    /**
     * 将页面的查询参数封装成一个对象
     * @params: SearchParam
     * @return:  String
     * @author: yuxiaobing
     * @date: 2022/3/11
     **/
    @GetMapping("/list.html")
    public String listPage(SearchParamVo param, Model model, HttpServletRequest request) {
        param.set_queryString(request.getQueryString());
        SearchResponseVo result = mallSearchService.search(param);
        model.addAttribute("result",result);
        return "list";
    }
}

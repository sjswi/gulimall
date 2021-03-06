package com.atguigu.common.constant;

/**
 * @program: gulimall
 * @description: 库存常量类
 * @author: yuxiaobing
 * @mail：a17281293@gmail.com
 * @date: 2022-03-06 17:44
 **/
public class WareConstant {
    public enum PurchaseStatusEnum{
        CREATE(0,"新建"),ASSIGNED(1,"已分配"),RECEIVED(2,"已领取"),FINISHED(3,"已完成"),HASERROR(4,"有异常");
        private int code;
        private String msg;
        PurchaseStatusEnum(int code, String msg){
            this.code=code;
            this.msg=msg;
        }

        PurchaseStatusEnum() {

        }

        public int getCode() {
            return this.code;
        }

        public String getMsg() {
            return this.msg;
        }
    }

    public enum PurchaseDetailStatusEnum{
        CREATE(0,"新建"),ASSIGNED(1,"已分配"),BUYING(2,"正在采购"),FINISHED(3,"已完成"),HASERROR(4,"采购失败");
        private int code;
        private String msg;
        PurchaseDetailStatusEnum(int code, String msg){
            this.code=code;
            this.msg=msg;
        }

        PurchaseDetailStatusEnum() {

        }

        public int getCode() {
            return this.code;
        }

        public String getMsg() {
            return this.msg;
        }
    }

}

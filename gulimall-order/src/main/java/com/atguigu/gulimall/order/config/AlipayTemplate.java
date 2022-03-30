package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;

import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * @author yaoxinjia
 * @email 894548575@qq.com
 */
@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public String app_id="2021000119650033";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public String merchant_private_key="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCCMTEdBvIKA9OvEhscyp9Agki4axDMrRKXA3XR1su3Z9FuOC1DYhPwrmKOx9M89NOm/hIB1YJ6wdd7bebmOXbYKV9Q1elUZR5E4fVIfjY7AlDA0+msl9vHmasQBVxV05wiemMsN73amrif242d+6SJFnxQiNsGibGGy7BJaeigLXKpoU4t5JxzcytCwEHFJ+qGJMYPEVbZrMwEo/B9vyzwScSnt8mV8mgJmlt4ogi3/M1N4tERC4ducZt1I7RiFWMth/qtUJFryriv9K14KDmV9IByN0GX1rZJTl5wKzhO67wocrnbdOLU78qXxPj/naPGdd1VyILk8tcQgXhYukBlAgMBAAECggEAZWk98Xdch7KNbFx1JAss20ngrU4wqs3RojH+z3I6AuXqe6lkkI1QLPUZLlC22U93oidcDXOrjEV7vNhRuFlgmmY3qbbGZofReSRtNIejZHmcP2eSKG+tsZ+gYP1b/Dq17yFYKLROvX4xe8vAVyQR+Pq74NuJiGEvGGmHKthFbKZ9ssMoHyc1KiKn1ObIUbqeQteWfsrKX4FR5lrzrLRq0I1y3fAv4uUBFio3GvaAUZvzv4W0hxIYPlUHx3knVH9JdNRh8eW4yJYVC3yMjMrIA4DQQfsSskLdEpMUcZl3qw0/q5GniS9V4QNRXNyzDdAb+P3u9AwLWycGNi29EXbNbQKBgQC4MKtv4amed1irbAEAmceYYCg+Xwgcs4bh6y7RgxVpAYasSEvE6SWZ2BS8FCWb+bWVWVqHyV8/ws7rL7QvRtbCuck11kcXiJ0JTtO78+Gs/6YwtqOAmjqVNHqEJola16JvsopMIvGQqhvzjhcb4lHMYCHi69vgPoHDJeABQkfzqwKBgQC08y/GU1WxvPpuEZPYbth104i2NoQnYx/dVVraeARzQT/BeXqAeYY7frVFlO5N4bdjObbRd73a8AkuJmOvn6HKGXCN6qbKq0tYKhLGxiDNxyktmmSEI7gvb76zQQ1rxJ6r/kS5q6kkZowyYuWfo/zYMQlNn9A0WU/1U0ZLicmMLwKBgQCI1te3Ai8S5NXCXTYNbTIlGWMsm0uajyHpW+Fnk/xnC9Qy0G9CNnhR6H/XDxy12Xm3IDAq60dRRniVr9YsvXM2LfK3hZ9Bc6Q/Uwno/BIwhanBTR8BnxNg4CEZZsvq6oLqevlWASv4bvjfJDQ06hodPVpPjSzIV4fuJIhcRxLPiwKBgGtQKk4xUAon7FFoxUi/fEpsmJCPxlJhnB00qLwWH3WHBdulQrx81dnk7HaGu8meipcjPEQAAq2cW6VuCQK35kioS7MurZpWekmgMiCGkh/X9JjiDqi7Ull3qR4//0ihhAS2uGVw48/9rTmYo1vI8oP4mO7bT6wsJ5pzt6XG4qvrAoGBAI+knUWZIBcyywU0KsPT1dimBgKKz3kxQv1PQXWQj1SN75BLfXUUFvYQ+L4wlRI3mZdU8t+dDnMIdqUusKnvkz3TQbuc9TPLEZ2QwEtKaNb6amiw/UQKN4Z2K1QqFmplESq/pUAqTfyw7CTPWd+gQac0yh2HUewhAUr5ifnN9ebM";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public String alipay_public_key="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyMlj6YVn4QezA1diTNCK/VxCgRXqiyYuq6Pk/YEqywSHb+bcE7L3kLivro6aLkLmx5xGtToNpn2jj2vvUi3pyO03tk35Fe2qycCNrlpj9UypWNiZrnZhWjVA4suaaQxN+sQh+XKvE5zUTJ5OtzapfXGekLuEApFKGYDOoxijfPX89uwrKsZma5iA2X8WFj2BxtH339khGOzsAQmu4EbB1bKX8naKWPXPt4S02AamGiTYfiF3xWvMLPejTyK4rniQ8LWWacccRmhdzfjpOf9Nky6oR/JZ1i2iuED+q6bx17JjrZCV1NdX609EMAOsNwfmeruaTzfAY10m7dJIgTJqMwIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    public String notify_url="http://e6y3prtjec.51xd.pub/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    public String return_url="http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type="RSA2";

    // 字符编码格式
    private  String charset="utf-8";

    //订单超时时间
    private String timeout = "1m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    public String gatewayUrl="https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+timeout+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}

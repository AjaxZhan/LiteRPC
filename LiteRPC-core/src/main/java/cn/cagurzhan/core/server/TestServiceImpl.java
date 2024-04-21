package cn.cagurzhan.core.server;

import cn.cagurzhan.interfaces.TestService;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试使用的服务Bean
 * @author AjaxZhan
 */
public class TestServiceImpl implements TestService {
    @Override
    public String sendData(String data) {
        System.out.println("收到的数据长度："+data.length());
        return "ok";
    }

    @Override
    public List<String> getList() {
        List<String> arrayList = new ArrayList<>();
        arrayList.add("idea1");
        arrayList.add("idea2");
        arrayList.add("idea3");
        return arrayList;
    }
}

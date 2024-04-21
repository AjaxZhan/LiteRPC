package cn.cagurzhan.interfaces;

import java.util.List;

/**
 * 测试服务接口
 * @author AjaxZhan
 */
public interface TestService {
    /**
     * 发送数据
     */
    String sendData(String data);

    /**
     * 拿到数据
     */
    List<String> getList();
}

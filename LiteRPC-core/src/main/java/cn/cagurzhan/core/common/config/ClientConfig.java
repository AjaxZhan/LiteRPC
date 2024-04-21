package cn.cagurzhan.core.common.config;

import lombok.Data;

/**
 * 客户端配置
 * @author AjaxZhan
 */
@Data
public class ClientConfig {
    private Integer port;
    private String serverHost;
}

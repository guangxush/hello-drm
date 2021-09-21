package com.shgx.drm.register;

import com.shgx.drm.commons.ConfigRegistryEnum;
import com.shgx.drm.configcenter.ConfigModel;
import com.shgx.drm.configcenter.ConfigRegistry;
import com.shgx.drm.configcenter.ConfigRegistryFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: guangxush
 * @create: 2021/09/21
 */
@Slf4j
public class ConfigDiscover {

    ConfigRegistry configRegistry;

    private static final String ADDRESS = "127.0.0.1:2181";

    @Before
    public void init() throws Exception {
        configRegistry = ConfigRegistryFactory.getInstance(ConfigRegistryEnum.zookeeper, ADDRESS);
    }

    @After
    public void close() throws Exception {
        configRegistry.close();
    }

    @Test
    public void testAll() throws Exception {
        ConfigModel test11 = ConfigModel
                .builder()
                .configName("test1")
                .configPort(8088)
                .configVersion("1.0.0")
                .address("127.0.0.1");
        ConfigModel test22 = ConfigModel
                .builder()
                .configName("test2")
                .configPort(8088)
                .configVersion("1.0.0")
                .address("127.0.0.2");
        ConfigModel test33 = ConfigModel
                .builder()
                .configName("test3")
                .configPort(8088)
                .configVersion("1.0.0")
                .address("127.0.0.3");

        configRegistry.register(test11);
        configRegistry.register(test22);
        configRegistry.register(test33);


        ConfigModel test1 = configRegistry.discovery("test1:1.0.0");
        ConfigModel test2 = configRegistry.discovery("test2:1.0.0");
        ConfigModel test3 = configRegistry.discovery("test3");

        assert test1 != null;
        assert test2 != null;
        assert test3 == null;

        configRegistry.unRegister(test11);
        configRegistry.unRegister(test22);
        configRegistry.unRegister(test33);
    }

    @Test
    public void testReRegister() throws Exception {
        configRegistry.register(ConfigModel
                .builder()
                .configName("test")
                .configPort(8088)
                .configVersion("1.0.0")
                .address("127.0.0.1"));
        configRegistry.register(ConfigModel
                .builder()
                .configName("test")
                .configPort(8088)
                .configVersion("1.0.0")
                .address("127.0.0.1"));
    }

    @Test
    public void testLoadBalance() throws Exception {
        configRegistry.register(ConfigModel
                .builder()
                .configName("test")
                .configPort(8088)
                .configVersion("1.0.0")
                .address("127.0.0.1"));

        configRegistry.register(ConfigModel
                .builder()
                .configName("test")
                .configPort(8088)
                .configVersion("1.0.0")
                .address("127.0.0.2"));

        configRegistry.register(ConfigModel
                .builder()
                .configName("test")
                .configPort(8088)
                .configVersion("1.0.0")
                .address("127.0.0.3"));


        ConfigModel test1 = configRegistry.discovery("test:1.0.0");
        ConfigModel test2 = configRegistry.discovery("test:1.0.0");
        ConfigModel test3 = configRegistry.discovery("test:1.0.0");

        assert test1 != null;
        assert test2 != null;
        assert test3 != null;

        assert !test1.getAddress().equals(test2.getAddress());
        assert !test1.getAddress().equals(test3.getAddress());
        assert !test2.getAddress().equals(test3.getAddress());

        log.info("test1: {}", test1.toString());
        log.info("test2: {}", test2.toString());
        log.info("test3: {}", test3.toString());
    }
}

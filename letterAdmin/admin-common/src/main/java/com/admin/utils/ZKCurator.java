package com.admin.utils;


import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKCurator {
    //zk客户端
    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCurator.class);

    public ZKCurator(CuratorFramework client){
        this.client = client;
    }

    public void init() {

        //为什么这个命名空间定义了没有用?
//        client = client.usingNamespace("admin");

        try {

            //判断在admin命名空间下是否有bgm节点 /admin/bgm

            if (client.checkExists().forPath("/admin/bgm") == null) {
                /**
                 * 对于zk来讲,有两种类型的节点:
                 * 持久节点: 当你创建一个节点的时候,这个节点就永远存在,除非你手动删除
                 * 临时节点:你创建一个节点之后,会话断开,会自动删除.也可以手动删除
                 */
                client.create().creatingParentContainersIfNeeded()
                        .withMode(CreateMode.PERSISTENT)  //节点类型:持久节点
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)  //acl:匿名权限
                        .forPath("/admin/bgm");

                log.info("zookeeper初始化成功");
                log.info("zookeeper服务器状态:{}",client.isStarted());
            }
        } catch (Exception e) {
            log.error("zookeeper客户端连接,初始化错误");

            e.printStackTrace();
        }
    }


    /**
     * 增加或删除bgm,向zk-server创建子节点,供小程序后端监听
     * @param bgmId
     * @param operObj
     */
    public void sendBgmOperator(String bgmId,String operObj){
        try {
            client.create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.PERSISTENT)  //节点类型:持久节点
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)  //acl:匿名权限
                    .forPath("/admin/bgm/" + bgmId,operObj.getBytes());  //把节点路径和json对象传出去
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.letter;

import com.letter.enums.BGMOperatorTypeEnum;
import com.letter.service.BgmService;
import com.letter.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class ZKCuratorClient {

    //zk客户端
    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

    @Autowired
    private BgmService bgmService;

    public static final String ZOOKEEPER_SERVER = "xxxx:xxxx";

    public void init() {
        if (client != null) {
            return;
        }

        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        //创建zk客户端
        client = CuratorFrameworkFactory.builder().connectString(ZOOKEEPER_SERVER)
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin")
                .build();
        //启动客户端
        client.start();

        try {
            //测试一下
//            String testNodeData = new String(client.getData().forPath("/bgm/201126ABN2MDF3C0"));
//            log.info("测试的节点数据为: {}",testNodeData);
            addChildWatch("/bgm");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildWatch(String nodePath) throws Exception {

        final PathChildrenCache cache = new PathChildrenCache(client,nodePath,true);
        cache.start();;
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                    throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                    log.info("监听到事件CHILD_ADDED");

                    //1. 从数据库查询bgm对象，获取路径path /bgm/2011296FH039K5KP
                    String path = event.getData().getPath();
                    //operatorObjStr:192.168.43.3
                    String operatorObjStr = new String(event.getData().getData());
                    //把监听到的后台json对象转换成map对象
                    Map<String,String> map = JsonUtils.jsonToPojo(operatorObjStr, Map.class);
                    String operatorType = map.get("operType");
                    //监听到的path就是songPath
                    String songPath = map.get("path");

                    //不需要再通过id查询路径
//                    String[] arr = path.split("/");
//                    String bgmId = arr[arr.length-1];
//                    Bgm bgm = bgmService.queryBgmById(bgmId);
//                    if (bgm == null){
//                        return;
//                    }
//
//                    //1.1 bgm所在的相对路径
//                    String songPath = bgm.getPath();

                    //2. 定义保存到本地的bgm路径
                    String filePath = "E:\\letter_videos_dev"+songPath;


                    //3. 定义下载的路径（播放url）
                    String[] arrPath = songPath.split("\\\\");
                    String finalPath = "";
                    //3.1 处理url的斜杠以及编码
                    for(int i=0;i<arrPath.length;i++){
                        if(StringUtils.isNotBlank(arrPath[i])){
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i],"UTF-8");

                        }
                    }

                    //从后台管理系统下载音乐的路径
                    String bgmUrl = "http://192.168.43.3:80/mvc" + finalPath;

                    //判断是删除还是下载
                    if(operatorType.equals(BGMOperatorTypeEnum.ADD.type)){
                        //下载bgm到springboot服务器
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url,file);
                        //当删除和添加完成结束后,要把对应的zookeeper的结点删掉
                        client.delete().forPath(path);
                    } else if(operatorType.equals(BGMOperatorTypeEnum.DELETE.type)){
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }

                }
            }
        });


    }


}

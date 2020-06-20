package cn.edu.hunnu.acm.serviceimpl;

import cn.edu.hunnu.acm.framework.annotation.Service;
import cn.edu.hunnu.acm.model.RunRequest;
import cn.edu.hunnu.acm.service.UDPServerNotify;
import cn.edu.hunnu.acm.util.GetJedisSession;
import com.alibaba.fastjson.JSON;
import org.apache.ibatis.io.Resources;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;

// Redis + UDP message
// 如果要使用redis pub/sub模式的话
// 不仅仅可能存在消息丢失的情况
// 判题端还将引入多个依赖
// 所以此处简单起见使用UDP通信

@Service("udpServerNotify")
public class UDPServerNotifyImpl implements UDPServerNotify {
    private InetAddress host;
    private int port;
    private byte[] message;
    private String problemChannel;
    private String contestChannel;

    public UDPServerNotifyImpl() {
        Properties prop = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream("judged.properties");
            prop.load(inputStream);
            host = InetAddress.getByName(prop.getProperty("judge_host"));
            port = Integer.parseInt(prop.getProperty("judge_port"));
            message = ("1").getBytes();
            problemChannel = prop.getProperty("problemChannel");
            contestChannel = prop.getProperty("contestChannel");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void notifyServer(RunRequest runRequest) {
        Jedis jedis = null;
        DatagramSocket socket = null;
        DatagramPacket packet = null;

        String requestBody = JSON.toJSONString(runRequest);
        String channel = runRequest.contestId == -1 ? problemChannel : contestChannel;
        try {
            jedis = GetJedisSession.getJedisSession();
            jedis.rpush(channel, requestBody);
            socket = new DatagramSocket();
            packet = new DatagramPacket(message, message.length, host, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(socket != null) {
                socket.close();
            }
            if(jedis != null) {
                jedis.close();
            }
        }
    }
}

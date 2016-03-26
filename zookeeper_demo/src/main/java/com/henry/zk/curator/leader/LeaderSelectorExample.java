package com.henry.zk.curator.leader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

import com.google.common.collect.Lists;
/**
 * 
 * <p>zk�쵼��ѡ��ʾ��</p>
 * @author henry
 * 2016-1-12 ����02:45:21
 */
public class LeaderSelectorExample
{
    private static final int  CLIENT_NUM = 5;
    private static String connnectHost = "192.168.15.15:2181,192.168.15.15:2182,192.168.15.15:2183";
    private static final String PATH = "/examples/leader";

    public static void main(String[] args) throws Exception
    {

        System.out.println("--���� " + CLIENT_NUM + " ���ͻ���, �ͻ��˵��쵼Ȩѡ���ǹ�ƽ�ģ�ÿ�����л��ᣡ-----");

        List<CuratorFramework>  clients = Lists.newArrayList();
        List<ExampleClient>   examples = Lists.newArrayList();
        try
        {
            for ( int i = 0; i < CLIENT_NUM; ++i )
            {
                CuratorFramework client = CuratorFrameworkFactory.newClient(connnectHost, new ExponentialBackoffRetry(1000, 3));
                clients.add(client);

                ExampleClient example = new ExampleClient(client, PATH, "Client #" + i);
                examples.add(example);

                client.start();
                example.start();
            }

            System.out.println("---->>Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        finally
        {
            System.out.println("�رգ�");

            for ( ExampleClient exampleClient : examples )
            {
                CloseableUtils.closeQuietly(exampleClient);
            }
            for ( CuratorFramework client : clients )
            {
                CloseableUtils.closeQuietly(client);
            }
        }
    }
}
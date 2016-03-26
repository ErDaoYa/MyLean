package com.henry.zk.curator.locking;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>curator�ֲ�ʽ��ѧϰ</p>
 * Ҳ�����Լ�ͨ��zkʵ�ֲַ�ʽ����
 *          1���ͻ��˵���create()����������Ϊ��_locknode_/guid-lock-���Ľڵ㣬��Ҫע����ǣ�����ڵ�Ĵ���������Ҫ����ΪEPHEMERAL_SEQUENTIAL��
 *          2���ͻ��˵���getChildren(��_locknode_��)��������ȡ�����Ѿ��������ӽڵ㣬ע�⣬���ﲻע���κ�Watcher��
 *          3���ͻ��˻�ȡ�������ӽڵ�path֮����������Լ��ڲ���1�д����Ľڵ������С����ô����Ϊ����ͻ��˻��������
 *          4������ڲ���3�з����Լ����������ӽڵ�����С�ģ�˵���Լ���û�л�ȡ��������ʱ�ͻ�����Ҫ�ҵ����Լ�С���Ǹ��ڵ㣬
 *              Ȼ��������exist()������ͬʱע���¼�������
 *          5��֮���������ע�Ľڵ㱻�Ƴ��ˣ��ͻ��˻��յ���Ӧ��֪ͨ�����ʱ��ͻ�����Ҫ�ٴε���getChildren(��_locknode_��)��������ȡ�����Ѿ��������ӽڵ㣬
 *              ȷ���Լ�ȷʵ����С�Ľڵ��ˣ�Ȼ����벽��3
 * Created by henry.zhao on 2016/3/12.
 */
public class TestLock {
    private final InterProcessMutex lock;
    private static int count=1;
    private static final String PATH;

    static {
        PATH = "/examples/locks";
    }

    //���캯����ʼ��
    public TestLock(CuratorFramework client, String lockPath)
    {
        lock = new InterProcessMutex(client, lockPath);
    }
    public static void main(String[] args) throws Exception{
        final ExecutorService service = Executors.newFixedThreadPool(15);//�����߳����̶�5�����̳߳�
        final TestingServer  zkServer=new TestingServer();//ʹ��curator���Զ�ģ��zookeeper
        try {
            for(int i=0;i<10;i++) {
                /*
                 * Callable:
                 *      java�����̵߳�2�ַ�ʽ��һ����ֱ�Ӽ̳�Thread������һ�־���ʵ��Runnable�ӿڡ�
                 *      ��2�ַ�ʽ����һ��ȱ�ݾ��ǣ���ִ��������֮���޷���ȡִ�н����
                 *
                 *  Callable��Future��ͨ�����ǿ���������ִ�����֮��õ�����ִ�н��
                 *         1.Callable��һ�㶼���ύ��ExecuteService��ִ�С�
                 *         2.Callableʵ�ֵ��� V call()����
                 *
                 */
                Callable<Void> callable = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        CuratorFramework  client = CuratorFrameworkFactory.newClient(zkServer.getConnectString(),new ExponentialBackoffRetry(1000, 3));
                        try {
                            client.start();
                            TestLock testLock = new TestLock(client,PATH);
                            testLock.doWork();
                        }finally {
                            CloseableUtils.closeQuietly(client);
                        }
                        return null;
                    }
                };
                service.submit(callable);
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
            System.out.println("---���߳�ִ�������ִ�д˴��룡---");
        }catch (Exception e){
            e.printStackTrace();
        } finally
        {
            System.out.println("---���zkServer��Ϊ�գ��رգ�---");
            CloseableUtils.closeQuietly(zkServer);
        }
    }

    public void doWork()throws Exception{
        if ( !lock.acquire(3000, TimeUnit.SECONDS) )
        {
            throw new IllegalStateException(Thread.currentThread() + " could not acquire the lock");
        }
        try {
            System.out.println("--���ظ�id---"+count++);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.release();
        }
    }
}

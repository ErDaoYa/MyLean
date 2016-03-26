package com.henry.zk;

import java.io.IOException;
import java.util.Random;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
/**
 * <p> zk�ͻ���</p>
 * @author henry
 * 2016-1-12 ����10:34:30
 */
public class Master implements Watcher {
	private ZooKeeper zk;
	private String connnectHost;
	// ��ʶ�Լ�
	private String serverId = Integer.toHexString(new Random().nextInt());
	// �Ƿ���Leader
	private boolean isLeader;

	public Master(String connnectHost) {
		this.connnectHost = connnectHost;
	}
	
	/**
	 * zk �ͻ��˽��������ӣ���ʾ��Session establishment complete on server
	 * 		sessionid = 0x25233a7e96a0000, negotiated timeout = 15000
	 * 
	 * ����������رգ����᲻���������ӡ������Ҫ�ֶ��ر����ӣ�����ʹ��zk.close();
	 * @throws IOException
	 */
	public void startZK() throws IOException {
		zk = new ZooKeeper(connnectHost, 15000, this);
	}

	@Override
	public void process(WatchedEvent event) {
		System.out.println("---�۲�״̬----"+event);
	}
	/**
	 * 
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void runForMaster() throws KeeperException, InterruptedException {
	    while (true) {
	        try {
	            zk.create("/master", serverId.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	            // ����/master�ڵ�ɹ�
	            isLeader = true;
	            break;
	        } catch (KeeperException.NodeExistsException e) {
	            // /master�Ѿ�����
	            isLeader = false;
	            break;
	        } catch (KeeperException.ConnectionLossException e) {
	        }
	        if (checkMaster()) break;
	    }
	}
	/**
	* ���master�Ƿ����
	* @return ���ڷ���true����֮false
	*/
	private boolean checkMaster() throws InterruptedException{
	    while (true) {
	        try {
	            Stat stat = new Stat();
	            byte data[] = zk.getData("/master", false, stat);
	            isLeader = new String(data).equals(serverId);
	            return true;
	        } catch (KeeperException.NoNodeException e) {
	            // û��master�ڵ㣬�ɷ���false
	            return false;
	        } catch (KeeperException e) {
	            e.printStackTrace();
	        }
	    }
	}
	public static void main(String[] args) throws Exception {
		String connnectHost = "192.168.15.15:2181,192.168.15.15:2182,192.168.15.15:2183";
		Master m = new Master(connnectHost);
		m.startZK();
		m.runForMaster();
	    if (m.isLeader){
	        System.out.println("I am leader");
	    } else {
	        System.out.println("I am not leader");
	    }
		// wait for a bit
		Thread.sleep(60000);
	}
}

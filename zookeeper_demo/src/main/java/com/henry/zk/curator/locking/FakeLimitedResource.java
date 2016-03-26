package com.henry.zk.curator.locking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>ͬһʱ��ֻ����һ�����̴��� </p>
 * @author henry
 * 2016-1-12 ����05:37:43
 */
public class FakeLimitedResource {
	/*
	 * AtomicBoolean�÷���
	 * 	������compareAndSet(boolean expect, boolean update)
	 * 		 1. �Ƚ�AtomicBoolean��expect��ֵ�����һ�£�ִ�з����ڵ����
	 * 		 2. ��AtomicBoolean��ֵ���update
	 * ��������һ���ǳɵģ�������������֮�䲻�ᱻ��ϣ��κ��ڲ������ⲿ����䶼����������������֮�����С�Ϊ���̵߳Ŀ����ṩ�˽���ķ���
	 */
	private final AtomicBoolean inUse = new AtomicBoolean(false);
	
	@SuppressWarnings("finally")
	public String getId() throws InterruptedException {
		// in a real application this would be accessing/manipulating a shared resource
		if (!inUse.compareAndSet(false, true)) {
			/*
			 * ��һ�����̽�����inUseΪfalse����compareAndSet(false, true)��false��ͬ��
			 * ���ԣ�inUse=true������true����true ��������if�ķ����壻
			 * ִ��Thread.sleep();��ʱ���������̽�����inUse=ture���׳��쳣��
			 */
			throw new IllegalStateException("Needs to be used by one client at a time");
		}
		String id="";
		try {
			Thread.sleep((long) (3 * Math.random()));
			
			File file = new File(
					"F:/Workspace_new/zk_demo/src/main/resources/id.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			id = br.readLine().trim();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(Long.parseLong(id) + 1 + "");
			bw.flush();
			bw.close();
			br.close();
			
		}catch (IOException e) {
			return null;
		}
		finally {
			/*
			 * �������󣬰�inUse����Ϊfalse�����������̽�����
			 */
			inUse.set(false);
			return id;
		}
	}
}
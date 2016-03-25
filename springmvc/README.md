##spring mvc
### ��ͼ������

	`InternalResourceViewResolver`��ѷ��ص���ͼ���ƶ�����ΪInternalResourceView����InternalResourceView���Controller�������������ص�ģ�����Զ���ŵ���Ӧ��request�����У�Ȼ��ͨ��RequestDispatcher�ڷ������˰�����forword�ض���Ŀ��URL��������InternalResourceViewResolver�ж�����prefix=/WEB-INF/��suffix=.jsp��Ȼ�������Controller�������������ص���ͼ����Ϊtest����ô���ʱ��InternalResourceViewResolver�ͻ��test����Ϊһ��InternalResourceView�����Ȱѷ��ص�ģ�����Զ���ŵ���Ӧ��HttpServletRequest�����У�Ȼ������RequestDispatcher�ڷ������˰�����forword��/WEB-INF/test.jsp�������InternalResourceViewResolverһ���ǳ���Ҫ�����ԣ����Ƕ�֪�������/WEB-INF/����������ǲ���ֱ��ͨ��request����ķ�ʽ���󵽵ģ�Ϊ�˰�ȫ�Կ��ǣ�����ͨ�����jsp�ļ�����WEB-INFĿ¼�£���InternalResourceView�ڷ���������ת�ķ�ʽ���ԺܺõĽ���������
```
<bean id="internalResourceViewResolver"
		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<!-- ǰ׺ -->
		<property name="prefix" value="/WEB-INF/jsp/" />
		<!-- ��׺ -->
		<property name="suffix" value=".jsp" />
	</bean>
```
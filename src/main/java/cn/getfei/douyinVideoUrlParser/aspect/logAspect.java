package cn.getfei.douyinVideoUrlParser.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class logAspect {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Pointcut("execution(public * cn.getfei.douyinVideoUrlParser.dao.ITechnologyDao.findAll(..)))")
	public void parserMonitorAspect() {

	}
	
	@Around("parserMonitorAspect()")
	public Object doAroundJobController(ProceedingJoinPoint pjp) throws Throwable {
		long startTime = System.currentTimeMillis();
		logger.info("解析工作监控器。");
		Object o = pjp.proceed();
		long endTime = System.currentTimeMillis();
		logger.info("解析工作监控器关闭，运行耗时：" + (endTime - startTime) + "毫秒。");
		return o;
	}
}

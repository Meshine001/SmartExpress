package com.xjtu.meshine.mcloudsdk.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Created by Meshine on 16/12/2.
 */

//@Aspect
public class MyAspect {
   // @Around("execution(String com.xjtu.meshine.mcloudsdk.test.AspectJActivity.greet())")
    public String around(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        String result = (String) thisJoinPoint.proceed();
        return result.replace("world", "aspect");
    }
}

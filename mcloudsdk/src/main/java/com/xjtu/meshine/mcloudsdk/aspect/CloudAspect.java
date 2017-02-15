package com.xjtu.meshine.mcloudsdk.aspect;

import com.xjtu.meshine.mcloudsdk.NetOptions;
import com.xjtu.meshine.mcloudsdk.net.CloudController;
import com.xjtu.meshine.mcloudsdk.net.NetInfo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Vector;

/**
 * Created by Meshine on 16/12/2.
 * 获取处理标记为@Cloud的方法体切面
 */
@Aspect
public class CloudAspect {
    private static final String TAG = CloudAspect.class.getSimpleName();
    private static NetOptions netOptions = NetInfo.getOptions();

    //带有Cloud注解的方法
    @Pointcut("execution(@com.xjtu.meshine.mcloudsdk.annotation.Cloud * *(..))")
    public void method(){


    }

    /**
     * method的advice
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("method()")
    public Object cloudExecute(ProceedingJoinPoint pjp) throws Throwable{
        if (netOptions.isServer()){
            return pjp.proceed();
        }

        long startTime =  System.nanoTime();

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Class[] paramTypes = method.getParameterTypes();
        Object[] paramValues = pjp.getArgs();
        Object localState = pjp.getTarget();
        Class stateType = localState.getClass();
        Object result;
        Object remoteState = null;


        //卸载到云端
        CloudController cloudController = CloudController.getInstance();
        Vector results = cloudController.execute(method,paramValues,localState,stateType);
        if (results != null){
            result = results.get(0);
            remoteState = results.get(1);
            System.out.println("From cloud result:"+result);
            System.out.println("From cloud remoteState:"+remoteState);
        }else {
            //本地执行
            System.out.println("Cloud failed!\nRun local");
            result = pjp.proceed();
            System.out.println("Run local, result:"+result);
        }

        long endTime = System.nanoTime();
        System.out.println("执行时间："+(endTime-startTime)/1000000000.0+"s");

        return result;
    }

    /**
     * 进入方法前
     * @param pjp
     */
    void beforeMethod(ProceedingJoinPoint pjp){

    }

    /**
     * 执行完方法后
     * @param pjp
     */
    void afterMethod(ProceedingJoinPoint pjp){

    }
}

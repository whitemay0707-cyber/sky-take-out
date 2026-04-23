package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.constant.MethodConstant;
import nonapi.io.github.classgraph.utils.Join;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.beans.beancontext.BeanContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义的切面类，实现公共字段的自动填充逻辑
 */
@Aspect//这个是切面
@Component
@Slf4j//用于打印日志
public class AutoFillAspect { //切面是通知加上切入点
    //指定一个切入点，对哪些类的哪些方法进行拦截，切入点也是一个方法，加上@Pointcut，切入点就是这样知道这个注解的位置
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)") //拦截这个包下面所有的类的所有的方法且带有AutoFill注解
    public void autoFillPointCut(){}
    //拦截到了之后的具体操作交给通知来做，这里需要用前置通知，提前给公共字段赋值，前置通知也是一个方法
    @Before("autoFillPointCut()") //指定切入点，匹配上就执行通知
    public void autoFill(JoinPoint joinPoint)  { //前置通知需要一个参数，这个参数是连接点，通过连接点知道哪一个方法被拦截到了，以及这个方法具体的参数
        log.info("开始进行公共字段的填充....");
        //获取当前被拦截到的方法的数据库操作的类型,先拿signature,然后拿到注解对象，再从注解里面取出数据库操作类型

        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();


        //获取当前被拦截到的方法的参数-实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length==0){
            return;
        }
        Object entity = args[0];
        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //根据不同的数据库操作类型，通过反射对实体对象对应的属性进行赋值
        if(operationType==OperationType.INSERT){
            //利用反射获取方法对象
            try{
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                //进行赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }else if(operationType == OperationType.UPDATE){
            try{
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                //赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}

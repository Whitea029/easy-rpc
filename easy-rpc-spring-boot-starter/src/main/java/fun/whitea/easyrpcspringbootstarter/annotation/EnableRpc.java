package fun.whitea.easyrpcspringbootstarter.annotation;

import fun.whitea.easyrpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import fun.whitea.easyrpcspringbootstarter.bootstrap.RpcInitBootstrap;
import fun.whitea.easyrpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    boolean needServer() default true;
}
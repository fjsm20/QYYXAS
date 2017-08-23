package com.shine.yxqy.test;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {
    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring-web.xml");
        HelloWorld hw = (HelloWorld)ac.getBean("helloWorld");
        hw.sayHello();
    }
}

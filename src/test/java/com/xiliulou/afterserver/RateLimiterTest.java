package com.xiliulou.afterserver;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class RateLimiterTest {
   public static final RateLimiter LIMITER_50 = RateLimiter.create(50.0);
    /**
     * @link <a href="https://juejin.cn/post/7314559238721732648?searchId=20240314104446762B3F79FEF7A066FDBC"> RateLimiter使用指南</a>
     */
    @Test
    public void testRateLimiter() throws InterruptedException {
        // 创建一个每秒放入5个令牌的RateLimiter
        RateLimiter limiter = RateLimiter.create(5.0);
        //时间格式化
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        extracted(limiter, sdf);
    }
    
    private void extracted(RateLimiter limiter, SimpleDateFormat sdf) {
        for (int i = 0; i < 10; i++) {
            // 请求一个令牌
            limiter.acquire();
            System.out.println("处理请求: " + i + "，当前时间: " + sdf.format(System.currentTimeMillis()) );
            // sleep(1000);
        }
    }
    
    @Test
    public void testRateLimiter2() throws InterruptedException {
        //时间格式化
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        // SmoothBursty模式
        RateLimiter burstyLimiter = RateLimiter.create(50.0); // 每秒5个令牌
        // 这里模拟请求，观察令牌获取情况
        
        // SmoothWarmingUp模式
        RateLimiter warmingUpLimiter = RateLimiter.create(5.0, 1, TimeUnit.SECONDS); // 每秒5个令牌，预热时间1秒
        // 同样模拟请求，观察令牌获取情况
        extracted(warmingUpLimiter, sdf);
    }
    
    @Test
    public void testRateLimiter3() throws InterruptedException {
        // 创建一个每秒允许2个请求的RateLimiter
        RateLimiter limiter = RateLimiter.create(5.0);
        
        for (int i = 1; i <= 10; i++) {
            double waitTime = limiter.acquire(); // 请求令牌并获取等待时间
            System.out.println("处理请求 " + i + "，等待时间: " + waitTime + "秒");
        }
        System.out.println("======================= 预热 =======================");
        // 创建一个预热时间为10秒，每秒5个请求的RateLimiter
        RateLimiter warmingUpLimiter = RateLimiter.create(5.0, 10, TimeUnit.SECONDS);
        
        for (int i = 1; i <= 10; i++) {
            double waitTime = warmingUpLimiter.acquire(); // 请求令牌并获取等待时间
            System.out.println("处理请求 " + i + "，等待时间: " + waitTime + "秒");
        }
    }
    
    @Test
    public void testRateLimiter4() throws InterruptedException {
        int size = 100;
        
        getPrintlnQueue( size);
        System.out.println("======================= 查完了 =======================");
        for (int i = 1; i <= size; i++) {
            double acquire = LIMITER_50.acquire();// 请求令牌并获取等待时间
            getPrintln(i,acquire);
        }
    }
    private static void getPrintln(int i, double waitTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println("处理请求 " + i + "，等待时间: " + waitTime + "秒 当前"+sdf.format(System.currentTimeMillis()));
    }
    
    private static void getPrintlnQueue( double waitTime) {
        for (int i = 1; i <= waitTime; i++) {
            double acquire = LIMITER_50.acquire();// 请求令牌并获取等待时间
            getPrintln(i,acquire);
        }
    }
    
    @Test
    public void testRateLimiter5() throws InterruptedException {
        int size = 100;
        // 创建一个每秒允许2个请求的RateLimiter
        RateLimiter limiter = RateLimiter.create(5.0);
        //创建size为100的list
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            list.add(i);
        }
        list.parallelStream().forEach( i -> {
            double acquire = LIMITER_50.acquire();// 请求令牌并获取等待时间
            getPrintln(i,acquire);
        });
      
    }
    
}

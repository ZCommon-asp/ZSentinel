# ZSentinel
根据sentinel扩展的自定义限流框架
## 我们提供什么
 1. 基于sentinel gateway的自定义维度限流控制
 2. 限流事件通知
 3. 可扩展的自定义限流规则,动态加载限流规则
 4. 增加警告事件
 5. 更简单的限流维度匹配
 6. 开放Sentinel参数,如获取令牌剩余数

--- 
## 如何使用
  maven
   > mvn install:install-file
  ```
        <dependency>
            <groupId>com.aspire.csp</groupId>
            <artifactId>sentinel-common</artifactId>
            <version>1.0.0</version>
        </dependency>

        <dependency>
            <groupId>com.aspire.csp</groupId>
            <artifactId>sentinel-flow</artifactId>
            <version>1.0.0</version>
        </dependency>
  ```
  
  ## 功能使用
  

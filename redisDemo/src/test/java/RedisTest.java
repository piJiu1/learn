import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis 8 Jedis 客户端功能测试
 * 依赖: Docker 运行 Redis 8 (测试环境)
 */
@DisplayName("Redis 8 Jedis 客户端功能测试")
public class RedisTest {
    private static GenericContainer<?>  redisContainer;
    private static JedisPool jedisPool;

    @BeforeAll
    static void startRedisContainer(){
        // 启动 Redis 8 Docker 容器 (测试专用)
        redisContainer = new GenericContainer<>("redis")
                .withExposedPorts(6379)
                .waitingFor(Wait.forListeningPort())
                .withReuse(true);
        redisContainer.start();
        // 初始化 Jedis 连接池 (指向 Docker 容器)
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(10);
        poolConfig.setMaxTotal(10);
        poolConfig.setMinIdle(2);

        jedisPool = new JedisPool(poolConfig, redisContainer.getContainerIpAddress(),redisContainer.getMappedPort(6379));
    }
    @AfterAll
    static void stopRedisContainer(){
        if(redisContainer != null){
            redisContainer.stop();
        }
    }
    @Test
    @DisplayName("测试字符串操作: SET/GET/DEL")
    void testStringOperations() {
        try (Jedis jedis = jedisPool.getResource()) {
            //set
            jedis.set("user:1001","Alice");
            //get
            String name = jedis.get("user:1001");
            assert name.equals("Alice");
            assertEquals("Alice",name,"字符串存储失败");
            //del
            long delCount = jedis.del("user:1001");
            assertEquals(1,delCount,"删除操作失败");
            //验证已删除
            assertNull(jedis.get("user:1001"),"删除后数据未清空");
        }
    }

    @Test
    @DisplayName("测试 Hash 操作: HSET/HGET/HDEL/HGETALL")
    void testHashOperations() {
        try (Jedis jedis = jedisPool.getResource()) {
            //hset
            jedis.hset("user:1002","name","Bob");
            jedis.hset("user:1002","age","30");
            //hget
            String name = jedis.hget("user:1002", "name");
            assertEquals("Bob",name,"Hash 字段获取失败");
            //hgetall
            Map<String, String> user = jedis.hgetAll("user:1002");
            assertEquals("Bob",user.get("name"),"hgetall 获取 name 失败");
            assertEquals("30",user.get("age"),"hgetall 获取 age 失败");
            //hdel
            long delCount = jedis.hdel("user:1002", "age");
            assertEquals(1,delCount,"hdel 删除字段失败");
            assertNull(jedis.hget("user:1002","age"),"字段未删除");
        }
    }

    @Test
    @DisplayName("测试 List 操作: lpush/lrange/lpop/llen")
    void testListOperations() {
        try (Jedis jedis = jedisPool.getResource()) {
            // LPUSH (从左侧插入)
            jedis.lpush("todo:1003","Buy milk");
            jedis.lpush("todo:1003","Walk dog");
            // LRANGE (获取列表)
            List<String> todos = jedis.lrange("todo:1003", 0, -1);
            assertEquals(2,todos.size(),"列表长度错误");
            assertEquals("Walk dog",todos.get(0),"列表顺序错误");
            assertEquals("Buy milk",todos.get(1),"列表顺序错误");
            // LPOP (从左侧弹出)
            String firstTask = jedis.lpop("todo:1003");
            assertEquals("Walk dog",firstTask,"lpop 弹出错误");
            // LLEN (检查长度)
            long length = jedis.llen("todo:1003");
            assertEquals(1,length,"列表长度错误");
        }
    }

    @Test
    @DisplayName("测试 set 操作: sadd/amembers/sismember/srem")
    void testSetOperations() {
        try (Jedis jedis = jedisPool.getResource()) {
            // SADD (添加元素)
            jedis.sadd("skills:1004","Java","Python","SQL");
            // SMEMBERS (获取所有元素)
            Set<String> skills = jedis.smembers("skills:1004");
            assertEquals(3,skills.size(),"集合数量错误");
            assertEquals(skills.contains("Java"),"集合缺少 Java");
            // SISMEMBER (检查成员)
            boolean haspython = jedis.sismember("skills:1004", "Python");
            long removeCount = jedis.srem("skills:1004", "Java", "Python", "SQL");
            assertTrue(haspython,"集合缺少 Python");
            // SREM (移除元素)
            jedis.srem("skills:1004","SQL");
            assertEquals(1,removeCount,"srem 移除失败");
            assertFalse(jedis.sismember("skills:1004","SQL"),"SQL 未移除");
        }
    }

    @Test
    @DisplayName("测试原子操作: incr/decr")
    void testAtomicOperations() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("counter","0");
            // INCR (自增)
            long count1 = jedis.incr("counter");
            assertEquals(1, count1, "INCR 操作失败");

            // DECR (自减)
            long count2 = jedis.decr("counter");
            assertEquals(0, count2, "DECR 操作失败");
        }
    }
    /**
     * Redis存储初级的字符串
     * CRUD
     */
    /**
     * jedis操作Map
     */
    /**
     * jedis操作List
     */
    /**
     * jedis操作Set
     */
}

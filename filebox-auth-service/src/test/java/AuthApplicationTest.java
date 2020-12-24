import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.xufilebox.auth.AuthApplication;
import top.xufilebox.auth.util.JwtTokenUtil;
import top.xufilebox.common.redis.RedisTemplateProxy;

import java.util.Set;

@SpringBootTest(classes = AuthApplication.class)
class FileboxEurekaService8001ApplicationTests {
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    RedisTemplateProxy redisTemplateProxy;
    @Test
    void contextLoads() {
    }
    @Test
    void jwtTest() {
        Claims claimsFromToken = jwtTokenUtil.getClaimsFromToken("eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoidXNlciIsInVzZXJOYW1lIjoidGlhbnh1IiwiZXhwIjoxNjA4NzAzNzgxfQ.HjqqTc00bGiZInnNou9WRNzw6hsuqBupLwSGTqHy2_ihfcRD3B48OhuUHoWjvm7qvIPXTLVWQdQ2C-HWoz5xnA");
        Set<String> strings = claimsFromToken.keySet();
        System.out.println(claimsFromToken.getAudience());
        System.out.println(claimsFromToken.getExpiration());
        System.out.println(claimsFromToken.getId());
        System.out.println(claimsFromToken.getIssuedAt());
        System.out.println(claimsFromToken.getIssuer());
        System.out.println(claimsFromToken.getNotBefore());
        System.out.println(claimsFromToken.getSubject());
        System.out.println(claimsFromToken);
        for (String key : strings) {
            System.out.println(key + " : " + claimsFromToken.get(key));
        }
    }

    @Test
    void redisTest() throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
            redisTemplateProxy.setEX("tianxu", "xxx", 1000 * 60 * 2);
            System.out.println(redisTemplateProxy.isExpired("tianxu"));
            System.out.println(redisTemplateProxy.getExpired("tianxu"));
        }
    }



}

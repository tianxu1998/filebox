import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.xufilebox.gateway.GatewayServiceApplication;
import top.xufilebox.gateway.util.JwtTokenUtil;

/**
 * @description:
 * @author: alextian
 * @create: 2020-12-23 18:12
 **/
@SpringBootTest(classes = GatewayServiceApplication.class)
public class GatewayApplicationTest {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Test
    public void testJWT() {
        Claims claimsFromToken = jwtTokenUtil.getClaimsFromToken("eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjpudWxsLCJuaWNrTmFtZSI6IueUsOaXrSIsInVzZXJOYW1lIjoidGlhbnh1IiwiZXhwIjoxNjEzOTA4NzQwLCJ1c2VySWQiOjR9.BVQKYK3zfBeL-GtSLpPpWrLAo1mUUExQnOxw6fkoE1fiWD94pCgpuUdr83kgLB-Sy6hRVECD_Y9Lu9zND6y5Sg");
        System.out.println(claimsFromToken);
    }
}

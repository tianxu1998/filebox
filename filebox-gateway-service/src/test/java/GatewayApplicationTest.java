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
    }
}

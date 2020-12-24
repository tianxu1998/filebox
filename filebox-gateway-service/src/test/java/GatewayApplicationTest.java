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
        Claims claimsFromToken = jwtTokenUtil.
                getClaimsFromToken("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJtYWNybyIsInNjb3BlIjpbImFsbCJdLCJpZCI6MSwiZXhwIjoxNjA4MjA2NTA4LCJhdXRob3JpdGllcyI6WyJBRE1JTiJdLCJqdGkiOiJmY2FhOGI1OC0yMTQyLTQ3YmMtODAwYS1iN2RhMTJkYmU5MTAiLCJjbGllbnRfaWQiOiJjbGllbnQtYXBwIn0.OyLBiMn1y6YSOoHS2nUKWd-OYuOchHBOBTAps2s6rduS7I8BHFRdgeXZWc86TAh6yJhmqf29DfQrwlwegJHXeT3GxKBEX0snzGwuBMyF-O4_jIJPWQ-R8EoFBC4f2ZWUAzM0AawTxSOcZ64fyXPgbaqwwwKd-S81BYov0FTzQCo");
        System.out.println(claimsFromToken.getExpiration());
        System.out.println(claimsFromToken.get("userName"));
    }
}

package kamindo.propertymanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    @GetMapping("/userinfo")
    public String getUserInfo(Principal principal) {
        return principal.getName();
    }
}

package ma.ens.security.web;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Reads the identity returned by the provider through @AuthenticationPrincipal OAuth2User.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User user, Model model) {
        model.addAttribute("authenticated", user != null);
        if (user != null) {
            model.addAttribute("name", user.getAttribute("name"));
        }
        return "index";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal OAuth2User user) {
        model.addAttribute("name", user.getAttribute("name"));
        model.addAttribute("email", user.getAttribute("email"));
        model.addAttribute("picture", user.getAttribute("picture"));
        model.addAttribute("attributes", user.getAttributes());
        return "profile";
    }

    /**
     * Convenience endpoint: the raw attributes carried by the ID token, as JSON.
     */
    @GetMapping("/profile/attributes")
    @ResponseBody
    public Map<String, Object> attributes(@AuthenticationPrincipal OAuth2User user) {
        return user.getAttributes();
    }
}

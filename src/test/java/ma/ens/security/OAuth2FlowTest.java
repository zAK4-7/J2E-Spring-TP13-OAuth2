package ma.ens.security;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Verifies that the OAuth2 login is enforced and that the provider attributes
 * are exposed to the profile page.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OAuth2FlowTest {

    @Autowired
    private MockMvc mockMvc;

    private RequestPostProcessor oauthUser() {
        Map<String, Object> attributes = Map.of(
                "sub", "1234567890",
                "name", "Zakaria Ait Elhaj",
                "email", "zakaria@example.com",
                "picture", "https://example.com/avatar.png");

        OAuth2User user = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")), attributes, "sub");

        return oauth2Login().oauth2User(user);
    }

    @Test
    void homePageShouldBePublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void profileShouldRedirectAnonymousUsersToTheProvider() throws Exception {
        // With two registrations Spring generates a provider chooser page on /login
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void profileShouldDisplayTheProviderAttributes() throws Exception {
        mockMvc.perform(get("/profile").with(oauthUser()))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("name", "Zakaria Ait Elhaj"))
                .andExpect(model().attribute("email", "zakaria@example.com"))
                .andExpect(model().attribute("picture", "https://example.com/avatar.png"));
    }

    @Test
    void rawAttributesEndpointShouldReturnTheIdTokenClaims() throws Exception {
        mockMvc.perform(get("/profile/attributes").with(oauthUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sub").value("1234567890"))
                .andExpect(jsonPath("$.email").value("zakaria@example.com"));
    }

    @Test
    void logoutShouldRedirectToHome() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/logout").with(oauthUser()).with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}

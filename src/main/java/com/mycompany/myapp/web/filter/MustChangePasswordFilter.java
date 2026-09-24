package com.mycompany.myapp.web.filter;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Blocks every {@code /api/**} call from a user flagged {@code mustChangePassword}, except the
 * couple of endpoints needed to read the account (so the SPA can detect the flag and redirect) and
 * to actually change the password.
 *
 * <p>Without this filter, a user given a known initial password by an admin (see
 * {@code UserService#createUser}) could simply skip the forced password change: the client-side
 * redirect in {@code user-route-access.service.ts} only guards navigation inside the SPA, it is not
 * a security boundary, and a JWT issued at login stays fully valid regardless of this flag.
 */
public class MustChangePasswordFilter extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_PATHS = Set.of("/api/account", "/api/account/change-password");

    private final UserRepository userRepository;

    public MustChangePasswordFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        // Request URI includes the contextPath if any, removed it.
        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean blocked =
            path.startsWith("/api") &&
            !ALLOWED_PATHS.contains(path) &&
            SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).map(User::isMustChangePassword).orElse(false);
        if (blocked) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Password must be changed before continuing");
            return;
        }
        filterChain.doFilter(request, response);
    }
}

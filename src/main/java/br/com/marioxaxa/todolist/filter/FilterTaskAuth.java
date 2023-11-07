package br.com.marioxaxa.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.marioxaxa.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)

            throws ServletException, IOException {
                System.out.println("aqui");
                var servletpath = request.getServletPath();

                if(servletpath.startsWith("/tasks/")) {
                    System.out.println("if");
                    //Pegar usuario e senha

                    var authorization = request.getHeader("Authorization");

                    var authEncoded = authorization.substring("Basic".length()).trim();

                    byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

                    String authString = new String(authDecoded);
                    
                    String[] credetials = authString.split(":");

                    String username = credetials[0];
                    String password = credetials[1];

                    // VAlidar usuario
                    var user = this.userRepository.findByUsername(username);

                    if (user == null){
                        response.sendError(401, "Usário ou senha invalida");
                    } else {
                        // VAlidar senha
                        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                        if(passwordVerify.verified){
                            request.setAttribute("userId", user.getId());
                            filterChain.doFilter(request, response);
                        } else {
                            response.sendError(401, "Usário ou senha invalida");
                        }
                    }
                } else {
                    System.out.println("else");
                    filterChain.doFilter(request, response);
                }

                
            

            }
}

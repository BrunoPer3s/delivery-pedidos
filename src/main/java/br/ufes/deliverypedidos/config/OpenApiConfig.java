package br.ufes.deliverypedidos.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI/Swagger. Declara o esquema de segurança Bearer/JWT para
 * habilitar o botão "Authorize" na UI; a documentação dos endpoints é gerada
 * automaticamente pelo springdoc a partir dos controllers.
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Informe o token JWT obtido em POST /auth/login")
public class OpenApiConfig {

    @Bean
    public OpenAPI deliveryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery API REST")
                        .description("API de delivery de pedidos com autenticação JWT, "
                                + "máquina de estados (State), rastreamento (Observer) e pagamento (Strategy)")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

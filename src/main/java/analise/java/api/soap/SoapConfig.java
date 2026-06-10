package analise.java.api.soap;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurer;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class SoapConfig implements WsConfigurer {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/soap/*");
    }

    @Bean(name = "service")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema soapSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("SoapPort");
        wsdl11Definition.setLocationUri("/");
        wsdl11Definition.setTargetNamespace("music.soap.service");
        wsdl11Definition.setSchema(soapSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema soapSchema() {
        return new SimpleXsdSchema(new ClassPathResource("soap.xsd"));
    }
}

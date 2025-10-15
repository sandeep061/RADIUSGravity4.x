package ois.cc.gravity;

import CrsCde.CODE.Common.Utils.ReflUtils;
import CrsCde.CODE.Common.Utils.TypeUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import code.common.exceptions.CODEException;
import ois.cc.gravity.context.ServerContext;
import ois.cc.gravity.services.exceptions.GravityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.core.io.ClassPathResource;


@SpringBootApplication(exclude =
        {
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        })
public class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                String command = args[0].toLowerCase();

                switch (command) {
                    case "version":
                        showVersion(command);
                        break;

                    case "start":
                        runApplication(args);
                        break;

                    default:
                        System.out.println("No such ServerCommand found: " + args[0]);
                        break;
                }
            } else {
                // default behaviour → run the app
                runApplication(args);
            }
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }


    }

    private static void runApplication(String[] args) throws Exception, CODEException, GravityException {
        SpringApplication.run(Main.class, args);

        initAppProps();

        ServerContext.InitContext();
        ServerContext.This().Start();

        logger.info("Application Started...");
    }
    private static void initAppProps() throws Exception {
//        logger.info("init App Props...");
        AppProps.ConfigFile = AppConst.getConfigPath() + AppConst.ConfigFileName;
        System.out.println("Initializing application properties - " + AppProps.ConfigFile);

        Properties props = new Properties();
        props.load(new FileInputStream(AppProps.ConfigFile));
        List<Field> allFields = ReflUtils.GetDeclaredFields(AppProps.class);

        for (Field field : allFields) {
            field.setAccessible(true);
            if (props.getProperty(field.getName()) != null) {
                field.set(null, TypeUtil.ValueOf(field.getType(), props.getProperty(field.getName()).replaceAll("/+$", "")));
                logger.info(field.getName() + ": " + props.getProperty(field.getName()));
            }
        }

    }

    private static void showVersion(String args) {


        String ver = "";
        ClassPathResource resource = new ClassPathResource("version.txt");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1) {
                sb.append((char) i);
            }
            ver = sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();

        }
        System.out.println(ver);
    }


}

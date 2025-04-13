package com.test.ratelimit.configuration;

import com.test.redis.RedisUtil;
import com.test.util.JsonUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.test.ratelimit.configuration.ConfigurationUtil.makeConfigurations;

public class ConfigXMLParser {

    public static String XML_LOCATION= System.getProperty("user.home") + "/ratelimit/conf.xml";
    public static String RATE_LIMIT_TAG="rateLimitConfig";




    public static List<Configuration> getConfigurationFromXML() throws Exception{
        return parseConfigurations(XML_LOCATION);
    }
    protected static List<Configuration> parseConfigurations(String location) throws Exception {

        List<Configuration> configurations = new ArrayList<>();

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(new File(location));
        document.normalize();
        NodeList confList = document.getElementsByTagName(RATE_LIMIT_TAG);

        for (int i = 0; i<confList.getLength() ; i++){

            Node configNode = confList.item(i);
            if (configNode.getNodeType() == Node.ELEMENT_NODE){
                Element nodeElement = (Element) configNode;

               Configuration configuration = makeConfigurations(nodeElement);
               configurations.add(configuration);

            }
        }

        return configurations;

    }




}

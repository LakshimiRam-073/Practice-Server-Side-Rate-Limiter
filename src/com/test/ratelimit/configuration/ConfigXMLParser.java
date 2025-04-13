package com.test.ratelimit.configuration;

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

public class ConfigXMLParser {

    public static String XML_LOCATION= System.getProperty("user.home") + "/ratelimit/conf.xml";
    public static String RATE_LIMIT_TAG="rateLimitConfig";



    public static List<Configuration> getConfiguration() throws Exception{
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

    private static Configuration makeConfigurations(Element nodeElement) throws  Exception{
        //must configuration
        String uri = requireConfig(nodeElement, Configuration.RLConfig.URI);
        Integer limit = parseRequiredInt(nodeElement, Configuration.RLConfig.LIMIT);
        Integer window = parseRequiredInt(nodeElement, Configuration.RLConfig.WINDOW);

        Configuration conf = new Configuration(uri, limit, window);

        // Optional parameters
        parseOptionalInt(nodeElement, Configuration.RLConfig.BURST,Configuration.DEFAULT_BURST).ifPresent(conf::setBurst);
        parseOptionalInt(nodeElement, Configuration.RLConfig.TTL,Configuration.DEFAULT_TTL).ifPresent(conf::setTtl);
        parseOptionalInt(nodeElement, Configuration.RLConfig.PENALTY_TIME, -1 ).ifPresent(conf::setPenalty);

        // Unit parameters (optional – if you want to use them) only used if the above config time is given
        parseOptionalTimeUnit(nodeElement, Configuration.RLConfig.LIMIT_UNIT,Configuration.DEFAULT_TIME_UNIT).ifPresent(conf::setLimitUnit);
        parseOptionalTimeUnit(nodeElement, Configuration.RLConfig.WINDOW_UNIT,Configuration.DEFAULT_TIME_UNIT).ifPresent(conf::setWindowUnit);
        parseOptionalTimeUnit(nodeElement, Configuration.RLConfig.TTL_UNIT,Configuration.DEFAULT_TIME_UNIT).ifPresent(conf::setTtlUnit);
        parseOptionalTimeUnit(nodeElement, Configuration.RLConfig.PENALTY_TIME_UNIT,Configuration.DEFAULT_TIME_UNIT).ifPresent(conf::setPenaltyUnit);

        return conf;
    }

    private static String getTagValue(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent() : null;
    }


    private static String requireConfig(Element element, Configuration.RLConfig tag) {
        return Objects.requireNonNull(getTagValue(element, tag.getConfig()), tag.name() + " is required");
    }

    private static Integer parseRequiredInt(Element element, Configuration.RLConfig tag) {
        return Integer.parseInt(requireConfig(element, tag));
    }

    private static Optional<Integer> parseOptionalInt(Element element, Configuration.RLConfig tag,Integer defaultVal) {
        String value = getTagValue(element, tag.getConfig());
        return (value != null && !value.isEmpty()) ? Optional.of(Integer.parseInt(value)) : Optional.of(defaultVal);
    }


    private static Optional<TimeUnit> parseOptionalTimeUnit(Element element, Configuration.RLConfig tag,TimeUnit defaultVal){
        String value = getTagValue(element, tag.getConfig());

        if (value != null && !value.isEmpty()) {
            switch (value.toLowerCase()) {
                case "nanoseconds":
                    return Optional.of(TimeUnit.NANOSECONDS);
                case "microseconds":
                    return Optional.of(TimeUnit.MICROSECONDS);
                case "milliseconds":
                    return Optional.of(TimeUnit.MILLISECONDS);
                case "seconds":
                    return Optional.of(TimeUnit.SECONDS);
                case "minutes":
                    return Optional.of(TimeUnit.MINUTES);
                case "hours":
                    return Optional.of(TimeUnit.HOURS);
                case "days":
                    return Optional.of(TimeUnit.DAYS);
                default:
                    // Invalid unit, return empty or throw an exception
                    return Optional.of(defaultVal);
            }
        } else {
            return Optional.of(defaultVal);
        }

    }




}

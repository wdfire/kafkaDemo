import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class WordCountDemo {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        //创建流应用程序的唯一标识符
        props.put(StreamsConfig.APPLICATION_ID_CONFIG,"streams_pipes");
        //设置建立到kafka集群的初始连接的主机/端口对应列表
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"node01:9092,node02:9092,node03:9092");
        //记录键值对的默认序列化和反序列化库
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());
/*        Properties props = new Properties();
        props.put("application.id", "streams-wordcount");
        props.put("bootstrap.servers", "node01:9092,node02:9092,node03:9092");
        props.put("cache.max.bytes.buffering", Integer.valueOf(0));
        props.put("default.key.serde", Serdes.String().getClass().getName());
        props.put("default.value.serde", Serdes.String().getClass().getName());
        props.put("auto.offset.reset", "earliest");*/
        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> source = builder.stream(new String[]{"TextLinesTopic"});


        KTable<String, Long> counts = source.flatMapValues(new ValueMapper<String, Iterable<String>>() {
            public Iterable<String> apply(String value) {
                //
                String line = value;
                return Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" "));
            }
        }).groupBy(new KeyValueMapper<String, String, String>() {
            public String apply(String key, String value) {
                String keys = key;
                String values = value;
                return value;
            }
        }).count("Counts");
        counts.to(Serdes.String(), Serdes.Long(), "WordsWithCountsTopic");
        final KafkaStreams streams = new KafkaStreams(builder, props);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable var8) {
            System.exit(1);
        }

        System.exit(0);
    }
}
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Exit;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.ValueMapper;

/**
 * @author wangxinnian
 *
 */
/**
 * 用 high-level KStream DSL演示怎么完成 WordCount程序 
 * 从一个输入的 txt 文件中，计算一个简单单词出现的频率 
 *
 * 在本例中，输入流从一个名为“streams-file-input”的主题中读取， 
 * 其中消息的值表示文本行;并且，直方图输出被写入到主题“streams-wordcount-output”， 
 * 其中每个记录都是单个单词的更新计数。 
 *
 * 在运行本例子钱，你一定要创建一个输入主题和输出主题 (e.g. kafka-topics.sh --create ...), 
 * 并写一些数据到输入主题中，(e.g. kafka-console-producer.sh).  
 * 否则的话，你在输出主题中什么都看不到. 
 */

public class WordCountTest {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub  
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "node01:9092,node02:9092,node03:9092");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        // 设置偏移复位到最早，这样我们就可以用相同的预加载数据重新运行演示代码  
        // 注意，重新运行 demo, 你需要用 偏移量复位工具（offset reset tool）:  
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool  
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> source = builder.stream("TextLinesTopic");
        KTable<String, Long> counts=source.flatMapValues(new ValueMapper<String, Iterable<String>>() {
            public Iterable<String> apply(String value) {
                //这个是将按照value形成value的迭代器，value的类型是String
                return Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" "));
            }
        }).map(new KeyValueMapper<String, String, KeyValue<String, String>>() {
            public KeyValue<String, String> apply(String key, String value) {
                return new KeyValue<String,String>(value, value);//这个就是将后面的形成一个迭代
            }

        }).groupByKey().count("Counts");
        // need to override value serde to Long type
        counts.to(Serdes.String(), Serdes.Long(), "WordsWithCountsTopic");
        final KafkaStreams streams = new KafkaStreams(builder, props);
        final CountDownLatch latch = new CountDownLatch(1);
        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            Exit.exit(1);
        }
        Exit.exit(0);

    }

}  
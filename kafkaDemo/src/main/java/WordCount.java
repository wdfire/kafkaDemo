import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KeyValueMapper;

import java.util.Properties;

public class WordCount {
        public static void main(String[] args) {
        //创建属性文件
        Properties props = new Properties();
        //创建流应用程序的唯一标识符
        props.put(StreamsConfig.APPLICATION_ID_CONFIG,"streams_pipe");
        //设置建立到kafka集群的初始连接的主机/端口对应列表
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"node01:9092,node02:9092,node03:9092");
        //记录键值对的默认序列化和反序列化库
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());
        //创建拓扑构建器
        final Serializer<String> stringSerializer = new StringSerializer();
        final Deserializer<String> stringDeserializer = new StringDeserializer();
        final Serializer<Long> longSerializer  = new LongSerializer();
        final Deserializer<Long> longDeserializer = new LongDeserializer();
        final KStreamBuilder builder = new KStreamBuilder();
        //使用此拓扑构建起构建拓扑
        //此处建立了从主题为streams-WordCount-input中得到数据流
        KStream<String, String> stream = builder.stream("TextLinesTopic");
        stream.map(new KeyValueMapper<String, String, KeyValue<?, ?>>() {
                           @Override
                           public KeyValue<String,String> apply(String s, String s2) {
                                   System.out.println(s2.toString());
                                   Line line = new Line(s2);
                                   String s1 = line.getWords();
                                   return new KeyValue<String,String>(s, s1) ;
                           }
                   }).to("WordsWithCountsTopic");
        stream.map(new KeyValue<>())
        KafkaStreams streams = new KafkaStreams(builder,props);
        streams.start();

    }
}
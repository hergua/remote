package xin.opstime.remote;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import xin.opstime.remote.dto.ConsoleOperationResult;
import xin.opstime.remote.dto.Operation;
import xin.opstime.remote.dto.Result;
import xin.opstime.remote.dto.Status;
import xin.opstime.remote.utils.StringUtils;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class Remote {

    static Long agentId;

    static String kafka;

    static String ip;

    static String operationTopic;

    static String operationResultTopic;

    static String agentStatusTopic;

    static String kafkaUsername;

    static String kafkaPassword;

    private static KafkaProducer<String, String> producer;

    private static KafkaConsumer<String, String> consumer;

    static String groupId = "opstime-agent";


    public static void main(String[] args) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        getParamsFromCommand(args);
        initKafkaProducer();
        startRecMsg();
        startGatherStatus();

        latch.await();
    }


    public static ConsoleOperationResult consoleExec(String operate) {
        String batchFile = "/Users/hergua/IdeaProect/remote/" + operate + ".sh";
        ConsoleOperationResult res = new ConsoleOperationResult();
        StringBuffer msg = new StringBuffer();
        try {
            // 创建 ProcessBuilder 对象，指定要执行的命令
            ProcessBuilder processBuilder = new ProcessBuilder("sh", batchFile);

            // 将输出流和错误流合并
            processBuilder.redirectErrorStream(true);

            // 启动进程并执行命令
            Process process = processBuilder.start();

            // 读取命令执行结果
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                msg.append(line);
            }

            process.waitFor();
            res.setSuccess(process.exitValue() == 0);

        } catch (IOException | InterruptedException ignore) {
        }
        res.setMsg(msg.toString());
        return res;
    }


    public static void getParamsFromCommand(String[] args) {
        if (args.length < 16) {
            throw new RuntimeException("参数不齐全!");
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-agent")) agentId = Long.parseLong(args[i + 1]);
            if (args[i].equals("-ip")) ip = args[i + 1];
            if (args[i].equals("-operation")) operationTopic = args[i + 1];
            if (args[i].equals("-result")) operationResultTopic = args[i + 1];
            if (args[i].equals("-status")) agentStatusTopic = args[i + 1];
            if (args[i].equals("-kafka")) kafka = args[i + 1];
            if (args[i].equals("-username")) kafkaUsername = args[i + 1];
            if (args[i].equals("-passwd")) kafkaPassword = args[i + 1];
        }

        if (StringUtils.isAnyEmpty(ip, operationTopic, operationResultTopic, agentStatusTopic, kafka, kafkaUsername, kafkaPassword) || agentId == null)
            throw new RuntimeException("参数传入错误！");
    }

    public static void startRecMsg() {
        // 配置 Kafka 消费者
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + kafkaUsername + "\" " +
                "password=\"" + kafkaPassword + "\";");

        // 创建 Kafka 消费者
        consumer = new KafkaConsumer<>(props);

        // 订阅主题
        consumer.subscribe(Collections.singletonList(operationTopic));

        Timer sub = new Timer();
        sub.schedule(new TimerTask() {
            @Override
            public void run() {
                {
                    try {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                        // 处理接收到的消息
                        for (ConsumerRecord<String, String> record : records) {
                            Operation operation = JSONObject.parseObject(record.value(), Operation.class);
                            if (operation.getAgent().equals(agentId)) {
                                ConsoleOperationResult op = consoleExec(operation.getOperate());
                                send(operationResultTopic, JSONObject.toJSONString(new Result(op.getSuccess(), operation.getId(), "", op.getMsg())));
                            }
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
        }, 100L, 1000);

    }

    static void initKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + kafkaUsername + "\" " +
                "password=\"" + kafkaPassword + "\";");
        producer = new KafkaProducer<>(props);
    }

    static void send(String topic, String msg) {
        ProducerRecord<String, String> rec = new ProducerRecord<>(topic, msg);
        producer.send(rec);
    }

    static void startGatherStatus() {
        Timer statusTimer = new Timer();
        statusTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ConsoleOperationResult status = consoleExec("status");
                send(agentStatusTopic, JSONObject.toJSONString(new Status(status.getMsg(), agentId)));
            }
        }, 100, 2000);
    }


}

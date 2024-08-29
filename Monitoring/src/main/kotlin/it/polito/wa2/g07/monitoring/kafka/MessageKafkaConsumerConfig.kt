package it.polito.wa2.g07.monitoring.kafka

import it.polito.wa2.g07.monitoring.dtos.MessageMonitoringDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer

@EnableKafka
@Configuration
class MessageKafkaConsumerConfig {
    @Value("\${spring.kafka.consumer.bootstrap-servers}")
    lateinit var kafkaServer: String
    @Value("\${spring.kafka.consumer.group-id}")
    lateinit var groupId: String
    @Bean
    fun messageConsumerFactory(): ConsumerFactory<String, MessageMonitoringDTO> {
        val configProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaServer,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ErrorHandlingDeserializer::class.java,
            ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS to JsonDeserializer::class.java,
            JsonDeserializer.VALUE_DEFAULT_TYPE to MessageMonitoringDTO::class.java.name ,
            JsonDeserializer.TRUSTED_PACKAGES to "*",
            JsonDeserializer.USE_TYPE_INFO_HEADERS to "false"

        )

        val jsonDeserializer = JsonDeserializer(MessageMonitoringDTO::class.java)
        //jsonDeserializer.addTrustedPackages("*")
        return DefaultKafkaConsumerFactory(
            configProps,
            StringDeserializer(),
            jsonDeserializer
        )
    }

    @Bean
    fun kafkaMessageListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, MessageMonitoringDTO> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, MessageMonitoringDTO>()
        factory.consumerFactory = messageConsumerFactory()
        return factory
    }
}
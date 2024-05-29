package com.assess.kafka;

import com.assess.kafka.consumer.consumer.EmployeeEventsConsumerManualOffset;
import com.assess.kafka.producer.controller.EmployeeController;
import com.assess.kafka.producer.domain.EmployeeDto;
import com.assess.kafka.producer.domain.EmployeeEvent;
import com.assess.kafka.producer.domain.EmployeeEventType;
import com.assess.kafka.producer.producer.EmployeeEventProducer;
import com.assess.kafka.testutils.MasterData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static com.assess.kafka.testutils.TestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;


@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@EnableKafka
public class EmployeeApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private KafkaTemplate<String, EmployeeEvent> kafkaTemplate;


    @MockBean
    private EmployeeEventProducer employeeEventProducer;

    @MockBean
    private EmployeeEventsConsumerManualOffset employeeEventsConsumerManualOffset;

    @Test
    public void test_BookControllerSendBook() throws Exception {
        final int[] count = new int[1];
        EmployeeDto employeeDto = EmployeeDto.builder()
                .address("america")
                .experience(5)
                .salary(100000d)
                .phoneNumber(2235455464l)
                .username("john")
                .build();
        EmployeeEvent employeeEvent = EmployeeEvent.
                builder()
                .eventDetails("Create Employee")
                .eventType(EmployeeEventType.EMPLOYEE_ENROLL)
                .employeeDto(employeeDto)
                .build();

        when(employeeEventProducer.sendCreateEmployeeEvent(employeeDto, "Employee Created")).then(new Answer<EmployeeEvent>() {

            @Override
            public EmployeeEvent answer(InvocationOnMock invocation) throws Throwable {
                // TODO Auto-generated method stub
                count[0]++;
                return employeeEvent;
            }
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/employees/")
                .content(MasterData.asJsonString(employeeDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        yakshaAssert(currentTest(), count[0] == 1, businessTestFile);

    }

    @Test
    public void testSendBook() throws Exception {
        EmployeeDto employeeDto = EmployeeDto.builder()
                .address("america")
                .experience(5)
                .salary(100000d)
                .phoneNumber(2235455464l)
                .username("john")
                .build();
        EmployeeEvent employeeEvent = EmployeeEvent.
                builder()
                .eventDetails("Create Employee")
                .eventType(EmployeeEventType.EMPLOYEE_ENROLL)
                .employeeDto(employeeDto)
                .build();
        try {
            CompletableFuture<SendResult<String, EmployeeEvent>> mockFuture = mock(CompletableFuture.class);
            when(kafkaTemplate.send("create-employee", employeeEvent.getEventType().toString(), employeeEvent)).thenReturn(mockFuture);
            this.employeeEventProducer.sendCreateEmployeeEvent(employeeDto, "Employee Created");
            yakshaAssert(currentTest(), true, businessTestFile);
        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }

    }

    @Test
    @Disabled
    public void testConsumeBook() {
        EmployeeDto employeeDto = EmployeeDto.builder()
                .address("america")
                .experience(5)
                .salary(100000d)
                .phoneNumber(2235455464l)
                .username("john")
                .build();
        EmployeeEvent employeeEvent = EmployeeEvent.
                builder()
                .eventDetails("Create Employee")
                .eventType(EmployeeEventType.EMPLOYEE_ENROLL)
                .employeeDto(employeeDto)
                .build();

        kafkaTemplate.send("create-employee", employeeEvent.getEventType().toString(), employeeEvent);


        await().atMost(5, SECONDS).untilAsserted(() -> {
            ConsumerRecord<String, EmployeeEvent> mockRecord = mock(ConsumerRecord.class);

            Acknowledgment mockAcknowledgment = mock(Acknowledgment.class);
            employeeEventsConsumerManualOffset.onMessage(mockRecord, mockAcknowledgment);
            yakshaAssert(currentTest(), true, businessTestFile);

        });
    }


}

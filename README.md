### Employee Application

- Provides employee operation like enroll/resign using Apache kafka.

### Employee Application Producer Module

- ** Create Employee using api endpoint /api/employees/ and publish employee event message to kafka using topic create-employee.

### Employee Application Consumer Module

- ** EmployeeEventsConsumerManualOffset Listening employee event and store details in database.
- Incase of any failure happen in EmployeeEventsConsumerManualOffset then New message has been published to
  retry-create-employee topic and store failure record in database

### Employee Application Retry Module

- ** EmployeeEventsRetryConsumer is Responsible to listen failed consumed message.
- A scheduler RetryScheduler is running wit 5 sec internal to fetch all failure record from database and publish again
  on kafka

---

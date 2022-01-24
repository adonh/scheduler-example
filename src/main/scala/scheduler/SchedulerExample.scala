package scheduler

import com.pagerduty.eris.ClusterCtx
import com.pagerduty.metrics.NullMetrics
import com.pagerduty.scheduler.{CassandraTaskExecutorService, ManagedCassandraTaskRunner, Scheduler, SchedulerClient, SchedulerImpl, SchedulerSettings}
import com.pagerduty.scheduler.model.Task
import com.timgroup.statsd.NoOpStatsDClient
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.kafka.common.serialization.StringSerializer
import scala.concurrent.duration._
import java.time.Instant
import java.util.Properties


object SchedulerExample extends App {

  val config = ConfigFactory.load()
  val kafka = config.getString("scheduler.kafka.kafkaBootstrapServers")
  val schedulerSettings = SchedulerSettings(config).copy(kafkaBootstrapBroker=kafka)

  val clusterCtx = Cassandra.get

  val workersPerSchedulerPartition = 1

  val taskRunner = new ManagedCassandraTaskRunner[Unit] {
      def makeClusterCtx() = clusterCtx
      def makeManagedResource(clusterCtx: ClusterCtx): Unit = {}
      def runTask(task: Task, resource: Unit): Unit = {
        println(s"hello ${task}")
      }
    }

  val schedulerLogging = new Scheduler.LoggingImpl(schedulerSettings, NullMetrics, None)

  val scheduler = new SchedulerImpl(
      schedulerSettings,
      config,
      NullMetrics,
      clusterCtx.cluster,
      clusterCtx.cluster.getKeyspace("TestScheduler"),
      CassandraTaskExecutorService.factory(workersPerSchedulerPartition, taskRunner)
    )(
      schedulerLogging
    )

  scheduler.start()

  ///

  val kafkaProducer =
    new KafkaProducer[String, String](
      {
        val props = new Properties()
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka)
        props
      },
      new StringSerializer(),
      new StringSerializer()
    )


  val schedulerClient =
    new SchedulerClient(
      kafkaProducer,
      "test",
      10.minutes,
      NullMetrics
    )(
    )

  val task = Task("some-ordering-id", Instant.now(), "some-uniqueness-key", Map("taskName" -> "my-task"))

  schedulerClient.scheduleTask(task)
}



import MessageActions._
import AutomaticDerivation._
import ProducerMain.config
import cats.implicits._
import cats.effect.{ContextShift, ExitCode, IO, IOApp, Resource, Timer}
import com.itv.bucky.{AmqpClient, AmqpClientConfig, ExchangeName, Publisher, QueueName, RoutingKey}
import com.itv.bucky.circe._
import com.itv.bucky.consume.Ack
import com.itv.bucky.wiring._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext



object ConsumerMain extends IOApp {
  implicit val ec: ExecutionContext = ExecutionContext.global
  val config = AmqpClientConfig(host = "localhost", port = 5672, username = "shift", password = "shift")

  override def run(args: List[String]): IO[ExitCode] = {
    val consumerService: Resource[IO, Unit] = for {
      client <- AmqpClient[IO](config)
      _ <- startConsumer(client)
    } yield ()

    consumerService.use(_ => IO.never).as(ExitCode.Success)
  }
}

object ProducerMain extends IOApp {
  implicit val ec: ExecutionContext = ExecutionContext.global
  val config = AmqpClientConfig(host = "localhost", port = 5672, username = "shift", password = "shift")

  override def run(args: List[String]): IO[ExitCode] = {
    AmqpClient[IO](config).use(startPublisher).as(ExitCode.Success)

  }
}

object SerializedProducer extends IOApp {
  implicit val ec: ExecutionContext = ExecutionContext.global
  val config = AmqpClientConfig(host = "localhost", port = 5672, username = "shift", password ="shift")


  override def run(args: List[String]): IO[ExitCode] = {
    AmqpClient[IO](config).use(client => publisher(client).as(ExitCode.Success))


  }
}





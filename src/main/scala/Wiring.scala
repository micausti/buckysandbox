import cats.effect.{ContextShift, IO, Timer}
import cats.implicits._
import com.itv.bucky.{AmqpClient, AmqpClientConfig, ExchangeName, QueueName, RoutingKey}
import com.itv.bucky.consume.Ack
import com.itv.bucky.wiring.{Wiring, WiringName}

import scala.concurrent.ExecutionContext

case class TestParams(stringToSend: String)

object SendTestMessage extends Wiring[String](
  WiringName("test-wiring"),
  setExchangeName = Some(ExchangeName("test-exchange")),
  setRoutingKey = Some(RoutingKey("test-routing-key")),
  setQueueName = Some(QueueName("test-queue")),
)

object MessageActions {
  def startConsumer(client: AmqpClient[IO]) =
    for {
      _<- SendTestMessage.registerConsumer(client) { message =>
        IO.delay{
          println(s"Here is your messsage", message)
          Ack
        }
      }
    } yield()

  def startPublisher(client:AmqpClient[IO]) =
    for {
      sendTestMessage <- SendTestMessage.publisher(client)
      _ <- sendTestMessage("Hello")
    } yield()


}
import AutomaticDerivation.Person
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import com.itv.bucky.{AmqpClient, AmqpClientConfig, ExchangeName, Handler, PayloadMarshaller, PayloadUnmarshaller, QueueName, RoutingKey, decl}
import com.itv.bucky.consume.{Ack, ConsumeAction}
import com.itv.bucky.decl.{Exchange, Queue}
import com.itv.bucky
import io.circe.{Decoder, Encoder}

import scala.collection.immutable
import scala.concurrent.ExecutionContext
import scala.util.Success

import io.circe.generic.semiauto._
import com.itv.bucky.circe._

object Consumer extends IOApp {


  implicit val encoder: Encoder[Message] = deriveEncoder[Message]
  implicit val decoder: Decoder[Message] = deriveDecoder[Message]

  implicit val marshaller: PayloadMarshaller[Message] = marshallerFromEncodeJson[Message]
  implicit val unmarshaller: PayloadUnmarshaller[Message] = unmarshallerFromDecodeJson[Message]

  case class Message(foo: String)

  val config = AmqpClientConfig(host = "localhost", port = 5672, username = "shift", password = "shift")
  val declarations: immutable.Seq[decl.Declaration] = List(
    Queue(QueueName("test-queue")),
    Exchange(ExchangeName("test-exchange")).binding(RoutingKey("test-routing-key") -> QueueName("test-queue"))
  )

  class MyHandler extends Handler[IO, Message] {
    override def apply(m: Message): IO[ConsumeAction] =
      IO(Ack)
  }


  override def run(args: List[String]): IO[ExitCode] = {
    implicit val ec: ExecutionContext = ExecutionContext.global
    (for {
      client <- AmqpClient[IO](config)
      handler = new MyHandler
      _ <- Resource.pure(client.declare(declarations))
      _ <- client.registerConsumerOf(QueueName("test-queue"), handler)
    } yield ()).use(_=>IO.never)
  }
}
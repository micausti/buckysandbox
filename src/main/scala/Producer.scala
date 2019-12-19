import cats._
import cats.implicits._
import cats.effect._
import io.circe.generic.auto._
import scala.concurrent.{ExecutionContext}
import com.itv.bucky.decl.Exchange
import com.itv.bucky.decl.Queue
import com.itv.bucky._
import com.itv.bucky.circe._
import com.itv.bucky.consume._
import com.itv.bucky.publish._
import publish

object MyApp extends IOApp {
  case class Message(foo: String)

  val config = AmqpClientConfig(host = "localhost", port = 5672, username = "shift", password = "shift")
  val declarations = List(
    Queue(QueueName("test-queue")),
    Exchange(ExchangeName("test-exchange")).binding(RoutingKey("test-routing-key") -> QueueName("test-queue"))
  )

  override def run(args: List[String]): IO[ExitCode] = {
    implicit val ec: ExecutionContext = ExecutionContext.global
    (for {
      client <- AmqpClient[IO](config)
      _ <- Resource.pure(client.declare(declarations))
    } yield client).use {client =>
      val publisher = client.publisherOf[Message](ExchangeName("test-exchange"), RoutingKey("test-routing-key"))  // <- publisher being created
      publisher(Message("Hello")) //message is sent
    } *> IO(ExitCode.Success)
  }
}
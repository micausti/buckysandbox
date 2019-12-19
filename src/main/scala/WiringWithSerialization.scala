import com.itv.bucky.circe.auto._
import io.circe.generic.auto._
import cats.effect.IO
import com.itv.bucky._
import com.itv.bucky.circe._
import io.circe.generic.auto._

object AutomaticDerivation {

  case class Person(name: String, age: Int)

  def publisher(client: AmqpClient[IO]) =
    for {
      // Create a publisher
      publisher <- IO.pure(client.publisherOf[Person](ExchangeName("test-exchange"), RoutingKey("test-routing-key")))
      // Send a message
      _ <- publisher(Person("Alice", 22))
    } yield ()
}

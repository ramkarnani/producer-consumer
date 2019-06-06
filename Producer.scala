
class Producer extends Actor {

  override def receive: Receive = {
    case ProduceNextItem => {
      bufferActor = sender
      produceNextItem() match {
        case Some(item) => bufferActor ! item
        case None => // Nothing to send, since item could not be generated
      }
    }
  }
  def produceNextItem(): Item = {
      // may be long running process to produce the item
  }
}

class Consumer extends Actor {

    override def preStart(): Unit  = {
        bufferActor ! ProduceNextItem
    }


    override def receive: Receive = {
        case item: Item => {
            val bufferActor = sender()
            consume(item)
            bufferActor ! ProduceNextItem
        }
    }

    def consume(item: Item): Unit = {
        // may be long running consume process
    }
}
class BufferActor extends Actor {

    var localItemQueue: Queue[Item]
    var localConsumerQueue: Queue[ActorRef]
    var localProducerQueue: Queue[ActorRef]

    def preStart(): Unit = {
        producer ! ProduceNextItem
    }

    override def receive: Receive = {
        
        case item: Item => {
            
            // add this producer to the localList to generate the data
            val producer = sender()
            localProducerQueue.enqueue(producer)

            // If no waiting consumer, then add the item to local queue else send it to the longest waiting consumers

            if (localConsumerQueue.isEmpty) {
                val lItemQueue = localItemQueue.enqueue(item)
                localItemQueue = lItemQueue
            } 
            else {
                // sending the item to be consumed to the consumer
                val (consumer, lConsumerQueue) = localConsumerQueue.dequeue
                localConsumerQueue = lConsumerQueue
                consumer ! item
            }
        }

        case ProduceNextItem => {

            val consumer = sender()

            // If we have excess of items then send those items to the consumer else enroll the consumer in the waiting list

            if (localItemQueue.isEmpty) {
                val lConsumerQueue = localConsumerQueue.enqueue(consumer)
                localConsumerQueue = lConsumerQueue
            } else {
                val (item, lItemQueue) = localItemQueue.dequeue
                localItemQueue = lItemQueue
                consumer ! item
            }

            // sending the message to the producer to generate the items
            // If the list is empty, then it means that all the producers are busy so nothing can be done, 
            // but since you have already pushed the consumer into the waiting queue, then it would get the item in the order of it's request.
            if(localProducerQueue.nonEmpty){
                val (producer, lProducerQueue) = localProducerQueue.dequeue
                localProducerQueue = lProducerQueue
                producer ! ProduceNextItem
            }   
        }
    }
}
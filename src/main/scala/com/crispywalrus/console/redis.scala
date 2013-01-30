package com.crispywalrus.console

package redis {

  import java.nio.{ByteBuffer,CharBuffer}
  import java.nio.charset.{Charset,CharsetDecoder}
  import java.nio.charset.CoderResult.OVERFLOW
  import java.util.Properties
  import java.util.concurrent.Future
  import com.lambdaworks.redis._
  import com.lambdaworks.redis.codec._

  trait RedisStore {
    def get(key: String): Future[Array[Byte]]
    def put(key: String, value: Array[Byte]): Future[String]
  }
  
  class ByteArrayCodec extends RedisCodec[String,Array[Byte]] {
    val charset:Charset = Charset.forName("UTF-8")
    val decoder:CharsetDecoder  = charset.newDecoder()
    var chars:CharBuffer = CharBuffer.allocate(1024)

    def decodeKey(bytes:ByteBuffer):String = decode(bytes)

    def decodeValue(bytes:ByteBuffer):Array[Byte] = {
      bytes.mark()
      val retv:Array[Byte] = bytes.array()
      bytes.reset()
      retv
    }

    def encodeKey(key:String):Array[Byte] = encode(key)

    def encodeValue(value:Array[Byte]):Array[Byte] = value

    def decode(bytes:ByteBuffer):String = {
      chars.clear()
      bytes.mark()

      decoder.reset();
      Iterator.continually(decoder.decode(bytes, chars, true)).takeWhile( _ == OVERFLOW || decoder.flush(chars) == OVERFLOW).foreach({
        result =>
        chars = CharBuffer.allocate(chars.capacity * 2)
        bytes.reset
      })

      chars.flip().toString()
    }

    def encode(string:String):Array[Byte] = string.getBytes(charset)
  }

  class LettuceStore(javaProps:Properties) extends RedisStore {

    val readHosts = Stream.continually(javaProps.getProperty("redis.read.slave.hosts","localhost").split(",").map({ host => new RedisClient(host) }).map({ client => new KeyValue(client,client.connectAsync(new ByteArrayCodec)) }))
    val writeHosts = Stream.continually(javaProps.getProperty("redis.write.master.host","localhost").split(",").map({ host => new RedisClient(host) }).map({ client => new KeyValue(client,client.connectAsync(new ByteArrayCodec)) }))
    
    def get(key: String): Future[Array[Byte]] = readHosts.take(1).map( kva => kva(0).value.get(key)).head

    def put(key: String, value: Array[Byte]): Future[String] = { writeHosts.take(1).map( kva => kva(0).value.set(key,value)).head }
  }

}

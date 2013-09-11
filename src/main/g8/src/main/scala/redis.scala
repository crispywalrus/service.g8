package $organization$.$name$ {

  package redis {

    import java.nio.{ByteBuffer,CharBuffer}
    import java.nio.charset.{Charset,CharsetDecoder}
    import java.nio.charset.CoderResult.OVERFLOW
    import java.util.Properties
    import java.util.concurrent.Future
    import com.lambdaworks.redis._
    import com.lambdaworks.redis.codec._

    trait RedisStore[K,V] {
      def async: RedisAsyncConnection[K,V]
      def conn: RedisConnection[K,V]
    }

    class ByteArrayCodec(charset: String="UTF-8") extends RedisCodec[String,Array[Byte]] {
      val charset:Charset = Charset.forName(charset)

      def decodeKey(bytes:ByteBuffer):String = {
        new String(decodeValue(bytes),charset)
      }

      def decodeValue(bytes:ByteBuffer):Array[Byte] = {
        decode(bytes)
      }

      def encodeKey(key:String):Array[Byte] = {
        key.getBytes(charset)
      }

      def encodeValue(value:Array[Byte]):Array[Byte] = value

      def decode(bytes:ByteBuffer):String = {
        if( bytes.hasArray() )
          bytes.array()
        else {
          val retv:Array[Byte] = new Array[Byte](bytes.limit())
          bytes.get(retv)
          retv
        }
      }
    }

    /**
      * this expect hosts to be described as 'hostname[:port]' pairs
      * (with port optional) seperated by comma's
      */
    class LettuceStore(hostConfig: String,master: Option[String]) extends RedisStore[String,Array[Byte]] {
      val hosts = conf.split(",").map( x => x.split(":")).map( z =>
        if( z.length > 1) {
          new config(z(0),z(1).toInt)
        } else {
          new config(z(0))
        })

      val writer = master.map( m => m.split(":").map( h =>
        if( h.length > 1) {
          new config(h(0),h(1).toInt)
        } else {
          new config(h(0))
        }))
    }
  }
}

}

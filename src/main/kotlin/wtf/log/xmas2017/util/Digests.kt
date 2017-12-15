package wtf.log.xmas2017.util

object Digests {

    fun toHexString(hash: ByteArray): String = hash.joinToString(separator = "") { "%02X".format(it) }

}

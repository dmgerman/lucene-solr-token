begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|BlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**   * This is the main entry ID generator to generate unique ids for each entry.   * The Generator uses {@link java.security.SecureRandom} Numbers and the   * {@link java.lang.System#currentTimeMillis()} to create a semi-unique sting;   * The string will be digested by a {@link java.security.MessageDigest} which   * returns a byte array. The generator encodes the byte array as a hex string.   *<p>   * The generated Id's will cached in a   * {@link java.util.concurrent.BlockingQueue} and reproduced if an id has been   * removed.   *</p>   *    * @author Simon Willnauer   *    */
end_comment

begin_class
DECL|class|IDGenerator
specifier|public
class|class
name|IDGenerator
block|{
DECL|field|stopped
specifier|final
name|AtomicBoolean
name|stopped
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|secureRandom
specifier|private
specifier|final
name|SecureRandom
name|secureRandom
decl_stmt|;
DECL|field|mdigest
specifier|private
specifier|final
name|MessageDigest
name|mdigest
decl_stmt|;
DECL|field|blockingQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|String
argument_list|>
name|blockingQueue
decl_stmt|;
DECL|field|runner
specifier|private
name|Thread
name|runner
decl_stmt|;
DECL|field|DEFAULT_CAPACITY
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_CAPACITY
init|=
literal|10
decl_stmt|;
DECL|field|LOGGER
specifier|protected
specifier|static
specifier|final
name|Log
name|LOGGER
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|IDGenerator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|RUNNER_THREAD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|RUNNER_THREAD_NAME
init|=
literal|"GDATA-ID Generator"
decl_stmt|;
comment|/**       * Constructs a new ID generator. with a fixed capacity of prebuild ids. The       * default capacity is 10. Every given parameter less than 10 will be       * ignored.       *        * @param capacity -       *            capacity of the prebuild id queue       * @throws NoSuchAlgorithmException -       *             if the algorithm does not exist       */
DECL|method|IDGenerator
specifier|public
name|IDGenerator
parameter_list|(
name|int
name|capacity
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
block|{
name|this
operator|.
name|secureRandom
operator|=
name|SecureRandom
operator|.
name|getInstance
argument_list|(
literal|"SHA1PRNG"
argument_list|)
expr_stmt|;
name|this
operator|.
name|mdigest
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"SHA-1"
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockingQueue
operator|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|String
argument_list|>
argument_list|(
operator|(
name|capacity
operator|<
name|DEFAULT_CAPACITY
condition|?
name|DEFAULT_CAPACITY
else|:
name|capacity
operator|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|startIDProducer
argument_list|()
expr_stmt|;
block|}
comment|/**       * This method takes a gnerated id from the IDProducer queue and retruns it.       * If no ID is available this method will wait until an ID is produced. This       * implementation is thread-safe.       *        * @return a UID       * @throws InterruptedException -       *             if interrupted while waiting       */
DECL|method|getUID
specifier|public
name|String
name|getUID
parameter_list|()
throws|throws
name|InterruptedException
block|{
return|return
name|this
operator|.
name|blockingQueue
operator|.
name|take
argument_list|()
return|;
block|}
DECL|method|startIDProducer
specifier|private
name|void
name|startIDProducer
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|runner
operator|==
literal|null
condition|)
block|{
name|UIDProducer
name|producer
init|=
operator|new
name|UIDProducer
argument_list|(
name|this
operator|.
name|blockingQueue
argument_list|,
name|this
operator|.
name|secureRandom
argument_list|,
name|this
operator|.
name|mdigest
argument_list|)
decl_stmt|;
name|this
operator|.
name|runner
operator|=
operator|new
name|Thread
argument_list|(
name|producer
argument_list|)
expr_stmt|;
name|this
operator|.
name|runner
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|runner
operator|.
name|setName
argument_list|(
name|RUNNER_THREAD_NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|runner
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**       * @return the current size of the queue       */
DECL|method|getQueueSize
specifier|public
name|int
name|getQueueSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|blockingQueue
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**       * Stops the id-producer       */
DECL|method|stopIDGenerator
specifier|public
name|void
name|stopIDGenerator
parameter_list|()
block|{
name|this
operator|.
name|stopped
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|runner
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
DECL|class|UIDProducer
specifier|private
class|class
name|UIDProducer
implements|implements
name|Runnable
block|{
DECL|field|random
name|SecureRandom
name|random
decl_stmt|;
DECL|field|queue
name|BlockingQueue
argument_list|<
name|String
argument_list|>
name|queue
decl_stmt|;
DECL|field|digest
name|MessageDigest
name|digest
decl_stmt|;
DECL|method|UIDProducer
name|UIDProducer
parameter_list|(
name|BlockingQueue
argument_list|<
name|String
argument_list|>
name|queue
parameter_list|,
name|SecureRandom
name|random
parameter_list|,
name|MessageDigest
name|digest
parameter_list|)
block|{
name|this
operator|.
name|queue
operator|=
name|queue
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|digest
expr_stmt|;
block|}
comment|/**           * @see java.lang.Runnable#run()           */
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|IDGenerator
operator|.
name|this
operator|.
name|stopped
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|this
operator|.
name|queue
operator|.
name|put
argument_list|(
name|produce
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"UIDProducer has been interrupted -- runner is going down"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
DECL|method|produce
specifier|private
name|String
name|produce
parameter_list|()
block|{
name|String
name|randomNumber
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|this
operator|.
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|byteResult
init|=
name|this
operator|.
name|digest
operator|.
name|digest
argument_list|(
name|randomNumber
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|hexEncode
argument_list|(
name|byteResult
argument_list|)
return|;
block|}
block|}
comment|/**       * Encodes a given byte array into a hex string.       *        * @param input -       *            the byte array to encode       * @return hex string representation of the given byte array       */
DECL|method|hexEncode
specifier|static
name|String
name|hexEncode
parameter_list|(
name|byte
index|[]
name|input
parameter_list|)
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|char
index|[]
name|digits
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|}
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|input
operator|.
name|length
condition|;
operator|++
name|idx
control|)
block|{
name|byte
name|b
init|=
name|input
index|[
name|idx
index|]
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|digits
index|[
operator|(
name|b
operator|&
literal|0xf0
operator|)
operator|>>
literal|4
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|digits
index|[
name|b
operator|&
literal|0x0f
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

